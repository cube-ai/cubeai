import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material';

@Injectable()
export class SnackBarService {
    duration = 3000;

    constructor(
        public snackBar: MatSnackBar,
    ) {}

    public open(msg, action?) {
        this.snackBar.open(
            msg,
            action ? action : '',
            {
                duration: this.duration,
                verticalPosition: 'top',
                horizontalPosition: 'right',
            });
    }

    public info(msg, action?) {
        this.snackBar.open(
            msg,
            action ? action : '',
            {
                duration: this.duration,
                verticalPosition: 'top',
                horizontalPosition: 'right',
                panelClass: ['alert-info'],
            });
    }

    public success(msg, action?) {
        this.snackBar.open(
            msg,
            action ? action : '',
            {
                duration: this.duration,
                verticalPosition: 'top',
                horizontalPosition: 'right',
                panelClass: ['alert-success'],
            });
    }

    public warning(msg, action?) {
        this.snackBar.open(
            msg,
            action ? action : '',
            {
                duration: this.duration,
                verticalPosition: 'top',
                horizontalPosition: 'right',
                panelClass: ['alert-warning'],
            });
    }

    public error(msg, action?) {
        this.snackBar.open(
            msg,
            action ? action : '',
            {
                duration: this.duration,
                verticalPosition: 'top',
                horizontalPosition: 'right',
                panelClass: ['alert-danger'],
            });
    }
}
