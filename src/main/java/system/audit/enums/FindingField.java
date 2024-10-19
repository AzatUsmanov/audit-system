package system.audit.enums;


public enum FindingField {

    FINDING_NAME(
            "FindingName",
            "Finding ID"
    ),
    FINDING_GRADATION(
            "findingGradation",
            "Finding gradation"),
    FINDING_TEXT(
            "findingText",
            "Finding text"),
    ROOT_CAUSE_ANALYSIS_DEADLINE(
            "rootCauseAnalysisDeadline",
            "Root cause analysis deadline"),
    PROPOSED_CORRECTIVE_ACTIONS(
            "proposedCorrectiveActions",
            "Proposed corrective actions"),
    CORRECTIVE_ACTIONS_IMPLEMENTATION_DEADLINE(
            "correctiveActionsImplementationDeadline",
            "Corrective actions implementation deadline"),
    CORRECTIVE_ACTIONS_ACCEPTANCE(
            "correctiveActionsAcceptance",
            "Corrective actions acceptance"),
    CORRECTIVE_ACTIONS_IMPLEMENTATION_INFORMATION(
            "correctiveActionsImplementationInformation",
            "Corrective actions implementation information"),
    CORRECTIVE_ACTIONS_IMPLEMENTATION_FACTUAL_DATE(
            "correctiveActionsImplementationFactualDate",
            "Corrective actions implementation factual date"),
    FINDING_CLOSURE_CONFIRMATION(
            "findingClosureConfirmation",
            "Finding closure confirmation");

    public final String originalFieldName;
    public final String view;


    FindingField(String originalFieldName, String view){
        this.originalFieldName = originalFieldName;
        this.view = view;
    }
}
