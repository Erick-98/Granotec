# 🎨 Ejemplos de Integración Frontend - Precio Promedio

## 📋 Índice
1. [React - Orden de Compra](#react---orden-de-compra)
2. [Vue.js - Selección de Productos](#vuejs---selección-de-productos)
3. [Angular - Servicio de Precios](#angular---servicio-de-precios)
4. [JavaScript Vanilla](#javascript-vanilla)

---

## React - Orden de Compra

### Componente Completo de Orden de Compra

```jsx
import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const OrdenCompraForm = () => {
  const [almacenes, setAlmacenes] = useState([]);
  const [proveedores, setProveedores] = useState([]);
  const [productos, setProductos] = useState([]);
  const [almacenSeleccionado, setAlmacenSeleccionado] = useState(null);
  
  const [formData, setFormData] = useState({
    numeroFactura: '',
    proveedorId: '',
    almacenId: '',
    fecha: new Date().toISOString().split('T')[0],
    detalles: []
  });

  useEffect(() => {
    cargarDatosIniciales();
  }, []);

  const cargarDatosIniciales = async () => {
    try {
      const [almacenesRes, proveedoresRes, productosRes] = await Promise.all([
        api.get('/storage'),
        api.get('/vendor'),
        api.get('/product?size=1000')
      ]);
      
      setAlmacenes(almacenesRes.data.data);
      setProveedores(proveedoresRes.data.data);
      setProductos(productosRes.data.data.content);
    } catch (error) {
      console.error('Error al cargar datos:', error);
    }
  };

  const agregarDetalle = () => {
    setFormData({
      ...formData,
      detalles: [...formData.detalles, {
        productoId: '',
        cantidadOrdenada: '',
        cantidadRecibida: '',
        precioUnitario: '',
        lote: '',
        fechaProduccion: '',
        fechaVencimiento: '',
        // Datos calculados
        precioPromedio: null,
        stockDisponible: null,
        productoNombre: ''
      }]
    });
  };

  const actualizarDetalle = (index, campo, valor) => {
    const nuevosDetalles = [...formData.detalles];
    nuevosDetalles[index][campo] = valor;
    setFormData({ ...formData, detalles: nuevosDetalles });
  };

  const cargarPrecioPromedio = async (index, productoId) => {
    if (!productoId || !formData.almacenId) return;

    try {
      const response = await api.get(
        `/product/${productoId}/precio-promedio?almacenId=${formData.almacenId}`
      );
      
      const data = response.data.data;
      const nuevosDetalles = [...formData.detalles];
      
      nuevosDetalles[index] = {
        ...nuevosDetalles[index],
        productoId: productoId,
        productoNombre: data.nombreProducto,
        precioUnitario: data.precioPromedioPonderado,
        precioPromedio: data.precioPromedioPonderado,
        stockDisponible: data.stockDisponible
      };
      
      setFormData({ ...formData, detalles: nuevosDetalles });
      
      if (data.stockDisponible === 0) {
        alert(`⚠️ ${data.mensaje}`);
      }
    } catch (error) {
      console.error('Error al cargar precio promedio:', error);
      alert('Error al calcular el precio promedio');
    }
  };

  const eliminarDetalle = (index) => {
    const nuevosDetalles = formData.detalles.filter((_, i) => i !== index);
    setFormData({ ...formData, detalles: nuevosDetalles });
  };

  const calcularSubtotal = (detalle) => {
    if (!detalle.cantidadOrdenada || !detalle.precioUnitario) return 0;
    return parseFloat(detalle.cantidadOrdenada) * parseFloat(detalle.precioUnitario);
  };

  const calcularTotal = () => {
    return formData.detalles.reduce((sum, detalle) => sum + calcularSubtotal(detalle), 0);
  };

  const validarFormulario = () => {
    if (!formData.numeroFactura) {
      alert('Debe ingresar el número de factura');
      return false;
    }
    
    if (!formData.proveedorId) {
      alert('Debe seleccionar un proveedor');
      return false;
    }
    
    if (!formData.almacenId) {
      alert('Debe seleccionar un almacén');
      return false;
    }
    
    if (formData.detalles.length === 0) {
      alert('Debe agregar al menos un producto');
      return false;
    }
    
    for (let i = 0; i < formData.detalles.length; i++) {
      const det = formData.detalles[i];
      
      if (!det.productoId) {
        alert(`Detalle ${i + 1}: Debe seleccionar un producto`);
        return false;
      }
      
      if (!det.cantidadOrdenada || det.cantidadOrdenada <= 0) {
        alert(`Detalle ${i + 1}: La cantidad ordenada debe ser mayor a 0`);
        return false;
      }
      
      if (!det.cantidadRecibida || det.cantidadRecibida < 0) {
        alert(`Detalle ${i + 1}: Debe ingresar la cantidad recibida`);
        return false;
      }
      
      if (parseFloat(det.cantidadRecibida) > parseFloat(det.cantidadOrdenada)) {
        alert(`Detalle ${i + 1}: La cantidad recibida no puede ser mayor a la ordenada`);
        return false;
      }
      
      if (!det.precioUnitario || det.precioUnitario <= 0) {
        alert(`Detalle ${i + 1}: El precio unitario debe ser mayor a 0`);
        return false;
      }
      
      if (!det.lote) {
        alert(`Detalle ${i + 1}: Debe ingresar el código de lote`);
        return false;
      }
    }
    
    return true;
  };

  const enviarFormulario = async (e) => {
    e.preventDefault();
    
    if (!validarFormulario()) return;

    try {
      const payload = {
        numeroFactura: formData.numeroFactura,
        proveedorId: parseInt(formData.proveedorId),
        almacenId: parseInt(formData.almacenId),
        fecha: formData.fecha,
        detalles: formData.detalles.map(det => ({
          productoId: parseInt(det.productoId),
          cantidadOrdenada: parseFloat(det.cantidadOrdenada),
          cantidadRecibida: parseFloat(det.cantidadRecibida),
          precioUnitario: parseFloat(det.precioUnitario),
          lote: det.lote,
          fechaProduccion: det.fechaProduccion || null,
          fechaVencimiento: det.fechaVencimiento || null
        }))
      };

      const response = await api.post('/orden-compra', payload);
      
      alert('✅ Orden de compra registrada exitosamente');
      console.log('Respuesta:', response.data);
      
      // Limpiar formulario
      setFormData({
        numeroFactura: '',
        proveedorId: '',
        almacenId: '',
        fecha: new Date().toISOString().split('T')[0],
        detalles: []
      });
      
      // Redireccionar o actualizar lista
      // window.location.href = '/ordenes-compra';
      
    } catch (error) {
      console.error('Error al registrar compra:', error);
      alert('❌ Error al registrar la orden de compra: ' + 
            (error.response?.data?.message || error.message));
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Nueva Orden de Compra</h1>
      
      <form onSubmit={enviarFormulario} className="bg-white shadow-md rounded px-8 pt-6 pb-8">
        {/* Encabezado */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Número de Factura *
            </label>
            <input
              type="text"
              className="shadow appearance-none border rounded w-full py-2 px-3"
              value={formData.numeroFactura}
              onChange={(e) => setFormData({...formData, numeroFactura: e.target.value})}
              placeholder="F001-00001234"
              required
            />
          </div>
          
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Proveedor *
            </label>
            <select
              className="shadow border rounded w-full py-2 px-3"
              value={formData.proveedorId}
              onChange={(e) => setFormData({...formData, proveedorId: e.target.value})}
              required
            >
              <option value="">Seleccione...</option>
              {proveedores.map(p => (
                <option key={p.id} value={p.id}>{p.razonSocial}</option>
              ))}
            </select>
          </div>
          
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Almacén *
            </label>
            <select
              className="shadow border rounded w-full py-2 px-3"
              value={formData.almacenId}
              onChange={(e) => {
                setFormData({...formData, almacenId: e.target.value});
                setAlmacenSeleccionado(e.target.value);
              }}
              required
            >
              <option value="">Seleccione...</option>
              {almacenes.map(a => (
                <option key={a.id} value={a.id}>{a.nombre}</option>
              ))}
            </select>
          </div>
          
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Fecha
            </label>
            <input
              type="date"
              className="shadow border rounded w-full py-2 px-3"
              value={formData.fecha}
              onChange={(e) => setFormData({...formData, fecha: e.target.value})}
            />
          </div>
        </div>

        {/* Detalles */}
        <div className="mb-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold">Detalles de la Compra</h2>
            <button
              type="button"
              onClick={agregarDetalle}
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
              disabled={!formData.almacenId}
            >
              + Agregar Producto
            </button>
          </div>

          {!formData.almacenId && (
            <div className="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 mb-4">
              <p>⚠️ Primero seleccione un almacén para agregar productos</p>
            </div>
          )}

          <div className="overflow-x-auto">
            {formData.detalles.map((detalle, index) => (
              <div key={index} className="border rounded p-4 mb-4 bg-gray-50">
                <div className="flex justify-between items-start mb-3">
                  <h3 className="font-bold">Producto {index + 1}</h3>
                  <button
                    type="button"
                    onClick={() => eliminarDetalle(index)}
                    className="text-red-500 hover:text-red-700"
                  >
                    ✕ Eliminar
                  </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="md:col-span-3">
                    <label className="block text-sm font-bold mb-2">Producto *</label>
                    <select
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.productoId}
                      onChange={(e) => {
                        actualizarDetalle(index, 'productoId', e.target.value);
                        cargarPrecioPromedio(index, e.target.value);
                      }}
                      required
                    >
                      <option value="">Seleccione un producto...</option>
                      {productos.map(p => (
                        <option key={p.id} value={p.id}>
                          {p.code} - {p.name}
                        </option>
                      ))}
                    </select>
                  </div>

                  {detalle.precioPromedio !== null && (
                    <div className="md:col-span-3 bg-blue-50 border-l-4 border-blue-500 p-3">
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div>
                          <span className="font-semibold">Precio Promedio Actual:</span>
                          <span className="ml-2">S/ {parseFloat(detalle.precioPromedio).toFixed(2)}</span>
                        </div>
                        <div>
                          <span className="font-semibold">Stock Disponible:</span>
                          <span className="ml-2">{detalle.stockDisponible} unidades</span>
                        </div>
                      </div>
                    </div>
                  )}

                  <div>
                    <label className="block text-sm font-bold mb-2">Cantidad Ordenada *</label>
                    <input
                      type="number"
                      step="0.001"
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.cantidadOrdenada}
                      onChange={(e) => actualizarDetalle(index, 'cantidadOrdenada', e.target.value)}
                      placeholder="0.000"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-bold mb-2">Cantidad Recibida *</label>
                    <input
                      type="number"
                      step="0.001"
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.cantidadRecibida}
                      onChange={(e) => actualizarDetalle(index, 'cantidadRecibida', e.target.value)}
                      placeholder="0.000"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-bold mb-2">Precio Unitario *</label>
                    <input
                      type="number"
                      step="0.01"
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.precioUnitario}
                      onChange={(e) => actualizarDetalle(index, 'precioUnitario', e.target.value)}
                      placeholder="0.00"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-bold mb-2">Código de Lote *</label>
                    <input
                      type="text"
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.lote}
                      onChange={(e) => actualizarDetalle(index, 'lote', e.target.value)}
                      placeholder="L001-2025"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-bold mb-2">Fecha Producción</label>
                    <input
                      type="date"
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.fechaProduccion}
                      onChange={(e) => actualizarDetalle(index, 'fechaProduccion', e.target.value)}
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-bold mb-2">Fecha Vencimiento</label>
                    <input
                      type="date"
                      className="shadow border rounded w-full py-2 px-3"
                      value={detalle.fechaVencimiento}
                      onChange={(e) => actualizarDetalle(index, 'fechaVencimiento', e.target.value)}
                    />
                  </div>

                  <div className="md:col-span-3 bg-gray-100 p-3 rounded">
                    <span className="font-bold">Subtotal: </span>
                    <span className="text-xl">S/ {calcularSubtotal(detalle).toFixed(2)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Total */}
        {formData.detalles.length > 0 && (
          <div className="bg-green-50 border-l-4 border-green-500 p-4 mb-6">
            <div className="flex justify-between items-center">
              <span className="text-xl font-bold">TOTAL:</span>
              <span className="text-3xl font-bold text-green-700">
                S/ {calcularTotal().toFixed(2)}
              </span>
            </div>
          </div>
        )}

        {/* Botones */}
        <div className="flex justify-end gap-4">
          <button
            type="button"
            onClick={() => window.history.back()}
            className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
            disabled={formData.detalles.length === 0}
          >
            💾 Registrar Orden de Compra
          </button>
        </div>
      </form>
    </div>
  );
};

export default OrdenCompraForm;
```

---

## Vue.js - Selección de Productos

```vue
<template>
  <div class="producto-selector">
    <h3>Seleccionar Producto</h3>
    
    <div class="form-group">
      <label>Almacén:</label>
      <select v-model="almacenId" @change="onAlmacenChange">
        <option value="">Seleccione almacén...</option>
        <option v-for="almacen in almacenes" :key="almacen.id" :value="almacen.id">
          {{ almacen.nombre }}
        </option>
      </select>
    </div>

    <div class="form-group">
      <label>Buscar Producto:</label>
      <input 
        v-model="searchQuery" 
        @input="buscarProductos"
        placeholder="Código o nombre..."
      />
    </div>

    <div v-if="loading" class="loading">Cargando...</div>

    <div v-else class="productos-lista">
      <div 
        v-for="producto in productosConPrecio" 
        :key="producto.id"
        class="producto-card"
        @click="seleccionarProducto(producto)"
      >
        <div class="producto-info">
          <h4>{{ producto.code }} - {{ producto.name }}</h4>
          <p class="descripcion">{{ producto.description }}</p>
        </div>
        
        <div class="producto-precio">
          <div class="precio-promedio">
            <span class="label">Precio Promedio:</span>
            <span class="valor">S/ {{ producto.precioPromedio?.toFixed(2) || '0.00' }}</span>
          </div>
          <div class="stock">
            <span class="label">Stock:</span>
            <span class="valor" :class="{ 'bajo': producto.stock < 10 }">
              {{ producto.stock || 0 }} unidades
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue';
import api from '@/services/api';

export default {
  name: 'ProductoSelector',
  props: {
    almacenIdProp: {
      type: Number,
      default: null
    }
  },
  emits: ['producto-seleccionado'],
  setup(props, { emit }) {
    const almacenId = ref(props.almacenIdProp);
    const almacenes = ref([]);
    const productos = ref([]);
    const productosConPrecio = ref([]);
    const searchQuery = ref('');
    const loading = ref(false);

    const cargarAlmacenes = async () => {
      try {
        const response = await api.get('/storage');
        almacenes.value = response.data.data;
      } catch (error) {
        console.error('Error al cargar almacenes:', error);
      }
    };

    const buscarProductos = async () => {
      if (!almacenId.value) {
        alert('Primero seleccione un almacén');
        return;
      }

      loading.value = true;
      try {
        const response = await api.get(`/product?q=${searchQuery.value}&size=20`);
        productos.value = response.data.data.content;
        
        await cargarPreciosPromedio();
      } catch (error) {
        console.error('Error al buscar productos:', error);
      } finally {
        loading.value = false;
      }
    };

    const cargarPreciosPromedio = async () => {
      const promises = productos.value.map(async (producto) => {
        try {
          const response = await api.get(
            `/product/${producto.id}/precio-promedio?almacenId=${almacenId.value}`
          );
          
          return {
            ...producto,
            precioPromedio: response.data.data.precioPromedioPonderado,
            stock: response.data.data.stockDisponible
          };
        } catch (error) {
          console.error(`Error al cargar precio de producto ${producto.id}:`, error);
          return {
            ...producto,
            precioPromedio: 0,
            stock: 0
          };
        }
      });

      productosConPrecio.value = await Promise.all(promises);
    };

    const onAlmacenChange = () => {
      productosConPrecio.value = [];
      searchQuery.value = '';
    };

    const seleccionarProducto = (producto) => {
      emit('producto-seleccionado', producto);
    };

    watch(() => props.almacenIdProp, (newVal) => {
      almacenId.value = newVal;
    });

    // Cargar almacenes al montar
    cargarAlmacenes();

    return {
      almacenId,
      almacenes,
      productosConPrecio,
      searchQuery,
      loading,
      buscarProductos,
      onAlmacenChange,
      seleccionarProducto
    };
  }
};
</script>

<style scoped>
.producto-selector {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.productos-lista {
  display: grid;
  gap: 15px;
  margin-top: 20px;
}

.producto-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.producto-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  border-color: #4CAF50;
}

.producto-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.descripcion {
  color: #666;
  font-size: 0.9em;
  margin: 5px 0;
}

.producto-precio {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.precio-promedio,
.stock {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 0.85em;
  color: #666;
}

.valor {
  font-size: 1.1em;
  font-weight: bold;
  color: #4CAF50;
}

.valor.bajo {
  color: #ff9800;
}
</style>
```

---

## Angular - Servicio de Precios

```typescript
// precio.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, shareReplay } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface ProductPriceResponse {
  productoId: number;
  nombreProducto: string;
  almacenId: number;
  nombreAlmacen: string;
  precioPromedioPonderado: number;
  stockDisponible: number;
  mensaje?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PrecioService {
  private apiUrl = `${environment.apiUrl}/product`;
  private cache = new Map<string, Observable<ProductPriceResponse>>();

  constructor(private http: HttpClient) {}

  /**
   * Obtiene el precio promedio ponderado de un producto
   * Con caché para evitar llamadas repetidas
   */
  getPrecioPromedio(
    productoId: number, 
    almacenId?: number,
    useCache: boolean = true
  ): Observable<ProductPriceResponse> {
    const cacheKey = `${productoId}-${almacenId || 'all'}`;

    if (useCache && this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey)!;
    }

    let params = new HttpParams();
    if (almacenId) {
      params = params.set('almacenId', almacenId.toString());
    }

    const request$ = this.http.get<any>(
      `${this.apiUrl}/${productoId}/precio-promedio`,
      { params }
    ).pipe(
      map(response => response.data),
      shareReplay(1), // Cache por 1 minuto
      catchError(error => {
        console.error('Error al obtener precio promedio:', error);
        return of({
          productoId,
          nombreProducto: '',
          almacenId: almacenId || 0,
          nombreAlmacen: '',
          precioPromedioPonderado: 0,
          stockDisponible: 0,
          mensaje: 'Error al calcular precio'
        });
      })
    );

    this.cache.set(cacheKey, request$);

    // Limpiar caché después de 1 minuto
    setTimeout(() => {
      this.cache.delete(cacheKey);
    }, 60000);

    return request$;
  }

  /**
   * Obtiene producto con precio incluido
   */
  getProductoConPrecio(productoId: number, almacenId?: number): Observable<any> {
    let params = new HttpParams();
    if (almacenId) {
      params = params.set('almacenId', almacenId.toString());
    }

    return this.http.get<any>(
      `${this.apiUrl}/${productoId}/with-price`,
      { params }
    ).pipe(
      map(response => response.data)
    );
  }

  /**
   * Invalida el caché de precios
   */
  invalidarCache(productoId?: number, almacenId?: number): void {
    if (productoId && almacenId) {
      const cacheKey = `${productoId}-${almacenId}`;
      this.cache.delete(cacheKey);
    } else {
      this.cache.clear();
    }
  }

  /**
   * Obtiene precios de múltiples productos en paralelo
   */
  getPreciosMultiples(
    productosIds: number[], 
    almacenId: number
  ): Observable<ProductPriceResponse[]> {
    const requests = productosIds.map(id => 
      this.getPrecioPromedio(id, almacenId)
    );

    return new Observable(observer => {
      Promise.all(requests.map(req => req.toPromise()))
        .then(results => {
          observer.next(results as ProductPriceResponse[]);
          observer.complete();
        })
        .catch(error => observer.error(error));
    });
  }
}
```

```typescript
// orden-compra.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { PrecioService } from '../services/precio.service';

@Component({
  selector: 'app-orden-compra',
  templateUrl: './orden-compra.component.html',
  styleUrls: ['./orden-compra.component.css']
})
export class OrdenCompraComponent implements OnInit {
  ordenForm: FormGroup;
  almacenes: any[] = [];
  proveedores: any[] = [];
  productos: any[] = [];
  loading = false;

  constructor(
    private fb: FormBuilder,
    private precioService: PrecioService
  ) {
    this.ordenForm = this.fb.group({
      numeroFactura: ['', Validators.required],
      proveedorId: ['', Validators.required],
      almacenId: ['', Validators.required],
      fecha: [new Date().toISOString().split('T')[0]],
      detalles: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  get detalles(): FormArray {
    return this.ordenForm.get('detalles') as FormArray;
  }

  crearDetalleFormGroup(): FormGroup {
    return this.fb.group({
      productoId: ['', Validators.required],
      productoNombre: [''],
      cantidadOrdenada: ['', [Validators.required, Validators.min(0.001)]],
      cantidadRecibida: ['', [Validators.required, Validators.min(0)]],
      precioUnitario: ['', [Validators.required, Validators.min(0.01)]],
      lote: ['', Validators.required],
      fechaProduccion: [''],
      fechaVencimiento: [''],
      // Campos calculados
      precioPromedio: [null],
      stockDisponible: [null],
      subtotal: [0]
    });
  }

  agregarDetalle(): void {
    this.detalles.push(this.crearDetalleFormGroup());
  }

  eliminarDetalle(index: number): void {
    this.detalles.removeAt(index);
  }

  onProductoChange(index: number): void {
    const detalle = this.detalles.at(index);
    const productoId = detalle.get('productoId')?.value;
    const almacenId = this.ordenForm.get('almacenId')?.value;

    if (!productoId || !almacenId) return;

    this.loading = true;

    this.precioService.getPrecioPromedio(productoId, almacenId)
      .subscribe({
        next: (data) => {
          detalle.patchValue({
            productoNombre: data.nombreProducto,
            precioUnitario: data.precioPromedioPonderado,
            precioPromedio: data.precioPromedioPonderado,
            stockDisponible: data.stockDisponible
          });

          if (data.stockDisponible === 0) {
            alert(`⚠️ ${data.mensaje}`);
          }

          this.loading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.loading = false;
        }
      });
  }

  calcularSubtotal(detalle: FormGroup): number {
    const cantidad = detalle.get('cantidadOrdenada')?.value || 0;
    const precio = detalle.get('precioUnitario')?.value || 0;
    const subtotal = cantidad * precio;
    detalle.patchValue({ subtotal }, { emitEvent: false });
    return subtotal;
  }

  calcularTotal(): number {
    return this.detalles.controls.reduce((sum, detalle) => {
      return sum + this.calcularSubtotal(detalle as FormGroup);
    }, 0);
  }

  async cargarDatosIniciales(): Promise<void> {
    // Implementar carga de almacenes, proveedores y productos
  }

  onSubmit(): void {
    if (this.ordenForm.invalid) {
      alert('Por favor complete todos los campos requeridos');
      return;
    }

    const payload = {
      ...this.ordenForm.value,
      detalles: this.detalles.value.map((det: any) => ({
        productoId: parseInt(det.productoId),
        cantidadOrdenada: parseFloat(det.cantidadOrdenada),
        cantidadRecibida: parseFloat(det.cantidadRecibida),
        precioUnitario: parseFloat(det.precioUnitario),
        lote: det.lote,
        fechaProduccion: det.fechaProduccion || null,
        fechaVencimiento: det.fechaVencimiento || null
      }))
    };

    // Enviar al backend
    console.log('Payload:', payload);
  }
}
```

---

## JavaScript Vanilla

```javascript
// precio-service.js
class PrecioService {
  constructor(baseURL, token) {
    this.baseURL = baseURL;
    this.token = token;
    this.cache = new Map();
  }

  async getPrecioPromedio(productoId, almacenId = null, useCache = true) {
    const cacheKey = `${productoId}-${almacenId || 'all'}`;

    if (useCache && this.cache.has(cacheKey)) {
      const cached = this.cache.get(cacheKey);
      if (Date.now() - cached.timestamp < 60000) { // 1 minuto
        return cached.data;
      }
    }

    try {
      const url = new URL(`${this.baseURL}/product/${productoId}/precio-promedio`);
      if (almacenId) {
        url.searchParams.append('almacenId', almacenId);
      }

      const response = await fetch(url, {
        headers: {
          'Authorization': `Bearer ${this.token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      const data = result.data;

      this.cache.set(cacheKey, {
        data,
        timestamp: Date.now()
      });

      return data;
    } catch (error) {
      console.error('Error al obtener precio promedio:', error);
      return {
        productoId,
        nombreProducto: '',
        precioPromedioPonderado: 0,
        stockDisponible: 0,
        mensaje: 'Error al calcular precio'
      };
    }
  }

  invalidarCache(productoId = null, almacenId = null) {
    if (productoId && almacenId) {
      this.cache.delete(`${productoId}-${almacenId}`);
    } else {
      this.cache.clear();
    }
  }
}

// Uso
const precioService = new PrecioService('http://localhost:8080/api', 'tu-token');

// Ejemplo de uso en formulario
document.getElementById('productoSelect').addEventListener('change', async function(e) {
  const productoId = e.target.value;
  const almacenId = document.getElementById('almacenSelect').value;

  if (!productoId || !almacenId) return;

  try {
    const data = await precioService.getPrecioPromedio(productoId, almacenId);

    // Actualizar campos
    document.getElementById('precioUnitario').value = data.precioPromedioPonderado.toFixed(2);
    document.getElementById('stockDisponible').textContent = data.stockDisponible;
    document.getElementById('precioPromedioInfo').textContent = 
      `Precio Promedio: S/ ${data.precioPromedioPonderado.toFixed(2)}`;

    if (data.stockDisponible === 0) {
      alert(`⚠️ ${data.mensaje}`);
    }
  } catch (error) {
    console.error('Error:', error);
    alert('Error al calcular el precio promedio');
  }
});
```

---

## 🎯 Resumen

Todos estos ejemplos muestran cómo integrar el sistema de precio promedio ponderado en diferentes frameworks de frontend. Las características clave son:

1. ✅ Carga automática del precio promedio al seleccionar producto
2. ✅ Caché para evitar llamadas repetidas
3. ✅ Validación de stock disponible
4. ✅ Feedback visual al usuario
5. ✅ Manejo de errores robusto

**¡Ya estás listo para integrar el precio promedio en tu frontend!** 🚀

