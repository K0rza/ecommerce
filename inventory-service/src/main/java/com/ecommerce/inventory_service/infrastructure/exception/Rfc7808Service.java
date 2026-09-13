package com.ecommerce.inventory_service.infrastructure.exception;

import org.apache.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.inventory_service.application.exception.IllegalOrderCreateEventIdempotent;
import com.ecommerce.inventory_service.application.exception.IllegalProductCreateEventIdempotent;

@RestControllerAdvice
public class Rfc7808Service {

    @ExceptionHandler(IllegalProductCreateEventIdempotent.class)    
    public ProblemDetail toIllegalEventIdempotent(IllegalProductCreateEventIdempotent ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        pd.setTitle("IllegalEventIdempotent");
        pd.setProperty("event_id", ex.getEventId());
        return pd;
    }

    @ExceptionHandler(IllegalOrderCreateEventIdempotent.class)
    public ProblemDetail toIllegalOrderCreateEventIdempotent(IllegalOrderCreateEventIdempotent ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        pd.setTitle("IllegalOrderCreateEventIdempotent");
        pd.setProperty("event_id", ex.getEventId());
        return pd;
    }
}
