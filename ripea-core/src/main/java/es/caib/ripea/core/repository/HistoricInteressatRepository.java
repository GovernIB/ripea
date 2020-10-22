package es.caib.ripea.core.repository;

import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.entity.HistoricInteressatEntity;

@Transactional
public interface HistoricInteressatRepository extends HistoricRepository<HistoricInteressatEntity> {
	
}
