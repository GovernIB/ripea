/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * Informació d'una carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CarpetaDto extends ContingutDto {

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
