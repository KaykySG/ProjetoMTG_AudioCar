package util;

import com.google.gson.*;
import java.lang.reflect.Type;
import model.CategoriaComponente;

public class CategoriaComponenteJsonAdapter implements JsonDeserializer<CategoriaComponente> {

    @Override
    public CategoriaComponente deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
            throws JsonParseException {

        if (json == null || json.isJsonNull()) return null;

        // Caso o backend envie apenas uma string: "ALTOFALANTE"
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String nome = json.getAsString();
            CategoriaComponente c = new CategoriaComponente();
            trySetNome(c, nome);
            trySetCodigo(c, nome); // espelhar no código se existir esse campo
            return c;
        }

        // Caso venha objeto: { "id": "...", "nome": "...", "codigo": "..." } (chaves podem variar)
        if (json.isJsonObject()) {
            JsonObject o = json.getAsJsonObject();
            CategoriaComponente c = new CategoriaComponente();

            if (o.has("id") && !o.get("id").isJsonNull())          trySetId(c, o.get("id").getAsString());
            if (o.has("nome") && !o.get("nome").isJsonNull())      trySetNome(c, o.get("nome").getAsString());
            if (o.has("name") && !o.get("name").isJsonNull())      trySetNome(c, o.get("name").getAsString());
            if (o.has("codigo") && !o.get("codigo").isJsonNull())  trySetCodigo(c, o.get("codigo").getAsString());
            if (o.has("code") && !o.get("code").isJsonNull())      trySetCodigo(c, o.get("code").getAsString());

            // fallback: se não houver código, espelha o nome
            if (safeGetCodigo(c) == null && safeGetNome(c) != null) trySetCodigo(c, safeGetNome(c));

            return c;
        }

        // Qualquer outro formato, armazena a representação textual no nome
        CategoriaComponente c = new CategoriaComponente();
        trySetNome(c, json.toString());
        return c;
    }

    // ---- helpers para não depender do nome exato dos métodos ----
    private void trySetId(CategoriaComponente c, String v) { invokeSetter(c, "setId", v); }
    private void trySetNome(CategoriaComponente c, String v) { invokeSetter(c, "setNome", v); }
    private void trySetCodigo(CategoriaComponente c, String v) { invokeSetter(c, "setCodigo", v); }

    private String safeGetNome(CategoriaComponente c) { return invokeGetter(c, "getNome"); }
    private String safeGetCodigo(CategoriaComponente c) { return invokeGetter(c, "getCodigo"); }

    private void invokeSetter(Object target, String method, String arg) {
        try { target.getClass().getMethod(method, String.class).invoke(target, arg); } catch (Exception ignored) {}
    }
    private String invokeGetter(Object target, String method) {
        try { Object r = target.getClass().getMethod(method).invoke(target); return r!=null? r.toString():null; } catch (Exception e) { return null; }
    }
}
