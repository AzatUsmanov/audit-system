package system.audit.service;


import system.audit.dto.FindingView;
import system.audit.tool.exception.FindingNameNotUniqueException;

import java.util.List;

public interface FindingService {

    FindingView getById(Integer id);

    void save(FindingView findingView, Integer auditId) throws FindingNameNotUniqueException;

    void updateByFindingId(FindingView findingView, Integer id) throws FindingNameNotUniqueException;

    List<FindingView> getAllSortedByAuditId(String sortField, Integer auditId);

}
