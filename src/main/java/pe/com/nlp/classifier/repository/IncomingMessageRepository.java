package pe.com.nlp.classifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.com.nlp.classifier.entity.IncomingMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public interface IncomingMessageRepository extends JpaRepository<IncomingMessage, Integer>  {

    @Modifying
    // @Query(value="SELECT * FROM usrsms.incoming_message WHERE received_date BETWEEN :lastExecute AND :currentExecute;\n", nativeQuery = true)
    // @Query(value="SELECT * FROM usrsms.incoming_message WHERE virtual_line = 'dev' \n", nativeQuery = true)
    @Query(value = "select * from usrsms.incoming_message \n" +
            "where node not ilike '%ENTEL%' \n" +
            "AND node not ilike '%CLARO%' \n" +
            "AND node not ilike '%TEST%' \n" +
            "and msisdn NOT ilike '%ovistar%'\n" +
            // "AND nlp_status = 0 \n" +
            "AND date_trunc('day', received_date) = CURRENT_DATE ", nativeQuery = true)
    ArrayList<IncomingMessage> getIncomingMessagesToday(@Param("lastExecute")LocalDateTime lastExecute, @Param("currentExecute")LocalDateTime currentExecute);

    /*
    @Modifying
    @Transactional
    @Query(value = "update usrsms.incoming_message \n" +
            "set nlp_status = 0, \n" +
            "qualification = :qualification, \n" +
            "last_execute = now(), \n" +
            "last_model = :model \n" +
            "where id = :id", nativeQuery = true)
    void updateNlpNumbersIncomingMessages(@Param("qualification")String qualification, @Param("model")String model, @Param("messageId") int id);
    */
}
