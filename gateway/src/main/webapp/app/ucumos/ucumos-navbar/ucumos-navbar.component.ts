import { Component } from '@angular/core';
import { Principal} from '../../account';

@Component({
    selector: 'jhi-ucumos-navbar',
    templateUrl: './ucumos-navbar.component.html',
    styleUrls: [
        'ucumos-navbar.css'
    ]
})
export class UcumosNavbarComponent {
    constructor(
        private principal: Principal,
    ) {}

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }
}
