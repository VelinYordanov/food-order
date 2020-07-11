import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  getItem(name:string) {
    return localStorage.getItem(name);
  }

  setItem(name:string, value:string) {
    localStorage.setItem(name, value);
  }

  removeItem(name:string) {
    localStorage.removeItem(name);
  }
}
