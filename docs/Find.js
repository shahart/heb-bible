
import { No2gim } from "./No2gim.js";
import { reportUsage } from "./Analytics.js";
import { createAppUrl, createReferenceUrl } from "./AppConfig.js";

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

    reportFind(findStr, found) {
      return reportUsage("Find", findStr, "found-" + found);
    }

    find(repo) {
        // todo? in chosen book
        this.output = ""; 
        let findStr = document.getElementById("find").value.replace("-", " ");
        let lucene = document.getElementById("lucene").checked;
        if (lucene && findStr.length >= 2) {
          this.lucene(findStr);
          // alert("LunrJs -> check console.log");
        }
        else {
          document.getElementById("resultFind").innerHTML = "";
        }
        if (!lucene && !(/^[\u05D0-\u05EA]+$/).test(findStr.replace(/\s+/g, ''))) { 
          alert("הטקסט לחיפוש חייב להכיל רק אותיות בעברית ורווחים");
          return;
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
                  const referenceUrl = createReferenceUrl(this.repo.getBookNumArr()[i] + 1, this.repo.getPPrk()[i]);
                  this.output += " -- <a href=\"" + referenceUrl + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
                }
                else {
                  this.output += this.repo.noName(line.substring(0, idx));
                  this.output += "<span style=\"color:blue;\">";
                  this.output += this.repo.noName(line.substring(idx, idx + findStr.length));
                  this.output += "</span>";
                  this.output += this.repo.noName(line.substring(idx + findStr.length));
                  const referenceUrl = createReferenceUrl(this.repo.getBookNumArr()[i] + 1, this.repo.getPPrk()[i]);
                  this.output += " -- <a href=\"" + referenceUrl + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
                }
            }
          }
        }
        if (!found) this.output += "חיפוש רגיל - לא נמצא";
        else { 
          const shareUrl = createAppUrl({ q: findStr });
          this.output += " חיפוש רגיל - ממצאים " + findings + "<br/><br/><span class=\"share\">&gt;</span><br><br><p dir=\"ltr\" class=\"share-url\">" + shareUrl + "</p>";
        }
        document.getElementById("resultFind").innerHTML += "<br/>" + this.output;
        //
        if ('requestIdleCallback' in window) {
          requestIdleCallback(() => {
            this.reportFind(findStr, found);
            }, { timeout: 5000 }
          );
        } 
        else {
          setTimeout(() => {
            this.reportFind(findStr, found);}, 0);
        }
    } 

}

export { Find }
