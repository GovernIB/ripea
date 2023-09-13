
/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;

/**
For new transactions
 */
@Component
public class ExpedientHelper2 {


	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private DocumentFirmaServidorFirma documentFirmaServidorFirma;
	@Autowired
	private AplicacioService aplicacioService;
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void checkIfExpedientCanBeClosed(Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);

		expedientHelper.concurrencyCheckExpedientJaTancat(expedient);
		
		if (!cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty()) {
			throw new ValidationException("No es pot tancar un expedient amb errors de validació");
		}
		if (CollectionUtils.isEmpty(documentRepository.findByExpedientAndEsborrat(expedient, 0))) {
			throw new ValidationException("No es pot tancar un expedient sense cap document");
		}
		if (CollectionUtils.isNotEmpty(documentRepository.findEnProccessDeFirma(expedient))) {
			throw new ValidationException("No es pot tancar un expedient amb documents en procés de firma");
		}	
		if (CollectionUtils.isNotEmpty(documentRepository.findDocumentsDePortafirmesNoCustodiats(expedient))) {
			throw new ValidationException("No es pot tancar un expedient amb documents firmats de portafirmes pendents de custodiar");
		}
		if (CollectionUtils.isNotEmpty(documentRepository.findDocumentsPendentsReintentsArxiu(expedient, contingutHelper.getArxiuMaxReintentsDocuments()))) {
			throw new ValidationException("No es pot tancar un expedient amb documents amb reintents pendents de guardar a l'arxiu");
		}	
		if (CollectionUtils.isNotEmpty(registreAnnexRepository.findDocumentsDeAnotacionesNoMogutsASerieFinal(expedient))) {
			throw new ValidationException("No es pot tancar un expedient amb documents d'anotacions no moguts a la sèrie documental final");
		}		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteDocumentsEsborranysArxiu(Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		// remove eborranys in arxiu
		List<ContingutArxiu> contingutsArxiu = pluginHelper.arxiuExpedientConsultarPerUuid(expedient.getArxiuUuid()).getContinguts();
		for (ContingutArxiu contingutArxiu : contingutsArxiu) {
			if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT) {
				Document document = pluginHelper.arxiuDocumentConsultar(contingutArxiu.getIdentificador());
				if (document.getEstat() == DocumentEstat.ESBORRANY) {
					pluginHelper.arxiuDocumentEsborrar(document.getIdentificador());
				}
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteDocumentsNotSelectedDB(Long entitatId, Long expedientId, Long[] documentsPerFirmar) {

		List<DocumentEntity> docsToDelete = new ArrayList<>();
		docsToDelete.addAll(findNotSelected(entitatId, expedientId, documentsPerFirmar));
		docsToDelete.addAll(documentRepository.findDeleted(expedientId));
		
		for (DocumentEntity docToDelete : docsToDelete) {
			
			if (CollectionUtils.isNotEmpty(docToDelete.getAnnexos())) {
				throw new ValidationException("No está permitido esborrar documents procedents d'anotacions");
			}
			
			documentHelper.deleteDefinitiu(docToDelete);
		}

	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void closeExpedientDbAndArxiu(Long expedientId, String motiu) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		
		if (! isTancamentLogicActiu()) {
			expedient.updateEstat(ExpedientEstatEnumDto.TANCAT, motiu);
			expedient.updateEstatAdditional(null);
			contingutLogHelper.log(expedient, LogTipusEnumDto.TANCAMENT, null, null, false, false);
	
			pluginHelper.arxiuExpedientTancar(expedient);
		} else {
			expedient.updateEstat(ExpedientEstatEnumDto.TANCAT, motiu, getDiesPerTancament());
			expedient.updateEstatAdditional(null);
			contingutLogHelper.log(expedient, LogTipusEnumDto.TANCAMENT_LOGIC, null, null, false, false);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void closeExpedientArxiu(ExpedientEntity expedient) {
		expedient.updateTancatData();
		contingutLogHelper.log(expedient, LogTipusEnumDto.TANCAMENT, null, null, false, false);
		pluginHelper.arxiuExpedientTancar(expedient);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markAllDocumentsEsborranysAsDefinitiusArxiu(Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		// Marquem esborannys firmats com a definitius
		List<DocumentEntity> esborranysArxiu = documentRepository.findByExpedientAndArxiuEstat(
				expedient,
				ArxiuEstatEnumDto.ESBORRANY);
		
		for (DocumentEntity pendent : esborranysArxiu) {
			documentHelper.actualitzarEstatADefinititu(pendent.getId());
		} 
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<DocumentEntity> findNotSelected(Long entitatId, Long expedientId, Long[] documentsSelected) {
		List<Long> documentsSelectedList = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(documentsSelected)) {
			documentsSelectedList = Arrays.asList(documentsSelected);
		}
		
		List<DocumentEntity> esborranys = documentHelper.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
				entitatId,
				expedientId);
		
		List<DocumentEntity> notSelected = new ArrayList<>();
		
		for (DocumentEntity esborrany : esborranys) {
			if (!documentsSelectedList.contains(esborrany.getId())) {
				notSelected.add(esborrany);
			}
		}
		
		return notSelected;
	}
	
	public void signDocumentsSelected(String motiu, Long[] documentsPerFirmar) {
		// Firmam els documents seleccionats
		if (ArrayUtils.isNotEmpty(documentsPerFirmar)) {
			for (Long documentPerFirmar : documentsPerFirmar) {
				documentFirmaServidorFirma.firmar(documentPerFirmar, motiu);
			}
		}
	}
	
	private boolean isTancamentLogicActiu() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.tancament.logic"));
	}
	
	private int getDiesPerTancament() {
		String dies = aplicacioService.propertyFindByNom("es.caib.ripea.expedient.tancament.logic.dies");
		
		return dies != null ? Integer.parseInt(dies) : 60;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper2.class);

}