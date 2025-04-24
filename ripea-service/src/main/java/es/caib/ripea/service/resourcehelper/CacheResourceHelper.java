package es.caib.ripea.service.resourcehelper;

import java.util.ArrayList;
import java.util.List;

import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.model.ValidacioErrorResource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.NodeEntity;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DadaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentNotificacioResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentPortafirmesResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.NodeResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DadaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentNotificacioResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentPortafirmesResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDadaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.service.base.helper.ObjectMappingHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.ErrorsValidacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class CacheResourceHelper {

	private final DadaResourceRepository dadaResourceRepository;
	private final DocumentResourceRepository documentResourceRepository;
	private final MetaDadaResourceRepository metaDadaResourceRepository;
	private final MetaDocumentResourceRepository metaDocumentResourceRepository;
	private final DocumentPortafirmesResourceRepository documentPortafirmesResourceRepository;
	private final DocumentNotificacioResourceRepository documentNotificacioResourceRepository;
	private final ObjectMappingHelper objectMappingHelper;
	private final ConfigHelper configHelper;
	
	@CacheEvict(value = "errorsValidacioNodeResource", key = "#node.id")
	public void evictErrorsValidacioPerNode(NodeEntity node) {}
	
	@Cacheable(value = "errorsValidacioNodeResource", key = "#node.id")
	public List<ValidacioErrorResource> findErrorsValidacioPerNode(NodeResourceEntity node) {
		
		List<ValidacioErrorResource> errors = new ArrayList<ValidacioErrorResource>();
		List<DadaResourceEntity> dades = dadaResourceRepository.findByNodeId(node.getId());
		// Valida dades específiques del meta-node
		List<MetaDadaResourceEntity> metaDades = metaDadaResourceRepository.findByMetaNodeAndActivaTrueAndMultiplicitatIn(
				node.getMetaNode(),
				new MultiplicitatEnumDto [] { MultiplicitatEnumDto.M_1, MultiplicitatEnumDto.M_1_N});
		
		for (MetaDadaResourceEntity metaDada: metaDades) {
			boolean trobada = false;
			for (DadaResourceEntity dada: dades) {
				if (dada.getMetaDada() != null && dada.getMetaDada().equals(metaDada)) {
					trobada = true;
					break;
				}
			}
			if (!trobada)
				errors.add(crearValidacioError(metaDada, metaDada.getMultiplicitat()));

		}
		if (node instanceof ExpedientResourceEntity) {
			
			ExpedientResourceEntity expedient = (ExpedientResourceEntity)node;
			List<DocumentResourceEntity> documents = documentResourceRepository.findByExpedientAndEsborrat(expedient, 0);
			
			// Valida documents específics del meta-node
			List<MetaDocumentResourceEntity> metaDocumentsDelMetaExpedient = metaDocumentResourceRepository.findByMetaExpedientAndMultiplicitatIn(
					expedient.getMetaExpedient(),
					new MultiplicitatEnumDto [] {
						MultiplicitatEnumDto.M_1,
						MultiplicitatEnumDto.M_1_N
					});
			
			for (MetaDocumentResourceEntity metaDocument: metaDocumentsDelMetaExpedient) {
				boolean trobat = false;
				for (DocumentResourceEntity document: documents) {
					if (document.getMetaDocument() != null && document.getMetaDocument().equals(metaDocument)) {
						trobat = true;
						break;
					}
				}
				if (!trobat)
					errors.add(crearValidacioError(metaDocument, metaDocument.getMultiplicitat(), ErrorsValidacioTipusEnumDto.MULTIPLICITAT));
			}
			
			for (DocumentResourceEntity document : documents) {
				if (document.getMetaNode() == null) {
					errors.add(crearValidacioError(null,null,ErrorsValidacioTipusEnumDto.METADOCUMENT));
					break;
				}
			}
			
			for (DocumentResourceEntity document : documents) {
				if (hasNotificacionsSenseErrorNoCaducadesPendents(document)) {
					errors.add(crearValidacioError(null,null,ErrorsValidacioTipusEnumDto.NOTIFICACIONS));
					break;
				}
			}
			
			boolean isObligarInteressatActiu = configHelper.getAsBoolean(PropertyConfig.PERMETRE_OBLIGAR_INTERESSAT);
			MetaExpedientResourceEntity procediment = expedient.getMetaExpedient();
			if (isObligarInteressatActiu && procediment.isInteressatObligatori()
				&& (expedient.getInteressats() == null|| expedient.getInteressats().isEmpty())) {
				errors.add(crearValidacioError(null, null, ErrorsValidacioTipusEnumDto.INTERESSATS));
			}
		}
		
		return errors;
	}
	
	@Cacheable(value = "enviamentsPortafirmesPendentsPerExpedientResource", key="#expedient")
	public boolean hasEnviamentsPortafirmesPendentsPerExpedient(ExpedientResourceEntity expedient) {
		boolean hasEnviamentsPortafirmesPendents = false; //enviaments Portafirmes amb error
		for (ContingutResourceEntity contingut : expedient.getFills()) {
			if (contingut instanceof DocumentResourceEntity) {
				List<DocumentPortafirmesResourceEntity> enviamentsPortafirmesPendents = documentPortafirmesResourceRepository.findByDocumentAndEstatInAndErrorOrderByCreatedDateAsc(
						(DocumentResourceEntity) contingut,
						new DocumentEnviamentEstatEnumDto[] {
								DocumentEnviamentEstatEnumDto.PENDENT,
								DocumentEnviamentEstatEnumDto.ENVIAT
						},
						false);
				//Si hi ha només un enviament pendent sortim del bucle
				if (enviamentsPortafirmesPendents != null && enviamentsPortafirmesPendents.size() > 0) {
					hasEnviamentsPortafirmesPendents = true;
					break;
				}
			}
		}
		return hasEnviamentsPortafirmesPendents;
	}

	@CacheEvict(value = "enviamentsPortafirmesPendentsPerExpedientResource", key="#expedient")
	public void evictEnviamentsPortafirmesPendentsPerExpedient(ExpedientResourceEntity expedient) {
	}
	
	private ValidacioErrorResource crearValidacioError(
			MetaDocumentResourceEntity metaDocument,
			MultiplicitatEnumDto multiplicitat,
			ErrorsValidacioTipusEnumDto tipus) {
		return new ValidacioErrorResource(
				multiplicitat != null ? objectMappingHelper.newInstanceMap(metaDocument, MetaDocumentResource.class) : null,
				multiplicitat != null ? MultiplicitatEnumDto.valueOf(multiplicitat.toString()) : null,
				tipus);
	}
	
	private ValidacioErrorResource crearValidacioError(
			MetaDadaResourceEntity metaDada,
			MultiplicitatEnumDto multiplicitat) {
		return new ValidacioErrorResource(
				objectMappingHelper.newInstanceMap(metaDada, MetaDadaResource.class),
				MultiplicitatEnumDto.valueOf(multiplicitat.toString()));
	}
	
	private boolean hasNotificacionsSenseErrorNoCaducadesPendents(DocumentResourceEntity document) {
		List<DocumentNotificacioResourceEntity> notificacionsPendents = documentNotificacioResourceRepository.findByDocumentOrderByCreatedDateDesc(document);
		//No permetrem tancar l'expedient si té alguna notificacio:
		// - Que esta pendent
		// - Que no té error
		// - Que no esta caducada
		if (Utils.isNotEmpty(notificacionsPendents) &&
			!notificacionsPendents.get(0).isNotificacioFinalitzada() &&
			!notificacionsPendents.get(0).isError() &&
			!notificacionsPendents.get(0).isCaducada()) {
				return true;
		}
		return false;
	}
}