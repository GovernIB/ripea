package es.caib.ripea.service.intf.utils;

import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;

/**
 * Nom de fitxer i títol del document del justificant de registre.
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
}
