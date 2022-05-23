export interface PrompPayload<T> {
    promptQuestion: string;
    successText: string;
    errorText: string;
    data: T
}