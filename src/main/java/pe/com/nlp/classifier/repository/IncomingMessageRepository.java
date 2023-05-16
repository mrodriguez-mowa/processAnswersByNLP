package pe.com.nlp.classifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.nlp.classifier.entity.IncomingMessage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public interface IncomingMessageRepository extends JpaRepository<IncomingMessage, Integer>  {

    @Modifying
    @Query(value="SELECT * FROM usrsms.incoming_message WHERE received_date BETWEEN '2023-05-16T07:13:06.568183' AND :currentExecute limit 100;\n", nativeQuery = true)
    ArrayList<IncomingMessage> getIncomingMessagesToday(@Param("currentExecute")LocalDateTime currentExecute);

}
