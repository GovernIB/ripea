package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentEnviamentInteressatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface DocumentEnviamentInteressatRepository extends JpaRepository<DocumentEnviamentInteressatEntity, Long> {

	
	@Query(	"from" +
			"    DocumentEnviamentInteressatEntity e "
			+ "where "
			+ "	 e.notificacio.notificacioIdentificador = :notificacioIdentificador " +
			"and e.enviamentReferencia = :enviamentReferencia")
	DocumentEnviamentInteressatEntity findByIdentificadorIReferencia(
			@Param("notificacioIdentificador") String notificacioIdentificador,
			@Param("enviamentReferencia") String enviamentReferencia);
}