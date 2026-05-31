import Repo from "./Repo.js";

class RepoInit {

    static DB_NAME = 'heb-bible-cache';
    static DB_VERSION = 1;
    static STORE_NAME = 'files';

    constructor() {
        this.ready = this.init();
    }

    async openDb() {
        if (typeof indexedDB === "undefined") {
            throw new Error("IndexedDB is not available");
        }

        return new Promise((resolve, reject) => {
            const request = indexedDB.open(RepoInit.DB_NAME, RepoInit.DB_VERSION);
            request.onupgradeneeded = () => {
                const db = request.result;
                if (!db.objectStoreNames.contains(RepoInit.STORE_NAME)) {
                    db.createObjectStore(RepoInit.STORE_NAME);
                }
            };
            request.onsuccess = () => resolve(request.result);
            request.onerror = () => reject(request.error);
        });
    }

    async idbGet(key) {
        const db = await this.openDb();
        return new Promise((resolve, reject) => {
            const tx = db.transaction(RepoInit.STORE_NAME, 'readonly');
            const store = tx.objectStore(RepoInit.STORE_NAME);
            const request = store.get(key);
            request.onsuccess = () => resolve(request.result || "");
            request.onerror = () => reject(request.error);
            tx.oncomplete = () => db.close();
            tx.onerror = () => db.close();
            tx.onabort = () => db.close();
        });
    }

    async idbSet(key, value) {
        const db = await this.openDb();
        return new Promise((resolve, reject) => {
            const tx = db.transaction(RepoInit.STORE_NAME, 'readwrite');
            const store = tx.objectStore(RepoInit.STORE_NAME);
            store.put(value, key);
            tx.oncomplete = () => {
                db.close();
                resolve();
            };
            tx.onerror = () => {
                db.close();
                reject(tx.error);
            };
            tx.onabort = () => {
                db.close();
                reject(tx.error);
            };
        });
    }

    async fetchText(candidates) {
        let lastError = null;
        for (const filePath of candidates) {
            try {
                const response = await fetch(filePath, { cache: 'no-cache' });
                if (!response.ok) {
                    lastError = new Error(`Failed to fetch ${filePath}: ${response.status}`);
                    continue;
                }
                return await response.text();
            }
            catch (err) {
                lastError = err;
            }
        }
        throw lastError || new Error("Failed to fetch text");
    }

    async fetchBinary(candidates) {
        let lastError = null;
        for (const filePath of candidates) {
            try {
                const response = await fetch(filePath, { cache: 'no-cache' });
                if (!response.ok) {
                    lastError = new Error(`Failed to fetch ${filePath}: ${response.status}`);
                    continue;
                }
                return new Uint8Array(await response.arrayBuffer());
            }
            catch (err) {
                lastError = err;
            }
        }
        throw lastError || new Error("Failed to fetch binary data");
    }

    async loadCachedValue(key) {
        let cachedValue = "";
        try {
            cachedValue = await this.idbGet(key);
        }
        catch (err) {
            console.warn(`IndexedDB read failed for ${key}: ${err}`);
        }
        if (cachedValue !== "") {
            return cachedValue;
        }
        if (typeof(Storage) !== "undefined") {
            return localStorage.getItem(key) || "";
        }
        return "";
    }

    async loadCachedData() {
        return {
            unzip: await this.loadCachedValue('unzip'),
            nData: await this.loadCachedValue('nData')
        };
    }

    async saveCachedData(unzip, nData) {
        try {
            await this.idbSet('unzip', unzip);
            await this.idbSet('nData', nData);
        }
        catch (err) {
            console.error(err);
        }
    }

    errata() {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', 'https://raw.githubusercontent.com/shahart/heb-bible/master/pascal/ERRATA.INF', true);
        xhr.overrideMimeType('text/plain; charset=x-user-defined'); // Hack to pass bytes through unprocessed
        xhr.onreadystatechange = function(e) {
          if ( this.readyState === 4 &&
                this.status === 200) {
            var binStr = this.responseText;
            let content = Uint8Array.from(binStr, c => c.charCodeAt(0));
            var eof = 0;
            var startTime = new Date();
            while (eof < binStr.length) {
                let kri = "";
                for (var k = eof + 12; k >= eof + 1; --k) {
                    let ch = content[k] - 1;
                    // s += getHebChar(binStr.charAt(k)-1);
                    kri += (ch !== 31 ?  String.fromCharCode('א'.charCodeAt(0) + ch - 127) : " ");
                }
                eof += 13;
                let ktiv = "";
                for (var k = eof + 12; k >= eof + 1; --k) {
                    let ch = content[k] - 1;
                    // s += getHebChar(binStr.charAt(k)-1);
                    ktiv += (ch !== 31 ?  String.fromCharCode('א'.charCodeAt(0) + ch - 127) : " ");
                }
                // console.debug(kri.trim() + " --> " + ktiv.trim() + " -- " + (content[eof + 13] - 31) + "- " + (content[eof + 14] - 31) + "- " + (content[eof + 15] - 31));
                var repo = Repo.getInstance();
                repo.kri("" + (content[eof + 13] - 31) + ":" + (content[eof + 14] - 31) + ":" + (content[eof + 15] - 31), kri.trim() + ":" + ktiv.trim());
                eof += 16;
            }
            console.log("Errata: " + eof/29);
            var endTime = new Date();
            console.log("Init Errata: " + (endTime - startTime) + " mSec");
          }
        }
        xhr.send();
    }

    async init() {
        if (Repo.getInstance().getSize() > 0) {
            console.log("Already init'ed");
            return;
        }

        console.log("Init..ing");
        var bookheb = ['תישארב','תומש','ארקיו','רבדמב','םירבד','עשוהי','םיטפוש','א לאומש','ב לאומש', 'א םיכלמ','ב םיכלמ','היעשי','הימרי','לאקזחי','עשוה','לאוי','סומע','הידבוע','הנוי','הכימ','םוחנ','קוקבח','הינפצ', 'יגח','הירכז','יכאלמ','םילהת','ילשמ','בויא','םירישה ריש','תור','הכיא','תלהק','רתסא','לאינד','ארזע','הימחנ', 'א םימיה ירבד','ב םימיה ירבד']; // 39 books
        var startTime = new Date();
        var repo = Repo.getInstance();
        var ungzipedData = "";
        var nData = "";
        const url = new URL(window.location.href);
        if (url.search !== '?refresh') {
            const cachedData = await this.loadCachedData();
            ungzipedData = cachedData.unzip;
            nData = cachedData.nData;
        }

        if (ungzipedData == "" || nData == "") {
            if (typeof pako === "undefined") {
                alert("No internet!");
                return;
            }

            if (ungzipedData == "") {
                const gzipedDataArray = await this.fetchBinary(["./bible.txt.gz", "https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz"]);
                ungzipedData = new TextDecoder().decode(pako.ungzip(gzipedDataArray));
            }

            if (nData == "") {
                nData = await this.fetchText(["./bible-niqqud.txt", "https://raw.githubusercontent.com/shahart/heb-bible/master/bible-niqqud.txt"]);
            }

            await this.saveCachedData(ungzipedData, nData);
        }

        var arr = ungzipedData.split("\n");
        var narr = nData == null ? null : nData.split("\n");
        console.log('Psukim: ', narr.length - 1);
        var TOTLETTERS = 1;
        var torTxtLength = 0;

        for (var j = 0; j < arr.length-1; ++j) {
            let line = arr[j].trim();
            let line1 = line.split(",")[1];
            let line0 = line.split(",")[0].split(":");
            repo.addVerse(line1);
            repo.addNikkudVerse(narr == null || narr.length == 1 ? "" : narr[j].trim().split(",")[1]);
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
        // this.errata();
    }
}

export { RepoInit }
