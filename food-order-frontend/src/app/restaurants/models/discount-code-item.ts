export interface DiscountCodeItem {
  id: string;
  code: string;
  discountPercentage: number;
  validFrom: Date;
  validTo: Date;
  isSingleUse: boolean;
  isOncePerUser: boolean;
  timesUsed: number;
}
