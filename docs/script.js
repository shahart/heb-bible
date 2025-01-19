import { RepoInit } from "./RepoInit.js";
import Repo from "./Repo.js";
import { Pasuk } from "./Pasuk.js";
import { Dilug } from "./Dilug.js";
import { Read } from "./Read.js";
import { Gematria } from "./Gematria.js";
import { Find } from "./Find.js";

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
    document.getElementById("chars").textContent = s.length + ' '; 
    dilug.dilug();
}

if (g && g.trim() !== '') {
    document.getElementById("gim").value = g;
    gematria.gematria();
    document.getElementById("tab4").checked = true;
}

if (q && q.trim() !== '') {
    document.getElementById("find").value = q;
    document.getElementById("tab5").checked = true;
    find.find();
}


