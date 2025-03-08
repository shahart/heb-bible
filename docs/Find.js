
import { No2gim } from "./No2gim.js";

class Find {

    output = "";
    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    lucene(t) {
      this.repo.lucene(t);
    }

    find(repo) {
        // todo? in chosen book
        this.output = ""; 
        let findStr = document.getElementById("find").value;
        let lucene = document.getElementById("lucene").checked;
        if (lucene && findStr.length >= 2) {
          this.lucene(findStr);
          // alert("LunrJs -> check console.log");
        }
        else {
          document.getElementById("resultFind").innerHTML = "";
        }
        if (!(/^[\u05D0-\u05EA]+$/).test(findStr.replace(/\s+/g, ''))) { 
          alert("הטקסט לחיפוש חייב להכיל רק אותיות בעברית ורווחים");
      }
        let found = false;
        let findings = 0;
        if (findStr.length >= 2) {
          for (let i = 0; i < this.repo.getVerses().length; ++i) {
            let line = this.repo.getVerses()[i];
            if (line.includes(findStr)) {
                found = true;
                let idx = line.indexOf(findStr);
                let noNameFind = this.repo.noName(findStr);
                ++ findings;
                if (noNameFind !== findStr) {
                  this.output += line.substring(0, idx);
                  this.output += "<span style=\"color:blue;\">";
                  this.output += line.substring(idx, idx + findStr.length);
                  this.output += "</span>";
                  this.output += line.substring(idx + findStr.length);
                  this.output += " -- " + "<a href=\"https://shahart.github.io/heb-bible/index.html?r=" + (this.repo.getBookNumArr()[i]+1) + "," + this.repo.getPPrk()[i] + "\"" + " target=\"_new\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
                }
                else {
                  this.output += this.repo.noName(line.substring(0, idx));
                  this.output += "<span style=\"color:blue;\">";
                  this.output += this.repo.noName(line.substring(idx, idx + findStr.length));
                  this.output += "</span>";
                  this.output += this.repo.noName(line.substring(idx + findStr.length));
                  this.output += " -- " + "<a href=\"https://shahart.github.io/heb-bible/index.html?r=" + (this.repo.getBookNumArr()[i]+1) + "," + this.repo.getPPrk()[i] + "\"" + " target=\"_new\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
                }
            }
          }
        }
        if (!found) this.output += "לא נמצא";
        else this.output += "<span class=\"share\">&gt;</span></br></br><p dir=\"ltr\" align=\"right\">https://shahart.github.io/heb-bible?q=" + findStr + "</p>" + findings + " ממצאים ";
        document.getElementById("resultFind").innerHTML += "<br/>" + this.output;
        //
        var xhrAws = new XMLHttpRequest();
        xhrAws.open('POST', 'https://z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws/');
        xhrAws.setRequestHeader("Content-Type", "application/json");
        xhrAws.send(JSON.stringify({ "name": findStr, "extra": "found-" + found, "type": "Find" }));
        xhrAws.onreadystatechange = function(e) {
          if ( xhrAws.readyState === 4) {
            console.debug(xhrAws.status + this.responseText);
          }
        }
        xhrAws.send();
    }

}

export { Find }
