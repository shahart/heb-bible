import { RepoInit } from "./RepoInit.js";
import Repo from "./Repo.js";
import { Pasuk } from "./Pasuk.js";
import { Dilug } from "./Dilug.js";
import { Read } from "./Read.js";
import { Gematria } from "./Gematria.js";
import { Find } from "./Find.js";

/*
window.onerror = function(message) { 
    alert(message); 
    console(message); 
    return true; 
};
*/

new RepoInit();

let pasuk = new Pasuk(Repo);
let dilug = new Dilug(Repo);
let read = new Read(Repo);
let gematria = new Gematria(Repo);
let find = new Find(Repo);

const params = new URLSearchParams(document.location.search);

const r = params.get("r");
const p = params.get("p");
const s = params.get("s"); // (s)kip
const g = params.get("g");
const q = params.get("q"); // (q)uery
const l = params.get("l");

const firstName = params.get("firstName");
if (firstName && firstName !== '') {
    console.info(firstName);
}

let cookieInput = firstName && firstName !== '' ? firstName : pasuk.loadInput("input");
if (cookieInput !== "") {
    document.getElementById('text').value = cookieInput;
}
let totalPsukim = firstName && firstName !== '' ? 0 : pasuk.loadInput("resCount");
if (totalPsukim !== "") {
    document.getElementById("result").innerHTML = totalPsukim + " פסוקים";
}

document.getElementById('showBook').addEventListener('click', () => {
    read.read();
});

document.getElementById('button1').addEventListener('click', () => {
    pasuk.pasuk();
});

if (firstName && firstName.trim() !== '') {
    pasuk.pasuk();
}

document.getElementById('buttonD').addEventListener('click', () => {
    dilug.dilug();
});

document.getElementById('buttonG').addEventListener('click', () => {
    gematria.gematria();
});

document.getElementById('buttonF').addEventListener('click', () => {
    find.find();
});

document.getElementById('sabort').addEventListener('click', () => {
    dilug.doAbort();
});

if (r && r.trim() !== '') {
    try {
      let book = r;
      let prk = undefined;
      if (r.indexOf(",")>0) {
        book=r.split(",")[0];
        prk = r;
      }
      document.getElementById("BookSelect").value = book;
      read.read(prk);
    }
    catch (e) {
      console.error(e);
      alert("מספר הספר אינו חוקי, 39..1" );
    }
}

if (p && p.trim() !== '') {
    document.getElementById("text").value = p;
    document.getElementById("tab2").checked = true;
    pasuk.pasuk();
}

if (s && s.trim() !== '') {
    document.getElementById("dilugTxt").value = s;
    document.getElementById("tab3").checked = true;
    const from = params.get("from"); 
    if (from && from.trim() !== '')
        document.getElementById("skipMin").value = from;
    document.getElementById("chars").textContent = s.length + ' '; 
    dilug.dilug();
}

if (g && g.trim() !== '') {
    document.getElementById("gim").value = g;
    document.getElementById("tab4").checked = true;
    gematria.gematria();
}

if (q && q.trim() !== '') {
    document.getElementById("find").value = q.replace("%20", ' ');
    document.getElementById("tab5").checked = true;
    find.find();
}

if (l) {
    document.getElementById("tab6").checked = true;
}

document.getElementById("text").addEventListener('keyup', function(e) {
    if (e.key === "Enter") {
        pasuk.pasuk();
    }
})

document.getElementById("dilugTxt").addEventListener('keyup', function(e) {
    if (e.key === "Enter") {
        dilug.dilug();
    }
})

document.getElementById("gim").addEventListener('keyup', function(e) {
    if (e.key === "Enter") {
        gematria.gematria();
    }
})

document.getElementById("find").addEventListener('keyup', function(e) {
    if (e.key === "Enter") {
        find.find();
    }
})

document.getElementById('psalms1B').addEventListener('click', () => {
    document.getElementById('psalms1').innerHTML = (document.getElementById('psalms1').innerHTML !== "") ? "" : read.read("27,1", true);
})

document.getElementById('psalms6B').addEventListener('click', () => {
    document.getElementById('psalms6').innerHTML = (document.getElementById('psalms6').innerHTML !== "") ? "" : read.read("27,6", true);
})

document.getElementById('psalms13B').addEventListener('click', () => {
    document.getElementById('psalms13').innerHTML = (document.getElementById('psalms13').innerHTML !== "") ? "" : read.read("27,13", true);
})

document.getElementById('psalms20B').addEventListener('click', () => {
    document.getElementById('psalms20').innerHTML = (document.getElementById('psalms20').innerHTML !== "") ? "" : read.read("27,20", true);
})

document.getElementById('psalms38B').addEventListener('click', () => {
    document.getElementById('psalms38').innerHTML = (document.getElementById('psalms38').innerHTML !== "") ? "" : read.read("27,38", true);
})

document.getElementById('psalms83B').addEventListener('click', () => {
    document.getElementById('psalms83').innerHTML = (document.getElementById('psalms83').innerHTML !== "") ? "" : read.read("27,83", true);
})

document.getElementById('psalms85B').addEventListener('click', () => {
    document.getElementById('psalms85').innerHTML = (document.getElementById('psalms85').innerHTML !== "") ? "" : read.read("27,85", true);
})

document.getElementById('psalms102B').addEventListener('click', () => {
    document.getElementById('psalms102').innerHTML = (document.getElementById('psalms102').innerHTML !== "") ? "" : read.read("27,102", true);
})

document.getElementById('psalms106B').addEventListener('click', () => {
    document.getElementById('psalms106').innerHTML = (document.getElementById('psalms106').innerHTML !== "") ? "" : read.read("27,106", true);
})

document.getElementById('psalms130B').addEventListener('click', () => {
    document.getElementById('psalms130').innerHTML = (document.getElementById('psalms130').innerHTML !== "") ? "" : read.read("27,130", true);
})

document.getElementById('psalms142B').addEventListener('click', () => {
    document.getElementById('psalms142').innerHTML = (document.getElementById('psalms142').innerHTML !== "") ? "" : read.read("27,142", true);
})

document.getElementById('lucene').addEventListener('click', () => {
    document.getElementById('lunrJsTip').innerHTML = (document.getElementById('lunrJsTip').innerHTML !== "") ? "" : "for Joker; See also: `+Foo Bar -Baz` FOR *<br/>contains Foo Must, Bar Maybe, Baz Not";
})

if (!navigator.onLine) {
    console.log('offline'); 
}

const registerServiceWorker = async () => {
    if ('serviceWorker' in navigator) {
        try {
            let serviceWorker = await navigator.serviceWorker.register('sw.js')
            if (serviceWorker.installing) {
                console.log("Service worker installing");
            } else if (serviceWorker.waiting) {
                console.log("Service worker installed");
            } else if (serviceWorker.active) {
                console.log("Service worker active");
            }
            console.log(`Service worker registered ${serviceWorker}`)
        } catch (err) {
            console.error(`Failed to register service worker: ${err}`)
        }
    }
}

registerServiceWorker();
