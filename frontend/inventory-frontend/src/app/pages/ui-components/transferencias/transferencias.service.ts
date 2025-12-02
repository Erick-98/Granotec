import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface TransferenciaRequest {
  almacenOrigenId: number;
  almacenDestinoId: number;
  productoId: number;
  usuarioId: number;
  motivo: string;
  lotes?: LoteTransferenciaDTO[];
  cantidad?: number;
}

export interface LoteTransferenciaDTO {
  loteId: number;
  cantidad: number;
}

export interface ProductoDisponible {
  productoId: number;
  productoNombre: string;
  productoCodigo: string;
  cantidadDisponible: number;
}

export interface LoteDisponible {
  loteId: number;
  codigoLote: string;
  cantidadDisponible: number;
  fechaProduccion: string;
  fechaVencimiento: string;
  costoUnitario: number;
}

export interface Almacen {
  id: number;
  nombre: string;
  descripcion?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransferenciasService {
  private baseUrl = `${environment.apiUrl}/inventario`;

  constructor(private http: HttpClient) {}

  getAlmacenes(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/storage`);
  }

  getProductosDisponibles(almacenId: number): Observable<ProductoDisponible[]> {
    return this.http.get<any>(`${this.baseUrl}/almacenes/${almacenId}/productos`).pipe(
      map(response => response.data || [])
    );
  }

  getLotesDisponibles(almacenId: number, productoId: number): Observable<LoteDisponible[]> {
    return this.http.get<any>(`${this.baseUrl}/almacenes/${almacenId}/productos/${productoId}/lotes`).pipe(
      map(response => response.data || [])
    );
  }

  realizarTransferencia(request: TransferenciaRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/transferencias`, request);
  }
}
