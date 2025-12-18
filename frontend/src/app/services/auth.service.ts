import { Injectable } from '@angular/core';

@Injectable({providedIn: 'root'})
export class AuthService {
  private TOKEN_KEY = 'APP_TOKEN';

  setToken(token: string){ localStorage.setItem(this.TOKEN_KEY, token); }
  getToken(){ return localStorage.getItem(this.TOKEN_KEY); }
  clear(){ localStorage.removeItem(this.TOKEN_KEY); }
}
