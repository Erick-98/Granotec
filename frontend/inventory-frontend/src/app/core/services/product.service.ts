import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ProductRequest, ProductResponse, FamilyProductResponse, VendorResponse } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/product`;
  private familyProductUrl = `${environment.apiUrl}/family_product`;
  private vendorUrl = `${environment.apiUrl}/vendor`;

  constructor(private http: HttpClient) { }

  createProduct(product: ProductRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}`, product).pipe(
      catchError(error => {
        console.error('Error creating product:', error);
        throw error;
      })
    );
  }

  getProducts(page: number = 0, size: number = 20, search: string = ''): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (search) {
      params = params.set('q', search);
    }
    
    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map(response => {
        // Ajustar según la estructura de tu API response
        return response.data || response;
      }),
      catchError(error => {
        console.error('Error en getProducts:', error);
        return of({ content: [], totalElements: 0 });
      })
    );
  }

  getProduct(id: number): Observable<ProductResponse> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data || response)
    );
  }

  updateProduct(id: number, product: ProductRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getFamilyProducts(): Observable<FamilyProductResponse[]> {
    return this.http.get<any>(this.familyProductUrl).pipe(
      map(response => {
        if (response && response.data && Array.isArray(response.data)) {
          return response.data;
        } else if (Array.isArray(response)) {
          return response;
        } else {
          console.warn('Formato inesperado para familias de producto:', response);
          return [];
        }
      }),
      catchError(error => {
        console.error('Error en getFamilyProducts:', error);
        return of([]);
      })
    );
  }

  getVendors(): Observable<VendorResponse[]> {
    return this.http.get<any>(this.vendorUrl).pipe(
      map(response => {
        if (response && response.data && Array.isArray(response.data)) {
          return response.data;
        } else if (Array.isArray(response)) {
          return response;
        } else {
          console.warn('Formato inesperado para proveedores:', response);
          return [];
        }
      }),
      catchError(error => {
        console.error('Error en getVendors:', error);
        return of([]);
      })
    );
  }
}