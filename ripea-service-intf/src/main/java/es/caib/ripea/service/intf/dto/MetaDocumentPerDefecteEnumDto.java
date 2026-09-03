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

	NOTIB_JUSTIFICANT_RECEPCIO(
			"Justificant de recepció de la notificació",
			null,
			"TD09",
			DocumentNtiEstadoElaboracionEnumDto.EE01,
			false),
	REGISTRE_JUSTIFICANT_ENTRADA(
			"Justificant de registre",
			null,
			"TD11",
			DocumentNtiEstadoElaboracionEnumDto.EE01,
			false),
	NOTIFICACIO_MULTIPLE(
			"Notificació de múltiples documents",
			"Al seleccionar varis documents de un expedient i notificar-los conjuntament, si son tots PDFs es combinarán en un sol PDF, en cas contrari es generará un zip que contendrá els documents.",
			"TD07",
			DocumentNtiEstadoElaboracionEnumDto.EE99,
			false),
	OTROS(
			"Otros",
			"Altres documents del procediment",
			"TD99",
			DocumentNtiEstadoElaboracionEnumDto.EE01,
			true);

	private final String nom;
	private final String descripcio;
	private final String ntiTipoDocumental;
	private final DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private final boolean perDefecte;

	MetaDocumentPerDefecteEnumDto(
			String nom,
			String descripcio,
			String ntiTipoDocumental,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			boolean perDefecte) {
		this.nom = nom;
		this.descripcio = descripcio;
		this.ntiTipoDocumental = ntiTipoDocumental;
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
		this.perDefecte = perDefecte;
	}

	/** El codi del tipus de document coincideix amb el nom de la constant. */
	public String getCodi() {
		return name();
	}
	public String getNom() {
		return nom;
	}
	/** Descripció del tipus de document; null si no en té cap. */
	public String getDescripcio() {
		return descripcio;
	}
	public String getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	/**
	 * Indica si, en crear un procediment nou, aquest tipus de document queda marcat com el tipus
	 * per defecte del procediment (el que es proposa en crear documents).
	 */
	public boolean isPerDefecte() {
		return perDefecte;
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
