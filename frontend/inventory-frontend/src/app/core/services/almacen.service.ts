import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { StorageResponse } from '../models/storage-response.model';
import { ApiResponse } from '../models/api-response.models';
import { StorageRequest } from '../models/storage-request.model';

@Injectable({
  providedIn: 'root'
})
export class AlmacenService {

  private readonly baseUrl = `${environment.apiUrl}/storage`;
  constructor(private http: HttpClient) { }


  getAll(): Observable<ApiResponse<StorageResponse[]>>{
    return this.http.get<ApiResponse<StorageResponse[]>>(this.baseUrl)
    .pipe(catchError(this.handleError));
  }

  getById(id: number | string): Observable<ApiResponse<StorageResponse>>{
  
    return this.http.get<ApiResponse<StorageResponse>>(`${this.baseUrl}/${id}`)
    .pipe(catchError(this.handleError));
  }


  create(payload: StorageRequest): Observable<ApiResponse<StorageResponse>>{
    return this.http.post<ApiResponse<StorageResponse>>(this.baseUrl, payload)
    .pipe(catchError(this.handleError));
  }


  update(id: number | string, payload: StorageRequest): Observable<ApiResponse<StorageResponse>>{
    return this.http.put<ApiResponse<StorageResponse>>(`${this.baseUrl}/${id}`, payload)
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
    console.error('StorageService error:', message);
    return throwError(() => new Error(message));
  }











}
