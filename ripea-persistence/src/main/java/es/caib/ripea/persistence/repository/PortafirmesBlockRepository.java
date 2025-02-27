package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentEnviamentEntity;
import es.caib.ripea.persistence.entity.PortafirmesBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PortafirmesBlockRepository extends JpaRepository<PortafirmesBlockEntity, Long> {
	List<PortafirmesBlockEntity> findByEnviament(DocumentEnviamentEntity enviament);
}