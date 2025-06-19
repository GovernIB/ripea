package es.caib.ripea.service.intf.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

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
}