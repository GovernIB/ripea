package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AclSidEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
	
	@Modifying
 	@Query(value = "UPDATE IPA_ACL_SID SET SID = :codiNou WHERE SID = :codiAntic AND PRINCIPAL = 1", nativeQuery = true)
	public int updateUsuariPermis(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}