import { AbstractControl, ValidatorFn } from "@angular/forms";

export function price(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const price = Number(control.value);
        if (!price) {
            return { 'price': { value: control.value } };
        }

        return price > 0.01 ? null : { 'price': { value: control.value } };
    };
}