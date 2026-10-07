package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
public class IntegracioDto implements Serializable {

	private String codi;
	private String nom;
    private List<IntegracioDto> subConjunt;

	private static final long serialVersionUID = -139254994389509932L;
}
