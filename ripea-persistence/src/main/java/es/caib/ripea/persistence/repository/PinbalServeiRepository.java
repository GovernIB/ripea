package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.PinbalServeiEntity;

@Component
public interface PinbalServeiRepository extends JpaRepository<PinbalServeiEntity, Long> {
	
	@Query(	"select ps from PinbalServeiEntity ps")
	Page<PinbalServeiEntity> findPaginat(Pageable pageable);
	
	@Query(	"select ps from PinbalServeiEntity ps where ps.actiu=1 order by ps.nom asc")
	List<PinbalServeiEntity> findActiusOrderByNom();
	
	PinbalServeiEntity findByCodi(String codi);
	
	@Modifying
 	@Query(value = "UPDATE IPA_PINBAL_SERVEI " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}