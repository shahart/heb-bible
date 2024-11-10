import Repo from "./repo.js";

class RepoInit {

    init() {
        if (Repo.getInstance().getSize() > 0) {
            console.log("Already init'ed");
            return;
        }
        console.log("Init..ing");
        var bookheb = ['תישארב','תומש','ארקיו','רבדמב','םירבד','עשוהי','םיטפוש','א לאומש','ב לאומש', 'א םיכלמ','ב םיכלמ','היעשי','הימרי','לאקזחי','עשוה','לאוי','סומע','הידבוע','הנוי','הכימ','םוחנ','קוקבח','הינפצ', 'יגח','הירכז','יכאלמ','םילהת','ילשמ','בויא','םירישה ריש','תור','הכיא','תלהק','רתסא','לאינד','ארזע','הימחנ', 'א םימיה ירבד','ב םימיה ירבד']; // 39 books
        var startTime = new Date();
        var xhr = new XMLHttpRequest();
        xhr.open('GET', 'https://raw.githubusercontent.com/shahart/heb-bible/master/BIBLE.TXT', true);
        xhr.overrideMimeType('text/plain; charset=x-user-defined'); // Hack to pass bytes through unprocessed
        xhr.onreadystatechange = function(e) {
          if ( this.readyState === 4 &&
                this.status === 200) {
            var binStr = this.responseText;
            var line = "";
            var currBookIdx = 0;
            var currBook = bookheb[0].split('').reverse().join('');
            let findStr2 = [];
            var EndFile = 0;
            var output = "";
            var PPsk = 999;
            var PPrk = 1;
            var eof = 0;
            var TOTLETTERS = 1;
            var currBookIdx = 0;
            var repo = Repo.getInstance();
            var torTxtLength = 0;
            while (eof < binStr.length) {
                findStr2 = [];
                for (var j = 0; j < 47; ++j) {
                  var c = binStr.charCodeAt(eof);
                  var xbyte = c & 0xff;  // byte at offset i
                  findStr2.push(xbyte);
                  ++ eof;
                }
                if (findStr2[1] - 31 !== PPsk
                        && line.length > 0) {
                    let pasuk = line.replace(/\s+/g, '');
                    pasuk = repo.suffix(pasuk);
                    TOTLETTERS += pasuk.length;
                    for (let g = 0; g < pasuk.length; ++g) {
                        ++ torTxtLength;
                        repo.addTorTxt(pasuk[g]);
                    }
                    ++ EndFile;
                    repo.addVerse(line);
                    repo.addCntLetter(TOTLETTERS);
                    repo.addCurrBookArr(currBook);
                    repo.addPPskArr(PPsk);
                    repo.addPPrkArr(PPrk);
                    if (findStr2[0] - 31 === 1 && findStr2[1] - 31 === 1 && findStr2[1] - 31 !== PPsk) {
                        ++currBookIdx;
                        currBook = (bookheb[currBookIdx]).split('').reverse().join('');
                    }
                    line = "";
                }
                PPrk = findStr2[0] - 31;
                PPsk = findStr2[1] - 31;
                // decrypt
                var s = "";
                var m = 2; // 0-Prk, 1-Psk
                let i = 1;
                while (i <= 72) {
                    s = repo.getHebChar(((findStr2[m]) >> 3)) + s;
                    s = repo.getHebChar((((findStr2[m]) & 7) << 2) | ((findStr2[m + 1]) >> 6)) + s;
                    s = repo.getHebChar(((findStr2[m + 1]) & 62) >> 1) + s;
                    s = repo.getHebChar((((findStr2[m + 1]) & 1) << 4) | ((findStr2[m + 2]) >> 4)) + s;
                    s = repo.getHebChar((((findStr2[m + 2]) & 15) << 1) | ((findStr2[m + 3]) >> 7)) + s;
                    s = repo.getHebChar(((findStr2[m + 3]) & 124) >> 2) + s;
                    s = repo.getHebChar((((findStr2[m + 3]) & 3) << 3) | ((findStr2[m + 4]) >> 5)) + s;
                    s = repo.getHebChar((findStr2[m + 4]) & 31) + s;
                    m += 5;
                    i += 8;
                }
                line = line + " " + s.trim();
            }
            let pasuk = line.replace(/\s+/g, '');
            pasuk = repo.suffix(pasuk);
            TOTLETTERS += pasuk.length;
            repo.setTOTLETTERS(TOTLETTERS);
            ++ EndFile;
            repo.addVerse(line);
            repo.addCurrBookArr(currBook);
            repo.addPPskArr(PPsk);
            repo.addPPrkArr(PPrk);
            var endTime = new Date();
            console.log("Init: " + (endTime - startTime) + " mSec");
            console.log(Repo.getSize());
            repo.done();
          }
        }
        xhr.send();
    }

    constructor() {
        this.init();
    }
}

export { RepoInit }
