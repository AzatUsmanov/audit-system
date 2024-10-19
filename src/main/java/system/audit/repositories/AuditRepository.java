package system.audit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import system.audit.dto.Audit;


@Repository
public interface AuditRepository extends JpaRepository<Audit, Integer> {

    boolean existsByAuditName(String auditName);

}
