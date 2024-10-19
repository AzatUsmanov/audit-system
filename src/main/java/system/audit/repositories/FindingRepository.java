package system.audit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import system.audit.dto.Finding;

import java.util.Optional;

@Repository
public interface FindingRepository extends JpaRepository<Finding, Integer> {

    boolean existsByFindingName(String findingName);

}
