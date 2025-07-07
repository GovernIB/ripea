package es.caib.ripea.service.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.ripea.persistence.entity.AlertaEntity;
import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DadaEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.NodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.AlertaRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DadaRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaNodeRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.helper.ArxiuConversions;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutHelper;
import es.caib.ripea.service.helper.ContingutLogHelper;
import es.caib.ripea.service.helper.ContingutsOrfesHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.DateHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PaginacioHelper.Converter;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.AlertaDto;
import es.caib.ripea.service.intf.dto.ArxiuContingutDto;
import es.caib.ripea.service.intf.dto.ArxiuContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.service.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.CodiValorDto;
import es.caib.ripea.service.intf.dto.ContingutDto;
import es.caib.ripea.service.intf.dto.ContingutFiltreDto;
import es.caib.ripea.service.intf.dto.ContingutLogDetallsDto;
import es.caib.ripea.service.intf.dto.ContingutLogDto;
import es.caib.ripea.service.intf.dto.ContingutMassiuDto;
import es.caib.ripea.service.intf.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.service.intf.dto.ContingutMovimentDto;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DominiDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermissionEnumDto;
import es.caib.ripea.service.intf.dto.ResultDocumentsSenseContingut;
import es.caib.ripea.service.intf.dto.ResultDocumentsSenseContingut.ResultDocumentSenseContingut;
import es.caib.ripea.service.intf.dto.ResultDto;
import es.caib.ripea.service.intf.dto.ResultEnumDto;
import es.caib.ripea.service.intf.dto.ResultatConsultaDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.dto.ValidacioErrorDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.service.ContingutService;
import es.caib.ripea.service.intf.service.DominiService;
import es.caib.ripea.service.intf.utils.DateUtil;
import es.caib.ripea.service.intf.utils.Utils;

@Service
public class ContingutServiceImpl implements ContingutService {

	@Autowired private ContingutRepository contingutRepository;
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private DadaRepository dadaRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private MetaNodeRepository metaNodeRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private AlertaRepository alertaRepository;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private TipusDocumentalRepository tipusDocumentalRepository;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private ContingutsOrfesHelper contingutRepositoryHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private DominiService dominiService;
	@Autowired private ConfigHelper configHelper;

	@Transactional
	@Override
	public void dadaSave(Long entitatId, Long contingutId, Map<String, Object> valors, Long tascaId) throws NotFoundException {
		
		logger.debug("Guardant dades del node (entitatId=" + entitatId + ", contingutId=" + contingutId + ", valors=" + valors + ")");
		
		NodeEntity node = null;
		if (tascaId == null) {
			node = contingutHelper.comprovarNodeDinsExpedientModificable(
					entitatId,
					contingutId,
					false,
					true,
					false,
					false, true, null);
		} else {
			node = contingutHelper.comprovarNodePertanyTascaAccesible(
					tascaId,
					contingutId);
		}

		// Esborra les dades no especificades 
		for (DadaEntity dada: dadaRepository.findByNode(node)) {
			if (!valors.keySet().contains(dada.getMetaDada().getCodi())) {
				dadaRepository.delete(dada);

				MetaDadaEntity metaDada = dada.getMetaDada();
				if (node instanceof ExpedientEntity && isPropagarMetadadesActiu() && metaDada.isEnviable()) {
					// Obtenir nom domini per guardar a l'arxiu per metadades 'enviables'
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
						pluginHelper.arxiuExpedientMetadadesActualitzar((ExpedientEntity)node, metaDada, "");
					}
				}
			}
		}

		// Modifica les dades existents
		for (String dadaCodi: valors.keySet()) {
			nodeDadaGuardar(
					node,
					dadaCodi,
					valors.get(dadaCodi),
					entitatId);
		}
		cacheHelper.evictErrorsValidacioPerNode(node);
	}
	
	private void nodeDadaGuardar(
			NodeEntity node,
			String dadaCodi,
			Object dadaValor,
			Long entitatId) {
		MetaDadaEntity metaDada = metaDadaRepository.findByMetaNodeAndCodi(
				node.getMetaNode(),
				dadaCodi);
		if (metaDada == null) {
			throw new ValidationException(
					node.getId(),
					NodeEntity.class,
					"No s'ha trobat la metaDada amb el codi " + dadaCodi);
		}
		List<DadaEntity> dades = dadaRepository.findByNodeAndMetaDadaOrderByOrdreAsc(
				node,
				metaDada);
		Object[] valors = (dadaValor instanceof Object[]) ? (Object[])dadaValor : new Object[] {dadaValor};
		// Esborra els valors nulls
		List<Object> valorsSenseNull = new ArrayList<Object>();
		for (Object o: valors) {
			if (o != null)
				valorsSenseNull.add(o);
		}
		// Esborra les dades ja creades que sobren
		if (dades.size() > valorsSenseNull.size()) {
			for (int i = valorsSenseNull.size(); i < dades.size(); i++) {
				dadaRepository.delete(dades.get(i));
			}
		}
		//Els valors de tipus domini, es guarden en un sol valor separat per comes. Diferent a la resta.
//		if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
//			String valorsDomini = StringUtils.join(valorsSenseNull, ",");
//		    valorsSenseNull.clear();
//		    valorsSenseNull.add(valorsDomini);
//		}
		// Modifica o crea les dades
		for (int i = 0; i < valorsSenseNull.size(); i++) {
			DadaEntity dada = (i < dades.size()) ? dades.get(i) : null;
			if (dada != null) {
				dada.update(
						valorsSenseNull.get(i),
						i);
				contingutLogHelper.log(
						node,
						LogTipusEnumDto.MODIFICACIO,
						dada,
						LogObjecteTipusEnumDto.DADA,
						LogTipusEnumDto.MODIFICACIO,
						dadaCodi,
						dada.getValorComString(),
						false,
						false);
			} else {
				if (valorsSenseNull.get(i)!=null && !"".equals(valorsSenseNull.get(i))) {
					dada = DadaEntity.getBuilder(
							metaDada,
							node,
							valorsSenseNull.get(i),
							i).build();
					dadaRepository.save(dada);
					contingutLogHelper.log(
							node,
							LogTipusEnumDto.MODIFICACIO,
							dada,
							LogObjecteTipusEnumDto.DADA,
							LogTipusEnumDto.CREACIO,
							dadaCodi,
							dada.getValorComString(),
							false,
							false);
				}
			}
			
			if (node instanceof ExpedientEntity && isPropagarMetadadesActiu() && metaDada.isEnviable()) {
				// Obtenir nom domini per guardar a l'arxiu per metadades 'enviables'
				if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
					DominiDto domini = dominiService.findByCodiAndEntitat(metaDada.getCodi(), entitatId);
					String valorDomini = dada.getValorComString();
					
					String idsDomini = buildIdsDominiString(valorDomini, entitatId, domini);
					
					pluginHelper.arxiuExpedientMetadadesActualitzar((ExpedientEntity)node, metaDada, idsDomini);
				} else {
					pluginHelper.arxiuExpedientMetadadesActualitzar((ExpedientEntity)node, metaDada, dada.getValorComString());
				}
			}
		}
	}

	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public void deleteReversible(
			Long entitatId,
			Long contingutId, 
			String rolActual, 
			Long tascaId) throws IOException {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		contingutHelper.deleteReversible(
				entitatId,
				contingutId,
				tascaId,
				rolActual);
	}

	@Transactional
	@Override
	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
	public void deleteDefinitiu(
			Long entitatId,
			Long contingutId) {
		logger.debug("Esborrant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		// No es comproven permisos perquè això només ho pot fer l'administrador
		if (contingut.getPare() != null) {
			contingut.getPare().getFills().remove(contingut);
		}
		
		if (contingut instanceof ExpedientEntity && contingut.getFills() != null && !contingut.getFills().isEmpty()) {
			List<ContingutEntity> descendants = new ArrayList<>();
			contingutHelper.findDescendants(contingut, descendants, false, true);
			
			Iterator<ContingutEntity> itr = descendants.iterator();
			while (itr.hasNext()) {
				ContingutEntity cont = itr.next();
				if (cont.getPare() != null) {
					cont.getPare().getFills().remove(cont);
				}
				if (cont instanceof DocumentEntity) {
					documentHelper.deleteDefinitiu((DocumentEntity) cont);
				} else {
					contingutRepository.delete(cont);
				}
				
			}
		} else if (contingut instanceof DocumentEntity) {
			documentHelper.deleteDefinitiu((DocumentEntity) contingut);
		} else {
			contingutRepository.delete(contingut);
		}
	}
	
	@Transactional
	@Override
	public void undelete(
			Long entitatId,
			Long contingutId) throws IOException {
		logger.debug("Recuperant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		// No es comproven permisos perquè això només ho pot fer l'administrador
		if (contingut.getEsborrat() == 0) {
			logger.error("Aquest contingut no està esborrat (contingutId=" + contingutId + ")");
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"Aquest contingut no està esborrat");
		}
		
		if (contingut instanceof DocumentEntity) {
			String uniqueNameInPare = contingutHelper.getUniqueNameInPare(contingut.getNom(), contingut.getPare().getId());
			contingut.updateNom(uniqueNameInPare);
		} else {
			boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
					contingut.getPare(),
					contingut.getNom(),
					0) != null;
			if (nomDuplicat) {
				throw new ValidationException(
						contingutId,
						ContingutEntity.class,
						"Ja existeix un altre contingut amb el mateix nom dins el mateix pare");
			}
		}

		// Recupera el contingut esborrat
		contingut.updateEsborrat(0);

		// Registra al log la recuperació del contingut
		contingutLogHelper.log(
				contingut,
				LogTipusEnumDto.RECUPERACIO,
				null,
				null,
				true,
				true);

		if (!contingutHelper.conteDocumentsDefinitius(contingut) && !(contingut instanceof DocumentEntity && ((DocumentEntity) contingut).getGesDocAdjuntId() != null)) {

			// Propaga l'acció a l'arxiu
			FitxerDto fitxer = null;

			if (contingut instanceof ExpedientEntity) {
				contingutHelper.arxiuPropagarModificacio((ExpedientEntity) contingut);
			} else if (contingut instanceof DocumentEntity) {
				
				DocumentEntity document = (DocumentEntity)contingut;
				if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {

					DocumentFirmaTipusEnumDto documentFirmaTipus = document.getDocumentFirmaTipus();
					List<ArxiuFirmaDto> firmes = null;
					
					if (documentFirmaTipus == DocumentFirmaTipusEnumDto.SENSE_FIRMA) {
						fitxer = contingutHelper.fitxerDocumentEsborratLlegir(document);
					} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
						
						fitxer = contingutHelper.fitxerDocumentEsborratLlegir(document);
						firmes = documentHelper.validaFirmaDocument(
								document, 
								fitxer,
								null, 
								false, 
								true);
						
					} else if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
						
						fitxer = contingutHelper.fitxerDocumentEsborratLlegir(document);
						byte[] firmaContingut = contingutHelper.firmaSeparadaEsborratLlegir(document);
						firmes = documentHelper.validaFirmaDocument(
								document, 
								fitxer,
								firmaContingut, 
								false, 
								true);
					} 
					
					ArxiuEstatEnumDto arxiuEstat = documentHelper.getArxiuEstat(documentFirmaTipus, null);
					
					if (arxiuEstat == ArxiuEstatEnumDto.ESBORRANY && documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
						pluginHelper.arxiuPropagarFirmaSeparada(
								document,
								firmes.get(0).getFitxer());
					}
					
					if (firmes == null && Utils.isEmpty(fitxer.getContingut())) {
						throw new ValidationException("No es pot recuperar el document perquè no conté el contingut");
					}
					
					contingutHelper.arxiuPropagarModificacio(
							document,
							fitxer,
							arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : documentFirmaTipus,
							firmes,
							arxiuEstat);
				}

			} else if (contingut instanceof CarpetaEntity) {
				contingutHelper.arxiuPropagarModificacio((CarpetaEntity) contingut);
			}

			if (fitxer != null) {
				contingutHelper.fitxerDocumentEsborratEsborrar((DocumentEntity)contingut);
			}
		}
	}
	
	@Transactional
	@Override
	public boolean isDeleted(Long contingutId) {
		ContingutEntity contingut = contingutRepository.getOne(contingutId);
		return contingut.getEsborrat() != 0;
	}

	@Transactional
	@Override
	public void move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId, 
			String rolActual) {
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingutOrigenId));
		logger.debug("Movent el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutOrigenId=" + contingutOrigenId + ", "
				+ "contingutDestiId=" + contingutDestiId + ")");
		
		contingutHelper.move(entitatId, contingutOrigenId, contingutDestiId, rolActual);
	}

	@Transactional
	@Override
	public ContingutDto copy(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		logger.debug("Copiant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutOrigenId=" + contingutOrigenId + ", "
				+ "contingutDestiId=" + contingutDestiId + ", "
				+ "recursiu=" + recursiu + ")");
		return contingutHelper.copy(entitatId, contingutOrigenId, contingutDestiId, recursiu);
	}

	@Transactional
	@Override
	public Long link(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		logger.debug("Vinculant el contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutOrigenId=" + contingutOrigenId + ", "
				+ "contingutDestiId=" + contingutDestiId + ", "
				+ "recursiu=" + recursiu + ")");
		return contingutHelper.link(entitatId, contingutOrigenId, contingutDestiId, recursiu);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long getPareId(Long contingutId) {
		ContingutEntity contingut = contingutRepository.getOne(contingutId);
		return contingut.getPare() != null ? contingut.getPare().getId() : null;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long getExpedientId(Long contingutId) {
		ContingutEntity contingut = contingutRepository.getOne(contingutId);
		return contingut.getExpedientPare().getId();
	}	

	@Transactional(readOnly = true)
	@Override
	public List<ContingutDto> getFillsBasicInfo(
			Long contingutId) {

		List<ContingutDto> contingutsDto = new ArrayList<>();

		List<ContingutEntity> continguts = contingutRepository.findByPareIdAndEsborrat(contingutId, 0);

		if (Utils.isNotEmpty(continguts)) {

			for (ContingutEntity contingut : continguts) {
				contingutsDto.add(contingutHelper.getBasicInfo(contingut));
			}
		}
		return contingutsDto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ContingutDto getBasicInfo(Long contingutId, boolean checkPermissions)  {
		
		ContingutEntity contingut = contingutRepository.getOne(contingutId);
		if (checkPermissions) {
			contingutHelper.comprovarContingutDinsExpedientAccessible(
					contingut.getEntitat().getId(),
					contingutId,
					true,
					false);
		} 

		return contingutHelper.getBasicInfo(contingut);
	}
	
	
	@Override
	public ContingutDto findAmbIdUserPerMoureCopiarVincular(Long entitatId, Long contingutId) throws NotFoundException {
		long t0 = System.currentTimeMillis();
		logger.debug("Obtenint contingut amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=onlyCarpetes, "
				+ "ambVersions=false)");
		ContingutEntity contingut = contingutRepository.getOne(contingutId);

		if (cacheHelper.mostrarLogsRendiment())
			logger.info("findAmbIdUserPerMoureCopiarVincular time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t0) + " ms");
		
		return contingutHelper.toContingutDtoSimplificat(contingut, true, null);
	}
	

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			boolean ambPermisos, 
			String rolActual, 
			Long organActualId) {
		return findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions,
				ambPermisos,
				rolActual,
				true,
				false, 
				false);
	}

	

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions,
			boolean ambPermisos, 
			String rolActual, 
			boolean ambEntitat,
			boolean ambMapPerTipusDocument, 
			boolean ambMapPerEstat) {
		
		long t2 = System.currentTimeMillis();
		logger.debug("Obtenint contingut amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ", "
				+ "ambVersions=" + ambVersions + ")");
		ContingutEntity contingut;
		if (ambPermisos) {
			contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
					entitatId,
					contingutId,
					true,
					false);
		} else {
			contingut = contingutRepository.getOne(contingutId);
		}
		// ** #979 -> Es comprova cada camp dins documentEntity
		// Comprovar si hi ha notificacions del document
//		for (ContingutEntity document: contingut.getFills()) {
//			if (document instanceof DocumentEntity) {
//				List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);
//				List<DocumentPortafirmesEntity> enviaments = documentPortafirmesRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);
//				if (notificacions != null && notificacions.size() > 0) {
//					document.setAmbNotificacions(true);
//					DocumentNotificacioEntity lastNofificacio = notificacions.get(0);
//					document.setEstatDarreraNotificacio(lastNofificacio.getNotificacioEstat() != null ? lastNofificacio.getNotificacioEstat().name() : "");
//					document.setErrorDarreraNotificacio(lastNofificacio.isError());
//				}
//				if (enviaments != null && enviaments.size() > 0) {
//					DocumentEnviamentEntity lastEnviament = enviaments.get(0);
//					document.setErrorEnviamentPortafirmes(lastEnviament.isError());
//				}
//			}
//		}
//		Long t0 = System.currentTimeMillis();
		ContingutDto dto = contingutHelper.toContingutDto(
				contingut,
				ambPermisos,
				ambFills,
				true,
				true,
				true,
				ambVersions,
				rolActual,
				false,
				null,
				isMantenirEstatCarpetaActiu(),
				0,
				null,
				null,
				true,
				ambEntitat,
				ambMapPerTipusDocument,
				ambMapPerEstat);
		dto.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				dto.getId()) > 0);

		if (cacheHelper.mostrarLogsRendiment())
			logger.info("findAmbIdUser time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public void checkIfPermitted(
			Long contingutId,
			String rolActual, 
			PermissionEnumDto permission) {
		
		contingutHelper.checkIfPermitted(
				contingutId,
				rolActual,
				permission);
		
	}
	
	@Transactional(readOnly = true)
	@Override
	public boolean isExpedient(Long contingutId) {
		ContingutEntity contingut = contingutRepository.findById(contingutId).get();
		return ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus());
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint contingut amb id per admin ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		return contingutHelper.toContingutDto(
				contingut,
				false, 
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ValidacioErrorDto> findErrorsValidacio(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint errors de validació del contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		return cacheHelper.findErrorsValidacioPerNode(node);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AlertaDto> findAlertes(
			Long entitatId,
			Long contingutId) throws NotFoundException {
		logger.debug("Obtenint alertes associades al contingut ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ")");
		contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		List<AlertaEntity> alertes = alertaRepository.findByLlegidaAndContingutId(
				false,
				contingutId,
				Sort.by(Sort.Direction.DESC, "createdDate"));
		List<AlertaDto> resposta = conversioTipusHelper.convertirList(
				alertes,
				AlertaDto.class);
		for (int i = 0; i < alertes.size(); i++) {
			resposta.get(i).setCreatedDate(
					Date.from(alertes.get(i).getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		return contingutLogHelper.findLogsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		return contingutLogHelper.findLogsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId);
		return contingutLogHelper.findLogDetalls(
				contingutId,
				contingutLogId);
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
//		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
//				entitatId,
//				contingutId,
//				true,
//				false);
		return contingutLogHelper.findLogDetalls(
				contingutId,
				contingutLogId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre de moviments pel contingut usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false, false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		return contingutLogHelper.findMovimentsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre de moviments pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		return contingutLogHelper.findMovimentsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public ResultDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		logger.debug("Consulta de continguts per usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);
		MetaNodeEntity metaNode = null;
		if (filtre.getMetaNodeId() != null) {
			metaNode = metaNodeRepository.findById(filtre.getMetaNodeId()).orElse(null);
			if (metaNode == null) {
				throw new NotFoundException(
						filtre.getMetaNodeId(),
						MetaNodeEntity.class);
			}
		}
		boolean tipusCarpeta = true;
		boolean tipusDocument = true;
		boolean tipusExpedient = true;
		if (filtre.getTipus() != null) {
			switch (filtre.getTipus()) {
			case CARPETA:
				tipusCarpeta = true;
				tipusDocument = false;
				tipusExpedient = false;
				break;
			case DOCUMENT:
				tipusCarpeta = false;
				tipusDocument = true;
				tipusExpedient = false;
				break;
			case EXPEDIENT:
				tipusCarpeta = false;
				tipusDocument = false;
				tipusExpedient = true;
				break;
			case REGISTRE:
				tipusCarpeta = false;
				tipusDocument = false;
				tipusExpedient = false;
				break;
			}
		}
		Date dataCreacioInici = DateHelper.toDateInicialDia(filtre.getDataCreacioInici());
		Date dataCreacioFi = DateHelper.toDateFinalDia(filtre.getDataCreacioFi());
		
		Date dataEsborratInici = DateHelper.toDateInicialDia(filtre.getDataEsborratInici());
		Date dataEsborratFi = DateHelper.toDateFinalDia(filtre.getDataEsborratFi());
		
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = expedientRepository.findById(filtre.getExpedientId()).orElse(null);
			if (expedient == null) {
				throw new NotFoundException(
						filtre.getExpedientId(),
						ExpedientEntity.class);
			}
		}
		
		
		ResultDto<ContingutDto> result = new ResultDto<ContingutDto>();
		
		if (resultEnum == ResultEnumDto.PAGE) {
		
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
			
			
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
			PaginaDto<ContingutDto> paginaDto = paginacioHelper.toPaginaDto(
					contingutRepository.findByFiltrePaginat(
							entitat,
							tipusCarpeta,
							tipusDocument,
							tipusExpedient,
							(filtre.getNom() == null),
							filtre.getNom() != null ? filtre.getNom().trim() : "",
							(filtre.getCreador() == null), filtre.getCreador() != null ?
							filtre.getCreador().trim() : "",
							(metaNode == null),
							metaNode,
							(dataCreacioInici == null),
							DateUtil.getLocalDateTimeFromDate(dataCreacioInici, true, false),
							(dataCreacioFi == null),
							DateUtil.getLocalDateTimeFromDate(dataCreacioFi, false, true),
							(dataEsborratInici == null),
							dataEsborratInici,
							(dataEsborratFi == null),
							dataEsborratFi,
							filtre.isMostrarEsborrats(),
							filtre.isMostrarNoEsborrats(),
							(expedient == null),
							expedient,
							paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap)),
					ContingutDto.class,
					new Converter<ContingutEntity, ContingutDto>() {
						@Override
						public ContingutDto convert(ContingutEntity source) {
							return contingutHelper.toContingutDto(
									source,
									true, 
									false);
						}
					});
	
			result.setPagina(paginaDto);
			
		} else {
			
			// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
			List<Long> ids = contingutRepository.findIdsByFiltre(
					entitat,
					tipusCarpeta,
					tipusDocument,
					tipusExpedient,
					(filtre.getNom() == null),
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					(filtre.getCreador() == null),
					filtre.getCreador() != null ? filtre.getCreador().trim() : "",
					(metaNode == null),
					metaNode,
					(dataCreacioInici == null),
					DateUtil.getLocalDateTimeFromDate(dataCreacioInici, true, false),
					(dataCreacioFi == null),
					DateUtil.getLocalDateTimeFromDate(dataCreacioFi, false, true),
					(dataEsborratInici == null),
					dataEsborratInici,
					(dataEsborratFi == null),
					dataEsborratFi,
					filtre.isMostrarEsborrats(),
					filtre.isMostrarNoEsborrats(),
					(expedient == null),
					expedient);	
			
			result.setIds(ids);
		}

		return result;
		
	}

	@SuppressWarnings("incomplete-switch")
	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(
			Long entitatId,
			Long contingutId) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingutId));
		logger.debug("Obtenint informació de l'arxiu pel contingut (entitatId=" + entitatId + ", contingutId=" + contingutId + ")");
		//Comprovar contingut ja comprova també entitat
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(entitatId, contingutId, true, false);
		EntitatEntity entitat = entitatRepository.findById(entitatId).get();
		List<ContingutArxiu> continguts = null;
		List<Firma> firmes = null;
		ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
		
		// ##################### EXPEDIENT ##################################
		if (contingut instanceof ExpedientEntity) {
			es.caib.plugins.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientConsultar(
					(ExpedientEntity)contingut);
			continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
						case OBERT:
							arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
							break;
						case TANCAT:
							arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
							break;
						case INDEX_REMISSIO:
							break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(getOrgansAmbNoms(entitatId, metadades.getOrgans()));
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());		
			}
			
		// ##################### DOCUMENT ##################################
		} else if (contingut instanceof DocumentEntity) {
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					(DocumentEntity) contingut,
					null,
					null,
					true);
			firmes = arxiuDocument.getFirmes();
			arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
			arxiuDetall.setNom(arxiuDocument.getNom());
			DocumentMetadades metadades = arxiuDocument.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());
				
				arxiuDetall.setEniOrigen(ArxiuConversions.getOrigen(metadades.getOrigen()));

				arxiuDetall.setEniEstatElaboracio(ArxiuConversions.getEstatElaboracio(metadades.getEstatElaboracio()));
				
				if (metadades.getTipusDocumental() != null) {
					List<TipusDocumentalEntity> tipos = tipusDocumentalRepository.findByCodi(metadades.getTipusDocumental().toString());
					if (Utils.isNotEmpty(tipos)) {
						TipusDocumentalDto tipus = conversioTipusHelper.convertir(tipos.get(0), TipusDocumentalDto.class);
						arxiuDetall.setEniTipusDocumental(tipus.getCodiNom());
					} else {
						arxiuDetall.setEniTipusDocumental(metadades.getTipusDocumental().toString());
					}
				}

				if (metadades.getTipusDocumental() == null && metadades.getTipusDocumentalAddicional() != null) {
					logger.info("Tipus documental addicional: " + metadades.getTipusDocumentalAddicional());
					TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
							metadades.getTipusDocumentalAddicional(),
							entitat);

					if (tipusDocumental != null) {
						arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
					} else {
						List<TipusDocumentalDto> docsAddicionals = pluginHelper.documentTipusAddicionals();
						
						for (TipusDocumentalDto docAddicional : docsAddicionals) {
							if (docAddicional.getCodi().equals(metadades.getTipusDocumentalAddicional())) {
								arxiuDetall.setEniTipusDocumentalAddicional(docAddicional.getNom());
							}
						}
					}

					arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
				}

				arxiuDetall.setEniOrgans(getOrgansAmbNoms(entitatId, metadades.getOrgans()));
				if (metadades.getFormat() != null) {
					arxiuDetall.setEniFormat(metadades.getFormat().toString());
				}
				arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());
				
				final String fechaSelladoKey = "eni:fecha_sellado";
				Map<String, Object> metadadasAddicionals = metadades.getMetadadesAddicionals();
				if (metadadasAddicionals != null && metadadasAddicionals.containsKey(fechaSelladoKey)) {
					try {
						DateFormat dfIn= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
						DateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						Date fechaSelladoValor = dfIn.parse(metadadasAddicionals.get(fechaSelladoKey).toString());
						String fechaSelladoValorStr = dfOut.format(fechaSelladoValor);
						metadadasAddicionals.put(fechaSelladoKey, fechaSelladoValorStr);
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}		
				}
				arxiuDetall.setMetadadesAddicionals(metadadasAddicionals);
				
				if (arxiuDocument.getContingut() != null) {
					arxiuDetall.setContingutArxiuNom(
							arxiuDocument.getContingut().getArxiuNom());
					arxiuDetall.setContingutTipusMime(
							arxiuDocument.getContingut().getTipusMime());
				}

			}
			if (arxiuDocument.getEstat() != null) {
				if (DocumentEstat.ESBORRANY.equals(arxiuDocument.getEstat()))
					arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.ESBORRANY);
				else if (DocumentEstat.DEFINITIU.equals(arxiuDocument.getEstat()))
					arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
			}
			
		// ##################### CARPETA ##################################
		} else if (contingut instanceof CarpetaEntity) {
			Carpeta arxiuCarpeta = pluginHelper.arxiuCarpetaConsultar(
					(CarpetaEntity)contingut);
			continguts = arxiuCarpeta.getContinguts();
			arxiuDetall.setIdentificador(arxiuCarpeta.getIdentificador());
			arxiuDetall.setNom(arxiuCarpeta.getNom());
		} else {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"Tipus de contingut desconegut: " + contingut.getClass().getName());
		}
		
		// ##################### CONTINGUT ##################################
		if (continguts != null) {
			List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
			for (ContingutArxiu cont: continguts) {
				ArxiuContingutDto detallFill = new ArxiuContingutDto();
				detallFill.setIdentificador(
						cont.getIdentificador());
				detallFill.setNom(
						cont.getNom());
				if (cont.getTipus() != null) {
					switch (cont.getTipus()) {
					case EXPEDIENT:
						detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
						break;
					case DOCUMENT:
						detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
						break;
					case CARPETA:
						detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
						break;
					}
				}
				detallFills.add(detallFill);
			}
			arxiuDetall.setFills(detallFills);
		}
		if (firmes != null) {
			List<ArxiuFirmaDto> dtos = new ArrayList<ArxiuFirmaDto>();
			for (Firma firma: firmes) {
				ArxiuFirmaDto dto = new ArxiuFirmaDto();
				
				if (firma.getTipus() != null) {
					switch (firma.getTipus()) {
					case CSV:
						dto.setTipus(ArxiuFirmaTipusEnumDto.CSV);
						break;
					case XADES_DET:
						dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
						break;
					case XADES_ENV:
						dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
						break;
					case CADES_DET:
						dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
						break;
					case CADES_ATT:
						dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
						break;
					case PADES:
						dto.setTipus(ArxiuFirmaTipusEnumDto.PADES);
						break;
					case SMIME:
						dto.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
						break;
					case ODT:
						dto.setTipus(ArxiuFirmaTipusEnumDto.ODT);
						break;
					case OOXML:
						dto.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
						break;
					}
				}
				if (firma.getPerfil() != null) {
					switch (firma.getPerfil()) {
					case BES:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
						break;
					case EPES:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
						break;
					case LTV:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTV);
						break;
					case T:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.T);
						break;
					case C:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.C);
						break;
					case X:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.X);
						break;
					case XL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.XL);
						break;
					case A:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.A);
						break;
					case BASIC:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASIC);
						break;
					case Basic:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.Basic);
						break;
					case BASELINE_B_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_B_LEVEL);
						break;
					case BASELINE_LTA_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LTA_LEVEL);
						break;
					case BASELINE_LT_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LT_LEVEL);
						break;
					case BASELINE_T:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T);
						break;
					case BASELINE_T_LEVEL:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T_LEVEL);
						break;
					case LTA:
						dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTA);
						break;	
					}
				}
				dto.setFitxerNom(firma.getFitxerNom());
				if (ArxiuFirmaTipusEnumDto.CSV.equals(dto.getTipus())) {
					dto.setContingut(firma.getContingut());
				}
				dto.setTipusMime(firma.getTipusMime());
				dto.setCsvRegulacio(firma.getCsvRegulacio());
				dtos.add(dto);
			}
			arxiuDetall.setFirmes(dtos);
		}
		return arxiuDetall;
	}

	@Transactional
	@Override
	public List<CodiValorDto> sincronitzarEstatArxiu(Long entitatId, Long contingutId) {
		return contingutHelper.sincronitzarEstatArxiu(entitatId, contingutId);
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacioEni(
			Long entitatId,
			Long contingutId) {
		logger.debug("Exportant document a format ENI (" +
				"entitatId=" + entitatId + ", " +
				"contingutId=" + contingutId + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				contingutId,
				true,
				false);
		String exportacio;
		if (contingut instanceof ExpedientEntity) {
			exportacio = pluginHelper.arxiuExpedientExportar(
					(ExpedientEntity)contingut);
		} else if (contingut instanceof DocumentEntity) {
			exportacio = pluginHelper.arxiuDocumentExportar(
					(DocumentEntity)contingut);
		} else {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"El contingut a exportar ha de ser un expedient o un document");
		}
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom("exportacio_ENI.xml");
		fitxer.setContentType("application/xml");
		fitxer.setContingut(exportacio.getBytes());
		return fitxer;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DocumentDto> findDocumentsMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);


		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		if (!metaExpedientsPermesos.isEmpty()) {
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
			ordenacioMap.put("metaDocument.nom", new String[] {"metaNode.nom"});
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerFirmaMassiu(
					entitat,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getDataInici() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataInici(), true, false),
					filtre.getDataFi() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataFi(), false, true),
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					DocumentDto.class,
					new Converter<DocumentEntity, DocumentDto>() {
						@Override
						public DocumentDto convert(DocumentEntity source) {
							DocumentDto dto = (DocumentDto)contingutHelper.toContingutDto(
									source,
									true,
									true);
							return dto;
						}
					});
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					DocumentDto.class);
		}
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutMassiuDto> findDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, 
				false);


		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdByCodiAndNom", new String[] {"createdBy.nom"});
			ordenacioMap.put("tipusDocumentNom", new String[] {"metaNode.nom"});
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerFirmaMassiu(
					entitat,
					Utils.getNullIfEmpty(metaExpedientsPermesos), 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getDataInici() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataInici(), true, false),
					filtre.getDataFi() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataFi(), false, true),
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					ContingutMassiuDto.class,
					new Converter<DocumentEntity, ContingutMassiuDto>() {
						@Override
						public ContingutMassiuDto convert(DocumentEntity source) {
							
							ContingutMassiuDto dto = new ContingutMassiuDto();
							dto.setId(source.getId());
							dto.setNom(source.getNom());
							dto.setTipusDocumentNom(source.getMetaDocument() != null ? source.getMetaDocument().getNom() : null);
							dto.setExpedientId(source.getExpedient().getId());
							dto.setExpedientNumeroNom(source.getExpedient().getNumeroINom());
							dto.setCreatedDate(Date.from(source.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()));
							UsuariEntity organAdminCreador = usuariRepository.findById(source.getCreatedBy().get()).get();
							dto.setCreatedByCodiAndNom(organAdminCreador.getCodiAndNom());
							return dto;
						}
					});
		
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);

		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		if (!metaExpedientsPermesos.isEmpty()) {
			List<Long> idsDocuments = documentRepository.findIdsDocumentsPerFirmaMassiu(
					entitat,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getDataInici() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataInici(), true, false),
					filtre.getDataFi() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataFi(), false, true));
			return idsDocuments;
		} else {
			return new ArrayList<>();
		}
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public ResultDto<ContingutMassiuDto> findDocumentsPerFirmaSimpleWebMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum) throws NotFoundException {

		ResultDto<ContingutMassiuDto> result = new ResultDto<ContingutMassiuDto>();

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true,
				false);

		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);

		if (resultEnum == ResultEnumDto.PAGE) {

			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdByCodiAndNom", new String[] { "createdBy.nom" });
			ordenacioMap.put("tipusDocumentNom", new String[] { "metaNode.nom" });
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerFirmaSimpleWebMassiu(
					entitat,
					Utils.getNullIfEmpty(metaExpedientsPermesos),
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					Utils.getEmptyStringIfNull(filtre.getNom()),
					filtre.getDataInici() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataInici(), true, false),
					filtre.getDataFi() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataFi(), false, true),
					paginacioHelper.toSpringDataPageable(
							paginacioParams,
							ordenacioMap));

			PaginaDto<ContingutMassiuDto> paginaDto = paginacioHelper.toPaginaDto(
					paginaDocuments,
					ContingutMassiuDto.class,
					new Converter<DocumentEntity, ContingutMassiuDto>() {
						@Override
						public ContingutMassiuDto convert(DocumentEntity source) {

							ContingutMassiuDto dto = new ContingutMassiuDto();
							dto.setId(source.getId());
							dto.setNom(source.getNom());
							dto.setTipusDocumentNom(source.getMetaDocument() != null ? source.getMetaDocument().getNom() : null);
							dto.setExpedientId(source.getExpedient().getId());
							dto.setExpedientNumeroNom(source.getExpedient().getNumeroINom());
							dto.setCreatedDate(Date.from(source.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()));
							UsuariEntity organAdminCreador = usuariRepository.findById(source.getCreatedBy().get()).get();
							dto.setCreatedByCodiAndNom(organAdminCreador.getCodiAndNom());
							return dto;
						}
					});
			result.setPagina(paginaDto);

		} else {

			List<Long> ids = documentRepository.findIdsDocumentsPerFirmaSimpleWebMassiu(
					entitat,
					Utils.getNullIfEmpty(metaExpedientsPermesos),
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					Utils.getEmptyStringIfNull(filtre.getNom()),
					filtre.getDataInici() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataInici(), true, false),
					filtre.getDataFi() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataFi(), false, true));

			result.setIds(ids);
		}

		return result;
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DocumentDto> findDocumentsPerCopiarCsv(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);


		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		if (!metaExpedientsPermesos.isEmpty()) {
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
			ordenacioMap.put("metaDocument.nom", new String[] {"metaNode.nom"});
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerCopiarCsv(
					entitat,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getDataInici() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataInici(), true, false),
					filtre.getDataFi() == null,
					DateUtil.getLocalDateTimeFromDate(filtre.getDataFi(), false, true),
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					DocumentDto.class,
					new Converter<DocumentEntity, DocumentDto>() {
						@Override
						public DocumentDto convert(DocumentEntity source) {
							DocumentDto dto = (DocumentDto)contingutHelper.toContingutDto(
									source,
									true,
									true);
							return dto;
						}
					});
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					DocumentDto.class);
		}
	}

	@Transactional
	@Override
	public void order(
			Long entitatId, 
			Long contingutId,
			Map<Integer, Long> orderedElements)
			throws NotFoundException, ValidationException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false,
				false, 
				false);
		contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				true,
				false,
				false,
				false, true, null);
		for (Map.Entry<Integer, Long> fill: orderedElements.entrySet()) {
			Integer ordre = fill.getKey();
			Long fillId = fill.getValue();
			
			ContingutEntity contingut = entityComprovarHelper.comprovarContingut(fillId);
			contingut.updateOrdre(ordre);
		}
	}

	@Override
	@Transactional
//	@Scheduled(cron = "0 0 5 * * ?")
	public Boolean netejaContingutsOrfes() {
		try {
			contingutRepositoryHelper.deleteContingutsOrfes();
			return true;
		} catch (Exception ex) {
			logger.error("No s'han pogut eliminar els continguts orfes.", ex);
		}
		return false;
	}

    @Override
    public ResultDocumentsSenseContingut arreglaDocumentsSenseContingut() {
		ResultDocumentsSenseContingut result = ResultDocumentsSenseContingut.builder().build();

//		List<Long> idsAnnexEsborranysAmbDocument = registreAnnexRepository.findIdsEsborranysAmbDocument();
		List<Long> idsAnnexEsborranysAmbDocument = Arrays.asList(2124706L, 2124707L, 2124767L, 2124768L, 2124771L, 2124776L, 2124783L, 2124784L, 2130635L, 2130636L, 2130637L, 2130638L, 2130639L, 2121164L, 2106239L, 2125312L, 2121243L, 2121256L, 2121263L, 2121343L, 2121345L, 2121348L, 2121361L, 2130992L, 2131025L, 2121380L, 2122778L, 2122779L, 2122821L, 2122837L, 2121395L, 2122311L, 2122312L, 2122313L, 2150632L);

		logger.info("[DOCS_SENSE_CONT] Detectats {} esborranys amb document associat.", idsAnnexEsborranysAmbDocument.size());
		for (Long annexId : idsAnnexEsborranysAmbDocument) {
			logger.info("[DOCS_SENSE_CONT] Processant annex: {}", annexId);
			ResultDocumentSenseContingut resultat = contingutHelper.arreglaDocumentSenseContingut(annexId);
			result.addResultDocument(resultat);
			logger.info("[DOCS_SENSE_CONT] Resultat del procés: {}", resultat.toString());
		}

		return result;
    }

    public boolean isMantenirEstatCarpetaActiu() {
		return configHelper.getAsBoolean(PropertyConfig.MANTENIR_ESTAT_CARPETA);
	}
    
	public boolean isPropagarMetadadesActiu() {
		return configHelper.getAsBoolean(PropertyConfig.PROPAGAR_METADADES);
	}
	
	private String buildIdsDominiString(String valorDomini, Long entitatId, DominiDto domini) {
	    if (valorDomini == null || valorDomini.isEmpty()) {
	        return "";
	    }
	    List<String> valorDominiArr = Arrays.asList(valorDomini.split(","));
	    StringBuilder idsDomini = new StringBuilder();
	    for (int i = 0; i < valorDominiArr.size(); i++) {
			ResultatConsultaDto resultat = dominiService.getSelectedDomini(entitatId, domini, valorDominiArr.get(i));
				if (resultat != null) {
				idsDomini.append(resultat.getId());
				if (i < valorDominiArr.size() - 1) {
					idsDomini.append(",");
	            }
			}
		}
	    return idsDomini.toString();
	}
	
	private List<String> getOrgansAmbNoms(Long entitatId, List<String> organsCodis) {
		List<String> organsCodisNoms = new ArrayList<>();
		if (Utils.isNotEmpty(organsCodis)) {
			for (String organCodi : organsCodis) {
				OrganGestorEntity organ = organGestorRepository.findByEntitatIdAndCodi(entitatId, organCodi);
				if (organ != null) {
					organsCodisNoms.add(organ.getCodiINom());
				} else {
					organsCodisNoms.add(organCodi);
				}
			}
		}

		return organsCodisNoms;
	}
	
    private static final Logger logger = LoggerFactory.getLogger(ContingutServiceImpl.class);

	@Override
	@Transactional(readOnly = true)
	public UsuariDto findUsuariCreacio(Long contingutId) {
		ContingutEntity ce = contingutRepository.findById(contingutId).get();
		if (ce!=null && ce.getCreatedBy()!=null) {
			return conversioTipusHelper.convertir(usuariRepository.findByCodi(ce.getCreatedBy().get()), UsuariDto.class);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public String findNtiCsvByDocumentId(Long entitatId, Long documentId) throws NotFoundException {
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(documentId);
		if (contingut!=null) return ((DocumentEntity)contingut).getNtiCsv();
		return null;
	}
}