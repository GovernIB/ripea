package es.caib.ripea.service.intf.utils;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;

/**
 * Interessats que poden ser el titular d'una consulta PINBAL segons els tipus de document que admet el servei (els del
 * servei a RIPEA, que la tasca en segon pla manté iguals que els de PINBAL). Si el servei no té cap tipus permès, no
 * restringeix el titular. Ho fan servir el filtre de titulars del formulari de consulta (JSP i REACT) i la comprovació
 * en fer la consulta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class PinbalTitularUtils {

	private PinbalTitularUtils() {
	}

	/**
	 * Tipus de document de les persones físiques que admet el servei: el NIF si admet DNI o NIF, el NIE i el passaport.
	 * Amb un NIF, PINBAL rep DNI si el servei l'admet i, si no, NIF.
	 */
	public static Set<InteressatDocumentTipusEnumDto> documentsPersonaFisicaPermesos(
			Collection<PinbalServeiDocPermesEnumDto> docsPermesos) {
		Set<InteressatDocumentTipusEnumDto> resultat = EnumSet.noneOf(InteressatDocumentTipusEnumDto.class);
		if (docsPermesos == null) {
			return resultat;
		}
		if (docsPermesos.contains(PinbalServeiDocPermesEnumDto.DNI) || docsPermesos.contains(PinbalServeiDocPermesEnumDto.NIF)) {
			resultat.add(InteressatDocumentTipusEnumDto.NIF);
		}
		if (docsPermesos.contains(PinbalServeiDocPermesEnumDto.NIE)) {
			resultat.add(InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS);
		}
		if (docsPermesos.contains(PinbalServeiDocPermesEnumDto.PASSAPORT)) {
			resultat.add(InteressatDocumentTipusEnumDto.PASSAPORT);
		}
		return resultat;
	}

	/** Si el servei admet persones jurídiques: si admet CIF o NIF. */
	public static boolean isPersonaJuridicaPermesa(Collection<PinbalServeiDocPermesEnumDto> docsPermesos) {
		return docsPermesos != null
				&& (docsPermesos.contains(PinbalServeiDocPermesEnumDto.CIF) || docsPermesos.contains(PinbalServeiDocPermesEnumDto.NIF));
	}

	/**
	 * Si un interessat pot ser el titular d'una consulta al servei. Si el servei restringeix el tipus de document, les
	 * persones físiques amb un altre tipus de document i les administracions no poden ser-ho.
	 *
	 * @param docsPermesos
	 *            tipus de document que admet el servei (buit o null: sense restricció).
	 */
	public static boolean isTitularPermes(
			InteressatTipusEnumDto tipus,
			InteressatDocumentTipusEnumDto documentTipus,
			Collection<PinbalServeiDocPermesEnumDto> docsPermesos) {
		if (docsPermesos == null || docsPermesos.isEmpty()) {
			return true;
		}
		if (tipus == InteressatTipusEnumDto.PERSONA_FISICA) {
			return documentTipus != null && documentsPersonaFisicaPermesos(docsPermesos).contains(documentTipus);
		}
		if (tipus == InteressatTipusEnumDto.PERSONA_JURIDICA) {
			return isPersonaJuridicaPermesa(docsPermesos);
		}
		return false;
	}

}
