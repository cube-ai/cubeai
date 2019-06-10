import { Component } from '@angular/core';
import { Principal} from '../../account';

@Component({
    selector: 'jhi-ai-ability-navbar',
    templateUrl: './ai-ability-navbar.component.html',
    styleUrls: [
        'ai-ability-navbar.css'
    ]
})
export class AiAbilityNavbarComponent {
    constructor(
        private principal: Principal,
    ) {}

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }
}
