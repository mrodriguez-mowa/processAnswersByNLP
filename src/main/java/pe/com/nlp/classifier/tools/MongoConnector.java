package pe.com.nlp.classifier.tools;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

public class MongoConnector {
    MongoClient mongoClient = null;
    MongoDatabase db = null;
    public String URL = "mongodb+srv://marko:marko@clinica.4d0rj.mongodb.net/?retryWrites=true&w=majority";

    // public String URL = "mongodb://127.0.0.1/incoming_nlp";

    private static final Logger log = LoggerFactory.getLogger(MongoConnector.class);


    public MongoDatabase  mongoConnection() {
        try {
            mongoClient = MongoClients.create(URL);
            db = mongoClient.getDatabase("incoming_nlp");
        } catch (Exception e) {
            log.error("ERROR CONNECTING MONGODB");
            log.error(e.getMessage());
        }
        return db;
    }
}
