package com.ecommerce.product_service.infrastructure.exception;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties.Problemdetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.product_service.domain.exception.InvalidPriceException;
import com.ecommerce.product_service.domain.exception.InvalidSkuException;
import com.ecommerce.product_service.domain.exception.InvalidVersionException;

@RestControllerAdvice
public class Rfc7807Factory {

    @ExceptionHandler(InvalidPriceException.class)
    public ProblemDetail toInvalidPrice(InvalidPriceException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problemDetail.setTitle("Invalid Price Argument");
        problemDetail.setDetail(String.format(
            "%s, recevied price: %s", exception.getMessage(), exception.getInvalidValue()));
            
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/invalid-price"));
        
        problemDetail.setProperty("invalid_value", exception.getInvalidValue());
        problemDetail.setStatus(422);

        return problemDetail;
    }

    @ExceptionHandler(InvalidSkuException.class)
    public ProblemDetail toInvalidSku(InvalidSkuException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problemDetail.setTitle("Invalid Sku Argument");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setStatus(422);
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/invalid-sku"));

        return problemDetail;
    }

    @ExceptionHandler(InvalidVersionException.class) 
    public ProblemDetail toInvalidVersion(InvalidVersionException ex) {
        ProblemDetail pb = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pb.setDetail(ex.getMessage());
        pb.setTitle("Invalid Version Argument");

        return pb;
    }

    /*
    @ExceptionHandler(Exception.class) 
    public ProblemDetail toGeneralException(Exception e) {
        ProblemDetail pb = ProblemDetail.forStatus(500);

        String traceId = UUID.randomUUID().toString();
        System.out.println(String.format("The StackTrace for traceId: {} is {}", traceId, e));

        pb.setDetail("Server-in problem is occured. TraceId: " + traceId);
        pb.setTitle("Server-in Error");
        pb.setType(URI.create("https://api.ecommerce.com/errors/internal-error"));
        pb.setProperty("trace_id", traceId);

        return pb;
    }
        */

}
