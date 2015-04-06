package com.piggymetrics.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBody {

    private String state;
    private String message;

    public ResponseBody(String state, String message) {
        this.state = state;
        this.message = message;
    }

    public ResponseBody(String state) {
        this.state = state;
    }
}