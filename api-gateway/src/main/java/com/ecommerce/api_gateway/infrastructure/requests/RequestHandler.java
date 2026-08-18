package com.ecommerce.api_gateway.infrastructure.requests;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ecommerce.api_gateway.application.usecase.AuthenticationUseCase;
import com.ecommerce.api_gateway.domain.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestHandler implements HandlerInterceptor {

    private final AuthenticationUseCase usecase;

    public RequestHandler(AuthenticationUseCase usecase) {
        this.usecase = usecase;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("PreHandle is called");
        try {
            usecase.execute(request.getHeader("Authorization"));
            return true;
        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return false;
        }
    }
    

}
