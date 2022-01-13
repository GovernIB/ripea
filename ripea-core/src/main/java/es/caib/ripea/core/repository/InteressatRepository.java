/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatRepository extends JpaRepository<InteressatEntity, Long> {

	List<InteressatEntity> findByExpedientAndDocumentNum(
			ExpedientEntity expedient, String documentNum);

	InteressatEntity findByExpedientAndId(
			ExpedientEntity expedient,
			Long id);

	List<InteressatEntity> findByDocumentNum(String documentNum); 

	@Query(	  "select "
			+ "    inter "
			+ "from "
			+ "    InteressatEntity inter "
			+ "where "
			+ "    inter.expedient = :expedient "
			+ "and (type(inter) = es.caib.ripea.core.entity.InteressatPersonaFisicaEntity or type(inter) = es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity) "
			+ "order by "
			+ "    inter.id asc")
	List<InteressatEntity> findByExpedientPerNotificacions(
			@Param("expedient") ExpedientEntity expedient);

	@Query(	  "select "
			+ "    inter "
			+ "from "
			+ "    InteressatEntity inter "
			+ "where "
			+ "    inter.expedient = :expedient "
			+ "and inter.esRepresentant = false "
			+ "order by "
			+ "    inter.id asc")
	List<InteressatEntity> findByExpedientAndNotRepresentant(
			@Param("expedient") ExpedientEntity expedient);
	
	
	@Query(	  "select "
			+ "    inter "
			+ "from "
			+ "    InteressatEntity inter "
			+ "where "
			+ "    inter.expedient = :expedient "
			+ "and inter.esRepresentant = false "
			+ "and inter.notificacioAutoritzat = true "
			+ "order by "
			+ "    inter.id asc")
	List<InteressatEntity> findByExpedientAndNotRepresentantAndNomesAmbNotificacioActiva(
			@Param("expedient") ExpedientEntity expedient);
	
	@Query(	  "select "
			+ "    count(inter) "
			+ "from "
			+ "    InteressatEntity inter "
			+ "where "
			+ "    inter.expedient = :expedient "
			+ "and inter.esRepresentant = false ")
	long countByExpedient(
			@Param("expedient") ExpedientEntity expedient);

	@Query(	  "select inter "
			+ "from InteressatPersonaFisicaEntity inter "
			+ "where inter.id = :id")
	InteressatPersonaFisicaEntity findPersonaFisicaById(@Param("id") Long id);
	
	@Query(	  "select "
			+ "    distinct inter "
			+ "from "
			+ "    InteressatPersonaFisicaEntity inter "
			+ "where "
			+ "	   (:esNullNom = true or inter.nom = :nom) "
			+ "and (:esNullDocumentNum = true or inter.documentNum = :documentNum) "
			+ "and (:esNullLlinatge1 = true or inter.llinatge1 = :llinatge1) "
			+ "and (:esNullLlinatge2 = true or inter.llinatge2 = :llinatge2) "
			+ "and (inter.expedient = :expedient) "
			+ "and inter.esRepresentant = false "
			+ "order by "
			+ "    inter.llinatge1 desc, inter.llinatge2 desc, inter.nom desc")
	List<InteressatPersonaFisicaEntity> findByFiltrePersonaFisica(
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDocumentNum") boolean esNullDocumentNum,
			@Param("documentNum") String documentNum,
			@Param("esNullLlinatge1") boolean esNullLlinatge1,
			@Param("llinatge1") String llinatge1,
			@Param("esNullLlinatge2") boolean esNullLlinatge2,
			@Param("llinatge2") String llinatge2,
			@Param("expedient") ExpedientEntity expedient);

	@Query(	  "select inter "
			+ "from InteressatPersonaJuridicaEntity inter "
			+ "where inter.id = :id")
	InteressatPersonaJuridicaEntity findPersonaJuridicaById(@Param("id") Long id);
	
	@Query(	  "select "
			+ "    distinct inter "
			+ "from "
			+ "    InteressatPersonaJuridicaEntity inter "
			+ "where "
			+ "    (:esNullDocumentNum = true or inter.documentNum = :documentNum) "
			+ "and (:esNullRaoSocial = true or inter.raoSocial = :raoSocial) "
			+ "and (inter.expedient = :expedient) "
			+ "and inter.esRepresentant = false "
			+ "order by "
			+ "    inter.raoSocial desc")
	List<InteressatPersonaJuridicaEntity> findByFiltrePersonaJuridica(
			@Param("esNullDocumentNum") boolean esNullDocumentNum,
			@Param("documentNum") String documentNum,
			@Param("esNullRaoSocial") boolean esNullRaoSocial,
			@Param("raoSocial") String raoSocial,
			@Param("expedient") ExpedientEntity expedient);
	
	@Query(	  "select inter "
			+ "from InteressatAdministracioEntity inter "
			+ "where inter.id = :id")
	InteressatAdministracioEntity findAdministracioById(@Param("id") Long id);
	
	@Query(	  "select "
			+ "    distinct inter "
			+ "from "
			+ "    InteressatAdministracioEntity inter "
			+ "where "
			+ "    (:esNullOrganCodi = true or inter.organCodi = :organCodi) "
			+ "and (inter.expedient = :expedient) "
			+ "order by "
			+ "    inter.organNom desc")
	List<InteressatAdministracioEntity> findByFiltreAdministracio(
			@Param("esNullOrganCodi") boolean esNullOrganCodi,
			@Param("organCodi") String organCodi,
			@Param("expedient") ExpedientEntity expedient);
	
	@Query(	  "select "
			+ "    inter.documentNum "
			+ "from "
			+ "    MetaExpedientEntity me, ExpedientEntity e JOIN e.interessats inter"
			+ " WHERE "
			+ "        e.metaExpedient = me "
			+ "    AND me = :metaExpedient "
			+ "group by "
			+ "    inter.documentNum ")
	List<String> findAllDocumentNumbers(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient
			);
	
	@Query(	 
//			"select "
//			+ "    distinct inter.documentNum "
//			+ 
			"from "
			+ "    InteressatEntity inter "
			+ "where "
			+ "    lower(inter.documentNum) like concat('%', lower(?1), '%') "
			+ "order by "
			+ "    inter.id asc")
	List<InteressatEntity> findByText(String text);
	
	
	@Query(	"select " +
			"    i " +
			"from " +
			"    InteressatEntity i " +
			"where " +
			"i.arxiuPropagat = false " +
			"and i.arxiuReintents < :arxiuMaxReintents " +
			"order by i.arxiuIntentData asc")
	public List<InteressatEntity> findInteressatsPendentsArxiu(
			@Param("arxiuMaxReintents") int arxiuMaxReintents);
	

	
	
	@Query(	"select " +
			"    i " +
			"from " +
			"    InteressatEntity i " +
			"where " +
			"    i.expedient.entitat = :entitat " +
			"and i.arxiuPropagat = false " +
			"and i.expedient.esborrat = 0 " +
			"and (:esNullNom = true " +
			"			or (lower(i.documentNum||' '||i.nom||' '||i.llinatge1||' '||i.llinatge2) like lower('%'||:nom||'%')" +
			"				or lower(i.raoSocial) like lower('%'||:nom||'%')" +
			"				or lower(i.organNom) like lower('%'||:nom||'%'))) " + 
			"and (:esNullExpedient = true or i.expedient = :expedient) " +
			"and (:esNullMetaExpedient = true or i.expedient.metaExpedient = :metaExpedient) ")
	public Page<InteressatEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,			
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			Pageable pageable);

	@Query(	"select " +
			"    i.id " +
			"from " +
			"    InteressatEntity i " +
			"where " +
			"    i.expedient.entitat = :entitat " +
			"and i.arxiuPropagat = false " +
			"and i.expedient.esborrat = 0 " +
			"and (:esNullNom = true " +
			"			or (lower(i.documentNum||' '||i.nom||' '||i.llinatge1||' '||i.llinatge2) like lower('%'||:nom||'%')" +
			"				or lower(i.raoSocial) like lower('%'||:nom||'%')" +
			"				or lower(i.organNom) like lower('%'||:nom||'%'))) " +
			"and (:esNullExpedient = true or i.expedient = :expedient) " +
			"and (:esNullMetaExpedient = true or i.expedient.metaExpedient = :metaExpedient) ")
	public List<Long> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);
	
	
}
