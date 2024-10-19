package system.audit.service;

import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import system.audit.repositories.AuditDocumentFileRepository;
import system.audit.repositories.FindingDocumentFileRepository;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Класс, предоставляющий функционал по скачиванию и удалению файлов
 */
@Service
@AllArgsConstructor
public class FileLoaderServiceImpl implements FileLoaderService {

    private final FindingDocumentFileRepository findingDocumentFileRepository;

    private final AuditDocumentFileRepository auditDocumentFileRepository;

    @Override
    public void deleteAuditDocumentFileById(Integer id) {
        auditDocumentFileRepository.deleteById(id);
    }

    @Override
    public void deleteFindingDocumentFileById(Integer id) {
        findingDocumentFileRepository.deleteById(id);
    }

    @Override
    public void loadAuditDocumentFileById(HttpServletResponse response, Integer id) {
        var file = auditDocumentFileRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        loadFile(response, file);
    }

    @Override
    public void loadFindingDocumentFileById(HttpServletResponse response, Integer id) {
        var file = findingDocumentFileRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        loadFile(response, file);
    }

    private void loadFile(HttpServletResponse response, MultipartFile file)  {
        String fileName = "";

        try {
            fileName = URLEncoder.encode(file.getOriginalFilename(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

        final var headerName = "Content-Disposition";
        final var headerValue = "attachment; filename=" + fileName;

        response.setContentType(file.getContentType());
        response.setHeader(headerName, headerValue);

        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = response.getOutputStream()) {
            inputStream.transferTo(outputStream);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
