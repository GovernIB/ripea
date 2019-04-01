/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatRepository extends JpaRepository<InteressatEntity, Long> {

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
	
}
