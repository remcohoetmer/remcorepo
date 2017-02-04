
import {Remco} from "./Remco"
import {PurchaseResponse, RequestHandler} from "./ProcessHandler"
var el = document.getElementById('content');
//let result = new Remco();
let result: PurchaseResponse = new RequestHandler().process(1);
el.innerHTML = "Result = " + result.getAsString(); //toString()) werkt niet
//el.innerHTML = "Result = " ; 
