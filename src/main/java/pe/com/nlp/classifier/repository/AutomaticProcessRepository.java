package pe.com.nlp.classifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.com.nlp.classifier.entity.AutomaticProcess;

@Repository
public interface AutomaticProcessRepository extends JpaRepository<AutomaticProcess, Integer> {
    AutomaticProcess findByName(String name);

    // insert into usrsms.automatic_processes(name, status, last_execute) values ('processAnswersByNLP', 1, now())
    @Modifying
    @Transactional
    @Query(value = "update usrsms.automatic_processes SET status = :status, last_execute = now() WHERE id = :id", nativeQuery = true)
    void changeStatusAutomaticProcesses(@Param("status") int status, @Param("id") int id);

}
