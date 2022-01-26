/**
 * 
 */
package es.caib.ripea.core.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;


@Component
public class GrupHelper {
	@Resource
	private MetaDocumentRepository metaDocumentRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private GrupRepository grupRepository;
	

	public GrupDto create(
			Long entitatId,
			GrupDto grupDto) throws NotFoundException {
		logger.debug("Creant un nou grup per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, 
				false, 
				false);
		
		GrupEntity enitity = GrupEntity.getBuilder(
				grupDto.getRol(),
				grupDto.getDescripcio(),
				entitat).build();

		GrupDto dto = conversioTipusHelper.convertir(
				grupRepository.save(enitity),
				GrupDto.class);
		return dto;
	}
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(GrupHelper.class);
	

}
