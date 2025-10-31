import { Component } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { UserService } from '../../services/user.service';

@Component({
    selector: 'app-account',
    imports: [MaterialModule],
    templateUrl: './account.component.html',
})
export class AccountComponent {
    constructor(public user: UserService) {}
}


