package pe.com.nlp.classifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.nlp.classifier.entity.IncomingMessage;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Repository
public interface IncomingMessageRepository extends JpaRepository<IncomingMessage, Integer>  {

    // alter table usrsms.incoming_message add column nlp_status integer
    @Modifying
    @Query(value="SELECT * FROM usrsms.incoming_message where date_trunc('day', received_date) = CURRENT_DATE AND nlp_status = 0 LIMIT 100 \n", nativeQuery = true)
    ArrayList<IncomingMessage> getIncomingMessagesToday();

    @Modifying
    @Transactional
    @Query(value = "UPDATE usrsms.incoming_message SET nlp_status = :status WHERE id IN (:idList)", nativeQuery = true)
    void updateNlpStatusMessage(@Param("idList")ArrayList<Integer> idList, @Param("status")int status);

}
