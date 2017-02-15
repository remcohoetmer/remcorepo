﻿"use strict";
import {MessageHandler} from "./MessageHandler"
import {PurchaseResponse, RequestHandler} from "./RequestHandler"

class Starter {
    
    spinner = <HTMLDivElement>document.querySelector('.spinner');

    constructor() {
        document.getElementById("startButton").onclick = this.startFromButton.bind(this);
    }
    public start(requestId: number) {
        if (Number.isNaN(requestId)) {
            return;
        }
        this.spinner.style.display = 'block';
        messageHandler.clear();

        new RequestHandler().process(requestId).then(
            purchaseResponse => {
                messageHandler.addMessage(undefined, 'result', purchaseResponse.toString());
                this.spinner.style.display = 'none';
            })
            .catch(err => {
                messageHandler.addMessage(undefined, 'error', err);
                this.spinner.style.display = 'none';
            });
    }
    public startFromButton(event: MouseEvent) {
        let input = <HTMLInputElement>document.getElementById("requestID");
        this.start(parseInt(input.value));
    }
}
let starter = new Starter();
let messageHandler = new MessageHandler();
messageHandler.drawLines();
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
    "customerId": 12
}
 {
	"purchaseRequestId": 11,
	"locationId": 11,
    "customerId": 11 --> Customer Validation Error
}
 {
	"purchaseRequestId": 13,
	"locationId": 13,
    "customerId": 13 --> Linking failed
}
*/


