package pe.com.nlp.classifier.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "automatic_processes", schema="usrsms")
public class AutomaticProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter @Setter
    private Integer id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private int status;
    @Getter @Setter @Column(name = "last_execute")
    private LocalDateTime lastExecute;

}
