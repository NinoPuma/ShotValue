package com.shotvalue.analizador_xgot.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue(); // ✅ evitar NullPointerException
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String text = in.nextString();
        return (text == null || text.isEmpty()) ? null : LocalDate.parse(text);
    }
}
