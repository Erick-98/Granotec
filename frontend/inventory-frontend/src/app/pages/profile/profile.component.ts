import { Component } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { UserService } from '../../services/user.service';

@Component({
    selector: 'app-profile',
    imports: [MaterialModule],
    templateUrl: './profile.component.html',
})
export class ProfileComponent {
    constructor(public user: UserService) {}
}


