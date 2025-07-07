package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ViaFirmaUsuariEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface ViaFirmaUsuariRepository extends JpaRepository<ViaFirmaUsuariEntity, String> {
	 @Modifying
     @Query(value = "UPDATE IPA_USUARI_VIAFIRMA_RIPEA SET RIPEA_USER_CODI = :codiNou WHERE RIPEA_USER_CODI = :codiAntic", nativeQuery = true)
	 public int updateUsuariViaFirma(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}