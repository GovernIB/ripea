package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ExpedientOrganPareEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;

@Component
public interface ExpedientOrganPareRepository extends JpaRepository<ExpedientOrganPareEntity, Long> {

	@Query(	"select " +
			"    eop.metaExpedientOrganGestor.organGestor " + 
			"from " +
			"    ExpedientOrganPareEntity eop " +
			"where " +
			"    eop.expedient.id = :expedientId")
	List<OrganGestorEntity> findOrganGestorByExpedientId(
			@Param("expedientId") Long expedientId);

	@Query(	"select " +
			"    eop.metaExpedientOrganGestor " + 
			"from " +
			"    ExpedientOrganPareEntity eop " +
			"where " +
			"    eop.expedient.id = :expedientId")
	List<MetaExpedientOrganGestorEntity> findMetaExpedientOrganGestorByExpedientId(
			@Param("expedientId") Long expedientId);
	
	@Modifying
 	@Query(value = "UPDATE IPA_EXPEDIENT_ORGANPARE " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);	
}