package com.simulator.atm.commons.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simulator.exception.handler.ControllerExceptionHandler;
import com.simulator.exception.model.ExceptionResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AtmControllerExceptionHandler extends ControllerExceptionHandler {
    @ExceptionHandler(FeignException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handlingFeignException(FeignException e) throws JsonProcessingException {
        return switch (e.status()) {
            case 400 -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ObjectMapper().readValue(e.contentUTF8(), ExceptionResponse.class));
            case 404 -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ObjectMapper().readValue(e.contentUTF8(), ExceptionResponse.class));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        };
    }
}
