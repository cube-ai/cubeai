import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { FormControl, Validators } from '@angular/forms';
import {SnackBarService, User, UserService} from '../../shared';

@Component({
    templateUrl: './user-password.component.html'
})
export class UserPasswordComponent implements OnInit {
    password: FormControl;
    user: User;

    constructor(public dialogRef: MatDialogRef<UserPasswordComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private userService: UserService,
                private snackBarService: SnackBarService,
    ) {
    }

    ngOnInit() {
        this.password = new FormControl('', [Validators.required, Validators.maxLength(50), Validators.minLength(4)]);
        this.userService.find(this.data.login).subscribe((res) => {
            this.user = res.body;
        });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onSubmit() {
        this.user.password = this.password.value;
        this.userService.update(this.user).subscribe((res) => {
            this.snackBarService.success('用户密码修改成功！');
            this.onClose();
        }, () => {
            this.snackBarService.error('用户密码修改失败！');
        });
    }

}
