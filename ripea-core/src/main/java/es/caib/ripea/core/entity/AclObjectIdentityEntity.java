/**
 * 
 */
package es.caib.ripea.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "ipa_acl_object_identity")
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
