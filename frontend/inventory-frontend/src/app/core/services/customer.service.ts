import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ApiResponse } from 'src/app/core/models/api-response.models';
import {
  CustomerRequest,
  CustomerResponse,
  TypeCustomerResponse,
} from 'src/app/core/models/customer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly baseUrl = `${environment.apiUrl}/customer`;
  private readonly typeCustomerUrl = `${environment.apiUrl}/type-customer`;

  constructor(private http: HttpClient) {}

  // LISTA SIMPLE (GET /customer)
  getCustomers(): Observable<ApiResponse<CustomerResponse[]>> {
    return this.http
      .get<ApiResponse<CustomerResponse[]>>(this.baseUrl)
      .pipe(catchError(this.handleError));
  }

  // OBTENER POR ID (GET /customer/{id})
  getCustomer(id: number | string): Observable<ApiResponse<CustomerResponse>> {
    return this.http
      .get<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // CREAR (POST /customer)
  createCustomer(
    payload: CustomerRequest
  ): Observable<ApiResponse<CustomerResponse>> {
    return this.http
      .post<ApiResponse<CustomerResponse>>(this.baseUrl, payload)
      .pipe(catchError(this.handleError));
  }

  // ACTUALIZAR (PUT /customer/{id})
  updateCustomer(
    id: number | string,
    payload: CustomerRequest
  ): Observable<ApiResponse<CustomerResponse>> {
    return this.http
      .put<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}`, payload)
      .pipe(catchError(this.handleError));
  }

  // ELIMINAR (DELETE /customer/{id})
  deleteCustomer(id: number | string): Observable<ApiResponse<void>> {
    return this.http
      .delete<ApiResponse<void>>(`${this.baseUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // LISTA DE TIPO DE CLIENTE (GET /type-customer)
  getTypeCustomers() {
  return this.http.get<ApiResponse<TypeCustomerResponse[]>>(
    `${environment.apiUrl}/type-customer`
  );
}


  private handleError(error: HttpErrorResponse) {
    const backendMessage =
      (error?.error &&
        (error.error.message || error.error.mensaje || error.error.error)) as
        | string
        | undefined;

    const message = backendMessage
      ? backendMessage
      : error.error instanceof ErrorEvent
      ? `Error de cliente: ${error.error.message}`
      : `Error del servidor (${error.status}): ${error.message}`;

    console.error('CustomerService error:', message);
    return throwError(() => new Error(message));
  }
}
