/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles orígens de documents i expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ArxiuEstatElaboracio {

	ORIGINAL("EE01"),
	COPIA_AUTENTICA_FORMAT("EE02"),
	COPIA_AUTENTICA_PAPER("EE03"),
	COPIA_AUTENTICA_PARCIAL("EE04"),
	ALTRES("EE99");

	private final String valor;
	private ArxiuEstatElaboracio(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, ArxiuEstatElaboracio> lookup;
	static {
		lookup = new HashMap<String, ArxiuEstatElaboracio>();
		for (ArxiuEstatElaboracio s: EnumSet.allOf(ArxiuEstatElaboracio.class))
			lookup.put(s.getValor(), s);
	}
	public static ArxiuEstatElaboracio valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
