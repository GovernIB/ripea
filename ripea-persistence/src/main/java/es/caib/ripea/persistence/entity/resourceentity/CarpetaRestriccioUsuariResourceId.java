package es.caib.ripea.persistence.entity.resourceentity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter 
@AllArgsConstructor
@NoArgsConstructor
public class CarpetaRestriccioUsuariResourceId implements Serializable {

	private static final long serialVersionUID = -8435274115250998128L;
	
	private Long carpeta_id;
    private String usuari_codi;
    
}
