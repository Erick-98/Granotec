import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { StorageService } from '../../core/services/almacen.service';
import { StorageRequest } from '../../core/models/storage-request.model';

@Component({
  selector: 'app-storage-modal',
  templateUrl: './storage-modal.component.html',
  styleUrls: ['./storage-modal.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class StorageModalComponent implements OnInit {
  storageForm: FormGroup;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private storageService: StorageService,
    public dialogRef: MatDialogRef<StorageModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.storageForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.data?.storage) {
      this.isEdit = true;
      this.populateForm(this.data.storage);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      descripcion: ['']
    });
  }

  populateForm(storage: any): void {
    this.storageForm.patchValue({
      nombre: storage.nombre || '',
      descripcion: storage.descripcion || ''
    });
  }

  onSubmit(): void {
    if (this.storageForm.valid) {
      const formData: StorageRequest = this.storageForm.value;
      
      if (this.isEdit) {
        this.storageService.updateStorage(this.data.storage.id, formData).subscribe({
          next: (response) => {
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Error updating storage:', error);
            alert('Error al actualizar almacén: ' + (error.error?.mensaje || error.message || 'Error desconocido'));
          }
        });
      } else {
        this.storageService.createStorage(formData).subscribe({
          next: (response) => {
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Error creating storage:', error);
            alert('Error al crear almacén: ' + (error.error?.mensaje || error.message || 'Error desconocido'));
          }
        });
      }
    } else {
      Object.keys(this.storageForm.controls).forEach(key => {
        this.storageForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}