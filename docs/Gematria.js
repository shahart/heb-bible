
import { No2gim } from "./No2gim.js";

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
            this.output += this.repo.noName(line) + " -- " + "<a href=\"https://shahart.github.io/heb-bible/index.html?r=" + (this.repo.getBookNumArr()[i]+1) + "," + this.repo.getPPrk()[i] + "\"" + " target=\"_new\">" + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a>-" + this.repo.getPPsk()[i] + "<br/><br/>";
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
        let gim = this.repo.gim(document.getElementById("gim").value);
        document.getElementById("resultGim").innerHTML = "ערך: " + gim;
        if (gim < 159 || gim > 13639 ) alert('השתמש באותיות בעברית, שערכן 13,639..159');
        this.output = new No2gim().no2gim(gim) + "<br/><br/>"; // "";
        let found = 0;
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            this.isGim(i, gim, this.repo, partGim);
        }
        document.getElementById("resultGim").innerHTML += "<br/><br/>" + this.output + 
            "<span class=\"share\">&gt;</span></br></br><p dir=\"ltr\" align=\"right\">https://shahart.github.io/heb-bible?g=" + document.getElementById("gim").value + "</p>";
        //
        var xhrAws = new XMLHttpRequest();
        xhrAws.open('POST', 'https://z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws/');
        xhrAws.setRequestHeader("Content-Type", "application/json");
        xhrAws.send(JSON.stringify({ "name": document.getElementById("gim").value, "extra": "", "type": "Gim" }));
        xhrAws.onreadystatechange = function(e) {
          if ( xhrAws.readyState === 4) {
            console.debug(xhrAws.status + this.responseText);
          }
        }
        xhrAws.send();
    }

}

export { Gematria }
