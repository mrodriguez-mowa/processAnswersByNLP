package pe.com.nlp.classifier.service;

import pe.com.nlp.classifier.entity.IncomingMessage;

import java.util.ArrayList;

public class AnswersService {
    public void classifyAnswersByNLP (ArrayList<IncomingMessage> incomingMessages) {
        ArrayList<IncomingMessage> alwaysPositive = new ArrayList<>();
        ArrayList<IncomingMessage> messagesToValidate = new ArrayList<>();

        // LOS MENSAJES QUE REPRESENTAN LLAMADAS DE CLIENTES SIEMPRE SERÁN POSITIVOS
        for(IncomingMessage message: incomingMessages) {
            if(message.getTextMessage().toLowerCase().contains("MOWA TE INFORMA:")) {
                alwaysPositive.add(message);
            }else {
                messagesToValidate.add(message);
            }
        }

        // PROBAR CON EL MODEL

    }
}
