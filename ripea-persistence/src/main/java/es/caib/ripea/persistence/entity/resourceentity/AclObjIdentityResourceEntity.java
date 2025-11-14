package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.AclObjIdentityResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "acl_object_identity")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class AclObjIdentityResourceEntity extends BaseEntity<AclObjIdentityResource> {

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "object_id_class", nullable = false)
    private AclClassResourceEntity classEntity;
    @Column(name = "object_id_identity", nullable = false)
    private Long objectId;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_sid", nullable = false)
    private AclSidResourceEntity ownerSid;
    @OneToMany(
            mappedBy = "aclObjectIdentity", fetch = FetchType.EAGER)
    private List<AclEntryResourceEntity> entries;
    @Column(name = "entries_inheriting", nullable = false)
    private boolean entriesInheriting = true;

}
