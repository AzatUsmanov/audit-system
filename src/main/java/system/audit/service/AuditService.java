package system.audit.service;

import system.audit.dto.AuditView;
import system.audit.tool.exception.AuditNameNotUniqueException;

import java.util.List;

public interface AuditService {

    void save(AuditView auditView) throws AuditNameNotUniqueException;

    void updateById(AuditView auditView, Integer id) throws AuditNameNotUniqueException;

    List<AuditView> getAllSorted(String sortField);

    AuditView getById(Integer id);

}
