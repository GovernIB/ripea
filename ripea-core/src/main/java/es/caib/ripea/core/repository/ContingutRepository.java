/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutRepository extends JpaRepository<ContingutEntity, Long> {
	
	
	
	@Query(	"select" +
			"    e " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and e.metaNode = :metaNode)")
	List<ContingutEntity> findByEntitatAndMetaExpedient(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNode") MetaNodeEntity metaNode);

	List<ContingutEntity> findByPareAndEsborrat(
			ContingutEntity pare,
			int esborrat,
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
			"and (:tipusRegistre = true or type(c) <> es.caib.ripea.core.entity.RegistreEntity) " +
			"and (:esNullMetaNode = true or ((type(c) = es.caib.ripea.core.entity.ExpedientEntity or type(c) = es.caib.ripea.core.entity.DocumentEntity) and c.metaNode = :metaNode)) " +
			"and (:esNullNom = true or lower(c.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or c.lastModifiedDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.lastModifiedDate <= :dataFi) " +
			"and ((:mostrarEsborrats = true and c.esborrat > 0) or (:mostrarNoEsborrats = true and c.esborrat = 0)) ")
	public Page<ContingutEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("tipusCarpeta") boolean tipusCarpeta,
			@Param("tipusDocument") boolean tipusDocument,
			@Param("tipusExpedient") boolean tipusExpedient,
			@Param("tipusRegistre") boolean tipusRegistre,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("mostrarEsborrats") boolean mostrarEsborrats,
			@Param("mostrarNoEsborrats") boolean mostrarNoEsborrats,
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
			"    (:esPareNull = true or c.pare = :pare) " +
			"and (:esPareNull = false or c.pare in (:pares))" +
			"and (:esNullContingutDescripcio = true or lower(c.nom) like lower('%'||:contingutDescripcio||'%')) " +
			"and (:esNullRemitent = true or lower(c.darrerMoviment.remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (:esNullFiltre = true or lower(c.nom) like lower('%'||:filtre||'%') or lower(c.darrerMoviment.remitent.nom) like lower('%'||:filtre||'%') or lower(c.darrerMoviment.comentari) like lower('%'||:filtre||'%')) " +
			"and ((:esNullEstat = true and (c.esborrat = 0 or c.esborrat = 1)) or (c.esborrat = :estat)) " +
			"and (type(c) != es.caib.ripea.core.entity.RegistreEntity or (c.procesEstat = es.caib.ripea.core.api.registre.RegistreProcesEstatEnum.PROCESSAT or c.procesEstat = es.caib.ripea.core.api.registre.RegistreProcesEstatEnum.NO_PROCES))")
	public Page<ContingutEntity> findBustiaPendentByPareAndFiltre(
			@Param("esPareNull") boolean esPareNull,
			@Param("pare") ContingutEntity pare,
			@Param("pares") List<? extends ContingutEntity> pares,
			@Param("esNullContingutDescripcio") boolean esNullContingutDescripcio,
			@Param("contingutDescripcio") String contingutDescripcio,
			@Param("esNullRemitent") boolean esNullRemitent,
			@Param("remitent") String remitent,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") int estat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query(	"select " +
			"    c.pare.id, " +
			"    count(*) " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and c.pare in (:pares) " +
			"and c.esborrat = 0 " +
			" and (type(c) != es.caib.ripea.core.entity.RegistreEntity or (c.procesEstat = es.caib.ripea.core.api.registre.RegistreProcesEstatEnum.PROCESSAT or c.procesEstat = es.caib.ripea.core.api.registre.RegistreProcesEstatEnum.NO_PROCES)) " +
			"group by " +
			"    c.pare")
	List<Object[]> countByPares(
			@Param("entitat") EntitatEntity entitat,
			@Param("pares") List<? extends ContingutEntity> pares);
	
	@Query("select " +
			"   c " +
			"from " +
			"   ContingutEntity c " +
			"where " +
			"   type(c) = es.caib.ripea.core.entity.RegistreEntity " +
			"AND " +
			"   c.pare.id = :pareId")
	List<ContingutEntity> findRegistresByPareId(
			@Param("pareId") Long pareId);
	
}
