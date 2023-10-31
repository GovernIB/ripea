/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.AclSidEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclSidRepository extends JpaRepository<AclSidEntity, Long> {

	@Query(	"select " +
			"    sid " +
			"from " +
			"    AclSidEntity " +
			"where " +
			"    principal = false")
	public List<String> findSidByPrincipalFalse();

	@Query(	"from " +
			"    AclSidEntity " +
			"where " +
			"    sid = :name " +
			"    and principal = true")
	public AclSidEntity getUserSid(@Param("name") String name);

	@Query(	"from " +
			"    AclSidEntity " +
			"where " +
			"     sid in (:name) " +
			" and principal = false")
	public List<AclSidEntity> findRolesSid(@Param("name") List<String> name);

}