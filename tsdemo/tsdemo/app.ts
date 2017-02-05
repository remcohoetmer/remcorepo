"use strict";
import {HTMLBuilder} from "./Util"
import {PurchaseResponse, RequestHandler} from "./ProcessHandler"

var fakeSlowNetwork: number;

function wait(ms) {
    return new Promise(function (resolve) {
        setTimeout(resolve, ms);
    });
}

function get(url) {
    // Return a new promise.
    // We do all the work within the constructor callback.
    var fakeNetworkWait = wait(3000 * Math.random() * fakeSlowNetwork);

    var requestPromise = new Promise(function (resolve, reject) {
        // Do the usual XHR stuff
        var req = new XMLHttpRequest();
        req.open('get', url);

        req.onload = function () {
            // 'load' triggers for 404s etc
            // so check the status
            if (req.status == 200) {
                // Resolve the promise with the response text
                resolve(req.response);
            }
            else {
                // Otherwise reject with the status text
                reject(Error(req.statusText));
            }
        };

        // Handle network errors
        req.onerror = function () {
            reject(Error("Network Error"));
        };

        // Make the request
        req.send();
    });

    return Promise.all([fakeNetworkWait, requestPromise]).then(function (results) {
        return results[1];
    });
}
function getJson(url) {
    return get(url).then(JSON.parse);
}

(function () {
    var lsKey = 'fake-slow-network';
    var networkFakeDiv = <HTMLDivElement>document.querySelector('.network-fake');
    var checkbox = <HTMLInputElement>networkFakeDiv.querySelector('input');

    fakeSlowNetwork = Number(localStorage.getItem(lsKey)) || 0;

    networkFakeDiv.style.display = 'block';
    checkbox.checked = !!fakeSlowNetwork;

    checkbox.addEventListener('change', function () {
        localStorage.setItem(lsKey, Number(checkbox.checked).toString());
        // location.reload();
    });
} ());




function spawn(generatorFunc) {
    function continuer(verb, arg) {
        var result;
        try {
            result = generator[verb](arg);
        } catch (err) {
            return Promise.reject(err);
        }
        if (result.done) {
            console.log(result.done);
            console.log(result.value);
            return result.value;
        } else {

            console.log(result.done);
            console.log(result.value);
            return Promise.resolve(result.value).then(callback, errback);
        }
    }
    var generator = generatorFunc();
    var callback = continuer.bind(continuer, "next");
    var errback = continuer.bind(continuer, "throw");
    callback();
}

var builder = new HTMLBuilder();


function startFlow() {
    return spawn(function* () {
        //  try {
        let story = yield getJson('data/story.json');

        var startTime = new Date();
        let chapterPromises = story.chapterUrls.map(getJson);

        // Can't use chapterPromises.forEach, because yielding within doesn't work
        for (let i = 0, chapterPromise; chapterPromise = chapterPromises[i]; i++) {
            // Wait for each chapter to be ready, then add it to the page
            let chapter = yield chapterPromise;

            builder.addMessageRow(startTime, new Date(), chapter.chapter.toString(), chapter.html.substring(0, 30));
        }

        // }
        //   catch (err) {
        // try/catch just works, rejected promises are thrown here
        //     builder.addTextToPage("Argh, broken: " + err.message);
        //}
        (<HTMLDivElement>document.querySelector('.spinner')).style.display = 'none';
    });
}
startFlow()


var startTime: Date;// TODO: workaround

class Starter {
    startTime: Date;// TODO : investigate why private member disappears

    finish(response: PurchaseResponse): void {
        new HTMLBuilder().addMessageRow(startTime, new Date(), 'service', response.toString());
    }
    public start(requestId: number) {
        let el = document.getElementById('tbody');
        el.innerHTML = "";
        startTime = new Date();
        new RequestHandler().process(requestId, this.finish);
    }
    public startFromButton(event: MouseEvent) {
        let input = <HTMLInputElement>document.getElementById("requestID");
        starter.start(parseInt(input.value));
    }
}

let startButton = <HTMLButtonElement>document.getElementById("startButton");
let starter = new Starter();

startButton.onclick = starter.startFromButton;
starter.start(123);


