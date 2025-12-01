// src/app/core/services/almacen.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { StorageRequest } from '../models/storage-request.model';
import { StorageResponse } from '../models/storage-response.model';
import { ApiResponse } from '../models/api-response.models'; // el mismo que usas en proveedor

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private readonly baseUrl = `${environment.apiUrl}/storage`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ApiResponse<StorageResponse[]>> {
    return this.http.get<ApiResponse<StorageResponse[]>>(this.baseUrl)
      .pipe(catchError(this.handleError));
  }

  getById(id: number | string): Observable<ApiResponse<StorageResponse>> {
    return this.http.get<ApiResponse<StorageResponse>>(`${this.baseUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  createStorage(payload: StorageRequest): Observable<ApiResponse<StorageResponse>> {
    return this.http.post<ApiResponse<StorageResponse>>(this.baseUrl, payload)
      .pipe(catchError(this.handleError));
  }

  updateStorage(id: number | string, payload: StorageRequest): Observable<ApiResponse<StorageResponse>> {
    return this.http.put<ApiResponse<StorageResponse>>(`${this.baseUrl}/${id}`, payload)
      .pipe(catchError(this.handleError));
  }

  deleteStorage(id: number | string): Observable<ApiResponse<any>> {
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
    console.error('StorageService error:', message);
    return throwError(() => new Error(message));
  }
}
