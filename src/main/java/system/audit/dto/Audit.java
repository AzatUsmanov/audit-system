package system.audit.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import system.audit.enums.AuditType;
import system.audit.enums.CertificationBodyType;

import java.sql.Date;
import java.util.List;
import java.util.Objects;


/**
 * Класс, используемый для передачи и получения информации об аудитах из БД
 */
@Data
@Builder
@Entity
@Table(name = "audits")
@NoArgsConstructor
@AllArgsConstructor
public class Audit {

    public static final int URI_MAX_LENGTH = 2100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "audit_name", nullable = false, unique = true)
    private String auditName;

    @Enumerated(EnumType.STRING)
    @Column(name = "certification_body", nullable = false)
    private CertificationBodyType certificationBody;

    @Column(name = "audit_start_date", nullable = false)
    private Date auditStartDate;

    @Column(name = "audit_completion_date", nullable = false)
    private Date auditCompletionDate;

    @Column(nullable = false)
    private String office;

    @Column(name = "contact_details", nullable = false, length = URI_MAX_LENGTH)
    private String contactDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_type", nullable = false)
    private AuditType auditType;

    @Column(name = "audit_plan_acceptance", nullable = false)
    private Boolean auditPlanAcceptance;

    @Column(name = "audit_report_acceptance", nullable = false)
    private Boolean auditReportAcceptance;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "audit_id")
    private List<AuditDocumentFile> files;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "audit_id")
    private List<Finding> findings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audit audit = (Audit) o;
        return Objects.equals(id, audit.id)
                && Objects.equals(auditName, audit.auditName)
                && certificationBody == audit.certificationBody
                && Objects.equals(auditStartDate, audit.auditStartDate)
                && Objects.equals(auditCompletionDate, audit.auditCompletionDate)
                && Objects.equals(office, audit.office)
                && Objects.equals(contactDetails, audit.contactDetails)
                && auditType == audit.auditType
                && Objects.equals(auditPlanAcceptance, audit.auditPlanAcceptance)
                && Objects.equals(auditReportAcceptance, audit.auditReportAcceptance);
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

