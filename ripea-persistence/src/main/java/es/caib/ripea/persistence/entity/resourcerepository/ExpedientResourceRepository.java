package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;

public interface ExpedientResourceRepository extends BaseRepository<ExpedientResourceEntity, Long> {

	// Consultes per lots per a les perspectives del llistat d'expedients. Màxim 1000 ids per crida (límit IN d'Oracle).

	//Ids dels expedients que segueix l'usuari indicat.
	@Query("select e.id from ExpedientResourceEntity e join e.seguidors s " +
			"where e.id in (:expedientIds) and s.codi = :usuariCodi")
	List<Long> findIdsAmbSeguidor(
			@Param("expedientIds") List<Long> expedientIds,
			@Param("usuariCodi") String usuariCodi);

	//[expedientId, nombre de seguidors] dels expedients amb algun seguidor.
	@Query("select e.id, count(s) from ExpedientResourceEntity e join e.seguidors s " +
			"where e.id in (:expedientIds) group by e.id")
	List<Object[]> countSeguidorsByExpedientIds(@Param("expedientIds") List<Long> expedientIds);

	// Inicialitzen d'una vegada les col·leccions lazy dels expedients que ja són a la sessió, perquè després
	// el codi de cada fila les recorri sense fer una consulta per expedient. No es poden carregar dues
	// col·leccions List en una mateixa consulta (MultipleBagFetchException): una consulta per col·lecció.
	// Sense distinct: el resultat (amb files repetides) no es fa servir, només interessa l'efecte sobre la sessió.

	@Query("select e from ExpedientResourceEntity e left join fetch e.relacionatsAmb where e.id in (:expedientIds)")
	List<ExpedientResourceEntity> fetchRelacionatsAmb(@Param("expedientIds") List<Long> expedientIds);

	@Query("select e from ExpedientResourceEntity e left join fetch e.relacionatsPer where e.id in (:expedientIds)")
	List<ExpedientResourceEntity> fetchRelacionatsPer(@Param("expedientIds") List<Long> expedientIds);

	@Query("select e from ExpedientResourceEntity e left join fetch e.interessats where e.id in (:expedientIds)")
	List<ExpedientResourceEntity> fetchInteressats(@Param("expedientIds") List<Long> expedientIds);
}
