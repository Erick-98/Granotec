import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ProductService } from '../../core/services/product.service';
import { ProductRequest, FamilyProductResponse, VendorResponse, TIPO_PRESENTACION, UNIT_OF_MEASURE, ProductResponse } from '../../core/models/product.model';

@Component({
  selector: 'app-product-modal',
  templateUrl: './product-modal.component.html',
  styleUrls: ['./product-modal.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCheckboxModule
  ]
})
export class ProductModalComponent implements OnInit {
  productForm: FormGroup;
  isEdit = false;
  isLoading = false;

  // Datos para dropdowns
  familyProducts: FamilyProductResponse[] = [];
  vendors: VendorResponse[] = [];
  tipoPresentacionOptions = TIPO_PRESENTACION;
  unitOfMeasureOptions = UNIT_OF_MEASURE;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    public dialogRef: MatDialogRef<ProductModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.productForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadDropdownData();

    if (this.data?.product) {
      this.isEdit = true;
      this.populateForm(this.data.product);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      code: ['', [Validators.required, Validators.maxLength(50)]],
      nombreComercial: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      proveedorId: [null],
      tipoPresentacion: ['', Validators.required],
      unitOfMeasure: ['', Validators.required],
      familiaId: [null],
      blocked: [false]
    });
  }

  populateForm(product: ProductResponse): void {
    this.productForm.patchValue({
      code: product.code ?? '',
      nombreComercial: product.nombreComercial ?? '',
      description: product.description ?? '',
      proveedorId: product.proveedorId ?? null,
      tipoPresentacion: product.tipoPresentacion ?? '',
      unitOfMeasure: product.unitOfMeasure ?? '',
      familiaId: product.familiaId ?? null,
      blocked: product.isLocked ?? false
    });
  }


loadDropdownData(): void {
  this.isLoading = true;

  // Cargar familias de producto
  this.productService.getFamilyProducts().subscribe({
    next: (families) => {
      console.log('🔵 Familias cargadas:', families);
      console.log('🔵 Número de familias:', families.length);
      
      if (families.length > 0) {
        console.log('🔵 Primera familia:', families[0]);
      } else {
        console.log('🔴 No se cargaron familias - array vacío');
      }
      
      this.familyProducts = families;
    },
    error: (error) => {
      console.error('🔴 Error cargando familias:', error);
      console.error('🔴 Error completo:', error);
    }
  });

  // Cargar proveedores (ya funciona)
  this.productService.getVendors().subscribe({
    next: (vendors) => {
      console.log('✅ Proveedores cargados:', vendors.length);
      this.vendors = vendors;
      this.isLoading = false;
    },
    error: (error) => {
      console.error('Error loading vendors:', error);
      this.isLoading = false;
    }
  });
}

  onSubmit(): void {
    if (this.productForm.valid && !this.isLoading) {
      const formData: ProductRequest = { ...this.productForm.value };

      console.log('Antes:', this.productForm.value);
      console.log('Después (formData inicial):', formData);

      // Normalizar proveedorId
      if (formData.proveedorId == null) {
        formData.proveedorId = undefined;
      } else {
        formData.proveedorId = Number(formData.proveedorId);
      }

      // Normalizar familiaId
      if (formData.familiaId == null) {
        formData.familiaId = undefined;
      } else {
        formData.familiaId = Number(formData.familiaId);
      }

      console.log('Datos finales a enviar:', formData);

      if (this.isEdit && this.data?.product?.id) {
        // 🟡 EDITAR
        this.productService.updateProduct(this.data.product.id, formData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => {
            console.error('🔴 Error al actualizar producto:', error);
            alert('Error al actualizar producto: ' + (error.error?.message || error.message || 'Error desconocido'));
          }
        });
      } else {
        // 🟢 CREAR
        this.productService.createProduct(formData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => {
            console.error('🔴 Error completo:', error);
            alert('Error al crear producto: ' + (error.error?.message || error.message || 'Error desconocido'));
          }
        });
      }
    }
  }



  onCancel(): void {
    this.dialogRef.close(false);
  }
}