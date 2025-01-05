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
        let bookRashiNum = parseInt(bookNum);
        if (bookRashiNum >= 15) bookRashiNum += 1;
        if (bookRashiNum >= 28) bookRashiNum += 1;
        if (bookRashiNum >= 30) bookRashiNum += 1;
        if (bookRashiNum == 32) bookRashiNum = 31; // todo better fix
        let bookHeb = document.getElementById('BookSelect').options[bookRashiNum-1].text.replace(' ', '_');
        if (bookHeb === 'תהלים') bookHeb = 'תהילים';
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
                    let rashiUrl = "<a href=\"" + "https://wiki.jewishbooks.org.il/mediawiki/wiki/%D7%A8%D7%A9%22%D7%99/" + bookHeb + "/" + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "#" + this.no2gim.no2gim(this.repo.getPPsk()[i]) + "\"" + " target=\"_blank\">" + this.repo.getPPsk()[i] + "</a>";
                    this.output += "-" + rashiUrl + " -- " + this.repo.getVerses()[i] + "<br/>";
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
            let pointer = bookRashiNum;
            if (pointer < 10) pointer = '0' + pointer;
            if (pointer == '08') pointer += 'a';
            if (pointer == '09') pointer = '08b';
            if (pointer == '10') pointer = '09a';
            if (pointer == '11') pointer = '09b';
            if (pointer >= 12) pointer -= 2;
            if (pointer >= 14) pointer -= 1;
            if (pointer == 29) pointer = 27; // אמת
            // todo fine-tune the כתובים ..
            let engTrans = "<a href=\"https://mechon-mamre.org/p/pt/pt" + pointer + "01.htm\" target=\"_blank\">" + bookeng[bookNum-1] + "</a>";
            this.output = engTrans + "</br></br>" + "פסוקים: " + psukim + "</br>" + "אותיות: " + totLetters + "</br>" + "תיבות: " + tevot + "</br></br>" + headers + "</br>" + this.output;
        }
        document.getElementById("bibleResult").innerHTML = this.output;
    }

}

export { Read }
