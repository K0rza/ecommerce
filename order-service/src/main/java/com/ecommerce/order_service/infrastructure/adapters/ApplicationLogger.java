package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.Logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationLogger implements Logger {
    
    @Override
    public void debug(String msg) { log.debug(msg); }

    @Override
    public void info(String msg) { log.info(msg); }

    @Override
    public void error(String msg) { log.error(msg); }
}
