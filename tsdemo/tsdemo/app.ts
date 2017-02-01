///<reference path='file1.ts'/>

class Greeter {
    element: HTMLElement;
    span: HTMLElement;
    timerToken: number;

    constructor(element: HTMLElement, user: Student) {
        this.element = element;
        let p = document.createElement('p');
        p.innerText = 'Hi ' + user.fullName;
        this.element.appendChild( p);
        let ti = document.createElement('span');
        this.element.appendChild(ti);
        ti.innerText = 'The time is ';
        
        this.span = document.createElement('span');
        this.element.appendChild(this.span);
    }

    start() {
        this.timerToken = setInterval(() => this.span.innerHTML = new Date().toUTCString(), 500);
    }

    stop() {
        clearTimeout(this.timerToken);
    }

}
class Student {
    fullName: string;
    constructor(public firstName, public middleInitial, public lastName) {
        this.fullName = firstName + " " + middleInitial + " " + lastName;
    }
}

interface Person {
    firstName: string;
    lastName: string;
}


window.onload = () => {
    var el = document.getElementById('content');
    var remco = new Student('R' + new Mankala.RequestHandler().process(1).orderId, 'David', 'Hoetmer');
    var greeter = new Greeter(el, remco);
    greeter.start();
    ;
 };