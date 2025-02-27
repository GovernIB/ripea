package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ContingutMovimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutIdOrderByCreatedDateAsc(Long contingutId);

	@Modifying
	@Query(value = "delete from ipa_cont_mov where contingut_id = :contingutId ", nativeQuery = true)
	int deleteMovimentsFromContingutsOrfes(@Param("contingutId") Long contingutId);
}