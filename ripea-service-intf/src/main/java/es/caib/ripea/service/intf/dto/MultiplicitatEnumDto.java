package es.caib.ripea.service.intf.dto;

public enum MultiplicitatEnumDto {
	M_1,
	M_0_1,
	M_0_N,
	M_1_N;
	
    /**
     * Indica si esta multiplicidad es obligatoria (mínimo >= 1)
     */
    public boolean esObligatoria() {
        return this == M_1 || this == M_1_N;
    }
    
    /**
     * Indica si permite múltiples valores
     */
    public boolean esMultiple() {
        return this == M_0_N || this == M_1_N;
    }
}