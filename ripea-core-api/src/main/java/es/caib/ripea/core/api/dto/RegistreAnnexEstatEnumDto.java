/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * Enumeració amb els possibles estats de registre annex
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreAnnexEstatEnumDto {
	CREAT,  // annex created in db
	PENDENT, // pending to create document in db and move document in arxiu
	MOGUT // created in db and moved in arxiu
}
