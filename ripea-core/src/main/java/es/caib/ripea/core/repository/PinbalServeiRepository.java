package es.caib.ripea.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.ripea.core.entity.PinbalServeiEntity;

public interface PinbalServeiRepository extends JpaRepository<PinbalServeiEntity, Long> {
	
	
	@Query(	"select ps from " +
			"    PinbalServeiEntity ps")
	Page<PinbalServeiEntity> findPaginat(
			Pageable pageable);
	
	
	PinbalServeiEntity findByCodi(String codi);
	
}
