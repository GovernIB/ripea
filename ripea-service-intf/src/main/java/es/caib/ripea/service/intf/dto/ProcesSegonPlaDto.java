package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estat d'un procés en segon pla (p.ex. el tancament d'un expedient) que l'usuari pot seguir en temps real.
 *
 * Mentre és EN_CURS, l'element sobre el qual s'executa (elementTipus + elementId) queda bloquejat: ningú més
 * hi pot llançar un altre procés ni modificar-lo (veure ProcesSegonPlaHelper).
 *
 * Es fa servir de dues maneres:
 *  - Complet (amb passos): el que consulta periòdicament el diàleg de progrés de l'usuari que l'ha llançat.
 *  - Resum (sense passos): el que s'informa al recurs de l'element perquè la interfície sàpiga que hi ha un
 *    procés en curs i de qui és.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProcesSegonPlaDto implements Serializable {

	private static final long serialVersionUID = -3518822361722870542L;

	private String operacioId;
	private ProcesSegonPlaTipusEnumDto tipus;
	private ElementTipusEnumDto elementTipus;
	private Long elementId;
	private String usuariCodi;
	private String usuariNom;
	private Date dataInici;
	private Date dataFi;
	private ProcesSegonPlaEstatEnumDto estat;
	private String errorMsg;
	private List<ProcesSegonPlaPasDto> passos = new ArrayList<>();

	// No es serialitza: el client fa servir l'estat, i així el DTO no porta cap propietat sense setter (el recurs que
	// el conté pot tornar al servidor en un formulari).
	@JsonIgnore
	public boolean isFinalitzat() {
		return estat != null && estat != ProcesSegonPlaEstatEnumDto.EN_CURS;
	}

	/**
	 * Còpia independent. Sense passos és el resum que es pot exposar a qualsevol usuari que vegi l'element.
	 */
	public ProcesSegonPlaDto copia(boolean ambPassos) {
		ProcesSegonPlaDto copia = new ProcesSegonPlaDto();
		copia.setOperacioId(operacioId);
		copia.setTipus(tipus);
		copia.setElementTipus(elementTipus);
		copia.setElementId(elementId);
		copia.setUsuariCodi(usuariCodi);
		copia.setUsuariNom(usuariNom);
		copia.setDataInici(dataInici);
		copia.setDataFi(dataFi);
		copia.setEstat(estat);
		copia.setErrorMsg(errorMsg);
		List<ProcesSegonPlaPasDto> passosCopia = new ArrayList<>();
		if (ambPassos && passos != null) {
			for (ProcesSegonPlaPasDto pas: passos) {
				passosCopia.add(pas.copia());
			}
		}
		copia.setPassos(passosCopia);
		return copia;
	}
}
