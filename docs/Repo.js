let instance;

let torTxt = [];
let verses = [];
let cntLetter = []; // [5846] = 304806-1 (Ok!)
let PPskArr = [];
let PPrkArr = [];
let currBookArr = [];
let bookNumArr = [];
let gims = [];
let TOTLETTERS;

class Repo {

    suffix(target) {
        target = target.replace(/\u05DA/g, 'כ');
        target = target.replace(/\u05DD/g, 'מ');
        target = target.replace(/\u05DF/g, 'נ');
        target = target.replace(/\u05E3/g, 'פ');
        target = target.replace(/\u05E5/g, 'צ');
        return target;
    }

    getCntLetter() {
        return cntLetter;
    }

    getTorTxt() {
        return torTxt;
    }

    getGims() {
        return gims;
    }

    setTOTLETTERS(i) {
        TOTLETTERS = i;
    }

    getTOTLETTERS() {
        return TOTLETTERS;
    }

    getVerses() {
        return verses;
    }

    getPPrk() {
        return PPrkArr;
    }

    getPPsk() {
        return PPskArr;
    }

    getCurrBook() {
        return currBookArr;
    }

    getHebChar(i) {
        return (i !== 31 ?  String.fromCharCode('א'.charCodeAt(0) + i) : " ");
    }

    getBookNumArr() {
        return bookNumArr;
    }

    addTorTxt(s) {
        torTxt.push(s);
    }

    addVerse(s) {
        verses.push(s);
        gims.push(this.gim(s.trim()));
    }

    addPPskArr(s) {
        PPskArr.push(s);
    }

    addPPrkArr(s) {
        PPrkArr.push(s);
    }

    addCurrBookArr(s) {
        currBookArr.push(s);
    }

    addBookNumArr(s) {
        bookNumArr.push(s);
    }

    addCntLetter(s) {
        cntLetter.push(s);
    }

    getSize() {
        return verses.length;
    }

    constructor() {
        if (instance) {
          throw new Error("You can only create one instance!");
        }
        instance = this;
        cntLetter.push(1);
    }

    getInstance() {
        return this;
    }

    enable(t) {
        if (document.getElementById(t)) {
            document.getElementById(t).disabled = false;
        }
    }

    done() {
        this.enable('button1');
        this.enable('buttonD');
        this.enable('buttonG');
        this.enable('showBook');
        this.enable('buttonF');
    }

    noName(orig) {
        orig = orig.replaceAll("יהוה", "ה'");
        orig = orig.replaceAll("שדי", "ש-די");
        orig = orig.replaceAll("אלהים", "א-להים");
        orig = orig.replaceAll("אלהינו", "א-להינו");
        // orig = orig.replaceAll(" אל ", " א-ל ");
        orig = orig.replaceAll("שדי", "ש-די");
        orig = orig.replaceAll("אדני", "א-דני");
        orig = orig.replaceAll("אלוה", "א-לוה");
        orig = orig.replaceAll("צבאות", "צ-באות");
        return orig;
    }

    gim(str) {
        let sum = parseInt(str);
        if (isNaN(sum)) {
            sum = 0;
            const aleph = 1488;
            for (let i = 0; i < str.length; ++ i) {
              if (str.charCodeAt(i) >= aleph && str.charCodeAt(i) <= (aleph+10-1)) {
                sum += str.charCodeAt(i) - (aleph-1);
              }
              else if (str.charCodeAt(i) === aleph+10 || str.charCodeAt(i) === aleph+11) {
                sum += 20;
              }
              else if (str.charCodeAt(i) === aleph+12) {
                sum += 30;
              }
              else if (str.charCodeAt(i) === aleph+13 || str.charCodeAt(i) === aleph+14) {
                sum += 40;
              }
              else if (str.charCodeAt(i) === aleph+15 || str.charCodeAt(i) === aleph+16 || str.charCodeAt(i) === 1487) {
                sum += 50;
              }
              else if (str.charCodeAt(i) === aleph+17) {
                sum += 60;
              }
              else if (str.charCodeAt(i) === aleph+18) {
                sum += 70;
              }
              else if (str.charCodeAt(i) === aleph+19 || str.charCodeAt(i) === aleph+20) {
                sum += 80;
              }
              else if (str.charCodeAt(i) === aleph+21 || str.charCodeAt(i) === aleph+22) {
                sum += 90;
              }
              else if (str.charCodeAt(i) >= aleph+23 && str.charCodeAt(i) <= aleph+23+4-1) {
                sum += 100 * (str.charCodeAt(i) - (aleph+23) + 1);
              }
          }
      }
      return sum;
    }

}

const repo = Object.freeze(new Repo());
export default repo;
