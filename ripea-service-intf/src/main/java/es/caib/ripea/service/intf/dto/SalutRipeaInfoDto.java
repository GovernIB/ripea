package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SalutRipeaInfoDto {
	private String codi; //Codi del subsistema o de la integració
	private long peticionsOk = 0;
	private long peticionsError = 0;
}