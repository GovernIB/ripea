package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentViaFirmaEntity;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DocumentViaFirmaRepository extends JpaRepository<DocumentViaFirmaEntity, Long> {

	List<DocumentViaFirmaEntity> findByDocument(DocumentEntity document);

	List<DocumentViaFirmaEntity> findByDocumentAndEstatInOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat);

	List<DocumentViaFirmaEntity> findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
			DocumentEntity document,
			DocumentEnviamentEstatEnumDto[] estat,
			boolean error);

	DocumentViaFirmaEntity findByMessageCode(
			String messageCode);
}