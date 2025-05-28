package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.PinbalServeiEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;

@Component
public class MetaDocumentHelper {
	
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private CacheHelper cacheHelper;
	
	@Autowired private PinbalServeiRepository pinbalServeiRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private DocumentRepository documentRepository;
	
	public MetaDocumentEntity update(
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut,
			String rolActual,
			Long organId) {
		MetaDocumentEntity metaDocumentEntity = metaDocumentRepository.findByMetaExpedientIdAndCodi(metaExpedientId, metaDocument.getCodi());
		PinbalServeiEntity pinbalServeiEntity = null;
		if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
			pinbalServeiEntity = pinbalServeiRepository.findById(metaDocument.getPinbalServei().getId()).orElse(null);
		}
		//* = No s'actualitza
		metaDocumentEntity.update(
				metaDocumentEntity.getCodi(), //*
				metaDocument.getNom(),
				metaDocument.getDescripcio(),
				metaDocument.getMultiplicitat(),
				metaDocument.isFirmaPortafirmesActiva(),
				metaDocument.getPortafirmesDocumentTipus(),
				metaDocument.getPortafirmesFluxId(),
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
		
		return metaDocumentEntity;
	}
	
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut,
			String rolActual,
			Long organId) {
		
		logger.debug("Creant un nou meta-document (" +
				"entitatId=" + entitatId + ", " +
				"metaExpedientId=" + metaExpedientId + ", " +
				"metaDocument=" + metaDocument + ")");

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		
		PinbalServeiEntity pinbalServeiEntity = null;
		if (metaDocument.getPinbalServei()!=null && metaDocument.getPinbalServei().getId()!=null) {
			pinbalServeiEntity = pinbalServeiRepository.findById(metaDocument.getPinbalServei().getId()).orElse(null);
		}
		
		MetaDocumentEntity entity = MetaDocumentEntity.getBuilder(
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
				metaDocumentRepository.countByMetaExpedient(metaExpedient)).
				biometricaLectura(metaDocument.isBiometricaLectura()).
				firmaBiometricaActiva(metaDocument.isFirmaBiometricaActiva()).
				firmaPortafirmesActiva(metaDocument.isFirmaPortafirmesActiva()).
				descripcio(metaDocument.getDescripcio()).
				portafirmesDocumentTipus(metaDocument.getPortafirmesDocumentTipus()).
				portafirmesFluxId(metaDocument.getPortafirmesFluxId()).
				portafirmesResponsables(metaDocument.getPortafirmesResponsables()).
				portafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus()).
				portafirmesCustodiaTipus(metaDocument.getPortafirmesCustodiaTipus()).
				firmaPassarelaActiva(metaDocument.isFirmaPassarelaActiva()).
				firmaPassarelaCustodiaTipus(metaDocument.getFirmaPassarelaCustodiaTipus()).
				portafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus()).
				pinbalServei(pinbalServeiEntity).
				build();
		
		entity.updatePerDefecte(metaDocument.isPerDefecte());
		entity.setPinbalUtilitzarCifOrgan(metaDocument.isPinbalUtilitzarCifOrgan());
		
		if (plantillaContingut != null) {
			entity.updatePlantilla(
					plantillaNom,
					plantillaContentType,
					plantillaContingut);
		}
		
		if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}

		return conversioTipusHelper.convertir(
				metaDocumentRepository.save(entity),
				MetaDocumentDto.class);
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
	
	public List<MetaDocumentEntity> findMetaDocumentsPinbalDisponiblesPerCreacio(Long metaExpedientId) {
		List<MetaDocumentEntity> aux = findMetaDocumentsDisponiblesPerCreacio(null, null, metaExpedientRepository.findById(metaExpedientId).get(), false);
		List<MetaDocumentEntity> resultat = new ArrayList<MetaDocumentEntity>();
		if (aux!=null) {
			for (MetaDocumentEntity metaDoc: aux) {
				if (metaDoc.isLeftPerCreacio() && metaDoc.isPinbalActiu()) {
					resultat.add(metaDoc);
				}
			}
		}
		return resultat;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentHelper.class);
}
