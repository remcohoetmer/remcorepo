"use strict";
var storyDiv = document.querySelector('.story');

var messages: HTMLTableSectionElement = <HTMLTableSectionElement>document.querySelector('tbody');

export class HTMLBuilder {
    addTextToPage(content: string): void {
        let p = <HTMLSpanElement>document.createElement('span');
        p.textContent = content;
        storyDiv.appendChild(p);
    }

    addMessageRow(start: Date, end: Date, service: string, message: string): void {
        let row: HTMLTableRowElement = document.createElement('tr');
        row.innerHTML = `<td>${start.toLocaleTimeString()}</td><td>${end.toLocaleTimeString()}</td><td>${service}</td><td>${message}</td>`;
        messages.appendChild(row);
    }

}
