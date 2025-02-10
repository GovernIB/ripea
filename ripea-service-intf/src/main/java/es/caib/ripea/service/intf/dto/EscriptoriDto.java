/**
 * 
 */
package es.caib.ripea.service.intf.dto;


/**
 * Informació d'un escriptori d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EscriptoriDto extends ContingutDto {

	protected EscriptoriDto copiarContenidor(ContingutDto original) {
		EscriptoriDto copia = new EscriptoriDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}

}
