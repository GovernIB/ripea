/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * Enumeració amb els possibles tipus genèrics del tipus de documents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum MetaDocumentTipusGenericEnumDto {
	OTROS ("OTROS"),
	ACUSE_RECIBO_NOTIFICACION ("ACUSE_RECIBO_NOTIFICACION"),
	NOTIFICACION ("NOTIFICACION"),
	JUSTIFICANTE_REGISTRO ("JUSTIFICANTE_REGISTRO");
	
	private final String value;
	private final String id;
	
	MetaDocumentTipusGenericEnumDto(String value) {
		this.value = value;
		this.id = this.name();
	}
	public String getValue() {
		return value;
	}
	public String getId() {
		return id;
	}
	public static MetaDocumentTipusGenericEnumDto getType(String value) {
        if (value == null)
            return null;
         for (MetaDocumentTipusGenericEnumDto interessatDocumentTipusEnum : MetaDocumentTipusGenericEnumDto.values()) {
            if (value.equals(interessatDocumentTipusEnum.getValue())) {
                return interessatDocumentTipusEnum;
            }
        }
        throw new IllegalArgumentException("No matching type in InteressatDocumentTipusEnumDto for code " + value);
    }
}
