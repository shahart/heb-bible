import Repo from "./Repo.js";

class RepoInit {

    loadFile(filePath) {
        var res = null;
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", filePath, false);
        xmlhttp.overrideMimeType('text/plain; charset=x-user-defined');
        xmlhttp.send();
        if (xmlhttp.status == 200) {
            res = xmlhttp.responseText;
        }
        return res;
    }

    init() {
        if (Repo.getInstance().getSize() > 0) {
            console.log("Already init'ed");
            return;
        }
        console.log("Init..ing");
        var bookheb = ['תישארב','תומש','ארקיו','רבדמב','םירבד','עשוהי','םיטפוש','א לאומש','ב לאומש', 'א םיכלמ','ב םיכלמ','היעשי','הימרי','לאקזחי','עשוה','לאוי','סומע','הידבוע','הנוי','הכימ','םוחנ','קוקבח','הינפצ', 'יגח','הירכז','יכאלמ','םילהת','ילשמ','בויא','םירישה ריש','תור','הכיא','תלהק','רתסא','לאינד','ארזע','הימחנ', 'א םימיה ירבד','ב םימיה ירבד']; // 39 books
        var startTime = new Date();
        var repo = Repo.getInstance();
        var ungzipedData = "";
        if (typeof (Storage) !== "undefined") {
            ungzipedData = localStorage.getItem('unzip') || "";
        }
        if (ungzipedData == "") {
            if (!(typeof(pako) != 'undefined')) {
                alert("No internet!");
            }
            const gezipedData = this.loadFile("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz"); 
            const gzipedDataArray = Uint8Array.from(gezipedData, c => c.charCodeAt(0));
            ungzipedData = new TextDecoder().decode(pako.ungzip(gzipedDataArray));
            if (typeof (Storage) !== "undefined") {
                localStorage.setItem('unzip', ungzipedData);
            }
        }
        let arr = ungzipedData.split("\n");
        console.log('Psukim: ', arr.length - 1);
        var TOTLETTERS = 1;
        var torTxtLength = 0;

        for (var j = 0; j < arr.length-1; ++j) {
            let line = arr[j].trim();
            let line1 = line.split(",")[1];
            let line0 = line.split(",")[0].split(":");
            repo.addVerse(line1);
            let currBook = line0[0];
            repo.addBookNumArr(currBook-1);
            let bookName = (bookheb[currBook-1]).split('').reverse().join('');
            repo.addCurrBookArr(bookName);
            repo.addPPrkArr(line0[1]);
            repo.addPPskArr(line0[2]);
            let pasuk = line1.replace(/\s+/g, '');
            pasuk = repo.suffix(pasuk);
            TOTLETTERS += pasuk.length;
            repo.setTOTLETTERS(TOTLETTERS);
            repo.addCntLetter(TOTLETTERS);
            for (let g = 0; g < pasuk.length; ++g) {
                ++ torTxtLength;
                repo.addTorTxt(pasuk[g]);
            }
            repo.addDoc(bookName, line.split(",")[0], line1);
        }

        var endTime = new Date();
        console.log("Init: " + (endTime - startTime) + " mSec");
        console.log(Repo.getSize());
        repo.done();
    }

    constructor() {
        this.init();
    }
}

export { RepoInit }
