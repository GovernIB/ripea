package es.caib.ripea.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.InteressatGrupEntity;

@Component
public interface InteressatGrupRepository  extends JpaRepository<InteressatGrupEntity, Long> {

	List<InteressatGrupEntity> findByIdIn(List<Long> ids);

	Optional<InteressatGrupEntity> findByExpedientIdAndNom(Long id, String nom);

	@Modifying
	@Query(value = "DELETE FROM IPA_INTERESSAT_GRUP_REL WHERE INTERESSAT_ID = :interessatId", nativeQuery = true)
	void deleteRelacionsInteressatGrup(@Param("interessatId") Long interessatId);
}