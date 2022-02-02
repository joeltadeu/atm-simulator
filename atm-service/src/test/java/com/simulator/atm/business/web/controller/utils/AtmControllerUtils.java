package com.simulator.atm.business.web.controller.utils;

import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;

public class AtmControllerUtils {

    public static RetryableException getRetryableException() {
        Request request = Request.create(Request.HttpMethod.GET, "url",
            new HashMap<>(), null, new RequestTemplate());

        return new RetryableException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Service Unavailable",
            Request.HttpMethod.GET, new Date(), request);
    }



    public static String getTransactionRequestJson() {
        return """
                {
                    "amount":500
                }
            """;
    }
}
