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

let r = params.get("r");
const p = params.get("p");
const s = params.get("s"); // (s)kip
const g = params.get("g");
const q = params.get("q"); // (q)uery
const l = params.get("l");
const b = params.get("b"); // (b)ook

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

const date = (new Date).toLocaleString('he',{calendar:"hebrew"})

if (date.startsWith("1 ") || date.startsWith("29 ") || date.startsWith("30 ")) {
    document.getElementById('yaale').innerHTML = "בראש חודש(?): אֱלהֵינוּ וֵאלהֵי אֲבוֹתֵינוּ. יַעֲלֶה וְיָבא, יַגִּיעַ, יֵרָאֶה, יֵרָצֶה, יִשָּׁמַע, יִפָּקֵד, יִזָּכֵר לְפָנֶיךָ זִכְרוֹנֵנוּ, זִכְרוֹן אֲבוֹתֵינוּ, זִכְרוֹן יְרוּשָׁלַיִם עִירָךְ, זִכְרוֹן מָשִׁיחַ בֶּן דָּוִד עַבְדָּךְ, זִכְרוֹן כָּל עַמְּךָ בֵית יִשְׂרָאֵל לְפָנֶיךָ. לִפְלֵיטָה, לְטוֹבָה, לִבְרָכָה, לְחֵן, לְחֶסֶד וּלְרַחֲמִים. בְּיוֹם ראשׁ הַחֹדֶשׁ הַזֶּה. לְרַחֵם בּוֹ עָלֵינוּ וּלְהוֹשִׁיעֵנוּ. זָכְרֵנוּ יי אֱלֹהֵינוּ בּוֹ לְטוֹבָה, פָּקְדֵנוּ בוֹ לִבְרָכָה, הוֹשִׁיעֵנוּ בוֹ לְחַיִּים, בִּדְבַר יְשׁוּעָה וְרַחֲמִים. חוּס וְחָנֵּנוּ וְרַחֵם עָלֵינוּ, וּמַלְּטֵנוּ בוֹ מִכּל צָרָה וְיָגוֹן, וְשַׂמְּחֵנוּ בוֹ שִׂמְחָה שְׁלֵמָה, כִּי אֵל מֶלֶךְ רַחוּם וְחַנּוּן אָתָּה:"
    document.getElementById('yaale2').innerHTML = "בראש חודש(?): וזכרנו לטובה ביום ראש חודש הזה:"
}

if (b && b.trim() !== '') {
    if (b.startsWith('Joshua')) {
        r = '6';
    }
    else if (b.startsWith('Judges')) {
        r = '7';
    }
    else if (b.startsWith('I Samuel')) {
        r = '8';
    }
    else if (b.startsWith('II Samuel')) {
        r = '9';
    }
    else if (b.startsWith('I Kings')) {
        r = '10';
    }
    else if (b.startsWith('II Kings')) {
        r = '11';
    }
    else if (b.startsWith('Isaiah')) {
        r = '12';
    }    
    else if (b.startsWith('Jeremiah')) {
        r = '13';
    }
    else if (b.startsWith('Ezekiel')) {
        r = '14';
    }
    else if (b.startsWith('Hosea')) {
        r = '15';
    }
    else if (b.startsWith('Joel')) {
        r = '16';
    }
    else if (b.startsWith('Amos')) {
        r = '17';
    }
    else if (b.startsWith('Obadiah')) {
        r = '18';
    }
    else if (b.startsWith('Jonah')) {
        r = '19';
    }
    else if (b.startsWith('Micah')) {
        r = '20';
    }
    else if (b.startsWith('Nachum')) {
        r = '21';
    }
    else if (b.startsWith('Habakkuk')) {
        r = '22';
    }
    else if (b.startsWith('Zephaniah')) {
        r = '23';
    }
    else if (b.startsWith('Haggai')) {
        r = '24';
    }
    else if (b.startsWith('Zechariah')) {
        r = '25';
    } 
    else if (b.startsWith('Malachi')) {
        r = '26';
    } 
    r += "," + b.split(" ")[1] // .split(":")[0];
}

if (r && r.trim() !== '') {
    try {
      let book = r;
      let prk = undefined;
      if (r.indexOf(",")>0) {
        book=r.split(",")[0];
        prk = r;
      }
      document.getElementById("BookSelect").value = book;
      // read.read();
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

function handleChapter(c) {
    document.getElementById('psalms'+c)    .innerHTML = (document.getElementById('psalms'+c)    .innerHTML !== "") ? "" : read.read("27,"+c, true);
    document.getElementById('psalms'+c+'B').innerHTML = (document.getElementById('psalms'+c+'B').innerHTML == "+") ? "-" : "+";
}

document.getElementById('psalms1B').addEventListener('click', () => {
    handleChapter(1);
})

document.getElementById('psalms6B').addEventListener('click', () => {
    handleChapter(6);
})

document.getElementById('psalms13B').addEventListener('click', () => {
    handleChapter(13);
})

document.getElementById('psalms20B').addEventListener('click', () => {
    handleChapter(20);
})

document.getElementById('psalms38B').addEventListener('click', () => {
    handleChapter(38);
})

document.getElementById('psalms83B').addEventListener('click', () => {
    handleChapter(83);
})

document.getElementById('psalms85B').addEventListener('click', () => {
    handleChapter(85);
})

document.getElementById('psalms91B').addEventListener('click', () => {
    handleChapter(91);
})

document.getElementById('psalms102B').addEventListener('click', () => {
    handleChapter(102);
})

document.getElementById('psalms106B').addEventListener('click', () => {
    handleChapter(106);
})

document.getElementById('psalms130B').addEventListener('click', () => {
    handleChapter(130);
})

document.getElementById('psalms142B').addEventListener('click', () => {
    handleChapter(142);
})

document.getElementById('psalms22B').addEventListener('click', () => {
    handleChapter(22);
})

document.getElementById('psalms25B').addEventListener('click', () => {
    handleChapter(25);
})

document.getElementById('psalms121B').addEventListener('click', () => {
    handleChapter(121);
})

document.getElementById('psalms86B').addEventListener('click', () => {
    handleChapter(86);
})

document.getElementById('MiSheb1B').addEventListener('click', () => {
    document.getElementById('MiSheb1').innerHTML = (document.getElementById('MiSheb1').innerHTML !== "") ? "" : "מִי שֶׁבֵּרַךְ אֲבוֹתֵינוּ אַבְרָהָם יִצְחָק וְיַעֲקֹב הוּא יְבָרֵךְ אֶת חַיָּלֵי צְבָא הֲגַנָּה לְיִשְׂרָאֵל, הָעוֹמְדִים עַל מִשְׁמַר אַרְצֵנוּ וְעָרֵי אֱלהֵינוּ מִגְּבוּל הַלְּבָנוֹן וְעַד מִדְבַּר מִצְרַיִם וּמִן הַיָּם הַגָּדוֹל עַד לְבוֹא הָעֲרָבָה בַּיַּבָּשָׁה בָּאֲוִיר וּבַיָּם. יִתֵּן ה' אֶת אוֹיְבֵינוּ הַקָּמִים עָלֵינוּ נִגָּפִים לִפְנֵיהֶם. הַקָּדוֹשׁ בָּרוּךְ הוּא יִשְׁמֹר וְיַצִּיל אֶת חַיָלֵינוּ מִכָּל צָרָה וְצוּקָה וּמִכָּל נֶגַע וּמַחְלָה וְיִשְׁלַח בְּרָכָה וְהַצְלָחָה בְּכָל מַעֲשֵׂה יְדֵיהֶם. יַדְבֵּר שׂוֹנְאֵינוּ תַּחְתֵּיהֶם וִיעַטְרֵם בְּכֶתֶר יְשׁוּעָה וּבְעֲטֶרֶת נִצָּחוֹן. וִיקֻיַּם בָּהֶם הַכָּתוּב: כִּי ה' אֱלֹהֵיכֶם הַהֹלֵךְ עִמָּכֶם לְהִלָּחֵם לָכֶם עִם איבֵיכֶם לְהוֹשִׁיעַ אֶתְכֶם: וְנאמַר אָמֵן";
})

document.getElementById('MiSheb2B').addEventListener('click', () => {
    document.getElementById('MiSheb2').innerHTML = (document.getElementById('MiSheb2').innerHTML !== "") ? "" : "אָבִינוּ שֶׁבַּשָּׁמַיִם, צוּר יִשְׂרָאֵל וְגוֹאֲלוֹ, בָּרֵךְ אֶת מְדִינַת יִשְׂרָאֵל, רֵאשִׁית צְמִיחַת גְּאֻלָּתֵנוּ. הָגֵן עָלֶיהָ בְּאֶבְרַת חַסְדֶּךָ, וּפְרֹשׁ עָלֶיהָ סֻכַּת שְׁלוֹמֶךָ, וּשְׁלַח אוֹרְךָ וַאֲמִתְּךָ לְרָאשֶׁיהָ, שָׂרֶיהָ וְיוֹעֲצֶיהָ, וְתַקְּנֵם בְּעֵצָה טוֹבָה מִלְּפָנֶיךָ. חַזֵּק אֶת יְדֵי מְגִנֵּי אֶרֶץ קָדְשֵׁנוּ, וְהַנְחִילֵם אֱלֹהֵינוּ יְשׁוּעָה וַעֲטֶרֶת נִצָּחוֹן תְּעַטְּרֵם, וְנָתַתָּ שָׁלוֹם בָּאָרֶץ וְשִׂמְחַת עוֹלָם לְיוֹשְׁבֶיהָ. וְאֶת אַחֵינוּ כָּל בֵּית יִשְׂרָאֵל פְּקָד-נָא בְּכָל אַרְצוֹת פְּזוּרֵיהֶם, וְתוֹלִיכֵם מְהֵרָה קוֹמְמִיּוּת לְצִיּוֹן עִירֶךָ וְלִירוּשָׁלַיִם מִשְׁכַּן שְׁמֶךָ, כַּכָּתוּב בְּתוֹרַת משֶׁה עַבְדֶּךָ: אִם יִהְיֶה נִדַּחֲךָ בִּקְצֵה הַשָּׁמַיִם, מִשָּׁם יְקַבֶּצְךָ ה' אֱלֹהֶיךָ וּמִשָּׁם יִקָּחֶךָ. וֶהֱבִיאֲךָ ה' אֱלֹהֶיךָ אֶל הָאָרֶץ אֲשֶׁר יָרְשׁוּ אֲבֹתֶיךָ וִירִשְׁתָּהּ, וְהֵיטִבְךָ וְהִרְבְּךָ מֵאֲבֹתֶיךָ. (וּמָל יְיָ אֱלֹהֶיךָ אֶת לְבָבְךָ וְאֶת לְבַב זַרְעֶךָ, לְאַהֲבָה אֶת יְיָ אֱלֹהֶיךָ בְּכׇל לְבָבְךָ וּבְכׇל נַפְשְׁךָ, לְמַעַן חַיֶּיךָ.) (דברים ל, ד-ו). וְיַחֵד לְבָבֵנוּ לְאַהֲבָה וּלְיִרְאָה אֶת שְׁמֶךָ, וְלִשְׁמֹר אֶת כָּל דִּבְרֵי תּוֹרָתֶךָ. וּשְׁלַח לָנוּ מְהֵרָה בֶּן דָּוִד מְשִׁיחַ צִדְקֶךָ, לִפְדּות מְחַכֵּי קֵץ יְשׁוּעָתֶךָ. הוֹפַע בַּהֲדַר גְּאוֹן עֻזֶּךָ עַל כָּל יוֹשְׁבֵי תֵּבֵל אַרְצֶךָ, וְיֹאמַר כֹּל אֲשֶׁר נְשָׁמָה בְּאַפּוֹ: ה' אֱלֹהֵי יִשְׂרָאֵל מֶלֶךְ, וּמַלְכוּתו בַּכּל מָשָׁלָה. אָמֵן סֶלָה";
})

document.getElementById('MiSheb3B').addEventListener('click', () => {
    document.getElementById('MiSheb3').innerHTML = (document.getElementById('MiSheb3').innerHTML !== "") ? "" : "אַחֵינוּ כָּל בֵּית יִשְׂרָאֵל הַנְּתוּנִים בַּצָּרָה וּבַשִּׁבְיָה הָעוֹמְדִים בֵּין בַּיָּם וּבֵין בַּיַּבָּשָׁה הַמָּקוֹם יְרַחֵם עֲלֵיהֶם וְיוֹצִיאֵם מִצָּרָה לִרְוָחָה וּמֵאֲפֵלָה לְאוֹרָה וּמִשִּׁעְבּוּד לִגְאֻלָּה הָשָׁתָא בַּעֲגָלָא וּבִזְמַן קָרִיב וְנֹאמַר אָמֵן ";
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
