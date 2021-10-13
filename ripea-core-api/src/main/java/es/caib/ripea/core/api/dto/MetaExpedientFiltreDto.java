package es.caib.ripea.core.api.dto;

import lombok.Data;

@Data
public class MetaExpedientFiltreDto {

	private String codi;
	private String nom;
	private String classificacioSia;
	private Long organGestorId;
	private MetaExpedientActiuEnumDto actiu;

	private MetaExpedientAmbitEnumDto ambit;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	private MetaExpedientRevisioEstatEnumDto[] revisioEstats;

}
