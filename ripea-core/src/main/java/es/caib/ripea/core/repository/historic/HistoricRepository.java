package es.caib.ripea.core.repository.historic;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.HistoricEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;

@NoRepositoryBean
public interface HistoricRepository<T extends HistoricEntity> extends JpaRepository<T, Long> {

	@Query("select t from #{#entityName} as t where t.data > :dataInici and t.data < :dataFi ")
	List<T> findByDateRange(@Param("dataInici") Date dataInici, @Param("dataFi") Date dataFi);

	@Query( "select t from #{#entityName} as t " +
			" where t.entitat = :entitat and t.data > :dataInici and t.data < :dataFi ")
	Page<T> findByEntitatAndDateRange(
			@Param("entitat") EntitatEntity entitat,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi,
			Pageable pageable);

	@Query("select t from #{#entityName} as t where t.entitat = :entitat")
	List<T> findByEntitat(@Param("entitat") EntitatEntity entitat);
	
	@Query("select t from #{#entityName} as t where t.metaExpedient = :metaExpedient")
	List<T> findByMetaExpedient(@Param("metaExpedient") MetaExpedientEntity metaExpedient);

}
