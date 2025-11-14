package es.caib.ripea.service.intf.dto;

public enum ExtendedPermissionEnum {
    READ(1),
    WRITE(2),
    CREATE(4),
    DELETE(8),
    ADMINISTRATION(16),
    STATISTICS(32),
    COMU(64),
    ADM_COMU(128),
    DISSENY(256),
    ADMINISTRATION_READ(512);

    private Integer codi;
    ExtendedPermissionEnum(Integer codi) {
        this.codi = codi;
    }
    public Integer getCodi() {
        return codi;
    }
}
