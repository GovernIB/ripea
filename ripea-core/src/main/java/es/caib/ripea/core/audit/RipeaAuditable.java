/**
 * 
 */
package es.caib.ripea.core.audit;

import java.io.Serializable;

import org.springframework.data.jpa.domain.AbstractAuditable;

import es.caib.ripea.core.persistence.UsuariEntity;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@EntityListeners(RipeaAuditingEntityListener.class)
public class RipeaAuditable<PK extends Serializable> extends AbstractAuditable<UsuariEntity, PK> {

	private static final long serialVersionUID = 5373083799666869320L;

}
