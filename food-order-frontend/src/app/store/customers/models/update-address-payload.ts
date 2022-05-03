import { Address } from "src/app/customers/models/address";

export interface UpdateAddressPayload {
    userId: string,
    addressId: string,
    address: Address
}