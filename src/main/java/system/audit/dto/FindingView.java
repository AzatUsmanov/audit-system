package system.audit.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import system.audit.enums.FindingGradationType;

import java.util.List;
import java.util.Objects;

/**
 * Класс, используемый для передачи и получения
 * информации о замечаниях для аудитов из HTML форм
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindingView {

    public static final int FINDING_NAME_MIN_LENGTH = 1;

    public static final int FINDING_NAME_MAX_LENGTH = 50;

    public static final int FINDING_TEXT_MIN_LENGTH = 1;

    public static final int FINDING_TEXT_MAX_LENGTH = 50;

    public static final int PROPOSED_CORRECTIVE_ACTIONS_MIN_LENGTH = 1;

    public static final int PROPOSED_CORRECTIVE_ACTIONS_MAX_LENGTH = 50;

    public static final int CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION_MIN = 1;

    public static final int CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION_MAX = 50;

    public static final int DATE_MIN_LENGTH = 1;

    private Integer id;

    @Size(min = FINDING_NAME_MIN_LENGTH, max = FINDING_NAME_MAX_LENGTH,
            message = "Finding name length must be between "
                    + FINDING_NAME_MIN_LENGTH + " and "
                    + FINDING_NAME_MAX_LENGTH )
    private String findingName;

    private FindingGradationType findingGradation;

    @Size(min = FINDING_TEXT_MIN_LENGTH, max = FINDING_TEXT_MAX_LENGTH,
            message = "Finding text length must be between "
                    + FINDING_TEXT_MIN_LENGTH + " and "
                    + FINDING_TEXT_MAX_LENGTH )
    private String findingText;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String rootCauseAnalysisDeadline;

    @Size(min = PROPOSED_CORRECTIVE_ACTIONS_MIN_LENGTH, max = PROPOSED_CORRECTIVE_ACTIONS_MAX_LENGTH,
            message = "Proposed corrective actions length must be between "
                    + PROPOSED_CORRECTIVE_ACTIONS_MIN_LENGTH + " and "
                    + PROPOSED_CORRECTIVE_ACTIONS_MAX_LENGTH )
    private String proposedCorrectiveActions;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String correctiveActionsImplementationDeadline;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String correctiveActionsAcceptance;

    @Size(min = CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION_MIN,
            max = CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION_MAX,
            message = "Corrective actions implementation information name length length must be between "
                    + CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION_MIN + " and "
                    + CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION_MAX )
    private String correctiveActionsImplementationInformation;

    private List<MultipartFile> correctiveActionsFiles;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String correctiveActionsImplementationFactualDate;

    @Size(min = DATE_MIN_LENGTH, message = "Date not selected")
    private String findingClosureConfirmation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindingView that = (FindingView) o;
        return Objects.equals(id, that.id)
                && Objects.equals(findingName, that.findingName)
                && findingGradation == that.findingGradation
                && Objects.equals(findingText, that.findingText)
                && Objects.equals(rootCauseAnalysisDeadline, that.rootCauseAnalysisDeadline)
                && Objects.equals(proposedCorrectiveActions, that.proposedCorrectiveActions)
                && Objects.equals(correctiveActionsImplementationDeadline, that.correctiveActionsImplementationDeadline)
                && Objects.equals(correctiveActionsAcceptance, that.correctiveActionsAcceptance)
                && Objects.equals(correctiveActionsImplementationInformation, that.correctiveActionsImplementationInformation)
                && Objects.equals(correctiveActionsImplementationFactualDate, that.correctiveActionsImplementationFactualDate)
                && Objects.equals(findingClosureConfirmation, that.findingClosureConfirmation);
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
