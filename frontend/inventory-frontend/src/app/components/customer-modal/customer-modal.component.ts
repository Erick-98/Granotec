// components/customer-modal/customer-modal.component.ts
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { CustomerService } from '../../services/customer.service';
import { 
  CustomerRequest, 
  TypeCustomerResponse 
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
    MatButtonModule
  ]
})
export class CustomerModalComponent implements OnInit {
  customerForm: FormGroup;
  typeCustomers: TypeCustomerResponse[] = []; // Asegurar que sea array
  isEdit = false;
  isLoading = true;

    documentTypes = [
    { value: 'DNI', label: 'DNI' },
    { value: 'RUC', label: 'RUC' }
    ];

paymentConditions = [
  { value: 'EFECTIVO', label: 'Efectivo' },
  { value: 'CREDIT_15_DAYS', label: 'Crédito 15 días' },
  { value: 'CREDIT_30_DAYS', label: 'Crédito 30 días' },
  { value: 'CREDIT_45_DAYS', label: 'Crédito 45 días' },
  { value: 'CREDIT_60_DAYS', label: 'Crédito 60 días' }
];

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    public dialogRef: MatDialogRef<CustomerModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.customerForm = this.createForm();
  }

 ngOnInit(): void {
  this.loadTypeCustomers();
  
  console.log('🔍 Data recibida en modal:', this.data);
  console.log('📝 Cliente para editar:', this.data?.customer);
  console.log('🆔 ID del cliente:', this.data?.customer?.id);
  
  if (this.data?.customer) {
    this.isEdit = true;
    console.log('✅ MODO: EDITAR - Cliente ID:', this.data.customer.id);
    this.populateForm(this.data.customer);
  } else {
    this.isEdit = false;
    console.log('✅ MODO: CREAR - Nuevo cliente');
  }
}

  createForm(): FormGroup {
    return this.fb.group({
      nombre: [''],
      apellidos: [''],
      razonSocial: [''],
      tipoDocumento: ['', Validators.required],
      nroDocumento: ['', [Validators.required, Validators.pattern(/^\d{8}|\d{11}$/)]],
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
      notas: ['']
    });
  }

  populateForm(customer: any): void {
  console.log('📝 Poblando formulario con datos del cliente:', customer);
  
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
    distritoId: customer.distritoId || null,
    tipoClienteId: customer.tipoClienteId || null,
    rubro: customer.rubro || '',
    condicionPago: customer.condicionPago || '',
    limiteDolares: customer.limiteDolares || 0,
    limiteCreditoSoles: customer.limiteCreditoSoles || 0,
    notas: customer.notas || ''
  });

  console.log('✅ Formulario después de poblar:', this.customerForm.value);
  this.onDocumentTypeChange();
}

// ✅ Métodos auxiliares para obtener IDs desde los nombres
private getTipoClienteIdFromName(tipoClienteName: string): number | null {
  if (!tipoClienteName || !this.typeCustomers.length) return null;
  
  const found = this.typeCustomers.find(tc => 
    tc.nombre === tipoClienteName || tc.id.toString() === tipoClienteName
  );
  return found ? found.id : null;
}

private getDistritoIdFromName(distritoName: string): number | null {
  // Si no tienes una lista de distritos, puedes necesitar cargarlos
  // Por ahora, retornamos null y el usuario deberá seleccionar manualmente
  return null;
}

  loadTypeCustomers(): void {
    this.isLoading = true;
    this.customerService.getTypeCustomers().subscribe({
      next: (response: any) => {
        console.log('Respuesta de tipos de cliente:', response); // Debug
        
        // Manejar diferentes formatos de respuesta
        if (Array.isArray(response)) {
          this.typeCustomers = response;
        } else if (response && response.data && Array.isArray(response.data)) {
          this.typeCustomers = response.data;
        } else if (response && response.items && Array.isArray(response.items)) {
          this.typeCustomers = response.items;
        } else {
          console.error('Formato de respuesta inesperado para tipos de cliente:', response);
          this.typeCustomers = [];
        }
        
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading type customers:', error);
        this.typeCustomers = [];
        this.isLoading = false;
      }
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
  console.log('🔍 Estado del formulario:', this.customerForm.valid);
  console.log('📋 Valores del formulario:', JSON.stringify(this.customerForm.value, null, 2));
  console.log('🔄 Modo:', this.isEdit ? 'EDITAR' : 'CREAR');
  console.log('🆔 ID del cliente:', this.data?.customer?.id);
  console.log('📊 Data completa:', this.data);
  
  this.customerForm.markAllAsTouched();
  
  if (this.customerForm.valid) {
    const formData: CustomerRequest = {
      ...this.customerForm.value,
      distritoId: Number(this.customerForm.get('distritoId')?.value),
      tipoClienteId: Number(this.customerForm.get('tipoClienteId')?.value),
      limiteDolares: Number(this.customerForm.get('limiteDolares')?.value) || 0,
      limiteCreditoSoles: Number(this.customerForm.get('limiteCreditoSoles')?.value) || 0,
      tipoDocumento: this.customerForm.get('tipoDocumento')?.value,
      condicionPago: this.customerForm.get('condicionPago')?.value
    };
    
    console.log('📤 Datos finales para enviar:', JSON.stringify(formData, null, 2));
    
    // ✅ VERIFICACIÓN MEJORADA - Asegurar que estamos en modo edición
    if (this.isEdit && this.data?.customer?.id) {
      // ✅ EDITAR CLIENTE EXISTENTE
      console.log('🔄 EJECUTANDO: Actualizando cliente ID:', this.data.customer.id);
      this.customerService.updateCustomer(this.data.customer.id, formData).subscribe({
        next: (response) => {
          console.log('✅ Cliente actualizado exitosamente:', response);
          this.dialogRef.close(true);
        },
        error: (error) => {
          console.error('❌ Error actualizando cliente:', error);
          console.error('❌ Detalles del error:', error.error);
          alert('Error al actualizar el cliente: ' + (error.error?.message || error.message || 'Error desconocido'));
        }
      });
    } else {
      // ✅ CREAR NUEVO CLIENTE
      console.log('🔄 EJECUTANDO: Creando nuevo cliente...');
      this.customerService.createCustomer(formData).subscribe({
        next: (response) => {
          console.log('✅ Cliente creado exitosamente:', response);
          this.dialogRef.close(true);
        },
        error: (error) => {
          console.error('❌ Error creando cliente:', error);
          console.error('❌ Detalles del error:', error.error);
          alert('Error al crear el cliente: ' + (error.error?.message || error.message || 'Error desconocido'));
        }
      });
    }
  } else {
    console.log('❌ Formulario inválido - Errores:');
    Object.keys(this.customerForm.controls).forEach(key => {
      const control = this.customerForm.get(key);
      if (control?.errors) {
        console.log(`   ${key}:`, control.errors);
      }
    });
    alert('Por favor complete todos los campos requeridos correctamente.');
  }
}
  onCancel(): void {
    this.dialogRef.close(false);
  }

  // Helper para verificar si typeCustomers es un array válido
  get hasTypeCustomers(): boolean {
    return Array.isArray(this.typeCustomers) && this.typeCustomers.length > 0;
  }
  // En customer-modal.component.ts
testButton() {
  console.log('🎯 BOTÓN PRESIONADO - Función test');
  console.log('🔍 Form valid:', this.customerForm.valid);
  console.log('🔍 Form values:', this.customerForm.value);
  
  // Prueba simple sin el servicio
  alert('Botón funcionando! Form válido: ' + this.customerForm.valid);
}
}
