// "use strict";
var http = require("https");

function getHebChar(i) {
    return (i !== 31 ?  String.fromCharCode('א'.charCodeAt(0) + i) : " ");
}
  
function decrypt(findStr2) {
    var s = "";
    var m = 2; // 0-Prk, 1-Psk
    let i = 1;
    while (i <= 72) {
        s = getHebChar(((findStr2[m]) >> 3)) + s;
        s = getHebChar((((findStr2[m]) & 7) << 2) | ((findStr2[m + 1]) >> 6)) + s;
        s = getHebChar(((findStr2[m + 1]) & 62) >> 1) + s;
        s = getHebChar((((findStr2[m + 1]) & 1) << 4) | ((findStr2[m + 2]) >> 4)) + s;
        s = getHebChar((((findStr2[m + 2]) & 15) << 1) | ((findStr2[m + 3]) >> 7)) + s;
        s = getHebChar(((findStr2[m + 3]) & 124) >> 2) + s;
        s = getHebChar((((findStr2[m + 3]) & 3) << 3) | ((findStr2[m + 4]) >> 5)) + s;
        s = getHebChar((findStr2[m + 4]) & 31) + s;
        m += 5;
        i += 8;
    }
    return s.trim();
}

exports.handler = async(event, context, callback) => {

//export const handler = async (event) => {
    
  console.log(1);

  var EndFile = 0; // amount of verses: 22329
  var bookeng = ['Genesis','Exodus','Leviticus','Numbers','Deuteronomy','Joshua','Judges','Samuel 1',
      'Samuel 2','Kings 1','Kings 2','Isaiah','Jeremiah','Ezekiel','Hosea','Joel','Amos','Obadiah','Jonah','Micha','Nachum',
      'Habakkuk','Zephaniah','Haggai','Zechariah','Malachi','Psalms','Proverbs','Job','Song of songs','Ruth','Lamentations',
      'Ecclesiastes','Esther','Daniel','Ezra','Nehemiah','Cronicles 1','Cronicles 2']; // 39 books
  var currBook = bookeng[0];
  var currBookIdx = 0;
  var PPrk = 1;
  var PPsk = 999;
  
  function isValid(line, containsName, args) {
      if ((line.charAt(1) === args.charAt(0) && line.charAt(line.length-1) === args.charAt(args.length-1)) || (containsName && line.indexOf(args) >= 0)) {
          console.log(line + " -- " + currBook + " " + PPrk + "-" + PPsk);
          ++ EndFile;
      }
  }
  
  var data = [];
  get("https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.5bit", (res) => {
  	res.on("data", function(chunk) {
  		data.push(chunk); // around 16k of binary data
  		console.log(chunk.length);
  	});
  	res.on("end", function() {
  		data = Buffer.concat(data);
          pasuk(data);
  	});
  });
  
  const response = {
    statusCode: 200,
    body: JSON.stringify(EndFile),
  };
  return response;
  
  
  function pasuk(data) {
      console.log(data.lengh);
      var args = event.name;
      var containsName = event.containsName || false;
      var binStr = data; 
      var line = "";
      currBookIdx = 0;
      currBook = bookeng[0];
      let findStr2 = [];
      EndFile = 0;
      PPsk = 999;
      PPrk = 1;
      var eof = 0;
  
      while (eof < binStr.length) {
          findStr2 = [];
          for (var i = 0; i < 47; ++i) {
              var c = binStr[eof];
              var xbyte = c & 0xff;  // byte at offset i
              findStr2.push(xbyte);
              ++ eof;
          }
          if (findStr2[1] - 31 !== PPsk
                  && line.length > 0) {
              isValid(line, containsName, args);
              if (findStr2[0] - 31 === 1 && findStr2[1] - 31 === 1 && findStr2[1] - 31 !== PPsk) {
                  ++currBookIdx;
                  currBook = bookeng[currBookIdx];
              }
              line = "";
          }
          PPrk = findStr2[0] - 31;
          PPsk = findStr2[1] - 31;
          line = line + " " + decrypt(findStr2);
      }
      isValid(line, containsName, args);

    }

    callback(null, EndFile);
}

