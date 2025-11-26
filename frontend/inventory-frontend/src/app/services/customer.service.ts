// services/customer.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { throwError } from 'rxjs';
import { 
  CustomerRequest, 
  CustomerResponse, 
  TypeCustomerResponse  // ← Ahora sí existe
} from '../core/models/customer.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = `${environment.apiUrl}/customer`;
  private typeCustomerUrl = `${environment.apiUrl}/type-customer`;

  constructor(private http: HttpClient) { }

createCustomer(customer: CustomerRequest): Observable<any> {
  console.log('🎯 SERVICIO createCustomer EJECUTADO');
  const processedCustomer = {
    ...customer
  };
  
  console.log('📤 URL:', `${this.apiUrl}`);
  console.log('📤 Datos a enviar:', processedCustomer);
  
  // Verificar que HttpClient esté disponible
  console.log('🔍 HttpClient:', this.http);
  
  return this.http.post(`${this.apiUrl}`, processedCustomer).pipe(
    tap(response => console.log('✅ Respuesta recibida:', response)),
    catchError(error => {
      console.error('❌ Error en servicio:', error);
      return throwError(() => error);
    })
  );
}
  getCustomers(): Observable<CustomerResponse[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => {
        console.log('📦 Respuesta de clientes:', response);
        if (response && response.data && Array.isArray(response.data)) {
          return response.data;
        } else if (Array.isArray(response)) {
          return response;
        } else {
          console.warn('Formato inesperado para clientes:', response);
          return [];
        }
      }),
      catchError(error => {
        console.error('Error en getCustomers:', error);
        return of([]);
      })
    );
  }

  getCustomer(id: number): Observable<CustomerResponse> {
    return this.http.get<CustomerResponse>(`${this.apiUrl}/${id}`);
  }

  updateCustomer(id: number, customer: CustomerRequest): Observable<any> {
    const processedCustomer = {
      ...customer,
      condicionPago: customer.condicionPago.toUpperCase()
    };
    return this.http.put(`${this.apiUrl}/${id}`, processedCustomer);
  }

  deleteCustomer(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getTypeCustomers(): Observable<TypeCustomerResponse[]> {
    console.log('🔍 Solicitando tipos de cliente desde:', this.typeCustomerUrl);
    
    return this.http.get<any>(this.typeCustomerUrl).pipe(
      map(response => {
        console.log('📦 Respuesta completa de tipos:', response);
        
        if (response && response.data && Array.isArray(response.data)) {
          console.log('✅ Datos encontrados en response.data:', response.data.length, 'registros');
          return response.data;
        } else if (Array.isArray(response)) {
          console.log('✅ Respuesta es array directo:', response.length, 'registros');
          return response;
        } else {
          console.warn('❌ Formato inesperado:', response);
          return [];
        }
      }),
      catchError(error => {
        console.error('💥 Error en getTypeCustomers:', error);
        return of([]);
      })
    );
  }
}