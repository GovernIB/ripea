/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles estats dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ArxiuExpedientEstat {

	OBERT("E01"),
	TANCAT("E02"),
	INDEX_REMISSIO("E03");

	private final String valor;
	private ArxiuExpedientEstat(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, ArxiuExpedientEstat> lookup;
	static {
		lookup = new HashMap<String, ArxiuExpedientEstat>();
		for (ArxiuExpedientEstat s: EnumSet.allOf(ArxiuExpedientEstat.class))
			lookup.put(s.getValor(), s);
	}
	public static ArxiuExpedientEstat valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
