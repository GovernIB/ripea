/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.persistence.AclClassEntity;
import es.caib.ripea.core.persistence.AclObjectIdentityEntity;
import es.caib.ripea.core.persistence.AclSidEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclObjectIdentityRepository extends JpaRepository<AclObjectIdentityEntity, Long> {

	@Query(	"select " +
			"    distinct oi.objectId " +
			"from " +
			"    AclObjectIdentityEntity oi join oi.entries entry " +
			"where " +
			"    oi.classname.classname = :classname " +
			"and entry.sid in (:sids) " +
			"and entry.mask = :mask " +
			"and entry.granting = true")
	public List<Serializable> findObjectsWithPermissions(
			@Param("classname") String classname, 
			@Param("sids") List<AclSidEntity> sids, 
			@Param("mask") Integer mask);
	
	
	@Query(	"select " +
			"    distinct oi.objectId " +
			"from " +
			"    AclObjectIdentityEntity oi " +
			"where " +
			"    oi.classname.classname = :classname " +
			"and (select count(entry) from AclEntryEntity entry where entry.aclObjectIdentity = oi and entry.sid in (:sids) and entry.mask = :mask1 and entry.granting = true) = 1 " +
			"and (select count(entry) from AclEntryEntity entry where entry.aclObjectIdentity = oi and entry.sid in (:sids) and entry.mask = :mask2 and entry.granting = true) = 1 ")
	public List<Serializable> findObjectsWithPermissions(
			@Param("classname") String classname, 
			@Param("sids") List<AclSidEntity> sids, 
			@Param("mask1") Integer mask1,
			@Param("mask2") Integer mask2);
	
	@Query(	"select " +
			"    distinct oi " +
			"from " +
			"    AclObjectIdentityEntity oi " +
			"where " +
			"    oi.objectId in (:objectsIdentityIdx) " +
			"and (select count(entry) from AclEntryEntity entry where entry.aclObjectIdentity = oi and entry.mask in (:masksIn1) and entry.granting = true) > 0")
	public List<AclObjectIdentityEntity> findByAclObjectIdentityInAndMaskIn(
			@Param("objectsIdentityIdx") List<Long> objectsIdentityIdx,
			@Param("masksIn1") List<Integer> masksIn1);
	
	@Query(	"select " +
			"    distinct oi " +
			"from " +
			"    AclObjectIdentityEntity oi " +
			"where " +
			"    oi.objectId in (:objectsIdentityIdx) " +
			"and (select count(entry) from AclEntryEntity entry where entry.aclObjectIdentity = oi and entry.mask in (:masksIn1) and entry.granting = true) > 0 " +
			"and (:masksIn2 is null or (select count(entry) from AclEntryEntity entry where entry.aclObjectIdentity = oi and entry.mask in (:masksIn2) and entry.granting = true) > 0) ")
	public List<AclObjectIdentityEntity> findByAclObjectIdentityInAndMaskIn(
			@Param("objectsIdentityIdx") List<Long> objectsIdentityIdx,
			@Param("masksIn1") List<Integer> masksIn1, 
    		@Param("masksIn2") List<Integer> masksIn2);

    AclObjectIdentityEntity findByClassnameAndObjectId(AclClassEntity classname, Long id);
}
