/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;

/**

 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentEnviamentInteressatRepository extends JpaRepository<DocumentEnviamentInteressatEntity, Long> {

	
	@Query(	"from" +
			"    DocumentEnviamentInteressatEntity e "
			+ "where "
			+ "	 e.notificacio.enviamentIdentificador = :enviamentIdentificador " +
			"and e.enviamentReferencia = :enviamentReferencia")
	DocumentEnviamentInteressatEntity findByIdentificadorIReferencia(
			@Param("enviamentIdentificador") String enviamentIdentificador,
			@Param("enviamentReferencia") String enviamentReferencia);
	
	

}
