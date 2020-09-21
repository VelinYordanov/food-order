package com.github.velinyordanov.foodorder.data;

public interface FoodOrderData {
    AuthoritiesRepository authorities();

    CategoriesRepository categories();

    CustomersRepository customers();

    FoodsRepository foods();

    OrdersRepository orders();

    RestaurantsRepository restaurants();

    AddressesRepository addresses();

    DiscountCodesRepository discountCodes();
}
