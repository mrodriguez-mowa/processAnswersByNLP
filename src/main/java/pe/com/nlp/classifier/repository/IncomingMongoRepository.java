package pe.com.nlp.classifier.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.com.nlp.classifier.entity.IncomingMessage;
import pe.com.nlp.classifier.tools.MongoConnector;

import java.util.ArrayList;

public class IncomingMongoRepository {
    private static final Logger log = LoggerFactory.getLogger(IncomingMongoRepository.class);

    public void insertIncomingMongoDB(MongoDatabase mongoDatabase, ArrayList<IncomingMessage> incomingMessages) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("incoming_messages");
        ArrayList<Document> documents = new ArrayList<>();
        log.info("INICIANDO PROCESO DE INSERCIÓN A MONGODB");
        for (IncomingMessage incomingMessage: incomingMessages) {
            Document document = new Document();
            document.append("message_id", incomingMessage.getId());
            document.append("text", incomingMessage.getTextMessage());
            document.append("received_date", incomingMessage.getReceivedDate());
            document.append("qualification", incomingMessage.getNlpClassification());
            document.append("model", incomingMessage.getTrainedByModel());
            documents.add(document);
        }
        try {
            collection.insertMany(documents);
            log.info("INSERCIÓN CORRECTA DE: "+ documents.size() + "REGISTROS");
        } catch (Exception e) {
            log.error("ERROR INSERTANDO INCOMING_MESSAGES A MONGODB");
        } finally {
            log.info("FINALIZÓ PROCESO INSERCIÓN A MONGODB");
        }
    }
}
