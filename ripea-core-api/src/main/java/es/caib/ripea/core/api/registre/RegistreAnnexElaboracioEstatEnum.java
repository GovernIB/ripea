/**
 * 
 */
package es.caib.ripea.core.api.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors de la validesa d'un document
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreAnnexElaboracioEstatEnum {

	ORIGINAL("EE01"),
	COPIA_ELECT_AUTENTICA_CANVI_FORMAT("EE02"),
	COPIA_ELECT_AUTENTICA_PAPER("EE03"),
	COPIA_ELECT_AUTENTICA_PARCIAL("EE04"),
	ALTRES("EE99");

	private final String valor;
	private RegistreAnnexElaboracioEstatEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreAnnexElaboracioEstatEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreAnnexElaboracioEstatEnum>();
		for (RegistreAnnexElaboracioEstatEnum s: EnumSet.allOf(RegistreAnnexElaboracioEstatEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreAnnexElaboracioEstatEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
