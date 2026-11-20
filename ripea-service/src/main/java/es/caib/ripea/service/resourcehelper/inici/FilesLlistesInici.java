package es.caib.ripea.service.resourcehelper.inici;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;

/**
 * Files de les llistes resum de la pantalla d'inici ({@link LlistaInici}). Cada fila és un mapa amb les columnes de
 * la llista i els identificadors dels seus enllaços; el front les llegeix per aquestes claus.
 *
 * S'han de cridar dins de la transacció de la consulta, perquè hi puguin llegir les relacions LAZY.
 */
public final class FilesLlistesInici {

	/** Llargada màxima dels textos llargs (missatges, comentaris): la llista només en mostra el principi. */
	public static final int MAXIM_TEXT = 300;

	private FilesLlistesInici() {}

	/**
	 * Tasca de «Tasques pendents per data límit». Els indicadors foraTermini i vencimentProper fan servir els mateixos
	 * límits en dies sencers que les targetes (veure {@link CriterisIndicadorsInici#tasquesForaTermini}).
	 *
	 * @param dema           inici de demà.
	 * @param fiPreavisExclos inici del primer dia que ja no és dins del preavís.
	 */
	public static Map<String, Object> tascaPerDataLimit(ExpedientTascaResourceEntity tasca, Date dema, Date fiPreavisExclos) {
		Map<String, Object> fila = tasca(tasca);
		Date dataLimit = tasca.getDataLimit();
		boolean foraTermini = dataLimit != null && dataLimit.before(dema);
		fila.put("dataLimit", dataLimit);
		fila.put("foraTermini", foraTermini);
		fila.put("vencimentProper", dataLimit != null && !foraTermini && dataLimit.before(fiPreavisExclos));
		return fila;
	}

	/** Tasca de «Tasques sense activitat», amb els responsables i la data de la darrera activitat. */
	public static Map<String, Object> tascaSenseActivitat(ExpedientTascaResourceEntity tasca, LocalDateTime darreraActivitat) {
		Map<String, Object> fila = tasca(tasca);
		fila.put("responsables", tasca.getResponsablesStr());
		fila.put("darreraActivitat", darreraActivitat);
		return fila;
	}

	public static Map<String, Object> expedientPerTancar(ExpedientResourceEntity expedient) {
		Map<String, Object> fila = new LinkedHashMap<>();
		fila.put("id", expedient.getId());
		fila.put("numero", expedient.getNumero());
		fila.put("nom", expedient.getNom());
		fila.put("procediment", expedient.getMetaExpedient() != null ? expedient.getMetaExpedient().getNom() : null);
		fila.put("createdDate", expedient.getCreatedDate());
		return fila;
	}

	/** @param nom nom de la integració per mostrar (el de les pestanyes del monitor). */
	public static Map<String, Object> integracio(IntegracioResourceEntity integracio, String nom) {
		Map<String, Object> fila = new LinkedHashMap<>();
		fila.put("id", integracio.getId());
		fila.put("data", integracio.getData());
		fila.put("integracio", integracio.getCodi());
		fila.put("integracioNom", nom);
		fila.put("descripcio", integracio.getDescripcio());
		fila.put("tipus", integracio.getTipus());
		fila.put("estat", integracio.getEstat());
		fila.put("tempsResposta", integracio.getTempsResposta());
		return fila;
	}

	/** Excepció amb la data de la darrera ocurrència (les repetides s'agrupen en una sola fila). */
	public static Map<String, Object> excepcio(ExcepcioLogResourceEntity excepcio) {
		Map<String, Object> fila = new LinkedHashMap<>();
		fila.put("id", excepcio.getId());
		fila.put("data", excepcio.getDataDarrera() != null ? excepcio.getDataDarrera() : excepcio.getData());
		fila.put("tipus", excepcio.getTipus());
		fila.put("missatge", StringUtils.abbreviate(excepcio.getMessage(), MAXIM_TEXT));
		fila.put("numOcurrencies", excepcio.getNumOcurrencies());
		return fila;
	}

	/** @param autor usuari autor del comentari (null si ja no existeix: es mostra el codi). */
	public static Map<String, Object> comentari(MetaExpedientComentariResourceEntity comentari, UsuariResourceEntity autor) {
		MetaExpedientResourceEntity procediment = comentari.getMetaExpedient();
		Map<String, Object> fila = new LinkedHashMap<>();
		fila.put("id", comentari.getId());
		fila.put("procedimentId", procediment.getId());
		fila.put("procedimentCodi", procediment.getCodi());
		fila.put("procedimentNom", procediment.getNom());
		fila.put("autor", nomUsuari(comentari.getCreatedBy(), autor));
		fila.put("data", comentari.getCreatedDate());
		fila.put("text", StringUtils.abbreviate(comentari.getText(), MAXIM_TEXT));
		return fila;
	}

	/** Procediment de «Procediments pendents de revisió»: la data és la de la darrera modificació. */
	public static Map<String, Object> procedimentPendentRevisio(MetaExpedientResourceEntity procediment) {
		Map<String, Object> fila = procediment(procediment);
		fila.put("organ", procediment.getOrganGestor() != null ? procediment.getOrganGestor().getNom() : null);
		return fila;
	}

	/**
	 * Procediment de «Procediments rebutjats», amb el seu darrer comentari (normalment, el motiu del rebuig).
	 *
	 * @param comentari darrer comentari del procediment (null si no en té).
	 * @param autor     usuari autor del comentari (null si no n'hi ha o ja no existeix).
	 */
	public static Map<String, Object> procedimentRebutjat(
			MetaExpedientResourceEntity procediment,
			MetaExpedientComentariResourceEntity comentari,
			UsuariResourceEntity autor) {
		Map<String, Object> fila = procediment(procediment);
		fila.put("comentari", comentari != null ? StringUtils.abbreviate(comentari.getText(), MAXIM_TEXT) : null);
		fila.put("comentariAutor", comentari != null ? nomUsuari(comentari.getCreatedBy(), autor) : null);
		fila.put("comentariData", comentari != null ? comentari.getCreatedDate() : null);
		return fila;
	}

	private static Map<String, Object> tasca(ExpedientTascaResourceEntity tasca) {
		Map<String, Object> fila = new LinkedHashMap<>();
		ExpedientResourceEntity expedient = tasca.getExpedient();
		fila.put("id", tasca.getId());
		fila.put("titol", StringUtils.isNotBlank(tasca.getTitol()) || tasca.getMetaExpedientTasca() == null
				? tasca.getTitol()
				: tasca.getMetaExpedientTasca().getNom());
		fila.put("estat", tasca.getEstat());
		fila.put("expedientId", expedient.getId());
		fila.put("expedientNumero", expedient.getNumero());
		fila.put("expedientNom", expedient.getNom());
		return fila;
	}

	private static Map<String, Object> procediment(MetaExpedientResourceEntity procediment) {
		Map<String, Object> fila = new LinkedHashMap<>();
		fila.put("id", procediment.getId());
		fila.put("codi", procediment.getCodi());
		fila.put("nom", procediment.getNom());
		fila.put("data", procediment.getLastModifiedDate() != null ? procediment.getLastModifiedDate() : procediment.getCreatedDate());
		return fila;
	}

	private static String nomUsuari(String codi, UsuariResourceEntity usuari) {
		return usuari != null && StringUtils.isNotBlank(usuari.getNom()) ? usuari.getNom() : codi;
	}

}
