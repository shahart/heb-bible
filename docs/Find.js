
import { No2gim } from "./No2gim.js";

class Find {

    output = "";
    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    find(repo) {
        // todo? in chosen book
        this.output = ""; 
        let findStr = document.getElementById("find").value;
        if (!(/^[\u05D0-\u05EA]+$/).test(findStr.replace(/\s+/g, ''))) { 
          alert("הטקסט לחיפוש חייב להכיל רק אותיות בעברית ורווחים");
      }
        let found = false;
        if (findStr.length >= 2) {
          for (let i = 0; i < this.repo.getVerses().length; ++i) {
            let line = this.repo.getVerses()[i];
            if (line.includes(findStr)) {
                found = true;
                let idx = line.indexOf(findStr);
                this.output += line.substring(0, idx);
                this.output += "<span style=\"color:blue;\">";
                this.output += line.substring(idx, idx + findStr.length);
                this.output += "</span>";
                this.output += line.substring(idx + findStr.length);
                this.output += " -- " + "<a href=\"https://shahart.github.io/heb-bible/index.html?r=" + (this.repo.getBookNumArr()[i]+1) + "," + this.repo.getPPrk()[i] + "\"" + " target=\"_new\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
            }
          }
        }
        if (!found) this.output += "לא נמצא";
        document.getElementById("resultFind").innerHTML = "<br/>" + this.output;
    }

}

export { Find }
