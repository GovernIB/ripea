package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaDocumentFluxPortafibEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaDocumentFluxPortafibRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import es.caib.ripea.service.intf.exception.ExisteixenDocumentsException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import io.micrometer.core.instrument.Timer;

@Component
public class MetaDocumentHelper {
	
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ApplicationHelper applicationHelper;
	@Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private PinbalServeiRepository pinbalServeiRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private MetaDocumentFluxPortafibRepository metaDocumentFluxPortafibRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private UsuariRepository usuariRepository;
	
	public void marcarPerDefecte(
			Long entitatId, 
			Long metaExpedientId,
			Long metaDocumentId,
			boolean remove) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		MetaDocumentEntity currentMetaDocument = entityComprovarHelper.comprovarMetaDocument(
				metaDocumentId);
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(
				entitat, 
				metaExpedientId);
//		Recupera els metadocuments del mateix procediment
		Set<MetaDocumentEntity> metaDocuments = metaExpedientEntity.getMetaDocuments();
		
		for (MetaDocumentEntity metaDocumentEntity : metaDocuments) {
			if (metaDocumentEntity.isPerDefecte()) {
				metaDocumentEntity.updatePerDefecte(false);
			}
		}
		if (!remove)
			currentMetaDocument.updatePerDefecte(true);
	}
	
	public MetaDocumentEntity delete(Long entitatId, Long metaExpedientId, Long id, String rolActual, Long organId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);

		MetaExpedientEntity metaExpedient = null;
		MetaDocumentEntity metaDocumentEntity = null;
		
		if (metaExpedientId!=null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
			metaDocumentEntity = entityComprovarHelper.comprovarMetaDocument(entitat, metaExpedient, id);
		} else {
			metaDocumentEntity = metaDocumentRepository.findById(id).get();
		}
		
		List<DocumentEntity> docs = documentRepository.findByMetaNode(metaDocumentEntity);
		if (docs != null && !docs.isEmpty()) {
			throw new ExisteixenDocumentsException();
		}
		
		//Eliminar les possibles validacions sobre el document
		List<MetaExpedientTascaValidacioEntity> validacionsDoc = metaExpedientTascaValidacioRepository.findByItemValidacioAndItemId(
				ItemValidacioTascaEnum.DOCUMENT,
				id);
		
		if (validacionsDoc!=null && validacionsDoc.size()>0) {
			metaExpedientTascaValidacioRepository.deleteAll(validacionsDoc);
		}
		
		metaDocumentRepository.delete(metaDocumentEntity);
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		
		return metaDocumentEntity;
	}
	
	public MetaDocumentEntity update(
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		
		MetaDocumentEntity metaDocumentEntity = null;
		
		//El Metadocument pot ser generic (sense associar a un procediment)
		if (metaExpedientId!=null) {
			metaDocumentEntity = metaDocumentRepository.findByMetaExpedientIdAndCodi(metaExpedientId, metaDocument.getCodi());
		} else {
			metaDocumentEntity = metaDocumentRepository.findById(metaDocument.getId()).get();
		}
		
		PinbalServeiEntity pinbalServeiEntity = null;
		if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
			pinbalServeiEntity = pinbalServeiRepository.findById(metaDocument.getPinbalServei().getId()).orElse(null);
		}

		metaDocumentEntity.update(
				metaDocumentEntity.getCodi(),
				metaDocument.getNom(),
				metaDocument.getDescripcio(),
				metaDocument.getMultiplicitat(),
				metaDocument.isFirmaPortafirmesActiva(),
				metaDocument.getPortafirmesDocumentTipus(),
				metaDocument.getPortafirmesResponsables(),
				metaDocument.getPortafirmesSequenciaTipus(),
				metaDocument.getPortafirmesCustodiaTipus(),
				metaDocument.isFirmaPassarelaActiva(),
				metaDocument.getFirmaPassarelaCustodiaTipus(),
				metaDocument.getNtiOrigen(),
				metaDocument.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument.isFirmaBiometricaActiva(),
				metaDocument.isBiometricaLectura(),
				metaDocument.getPortafirmesFluxTipus(),
				metaDocument.isPinbalActiu(),
				pinbalServeiEntity,
				metaDocument.getPinbalFinalitat(),
				metaDocument.isPinbalUtilitzarCifOrgan());
		
		metaDocumentEntity.updatePerDefecte(metaDocument.isPerDefecte());
		metaDocumentEntity.updateOrdre(metaDocument.getOrdre());
		
		if (plantillaContingut != null) {
			metaDocumentEntity.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		
		updateFluxos(metaDocumentEntity, metaDocument.getPortafirmesFluxosId());
		
		return metaDocumentEntity;
	}
	
	//Es crida desde el servei de BASEBOOT despres del create
	public void updateFluxosFirmaMetaDoc(Long metaDocumentEntityId, String[] newFluxos) {
		metaDocumentRepository.findById(metaDocumentEntityId).get();
	}
	
	@Transactional(dontRollbackOn = SistemaExternException.class)
	private void updateFluxos(MetaDocumentEntity metaDocumentEntity, String[] newFluxos) {
		
		if (newFluxos!=null && newFluxos.length>0) {

			for (String fluxId : newFluxos) {
				//Insertar els fluxos que no existeixen actualment a la entitat
				if (!metaDocumentEntity.fluxeExistById(fluxId))	{
					//Revisam si hi ha algun flux amb aquest ID pero sense meta-doc associat.
					MetaDocumentFluxPortafibEntity fluxOrfe = metaDocumentFluxPortafibRepository.findByMetaDocumentIsNullAndPortafirmesFluxId(fluxId);
					MetaDocumentFluxPortafibEntity metaDocumentFluxPortafibEntity = new MetaDocumentFluxPortafibEntity();
					if (fluxOrfe!=null) {
						metaDocumentFluxPortafibEntity = fluxOrfe;
					}
					metaDocumentFluxPortafibEntity.setMetaDocument(metaDocumentEntity);
					metaDocumentFluxPortafibEntity.setPortafirmesFluxId(fluxId);
					try {
						Authentication auth = SecurityContextHolder.getContext().getAuthentication();
						UsuariEntity usuariActualEntity = usuariRepository.findById(auth.getName()).get();
						metaDocumentFluxPortafibEntity.setPortafirmesFluxDesc(pluginHelper.portafirmesRecuperarInfoFluxDeFirma(
								fluxId,
								usuariActualEntity.getIdioma(),
								false).getNom());
					} catch (SistemaExternException ex) {
						//No s'ha trobat el fluxe
					}
					metaDocumentFluxPortafibRepository.save(metaDocumentFluxPortafibEntity);
				}
			}
			
		}
		
		//Eliminar els fluxos anteriors que ja no apliquen segons el DTO rebut
		metaDocumentEntity.getFluxosFirma().removeIf(flux -> 
			!Arrays.asList(newFluxos).contains(flux.getPortafirmesFluxId())
		);
	}
	
	public MetaDocumentEntity create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut,
			String rolActual,
			Long organId) {
		
		Timer.Sample sample = Timer.start(applicationHelper.getMeterRegistry());
		
		try {
		
			logger.debug("Creant un nou meta-document (entitatId=" + entitatId + ", metaExpedientId=" + metaExpedientId + ", metaDocument=" + metaDocument + ")");
	
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false, 
					true, false);
			
			MetaExpedientEntity metaExpedient = null;
			int ordre = 0;
			//El Metadocument pot ser generic (sense associar a un procediment)
			if (metaExpedientId!=null) {
				metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
				ordre = metaDocumentRepository.countByMetaExpedient(metaExpedient);
			}
			
			PinbalServeiEntity pinbalServeiEntity = null;
			if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
				pinbalServeiEntity = pinbalServeiRepository.findById(metaDocument.getPinbalServei().getId()).orElse(null);
			}
			
			MetaDocumentEntity newMetaDocumententity = MetaDocumentEntity.getBuilder(
					entitat,
					metaDocument.getCodi(),
					metaDocument.getNom(),
					metaDocument.getMultiplicitat(),
					metaExpedient,
					metaDocument.getNtiOrigen(),
					metaDocument.getNtiEstadoElaboracion(),
					metaDocument.getNtiTipoDocumental(),
					metaDocument.isPinbalActiu(),
					metaDocument.getPinbalFinalitat(),
					ordre).
					biometricaLectura(metaDocument.isBiometricaLectura()).
					firmaBiometricaActiva(metaDocument.isFirmaBiometricaActiva()).
					firmaPortafirmesActiva(metaDocument.isFirmaPortafirmesActiva()).
					descripcio(metaDocument.getDescripcio()).
					portafirmesDocumentTipus(metaDocument.getPortafirmesDocumentTipus()).
					portafirmesResponsables(metaDocument.getPortafirmesResponsables()).
					portafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus()).
					portafirmesCustodiaTipus(metaDocument.getPortafirmesCustodiaTipus()).
					firmaPassarelaActiva(metaDocument.isFirmaPassarelaActiva()).
					firmaPassarelaCustodiaTipus(metaDocument.getFirmaPassarelaCustodiaTipus()).
					portafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus()).
					pinbalServei(pinbalServeiEntity).
					build();
			
			newMetaDocumententity.updatePerDefecte(metaDocument.isPerDefecte());
			newMetaDocumententity.setPinbalUtilitzarCifOrgan(metaDocument.isPinbalUtilitzarCifOrgan());
			
			if (plantillaContingut != null) {
				newMetaDocumententity.updatePlantilla(
						plantillaNom,
						plantillaContentType,
						plantillaContingut);
			}
			
			if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
				metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
			}
	
			newMetaDocumententity = metaDocumentRepository.save(newMetaDocumententity);
	
			updateFluxos(newMetaDocumententity, metaDocument.getPortafirmesFluxosId());

			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Procediment.metaDoc", "resultado", "exito");
		
			return newMetaDocumententity;
			
		} catch (Exception e) {
			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Procediment.metaDoc", "resultado", "error");
			throw e;
		}			
	}
	
	public MetaDocumentEntity findByCodiAndProcediment(MetaExpedientEntity metaExpedientEntity, String codi) {
		return metaDocumentRepository.findByMetaExpedientAndCodi(metaExpedientEntity, codi);
	}
	
	public List<MetaDocumentEntity> findActiusPerCreacio(EntitatEntity entitat, Long contingutId, Long metaExpedientId, boolean findAllMarkDisponiblesPerCreacio) {
		
		List<MetaDocumentEntity> metaDocuments = new ArrayList<>();
		
		if (contingutId != null) {
			ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
					contingutId);
			ExpedientEntity expedient = contingutHelper.getExpedientSuperior(
					contingut,
					true,
					false,
					false, 
					null);
			metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
					entitat,
					expedient, 
					null, 
					findAllMarkDisponiblesPerCreacio);
		} else {
			MetaExpedientEntity metaExpedient =  metaExpedientRepository.getOne(metaExpedientId);
			metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
					entitat,
					null, 
					metaExpedient, 
					findAllMarkDisponiblesPerCreacio);
		}
		return metaDocuments;
	}
	
	public List<MetaDocumentEntity> findActiusPerModificacio(EntitatEntity entitat, Long documentId) {
		
		DocumentEntity document = entityComprovarHelper.comprovarDocument(entitat,null,documentId,false,false,false,false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(document, true,false,false, null);
		// Han de ser els mateixos que per a la creació però afegit el meta-document
		// del document que es vol modificar
		List<MetaDocumentEntity> metaDocuments = findMetaDocumentsDisponiblesPerCreacio(
				entitat,
				expedientSuperior, 
				null, 
				false);
		if (document.getMetaDocument() != null && !metaDocuments.contains(document.getMetaDocument())) {
			metaDocuments.add(document.getMetaDocument());
		}
		Collections.sort(metaDocuments, new Comparator<MetaDocumentEntity>(){
		     public int compare(MetaDocumentEntity o1, MetaDocumentEntity o2){
		         if(o1.getNom().toLowerCase() == o2.getNom().toLowerCase())
		             return 0;
		         return o1.getNom().toLowerCase().compareTo(o2.getNom().toLowerCase()) < -1 ? -1 : 1;
		     }
		});
		return metaDocuments;
	}
	
	public List<MetaDocumentEntity> findMetaDocumentsDisponiblesPerCreacio(
			EntitatEntity entitat,
			ExpedientEntity expedient, 
			MetaExpedientEntity metaExpedient, 
			boolean findAllMarkDisponiblesPerCreacio) {
		
		long t1 = System.currentTimeMillis();
		
		List<MetaDocumentEntity> metaDocuments = new ArrayList<MetaDocumentEntity>();
		
		// Dels meta-documents actius pel meta-expedient només deixa els que encara es poden afegir segons la multiplicitat.
		List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedientAndActiuTrue(
				expedient != null ? expedient.getMetaExpedient() : metaExpedient);
		
		if (expedient != null ? expedient.getMetaExpedient().isPermetMetadocsGenerals() : metaExpedient.isPermetMetadocsGenerals()) {
			metaDocumentsDelMetaExpedient.addAll(metaDocumentRepository.findWithoutMetaExpedient());
		}
		
		if (expedient != null) {
			
			// Nomes retorna els documents que no s'hagin esborrat
			List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
					expedient,
					0);
			
			for (MetaDocumentEntity metaDocument: metaDocumentsDelMetaExpedient) {
				boolean afegir = true;
				for (DocumentEntity document: documents) {
					if (document.getMetaNode() != null && document.getMetaNode().equals(metaDocument)) {
						if (metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1) || metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1))
							afegir = false;
						break;
					}
				}
				if (findAllMarkDisponiblesPerCreacio) {
					metaDocument.setLeftPerCreacio(afegir);
					metaDocuments.add(metaDocument);
				} else {
					if (afegir) {
						metaDocuments.add(metaDocument);
					}
				}
			}
			Collections.sort(metaDocuments, new Comparator<MetaDocumentEntity>(){
			     public int compare(MetaDocumentEntity o1, MetaDocumentEntity o2){
			         if(o1.getNom().toLowerCase() == o2.getNom().toLowerCase())
			             return 0;
			         return o1.getNom().toLowerCase().compareTo(o2.getNom().toLowerCase()) < -1 ? -1 : 1;
			     }
			});
		} else {
			metaDocuments = metaDocumentsDelMetaExpedient;
		}

		
    	if (expedient != null && cacheHelper.mostrarLogsRendiment())
    		logger.info("findMetaDocumentsDisponiblesPerCreacio time (" + expedient.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		
		return metaDocuments;
	}
	
	public List<MetaDocumentEntity> findMetaDocumentsPinbalDisponiblesPerCreacio(ExpedientEntity expedientEntity) {
		List<MetaDocumentEntity> aux = findMetaDocumentsDisponiblesPerCreacio(null, expedientEntity, null, false);
		List<MetaDocumentEntity> resultat = new ArrayList<MetaDocumentEntity>();
		if (aux!=null) {
			for (MetaDocumentEntity metaDoc: aux) {
				if (metaDoc.isPinbalActiu()) {
					resultat.add(metaDoc);
				}
			}
		}
		return resultat;
	}
	
	public List<MetaDocumentEntity> findMetaDocumentsPinbalDisponiblesPerCreacio(Long expedientId) {
		return findMetaDocumentsPinbalDisponiblesPerCreacio(expedientRepository.findById(expedientId).get());
	}

	public String initMetaDocumentFlux(Long metaDocId) throws Exception {return "";}

	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentHelper.class);
}