import { RepoInit } from "../RepoInit.js";
import Repo from "../Repo.js";
import { Read } from "../Read.js";

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
        chai.assert.equal(psk, 'ברוך אתה יהוה למדני חקיך: ');
    });

});
