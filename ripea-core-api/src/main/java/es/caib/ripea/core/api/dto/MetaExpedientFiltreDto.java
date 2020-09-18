package es.caib.ripea.core.api.dto;

import lombok.Data;

@Data
public class MetaExpedientFiltreDto {

	private String codi;
	private String nom;
	private Long organGestorId;
	private Boolean veureTots = true;
	private MetaExpedientActiuEnumDto actiu;

}
