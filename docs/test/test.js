import { RepoInit } from "../RepoInit.js";
import Repo from "../Repo.js";

describe('Mocha tests', function () {

    it('repo size', function () {
        console.log(new Date());
        new RepoInit();
        setTimeout(function() {
            console.log(new Date());
            chai.assert.equal('23204', Repo.getSize()); // TODO work also without dev tools > console
        }, 2000);
    });

});
