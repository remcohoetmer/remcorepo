"use strict";
import {MessageHandler} from "./MessageHandler"
import {PurchaseResponse, RequestHandler} from "./RequestHandler"

class Starter {
    messageHandler = new MessageHandler();
    spinner = <HTMLDivElement>document.querySelector('.spinner');
    public start(requestId: number) {
        this.spinner.style.display = 'block';
        this.messageHandler.clearMessages();

        new RequestHandler().process(requestId).then(
            purchaseResponse => {
                this.messageHandler.addMessage(undefined, 'result', purchaseResponse.toString());
                this.spinner.style.display = 'none';
            })
            .catch(err => {
                this.messageHandler.addMessage(undefined, 'error', err);
                this.spinner.style.display = 'none';
            });
    }
    public startFromButton(event: MouseEvent) {
        let input = <HTMLInputElement>document.getElementById("requestID");
        starter.start(parseInt(input.value));// TODO: investigate why "this" cannot be used
    }
    public initialise() {
        let startButton = <HTMLButtonElement>document.getElementById("startButton");
        startButton.onclick = this.startFromButton;
    }
}
let starter = new Starter();
starter.initialise();
starter.start(123);

/*
 {
	"purchaseRequestId": 123,
	"locationId": 20,
    "customerId": 10
}
 {
	"purchaseRequestId": 12, --> Transaction Validation Error
 	"locationId": 20,
    "customerId": 10 
}
 {
	"purchaseRequestId": 11,
	"locationId": 11,
    "customerId": 11 --> Customer Validation Error
}
*/
