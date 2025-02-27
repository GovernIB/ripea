package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientCarpetaArbreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExpedientCarpetaArbreRepository extends JpaRepository<ExpedientCarpetaArbreEntity, Long> {

	@Query(	"select c " +
			"from " +
			"    ExpedientCarpetaArbreEntity c " +
			"where " +
			"	c.entitat = :entitat " +
			"	and (" + 
			"			(c.pare is null and c.expedient.id = :pare) " + 
			"				or (c.pare = :pare))")
	List<ExpedientCarpetaArbreEntity> findByPare(
			@Param("entitat") Long entitat, 
			@Param("pare") Long pare);
}