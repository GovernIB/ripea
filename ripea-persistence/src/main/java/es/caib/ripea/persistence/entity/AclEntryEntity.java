/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = BaseConfig.DB_PREFIX + "acl_entry")
public class AclEntryEntity extends AbstractPersistable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "acl_object_identity", nullable = false)
	private AclObjectIdentityEntity aclObjectIdentity;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "sid", nullable = false)
	private AclSidEntity sid;
	@Column(name = "ace_order", nullable = false)
	private Integer order;
	@Column(name = "mask", nullable = false)
	private Integer mask;
	@Column(name = "granting", nullable = false)
	private Boolean granting;

	@Builder.Default
	@Column(name = "audit_success", nullable = false)
	private boolean auditSuccess = false;
	@Builder.Default
	@Column(name = "audit_failure", nullable = false)
	private boolean auditFailure = false;
	
	private static final long serialVersionUID = -2299453443943600172L;

}
