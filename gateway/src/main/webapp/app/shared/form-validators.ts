import {
    AbstractControl, AsyncValidatorFn, ValidationErrors, ValidatorFn
} from '@angular/forms';
import {Injectable} from '@angular/core';
import {catchError, map} from 'rxjs/internal/operators';
import {Observable} from 'rxjs/Rx';
import {UserService} from '../account/service/user.service';

export function passwordMatchValidator(ctlAnother: AbstractControl): ValidatorFn {
    return (ctl: AbstractControl): ValidationErrors | null => {
        const password = ctl.value;
        const confirmPassword = ctlAnother.value;
        return (password !== confirmPassword) ? {'passwordMatch': {value: '两次输入密码不匹配！'}} : null;
    };
}

export function passwordStrongValidator(): ValidatorFn {
    return (ctl: AbstractControl): ValidationErrors | null => {
        const password = ctl.value;
        const strong = (password.length > 7 && /[a-z]+/.test(password) && /[A-Z]+/.test(password) && /[0-9]+/.test(password));

        return (!strong) ? {'passwordStrong': {value: '密码应至少8位大小写字母和数字！'}} : null;
    };
}

export function itemExistValidator(items: string[]): ValidatorFn {
    return (ctl: AbstractControl): ValidationErrors | null => {
        const item = ctl.value + '';
        return items.includes(item) ? {'itemExist': {value: '已存在，请另选一个！'}} : null;
    };
}

export function listEmptyValidator(): ValidatorFn {
    return (ctl: AbstractControl): ValidationErrors | null => {
        return ctl.value.length < 1 ? {'listEmpty': {value: '列表不能为空！'}} : null;
    };
}

@Injectable({ providedIn: 'root' })
export class UniqueLoginValidator {
    constructor(private userService: UserService) {}

    validate(except?: string): AsyncValidatorFn {
        return (ctrl: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
            if (!ctrl.value) {
                return Observable.of(null);
            } else if (except && ctrl.value === except) {
                return Observable.of(null);
            } else {
                return this.userService.getLoginExist(ctrl.value).pipe(
                    map((res) => (res.body ? { unique: true } : null)),
                    catchError(() => null)
                );
            }
        };
    }
}

@Injectable({ providedIn: 'root' })
export class UniqueEmailValidator {
    constructor(private userService: UserService) {}

    validate(except?: string): AsyncValidatorFn {
        return (ctrl: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
            if (!ctrl.value) {
                return Observable.of(null);
            } else if (except && ctrl.value === except) {
                return Observable.of(null);
            } else {
                return this.userService.getEmailExist(ctrl.value).pipe(
                    map((res) => (res.body ? {unique: true} : null)),
                    catchError(() => null)
                );
            }
        };
    }
}

@Injectable({ providedIn: 'root' })
export class UniquePhoneValidator {
    constructor(private userService: UserService) {}

    validate(except?: string): AsyncValidatorFn {
        return (ctrl: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
            if (!ctrl.value) {
                return Observable.of(null);
            } else if (except && ctrl.value === except) {
                return Observable.of(null);
            } else {
                return this.userService.getPhoneExist(ctrl.value).pipe(
                    map((res) => (res.body ? {unique: true} : null)),
                    catchError(() => null)
                );
            }
        };
    }
}

@Injectable({ providedIn: 'root' })
export class NoEmailValidator {
    constructor(private userService: UserService) {}

    validate(): AsyncValidatorFn {
        return (ctrl: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
            return this.userService.getEmailExist(ctrl.value).pipe(
                map((res) => (res.body ? null : {noExist: true})),
                catchError(() => null)
            );
        };
    }
}

@Injectable({ providedIn: 'root' })
export class NoLoginValidator {
    constructor(private userService: UserService) {}

    validate(): AsyncValidatorFn {
        return (ctrl: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> => {
            return this.userService.getLoginExist(ctrl.value).pipe(
                map((res) => (res.body ? null : {noExist: true})),
                catchError(() => null)
            );
        };
    }
}
