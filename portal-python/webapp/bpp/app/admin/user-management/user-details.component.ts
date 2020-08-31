import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import {UniqueEmailValidator, UniqueLoginValidator, UniquePhoneValidator} from '../../shared/form-validators';
import {SnackBarService, Principal, User, HttpService} from '../../shared';

export class Authority {
    name: string;
    selected: FormControl;

    constructor(name?: string,
                selected?: FormControl,
    ) {
        this.name = name ? name : null;
        this.selected = selected ? selected : null;
    }
}

@Component({
    templateUrl: './user-details.component.html'
})
export class UserDetailsComponent implements OnInit {
    formGroup: FormGroup;
    user: User;
    authorities: Authority[];
    createNew: Boolean;
    editUser: Boolean;
    viewUser: Boolean;

    get login() { return this.formGroup.get('login'); }
    get fullName() { return this.formGroup.get('fullName'); }
    get email() { return this.formGroup.get('email'); }
    get phone() { return this.formGroup.get('phone'); }
    get password() { return this.formGroup.get('password'); }
    get activated() { return this.formGroup.get('activated'); }
    get authoritySelections() {
        return this.formGroup.get('authoritySelections') as FormArray;
    }

    constructor(private formBuilder: FormBuilder,
                public dialogRef: MatDialogRef<UserDetailsComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private principal: Principal,
                private http: HttpService,
                private snackBarService: SnackBarService,
                private uniqueLoginValidator: UniqueLoginValidator,
                private uniqueEmailValidator: UniqueEmailValidator,
                private uniquePhoneValidator: UniquePhoneValidator,
    ) {
        this.formGroup = this.formBuilder.group({
            login: ['', {
                validators: [Validators.required, Validators.maxLength(50),
                    Validators.pattern('^[_.@A-Za-z0-9-]*$')],
                updateOn: 'blur',
            }],
            fullName: ['', [Validators.maxLength(50)]],
            email: ['', {
                validators: [Validators.required, Validators.email, Validators.maxLength(50), Validators.minLength(5)],
                updateOn: 'blur',
            }],
            phone: ['', {
                validators: [Validators.required, Validators.minLength(5), Validators.maxLength(20), Validators.pattern('^[0-9]*$')],
                updateOn: 'blur',
            }],
            password: [''],
            activated: [true],
            authoritySelections: this.formBuilder.array([]),
            createdDate: [''],
            lastModifiedDate: [''],
            lastModifiedBy: [''],
        });
    }

    ngOnInit() {
        this.authorities = [];

        const body1 = {
            action: 'get_authorities',
            args: {},
        };
        this.http.post('uaa', body1).subscribe(
            (res1) => {
                if (res1.body['status'] === 'ok') {
                    res1.body['value'].forEach((authorityName) => {
                        const formControl = new FormControl(false);
                        this.authorities.push(new Authority(authorityName, formControl));
                        this.authoritySelections.push(formControl);

                        // 默认每个用户必须有ROLE_USER角色
                        if (authorityName === 'ROLE_USER') {
                            if (this.data.operation === 'create') {
                                formControl.setValue(true);
                            }
                            formControl.disable();
                        }
                    });

                    switch (this.data.operation) {
                        case 'create':
                            this.createNew = true;
                            this.editUser = false;
                            this.viewUser = false;
                            this.user = new User();
                            this.login.setAsyncValidators([this.uniqueLoginValidator.validate()]);
                            this.email.setAsyncValidators([this.uniqueEmailValidator.validate()]);
                            this.phone.setAsyncValidators([this.uniquePhoneValidator.validate()]);
                            this.password.setValidators([Validators.required, Validators.maxLength(50), Validators.minLength(4)]);
                            this.activated.disable();
                            break;
                        case 'edit':
                            this.createNew = false;
                            this.editUser = true;
                            this.viewUser = false;
                            const body2 = {
                                action: 'find_user',
                                args: {
                                    login: this.data.login,
                                },
                            };
                            this.http.post('uaa', body2).subscribe((res2) => {
                                if (res2.body['status'] === 'ok') {
                                    this.user = res2.body['value'];
                                    this.login.setValue(this.user.login);
                                    this.login.disable();
                                    this.fullName.setValue(this.user.fullName);
                                    this.email.setValue(this.user.email);
                                    this.phone.setValue(this.user.phone);
                                    this.password.setValue(null);
                                    this.activated.setValue(this.user.activated);
                                    this.authorities.forEach((authority) => {
                                        if (this.user.authorities.includes(authority.name)) {
                                            authority.selected.setValue(true);
                                        }
                                    });
                                    this.email.setAsyncValidators([this.uniqueEmailValidator.validate(this.user.email)]);
                                    this.phone.setAsyncValidators([this.uniquePhoneValidator.validate(this.user.phone)]);
                                }
                            });
                            break;
                        case 'view':
                            this.createNew = false;
                            this.editUser = false;
                            this.viewUser = true;
                            this.formGroup.disable();
                            const body3 = {
                                action: 'find_user',
                                args: {
                                    login: this.data.login,
                                },
                            };
                            this.http.post('uaa', body3).subscribe((res3) => {
                                if (res3.body['status'] === 'ok') {
                                    this.user = res3.body['value'];
                                    this.login.setValue(this.user.login);
                                    this.fullName.setValue(this.user.fullName);
                                    this.email.setValue(this.user.email);
                                    this.phone.setValue(this.user.phone);
                                    this.activated.setValue(this.user.activated);
                                    this.authorities.forEach((authority) => {
                                        if (this.user.authorities.includes(authority.name)) {
                                            authority.selected.setValue(true);
                                        }
                                    });

                                    if (this.user.createdDate) {
                                        this.formGroup.get('createdDate').setValue(this.user.createdDate.toString());
                                    }
                                    if (this.user.lastModifiedDate) {
                                        this.formGroup.get('lastModifiedDate').setValue(this.user.lastModifiedDate.toString());
                                    }
                                    this.formGroup.get('lastModifiedBy').setValue(this.user.lastModifiedBy);
                                }
                            });
                            break;
                    }
                }
            }
        );
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onSubmit() {
        this.user.login = this.login.value;
        this.user.fullName = this.fullName.value;
        this.user.phone = this.phone.value;
        this.user.email = this.email.value;
        this.user.password = this.password.value;
        this.user.activated = this.activated.value;

        this.user.authorities = [];
        this.authorities.forEach((authority) => {
            if (authority.selected.value) {
                this.user.authorities.push(authority.name);
            }
        });

        if (this.createNew) {
            const body = {
                action: 'create_user',
                args: {
                    user: this.user,
                },
            };
            this.http.post('uaa', body).subscribe((res) => {
                if (res.body['status'] === 'ok') {
                    this.data.caller.refresh(true);
                    this.principal.updateCurrentAccount().then();
                    this.snackBarService.success('新用户创建成功！');
                    this.onClose();
                } else {
                    this.snackBarService.error('创建新用户失败！');
                }
            }, () => {
                this.snackBarService.error('创建新用户失败！');
            });
        } else {
            const body = {
                action: 'update_user',
                args: {
                    user: this.user,
                },
            };
            this.http.post('uaa', body).subscribe((res) => {
                if (res.body['status'] === 'ok') {
                    this.data.caller.refresh(false);
                    this.principal.updateCurrentAccount().then();
                    this.snackBarService.success('用户信息更新成功！');
                    this.onClose();
                } else {
                    this.snackBarService.error('更新用户信息失败！');
                }
            }, () => {
                this.snackBarService.error('更新用户信息失败！');
            });
        }
    }

}
