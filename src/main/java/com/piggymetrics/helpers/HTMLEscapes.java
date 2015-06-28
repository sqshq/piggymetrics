package com.piggymetrics.helpers;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;

public class HTMLEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;

    public HTMLEscapes() {
        asciiEscapes = standardAsciiEscapesForJSON();
        asciiEscapes['/'] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_STANDARD;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int i) {
        return null;
    }
}