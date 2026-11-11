package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.service.intf.dto.IntegracioCodiEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IntegracioResourceRepository extends BaseRepository<IntegracioResourceEntity, Long> {

	List<IntegracioResourceEntity> findByCodiOrderByDataDesc(IntegracioCodiEnum codi);

	/** Les primeres accions d'una integració segons el Pageable: el límit s'aplica a la consulta (top-N). */
	List<IntegracioResourceEntity> findByCodiOrderByDataDesc(IntegracioCodiEnum codi, Pageable pageable);

	/**
	 * Esborrat massiu en una sola sentència. No es pot fer servir un delete derivat del nom
	 * del mètode: Spring Data el resol carregant totes les entitats a memòria (amb els seus
	 * CLOB) i cridant em.remove() una per una.
	 */
	@Modifying
	@Query("delete from IntegracioResourceEntity ia where ia.data < :data")
	int deleteByDataBefore(@Param("data") Date data);
}