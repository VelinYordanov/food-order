import { DiscountCodeDto } from "src/app/restaurants/models/discount-code-dto";

export interface CreateDiscountCodePayload {
    restaurantId: string;
    discountCode: DiscountCodeDto
}