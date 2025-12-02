import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../models/api-response.models';
import { ProveedorResponse } from '../models/proveedor-response.model';
import { ProveedorRequest } from '../models/proveedor-request.model';

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {
  
  private readonly baseUrl = `${environment.apiUrl}/vendor`;
  constructor(private http: HttpClient) { }

  getAll(): Observable<ApiResponse<ProveedorResponse[]>>{
    return this.http.get<ApiResponse<ProveedorResponse[]>>(this.baseUrl)
    .pipe(catchError(this.handleError));
  }

  getById(id: number | string): Observable<ApiResponse<ProveedorResponse>>{
    return this.http.get<ApiResponse<ProveedorResponse>>(`${this.baseUrl}/${id}`)
    .pipe(catchError(this.handleError));
  }

  create(payload: ProveedorRequest): Observable<ApiResponse<ProveedorResponse>>{
    return this.http.post<ApiResponse<ProveedorResponse>>(this.baseUrl, payload) 
    .pipe(catchError(this.handleError));
  }

  update(id: number | string, payload: ProveedorRequest): Observable<ApiResponse<ProveedorResponse>>{
    return this.http.put<ApiResponse<ProveedorResponse>>(`${this.baseUrl}/${id}`, payload)
    .pipe(catchError(this.handleError));
  }

  delete(id: number | string): Observable<ApiResponse<any>>{
    return this.http.delete<ApiResponse<any>>(`${this.baseUrl}/${id}`)
    .pipe(catchError(this.handleError));
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
      console.error('ProveedorService error:', message);
      return throwError(() => new Error(message));
    }
  


}
