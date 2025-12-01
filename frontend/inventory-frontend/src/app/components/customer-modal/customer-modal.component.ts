import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { CustomerService } from '../../core/services/customer.service';
import {
  CustomerRequest,
  CustomerResponse,
  TypeCustomerResponse,
  CondicionPago,
  DocumentType,
} from '../../core/models/customer.model';

@Component({
  selector: 'app-customer-modal',
  templateUrl: './customer-modal.component.html',
  styleUrls: ['./customer-modal.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
})
export class CustomerModalComponent implements OnInit {
  customerForm: FormGroup;
  typeCustomers: TypeCustomerResponse[] = [];
  isEdit = false;
  isLoading = true;

  documentTypes = [
    { value: 'DNI' as DocumentType, label: 'DNI' },
    { value: 'RUC' as DocumentType, label: 'RUC' },
  ];

  paymentConditions = [
    { value: 'EFECTIVO' as CondicionPago, label: 'Efectivo' },
    { value: 'CREDIT_15_DAYS' as CondicionPago, label: 'Crédito 15 días' },
    { value: 'CREDIT_30_DAYS' as CondicionPago, label: 'Crédito 30 días' },
    { value: 'CREDIT_45_DAYS' as CondicionPago, label: 'Crédito 45 días' },
    { value: 'CREDIT_60_DAYS' as CondicionPago, label: 'Crédito 60 días' },
  ];

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    public dialogRef: MatDialogRef<CustomerModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { customer?: CustomerResponse; isEdit?: boolean }
  ) {
    this.customerForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadTypeCustomers();

    if (this.data?.customer) {
      this.isEdit = true;
      this.populateForm(this.data.customer);
    } else {
      this.isEdit = false;
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      nombre: [''],
      apellidos: [''],
      razonSocial: [''],
      tipoDocumento: ['', Validators.required],
      nroDocumento: [
        '',
        [Validators.required, Validators.pattern(/^(\d{8}|\d{11})$/)],
      ],
      direccion: [''],
      telefono: ['', [Validators.maxLength(9)]],
      email: ['', Validators.email],
      zona: ['', Validators.maxLength(150)],
      distritoId: [null, Validators.required],
      tipoClienteId: [null, Validators.required],
      rubro: ['', Validators.maxLength(100)],
      condicionPago: ['', Validators.required],
      limiteDolares: [0, [Validators.min(0)]],
      limiteCreditoSoles: [0, [Validators.min(0)]],
      notas: [''],
    });
  }

  populateForm(customer: CustomerResponse): void {
    this.customerForm.patchValue({
      nombre: customer.nombre || '',
      apellidos: customer.apellidos || '',
      razonSocial: customer.razonSocial || '',
      tipoDocumento: customer.tipoDocumento || '',
      nroDocumento: customer.nroDocumento || '',
      direccion: customer.direccion || '',
      telefono: customer.telefono || '',
      email: customer.email || '',
      zona: customer.zona || '',
      // distritoId y tipoClienteId no vienen en el response -> el usuario elige
      rubro: customer.rubro || '',
      condicionPago: customer.condicionPago || '',
      limiteDolares: customer.limiteDolares ?? 0,
      limiteCreditoSoles: customer.limiteCreditoSoles ?? 0,
      notas: customer.notas || '',
    });

    this.onDocumentTypeChange();
  }

  loadTypeCustomers(): void {
    this.isLoading = true;
    this.customerService.getTypeCustomers().subscribe({
      next: (resp) => {
        this.typeCustomers = resp.data ?? [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading type customers:', error);
        this.typeCustomers = [];
        this.isLoading = false;
      },
    });
  }

  onDocumentTypeChange(): void {
    const tipoDocumento = this.customerForm.get('tipoDocumento')?.value;

    if (tipoDocumento === 'DNI') {
      this.customerForm.get('razonSocial')?.clearValidators();
      this.customerForm.get('nombre')?.setValidators([Validators.required]);
      this.customerForm.get('apellidos')?.setValidators([Validators.required]);
    } else if (tipoDocumento === 'RUC') {
      this.customerForm.get('nombre')?.clearValidators();
      this.customerForm.get('apellidos')?.clearValidators();
      this.customerForm.get('razonSocial')?.setValidators([Validators.required]);
    }

    this.customerForm.get('nombre')?.updateValueAndValidity();
    this.customerForm.get('apellidos')?.updateValueAndValidity();
    this.customerForm.get('razonSocial')?.updateValueAndValidity();
  }

  onSubmit(): void {
    this.customerForm.markAllAsTouched();

    if (!this.customerForm.valid) {
      alert('Por favor complete todos los campos requeridos correctamente.');
      return;
    }

    const form: CustomerRequest = {
      ...this.customerForm.value,
      distritoId: Number(this.customerForm.get('distritoId')?.value),
      tipoClienteId: Number(this.customerForm.get('tipoClienteId')?.value),
      limiteDolares:
        Number(this.customerForm.get('limiteDolares')?.value) || 0,
      limiteCreditoSoles:
        Number(this.customerForm.get('limiteCreditoSoles')?.value) || 0,
      tipoDocumento: this.customerForm.get('tipoDocumento')?.value,
      condicionPago: this.customerForm.get('condicionPago')?.value,
    };

    if (this.isEdit && this.data?.customer?.id) {
      this.customerService
        .updateCustomer(this.data.customer.id, form)
        .subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => {
            console.error('Error actualizando cliente:', error);
            alert(
              'Error al actualizar el cliente: ' +
                (error.error?.message || error.message || 'Error desconocido')
            );
          },
        });
    } else {
      this.customerService.createCustomer(form).subscribe({
        next: () => this.dialogRef.close(true),
        error: (error) => {
          console.error('Error creando cliente:', error);
          alert(
            'Error al crear el cliente: ' +
              (error.error?.message || error.message || 'Error desconocido')
          );
        },
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  get hasTypeCustomers(): boolean {
    return Array.isArray(this.typeCustomers) && this.typeCustomers.length > 0;
  }

  testButton() {
    alert('Botón funcionando! Form válido: ' + this.customerForm.valid);
  }
}
