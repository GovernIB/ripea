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

	/** Id de l'excepció més recent amb aquesta signatura la darrera ocurrència de la qual és posterior a "des de". */
	@Query("select max(el.id) from ExcepcioLogResourceEntity el where el.signatura = :signatura and el.dataDarrera > :desde")
	Long findIdRecentBySignatura(@Param("signatura") String signatura, @Param("desde") Date desde);

	/** Incrementa el comptador d'ocurrències en una sola sentència (atòmic entre fils). */
	@Modifying
	@Query("update ExcepcioLogResourceEntity el set el.numOcurrencies = el.numOcurrencies + 1, el.dataDarrera = :ara where el.id = :id")
	int incrementarOcurrencies(@Param("id") Long id, @Param("ara") Date ara);
}