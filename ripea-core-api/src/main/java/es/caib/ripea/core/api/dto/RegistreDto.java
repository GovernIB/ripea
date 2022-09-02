/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreDto {

	private Long id;
	private String aplicacioCodi;
	private String aplicacioVersio;
	private String assumpteCodiCodi;
	private String assumpteCodiDescripcio;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private Date data;
	private String docFisicaCodi;
	private String docFisicaDescripcio;
	private String entitatCodi;
	private String entitatDescripcio;
	private String expedientNumero;
	private String exposa;
	private String extracte;
	private String procedimentCodi;
	private String identificador;
	private String idiomaCodi;
	private String idiomaDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String observacions;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private Date origenData;
	private String origenRegistreNumero;
	private String refExterna;
	private String solicita;
	private String transportNumero;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String usuariCodi;
	private String usuariNom;
	private String destiCodi;
	private String destiDescripcio;
	
	private List<RegistreInteressatDto> interessats;
	private List<RegistreAnnexDto> annexos;
	private String justificantArxiuUuid;

	private RegistreJustificantDto justificant;
	
	
    public String getDestiCodiINom() {
    	return destiCodi + " - " + destiDescripcio;
    }
	

}
