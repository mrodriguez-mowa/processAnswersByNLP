package pe.com.nlp.classifier.service;

import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.com.nlp.classifier.entity.IncomingMessage;
import com.google.gson.Gson;
import pe.com.nlp.classifier.entity.RequestBody;
import pe.com.nlp.classifier.tools.NlpLabeler;

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
    @Setter @Getter
    ArrayList<IncomingMessage> alwaysNegative = new ArrayList<>();

    // Always Negative - relacionados a denuncias o indecopi

    //

    NlpLabeler nlpLabeler = new NlpLabeler();

    ArrayList<IncomingMessage> messagesToValidate = new ArrayList<>();

    public void setMessagesToValidate(ArrayList<IncomingMessage> incomingMessages) {
        for(IncomingMessage message: incomingMessages) {
            if(message.getTextMessage().toUpperCase().contains("MOWA TE INFORMA:")) {
                message.setToLearn(nlpLabeler.NlpLabelerHash("ASESORAMIENTO"));
                alwaysPositive.add(message);
            } else if (message.getTextMessage().toUpperCase().contains("DENUNCIA")) {
                message.setToLearn(nlpLabeler.NlpLabelerHash("PREVENCIÓN"));
                alwaysNegative.add(message);
            } else{
                String pythonDate = message.getReceivedDate().toString();
                message.setDateForPythonAPI(pythonDate);
                messagesToValidate.add(message);
            }
        }
    }
    public JsonArray classifyAnswersByNLP (String model) {

        HashMap<String, String> modelHash = new HashMap<>();
        modelHash.put("sklearn","http://20.228.191.65:5000/api/process-nlp" );

        String urlToRequest = modelHash.get(model);

        // JSON PARA LOS MODELOS
        Gson gson = new Gson();
        log.info("MESSAGES "+ messagesToValidate.size());
        log.info("MODEL "+  model);
        RequestBody requestBody = new RequestBody();
        requestBody.setData(messagesToValidate);
        String jsonPayload = gson.toJson(requestBody);

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
}
