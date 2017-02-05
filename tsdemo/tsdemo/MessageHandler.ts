"use strict";

export class MessageHandler {
    messages = <HTMLTableSectionElement>document.querySelector('tbody');
    clearMessages(): void {
        this.messages.innerHTML = '';
    }
    addMessage(start: Date, service: string, message: string): void {
        let row: HTMLTableRowElement = document.createElement('tr');
        let startDateValue = '';
        if (start) {
            startDateValue = start.toLocaleTimeString()
        }
        row.innerHTML = `<td>${startDateValue}</td><td>${new Date().toLocaleTimeString()}</td><td>${service}</td><td>${message}</td>`;
        this.messages.appendChild(row);
    }

}
