import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Employee { id?: number; firstName: string; lastName?: string; email?: string; position?: string }

@Injectable({providedIn: 'root'})
export class EmployeeService {
  private base = '/api/employees';
  constructor(private http: HttpClient) {}
  list(): Observable<Employee[]> { return this.http.get<Employee[]>(this.base); }
  get(id: number){ return this.http.get<Employee>(`${this.base}/${id}`); }
  create(e: Employee){ return this.http.post<Employee>(this.base, e); }
  update(id: number, e: Employee){ return this.http.put<Employee>(`${this.base}/${id}`, e); }
  delete(id: number){ return this.http.delete<void>(`${this.base}/${id}`); }
}
