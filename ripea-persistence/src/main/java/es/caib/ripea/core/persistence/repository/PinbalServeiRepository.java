package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.PinbalServeiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PinbalServeiRepository extends JpaRepository<PinbalServeiEntity, Long> {
	
	@Query(	"select ps from PinbalServeiEntity ps")
	Page<PinbalServeiEntity> findPaginat(Pageable pageable);
	
	@Query(	"select ps from PinbalServeiEntity ps where ps.actiu=1 order by ps.nom asc")
	List<PinbalServeiEntity> findActiusOrderByNom();
	
	PinbalServeiEntity findByCodi(String codi);
}