package es.caib.ripea.service.helper;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaEstatEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaPasDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaPasEstatEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaTipusEnumDto;

/**
 * Estat en memòria d'un procés en segon pla (veure {@link ProcesSegonPlaHelper}).
 *
 * El modifica el fil que executa el procés (a través de {@link ProcesSegonPlaContext}) i el llegeixen les peticions
 * que en consulten el progrés. Per això tots els accessos a l'estat mutable són sincronitzats i cap a fora només
 * surten còpies (copia()), que es poden serialitzar mentre el procés continua avançant.
 */
class ProcesSegonPla {

	// Dades que no canvien mai: es poden llegir sense sincronitzar
	private final String operacioId;
	private final ProcesSegonPlaTipusEnumDto tipus;
	private final ElementTipusEnumDto elementTipus;
	private final Long elementId;
	private final String usuariCodi;
	private final String usuariNom;

	// Estat mutable, protegit per this
	private final ProcesSegonPlaDto estat;

	ProcesSegonPla(
			String operacioId,
			ProcesSegonPlaTipusEnumDto tipus,
			ElementTipusEnumDto elementTipus,
			Long elementId,
			String usuariCodi,
			String usuariNom) {
		this.operacioId = operacioId;
		this.tipus = tipus;
		this.elementTipus = elementTipus;
		this.elementId = elementId;
		this.usuariCodi = usuariCodi;
		this.usuariNom = usuariNom;
		this.estat = new ProcesSegonPlaDto();
		estat.setOperacioId(operacioId);
		estat.setTipus(tipus);
		estat.setElementTipus(elementTipus);
		estat.setElementId(elementId);
		estat.setUsuariCodi(usuariCodi);
		estat.setUsuariNom(usuariNom);
		estat.setDataInici(new Date());
		estat.setEstat(ProcesSegonPlaEstatEnumDto.EN_CURS);
	}

	String getOperacioId() { return operacioId; }
	ProcesSegonPlaTipusEnumDto getTipus() { return tipus; }
	ElementTipusEnumDto getElementTipus() { return elementTipus; }
	Long getElementId() { return elementId; }
	String getUsuariCodi() { return usuariCodi; }
	String getUsuariNom() { return usuariNom; }

	/** Afegeix un pas EN_CURS i en retorna la posició, per poder-lo tancar encara que se n'hagin afegit d'altres. */
	synchronized int iniciarPas(String codi, Map<String, String> params) {
		ProcesSegonPlaPasDto pas = new ProcesSegonPlaPasDto();
		pas.setCodi(codi);
		pas.setParams(params != null ? new LinkedHashMap<>(params) : new LinkedHashMap<>());
		pas.setEstat(ProcesSegonPlaPasEstatEnumDto.EN_CURS);
		pas.setDataInici(new Date());
		estat.getPassos().add(pas);
		return estat.getPassos().size() - 1;
	}

	synchronized void finalitzarPas(int index, ProcesSegonPlaPasEstatEnumDto estatPas, String detall) {
		ProcesSegonPlaPasDto pas = estat.getPassos().get(index);
		pas.setEstat(estatPas);
		pas.setDetall(detall);
		pas.setDataFi(new Date());
		// La fase només té sentit mentre el pas és en curs
		pas.setFase(null);
		pas.getFaseParams().clear();
	}

	/** Informa què s'està fent dins el darrer pas que encara és en curs. Si no n'hi ha cap, no fa res. */
	synchronized void fase(String fase, Map<String, String> params) {
		for (int i = estat.getPassos().size() - 1; i >= 0; i--) {
			ProcesSegonPlaPasDto pas = estat.getPassos().get(i);
			if (pas.getEstat() == ProcesSegonPlaPasEstatEnumDto.EN_CURS) {
				pas.setFase(fase);
				pas.setFaseParams(params != null ? new LinkedHashMap<>(params) : new LinkedHashMap<>());
				return;
			}
		}
	}

	/** Informa el resultat del darrer pas que encara és en curs. Si no n'hi ha cap, no fa res. */
	synchronized void resultat(String resultat, Map<String, String> params) {
		for (int i = estat.getPassos().size() - 1; i >= 0; i--) {
			ProcesSegonPlaPasDto pas = estat.getPassos().get(i);
			if (pas.getEstat() == ProcesSegonPlaPasEstatEnumDto.EN_CURS) {
				pas.setResultat(resultat);
				pas.setResultatParams(params != null ? new LinkedHashMap<>(params) : new LinkedHashMap<>());
				return;
			}
		}
	}

	/** Afegeix una línia ja acabada (p.ex. un avís) al registre. */
	synchronized void afegirPas(String codi, Map<String, String> params, ProcesSegonPlaPasEstatEnumDto estatPas, String detall) {
		int index = iniciarPas(codi, params);
		finalitzarPas(index, estatPas, detall);
	}

	/**
	 * Marca el procés com a acabat: FINALITZAT si errorMsg és null i ERROR si no. Els passos que haguessin quedat
	 * en curs (no hauria de passar, els tanca ProcesSegonPlaContext) es tanquen amb el mateix resultat.
	 */
	synchronized void finalitzar(String errorMsg) {
		boolean ambError = errorMsg != null;
		for (int i = 0; i < estat.getPassos().size(); i++) {
			if (estat.getPassos().get(i).getEstat() == ProcesSegonPlaPasEstatEnumDto.EN_CURS) {
				finalitzarPas(i, ambError ? ProcesSegonPlaPasEstatEnumDto.ERROR : ProcesSegonPlaPasEstatEnumDto.OK, null);
			}
		}
		estat.setEstat(ambError ? ProcesSegonPlaEstatEnumDto.ERROR : ProcesSegonPlaEstatEnumDto.FINALITZAT);
		estat.setErrorMsg(errorMsg);
		estat.setDataFi(new Date());
	}

	synchronized boolean isFinalitzat() {
		return estat.isFinalitzat();
	}

	synchronized ProcesSegonPlaEstatEnumDto getEstat() {
		return estat.getEstat();
	}

	synchronized Date getDataFi() {
		return estat.getDataFi();
	}

	synchronized ProcesSegonPlaDto copia(boolean ambPassos) {
		return estat.copia(ambPassos);
	}

}
