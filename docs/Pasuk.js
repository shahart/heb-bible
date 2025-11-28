import { No2gim } from "./No2gim.js";

class Pasuk {

    output = "";
    repo;
    EndFile = 0;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
        // todo one time Init dictionary
    }

    saveInput(cname, cvalue) {
        if (typeof (Storage) !== "undefined") {
            // ~5M max
            localStorage.setItem(cname, cvalue);
        } else {
            // 4K
            const d = new Date();
            let expireInDays = 7;
            d.setTime(d.getTime() + (expireInDays * 24 * 60 * 60 * 1000));
            let expires = "expires=" + d.toUTCString();
            var myCookieValue = cvalue;
            document.cookie = cname + "=" + myCookieValue + ";" + expires + ";path=/";
        }
    }

    loadInput(cname) {
        if (typeof (Storage) !== "undefined") {
            let res = localStorage.getItem(cname);
            return res || "";
        } else {
            let name = cname + "=";
            let decodedCookie = document.cookie;
            let ca = decodedCookie.split(';');
            for (let c of ca) {
                while (c.charAt(0) === ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) === 0) {
                    return c.substring(name.length, c.length).split('\\').join('\n');
                }
            }
            return "";
        }
    }

    isValid(i, containsName, args) {
        let line = this.repo.getVerses()[i];
        if ((line.charAt(0) === args.charAt(0) && line.charAt(line.length-1) === args.charAt(args.length-1)) || (containsName && line.indexOf(args) >= 0)) {
            let newLine;
            if (this.repo.getNikkudVerses()[i].length >= 1) { 
                newLine = this.repo.getNikkudVerses()[i];
            }
            else {
                newLine = this.repo.noName(line);
            }
            if (this.output.indexOf(newLine) == -1) {
                this.output += newLine + " -- " + "<a href=\"https://shahart.github.io/heb-bible/index.html?r=" + (this.repo.getBookNumArr()[i]+1) + "," + this.repo.getPPrk()[i] + "\"" + " target=\"_new\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
                return true;
            }
        }
        return false;
    }

    /**
     * Pure function for searching psukim, returns found count for testability.
     */
    searchPasuk(args, containsName) {
        if (!(/^[\u05D0-\u05EA]+$/).test(args)) {
            throw new Error("הטקסט לחיפוש חייב להכיל רק אותיות בעברית ורווחים");
        }
        if (args.length <= 1 || args.charAt(0) > 'ת' || args.charAt(0) < 'א' || args.charAt(args.length-1) > 'ת' || args.charAt(args.length-1) < 'א') {
            throw new Error( "ערך לא חוקי" );
        }
        this.output = "";
        let found = 0;
        if (! containsName) {
            var dictionary = {};
            dictionary["אא"] = 43;
            dictionary["אב"] = 34;
            dictionary["אג"] = 2;
            dictionary["אד"] = 33;
            // todo see SvcTest.java output
            var shortName = args.charAt(0) + args.charAt(args.length-1);
            if (dictionary[shortName]) {
                this.output = "Total Psukim: " + dictionary[shortName];
                return dictionary[shortName];
            }
        }
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            if (this.isValid(i, containsName, args)) {
                ++found;
            }
        }
        return found;
    }

    pasuk(repo) { // same todo as in Pasuk.html
        let containsName = document.getElementById("containsName").checked;
        document.getElementById("result").innerHTML = "";
        let args = document.getElementById("text").value.trim();
        let found;
        try {
            found = this.searchPasuk(args, containsName);
        } catch (e) {
            alert(e.message);
            return;
        }
        if (this.output === "ערך לא חוקי") {
            document.getElementById("result").innerHTML = this.output;
        } else {
            this.saveInput("input", args);
            this.saveInput('resCount', found);
            document.getElementById("result").innerHTML = found + " פסוקים <br/><br/>" + this.output +
                "<span class=\"share\">&gt;</span></br></br><p dir=\"ltr\" align=\"right\">https://shahart.github.io/heb-bible?p=" + args + "</p>";
            //
            var xhrAws = new XMLHttpRequest();
            xhrAws.open('POST', 'https://z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws/');
            xhrAws.setRequestHeader("Content-Type", "application/json");
            xhrAws.send(JSON.stringify({ "name": args, "extra": "containsName-" + containsName, "type": "Pasuk" }));
            xhrAws.onreadystatechange = function(e) {
              if ( xhrAws.readyState === 4) {
                console.debug(xhrAws.status + this.responseText);
                // if (this.responseText != "Total Psukim: " + foundInclDups) {
                    // alert("Total Psukim diff was found, please contact shahar_t AT hotmail. Java " + this.responseText + " -- " + "JS Total Psukim: " + foundInclDups + " -- " + args);
                // }
              }
            }
            xhrAws.send();
        }
    }

}

export { Pasuk }
