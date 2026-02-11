package es.caib.ripea.plugin.registre;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Resposta a una consulta de registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaConsultaRegistre extends RespostaBase implements Serializable {

	private static final long serialVersionUID = -7743620649741298859L;
	
	private String registreNumero;
	private Date registreData;
	private Date sirRecepecioData;
	private Date sirRegistreDestiData;
	private String oficinaCodi;
	private String oficinaDenominacio;
	private String entitatCodi;
	private String entitatDenominacio;
	private String registreNumeroFormatat;
	private String codiLlibre;
	private String numeroRegistroDestino;
	private String motivo;
	private String codigoEntidadRegistralProcesado;
	private String decodificacionEntidadRegistralProcesado;
	private List<RegistreAnnex> annexos;
	private List<RegistreDadesInteressat> interessats;
	
}
