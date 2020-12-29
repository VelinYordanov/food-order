export interface DiscountCodeDto {
  code: string;
  discountPercentage: number;
  validFrom: Date;
  validTo: Date;
  isSingleUse: boolean;
  isOncePerUser: boolean;
}
