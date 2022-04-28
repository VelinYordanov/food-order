import { Address } from "../../models/address";

export interface UpdateAddressPayload {
    userId: string,
    addressId: string,
    address: Address
}