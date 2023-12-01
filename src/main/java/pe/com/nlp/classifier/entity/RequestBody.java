package pe.com.nlp.classifier.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class RequestBody {
    @Getter @Setter
    ArrayList<IncomingMessage> data;
}
