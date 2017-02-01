///<reference path='file1.ts'/>
var Greeter = (function () {
    function Greeter(element, user) {
        this.element = element;
        var p = document.createElement('p');
        p.innerText = 'Hi ' + user.fullName;
        this.element.appendChild(p);
        var ti = document.createElement('span');
        this.element.appendChild(ti);
        ti.innerText = 'The time is ';
        this.span = document.createElement('span');
        this.element.appendChild(this.span);
    }
    Greeter.prototype.start = function () {
        var _this = this;
        this.timerToken = setInterval(function () { return _this.span.innerHTML = new Date().toUTCString(); }, 500);
    };
    Greeter.prototype.stop = function () {
        clearTimeout(this.timerToken);
    };
    return Greeter;
}());
var Student = (function () {
    function Student(firstName, middleInitial, lastName) {
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.fullName = firstName + " " + middleInitial + " " + lastName;
    }
    return Student;
}());
window.onload = function () {
    var el = document.getElementById('content');
    var remco = new Student('R' + new Mankala.RequestHandler().process(1).orderId, 'David', 'Hoetmer');
    var greeter = new Greeter(el, remco);
    greeter.start();
    ;
};
//# sourceMappingURL=app.js.map