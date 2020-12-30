export interface DiscountCodeEdit {
  discountPercentage: number;
  validFrom: Date;
  validTo: Date;
  isSingleUse: boolean;
  isOncePerUser: boolean;
}
