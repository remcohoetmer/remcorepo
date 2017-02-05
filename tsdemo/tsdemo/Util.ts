"use strict";
var storyDiv = document.querySelector('.story');

var messages: HTMLTableSectionElement = <HTMLTableSectionElement>document.querySelector('tbody');


export class HTMLBuilder {
    addHtmlToPage(content: string): void {
        var div: HTMLDivElement = document.createElement('div');
        div.innerHTML = content;
        storyDiv.appendChild(div);
    }

    addTextToPage(content: string): void {
        var p: HTMLParagraphElement = document.createElement('p');
        p.textContent = content;
        storyDiv.appendChild(p);
    }

    addMessageRow(start: Date, end: Date, message: string): void {
        var row: HTMLTableRowElement = document.createElement('tr');
        row.innerHTML = `<td>${start.toLocaleTimeString()}</td><td>${end.toLocaleTimeString()}</td><td>${message}</td>`;
        messages.appendChild(row);
    }

}
