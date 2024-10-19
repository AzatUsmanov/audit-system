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
import system.audit.enums.FindingGradationType;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

/**
 * Класс, используемый для передачи и получения
 * информации о замечаниях для аудитов из БД
 */
@Data
@Builder
@Entity
@Table(name = "findings")
@NoArgsConstructor
@AllArgsConstructor
public class Finding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "finding_name", nullable = false, unique = true)
    private String findingName;

    @Enumerated(EnumType.STRING)
    @Column(name = "finding_gradation", nullable = false)
    private FindingGradationType findingGradation;

    @Column(name = "finding_text", nullable = false)
    private String findingText;

    @Column(name = "root_cause_analysis_deadline", nullable = false)
    private Date rootCauseAnalysisDeadline;

    @Column(name = "proposed_corrective_actions", nullable = false)
    private String proposedCorrectiveActions;

    @Column(name = "corrective_actions_implementation_deadline", nullable = false)
    private Date correctiveActionsImplementationDeadline;

    @Column(name = "corrective_actions_acceptance", nullable = false)
    private Date correctiveActionsAcceptance;

    @Column(name = "corrective_actions_implementation_information", nullable = false)
    private String correctiveActionsImplementationInformation;

    @Column(name = "corrective_actions_implementation_factual_date", nullable = false)
    private Date correctiveActionsImplementationFactualDate;

    @Column(name = "finding_closure_confirmation", nullable = false)
    private Date findingClosureConfirmation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "finding_id")
    private List<FindingDocumentFile> files;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Finding finding = (Finding) o;
        return Objects.equals(id, finding.id)
                && Objects.equals(findingName, finding.findingName)
                && findingGradation == finding.findingGradation
                && Objects.equals(findingText, finding.findingText)
                && Objects.equals(rootCauseAnalysisDeadline, finding.rootCauseAnalysisDeadline)
                && Objects.equals(proposedCorrectiveActions, finding.proposedCorrectiveActions)
                && Objects.equals(correctiveActionsImplementationDeadline, finding.correctiveActionsImplementationDeadline)
                && Objects.equals(correctiveActionsAcceptance, finding.correctiveActionsAcceptance)
                && Objects.equals(correctiveActionsImplementationInformation, finding.correctiveActionsImplementationInformation)
                && Objects.equals(correctiveActionsImplementationFactualDate, finding.correctiveActionsImplementationFactualDate)
                && Objects.equals(findingClosureConfirmation, finding.findingClosureConfirmation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                findingName,
                findingGradation,
                findingText,
                rootCauseAnalysisDeadline,
                proposedCorrectiveActions,
                correctiveActionsImplementationDeadline,
                correctiveActionsAcceptance,
                correctiveActionsImplementationInformation,
                correctiveActionsImplementationFactualDate,
                findingClosureConfirmation);
    }
}
