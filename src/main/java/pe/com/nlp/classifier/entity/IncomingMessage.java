package pe.com.nlp.classifier.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "automatic_processes", schema="usrsms")
public class IncomingMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;
    @Getter @Setter @Column(name="txt_msg")
    private String textMessage;
    @Getter @Setter @Column(name = "received_date")
    private LocalDateTime receivedDate;
    @Getter @Setter @Transient//@Column(name = "nlp_status")
    private int nlpStatus;
    @Getter @Setter @Transient //@Column(name="qualification")
    private String nlpClassification;
    @Getter @Setter @Transient // @Column(name="last_model")
    private String trainedByModel;
    @Getter @Setter @Transient
    private String dateForPythonAPI;
    public IncomingMessage() {
    }


}
