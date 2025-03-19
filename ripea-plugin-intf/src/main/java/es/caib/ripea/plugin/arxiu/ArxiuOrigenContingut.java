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
public enum ArxiuOrigenContingut {

	CIUTADA("0"),
	ADMINISTRACIO("1");

	private final String valor;
	private ArxiuOrigenContingut(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, ArxiuOrigenContingut> lookup;
	static {
		lookup = new HashMap<String, ArxiuOrigenContingut>();
		for (ArxiuOrigenContingut s: EnumSet.allOf(ArxiuOrigenContingut.class))
			lookup.put(s.getValor(), s);
	}
	public static ArxiuOrigenContingut valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
