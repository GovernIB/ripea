/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class Enviament {

	private Persona titular;
	private List<Persona> destinataris;
	private EntregaPostalTipus entregaPostalTipus;
	private EntregaPostalViaTipus  entregaPostalViaTipus;
	private String entregaPostalViaNom;
	private String entregaPostalNumeroCasa;
	private String entregaPostalNumeroQualificador;
	private String entregaPostalPuntKm;
	private String entregaPostalApartatCorreus;
	private String entregaPostalPortal;
	private String entregaPostalEscala;
	private String entregaPostalPlanta;
	private String entregaPostalPorta;
	private String entregaPostalBloc;
	private String entregaPostalComplement;
	private String entregaPostalCodiPostal;
	private String entregaPostalPoblacio;
	private String entregaPostalMunicipiCodi;
	private String entregaPostalProvinciaCodi;
	private String entregaPostalPaisCodi;
	private String entregaPostalLinea1;
	private String entregaPostalLinea2;
	private Integer entregaPostalCie;
	private String entregaPostalFormatSobre;
	private String entregaPostalFormatFulla;
	private Boolean entregaDehObligat;
	private String entregaDehProcedimentCodi;
	private boolean entregaPostalActiva;
	private boolean entregaDehActiva;
	private String entregaNif;
}
