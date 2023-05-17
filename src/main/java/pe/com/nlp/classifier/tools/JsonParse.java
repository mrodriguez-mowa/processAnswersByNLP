package pe.com.nlp.classifier.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;
import pe.com.nlp.classifier.entity.IncomingMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class JsonParse {

    private ArrayList<IncomingMessage> messagesArray = new ArrayList<>();
    public ArrayList<IncomingMessage> convertJsonIntoArrayIncoming(JsonArray response) {
        messagesArray.clear();
        for (JsonElement message: response) {
            IncomingMessage newIncomingMessage = new IncomingMessage();
            newIncomingMessage.setId(message.getAsJsonObject().get("id").getAsInt());
            newIncomingMessage.setTextMessage(message.getAsJsonObject().get("textMessage").getAsString());
            newIncomingMessage.setReceivedDate(LocalDateTime.parse(message.getAsJsonObject().get("dateForPythonAPI").getAsString()));
            newIncomingMessage.setTrainedByModel(message.getAsJsonObject().get("model").getAsString());
            newIncomingMessage.setNlpClassification(message.getAsJsonObject().get("qualification").getAsString());
            messagesArray.add(newIncomingMessage);
        }

        return messagesArray;
    }
}
