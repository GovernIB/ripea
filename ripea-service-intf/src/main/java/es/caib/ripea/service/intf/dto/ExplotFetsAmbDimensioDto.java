package es.caib.ripea.service.intf.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExplotFetsAmbDimensioDto {

	private Long entitatId;
	private Long procedimentId;
	private Long organId;
	private String usuariCodi;
	
	private Long aux;
	
	private Long procedimentActiusTotal;
	private Long serveisActiusTotal;
	
	private Long expedientsOberts;
	private Long expedientsObertsTotal;
	private Long expedientsTancats;
	private Long expedientsTancatsTotal;
	
	private Long tasquesPendents;
	private Long tasquesPendentsTotal;
	private Long tasquesIniciades;
	private Long tasquesIniciadesTotal;
	private Long tasquesFinalitzadesDinsTermini;
	private Long tasquesFinalitzadesForaTermini;
	private Long tasquesFinalitzadesTotalDinsTermini;
	private Long tasquesFinalitzadesTotalForaTermini;	
	private Long tasquesCancelades;
	private Long tasquesCanceladesTotal;
	private Long tasquesRebutjades;
	private Long tasquesRebutjadesTotal;
	private Long tasquesAgafades;
	private Long tasquesAgafadesTotal;
	
	private Long anotacionsNoves;
	private Long anotacionsNovesTotal;
	private Long anotacionsProcessades;
	private Long anotacionsProcessadesTotal;
	private Long anotacionsRebutjades;
	private Long anotacionsRebutjadesTotal;
	
	private Long pinbalEnviamentsOk;
	private Long pinbalEnviamentsError;
	private Long pinbalEnviamentsTotalOk;
	private Long pinbalEnviamentsTotalError;
	
	private Long notificacionsEnviades;
	private Long notificacionsEnviadesTotal;
	private Long notificacionsPendents;
	private Long notificacionsPendentsTotal;
	private Long notificacionsRegistrades;
	private Long notificacionsRegistradesTotal;
	private Long notificacionsFinalitzades;
	private Long notificacionsFinalitzadesTotal;
	private Long notificacionsProcessades;
	private Long notificacionsProcessadesTotal;
	private Long notificacionsEnvError;
	private Long notificacionsEnvErrorTotal;
	private Long notificacionsFinError;
	private Long notificacionsFinErrorTotal;
	
	private Long firmesIniciades;
	private Long firmesIniciadesTotal;
	private Long firmesPausades;
	private Long firmesPausadesTotal;
	private Long firmesFirmades;
	private Long firmesFirmadesTotal;
	private Long firmesRebutjades;
	private Long firmesRebutjadesTotal;
	private Long firmesParcials;
	private Long firmesParcialsTotal;
	
	public enum FetsEnum {
		PROCEDIMENTS_ACTIUS_TOTAL,
		SERVEIS_ACTIUS_TOTAL,
        EXP_OBERTS,
        EXP_OBERTS_TOTAL,
        EXP_TANCATS,
        EXP_TANCATS_TOTAL,
        TAS_PENDENTS,
        TAS_PENDENTS_TOTAL,
        TAS_INICIADES,
        TAS_INICIADES_TOTAL,
        TAS_FINALITZADES_DINS_TERMINI,
        TAS_FINALITZADES_FORA_TERMINI,
        TAS_FINALITZADES_TOTAL_DINS_TERMINI,
        TAS_FINALITZADES_TOTAL_FORA_TERMINI,        
        TAS_CANCELADES,
        TAS_CANCELADES_TOTAL,
        TAS_REBUTJADES,
        TAS_REBUTJADES_TOTAL,
        TAS_AGAFADES,
        TAS_AGAFADES_TOTAL,
        ANO_NOVES,
        ANO_NOVES_TOTAL,
        ANO_PROCESSADES,
        ANO_PROCESSADES_TOTAL,
        ANO_REBUTJADES,
        ANO_REBUTJADES_TOTAL,
        PIN_ENVIAMENTS_OK,
        PIN_ENVIAMENTS_ERROR,
        PIN_ENVIAMENTS_TOTAL_OK,
        PIN_ENVIAMENTS_TOTAL_ERROR,
        NOT_ENVIADES,
        NOT_ENVIADES_TOTAL,
        NOT_PENDENTS,
        NOT_PENDENTS_TOTAL,
        NOT_REGISTRADES,
        NOT_REGISTRADES_TOTAL,
        NOT_FINALITZADES,
        NOT_FINALITZADES_TOTAL,
        NOT_PROCESSADES,
        NOT_PROCESSADES_TOTAL,
        NOT_ENVIADES_ERROR,
        NOT_ENVIADES_ERROR_TOTAL,
        NOT_FINALITZADES_ERROR,
        NOT_FINALITZADES_ERROR_TOTAL,
        FIR_INICIADES,
        FIR_INICIADES_TOTAL,
        FIR_PAUSADES,
        FIR_PAUSADES_TOTAL,
        FIR_FIRMADES,
        FIR_FIRMADES_TOTAL,
        FIR_REBUTJADES,
        FIR_REBUTJADES_TOTAL,
        FIR_PARCIALS,
        FIR_PARCIALS_TOTAL
    }
	
	public ExplotFetsAmbDimensioDto(Long entitatId, Long procedimentId, Long organId, String usuariCodi, Long aux) {
		super();
		this.entitatId = entitatId;
		this.procedimentId = procedimentId;
		this.organId = organId;
		this.usuariCodi = usuariCodi;
		this.aux = aux;
	}
	
	public boolean isSameDimensio(ExplotFetsAmbDimensioDto other) {
		if (other == null) return false;
		return Objects.equals(entitatId, other.entitatId) && Objects.equals(organId, other.organId)
				&& Objects.equals(procedimentId, other.procedimentId) && Objects.equals(usuariCodi, other.usuariCodi);
	}
}