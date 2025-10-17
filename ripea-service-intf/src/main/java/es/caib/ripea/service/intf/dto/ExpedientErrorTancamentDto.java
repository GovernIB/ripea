package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpedientErrorTancamentDto {

    private String numero;
    private Long id;
    private String missatgeError;

    public ExpedientErrorTancamentDto(String numero, Long id, String missatgeError) {
        this.numero = numero;
        this.id = id;
        this.missatgeError = missatgeError;
    }
    
}
