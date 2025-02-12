/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AclEntryEntity;
import es.caib.ripea.persistence.entity.AclObjectIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclEntryRepository extends JpaRepository<AclEntryEntity, Long> {

    List<AclEntryEntity> findByAclObjectIdentity(AclObjectIdentityEntity objectIdentity);

//	@Query(	"select " +
//			"    sid " +
//			"from " +
//			"    AclSidEntity " +
//			"where " +
//			"    principal = false")
//	public List<String> findSidByPrincipalFalse();
//
//	@Query(	"from " +
//			"    AclSidEntity " +
//			"where " +
//			"    sid = :name " +
//			"    and principal = true")
//	public AclSidEntity getUserSid(@Param("name") String name);
//
//	@Query(	"from " +
//			"    AclSidEntity " +
//			"where " +
//			"     sid in (:name) " +
//			" and principal = false")
//	public List<AclSidEntity> findRolesSid(@Param("name") List<String> name);

}
