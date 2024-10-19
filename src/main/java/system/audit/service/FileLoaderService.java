package system.audit.service;


import jakarta.servlet.http.HttpServletResponse;

public interface FileLoaderService {

    void deleteAuditDocumentFileById(Integer id);

    void deleteFindingDocumentFileById(Integer id);

    void loadAuditDocumentFileById(HttpServletResponse response, Integer id);

    void loadFindingDocumentFileById(HttpServletResponse response, Integer id);

}
