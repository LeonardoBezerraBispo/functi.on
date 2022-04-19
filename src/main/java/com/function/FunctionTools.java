package com.function;

import java.util.ArrayList;
import java.util.Base64;
import java.io.InputStream;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;

public class FunctionTools {

    private ExecutionContext context;

    public FunctionTools(ExecutionContext context) {}

    public String base64encoder(final String username, final String password) {
        String credential = username + ":" + password;
        String encodedCredential = "Basic " + Base64.getEncoder().encodeToString(credential.getBytes());
        return encodedCredential;
    }
        
    public void Logger(String string) {
        if(string.length() < 8000){
            context.getLogger().info("[FUNCTION(" + context.getFunctionName().toUpperCase() + ")] - " + string);
        } else{
            stringSplitter(string);
        }
    }

    public String exceptionTransformer(List<String> erroLista) {
        JSONArray listaErro = new JSONArray();
        JSONObject badRequest = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date date = new Date();
        badRequest.put("timestamp", dateFormat.format(date));
        badRequest.put("message", "JSON inválido");
        badRequest.put("code", "400");
        if (erroLista.size() > 0) {
            for (int i = 0; i < erroLista.size(); i++) {
                JSONObject objetoErro = new JSONObject();
                objetoErro.put("detail", erroLista.get(i).replace("#/", "$."));
                listaErro.put(objetoErro);
            }
        }
        badRequest.put("details", listaErro);
        return badRequest.toString();
    }

    public void Validate(String schemaPath) {
        JSONObject requestJSON = new JSONObject();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(schemaPath);
        JSONObject schemaBase = new JSONObject(new JSONTokener(inputStream));
        Schema schema = SchemaLoader.load(schemaBase);
        schema.validate(requestJSON); // throws ValidationException if invalid
    }

    private void stringSplitter(String text){
        int splitSize = 8000;

        //execução da divisão de trechos a cada <splitSize> caracteres.
        List<String> arrayText = new ArrayList<>();
        for (int start = 0; start < text.length(); start += splitSize) {
            arrayText.add(text.substring(start, Math.min(text.length(), start + splitSize)));
        }
        for (int i = 0; i < arrayText.size(); i++){
            context.getLogger().info("[FUNCTION(" + context.getFunctionName().toUpperCase() + ")] - " + arrayText.get(i));
        }
    }
}
