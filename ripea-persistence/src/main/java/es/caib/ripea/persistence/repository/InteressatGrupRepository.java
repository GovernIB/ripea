package es.caib.ripea.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.InteressatGrupEntity;

/**
 * Repositori per gestionar un grup d'interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public interface InteressatGrupRepository  extends JpaRepository<InteressatGrupEntity, Long> {

	List<InteressatGrupEntity> findByIdIn(List<Long> ids);

	Optional<InteressatGrupEntity> findByExpedientIdAndNom(Long id, String nom);

}
