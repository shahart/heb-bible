
class Gematria {

    output = "";
    repo;

    constructor(repo) {
        this.repo = repo;
    }

    isGim(i, gim) {
        let line = this.repo.getVerses()[i];
        if (this.repo.getGims()[i] == gim) {
            this.output += this.repo.noName(line) + " -- " + this.repo.getCurrBook()[i] + " " + this.repo.getPPrk()[i] + "-" + this.repo.getPPsk()[i] + "<br/><br/>";
        }
    }

    gematria(repo) {
        document.getElementById("resultGim").innerHTML = "";
        let gim = this.repo.gim(document.getElementById("gim").value);
        if (gim == 0) alert('השתמש באותיות בעברית, שערכן 159..13,639');
        this.output = "";
        let found = 0;
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            this.isGim(i, gim);
        }
        document.getElementById("resultGim").innerHTML = "<br/><br/>" + this.output;
    }

}

export { Gematria }
