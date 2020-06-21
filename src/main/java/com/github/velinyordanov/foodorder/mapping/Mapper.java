package com.github.velinyordanov.foodorder.mapping;

public interface Mapper {
    <TDest> TDest map(Object source, Class<TDest> destination);
}
