package com.github.velinyordanov.foodorder.services;

import com.github.velinyordanov.foodorder.data.entities.BaseUser;
import com.github.velinyordanov.foodorder.dto.JwtUserDto;

public interface JwtTokenService {
    JwtUserDto parseToken(String token);

    String generateToken(BaseUser user);
}
