package system.audit.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import system.audit.enums.DocumentFileType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Класс, используемый для хранения информации о фойлах для замечаний
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "finding_document_files")
public class FindingDocumentFile implements MultipartFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_file_type", nullable = false)
    private DocumentFileType documentFileType;

    private String name;

    @Column(name = "original_file_name", unique = true)
    private String originalFilename;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    public FindingDocumentFile(MultipartFile file) {
        try {
            this.name = file.getName();
            this.originalFilename = file.getOriginalFilename();
            this.fileSize = file.getSize();
            this.contentType = file.getContentType();
            this.content = file.getBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return fileSize;
    }


    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try(FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }


}
