package es.caib.ripea.service.resourceservice;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientPeticioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.service.intf.dto.NtiTipoDocumentoEnumDto;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.model.RegistreResource;
import es.caib.ripea.service.intf.registre.RegistreAnnexFirmaTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiEstadoElaboracionEnum;
import es.caib.ripea.service.intf.registre.RegistreAnnexNtiOrigenEnum;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientPeticioResourceServiceImpl extends BaseMutableResourceService<ExpedientPeticioResource, Long, ExpedientPeticioResourceEntity> implements ExpedientPeticioResourceService {

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	
    @PostConstruct
    public void init() {
        register(ExpedientPeticioResource.PERSPECTIVE_REGISTRE_CODE, new RegistrePerspectiveApplicator());
        register(ExpedientPeticioResource.PERSPECTIVE_ESTAT_VIEW_CODE, new EstatViewPerspectiveApplicator());
        register(ExpedientPeticioResource.REPORT_DOWNLOAD_JUSTIFICANT, new DescarregarJustificantReportGenerator());
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
            		justificant.setTamany(documentDetalls.getContingut().getTamany());
            		justificant.setTipusMime(documentDetalls.getContingut().getTipusMime());
            	}
            	justificant.setObservacions(documentDetalls.getDescripcio());
            	//TODO: Millora: es fan conversions d'enumerats que no farien falta si la classe destí tengues com a tipus d'atribut la clase enum del origen
            	try {
            		ArxiuEstatEnumDto estatArxiu = ArxiuEstatEnumDto.valueOf(documentDetalls.getEstat().toString());
            		justificant.setAnnexArxiuEstat(estatArxiu);
            	} catch (Exception ex) {}            	
            	if (documentDetalls.getMetadades()!=null) {
            		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";
            		justificant.setNom(documentDetalls.getNom()+extensio);
            		justificant.setNtiFechaCaptura(documentDetalls.getMetadades().getDataCaptura());
            		if (ContingutOrigen.ADMINISTRACIO.equals(documentDetalls.getMetadades().getOrigen())) {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.ADMINISTRACIO);
            		} else {
            			justificant.setNtiOrigen(RegistreAnnexNtiOrigenEnum.CIUTADA);
            		}
            		try {
            			NtiTipoDocumentoEnumDto enumTD = NtiTipoDocumentoEnumDto.valueOf(documentDetalls.getMetadades().getTipusDocumental().name());
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
            		try {
            			RegistreAnnexNtiEstadoElaboracionEnum enumEE = RegistreAnnexNtiEstadoElaboracionEnum.valueOf(documentDetalls.getMetadades().getEstatElaboracio().name());
            			justificant.setNtiEstadoElaboracion(enumEE);
            		} catch (Exception ex) {}
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
    
    private class DescarregarJustificantReportGenerator implements ReportGenerator<ExpedientPeticioResourceEntity, Serializable, Serializable> {

    	@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
    		
    		DownloadableFile resultat = null;
    		
    		try {		
	    		
            	Document documentDetalls = pluginHelper.arxiuDocumentConsultar(
    					null, 
    					data.get(0).toString(), 
    					null, 
    					true, 
    					false);
            	
        		String extensio = documentDetalls.getMetadades().getExtensio()!=null?documentDetalls.getMetadades().getExtensio().toString():".pdf";
            	
            	resultat = new DownloadableFile(
            			documentDetalls.getNom()+extensio,
            			documentDetalls.getContingut().getTipusMime(),
            			documentDetalls.getContingut().getContingut());

			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/expedientPeticio/"+data.get(1)+"/DescarregarJustificantReportGenerator", e);
				throw new ReportGenerationException(getResourceClass(), data.get(1).toString(), code, "S'ha produit un error al descarregar el jsutificant del registre.");
			}
            
            return resultat;
		}

		@Override
		public List<Serializable> generateData(String code, ExpedientPeticioResourceEntity entity, Serializable params) throws ReportGenerationException {
			List<Serializable> parametres = new ArrayList<Serializable>();
			parametres.add(entity.getRegistre().getJustificantArxiuUuid());
			parametres.add(entity.getId());
			return parametres;
		}
    	
		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}
    }
}