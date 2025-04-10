/**
 * 
 */
package es.caib.ripea.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;

public interface DocumentEnviamentInteressatRepository extends JpaRepository<DocumentEnviamentInteressatEntity, Long> {
	
	@Query(	"from" +
			"    DocumentEnviamentInteressatEntity e "
			+ "where "
			+ "	 e.notificacio.notificacioIdentificador = :notificacioIdentificador " +
			"and e.enviamentReferencia = :enviamentReferencia")
	DocumentEnviamentInteressatEntity findByIdentificadorIReferencia(
			@Param("notificacioIdentificador") String notificacioIdentificador,
			@Param("enviamentReferencia") String enviamentReferencia);
	
	@Modifying
 	@Query(value = "UPDATE IPA_DOCUMENT_ENVIAMENT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoriaDocEnv(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
	
	@Modifying
 	@Query(value = "UPDATE IPA_DOCUMENT_ENVIAMENT_INTER " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}