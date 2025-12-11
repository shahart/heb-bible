import { RepoInit } from "../RepoInit.js";
import Repo from "../Repo.js";
import { Read } from "../Read.js";
import { No2gim } from "../No2gim.js";
import { Pasuk } from "../Pasuk.js";

describe('Mocha tests', function () {

    it('repo size', function () {
        console.log(new Date());
        new RepoInit();
        // chai.assert.equal('23204', Repo.getSize());
        setTimeout(function() {
            console.log(new Date());
            chai.assert.equal('23204', Repo.getSize()); // TODO work also without dev tools > console
        }, 10);
    });

    it('read-pasuk', function() {
        let read = new Read(Repo);
        let psk = read.read("27,119,12-1", true);
        chai.assert.equal(psk, 'בָּר֖וּךְ אַתָּ֥ה יְהוָ֗ה לַמְּדֵ֥נִי חֻקֶּֽיךָ: ');
    });

    it('count-pasuk-starts-ends', function() {
        let pasuk = new Pasuk(Repo);
        let psk = pasuk.searchPasuk("שחר", true);
        chai.assert.equal(psk, 74);
        psk = pasuk.searchPasuk("שחר", false);
        chai.assert.equal(psk, 25);
    });

    it('No2gim - single digit', function() {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(7), 'ז');
    });

    it('No2gim - double digits', function() {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(42), 'מב');
    });

    it('No2gim - triple digits', function() {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(123), 'קכג');
    });

    it('No2gim - special case 15', function() {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(15), 'טו');
    });

    it('No2gim - special case 16', function() {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(16), 'טז');
    });

    it('No2gim - large number', function() {
        let n2g = new No2gim();
        chai.assert.equal(n2g.no2gim(5782), 'ה\'תשפב');
    });

});
