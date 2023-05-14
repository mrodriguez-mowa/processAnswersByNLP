package pe.com.nlp.classifier;

import com.google.gson.JsonArray;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import pe.com.nlp.classifier.entity.AutomaticProcess;
import pe.com.nlp.classifier.entity.IncomingMessage;
import pe.com.nlp.classifier.repository.AutomaticProcessRepository;
import pe.com.nlp.classifier.repository.IncomingMessageRepository;
import pe.com.nlp.classifier.repository.IncomingMongoRepository;
import pe.com.nlp.classifier.service.AnswersService;
import pe.com.nlp.classifier.tools.JsonParse;
import pe.com.nlp.classifier.tools.MongoConnector;

import java.util.ArrayList;

@SpringBootApplication
@PropertySource("${path.main.properties}")
public class ClassifierApplication {
	private static final Logger log = LoggerFactory.getLogger(ClassifierApplication.class);

	private static AutomaticProcessRepository automaticProcessRepository;
	private static IncomingMessageRepository incomingMessageRepository;
	private static MongoConnector mongoConnector = new MongoConnector();
	private static MongoDatabase mongoDB = mongoConnector.mongoConnection();

	private static IncomingMongoRepository incomingMongoRepository = new IncomingMongoRepository();
	private static AnswersService answersService = new AnswersService();

	private static JsonParse jsonParse = new JsonParse();
	@Autowired
	ClassifierApplication(AutomaticProcessRepository automaticProcessRepository, IncomingMessageRepository incomingMessageRepository) {
		ClassifierApplication.automaticProcessRepository = automaticProcessRepository;
		ClassifierApplication.incomingMessageRepository = incomingMessageRepository;

	}



	public static void main(String[] args) {
		SpringApplication.run(ClassifierApplication.class, args);
		log.info("INICIANDO CLASIFICACIÓN DE INCOMING POR NLP");
		AutomaticProcess automaticProcess = automaticProcessRepository.findByName("processAnswersByNLP");
		if (automaticProcess.getStatus() == 1) {
			log.info("ACTUALIZANDO A ESTADO 0 EL PROCESO");
			automaticProcessRepository.changeStatusAutomaticProcesses(0, automaticProcess.getId());
			ArrayList<IncomingMessage> messagesToday = incomingMessageRepository.getIncomingMessagesToday();
			if (messagesToday.size() > 0) {
				log.info("EMPIEZA LÓGICA PARA CLASIFICAR POR NLP");
				ArrayList<Integer> idArray = new ArrayList();
				for(IncomingMessage message: messagesToday){
					idArray.add(message.getId());
				}
				JsonArray responses = answersService.classifyAnswersByNLP(messagesToday);
				System.out.println(responses.size());
				if (responses != null) {
					ArrayList<IncomingMessage> parsedResponse = jsonParse.convertJsonIntoArrayIncoming(responses);
					incomingMongoRepository.insertIncomingMongoDB(mongoDB, parsedResponse);
					incomingMessageRepository.updateNlpStatusMessage(idArray, 1);
				}
				incomingMessageRepository.updateNlpStatusMessage(idArray, 1);
				log.info("CERRADO!");
			} else {
				log.warn("NO HAY MENSAJES PARA PROCESAR");
			}
		} else {
			log.info("PROCESO NO ACTIVO O EN CURSO");
		}

	}

}
