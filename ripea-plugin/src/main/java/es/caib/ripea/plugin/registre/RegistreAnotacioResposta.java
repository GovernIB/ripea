/**
 * 
 */
package es.caib.ripea.plugin.registre;

import es.caib.ripea.core.api.registre.RegistreAnotacio;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;


/**
 * Classe que representa una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnotacioResposta {

	private RegistreTipusEnum tipus;
	private String unitatAdministrativa;
	private RegistreAnotacio registreAnotacio;



	public RegistreTipusEnum getTipus() {
		return tipus;
	}
	public void setTipus(RegistreTipusEnum tipus) {
		this.tipus = tipus;
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public RegistreAnotacio getRegistreAnotacio() {
		return registreAnotacio;
	}
	public void setRegistreAnotacio(RegistreAnotacio registreAnotacio) {
		this.registreAnotacio = registreAnotacio;
	}

}
