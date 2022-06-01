/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutRepository extends JpaRepository<ContingutEntity, Long> {


	List<ContingutEntity> findByNomAndTipusAndPareAndEntitatAndEsborrat(
			String nom,
			ContingutTipusEnumDto tipus,
			ContingutEntity pare,
			EntitatEntity entitat,
			int esborrat);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"c.pare = :pare " +
			"and c.esborrat = :esborrat " +
			"and c.ordre != 0")
	List<ContingutEntity> findByPareAndEsborratAndOrdenat(
			@Param("pare") ContingutEntity pare,
			@Param("esborrat") int esborrat,
			Sort sort);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"c.pare = :pare " +
			"and c.esborrat = :esborrat " +
			"and c.ordre = 0")
	List<ContingutEntity> findByPareAndEsborratSenseOrdre(
			@Param("pare") ContingutEntity pare,
			@Param("esborrat") int esborrat,
			Sort sort);

	List<ContingutEntity> findByPareAndNomOrderByEsborratAsc(
			ContingutEntity pare,
			String nom);

	ContingutEntity findByPareAndNomAndEsborrat(
			ContingutEntity pare,
			String nom,
			int esborrat);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and (:tipusCarpeta = true or type(c) <> es.caib.ripea.core.entity.CarpetaEntity) " +
			"and (:tipusDocument = true or type(c) <> es.caib.ripea.core.entity.DocumentEntity) " +
			"and (:tipusExpedient = true or type(c) <> es.caib.ripea.core.entity.ExpedientEntity) " +
			"and (:esNullMetaNode = true or ((type(c) = es.caib.ripea.core.entity.ExpedientEntity or type(c) = es.caib.ripea.core.entity.DocumentEntity) and c.metaNode = :metaNode)) " +
			"and (:esNullNom = true or lower(c.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullCreador = true or lower(c.createdBy) like lower('%'||:creador||'%')) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (:esNullDataEsborratInici = true or c.esborratData >= :dataEsborratInici) " +
			"and (:esNullDataEsborratFi = true or c.esborratData <= :dataEsborratFi) " +
			"and ((:mostrarEsborrats = true and c.esborrat > 0) or (:mostrarNoEsborrats = true and c.esborrat = 0)) " +
			"and (:esNullExpedient = true or c.expedient = :expedient) ")
	public Page<ContingutEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("tipusCarpeta") boolean tipusCarpeta,
			@Param("tipusDocument") boolean tipusDocument,
			@Param("tipusExpedient") boolean tipusExpedient,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCreador") boolean esNullCreador,
			@Param("creador") String creador,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullDataEsborratInici") boolean esNullDataEsborratInici,
			@Param("dataEsborratInici") Date dataEsborratInici,
			@Param("esNullDataEsborratFi") boolean esNullDataEsborratFi,
			@Param("dataEsborratFi") Date dataEsborratFi,
			@Param("mostrarEsborrats") boolean mostrarEsborrats,
			@Param("mostrarNoEsborrats") boolean mostrarNoEsborrats,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			Pageable pageable);

	@Query(	"select " +
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and (:esNullNom = true or lower(c.nom) like :nom) " +
			"and (:esNullUsuari = true or c.lastModifiedBy = :usuari) " +
			"and (:esNullDataInici = true or c.lastModifiedDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.lastModifiedDate <= :dataFi) " +
			"and esborrat > 0")
	public Page<ContingutEntity> findEsborratsByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullUsuari") boolean esNullUsuari,
			@Param("usuari") UsuariEntity usuari,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);


	@Query(	"select " +
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"c.arxiuUuid = null " +
			"and ((c.tipus = 0 and c.arxiuReintents < :arxiuMaxReintentsExpedients) or (c.tipus = 2 and c.arxiuReintents < :arxiuMaxReintentsDocuments)) " +
			"and c.esborrat = 0 " +
			"order by c.arxiuIntentData asc")
	public List<ContingutEntity> findContingutsPendentsArxiu(
			@Param("arxiuMaxReintentsExpedients") int arxiuMaxReintentsExpedients,
			@Param("arxiuMaxReintentsDocuments") int arxiuMaxReintentsDocuments);




	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Query(value = "select c.id from ContingutEntity c " +
			" where c.id not in (select id from NodeEntity)" +
			"   and c.id not in (select id from CarpetaEntity) ")
	List<Long> getIdContingutsOrfes();

	@Query(value = "select id from ipa_contingut where pare_id = :pareId", nativeQuery = true)
	List<Long> findIdFills(@Param("pareId") Long pareId);

	@Modifying
	@Query(value = "delete from ipa_contingut where id = :contingutId", nativeQuery = true)
	int deleteContingutsOrfes(@Param("contingutId") Long contingutId);

	@Query(	"select count(c.id) " +
			"from ContingutEntity c " +
			"where c.arxiuUuid = null " +
			"and ((c.tipus = 0 and c.arxiuReintents < :arxiuMaxReintentsExpedients) or (c.tipus = 2 and c.arxiuReintents < :arxiuMaxReintentsDocuments)) " +
			"and c.esborrat = 0 " +
			"and c.id not in (select id from NodeEntity) " +
			"and c.id not in (select id from CarpetaEntity)")
	public int countContingutsPendentsArxiuOrfes(
			@Param("arxiuMaxReintentsExpedients") int arxiuMaxReintentsExpedients,
			@Param("arxiuMaxReintentsDocuments") int arxiuMaxReintentsDocuments);

	@Modifying
	@Query(value = "update ipa_contingut set pare_id = null where id = :contingutId", nativeQuery = true)
	void removePare(Long contingutId);
}
