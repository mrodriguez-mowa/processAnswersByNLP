package pe.com.nlp.classifier.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

public class AnswersService {
    private static final Logger log = LoggerFactory.getLogger(AnswersService.class);
    @Setter
    ArrayList<IncomingMessage> incomingMessages = new ArrayList<>();
    @Setter
    ArrayList<IncomingMessage> alwaysPositive = new ArrayList<>();

    ArrayList<IncomingMessage> messagesToValidate = new ArrayList<>();

    public void setMessagesToValidate(ArrayList<IncomingMessage> incomingMessages) {
        for(IncomingMessage message: incomingMessages) {
            if(message.getTextMessage().toLowerCase().contains("MOWA TE INFORMA:")) {
                alwaysPositive.add(message);
            }else {
                String pythonDate = message.getReceivedDate().toString();
                message.setDateForPythonAPI(pythonDate);
                messagesToValidate.add(message);
            }
        }
    }
    public JsonArray classifyAnswersByNLP () {

        // JSON PARA LOS MODELOS
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(messagesToValidate);

        JsonArray jsonResponse = null;

        try {
            URL url = new URL("http://34.232.95.220:6000/api/trained-model");
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
            System.out.println(response);
            jsonResponse = gson.fromJson(response, JsonArray.class);

        } catch (Exception e) {
            log.error("ERROR AL ENVIAR A X MODELO");
            log.error(e.getMessage());
        }
    return jsonResponse;

    }
}
