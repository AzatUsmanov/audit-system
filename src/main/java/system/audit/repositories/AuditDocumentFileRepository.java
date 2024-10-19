package system.audit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import system.audit.dto.AuditDocumentFile;

@Repository
public interface AuditDocumentFileRepository extends JpaRepository<AuditDocumentFile, Integer> {
}
