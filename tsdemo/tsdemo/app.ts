"use strict";
import {MessageHandler} from "./MessageHandler"
import {PurchaseResponse, RequestHandler} from "./RequestHandler"

class Starter {
    messageHandler = new MessageHandler();
    spinner = <HTMLDivElement>document.querySelector('.spinner');

    constructor() {
//        this.start = this.start.bind(this);
        this.startFromButton = this.startFromButton.bind(this);
        document.getElementById("startButton").onclick = this.startFromButton;
    }
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
        this.start(parseInt(input.value));
    }
}
let starter = new Starter();
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
