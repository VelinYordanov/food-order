export interface Page<T> {
  content: T[];
  totalPages: Number;
  totalElements: Number;
  last: boolean;
  numberOfElements: number;
  first: boolean;
  number: number;
  size: number;
}
