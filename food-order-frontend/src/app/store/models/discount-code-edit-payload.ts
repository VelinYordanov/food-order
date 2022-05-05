import { DiscountCodeEdit } from "src/app/restaurants/models/discount-code-edit";

export interface DiscountCodeEditPayload {
    restaurantId: string,
    discountCodeId: string,
    discountCode: DiscountCodeEdit
}