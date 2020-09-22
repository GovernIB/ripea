/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.AclObjectIdentityEntity;
import es.caib.ripea.core.entity.AclSidEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclObjectIdentityRepository extends JpaRepository<AclObjectIdentityEntity, Long> {

	@Query(	"select distinct " +
			"    oi.objectId " +
			"from " +
			"    AclObjectIdentityEntity oi JOIN oi.entries entry " +
			"where " +
			"      oi.classname.classname = :classname   " +
			" and  entry.sid in (:sids)   " +
			" and  entry.mask = :mask   " +
			" and  entry.granting = true   ")
	public List<Long> findObjectsWithPermissions(
			@Param("classname") String classname, 
			@Param("sids") List<AclSidEntity> sids, 
			@Param("mask") Integer mask);
}
