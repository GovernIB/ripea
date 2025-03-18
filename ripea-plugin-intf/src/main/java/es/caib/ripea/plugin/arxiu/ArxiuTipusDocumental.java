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
public enum ArxiuTipusDocumental {

	RESOLUCIO("TD01"),
	ACORD("TD02"),
	CONTRACTE("TD03"),
	CONVENI("TD04"),
	DECLARACIO("TD05"),
	COMUNICACIO("TD06"),
	NOTIFICACIO("TD07"),
	PUBLICACIO("TD08"),
	JUSTIFICANT_RECEPCIO("TD09"),
	ACTA("TD10"),
	CERTIFICAT("TD11"),
	DILIGENCIA("TD12"),
	INFORME("TD13"),
	SOLICITUD("TD14"),
	DENUNCIA("TD15"),
	ALEGACIO("TD16"),
	RECURSOS("TD17"),
	COMUNICACIO_CIUTADA("TD18"),
	FACTURA("TD19"),
	ALTRES_INCAUTATS("TD20"),
	ALTRES("TD99");

	private final String valor;
	private ArxiuTipusDocumental(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, ArxiuTipusDocumental> lookup;
	static {
		lookup = new HashMap<String, ArxiuTipusDocumental>();
		for (ArxiuTipusDocumental s: EnumSet.allOf(ArxiuTipusDocumental.class))
			lookup.put(s.getValor(), s);
	}
	public static ArxiuTipusDocumental valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
