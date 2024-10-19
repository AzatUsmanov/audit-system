package system.audit.enums;

public enum AuditType {
    HO("HO"),
    SL("SL"),
    PAC("PAC"),
    VCA_NC("VCA NC"),
    VCA_SIO("VCA SIO"),
    VCA_ISM_DOC("VCA ISM DOC"),
    VCA_ISM_SMC("VCA ISM SMC"),
    VCA_ISPS("VCA ISPS"),
    VCA_MLC("VCA MLC"),
    VCA_MAE("VCA MAE");

    public final String view;

    AuditType(String view) {
        this.view = view;
    }
}
