import { No2gim } from "./No2gim.js";

class Read {

    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    read() {
        let bookNum = document.getElementById('BookSelect').value;
        this.output = "";
        let letters = 0;
        let psukim = 0;
        let tevot = 0;
        let totLetters = 0;
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            if (this.repo.getBookNumArr()[i] == bookNum-1 ) {
                ++ psukim;
                if (this.repo.getPPsk()[i] == 1 || (this.repo.getBookNumArr()[i] == 27-1 && this.repo.getPPrk()[i] == 119 && this.repo.getPPsk()[i] % 8 == 1)) this.output += "</br>";
                this.output += this.no2gim.no2gim(this.repo.getPPrk()[i]) + "-" + this.repo.getPPsk()[i] + " -- " + this.repo.getVerses()[i] + "<br/>";
                totLetters += this.repo.getVerses()[i].replace(/\s+/g, '').length;
                tevot += this.repo.getVerses()[i].split(' ').length; // - 1;
            }
        }
        document.getElementById("bibleResult").innerHTML = "פסוקים: " + psukim + "</br>" + "אותיות: " + totLetters + "</br>" + "תיבות: " + tevot + "</br>" + this.output;
    }

}

export { Read }
