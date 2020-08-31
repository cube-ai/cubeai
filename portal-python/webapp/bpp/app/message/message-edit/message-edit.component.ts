import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FormBuilder, FormGroup, Validators } from '@angular/forms';
import {SnackBarService, HttpService} from '../../shared';
import {Message} from '../model/message.model';
import {MessageDraft} from '../model/massage-draft.model';
import {listEmptyValidator} from '../../shared/form-validators';
import {COMMA, ENTER, SPACE} from '@angular/cdk/keycodes';
import {Router} from '@angular/router';

@Component({
    templateUrl: './message-edit.component.html'
})
export class MessageEditComponent implements OnInit {
    formGroup: FormGroup;
    createNew: Boolean;
    viewMsg: Boolean;
    viewReceived = true;
    url: string;

    readonly separatorKeysCodes: number[] = [ENTER, COMMA, SPACE];

    get receiverList() { return this.formGroup.get('receiverList'); }
    get sender() { return this.formGroup.get('sender'); }
    get receiver() { return this.formGroup.get('receiver'); }
    get subject() { return this.formGroup.get('subject'); }
    get content() { return this.formGroup.get('content'); }

    constructor(private formBuilder: FormBuilder,
                public dialogRef: MatDialogRef<MessageEditComponent>,
                private router: Router,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private http: HttpService,
                private snackBarService: SnackBarService,
    ) {
        this.formGroup = this.formBuilder.group({
            sender: [''],
            receiver: [''],
            receiverList: [[]],
            subject: ['', [Validators.maxLength(120)]],
            content: ['', [Validators.maxLength(1024)]],
        });
    }

    ngOnInit() {
        switch (this.data.operation) {
            case 'create':
                this.createNew = true;
                this.viewMsg = false;
                this.sender.setValue(this.data.sender);
                this.sender.disable();
                this.receiverList.setValidators(listEmptyValidator());
                break;
            case 'view':
                this.createNew = false;
                this.viewMsg = true;
                if (this.data.viewSent) {
                    this.viewReceived = false;
                }
                this.formGroup.disable();
                this.sender.setValue(this.data.message.sender);
                this.receiver.setValue(this.data.message.receiver);
                this.subject.setValue(this.data.message.subject);
                this.content.setValue(this.data.message.content);
                this.url = this.data.message.url;
                break;
        }
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onSend() {
        const message = new Message();
        message.subject = this.subject.value;
        message.content = this.content.value;

        const messageDraft = new MessageDraft();
        messageDraft.message = message;
        messageDraft.receivers = this.receiverList.value;

        const body = {
            action: 'send_multicast_message',
            args: {
                draft: messageDraft,
            },
        };
        this.http.post('uaa', body).subscribe();
        this.onClose();
    }

    addToReceiverList(value: string): void {
        if ((value || '').trim()) {
            const receiver = value.trim();
            if (this.receiverList.value.includes(receiver)) {
                this.snackBarService.error('已输入，请勿重复添加...');
                return;
            }

            const body = {
                action: 'get_login_exist',
                args: {
                    login: receiver,
                },
            };
            this.http.post('uaa', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok' && res.body['value'] === 1) {
                        this.receiverList.value.push(receiver);
                        this.receiverList.updateValueAndValidity();
                    } else {
                        this.snackBarService.error('该收信人不存在！');
                    }
                },
                () => {
                    this.receiverList.value.push(receiver);
                    this.receiverList.updateValueAndValidity();
                }
            );
        }
    }

    removeFromReceiverList(receiver: string) {
        this.receiverList.value.splice(this.receiverList.value.indexOf(receiver), 1);
        this.receiverList.updateValueAndValidity();
    }

    jumpToUrl() {
        // this.router.navigate([this.url]);
        window.location.href = this.url;
        this.dialogRef.close();
    }

}
