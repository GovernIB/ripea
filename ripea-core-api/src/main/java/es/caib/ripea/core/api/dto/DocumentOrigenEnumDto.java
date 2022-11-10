/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * RIPEA - Document putjat manualment en Ripea
 * DISTRIBUCIO - Document que vene d'una anotació de registre
 * PINBAL - Document generat de les resposta de PINBAL
 *
 */
public enum DocumentOrigenEnumDto { 
	/**
	 * Document putjat manualment en Ripea
	 */
	RIPEA,
	/**
	 * Document que vene d'una anotació de registre
	 */
	DISTRIBUCIO,
	/**
	 * Document generat de les resposta de PINBAL
	 */
	PINBAL,
	ANOTHER // TODO should be change for specific other cases

}
