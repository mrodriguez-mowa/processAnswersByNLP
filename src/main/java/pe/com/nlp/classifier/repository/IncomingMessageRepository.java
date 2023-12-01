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
    @Query(value = "select * from usrsms.incoming_message \n" +
            "where node not ilike '%ENTEL%' \n" +
            "AND node not ilike '%CLARO%' \n" +
            "AND node not ilike '%TEST%' \n" +
            "and msisdn NOT ilike '%movistar%'\n" +
            "AND to_learn = 0 \n" +
            "AND date_trunc('day', received_date) = CURRENT_DATE limit 100", nativeQuery = true)
    ArrayList<IncomingMessage> getIncomingMessagesToday();

    @Modifying
    @Transactional
    @Query(value = "update usrsms.incoming_message set to_learn =:nlpLabelId where id in :incomingIds", nativeQuery = true)
    void updateNlpIncomingMessages(@Param("nlpLabelId")int nlpLabel, @Param("incomingIds")ArrayList<Integer> incomingIds);

}
