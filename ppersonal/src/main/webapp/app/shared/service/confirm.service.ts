import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material';
import { ConfirmDialogComponent } from '../';
import {Observable} from 'rxjs/Rx';

@Injectable()
export class ConfirmService {

    constructor(public dialog: MatDialog) {
    }

    // 使用Promise，调用格式： confirmService.ask('...').then((confirm) => {})
    ask(msg: string): Promise<boolean> {
        return new Promise<boolean>((resolve) => {
            const dialogRef = this.dialog.open(ConfirmDialogComponent, {
                width: '400px',
                data: {
                    msg,
                }
            });

            dialogRef.afterClosed().subscribe((confirm) => {
                resolve(confirm);
            });
        });
    }

    // 使用Observable，调用格式： confirmService.askObservable('...').subscribe((confirm) => {})
    askObservable(msg: string): Observable<boolean> {
        return new Observable<boolean>((observer) => {
            const dialogRef = this.dialog.open(ConfirmDialogComponent, {
                width: '400px',
                data: {
                    msg,
                }
            });

            dialogRef.afterClosed().subscribe((confirm) => {
                observer.next(confirm);
            });
        });
    }
}
