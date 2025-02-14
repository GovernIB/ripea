/**
 * 
 */
package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


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
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	

	public GrupDto create(
			Long entitatId,
			GrupDto grupDto) throws NotFoundException {
		logger.debug("Creant un nou grup per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		
		GrupEntity enitity = GrupEntity.getBuilder(
				grupDto.getCodi(),
				grupDto.getDescripcio(),
				entitat, 
				grupDto.getOrganGestorId() != null ?  organGestorRepository.findById(grupDto.getOrganGestorId()).orElse(null) : null).build();

		GrupDto dto = conversioTipusHelper.convertir(
				grupRepository.save(enitity),
				GrupDto.class);
		return dto;
	}
	
	
	@Transactional
	public void crearPermisosDeGrup(
			Long grupId, 
			PermisDto permis)  {
		GrupEntity grup = grupRepository.getOne(grupId);
		PermisDto dto = new PermisDto();
		dto.setRead(true);
		dto.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
		dto.setPrincipalNom(grup.getCodi());
		permisosHelper.updatePermis(grup.getId(), GrupEntity.class, dto);
		
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(GrupHelper.class);
	

}