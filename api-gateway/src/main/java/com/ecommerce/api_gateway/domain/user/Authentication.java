package com.ecommerce.api_gateway.domain.user;

import com.ecommerce.api_gateway.domain.exception.UnauthorizedException;

public record Authentication(String authentication) {

    public Authentication {
        if(!"Bearer admin-secret-token".equals(authentication))
            throw new UnauthorizedException();
    }
}
