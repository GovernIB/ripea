package es.caib.ripea.service.intf.utils;

import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;

/**
 * Nom de fitxer i títol del document del justificant de registre, i com es reconeix aquest document.
 *
 * Viu a ripea-service-intf perquè el comparteixen totes les vies d'incorporació: el procés automàtic i
 * l'acceptació de l'anotació des de REACT (ripea-service) i des de JSP (ripea-back, que només veu aquest
 * mòdul en el perfil jboss). El nom ha de ser idèntic a totes elles perquè la detecció de duplicats es fa
 * amb {@code existsByExpedientIdAndFitxerNom}.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreJustificantUtils {

	private RegistreJustificantUtils() {}

	/** Nom del fitxer: TIPUSDOC_&lt;idAnotacio&gt;_&lt;registre.identificador amb / -&gt; _&gt;.pdf */
	public static String nomFitxerJustificant(Long anotacioRegistreId, String registreIdentificador) {
		String identificadorPerNom = registreIdentificador != null
			? registreIdentificador.replace("/", "_")
			: String.valueOf(anotacioRegistreId);
		return MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi()
			+ "_" + anotacioRegistreId + "_" + identificadorPerNom + ".pdf";
	}

	/** Títol del document del justificant. */
	public static String titolJustificant(String registreIdentificador) {
		String titol = "Justificant del registre";
		return registreIdentificador != null ? titol + " " + registreIdentificador : titol;
	}

	/**
	 * Indica si un document és el justificant de registre d'una anotació, que de moment no es pot esborrar: amb la
	 * propietat es.caib.ripea.document.esborrar.finals activada, esborrar-lo el mouria a l'Arxiu, i el justificant
	 * pendent de moure és el de la sèrie de Regweb.
	 *
	 * És justificant el document que no prové d'un annex de l'anotació i que, o bé està marcat com a pendent de moure a
	 * l'expedient de l'Arxiu (tots els que s'incorporen ara), o bé té el número de registre i el tipus de document
	 * REGISTRE_JUSTIFICANT_ENTRADA, que és com el procés en segon pla troba el justificant d'una anotació.
	 *
	 * @param justificantMoureError el document està marcat com a justificant pendent de moure a l'Arxiu.
	 * @param metaDocumentCodi codi del tipus de document, o null si no en té.
	 * @param numeroRegistre número de registre del document, o null si no en té.
	 * @param documentDeAnotacio el document prové d'un annex d'una anotació.
	 */
	public static boolean isJustificantRegistre(
			boolean justificantMoureError,
			String metaDocumentCodi,
			String numeroRegistre,
			boolean documentDeAnotacio) {
		if (documentDeAnotacio) {
			return false;
		}
		return justificantMoureError || (numeroRegistre != null
				&& MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi().equals(metaDocumentCodi));
	}
}
