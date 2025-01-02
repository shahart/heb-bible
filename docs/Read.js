import { No2gim } from "./No2gim.js";

class Read {

    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    read() {
        read(undefined);
    }

    read(bookPrk) {
        let bookNum = document.getElementById('BookSelect').value;
        if (bookPrk) bookPrk=Number(bookPrk.split(",")[1]);
        var bookeng = ['Genesis','Exodus','Leviticus','Numbers','Deuteronomy','Joshua','Judges','Samuel 1',
            'Samuel 2','Kings 1','Kings 2','Isaiah','Jeremiah','Ezekiel','Hosea','Joel','Amos','Obadiah','Jonah','Micha','Nachum',
            'Habakkuk','Zephaniah','Haggai','Zechariah','Malachi','Psalms','Proverbs','Job','Song of songs','Ruth','Lamentations',
            'Ecclesiastes','Esther','Daniel','Ezra','Nehemiah','Cronicles 1','Cronicles 2']; // 39 books
        this.output = "";
        let letters = 0;
        let psukim = 0;
        let tevot = 0;
        let totLetters = 0;
        let headers = "";
        if (bookPrk) {
            this.output = "<span style=\"color:blue;\"> פרק " + this.no2gim.no2gim(bookPrk) + "  </span></br>";
        }
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            if (this.repo.getBookNumArr()[i] == bookNum-1 ) {
                if (!!!bookPrk || Number(this.repo.getPPrk()[i]) == bookPrk) {
                    if (bookPrk)
                        this.output += "<span style=\"color:blue;\">";
                    ++ psukim;
                    if (this.repo.getPPsk()[i] == 1 || (this.repo.getBookNumArr()[i] == 27-1 && this.repo.getPPrk()[i] == 119 && this.repo.getPPsk()[i] % 8 == 1)) this.output += "</br>";
                    if (this.repo.getPPsk()[i] == 1)
                        this.output += "<a id=\"prk" + this.repo.getPPrk()[i] + "\"/>" + "<a href=\"#book\">";
                    this.output += this.no2gim.no2gim(this.repo.getPPrk()[i]);
                    if (this.repo.getPPsk()[i] == 1)
                        this.output += "</a>";
                    this.output += "-" + this.repo.getPPsk()[i] + " -- " + this.repo.getVerses()[i] + "<br/>";
                    totLetters += this.repo.getVerses()[i].replace(/\s+/g, '').length;
                    tevot += this.repo.getVerses()[i].split(' ').length; // - 1;
                    if (bookPrk)
                        this.output += "</span>";
                    if (this.repo.getPPsk()[i] == 1) {
                        headers += "<a id=\"book\"/><a href=\"#prk" + this.repo.getPPrk()[i] + "\">" + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a> ";
                    }
                }
            }
        }
        if (!!!bookPrk) {
            this.output = bookeng[bookNum-1] + "</br></br>" + "פסוקים: " + psukim + "</br>" + "אותיות: " + totLetters + "</br>" + "תיבות: " + tevot + "</br></br>" + headers + "</br>" + this.output;
        }
        document.getElementById("bibleResult").innerHTML = this.output;
    }

}

export { Read }
