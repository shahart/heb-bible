
class Dilug {

    repo;

    constructor(repo) {
        this.repo = repo;
    }

    doAbort() {
        console.log('החיפוש הופסק');
        document.getElementById('sabort').disabled = true;
    }

    dilug() {
        document.getElementById('sabort').disabled = false;
        var skipMin = Math.ceil(document.getElementById("skipMin").value);
        if (skipMin < 0) { skipMin = 1; }
        var skipMax = Math.ceil(document.getElementById("skipMax").value);
        if (skipMax < 0) { skipMax = 1; }
        if (skipMax >= 10000) { skipMax = 9999 ; }
        var startTime = new Date();
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

        var indVrsRange = function(cntLtr, indLowVrs, indHigVrs) {
          if (indLowVrs == indHigVrs) {
            return indLowVrs;
          }
          else {
            var indMidVrs = Math.ceil((indLowVrs + indHigVrs) / 2);
            if (cntLtr < repo.getCntLetter()[indMidVrs]) {
                return indVrsRange(cntLtr, indLowVrs, indMidVrs-1);
            }
            else {
              return indVrsRange(cntLtr, indMidVrs, indHigVrs);
            }
          }
        }

        var repo = this.repo;
        var el = document.getElementById("resultDilug");
        var iSkip = skipMin;
        window.requestAnimationFrame(function loop() {
            el.innerHTML = iSkip;
            let lastInd = repo.getTOTLETTERS() - (targetLen-1) * iSkip;
            for (let j = 0; j < lastInd; j++) { // loop on Torah
                let match = true;
                for (let k = 0; k < targetLen; k++) { // loop on target
                    if (repo.getTorTxt()[j+k*iSkip] != target[k]) {
                        match = false;
                        break;
                    }
                }
                if (!match) {
                    match = true;
                    for (let k = 0; k < targetLen; k++) { // loop on target
                        if (repo.getTorTxt()[j+k*iSkip] != target[targetLen-k-1]) {
                            match = false;
                            break;
                        }
                    }
                }
                if (match) {
                    var foundStr = "דילוג של " + iSkip + " החל ממיקום " + (j+1).toString() + "<br>";
                    var idx = indVrsRange(j+1, 0, repo.getVerses().length); // todo fix?
                    foundStr += repo.getVerses()[idx] + " - " + repo.getCurrBook()[idx] + " " + repo.getPPrk()[idx] + "-" + repo.getPPsk()[idx];

                    let txt = "";
                    for (let h = j; h <= j+ targetLen * iSkip; ++h) {
                        txt += repo.getTorTxt()[h];
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
            ++iSkip;
            if (iSkip <= skipMax && document.getElementById('sabort').disabled == false) {
                window.requestAnimationFrame(loop);
            }
        });
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
