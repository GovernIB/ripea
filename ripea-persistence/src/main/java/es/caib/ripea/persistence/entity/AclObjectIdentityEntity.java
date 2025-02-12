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
import java.util.List;

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
@Table(name = BaseConfig.DB_PREFIX + "acl_object_identity")
public class AclObjectIdentityEntity extends AbstractPersistable<Long> {

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "object_id_class", nullable = false)
	private AclClassEntity classname;
	@Column(name = "object_id_identity", nullable = false)
	private Long objectId;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_sid", nullable = false)
	private AclSidEntity ownerSid;
	@OneToMany(
			mappedBy = "aclObjectIdentity", fetch = FetchType.EAGER)
	private List<AclEntryEntity> entries;
	@Builder.Default
	@Column(name = "entries_inheriting", nullable = false)
	private boolean entriesInheriting = true;

	private static final long serialVersionUID = -2299453443943600172L;

}
