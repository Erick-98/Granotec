import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { TablerIconsModule } from 'angular-tabler-icons';
import { UserService } from '../../../services/user.service';

@Component({
    selector: 'app-topstrip',
    imports: [TablerIconsModule, MatButtonModule],
    templateUrl: './topstrip.component.html',
})
export class AppTopstripComponent {
    constructor(public user: UserService) { }

    toggleTheme() {
        const newTheme = this.user.theme() === 'light' ? 'dark' : 'light';
        this.user.setTheme(newTheme); // método que cambie y guarde el tema
      }
}
