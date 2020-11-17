package es.caib.ripea.plugin.viafirma;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Paràmetres entrada per el sistema viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ViaFirmaParams implements Serializable {

	private boolean lecturaObligatoria;
	private String titol;
	private String descripcio;
	private String contingut;
	private String codiUsuari;
	private String contrasenya;
	private ViaFirmaDispositiu viaFirmaDispositiu;
	private String signantNif;
	private String signantNom;
	private String expedientCodi;
	private String observaciones;

	private static final long serialVersionUID = 7518162775992143312L;
}
