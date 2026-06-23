package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.RegistreAnnexResourceEntity;

public interface RegistreAnnexResourceRepository extends BaseRepository<RegistreAnnexResourceEntity, Long> {
	List<RegistreAnnexResourceEntity> findByDocumentId(Long id);

	//Compta els annexos d'una anotació que han quedat amb error (sense document creat o amb error registrat).
	@Query("select count(a) from RegistreAnnexResourceEntity a " +
			"where a.registre.id = :registreId and (a.document is null or a.error is not null)")
	long countAnnexosAmbErrorByRegistreId(@Param("registreId") Long registreId);
}