import { No2gim } from "./No2gim.js";
import { createAppUrl } from "./AppConfig.js";

const TORAH_PARASHOT = [
    { book: 1, name: "בראשית", chapter: 1, verse: 1 }, { book: 1, name: "נח", chapter: 6, verse: 9 },
    { book: 1, name: "לך לך", chapter: 12, verse: 1 }, { book: 1, name: "וירא", chapter: 18, verse: 1 },
    { book: 1, name: "חיי שרה", chapter: 23, verse: 1 }, { book: 1, name: "תולדות", chapter: 25, verse: 19 },
    { book: 1, name: "ויצא", chapter: 28, verse: 10 }, { book: 1, name: "וישלח", chapter: 32, verse: 4 },
    { book: 1, name: "וישב", chapter: 37, verse: 1 }, { book: 1, name: "מקץ", chapter: 41, verse: 1 },
    { book: 1, name: "ויגש", chapter: 44, verse: 18 }, { book: 1, name: "ויחי", chapter: 47, verse: 28 },
    { book: 2, name: "שמות", chapter: 1, verse: 1 }, { book: 2, name: "וארא", chapter: 6, verse: 2 },
    { book: 2, name: "בא", chapter: 10, verse: 1 }, { book: 2, name: "בשלח", chapter: 13, verse: 17 },
    { book: 2, name: "יתרו", chapter: 18, verse: 1 }, { book: 2, name: "משפטים", chapter: 21, verse: 1 },
    { book: 2, name: "תרומה", chapter: 25, verse: 1 }, { book: 2, name: "תצוה", chapter: 27, verse: 20 },
    { book: 2, name: "כי תשא", chapter: 30, verse: 11 }, { book: 2, name: "ויקהל", chapter: 35, verse: 1 },
    { book: 2, name: "פקודי", chapter: 38, verse: 21 },
    { book: 3, name: "ויקרא", chapter: 1, verse: 1 }, { book: 3, name: "צו", chapter: 6, verse: 1 },
    { book: 3, name: "שמיני", chapter: 9, verse: 1 }, { book: 3, name: "תזריע", chapter: 12, verse: 1 },
    { book: 3, name: "מצורע", chapter: 14, verse: 1 }, { book: 3, name: "אחרי מות", chapter: 16, verse: 1 },
    { book: 3, name: "קדשים", chapter: 19, verse: 1 }, { book: 3, name: "אמר", chapter: 21, verse: 1 },
    { book: 3, name: "בהר", chapter: 25, verse: 1 }, { book: 3, name: "בחקתי", chapter: 26, verse: 3 },
    { book: 4, name: "במדבר", chapter: 1, verse: 1 }, { book: 4, name: "נשא", chapter: 4, verse: 21 },
    { book: 4, name: "בהעלתך", chapter: 8, verse: 1 }, { book: 4, name: "שלח", chapter: 13, verse: 1 },
    { book: 4, name: "קרח", chapter: 16, verse: 1 }, { book: 4, name: "חקת", chapter: 19, verse: 1 },
    { book: 4, name: "בלק", chapter: 22, verse: 2 }, { book: 4, name: "פינחס", chapter: 25, verse: 10 },
    { book: 4, name: "מטות", chapter: 30, verse: 2 }, { book: 4, name: "מסעי", chapter: 33, verse: 1 },
    { book: 5, name: "דברים", chapter: 1, verse: 1 }, { book: 5, name: "ואתחנן", chapter: 3, verse: 23 },
    { book: 5, name: "עקב", chapter: 7, verse: 12 }, { book: 5, name: "ראה", chapter: 11, verse: 26 },
    { book: 5, name: "שופטים", chapter: 16, verse: 18 }, { book: 5, name: "כי תצא", chapter: 21, verse: 10 },
    { book: 5, name: "כי תבוא", chapter: 26, verse: 1 }, { book: 5, name: "נצבים", chapter: 29, verse: 9 },
    { book: 5, name: "וילך", chapter: 31, verse: 1 }, { book: 5, name: "האזינו", chapter: 31, verse: 30 },
    { book: 5, name: "וזאת הברכה", chapter: 33, verse: 1 }
];

class Read {

    repo;
    no2gim = new No2gim();

    constructor(repo) {
        this.repo = repo;
    }

    readRandomPsalm() {
        const chapter = Math.floor(Math.random() * 150) + 1;
        return this.read("27," + chapter);
    }

    read() {
        read(undefined);
    }

    read(bookPrk,siddurCall) {
        let bookNum;
        if (bookPrk && bookPrk.indexOf(",") == 0) {
            bookNum = document.getElementById('BookSelect').value;
        }
        else if (bookPrk) {
            bookNum = bookPrk.split(",")[0];
        }
        else {
            bookNum = document.getElementById('BookSelect').value;
        }
        var bookHebArr = ['תישארב','תומש','ארקיו','רבדמב','םירבד','עשוהי','םיטפוש','א לאומש','ב לאומש', 'א םיכלמ','ב םיכלמ','היעשי','הימרי','לאקזחי','עשוה','לאוי','סומע','הידבוע','הנוי','הכימ','םוחנ','קוקבח','הינפצ', 'יגח','הירכז','יכאלמ','םיליהת','ילשמ','בויא','םירישה ריש','תור','הכיא','תלהק','רתסא','לאינד','ארזע','הימחנ', 'א םימיה ירבד','ב םימיה ירבד']; // 39 books
        let bookHeb = bookHebArr[bookNum-1].split('').reverse().join('')
        // console.debug(bookNum + " --> " + bookHeb);
        let bookPsk = undefined;
        let amount = undefined;
        if (bookPrk && bookPrk.split(",").length == 3) {
            if (bookPrk.split(",")[2].indexOf("-") > 0) {
                bookPsk = Number(bookPrk.split(",")[2].split("-")[0]);
                amount = Number(bookPrk.split(",")[2].split("-")[1]);
            }
            else {
                bookPsk = Number(bookPrk.split(",")[2]);
            }
        }
        if (bookPrk) bookPrk=Number(bookPrk.split(",")[1]);
        var bookeng = ['Genesis','Exodus','Leviticus','Numbers','Deuteronomy','Joshua','Judges','Samuel 1',
            'Samuel 2','Kings 1','Kings 2','Isaiah','Jeremiah','Ezekiel','Hosea','Joel','Amos','Obadiah','Jonah','Micha','Nachum',
            'Habakkuk','Zephaniah','Haggai','Zechariah','Malachi','Psalms','Proverbs','Job','Song of songs','Ruth','Lamentations',
            'Ecclesiastes','Esther','Daniel','Ezra','Nehemiah','Cronicles 1','Cronicles 2']; // 39 books
        this.output = "";
        let clearText = "";
        let letters = 0;
        let psukim = 0;
        let tevot = 0;
        let prkTevot = 0;
        let prevTevot = 0;
        let prkLetters = 0;
        let prevLetters = 0;
        let totLetters = 0;
        let headers = "";
        if (bookPrk && !!!bookPsk) {
            this.output = "<span style=\"color:blue;\"> פרק " + this.no2gim.no2gim(bookPrk) + "  </span></br>";
        }
        if (bookNum == 27 && !!!bookPrk) {
            this.output += "</br>ליום ראשון - א, ליום שני - <a id=\"book\"/><a href=\"#prk30\">ל</a> , ליום שלישי - <a id=\"book\"/><a href=\"#prk51\">נא</a> , ליום רביעי - <a id=\"book\"/><a href=\"#prk73\">עג</a> , ליום חמישי - <a id=\"book\"/><a href=\"#prk90\">צ</a> , ליום שישי - <a id=\"book\"/><a href=\"#prk107\">קז</a> , ליום השבת - <a id=\"book\"/><a href=\"#prk120\">קכ</a>  " + "</br>";
        }
        if (bookNum >= 1 && bookNum <= 5 && !!!bookPrk) {
            this.output += "</br>" + TORAH_PARASHOT.map((parasha, index) => ({ parasha, index })).filter(({ parasha }) => parasha.book == bookNum).map(({ parasha, index }) => "<a href=\"#parasha" + index + "\">" + parasha.name + "</a>").join(" , ") + "</br>";
        }
        // console.info("Book "+ bookNum);
        // todo output += בראשית נח לך-לך וירא and maybe with pointer
        var lastI = 0;
        for (let i = 0; i < this.repo.getVerses().length; ++i) {
            lastI = i;
            if (this.repo.getBookNumArr()[i] == bookNum-1 ) {
                if (!!!bookPrk || Number(this.repo.getPPrk()[i]) == bookPrk) {
                    if (bookPrk)
                        this.output += "<span style=\"color:blue;\">";
                    if (((!!!bookPsk || Number(this.repo.getPPsk()[i]) >= bookPsk)) && (!!!amount || psukim < amount)) {
                        ++ psukim;
                        if (this.repo.getPPsk()[i] == 1 || (this.repo.getBookNumArr()[i] == 27-1 && this.repo.getPPrk()[i] == 119 && this.repo.getPPsk()[i] % 8 == 1)) 
                            this.output += "</br>";
                        if (bookNum >= 1 && bookNum <= 5) {
                            const parashaIndex = TORAH_PARASHOT.findIndex(parasha => parasha.book == bookNum && parasha.chapter == Number(this.repo.getPPrk()[i]) && parasha.verse == Number(this.repo.getPPsk()[i]));
                            if (parashaIndex >= 0)
                                this.output += "<a id=\"parasha" + parashaIndex + "\"></a>";
                        }
                        // todo if psk,prk,book in Parashot, + br
                        if (this.repo.getPPsk()[i] == 1) {
                            if (this.repo.getPPrk()[i] >= 2) {
                                this.output += "<a id=\"prk" + this.repo.getPPrk()[i] + "\"/>" + "<a onClick=\"alert(' פרק ' + " + (this.repo.getPPrk()[i]-1) + " + ' תיבות ' + " + (prkTevot - prevTevot)  + " + ' אותיות ' + " + (prkLetters  - prevLetters) + ")\" href=\"#book\">";
                            }
                            else {
                                this.output += "<a id=\"prk" + this.repo.getPPrk()[i] + "\"/>" + "<a href=\"#book\">";
                            }
                        }
                        this.output += this.no2gim.no2gim(this.repo.getPPrk()[i]);
                        if (this.repo.getPPsk()[i] == 1)
                            this.output += "</a>";
                        let rashiUrl = "<a href=\"" + "https://wiki.jewishbooks.org.il/mediawiki/wiki/%D7%A8%D7%A9%22%D7%99/" + bookHeb + "/" + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "#" + this.no2gim.no2gim(this.repo.getPPsk()[i]) + "\"" + " target=\"_blank\" rel=\"noopener noreferrer\">" + this.repo.getPPsk()[i] + "</a>";
                        this.output += "-" + rashiUrl + " -- ";
                        if (this.repo.getNikkudVerses()[i].length >= 1) { 
                            this.output += this.repo.getNikkudVerses()[i];
                        }
                        else {
                            this.output += this.repo.noName(this.repo.getVerses()[i]);
                        }
                        this.output += "<br/>";
                        totLetters += this.repo.getVerses()[i].replace(/\s+/g, '').length;
                        tevot += this.repo.getVerses()[i].split(' ').length; // - 1;
                        if (bookPrk)
                            this.output += "</span>";
                        if (this.repo.getPPsk()[i] == 1) {
                            if (this.repo.getPPrk()[i] > 1) {
                                // headers += " " +  (tevot - prevTevot);
                                // console.info("Perek " + this.repo.getPPrk()[i-1] + " Tevot " + (prkTevot - prevTevot) + " Letters: " +  (prkLetters - prevLetters));
                            }
                            headers += "<a id=\"book\"/><a href=\"#prk" + this.repo.getPPrk()[i] + "\">" + this.no2gim.no2gim(this.repo.getPPrk()[i]) + "</a> ";
                            prevTevot = prkTevot;
                            prevLetters = prkLetters;
                        }
                        prkLetters += this.repo.getVerses()[i].replace(/\s+/g, '').length;
                        prkTevot += this.repo.getVerses()[i].split(' ').length; // - 1;
                        if (amount !== 1) {
                            clearText += "(" + (this.repo.getPPsk()[i]) + ") ";
                        }
                        if (this.repo.getNikkudVerses()[i].length >= 1) { 
                            clearText += this.repo.getNikkudVerses()[i];
                        }
                        else {
                            clearText += this.repo.getVerses()[i];
                        }
                        clearText += ": ";
                    }
                }
            }
            else if (this.repo.getBookNumArr()[i] > bookNum-1 && bookPrk == undefined) { 
                if (bookeng[bookNum-1] === 'Isaiah')
                    this.output += "<br/>" + this.repo.getVerses()[i-2] + "<br/>";
                else if (bookeng[bookNum-1] === 'Malachi')
                    this.output += "<br/>" + this.repo.getVerses()[i-2] + "<br/>";
                else if (bookeng[bookNum-1] === 'Song of songs')
                    this.output += "<br/>" + this.repo.getVerses()[i-2] + "<br/>";
                else if (bookeng[bookNum-1] === 'Lamentations')
                    this.output += "<br/>" + this.repo.getVerses()[i-2] + "<br/>";
                else if (bookeng[bookNum-1] === 'Ecclesiastes')
                    this.output += "<br/>" + this.repo.getVerses()[i-2] + "<br/>";
                break;
            }
        }

        this.output = this.output.substring(0, this.output.length - "<br/>".length);
        this.output += "<a id=\"lastPrk" + "\"/>" + "<a onClick=\"alert(' פרק אחרון תיבות ' + " + (prkTevot - prevTevot)  + " + ' אותיות ' + " + (prkLetters  - prevLetters) + ")\" href=\"#book\">.</a>";
        
        // console.info("Perek " + this.repo.getPPrk()[lastI-1] + " Tevot " + (prkTevot - prevTevot) + " Letters: " +  (prkLetters - prevLetters));
        // console.info("Tevot on last chapter " + (prevTevot - tevot));
        
        if (!!!bookPrk) {
            var pointerArr = ['01','02','03','04','05','06','07','08a',
                '08b','09a','09b','10','11','12','13','14','15','16','17','18','19',
                '20','21','22','23','24','26','28','27','30','29',
                '32','31','33','34','35a','35b','25a','25b']; // 39 books
            let pointer = pointerArr[bookNum-1];
            let engTrans = "<a href=\"https://mechon-mamre.org/p/pt/pt" + pointer + "01.htm\" target=\"_blank\" rel=\"noopener noreferrer\">" + bookeng[bookNum-1] + "</a>";
            this.output = engTrans + "</br></br>" + "פסוקים: " + psukim + "</br>" + "אותיות: " + totLetters + "</br>" + "תיבות: " + tevot + "</br></br>" + headers + "</br>" + this.output;
        }
        const reference = bookPrk ? `${bookNum},${bookPrk}` : bookNum;
        this.output += "<br/><br/><span class=\"share\">&gt;</span><br><br><p dir=\"ltr\" class=\"share-url\">" + createAppUrl({ r: reference });
        if (!!!siddurCall)
            document.getElementById("bibleResult").innerHTML = this.output +
             "</p>";
        return clearText;
    }

}

export { Read }
