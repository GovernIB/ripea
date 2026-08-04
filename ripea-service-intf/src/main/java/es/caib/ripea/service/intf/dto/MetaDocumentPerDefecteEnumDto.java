/**
 *
 */
package es.caib.ripea.service.intf.dto;


/**
 * Tipus de document que es creen automàticament a l'alta de tot procediment.
 *
 * Només un administrador d'entitat (IPA_ADMIN) els pot modificar o eliminar; per a la
 * resta de rols són de només consulta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum MetaDocumentPerDefecteEnumDto {

	NOTIB_JUSTIFICANT_RECEPCIO("Justificant de recepció de la notificació", "TD09"),
	REGISTRE_JUSTIFICANT_ENTRADA("Justificant de registre", "TD11");

	private final String nom;
	private final String ntiTipoDocumental;

	MetaDocumentPerDefecteEnumDto(String nom, String ntiTipoDocumental) {
		this.nom = nom;
		this.ntiTipoDocumental = ntiTipoDocumental;
	}

	/** El codi del tipus de document coincideix amb el nom de la constant. */
	public String getCodi() {
		return name();
	}
	public String getNom() {
		return nom;
	}
	public String getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}

	/**
	 * Indica si un codi de tipus de document és un dels creats per defecte i, per tant,
	 * només modificable per un administrador d'entitat.
	 */
	public static boolean isCodiPerDefecte(String codi) {
		if (codi == null) {
			return false;
		}
		for (MetaDocumentPerDefecteEnumDto metaDocumentPerDefecte : values()) {
			if (metaDocumentPerDefecte.getCodi().equals(codi)) {
				return true;
			}
		}
		return false;
	}
}
