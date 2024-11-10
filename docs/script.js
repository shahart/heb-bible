import { RepoInit } from "./repoInit.js";
import Repo from "./repo.js";
import { Pasuk } from "./pasuk.js";
import { Dilug } from "./dilug.js";

new RepoInit();

let pasuk = new Pasuk(Repo);
let dilug = new Dilug(Repo);

const params = new URLSearchParams(document.location.search);
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
    document.getElementById("result").innerHTML = "Total Psukim: " + totalPsukim;
}

function noName(orig) {
    return orig.replaceAll("יהוה", "ה'");
}

document.getElementById('button1').addEventListener('click', () => {
    pasuk.pasuk(); // todo one time Init
});

if (firstName && firstName !== '') {
    pasuk.pasuk();
}

document.getElementById('buttonD').addEventListener('click', () => {
    dilug.dilug();
});
