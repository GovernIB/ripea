package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un pas (una línia del registre) d'un procés en segon pla.
 *
 * El text no el genera el servidor: el front el tradueix amb la clau procesSegonPla.pas.&lt;codi&gt; i els
 * paràmetres (p.ex. {document: "Informe.pdf"}). El mateix amb la fase (procesSegonPla.fase.&lt;fase&gt;), que
 * indica què s'està fent dins el pas mentre és EN_CURS. El detall, en canvi, és text lliure (normalment el
 * missatge d'un error), i es mostra tal qual.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProcesSegonPlaPasDto implements Serializable {

	private static final long serialVersionUID = 2687434781244912630L;

	private String codi;
	private Map<String, String> params = new LinkedHashMap<>();
	private ProcesSegonPlaPasEstatEnumDto estat;
	private String fase;
	private Map<String, String> faseParams = new LinkedHashMap<>();
	private String detall;
	// Resultat del pas (procesSegonPla.resultat.<resultat>), que es mostra a continuació del text
	private String resultat;
	private Map<String, String> resultatParams = new LinkedHashMap<>();
	private Date dataInici;
	private Date dataFi;

	/** Còpia independent, per poder-la retornar mentre el fil del procés continua modificant l'original. */
	public ProcesSegonPlaPasDto copia() {
		ProcesSegonPlaPasDto copia = new ProcesSegonPlaPasDto();
		copia.setCodi(codi);
		copia.setParams(params != null ? new LinkedHashMap<>(params) : new LinkedHashMap<>());
		copia.setEstat(estat);
		copia.setFase(fase);
		copia.setFaseParams(faseParams != null ? new LinkedHashMap<>(faseParams) : new LinkedHashMap<>());
		copia.setDetall(detall);
		copia.setResultat(resultat);
		copia.setResultatParams(resultatParams != null ? new LinkedHashMap<>(resultatParams) : new LinkedHashMap<>());
		copia.setDataInici(dataInici);
		copia.setDataFi(dataFi);
		return copia;
	}
}
