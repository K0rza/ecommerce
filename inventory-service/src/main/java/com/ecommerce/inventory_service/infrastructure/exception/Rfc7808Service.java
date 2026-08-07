package com.ecommerce.inventory_service.infrastructure.exception;

import org.apache.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.inventory_service.application.exception.IllegalEventIdempotent;

@RestControllerAdvice
public class Rfc7808Service {

    @ExceptionHandler(IllegalEventIdempotent.class)    
    public ProblemDetail toIllegalEventIdempotent(IllegalEventIdempotent ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        pd.setTitle("IllegalEventIdempotent");
        pd.setProperty("event_id", ex.getEventId());
        return pd;
    }
}
