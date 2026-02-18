package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.Date;

import es.caib.comanda.model.server.monitoring.SalutNivell;
import lombok.Data;

@Data
public class AvisDto implements Serializable {
	private static final long serialVersionUID = 5460274919729277906L;
	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;
	private Boolean avisAdministrador;
	private Long entitatId;
	
	public SalutNivell getSalutNivellComanda() {
		switch (this.avisNivell) {
			case ERROR: return SalutNivell.ERROR;
			case INFO: return SalutNivell.INFO;
			case WARNING: return SalutNivell.WARN;
		}
		return null;
	}
}