/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class CarpetaDto extends ContingutDto {

	private ExpedientDto expedientRelacionat;
	
	// Per evitar errors en el cercador de contenedors per admins
	public String getMetaNode() {
		return null;
	}

	protected CarpetaDto copiarContenidor(ContingutDto original) {
		CarpetaDto copia = new CarpetaDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}

}
