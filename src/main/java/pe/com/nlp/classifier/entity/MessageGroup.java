package pe.com.nlp.classifier.entity;

import lombok.Getter;
import lombok.Setter;


import java.util.List;

public class MessageGroup {
    @Getter @Setter
    int labelId;
    @Getter @Setter
    List<Integer> messagesId;
}
