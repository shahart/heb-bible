
class Pasuk {

    output = "";
    repo;
    EndFile = 0;

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
        if ((line.charAt(1) === args.charAt(0) && line.charAt(line.length-1) === args.charAt(args.length-1)) || (containsName && line.indexOf(args) >= 0)) {
            this.output += line + " -- " + this.repo.getCurrBook()[i] + " " + this.repo.getPPrk()[i] + "-" + this.repo.getPPsk()[i] + "<br/><br/>";
            return true;
        }
        return false;
    }

    pasuk(repo) { // same todo as in Pasuk.html
        let containsName = document.getElementById("containsName").checked;
        document.getElementById("result").innerHTML = "";
        let args = document.getElementById("text").value;
        if (args.length <= 1 || args.charAt(0) > 'ת' || args.charAt(0) < 'א' || args.charAt(args.length-1) > 'ת' || args.charAt(args.length-1) < 'א') {
            document.getElementById("result").innerHTML = "Invalid name";
        }
        else {
            if (! containsName) {
                var dictionary = {};
                dictionary["אא"] = 43;
                dictionary["אב"] = 34;
                dictionary["אג"] = 2;
                dictionary["אד"] = 33;
                // todo see SvcTest.java output
                var shortName = args.charAt(0) + args.charAt(args.length-1);
                if (dictionary[shortName]) {
                    document.getElementById("result").innerHTML = "Total Psukim: " + dictionary[shortName];
                }
            }
            this.saveInput("input", args);
            this.output = "";
            let found = 0;
            let foundInclDups = 0;
            for (let i = 1; i < this.repo.getVerses().length; ++i) {
                if (this.isValid(i, containsName, args)) {
                    ++ found;
                }
            }
            this.saveInput('resCount', found);
            document.getElementById("result").innerHTML = "Total Psukim: " + found + "<br/><br/>" + this.output;

            //
            var xhrAws = new XMLHttpRequest();
            xhrAws.open('POST', 'https://z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws/');
            xhrAws.setRequestHeader("Content-Type", "application/json");
            xhrAws.send(JSON.stringify({ "name": args, "containsName": containsName }));
            xhrAws.onreadystatechange = function(e) {
              if ( xhrAws.readyState === 4 &&
                    xhrAws.status === 200) {
                console.log(this.responseText);
                if (this.responseText != "Total Psukim: " + foundInclDups) {
                    // alert("Total Psukim diff was found, please contact shahar_t AT hotmail. Java " + this.responseText + " -- " + "JS Total Psukim: " + foundInclDups + " -- " + args);
                }
              }
            }
            xhrAws.send();
        }
    }

}

export { Pasuk }