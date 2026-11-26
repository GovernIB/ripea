package es.caib.ripea.service.intf.dto.historic;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Una petició HTTP a la consulta d'estadístiques (històrics): la pantalla JSP {@code /historic} o l'API interna
 * {@code /api/historic}. Es desa com una línia del registre d'accessos, per saber si algú en fa ús.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class AccesHistoricDto implements Serializable {

	/** Aplicació que ha rebut la petició: {@link #APLICACIO_JSP} o {@link #APLICACIO_API_INTERNA}. */
	public static final String APLICACIO_JSP = "JSP";
	public static final String APLICACIO_API_INTERNA = "API_INTERNA";

	private Date data;
	private String aplicacio;
	private String metodeHttp;
	private String ruta;
	private String parametres;
	private String ipRemota;
	private String xForwardedFor;
	private String usuari;
	private String rolActual;
	private String userAgent;
	private Integer estatHttp;
	private Long duradaMs;
	private String error;

	private static final long serialVersionUID = 1L;

}
