
class Dilug {

    repo;

    constructor(repo) {
        this.repo = repo;
    }

    indVrsRange(cntLtr, indLowVrs, indHigVrs) {
        if (indLowVrs == indHigVrs) {
            return indLowVrs;
        }
        else {
            var indMidVrs = Math.ceil((indLowVrs + indHigVrs) / 2);
            if (cntLtr < this.repo.getCntLetter()[indMidVrs]) {
                return this.indVrsRange(cntLtr, indLowVrs, indMidVrs-1);
            }
            else {
                return this.indVrsRange(cntLtr, indMidVrs, indHigVrs);
            }
        }
    }

    dilug() {
        var skipMin = Math.ceil(document.getElementById("skipMin").value);
        if (skipMin < 0) { skipMin = 1; }
        var skipMax = Math.ceil(document.getElementById("skipMax").value);
        if (skipMax < 0) { skipMax = 1; }
        if (skipMax >= 10000) { skipMax = 9999 ; }
        var startTime = new Date();
        // todo document.getElementById('buttonD').disabled = true;
        document.getElementById("resultDilug").innerHTML = "";
        let args = document.getElementById("dilugTxt").value;
        var found = 0;

        var target = args.replace(/\s+/g, ''); // remove spaces
        if (!(/^[\u05D0-\u05EA]+$/).test(target)) {
            alert("הטקסט לחיפוש חייב להכיל רק אותיות בעברית ורווחים");
            return;
        }
        target = this.repo.suffix(target);
        var targetLen = target.length;

        for (let iSkip = skipMin; iSkip <= skipMax; ++ iSkip) {
            let lastInd = this.repo.getTOTLETTERS() - (targetLen-1) * iSkip;
            for (let j = 0; j < lastInd; j++) { // loop on Torah
                let match = true;
                for (let k = 0; k < targetLen; k++) { // loop on target
                    if (this.repo.getTorTxt()[j+k*iSkip] != target[k]) {
                        match = false;
                        break;
                    }
                }
                if (!match) {
                    match = true;
                    for (let k = 0; k < targetLen; k++) { // loop on target
                        if (this.repo.getTorTxt()[j+k*iSkip] != target[targetLen-k-1]) {
                            match = false;
                            break;
                        }
                    }
                }
                if (match) {
                    var foundStr = "דילוג של " + iSkip + " החל ממיקום " + (j+1).toString() + "<br>";
                    var idx = this.indVrsRange(j+1, 0, this.repo.getVerses().length); // todo fix?
                    foundStr += this.repo.getVerses()[idx] + " - " + this.repo.getCurrBook()[idx] + " " + this.repo.getPPrk()[idx] + "-" + this.repo.getPPsk()[idx];

                    let txt = "";
                    for (let h = j; h <= j+ targetLen * iSkip; ++h) {
                        txt += this.repo.getTorTxt()[h];
                    }
                    foundStr += "<pre>";
                    for (let h = 0; h < targetLen; ++h) {
                        foundStr += "<br>" + "<b>" + txt[(h)*iSkip] + "</b>"  + txt.substring(h*iSkip+1, (h+1)*iSkip) ;
                    }
                    foundStr += "</pre>";
                    document.getElementById("resultDilug").innerHTML = foundStr;
                    found++;
                    j = lastInd;
                    iSkip = skipMax + 1;
                }
            }
            // todo document.getElementById('buttonD').disabled = false;
        }
        if (found == 0) {
            document.getElementById("resultDilug").innerHTML = "Not found";
        }
        var endTime = new Date();
        console.log((endTime - startTime) + " mSec");

        //
        var xhrAws = new XMLHttpRequest();
        xhrAws.open('POST', 'https://z4r74tvfwdi3wywr4aegh4f3di0zhhuo.lambda-url.eu-north-1.on.aws/');
        xhrAws.setRequestHeader("Content-Type", "application/json");
        xhrAws.send(JSON.stringify({ "name": args, "found": (found >= 1), "dilugim": true }));
        xhrAws.onreadystatechange = function(e) {
          if ( xhrAws.readyState === 4 &&
                xhrAws.status === 200) {
            console.log(this.responseText);
          }
        }
        xhrAws.send();
    }

}

export { Dilug }
