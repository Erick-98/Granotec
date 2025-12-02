import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CompraRequest } from '../models/compra-request.model';
import { CompraResponse } from '../models/compra-response.model';

@Injectable({
  providedIn: 'root',
})
export class CompraService {
  private readonly baseUrl = `${environment.apiUrl}/compras`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<CompraResponse[]> {
    return this.http.get<CompraResponse[]>(this.baseUrl).pipe(catchError(this.handleError));
  }

  getById(id: number | string): Observable<CompraResponse> {
    return this.http.get<CompraResponse>(`${this.baseUrl}/${id}`).pipe(catchError(this.handleError));
  }

  create(payload: CompraRequest): Observable<CompraResponse> {
    return this.http.post<CompraResponse>(this.baseUrl, payload).pipe(catchError(this.handleError));
  }

  update(id: number | string, payload: CompraRequest): Observable<CompraResponse> {
    return this.http.put<CompraResponse>(`${this.baseUrl}/${id}`, payload).pipe(catchError(this.handleError));
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(catchError(this.handleError));
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
    console.error('CompraService error:', message);
    return throwError(() => new Error(message));
  }
}
