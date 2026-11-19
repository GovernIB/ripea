package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Id;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.model.IniciResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitat de suport del recurs de la pantalla d'inici. No és una entitat JPA: el recurs no té taula
 * i només exposa informes (veure {@link IniciResource}).
 */
@Getter
@Setter
@NoArgsConstructor
public class IniciResourceEntity implements ResourceEntity<IniciResource, Long> {

	@Id
	private Long id;

	@Override
	public boolean isNew() {
		return getId() == null;
	}

}
