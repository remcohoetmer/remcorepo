"use strict";

export class MessageHandler {
    messages = <HTMLTableSectionElement>document.querySelector('tbody');
    canvas = <HTMLCanvasElement>document.getElementById("taskCanvas");

    clear(): void {
        this.messages.innerHTML = '';

        var ctx = this.canvas.getContext("2d");
        ctx.font = "13px Verdana";

        for (var taskid in this.tasks) {
            this.setTaskClass(taskid, 'inInitial');
        }

    }
    addMessage(start: Date, taskId: string, message: string): void {
        let row: HTMLTableRowElement = document.createElement('tr');
        let startDateValue = '';
        if (start) {
            startDateValue = start.toLocaleTimeString()
        }
        let taskName;
        try {
            taskName = this.tasks[taskId].name;
        } catch (e) {
            taskName = taskId;
        }
        row.innerHTML = `   <td>${new Date().toLocaleTimeString()}</td>
                            <td>${taskName}</td>
                            <td>${message}</td>`;
        this.messages.appendChild(row);
    }
    tasks = {
        "retpur": {
            name: "Retrieve Purchase Request",
            x: 160,
            y: 13,
        },
        "retcus": {
            name: "Retrieve Customer Data",
            x: 230,
            y: 100,
        },
        "retloc": {
            name: "Retrieve Location Config",
            x: 424,
            y: 100,
        },
        "valcus": {
            name: "Validate Customer",
            x: 359,
            y: 180,
        },
        "valtra": {
            name: "Validate Transaction",
            x: 19,
            y: 180,
        },
        "exeord": {
            name: "Execute Order ",
            x: 180,
            y: 265,
        },
        "updpur": {
            name: "Update Purchase Request with Order",
            x: 165,
            y: 340,
        },
        "linord": {
            name: "Link Order to Transaction",
            x: 160,
            y: 410,
        },
        "snderr": {
            name: "Send Error Message ",
            x: 400,
            y: 410,
        },
        "http": {
            name: "HTTP Final Response",
            x: 165,
            y: 475,
        }

    };
    lines = [{ "x": 203, "y": 44, "rotation": 0, "id": 9, "width": 100, "height": 56 }, { "x": 232, "y": 44, "rotation": 0, "id": 10, "width": 247, "height": 56 }, { "x": 190, "y": 44, "rotation": 0, "id": 11, "width": -100, "height": 138 }, { "x": 84, "y": 214, "rotation": 0, "id": 12, "width": 125, "height": 50 }, { "x": 385.5, "y": 214, "rotation": 0, "id": 13, "width": -160, "height": 50 }, { "x": 231, "y": 296, "rotation": 0, "id": 14, "width": 0, "height": 44 }, { "x": 231, "y": 376, "rotation": 0, "id": 15, "width": 0, "height": 33 }, { "x": 231, "y": 444, "rotation": 0, "id": 22, "width": 0, "height": 30 }, { "x": 350, "y": 429, "rotation": 0, "id": 16, "width": 44, "height": 0 }, { "x": 306, "y": 136, "rotation": 0, "id": 17, "width": 58, "height": 44 }, { "x": 280, "y": 136, "rotation": 0, "id": 20, "width": -170, "height": 44 }, { "x": 480, "y": 136, "rotation": 0, "id": 18, "width": -53, "height": 44 },
        { "x": 470, "y": 136, "rotation": 0, "id": 21, "width": -340, "height": 44 }, { "x": 1000, "y": 1000, "rotation": 0, "id": 99, "width": 100, "height": 100 }];
    getColorForState(state: string): string {
        switch (state) {
            case 'inError': return "#FF8888";
            case 'inInitial': return '#EEEEEE';
            case 'inExecution': return '#ffd800';
            case 'inFinished': return 'LightSeaGreen';
            default: return 'blue';
        }
    }
    initTask(taskId: string): void {
        this.setTaskClass(taskId, "inInitial");
    }
    startTask(taskId: string): void {
        this.setTaskClass(taskId, "inExecution");
    }
    finishTask(taskId: string, message: any): void {
        this.setTaskClass(taskId, "inFinished");
        this.addMessage(null, taskId, message);
    }
    errorTask(taskId: string, message: any): void {
        this.setTaskClass(taskId, "inError");
    }
    createRectangle(ctx: CanvasRenderingContext2D,
        x: number, y: number, width: number, height: number, color: string): void {
        ctx.fillStyle = color;
        ctx.fillRect(x, y, width, height);

        ctx.lineWidth = 1;
        ctx.rect(x, y, width, height);
        ctx.stroke();

    }

    setTaskClass(taskId, state): void {
        let task = this.tasks[taskId];
        let ctx = this.canvas.getContext("2d");
        ctx.strokeStyle = "#888";

        this.createRectangle(ctx, task.x - 8, task.y, task.name.length * 8, 35, this.getColorForState(state));

        ctx.fillStyle = 'black';
        ctx.fillText(task.name, task.x, task.y + 22);
        ctx.stroke();
    }

    drawLines(): void {
        var ctx = this.canvas.getContext("2d");
        ctx.strokeStyle = "black";
       // ctx.lineCap = "round";
        for (let i = 0; i < this.lines.length; i++) {
            var line = this.lines[i];
            ctx.beginPath();
            ctx.moveTo(line.x, line.y);
            ctx.lineTo(line.x + line.width, line.y + line.height);
            ctx.stroke();
        }

    }
}
