import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../models/api-response.models';
import { ProductoResponse } from '../models/producto-response.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private readonly apiUrl = `${environment.apiUrl}/product`;

  constructor(private http: HttpClient) {}

    getAll(): Observable<ApiResponse<ProductoResponse[]>>{
      return this.http.get<ApiResponse<ProductoResponse[]>>(this.apiUrl).pipe(catchError(this.handleError));
    }

    getByIdWithPrice(id: number, almacenId?: number): Observable<ApiResponse<ProductoResponse>> {
      const url = `${this.apiUrl}/${id}/with-price`;
      const params: any = {};
      if (almacenId !== undefined && almacenId !== null) {
        params.almacenId = String(almacenId);
      }
      return this.http.get<ApiResponse<ProductoResponse>>(url, { params }).pipe(catchError(this.handleError));
    }

    private handleError(error: HttpErrorResponse) {
      const backendMessage = (error?.error && (error.error.message || error.error.error || error.error.detail)) as
            | string
            | undefined;
          const message = backendMessage
            ? backendMessage
            : error.error instanceof ErrorEvent
            ? `Error de cliente: ${error.error.message}`
            : `Error del servidor (${error.status}): ${error.message}`;
          console.error('ProductoService error:', message);
          return throwError(() => new Error(message));
    }

}
