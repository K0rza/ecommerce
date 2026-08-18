package com.ecommerce.api_gateway.domain.user;

import com.ecommerce.api_gateway.domain.exception.UnauthorizedException;

public record Authentication(String authentication) {

    public Authentication {
        if(authentication.isEmpty() || authentication != "Bearer admin-secret-token")
            throw new UnauthorizedException();
    }
}
