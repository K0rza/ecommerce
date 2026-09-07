package com.ecommerce.order_service.application.port;

public interface Logger {

    void debug(String msg);

    void info(String msg);

    void error(String msg);
}
