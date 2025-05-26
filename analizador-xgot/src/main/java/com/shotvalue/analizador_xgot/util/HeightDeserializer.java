package com.shotvalue.analizador_xgot.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class HeightDeserializer implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            return obj.has("name") ? obj.get("name").getAsString() : "Unknown";
        }
        return json.getAsString();
    }
}
