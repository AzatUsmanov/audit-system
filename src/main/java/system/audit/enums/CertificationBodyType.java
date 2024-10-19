package system.audit.enums;

public enum CertificationBodyType {
    IACS("IACS"),
    IFC_GLOBAL("IFC GLOBAL");

    public final String view;

    CertificationBodyType(String view) {
        this.view = view;
    }
}
