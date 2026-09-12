package com.ecommerce.api_gateway.application.usecases;

import com.ecommerce.api_gateway.domain.user.Authentication;

public class AuthenticationUseCase {

    public void execute(String auth) {
        Authentication user = new Authentication(auth);
        System.out.println("The users' request recived " + user);
    }

}
