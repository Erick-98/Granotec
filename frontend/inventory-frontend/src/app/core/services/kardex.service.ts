import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KardexItem, KardexFilter, KardexSummary } from '../models/kardex.model';

@Injectable({
providedIn: 'root'
})
export class KardexService {
private apiUrl = 'http://localhost:8080/api/kardex';

constructor(private http: HttpClient) { }


}