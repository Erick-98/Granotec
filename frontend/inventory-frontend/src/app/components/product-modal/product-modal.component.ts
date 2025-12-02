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
import { ProductRequest, FamilyProductResponse, VendorResponse, TIPO_PRESENTACION, UNIT_OF_MEASURE, ProductResponse, TYPE_PRODUCT } from '../../core/models/product.model';

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

  familyProducts: FamilyProductResponse[] = [];
  vendors: VendorResponse[] = [];
  tipoPresentacionOptions = TIPO_PRESENTACION;
  unitOfMeasureOptions = UNIT_OF_MEASURE;
  typeProductOptions = TYPE_PRODUCT;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    public dialogRef: MatDialogRef<ProductModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { product?: ProductResponse | null }
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
        tipoProducto: ['', Validators.required], //AGREGADO
        tipoPresentacion: ['', Validators.required],
        unitOfMeasure: ['', Validators.required],
        familiaId: [null],
        blocked: [false]
    });
  }



  populateForm(product: ProductResponse): void {
    this.productForm.patchValue({
        code: product.code ?? '',
        nombreComercial: product.name ?? '',          // viene como "name"
        description: product.description ?? '',
        // proveedorId y familiaId NO vienen en el response,
        // así que se quedan en null (el usuario los vuelve a elegir si edita)
        proveedorId: null,
        familiaId: null,
        tipoPresentacion: product.tipoPresentacion ?? '',
        unitOfMeasure: product.unitOfMeasure ?? '',
        tipoProducto: product.tipoProducto ?? '',     //nuevo
        blocked: product.isLocked ?? false
    });
    }



  loadDropdownData(): void {
    this.isLoading = true;

    this.productService.getFamilyProducts().subscribe({
      next: (families) => {
        this.familyProducts = families;
      },
      error: (error) => {
        console.error('Error cargando familias:', error);
      }
    });

    this.productService.getVendors().subscribe({
      next: (vendors) => {
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
      const formValue = this.productForm.value;

      const formData: ProductRequest = {
        code: formValue.code,
        nombreComercial: formValue.nombreComercial,
        description: formValue.description,
        tipoPresentacion: formValue.tipoPresentacion,
        tipoProducto: formValue.tipoProducto,         // 🔹 OBLIGATORIO
        unitOfMeasure: formValue.unitOfMeasure,
        proveedorId: formValue.proveedorId ? Number(formValue.proveedorId) : undefined,
        familiaId: formValue.familiaId ? Number(formValue.familiaId) : undefined,
        blocked: formValue.blocked
      };

      console.log('Datos finales a enviar:', formData);

      if (this.isEdit && this.data?.product?.id) {
        this.productService.updateProduct(this.data.product.id, formData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => {
            console.error('🔴 Error al actualizar producto:', error);
            alert('Error al actualizar producto: ' + (error.error?.message || error.message || 'Error desconocido'));
          }
        });
      } else {
        this.productService.createProduct(formData).subscribe({
          next: () => this.dialogRef.close(true),
          error: (error) => {
            console.error('🔴 Error al crear producto:', error);
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
