import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MaterialModule } from 'src/app/material.module';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from 'src/app/core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-side-login',
  imports: [RouterModule, MaterialModule, FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './side-login.component.html',
})
export class AppSideLoginComponent {
  constructor(private router: Router, private auth: AuthService, private snackBar: MatSnackBar) {}

  form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
  });

  get f() {
    return this.form.controls;
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.getRawValue();
    this.auth.login({ email: email as string, password: password as string }).subscribe({
      next: (res) => {
        console.log('✅ Login exitoso', res);
        // Intentar navegar y loguear resultado para depuración
        this.router
          .navigate(['/dashboard'])
          .then((ok) => console.log('Router.navigate -> success:', ok))
          .catch((err) => console.error('Router.navigate -> error:', err));
      },
      error: (err) => {
        console.error('❌ Error en login', err);
        this.form.setErrors({invalidCredentials: true});
        this.snackBar.open('Credenciales inválidas. Por favor, inténtalo de nuevo.', 'Cerrar', {
          duration: 3000,
          panelClass: ['snackbar-error'],
        });
      },
    });
  }
}
