package com.ecommerce.api_gateway.domain.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized users' request received.");
    }

    
}
