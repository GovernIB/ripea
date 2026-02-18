package es.caib.ripea.plugin.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus d'un interessat
 * del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreInteressatDocumentTipusEnum {

	NIF("N"),
	CIF("C"),
	PASSAPORT("P"),
	DOCUMENT_IDENTIFICACIO_EXTRANGERS("E"),
	ALTRES_PERSONES_FISIQUES("X"),
	CODI_ORIGEN("O");

	private final String valor;
	private RegistreInteressatDocumentTipusEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreInteressatDocumentTipusEnum> lookup;

	static {
		lookup = new HashMap<>();
		for (var s: EnumSet.allOf(RegistreInteressatDocumentTipusEnum.class)) {
			lookup.put(s.getValor(), s);
		}
	}

	public static RegistreInteressatDocumentTipusEnum valorAsEnum(String valor) {
        return valor != null ? lookup.get(valor) : null;
    }

}
