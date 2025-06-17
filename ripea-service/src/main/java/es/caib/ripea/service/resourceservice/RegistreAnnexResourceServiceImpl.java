package es.caib.ripea.service.resourceservice;

import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.RegistreAnnexResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService.PerspectiveApplicator;
import es.caib.ripea.service.base.service.BaseReadonlyResourceService.ReportGenerator;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ReportFileType;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.dto.DocumentVersioDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import es.caib.ripea.service.intf.resourceservice.RegistreAnnexResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistreAnnexResourceServiceImpl extends BaseMutableResourceService<RegistreAnnexResource, Long, RegistreAnnexResourceEntity> implements RegistreAnnexResourceService {

	private final PluginHelper pluginHelper;
	
    @PostConstruct
    public void init() {
    	register(RegistreAnnexResource.REPORT_DOWNLOAD_ANNEX, new DescarregarAnnexReportGenerator());
        register(RegistreAnnexResource.PERSPECTIVE_FIRMES, new AnnexFirmesPerspectiveApplicator());
    }
    
    private class AnnexFirmesPerspectiveApplicator implements PerspectiveApplicator<RegistreAnnexResourceEntity, RegistreAnnexResource> {
        @Override
        public void applySingle(String code, RegistreAnnexResourceEntity entity, RegistreAnnexResource resource) throws PerspectiveApplicationException {
        	resource.setFirmes(pluginHelper.validaSignaturaObtenirFirmes(entity.getUuid(), false));
        }
    }
    
    private class DescarregarAnnexReportGenerator implements ReportGenerator<RegistreAnnexResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
        	Document document = pluginHelper.arxiuDocumentConsultar(null, data.get(0).toString(), null, true, true);
            return new DownloadableFile(
            		document.getNom(),
            		document.getContingut().getTipusMime(),
            		document.getContingut().getContingut());
        }
		
		@Override
		public List<Serializable> generateData(String code, RegistreAnnexResourceEntity entity, Serializable params) throws ReportGenerationException {
            List<Serializable> parametres = new ArrayList<Serializable>();
            parametres.add(entity.getUuid());
            return parametres;
		}
    	
    }
}