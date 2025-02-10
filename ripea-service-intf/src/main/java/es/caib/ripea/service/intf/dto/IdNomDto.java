package es.caib.ripea.service.intf.dto;

import lombok.Data;

@Data
public class IdNomDto {
	
	public IdNomDto(
			Long id,
			String nom) {
		this.id = id;
		this.nom = nom;
	}
	
	private Long id;
	private String nom;
	
}
