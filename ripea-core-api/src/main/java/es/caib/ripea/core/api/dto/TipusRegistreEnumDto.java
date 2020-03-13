package es.caib.ripea.core.api.dto;

public enum TipusRegistreEnumDto {
	ENTRADA ("0"),
	SORTIDA ("1");
	
	private final String label;
	private final String id;
	
	TipusRegistreEnumDto(String label) {
		this.label = label;
		this.id = this.name();
		
	}
	public String getLabel() {
		return label;
	}
	public String getId() {
		return id;
	}
	public static TipusRegistreEnumDto getType(String label) {
        if (label == null)
            return null;
         for (TipusRegistreEnumDto tipusRegistreEnum : TipusRegistreEnumDto.values()) {
            if (label.equals(tipusRegistreEnum.getLabel())) {
                return tipusRegistreEnum;
            }
        }
        throw new IllegalArgumentException("No matching type in TipusRegistreEnumDto for code " + label);
    }
}
