package es.caib.ripea.persistence.repository;

import java.util.List;

import es.caib.ripea.persistence.entity.AclEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface AclEntryRepository extends JpaRepository<AclEntryEntity, Long> {

	/**
	 * Entrades ACL (una per màscara) atorgades a qualsevol dels SID indicats, amb la classe
	 * i l'identificador de l'objecte. No comprova que l'objecte encara existeixi.
	 */
	@Query(	"select " +
			"    s.id as sidId, " +
			"    s.sid as sid, " +
			"    s.principal as principal, " +
			"    c.classname as classname, " +
			"    o.objectId as objectId, " +
			"    e.mask as mask " +
			"from " +
			"    AclEntryEntity e " +
			"    join e.sid s " +
			"    join e.aclObjectIdentity o " +
			"    join o.classname c " +
			"where " +
			"    s.id in (:sidIds) " +
			"and e.granting = true")
	List<AclEntrySidProjection> findBySidIdIn(@Param("sidIds") List<Long> sidIds);

	interface AclEntrySidProjection {
		Long getSidId();
		String getSid();
		/** true = usuari, false = rol */
		Boolean getPrincipal();
		String getClassname();
		Long getObjectId();
		Integer getMask();
	}
}
