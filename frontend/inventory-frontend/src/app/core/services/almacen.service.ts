import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { StorageRequest } from '../models/storage-request.model'; // ← Tus modelos
import { StorageResponse } from '../models/storage-response.model'; // ← Tus modelos

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private apiUrl = `${environment.apiUrl}/storage`;

  constructor(private http: HttpClient) { }

  createStorage(storage: StorageRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}`, storage).pipe(
      catchError(error => {
        console.error('Error creating storage:', error);
        throw error;
      })
    );
  }

  getStorages(): Observable<StorageResponse[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => {
        if (response && response.data && Array.isArray(response.data)) {
          return response.data;
        } else if (Array.isArray(response)) {
          return response;
        } else {
          console.warn('Formato inesperado para almacenes:', response);
          return [];
        }
      }),
      catchError(error => {
        console.error('Error en getStorages:', error);
        return of([]);
      })
    );
  }

  getStorage(id: number): Observable<StorageResponse> {
    return this.http.get<StorageResponse>(`${this.apiUrl}/${id}`);
  }

  updateStorage(id: number, storage: StorageRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, storage);
  }

  deleteStorage(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}