let instance;

let torTxt = [];
let verses = [];
let cntLetter = []; // [5846] = 304806-1 (Ok!)
let PPskArr = [];
let PPrkArr = [];
let currBookArr = [];
let bookNumArr = [];
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
        this.enable('showBook');
    }
}

const repo = Object.freeze(new Repo());
export default repo;
