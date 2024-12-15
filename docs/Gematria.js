
import { No2gim } from "./No2gim.js";

class Gematria {

    output = "";
    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    isGim(i, gim) {
        let line = this.repo.getVerses()[i];
        if (this.repo.getGims()[i] == gim) {
            this.output += this.repo.noName(line) + " -- " + this.repo.getCurrBook()[i] + " " + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "-" + this.repo.getPPsk()[i] + "<br/><br/>";
        }
    }

    gematria(repo) {
        let gim = this.repo.gim(document.getElementById("gim").value);
        document.getElementById("resultGim").innerHTML = "ערך: " + gim;
        if (gim < 159 || gim > 13639 ) alert('השתמש באותיות בעברית, שערכן 13,639..159');
        this.output = new No2gim().no2gim(gim) + "<br/><br/>"; // "";
        let found = 0;
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            this.isGim(i, gim);
        }
        document.getElementById("resultGim").innerHTML += "<br/><br/>" + this.output;
    }

}

export { Gematria }
