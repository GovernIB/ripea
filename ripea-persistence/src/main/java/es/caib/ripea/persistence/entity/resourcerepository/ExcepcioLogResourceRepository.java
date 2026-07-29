package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ExcepcioLogResourceRepository extends BaseRepository<ExcepcioLogResourceEntity, Long> {

	/**
	 * Esborrat massiu en una sola sentència. No es pot fer servir un delete derivat del nom
	 * del mètode: Spring Data el resol carregant totes les entitats a memòria (amb els seus
	 * CLOB) i cridant em.remove() una per una.
	 */
	@Modifying
	@Query("delete from ExcepcioLogResourceEntity el where el.data < :data")
	int deleteByDataBefore(@Param("data") Date data);
}