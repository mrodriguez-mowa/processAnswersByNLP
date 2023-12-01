package pe.com.nlp.classifier;

import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import pe.com.nlp.classifier.entity.AutomaticProcess;
import pe.com.nlp.classifier.entity.IncomingMessage;
import pe.com.nlp.classifier.entity.MessageGroup;
import pe.com.nlp.classifier.repository.AutomaticProcessRepository;
import pe.com.nlp.classifier.repository.IncomingMessageRepository;
import pe.com.nlp.classifier.service.AnswersService;
import pe.com.nlp.classifier.tools.JsonParse;
import pe.com.nlp.classifier.tools.NlpLabeler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@PropertySource("${path.main.properties}")
public class ClassifierApplication {
	private static final Logger log = LoggerFactory.getLogger(ClassifierApplication.class);

	private static AutomaticProcessRepository automaticProcessRepository;
	private static IncomingMessageRepository incomingMessageRepository;

	private static final AnswersService answersService = new AnswersService();

	private static final JsonParse jsonParse = new JsonParse();

	NlpLabeler nlpLabeler = new NlpLabeler();

	@Autowired
	ClassifierApplication(AutomaticProcessRepository automaticProcessRepository, IncomingMessageRepository incomingMessageRepository) {
		ClassifierApplication.automaticProcessRepository = automaticProcessRepository;
		ClassifierApplication.incomingMessageRepository = incomingMessageRepository;

	}

	public static void main(String[] args) {
		SpringApplication.run(ClassifierApplication.class, args);
		log.info("INICIANDO CLASIFICACIÓN DE INCOMING POR NLP");
		AutomaticProcess automaticProcess = automaticProcessRepository.findByName("processAnswersByNLP");
		if (automaticProcess.getStatus() != 3) {
			log.info("ACTUALIZANDO A ESTADO 0 EL PROCESO");
			// automaticProcessRepository.changeStatusAutomaticProcesses(0, automaticProcess.getId());

			ArrayList<IncomingMessage> messagesToday = incomingMessageRepository.getIncomingMessagesToday();
			log.info("MENSAJES ENCONTRADOS:"+ messagesToday.size());
			if (messagesToday.size() > 0) {
				log.info("EMPIEZA LÓGICA PARA CLASIFICAR POR NLP");

				answersService.setMessagesToValidate(messagesToday);

				JsonArray responsesSk = answersService.classifyAnswersByNLP("sklearn");

				ArrayList<IncomingMessage> alwaysPositive = answersService.getAlwaysPositive(); // CODIGO 1

				ArrayList<IncomingMessage> alwaysNegative = answersService.getAlwaysNegative();

				if (responsesSk != null) {
					ArrayList<IncomingMessage> parsedResponse = jsonParse.convertJsonIntoArrayIncoming(responsesSk);

					// SEPARAR POR CALIFICACIÓN Y JUNTAR ALL EN UN ARREGLO DE ARREGLOS
					ArrayList<Integer> foundLabels = new ArrayList<>();
					for (IncomingMessage incomingMessage: parsedResponse) {
						if(!foundLabels.contains(incomingMessage.getToLearn())){
							foundLabels.add(incomingMessage.getToLearn());
						}
					}

					// MANDAR LA CLASIFICACION AL ARREGLO FINAL
					for(Integer labelId: foundLabels) {
						List<Integer> incomingIds = parsedResponse.stream().filter(el -> el.getToLearn() == labelId).map(IncomingMessage::getId).collect(Collectors.toList());
						incomingMessageRepository.updateNlpIncomingMessages(labelId, (ArrayList<Integer>) incomingIds);
					}

					if (alwaysPositive.size() > 0) {
						List<Integer> positiveMessagesIds = alwaysPositive.stream().map(IncomingMessage::getId).collect(Collectors.toList());
						incomingMessageRepository.updateNlpIncomingMessages(alwaysPositive.get(0).getToLearn(), (ArrayList<Integer>) positiveMessagesIds);
					}

					if (alwaysNegative.size() > 0) {
						List<Integer> negativeMessagesIds = alwaysNegative.stream().map(IncomingMessage::getId).collect(Collectors.toList());
						incomingMessageRepository.updateNlpIncomingMessages(alwaysNegative.get(0).getToLearn(), (ArrayList<Integer>) negativeMessagesIds);
					}
				}

				// automaticProcessRepository.changeStatusAutomaticProcesses(1, automaticProcess.getId());
				log.info("CERRADO!");
			} else {
				log.warn("NO HAY MENSAJES PARA PROCESAR");
				// automaticProcessRepository.changeStatusAutomaticProcesses(1, automaticProcess.getId());
			}
		} else {
			log.info("PROCESO NO ACTIVO O EN CURSO");
		}

	}

}
