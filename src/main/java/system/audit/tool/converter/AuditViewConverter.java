package system.audit.tool.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import system.audit.dto.Audit;
import system.audit.dto.AuditDocumentFile;
import system.audit.dto.AuditView;
import system.audit.enums.DocumentFileType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, выполняющий процесс конвертации данных аудита, полученных от клиента,
 * для сохранения в БД
 */
@Component
public class AuditViewConverter implements Converter<AuditView,Audit> {

    @Override
    public Audit convert(AuditView source) {
        return Audit.builder()
                .id
                        (source.getId())
                .auditName
                        (source.getAuditName())
                .certificationBody
                        (source.getCertificationBody())
                .auditStartDate
                        (Date.valueOf(source.getAuditStartDate()))
                .auditCompletionDate
                        (Date.valueOf(source.getAuditCompletionDate()))
                .office
                        (source.getOffice())
                .contactDetails
                        (source.getContactDetails())
                .auditType
                        (source.getAuditType())
                .auditPlanAcceptance
                        (source.getAuditPlanAcceptance())
                .auditReportAcceptance
                        (source.getAuditReportAcceptance())
                .files
                        (getFilesFromAuditView(source))
                .build();
    }

    private static List<AuditDocumentFile> getFilesFromAuditView(AuditView auditView) {
        List<AuditDocumentFile> auditDocumentFiles = new ArrayList<>();

        auditDocumentFiles.addAll(
                toDocumentFiles(auditView.getInvoiceFiles(), DocumentFileType.INVOICE));
        auditDocumentFiles.addAll(
                toDocumentFiles(auditView.getAuditPlanFiles(), DocumentFileType.AUDIT_PLAN));
        auditDocumentFiles.addAll(
                toDocumentFiles(auditView.getInformationForAuditFiles(), DocumentFileType.INFORMATION_FOR_AUDIT));
        auditDocumentFiles.addAll(
                toDocumentFiles(auditView.getAccommodationFiles(), DocumentFileType.ACCOMMODATION));
        auditDocumentFiles.addAll(
                toDocumentFiles(auditView.getVisaFiles(), DocumentFileType.VISA));
        auditDocumentFiles.addAll(
                toDocumentFiles(auditView.getTravelFiles(), DocumentFileType.TRAVEL));
        auditDocumentFiles.addAll(
                toDocumentFiles(List.of(auditView.getAuditReportFile()), DocumentFileType.AUDIT_REPORT));
        auditDocumentFiles.addAll(
                toDocumentFiles(List.of(auditView.getPaymentConfirmationFile()), DocumentFileType.PAYMENT_CONFIRMATION));

        return auditDocumentFiles;
    }

    public static List<AuditDocumentFile> toDocumentFiles(List<MultipartFile> files,
                                                          DocumentFileType documentFileType) {
        return files.stream()
                .map(AuditDocumentFile::new)
                .peek(x -> x.setDocumentFileType(documentFileType))
                .filter(x -> x.getOriginalFilename() != null && !x.getOriginalFilename().isEmpty())
                .toList();
    }

}
