package pe.com.nlp.classifier.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    public JsonArray classifyAnswersByNLP (ArrayList<IncomingMessage> incomingMessages) {
        ArrayList<IncomingMessage> alwaysPositive = new ArrayList<>();
        ArrayList<IncomingMessage> messagesToValidate = new ArrayList<>();
        JsonArray jsonResponse = null;
        // LOS MENSAJES QUE REPRESENTAN LLAMADAS DE CLIENTES SIEMPRE SERÁN POSITIVOS
        for(IncomingMessage message: incomingMessages) {
            if(message.getTextMessage().toLowerCase().contains("MOWA TE INFORMA:")) {
                alwaysPositive.add(message);
            }else {
                String pythonDate = message.getReceivedDate().toString();
                message.setDateForPythonAPI(pythonDate);
                messagesToValidate.add(message);
            }
        }

        // JSON PARA LOS MODELOS
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(messagesToValidate);

        try {
            URL url = new URL("http://localhost:5000/api/trained-model");
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
