import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ProveedorService } from 'src/app/core/services/proveedor.service';
import { ProveedorRequest } from 'src/app/core/models/proveedor-request.model';
import { ProveedorResponse } from 'src/app/core/models/proveedor-response.model';

// Angular Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';


@Component({
  standalone: true,
  selector: 'app-proveedor-dialog',
  templateUrl: './proveedor-dialog.component.html',
  styleUrls: ['./proveedor-dialog.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // Angular Material
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDialogModule
  ]
})

export class ProveedorDialogComponent {
  form: FormGroup;
  title = 'Nuevo Proveedor';

  tiposDocumento = ['DNI', 'RUC'];
  monedas = ['PEN', 'USD'];
  condicionesPago = ['EFECTIVO', 'CREDIT_15_DAYS', 'CREDIT_30_DAYS', 'CREDIT_45_DAYS', 'CREDIT_60_DAYS'];

  constructor(
    private fb: FormBuilder,
    private proveedorService: ProveedorService,
    public dialogRef: MatDialogRef<ProveedorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProveedorResponse | null
  ) {
    this.title = this.data ? 'Editar Proveedor' : 'Nuevo Proveedor';

this.form = this.fb.group({
  razonSocial: [data?.razonSocial ?? '', Validators.required], // ← USA razonSocial
  tipoDocumento: [data?.tipoDocumento ?? null],
  nroDocumento: [data?.nroDocumento ?? '', Validators.maxLength(20)], // ← USA nroDocumento
  direccion: [data?.direccion ?? ''],
  telefono: [data?.telefono ?? ''],
  email: [data?.email ?? '', Validators.email],
  moneda: [data?.moneda ?? 'PEN', Validators.required],
  condicionPago: [data?.condicionPago ?? 'EFECTIVO'],
  notas: [data?.notas ?? '']
});
}
  save(): void {
    if (this.form.invalid) return;

    const request: ProveedorRequest = this.form.value;

    if (this.data?.id) {
      this.proveedorService.update(this.data.id, request).subscribe(() => this.dialogRef.close(true));
    } else {
      this.proveedorService.create(request).subscribe(() => this.dialogRef.close(true));
    }
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
