/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Getter;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
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

	private static final long serialVersionUID = -2299453443943600172L;

}
