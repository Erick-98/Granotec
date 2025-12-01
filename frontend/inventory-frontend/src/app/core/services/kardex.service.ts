import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KardexItem, KardexFilter, KardexSummary, KardexPageResponse } from '../models/kardex.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KardexService {
  private apiUrl = `${environment.apiUrl}/kardex`;

  constructor(private http: HttpClient) { }

  /**
   * Búsqueda general de movimientos de kardex con filtros
   */
  searchKardex(filters: {
    productoId?: number;
    almacenId?: number;
    desde?: string;
    hasta?: string;
    page?: number;
    size?: number;
  }): Observable<any> {
    let params = new HttpParams();
    
    if (filters.productoId) {
      params = params.set('productoId', filters.productoId.toString());
    }
    if (filters.almacenId) {
      params = params.set('almacenId', filters.almacenId.toString());
    }
    if (filters.desde) {
      params = params.set('desde', filters.desde);
    }
    if (filters.hasta) {
      params = params.set('hasta', filters.hasta);
    }
    if (filters.page !== undefined) {
      params = params.set('page', filters.page.toString());
    }
    if (filters.size !== undefined) {
      params = params.set('size', filters.size.toString());
    }

    return this.http.get<any>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Obtener movimientos por producto
   */
  getMovimientosPorProducto(productoId: number, page: number = 0, size: number = 20): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(`${this.apiUrl}/producto/${productoId}`, { params });
  }

  /**
   * Obtener movimientos por lote
   */
  getMovimientosPorLote(loteId: number, page: number = 0, size: number = 20): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(`${this.apiUrl}/lote/${loteId}`, { params });
  }

  /**
   * Obtener movimientos por almacén
   */
  getMovimientosPorAlmacen(almacenId: number, page: number = 0, size: number = 20): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(`${this.apiUrl}/almacen/${almacenId}`, { params });
  }
}