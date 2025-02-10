package es.caib.ripea.service.intf.dto;

import lombok.Data;

@Data
public class MetaExpedientFiltreDto {

	private String codi;
	private String nom;
	private String classificacio;
	private Long organGestorId;
	private MetaExpedientActiuEnumDto actiu;

	private MetaExpedientAmbitEnumDto ambit;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	private MetaExpedientRevisioEstatEnumDto[] revisioEstats;

}
