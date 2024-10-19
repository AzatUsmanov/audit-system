package system.audit.enums;

public enum AuditField {
    AUDIT_NAME("auditName",
            "Audit ID"),
    CERTIFICATION_BODY(
            "certificationBody",
            "Certification body"),
    AUDIT_START_DATE(
            "auditStartDate",
            "Audit start date"),
    AUDIT_COMPLETION_DATE(
            "auditCompletionDate",
            "Audit completion date"),
    OFFICE(
            "office",
            "Office"),
    CONTACT_DETAILS(
            "contactDetails",
            "Contact details"),
    AUDIT_TYPE(
            "auditType",
            "Audit type"),
    AUDIT_PLAN_ACCEPTANCE(
            "auditPlanAcceptance",
            "Audit plan acceptance"),
    AUDIT_REPORT_ACCEPTANCE(
            "auditReportAcceptance",
            "Audit report acceptance");

    public final String originalFieldName;
    public final String view;


    AuditField(String originalFieldName, String view){
        this.originalFieldName = originalFieldName;
        this.view = view;
    }
}
