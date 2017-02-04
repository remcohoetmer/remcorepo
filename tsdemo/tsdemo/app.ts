///<reference path='ProcessHandler.ts'/>




var el = document.getElementById('content');
let result: ProcessHandler.PurchaseResponse = new ProcessHandler.RequestHandler().process(1);
el.innerHTML = "Result = " + result.getAsString(); //toString()) werkt niet
