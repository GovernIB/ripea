package es.caib.ripea.persistence.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CarpetaRestriccioUsuariId implements Serializable {
    private static final long serialVersionUID = 5899613018891688857L;
    
	private Long carpeta_id;
    private String usuari_codi;
    
}
