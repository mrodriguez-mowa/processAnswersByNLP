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
    // @Query(value="SELECT * FROM usrsms.incoming_message WHERE received_date BETWEEN :lastExecute AND :currentExecute;\n", nativeQuery = true)
    // @Query(value="SELECT * FROM usrsms.incoming_message WHERE virtual_line = 'dev' \n", nativeQuery = true)
    @Query(value = "SELECT * FROM usrsms.incoming_message WHERE date_trunc('day', received_date) = CURRENT_DATE AND nlp_status = 0", nativeQuery = true)
    ArrayList<IncomingMessage> getIncomingMessagesToday(@Param("lastExecute")LocalDateTime lastExecute, @Param("currentExecute")LocalDateTime currentExecute);

}
