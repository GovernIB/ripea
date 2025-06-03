package es.caib.ripea.service.resourceservice;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.model.RegistreResource;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientPeticioResourceServiceImpl extends BaseMutableResourceService<ExpedientPeticioResource, Long, ExpedientPeticioResourceEntity> implements ExpedientPeticioResourceService {

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	
    @PostConstruct
    public void init() {
        register(ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE, new RegistrePerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE, new EstatViewPerspectiveApplicator());
    }

    private class RegistrePerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            resource.setRegistreInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getRegistre()), RegistreResource.class));
            if (resource.getRegistreInfo().getJustificantArxiuUuid()!=null && Boolean.parseBoolean(configHelper.getConfig(PropertyConfig.INCORPORAR_JUSTIFICANT))) {
            	RegistreAnnexResource justificant = new RegistreAnnexResource();
            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null, 
    					resource.getRegistreInfo().getJustificantArxiuUuid(), 
    					null, 
    					true, 
    					false);
            	justificant.setTitol(documentDetalls.getNom());
            	if (documentDetalls.getContingut()!=null) {
            		justificant.setNom(documentDetalls.getContingut().getArxiuNom());
            		justificant.setTamany(documentDetalls.getContingut().getTamany());
            		justificant.setTipusMime(documentDetalls.getContingut().getTipusMime());
            	}
            	justificant.setObservacions(documentDetalls.getDescripcio());
            	//TODO: Millora: es fan conversions d'enumerats que no farien falta si la classe destí tengues com a tipus d'atribut la clase enum del origen
            	try {
            		RegistreAnnexEstatEnumDto estatArxiu = RegistreAnnexEstatEnumDto.valueOf(documentDetalls.getEstat().toString());
            		justificant.setEstat(estatArxiu);
            	} catch (Exception ex) {}            	
            	if (documentDetalls.getMetadades()!=null) {
            		justificant.setNtiFechaCaptura(documentDetalls.getMetadades().getDataCaptura());
            		if (ContingutOrigen.ADMINISTRACIO.equals(documentDetalls.getMetadades().getOrigen())) {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.ADMINISTRACIO);
            		} else {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.CIUTADA);
            		}
            		try {
            			NtiTipoDocumentoEnumDto enumTD = NtiTipoDocumentoEnumDto.valueOf(documentDetalls.getMetadades().getTipusDocumental().toString());
            			justificant.setNtiTipoDocumental(enumTD);
            		} catch (Exception ex) {}
            		justificant.setUuid(documentDetalls.getIdentificador());
            		if (documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
            			justificant.setFirmaPerfil(documentDetalls.getFirmes().get(0).getPerfil().name());
            			try {
            				RegistreAnnexFirmaTipusEnum enumTF = RegistreAnnexFirmaTipusEnum.valueOf(documentDetalls.getFirmes().get(0).getTipus().name());
            				justificant.setFirmaTipus(enumTF);
            			} catch (Exception ex) {}
            		}
            	}
            	resource.getRegistreInfo().setJustificant(justificant);
            }
        }
    }

    private class EstatViewPerspectiveApplicator implements PerspectiveApplicator<ExpedientPeticioResourceEntity, ExpedientPeticioResource> {
        @Override
        public void applySingle(String code, ExpedientPeticioResourceEntity entity, ExpedientPeticioResource resource) throws PerspectiveApplicationException {
            if (resource.getEstat() != null) {
                switch (resource.getEstat()) {
                    case PENDENT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.PENDENT);
                        break;
                    case PROCESSAT_PENDENT:
                    case PROCESSAT_NOTIFICAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.ACCEPTAT);
                        break;
                    case REBUTJAT:
                        resource.setEstatView(ExpedientPeticioEstatViewEnumDto.REBUTJAT);
                        break;
                }
            }
        }
    }
}