package pe.com.nlp.classifier.service;

import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.com.nlp.classifier.entity.IncomingMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class AnswersService {
    private static final Logger log = LoggerFactory.getLogger(AnswersService.class);
    @Setter
    ArrayList<IncomingMessage> incomingMessages = new ArrayList<>();
    @Setter @Getter
    ArrayList<IncomingMessage> alwaysPositive = new ArrayList<>();

    // Always Negative - relacionados a denuncias o indecopi

    //

    ArrayList<IncomingMessage> messagesToValidate = new ArrayList<>();

    public void setMessagesToValidate(ArrayList<IncomingMessage> incomingMessages) {
        for(IncomingMessage message: incomingMessages) {
            if(message.getTextMessage().toUpperCase().contains("MOWA TE INFORMA:")) {
                message.setNlpClassification("POSITIVO");
                message.setTrainedByModel("REGLA");
                alwaysPositive.add(message);
            }else {
                String pythonDate = message.getReceivedDate().toString();
                message.setDateForPythonAPI(pythonDate);
                messagesToValidate.add(message);
            }
        }
    }
    public JsonArray classifyAnswersByNLP (String model) {

        HashMap<String, String> modelHash = new HashMap<String, String>();
        modelHash.put("sklearn","http://34.200.218.9:6000/api/trained-model" );
        // modelHash.put("tensorflow", "http://localhost:6000/api/trained-model");
        // modelHash.put("nlpjs", "http://localhost:7000/api/trained-model");

        String urlToRequest = modelHash.get(model);

        // JSON PARA LOS MODELOS
        Gson gson = new Gson();
        log.info("MESSAGES "+ messagesToValidate.size());
        log.info("MODEL "+  model);
        String jsonPayload = gson.toJson(messagesToValidate);

        JsonArray jsonResponse = null;

        try {
            URL url = new URL(urlToRequest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                responseBuilder.append(line);
            }
            bufferedReader.close();
            outputStream.close();
            connection.disconnect();
            log.debug("HTTP request completed successfully");

            String response = responseBuilder.toString();
            jsonResponse = gson.fromJson(response, JsonArray.class);

        } catch (Exception e) {
            log.error("ERROR AL ENVIAR A "+model+" MODELO");
            log.error(e.getMessage());
        }
    return jsonResponse;

    }


    public void updateBdAlwaysPositive() {
        int sizePositive = alwaysPositive.size();
        if ( sizePositive > 0){
            log.info("MENSAJES DE LLAMADAS ENCONTRADOS: "+ sizePositive);

        }else{
            log.info("NO HAY MENSAJES DE LLAMADAS PARA ACTUALIZAR");
        }
    }
}
