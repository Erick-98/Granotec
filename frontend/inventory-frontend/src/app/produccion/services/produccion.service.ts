import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProduccionService {

  private api = 'http://localhost:8080/produccion';

  constructor(private http: HttpClient) {}

  listarOrdenes() {
    return this.http.get<any[]>(`${this.api}/ordenes`);
  }

  obtenerOrden(id: number) {
    return this.http.get<any>(`${this.api}/ordenes/${id}`);
  }

  crearOrden(data: any) {
    return this.http.post(`${this.api}/ordenes`, data);
  }

  iniciarOrden(id: number, data: any = {}) {
    return this.http.post(`${this.api}/ordenes/${id}/iniciar`, data);
  }

  registrarConsumo(id: number, data: any) {
    return this.http.post(`${this.api}/ordenes/${id}/consumos`, data);
  }

  aprobarLaboratorio(id: number, data: any) {
    return this.http.post(`${this.api}/ordenes/${id}/laboratorio`, data);
  }

  cerrarOrden(id: number, data: any) {
    return this.http.post(`${this.api}/ordenes/${id}/cerrar`, data);
  }
}
