export interface AlertResult<T> {
    readonly isConfirmed: boolean;
    readonly isDismissed: boolean;
    readonly value?: T;
}