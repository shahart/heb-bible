
import { No2gim } from "./No2gim.js";
import { reportUsage } from "./Analytics.js";
import { createAppUrl, createReferenceUrl } from "./AppConfig.js";

class Gematria {

    output = "";
    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    isGim(i, gim, repo, partGim) {
        let line = this.repo.getVerses()[i];
        if (this.repo.getGims()[i] == gim) {
            let newLine;
            if (this.repo.getNikkudVerses()[i].length >= 1) { 
                newLine = this.repo.getNikkudVerses()[i];
            }
            else {
                newLine = this.repo.noName(line);
            }
            const referenceUrl = createReferenceUrl(this.repo.getBookNumArr()[i] + 1, this.repo.getPPrk()[i]);
            this.output += newLine + " -- <a href=\"" + referenceUrl + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
        }
        else if (partGim) {
            let words = line.split(" ");
            let gims = words.map(function(word){
                return repo.gim(word);
            });
            for (let x = 1; x < words.length; ++x) {
                let sum = gims[x-1];
                for (let y = x+1; y <= words.length; ++y) {
                    sum += gims[y-1];
                    if (sum == gim) {
                        for (let z = 1; z <= words.length; ++z) {
                            if (z >=x && z <= y) {
                                this.output += "<span style=\"color:blue;\">"
                            }
                            this.output += this.repo.noName(words[z-1]) + " ";
                            if (z >=x && z <= y) {
                                this.output += "</span>"
                            }
                        }
                        this.output += " -- " + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "-" + this.repo.getPPsk()[i] + "<br/><br/>"
                        break;
                    }
                }
            }
        }
    }

    gematria(repo) {
        let partGim = document.getElementById("partGim").checked;
        if (partGim) console.info("partGim");
        const input = document.getElementById("gim").value.trim();
        let gim = this.repo.gim(input);
        document.getElementById("resultGim").innerHTML = "ערך: " + gim;
        if (gim < 159 || gim > 13639) {
            alert('השתמש באותיות בעברית, שערכן 13,639..159');
            return;
        }
        this.output = new No2gim().no2gim(gim) + "<br/><br/>"; // "";
        let found = 0;
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            this.isGim(i, gim, this.repo, partGim);
        }
        const shareUrl = createAppUrl({ g: input });
        document.getElementById("resultGim").innerHTML += "<br/><br/>" + this.output + 
            "<span class=\"share\">&gt;</span><br><br><p dir=\"ltr\" class=\"share-url\">" + shareUrl + "</p>";
        reportUsage("Gim", input);
    }

}

export { Gematria }
