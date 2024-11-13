
class Read {

    repo;

    constructor(repo) {
        this.repo = repo;
    }

    read() {
        let bookNum = document.getElementById('BookSelect').value;
        this.output = "";
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            if (this.repo.getBookNumArr()[i] == bookNum-1 ) {
                this.output += this.repo.getVerses()[i] + " -- " + this.repo.getPPrk()[i] + "-" + this.repo.getPPsk()[i] + "<br/>";
            }
        }
        document.getElementById("bibleResult").innerHTML = this.output;
    }

}

export { Read }
