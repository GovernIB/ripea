
/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;

/**
For new transactions
 */
@Component
public class ExpedientHelper2 {

	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private InteressatRepository interessatRepository;
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
	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void checkIfExpedientCanBeClosed(Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		expedientHelper.concurrencyCheckExpedientJaTancat(expedient);
		if (anyExecucioMassiva(expedient)) {
			throw new ValidationException("No es pot tancar un expedient amb execucions massives pendents de finalitzar");
		}
		List<ValidacioErrorDto> errorsExp = cacheHelper.findErrorsValidacioPerNode(expedient);
		if (!errorsExp.isEmpty()) {
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
//		if (CollectionUtils.isNotEmpty(registreAnnexRepository.findDocumentsDeAnotacionesNoMogutsASerieFinal(expedient))) {
//			throw new ValidationException("No es pot tancar un expedient amb documents d'anotacions no moguts a la sèrie documental final");
//		}
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
	public void deleteDocumentsNotSelectedDB(Long entitatId, Long expedientId, List<Long> documentsPerFirmar) {

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
			logger.debug("Tancant expedient a l'arxiu per acció iniciada per usuari...");
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
		logger.debug("Tancant expedient a l'arxiu desde acció en segon pla...");		
		pluginHelper.arxiuExpedientTancar(expedient);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualitzaEstatNotificacionsCaducades(Long expedientId) {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		for (DocumentEntity document : documents) {
	        List<DocumentNotificacioEntity> notificacionsPendents = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc(document);
	        if (notificacionsPendents!=null && notificacionsPendents.size()>0) {
	            if (notificacionsPendents.get(0).isCaducada() && !notificacionsPendents.get(0).isNotificacioFinalitzada()) {
	        		if (notificacionsPendents.get(0).getDocumentEnviamentInteressats()!=null) {
	        			for (DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity: notificacionsPendents.get(0).getDocumentEnviamentInteressats()) {
	        				try {
	        					pluginHelper.notificacioConsultarIActualitzarEstat(documentEnviamentInteressatEntity);
	        				} catch (Exception ex) {
	        					logger.warn("No s'ha pogut actualitzar l'estat de la notificació "+notificacionsPendents.get(0).getNotificacioIdentificador());
	        				}
	        			}
	        		}
	            }
	        }
        }
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
	public List<DocumentEntity> findNotSelected(Long entitatId, Long expedientId, List<Long> documentsSelected) {
		
		List<DocumentEntity> esborranys = documentHelper.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
				entitatId,
				expedientId);
		
		List<DocumentEntity> notSelected = new ArrayList<>();
		
		for (DocumentEntity esborrany : esborranys) {
			if (!documentsSelected.contains(esborrany.getId())) {
				notSelected.add(esborrany);
			}
		}
		
		return notSelected;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Long> reprocessarAnnexesAnotacionsAmbError(Long expedientId) {
		
		List<Long> documentsClonar = new ArrayList<Long>();
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		List<RegistreAnnexEntity> annexesReprocessar = registreAnnexRepository.findDocumentsDeAnotacionesNoMogutsASerieFinal(expedient);
		
		if(annexesReprocessar!=null) {
			for (RegistreAnnexEntity ra: annexesReprocessar) {
				//El registre té errors de processament
				if (Utils.isNotEmpty(ra.getError())) {
					//Si no esta creat el document, no podem crear-lo, ja que no sabriem quin tipus ha de tenir. Això es selecciona al acceptar la anotacio.
					//La segona condició es perque no volem comprovar documents que ja ham estat clonats. Ja que el error de la anotació el mantenim.
					//i si es reintenta el tancar expedient, pot ser aquest annex ja estigui tractat i clonat (tendrá Uuid_distribucio)
					if (ra.getDocument()!=null && ra.getDocument().getUuid_distribucio()==null) {
						boolean clonarDocument = false;
						//Ja ens avisa distribució, que la firma no es correcte
						if (!ra.isValidacioFirmaCorrecte()) {
							clonarDocument = true;
						} else {
							
							//Reintentam mourer annex al Arxiu...
							Exception excepcio = expedientHelper.moveAnnexArxiu(ra.getId());
							
							//Novament ha donat un error al mourer el annex al arxiu.
							if (excepcio!=null) {
								clonarDocument = true;
							}
						}
						
						if (clonarDocument) {
							//Afegim al document per clonar en una llista, es farà en una funcio REQUIRES_NEW posterior
							//Necessitam posar la variable firma correcte a false, perque la funció de firmaEnServidor elimini la firma actual
							ra.getDocument().setValidacioFirmaCorrecte(false);
							documentsClonar.add(ra.getDocument().getId());
						}
					}
				}
			}
		}
		return documentsClonar;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void clonarDocumentsAmbFirmaInvalida(Long expedientId, List<DocumentEntity> documentsClonar, Long[] documentsPerFirmar) {
		if (documentsClonar!=null) {
			ExpedientEntity expedient = expedientRepository.findOne(expedientId);
			for (DocumentEntity document: documentsClonar) {
				
				//Cream un document igual al anterior, pero sense les firmes.
				DocumentDto docDto = conversioTipusHelper.convertir(document, DocumentDto.class);
				docDto.setId(null);
				docDto.eliminaDadesFirma();
				docDto.setArxiuUuid(null);
				
				docDto.setNom(Utils.addSuffixToFileName(docDto.getNom(), "_copiaFirmaServidor"));
				
				//Crea un nou document a la mateixa ubicació que l'anterior, pero sense tipus (metaDocument=null) per evitar error de multiplicitat
				DocumentDto nouDocument = documentHelper.crearDocument(
						docDto,
						document.getPare(),
						expedient,
						null, //document.getMetaDocument()
						false);
				
				//Relacionam els dos documents, original i clon
//				document.setDocumentClonId(nouDocument.getId());
				
				//Eliminam les dades de arxiu del document original amb errors, a arxiu hi haurà el clon
				document.eliminaDadesArxiu();
				
				//Si el document original tenia annexes amb error de processament, esborram el error
				if (document.getAnnexos()!=null) {
					for (RegistreAnnexEntity ra: document.getAnnexos()) {
						ra.updateError(null);
					}
				}
				
				//El document creat, el voldrem firmam en servidor a una passa posterior, l'afegim a la llista
				documentsPerFirmar = Utils.addElementToArray(documentsPerFirmar, nouDocument.getId());
				//Mentre que el document original, el retiram de la llista, perque no volem eliminar les firmes ni firmarlo en servidor.
				documentsPerFirmar = Utils.removeElementFromArray(documentsPerFirmar, document.getId());
			}
		}
	}
	
	public void signDocumentsSelected(String motiu, List<Long> documentsPerFirmar, List<Long> documentsClonar) {
		// Firmam els documents seleccionats en servidor
		for (Long documentPerFirmar : documentsPerFirmar) {
			if (documentPerFirmar!=null) {
				documentFirmaServidorFirma.firmar(documentPerFirmar, motiu, documentsClonar);
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

	private boolean anyExecucioMassiva(ExpedientEntity expedient) {
		List<Long> elementIds = new ArrayList<Long>();
		elementIds.add(expedient.getId());
		List<DocumentEntity> documentsExpedient = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		for (DocumentEntity document: documentsExpedient) {
			elementIds.add(document.getId());
		}
		List<InteressatEntity> interessatsExpedient = interessatRepository.findByExpedient(expedient);
		for (InteressatEntity interessat: interessatsExpedient) {
			elementIds.add(interessat.getId());
		}
		long pendentsCount = execucioMassivaContingutRepository.countByElementIdInAndEstat(
				elementIds,
				ExecucioMassivaEstatDto.ESTAT_PENDENT);
		return pendentsCount > 0;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper2.class);

}