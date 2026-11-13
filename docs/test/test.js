import { RepoInit } from "../RepoInit.js";
import Repo from "../Repo.js";
import { Read } from "../Read.js";
import { No2gim } from "../No2gim.js";
import { Pasuk } from "../Pasuk.js";
import { createAppUrl } from "../AppConfig.js";
import { parseExternalBookReference } from "../BookReferences.js";
describe('Mocha tests', function () {
    let repoInit;
    it('repo size', async function () {
        console.log(new Date());
        repoInit = new RepoInit();
        await repoInit.ready;
        console.log(new Date());
        chai.assert.equal('23204', Repo.getSize()); // TODO work also without dev tools > console
    });
    it('decompresses the local gzip format without a third-party library', async function () {
        chai.assert.isFunction(CompressionStream, 'CompressionStream should be available in the test browser');
        const original = 'תנ״ך עובד גם בלי חיבור לרשת';
        const compressed = await new Response(new Blob([original]).stream().pipeThrough(new CompressionStream('gzip'))).arrayBuffer();
        const decompressed = await repoInit.ungzip(new Uint8Array(compressed));
        chai.assert.equal(new TextDecoder().decode(decompressed), original);
    });
    it('uses local substring search when Lunr is unavailable', function () {
        chai.assert.isUndefined(window.lunr, 'the offline test should not load Lunr');
        let result = document.getElementById('resultFind');
        if (!result) {
            result = document.createElement('div');
            result.id = 'resultFind';
            document.body.appendChild(result);
        }
        Repo.lucene('בראשית ברא');
        chai.assert.include(result.textContent, 'ממצאים');
        chai.assert.match(result.textContent, /בראשית ברא/);
    });
    it('serves local Bible files from the Pages folder', async function () {
        const [plain, niqqud] = await Promise.all([
            fetch('../bible.txt.gz'),
            fetch('../bible-niqqud.txt')
        ]);
        chai.assert.isTrue(plain.ok, 'compressed Bible file should be deployed locally');
        chai.assert.isTrue(niqqud.ok, 'niqqud Bible file should be deployed locally');
        chai.assert.isAbove(Number(plain.headers.get('content-length') || 1), 1);
        chai.assert.isAbove(Number(niqqud.headers.get('content-length') || 1), 1);
    });
    it('precaches the Bible data and app shell for offline deep links', async function () {
        chai.assert.isTrue('serviceWorker' in navigator, 'service workers should be available');
        const registration = await navigator.serviceWorker.register('../sw.js');
        await navigator.serviceWorker.ready;
        chai.assert.isNotNull(registration.active, 'service worker should activate');
        const cache = await caches.open('heb-bible-v38');
        const appRootWithQuery = new URL('../?r=27,1', location.href);
        const [home, compressed, niqqud] = await Promise.all([
            cache.match(appRootWithQuery, { ignoreSearch: true }),
            cache.match(new URL('../bible.txt.gz', location.href)),
            cache.match(new URL('../bible-niqqud.txt', location.href))
        ]);
        chai.assert.isNotNull(home, 'cached app shell should match a query-string deep link');
        chai.assert.isNotNull(compressed, 'gzip dataset should be precached');
        chai.assert.isNotNull(niqqud, 'niqqud dataset should be precached');
    });
    it('read-pasuk', function () {
        let read = new Read(Repo);
        let psk = read.read("27,119,12-1", true);
        chai.assert.equal(psk, 'בָּר֖וּךְ אַתָּ֥ה יְהוָ֗ה לַמְּדֵ֥נִי חֻקֶּֽיךָ: ');
    });
    it('read-random-psalm', function () {
        let read = new Read(Repo);
        const originalRandom = Math.random;
        const chapters = [];
        read.read = chapter => chapters.push(chapter);
        try {
            Math.random = () => 0;
            read.readRandomPsalm();
            Math.random = () => 0.999999;
            read.readRandomPsalm();
            chai.assert.deepEqual(chapters, ['27,1', '27,150']);
        }
        finally {
            Math.random = originalRandom;
        }
    });
    it('count-pasuk-starts-ends', function () {
        let pasuk = new Pasuk(Repo);
        let psk = pasuk.searchPasuk("שחר", true);
        chai.assert.equal(psk, 74);
        psk = pasuk.searchPasuk("שחר", false);
        chai.assert.equal(psk, 25);
    });
    it('No2gim - single digit', function () {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(7), 'ז');
    });
    it('No2gim - double digits', function () {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(42), 'מב');
    });
    it('No2gim - triple digits', function () {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(123), 'קכג');
    });
    it('No2gim - special case 15', function () {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(15), 'טו');
    });
    it('No2gim - special case 16', function () {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(16), 'טז');
    });
    it('No2gim - large number', function () {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(5782), 'ה\'תשפב');
    });
    it('creates encoded share URLs', function () {
        chai.assert.equal(createAppUrl({ q: 'אמת ושלום', from: 2 }), 'https://shahart.github.io/heb-bible/?q=%D7%90%D7%9E%D7%AA+%D7%95%D7%A9%D7%9C%D7%95%D7%9D&from=2');
    });
    it('parses external book references', function () {
        chai.assert.equal(parseExternalBookReference('II Kings 12:4'), '11,12');
        chai.assert.equal(parseExternalBookReference('Micah 3'), '20,3');
        chai.assert.isNull(parseExternalBookReference('Unknown 3'));
    });
});
