import { Injectable } from '@angular/core';
import * as SockJs from 'sockjs-client';
import { Client, Message, StompConfig } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { skip, switchMapTo, takeUntil } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root',
})
export class RealTimeNotificationsService {
  private messageClient: Client;
  private cancel$: Subject<void> = new Subject<void>();
  private authenticationService: AuthenticationService;

  private stompConfiguration: StompConfig = {
    webSocketFactory: () => new SockJs('/api/ws'),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    connectHeaders: {
      Authorization: `Bearer ${localStorage.getItem('jwt-user')}`,
    },
  };

  constructor() {
    this.messageClient = new Client(this.stompConfiguration);
  }

  public connect() {
    return new Observable((observer) => {
      this.messageClient.activate();

      this.messageClient.onStompError = (error) => observer.error(error.body);
      this.messageClient.onConnect = (connect) => {
        observer.next(connect.body);
      };

      return () => this.messageClient.deactivate();
    });
  }

  public disconnect() {
    this.cancel$.next();
    this.cancel$.complete();
  }

  public subscribe(url: string): Observable<string> {
    const subscription$ = new Observable<string>((observer) => {
      const subscription = this.messageClient.subscribe(url, (message) =>
        observer.next(message.body)
      );
      return () => subscription.unsubscribe();
    });

    if (!this.messageClient.connected) {
      return this.connect().pipe(
        switchMapTo(subscription$),
        takeUntil(this.cancel$.pipe(skip(1)))
      );
    }

    return subscription$;
  }
}
