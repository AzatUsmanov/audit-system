package system.audit.dto;



import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import system.audit.enums.AuditType;
import system.audit.enums.CertificationBodyType;

import java.util.List;
import java.util.Objects;


/**
 * Класс, используемый для передачи и получения информации об аудитах из HTML форм
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditView {

    public static final int AUDIT_NAME_MIN_LENGTH = 1;

    public static final int AUDIT_NAME_MAX_LENGTH = 50;

    public static final int OFFICE_MIN_LENGTH = 1;

    public static final int OFFICE_MAX_LENGTH = 50;

    public static final int CONTACT_DETAILS_MIN_LENGTH = 1;

    public static final int CONTACT_DETAILS_MAX_LENGTH = 50;

    public static final int DATE_MIN_LENGTH = 1;

    private Integer id;

    @Size(min = AUDIT_NAME_MIN_LENGTH, max = AUDIT_NAME_MAX_LENGTH,
    message = "Audit ID length must be between "
            + AUDIT_NAME_MIN_LENGTH + " and "
            + AUDIT_NAME_MAX_LENGTH )
    private String auditName;

    private CertificationBodyType certificationBody;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String auditStartDate;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String auditCompletionDate;

    @Size(min = OFFICE_MIN_LENGTH, max = OFFICE_MAX_LENGTH,
            message = "Office length must be between "
                    + AUDIT_NAME_MIN_LENGTH + " and "
                    + AUDIT_NAME_MAX_LENGTH )
    private String office;

    @Size(min = CONTACT_DETAILS_MIN_LENGTH, max = CONTACT_DETAILS_MAX_LENGTH,
            message = "Contact details length must be between "
                    + AUDIT_NAME_MIN_LENGTH + " and "
                    + AUDIT_NAME_MAX_LENGTH )
    private String contactDetails;

    private AuditType auditType;

    private List<MultipartFile> travelFiles;

    private List<MultipartFile> accommodationFiles;

    private List<MultipartFile> visaFiles;

    private List<MultipartFile> informationForAuditFiles;

    private List<MultipartFile> auditPlanFiles;

    private Boolean auditPlanAcceptance;

    private MultipartFile auditReportFile;

    private Boolean auditReportAcceptance;

    private List<MultipartFile> invoiceFiles;

    private MultipartFile paymentConfirmationFile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditView auditView = (AuditView) o;
        return Objects.equals(id, auditView.id)
                && Objects.equals(auditName, auditView.auditName)
                && certificationBody == auditView.certificationBody
                && Objects.equals(auditStartDate, auditView.auditStartDate)
                && Objects.equals(auditCompletionDate, auditView.auditCompletionDate)
                && Objects.equals(office, auditView.office)
                && Objects.equals(contactDetails, auditView.contactDetails)
                && auditType == auditView.auditType
                && Objects.equals(auditPlanAcceptance, auditView.auditPlanAcceptance)
                && Objects.equals(auditReportAcceptance, auditView.auditReportAcceptance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                auditName,
                certificationBody,
                auditStartDate,
                auditCompletionDate,
                office,
                contactDetails,
                auditType,
                auditPlanAcceptance,
                auditReportAcceptance);
    }
}
