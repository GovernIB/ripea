package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;

/**
 * Funcions sense estat per donar d'alta serveis de PINBAL a RIPEA a partir de les dades de l'API v2 de
 * recobriment (getEntitats i getServeisPerEntitat).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class PinbalServeiUtils {

	/** Longitud màxima del nom del servei a RIPEA (columna NOM de IPA_PINBAL_SERVEI). */
	public static final int NOM_MAX_LENGTH = 256;

	private PinbalServeiUtils() {
	}

	/**
	 * Entitat de PINBAL amb el CIF indicat (el d'una entitat de RIPEA). La comparació no distingeix majúscules
	 * ni espais.
	 *
	 * @return l'entitat de PINBAL, o null si no n'hi ha cap amb aquest CIF.
	 */
	public static Entitat trobarEntitatPerCif(List<Entitat> entitatsPinbal, String cif) {
		String cifNormalitzat = normalitzar(cif);
		if (entitatsPinbal == null || cifNormalitzat == null) {
			return null;
		}
		return entitatsPinbal.stream()
				.filter(e -> e != null && e.getCodi() != null && cifNormalitzat.equals(normalitzar(e.getCif())))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Serveis actius de PINBAL que encara no estan donats d'alta a RIPEA. La comparació de codis no distingeix
	 * majúscules ni espais.
	 *
	 * @return els serveis nous, ordenats per codi.
	 */
	public static List<ServeiBasic> filtrarServeisNous(List<ServeiBasic> serveisPinbal, Collection<String> codisRipea) {
		if (serveisPinbal == null) {
			return new ArrayList<>();
		}
		Set<String> codis = codisRipea == null ? Set.of() : codisRipea.stream()
				.map(PinbalServeiUtils::normalitzar)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		return serveisPinbal.stream()
				.filter(s -> s != null
						&& normalitzar(s.getCodi()) != null
						&& Boolean.TRUE.equals(s.getActiu())
						&& !codis.contains(normalitzar(s.getCodi())))
				.sorted(Comparator.comparing(ServeiBasic::getCodi))
				.collect(Collectors.toList());
	}

	/**
	 * Converteix els tipus de document permesos de PINBAL als de RIPEA. Els que RIPEA no contempla (CSV,
	 * NumeroIdentificacion i Otros) s'ignoren. Una llista buida a RIPEA vol dir que el servei no restringeix el
	 * tipus de document del titular.
	 */
	public static List<PinbalServeiDocPermesEnumDto> toDocsPermesos(List<DocumentTipus> tipusPinbal) {
		List<PinbalServeiDocPermesEnumDto> resultat = new ArrayList<>();
		if (tipusPinbal == null) {
			return resultat;
		}
		for (DocumentTipus tipus : tipusPinbal) {
			PinbalServeiDocPermesEnumDto docPermes = toDocPermes(tipus);
			if (docPermes != null && !resultat.contains(docPermes)) {
				resultat.add(docPermes);
			}
		}
		return resultat;
	}

	/** Descripció del servei a PINBAL retallada a la longitud del nom a RIPEA. */
	public static String retallarNom(String descripcio) {
		if (descripcio == null) {
			return null;
		}
		String nom = descripcio.trim();
		return nom.length() > NOM_MAX_LENGTH ? nom.substring(0, NOM_MAX_LENGTH) : nom;
	}

	private static PinbalServeiDocPermesEnumDto toDocPermes(DocumentTipus tipus) {
		if (tipus == null) {
			return null;
		}
		switch (tipus) {
			case DNI: return PinbalServeiDocPermesEnumDto.DNI;
			case NIF: return PinbalServeiDocPermesEnumDto.NIF;
			case CIF: return PinbalServeiDocPermesEnumDto.CIF;
			case NIE: return PinbalServeiDocPermesEnumDto.NIE;
			case Pasaporte: return PinbalServeiDocPermesEnumDto.PASSAPORT;
			default: return null;
		}
	}

	private static String normalitzar(String valor) {
		if (valor == null) {
			return null;
		}
		String normalitzat = valor.trim().toUpperCase(Locale.ROOT);
		return normalitzat.isEmpty() ? null : normalitzat;
	}

}
