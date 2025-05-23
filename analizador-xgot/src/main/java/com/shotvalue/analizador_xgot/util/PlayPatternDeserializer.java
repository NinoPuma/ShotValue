package com.shotvalue.analizador_xgot.util;

import com.google.gson.*;
import com.shotvalue.analizador_xgot.model.PlayPattern;

import java.lang.reflect.Type;

public class PlayPatternDeserializer implements JsonDeserializer<PlayPattern> {

    @Override
    public PlayPattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        PlayPattern pattern = new PlayPattern();

        if (json.isJsonPrimitive()) {
            pattern.setName(json.getAsString());
        } else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("id")) {
                pattern.setId(obj.get("id").getAsInt());
            }
            if (obj.has("name")) {
                pattern.setName(obj.get("name").getAsString());
            }
        }

        return pattern;
    }
}
