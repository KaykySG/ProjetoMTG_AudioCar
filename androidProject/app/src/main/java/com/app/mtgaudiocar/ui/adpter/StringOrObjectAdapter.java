package com.app.mtgaudiocar.ui.adpter;

import com.google.gson.*;
import java.lang.reflect.Type;

public class StringOrObjectAdapter implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
            throws JsonParseException {
        if (json.isJsonNull()) return null;
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return json.getAsString(); // já é string
        }
        if (json.isJsonObject()) {
            // tenta pegar um "name" ou "tipo" comum; ajuste se necessário
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("nome")) return obj.get("nome").getAsString();
            if (obj.has("name")) return obj.get("name").getAsString();
            if (obj.has("tipo")) return obj.get("tipo").getAsString();
            // fallback: objeto como JSON compacto
            return obj.toString();
        }
        return json.toString();
    }
}
