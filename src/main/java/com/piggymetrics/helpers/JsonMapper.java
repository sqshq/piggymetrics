package com.piggymetrics.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonMapper extends ObjectMapper {

    public JsonMapper() {
        this.getFactory().setCharacterEscapes(new HTMLEscapes());
    }

}
