package system.audit.tool.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import system.audit.dto.Audit;
import system.audit.dto.AuditView;
import system.audit.dto.AuditDocumentFile;
import system.audit.enums.DocumentFileType;

import java.util.List;

/**
 * Класс, выполняющий процесс конвертации данных аудита, полученных из БД,
 * для предоставления информации клиенту
 */
@Component
public class AuditConverter implements Converter<Audit, AuditView> {

    @Override
    public AuditView convert(Audit source) {
        final var auditReportFile =  toMultipartFiles(source.getFiles(), DocumentFileType.AUDIT_REPORT);
        final var paymentConfirmationFile =  toMultipartFiles(source.getFiles(), DocumentFileType.PAYMENT_CONFIRMATION);
        return AuditView.builder()
                .id
                        (source.getId())
                .auditName
                        (source.getAuditName())
                .certificationBody
                        (source.getCertificationBody())
                .auditStartDate
                        (source.getAuditStartDate().toString())
                .auditCompletionDate
                        (source.getAuditCompletionDate().toString())
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
                .travelFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.TRAVEL))
                .accommodationFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.ACCOMMODATION))
                .visaFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.VISA))
                .informationForAuditFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.INFORMATION_FOR_AUDIT))
                .auditPlanFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.AUDIT_PLAN))
                .invoiceFiles
                        (toMultipartFiles(source.getFiles(), DocumentFileType.INVOICE))
                .paymentConfirmationFile
                        (paymentConfirmationFile.isEmpty() ? null : paymentConfirmationFile.getFirst())
                .auditReportFile
                        (auditReportFile.isEmpty() ? null : auditReportFile.getFirst())
                .build();
    }

    public static <T extends AuditDocumentFile> List<MultipartFile> toMultipartFiles(
            List<AuditDocumentFile> files, DocumentFileType documentFileType) {
        return files.stream()
                .filter(x -> x.getDocumentFileType() == documentFileType)
                .map(x -> (MultipartFile) x )
                .toList();
    }

}
