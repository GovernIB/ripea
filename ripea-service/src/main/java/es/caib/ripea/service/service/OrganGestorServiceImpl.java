package es.caib.ripea.service.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.plugin.unitat.NodeDir3;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.IntegracioHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.RolHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.dto.ActualitzacioInfo;
import es.caib.ripea.service.intf.dto.ArbreDto;
import es.caib.ripea.service.intf.dto.ArbreNodeDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.OrganGestorFiltreDto;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PermisOrganGestorDto;
import es.caib.ripea.service.intf.dto.PrediccioSincronitzacio;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.service.OrganGestorService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;

@Service
public class OrganGestorServiceImpl implements OrganGestorService {

	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private ConfigHelper configHelper;
	
	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<>();

	@Override
	public void actualitzarOrganCodi(String organCodi) {
		organGestorHelper.actualitzarOrganCodi(organCodi);
	}

	@Override
	public String getOrganCodi() {
		return configHelper.getOrganActualCodi();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
		List<OrganGestorEntity> organs = organGestorRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
		return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findById(Long entitatId, Long id) {
		logger.debug("Consulta del organ gestor (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		OrganGestorEntity organGestor = entityComprovarHelper.comprovarPermisOrganGestor(entitatId, id, false);
		OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		resposta.setPareId(organGestor.getPare() != null ? organGestor.getPare().getId() : null);
		return resposta;
	}
	
	@Transactional
	@Override
	public OrganGestorDto create(Long entitatId, OrganGestorDto organGestorDto) {
		logger.debug(
				"Creant un nou organ (" + "entitatId=" + entitatId + ", " + "organGestor=" + organGestorDto +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		
		OrganGestorEntity organPareEntity = null;
		if (organGestorDto.getPareId() != null) {
			organPareEntity = organGestorRepository.getOne(organGestorDto.getPareId());
		}
		
		OrganGestorEntity entity = OrganGestorEntity.getBuilder(
				organGestorDto.getCodi()).
				nom(organGestorDto.getNom()).
				entitat(entitat).
				pare(organPareEntity).
				estat(OrganEstatEnumDto.V).
				cif(organGestorDto.getCif()).
				build();
		
		OrganGestorEntity organGestorEntity = organGestorRepository.save(entity);
		
		return conversioTipusHelper.convertir(organGestorEntity, OrganGestorDto.class);
	}

	@Transactional
	@Override
	public OrganGestorDto update(Long entitatId, OrganGestorDto organGestorDto) {
		logger.debug(
				"Actualitzant organ gestor existent (" + "entitatId=" + entitatId + ", " + "organGestorDto=" +
						organGestorDto + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		OrganGestorEntity organGestorEntity = entityComprovarHelper.comprovarPermisOrganGestor(entitatId, organGestorDto.getId(), false);
		
		organGestorEntity.update(
                organGestorDto.isUtilitzarCifPinbal(),
                organGestorDto.isPermetreEnviamentPostal(),
                organGestorDto.isPermetreEnviamentPostalDescendents()
        );

		return conversioTipusHelper.convertir(organGestorEntity, OrganGestorDto.class);
	}
	
	@Transactional
	@Override
	public String delete(Long entitatId, Long id) {
		logger.debug("Esborrant organ gestor (id=" + id + ")");
		OrganGestorEntity organGestor = entityComprovarHelper.comprovarPermisOrganGestor(entitatId, id, false);
		organGestorRepository.delete(organGestor);
		return organGestor.getNom();
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public OrganGestorDto findItem(Long id) {
		OrganGestorEntity organGestor = organGestorRepository.findById(id).orElse(null);
		if (organGestor == null) {
			throw new NotFoundException(id, OrganGestorEntity.class);
		}
		OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		return resposta;
	}
	
	@Override
	@Transactional(readOnly = true)
	public OrganGestorDto findItemByEntitatAndCodi(Long entitatId, String codi) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		OrganGestorEntity organGestor = organGestorRepository.findByEntitatAndCodi(entitat, codi);
		if (organGestor == null) {
			throw new NotFoundException(codi, OrganGestorEntity.class);
		}
		OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
		return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(
			Long entitatId,
			String filter) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, 
				false, 
				false);
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitatAndFiltre(
				entitat.getId(),
				filter == null || filter.isEmpty(),
				filter);
		return conversioTipusHelper.convertirList(
				organs,
				OrganGestorDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll(Long entitatId, String filter) {
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitatAndFiltre(
				entitatId,
				filter == null || filter.isEmpty(),
				filter != null ? filter : "");
		return conversioTipusHelper.convertirList(
				organs,
				OrganGestorDto.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object[] syncDir3OrgansGestors(EntitatDto entitatDto, Locale locale) throws Exception {
	    EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, true, false, false, false);
	    boolean primeraSync = entitat.getDataSincronitzacio() == null;
	    
		ConfigHelper.setEntitat(entitatDto);
		MessageHelper.setCurrentLocale(locale);
		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			throw new Exception(msg("unitat.synchronize.error.dir3"));
		}

		// Comprova si hi ha una altra instància del procés en execució
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitat.getCodi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			logger.debug("[ORGANS GESTORS] Ja existeix un altre procés que està executant l'actualització");
			return null;	// Ja existeix un altre procés que està executant l'actualització.
		}

		// inicialitza el seguiment del progrés d'actualització
		progres = new ProgresActualitzacioDto();
		progresActualitzacio.put(entitat.getCodi(), progres);

		progres.setNumOperacions(100);
		progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.actualitzar")).infoText(msg("unitat.synchronize.info.actualitzar.inici")).build());
		progres.setProgres(1);

		List<OrganGestorEntity> obsoleteUnitats = new ArrayList<>();
		List<OrganGestorEntity> organsDividits = new ArrayList<>();
		List<OrganGestorEntity> organsFusionats = new ArrayList<>();
		List<OrganGestorEntity> organsSubstituits = new ArrayList<>();

		try {
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.organigrama")).infoText(msg("unitat.synchronize.info.organigrama.inici")).build());
			List<UnitatOrganitzativa> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(
					entitat.getUnitatArrel(),
					entitat.getDataActualitzacio(),
					entitat.getDataSincronitzacio());
			progres.setProgres(2);
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.organigrama")).infoText(unitatsWs.isEmpty() ? msg("unitat.synchronize.info.organigrama.fi.buid") : msg("unitat.synchronize.info.organigrama.fi", unitatsWs.size())).build());

			// Sincronitzar òrgans
			progres.setFase(1); 
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.organs")).infoText(msg("unitat.synchronize.info.organs.inici")).build());
			organGestorHelper.sincronitzarOrgans(entitatDto.getId(), unitatsWs, obsoleteUnitats, organsDividits, organsFusionats, organsSubstituits, progres);
			progres.setProgres(27);
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.organs")).infoText(msg("unitat.synchronize.info.organs.fi")).build());

			// Actualitzar procediments
			progres.setFase(2);
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.procediments")).infoText(msg("unitat.synchronize.info.procediments.inici")).build());
			metaExpedientHelper.actualitzarProcediments(
					entitat,
					metaExpedientRepository.findByEntitatOrderByNomAsc(entitat),
					locale,
					progres);
			progres.setProgres(51);
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.procediments")).infoText(msg("unitat.synchronize.info.procediments.fi")).build());

			if (!primeraSync) {
				// Actualitzar permisos
				progres.setFase(3);
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.permisos")).infoText(msg("unitat.synchronize.info.permisos.inici")).build());
				permisosHelper.actualitzarPermisosOrgansObsolets(obsoleteUnitats, organsDividits, organsFusionats, organsSubstituits, progres);
				progres.setProgres(75);
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.permisos")).infoText(msg("unitat.synchronize.info.permisos.fi")).build());
				
				// Actualitzar expedients oberts
				progres.setFase(4);
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.expedients")).infoText(msg("unitat.synchronize.info.expedients.inici")).build());
				organGestorHelper.actualitzarExpedientsObertsAmbOrgansObsolets(
						ListUtils.union(organsSubstituits, organsFusionats),
						progres);
				progres.setProgres(99);
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.expedients")).infoText(msg("unitat.synchronize.info.expedients.fi")).build());

			}

//			// Eliminar organs no vigents no utilitzats??
//			progres.setFase(4);
//			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.obsolets")).infoText(msg("unitat.synchronize.info.obsolets.inici")).build());
//			organGestorHelper.deleteExtingitsNoUtilitzats(obsoleteUnitats, progres);
//			progres.setProgres(99);
//			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-warning").infoTitol(msg("unitat.synchronize.titol.obsolets")).infoText(msg("unitat.synchronize.info.obsolets.fi")).build());

			cacheHelper.evictUnitatsOrganitzativesPerEntitat(entitat.getCodi());
			cacheHelper.evictAllOrganismesEntitatAmbPermis();

			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoClass("panel-success").infoTitol(msg("unitat.synchronize.titol.actualitzar")).infoText(msg("unitat.synchronize.info.actualitzar.fi")).build());
		} catch (Exception ex) {
			progres.addInfo(ActualitzacioInfo.builder().hasError(true).infoTitol(msg("unitat.synchronize.titol.error")).errorText("S'ha produit un error al realitzar la sincronització dels òrgans gestors: " + ex.getMessage()).build());
			throw ex;
		} finally {
			progres.setProgres(100);
			progres.setFinished(true);
			MessageHelper.setCurrentLocale(null); //Ara el massages helper tornrà a afagar el locale del contexte
		}

		return new ArrayList[]{(ArrayList) obsoleteUnitats, (ArrayList) organsDividits, (ArrayList) organsFusionats, (ArrayList) organsSubstituits};
	}

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) throws Exception {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);

		boolean isFirstSincronization = entitat.getDataSincronitzacio() == null;
		List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();

		if (isFirstSincronization) {
			return PrediccioSincronitzacio.builder()
					.isFirstSincronization(isFirstSincronization)
					.unitatsVigents(predictFirstSynchronization(entitat))
					.build();
		}

		try {
			// Obtenir lista de canvis del servei web
			List<UnitatOrganitzativa> unitatsWS = pluginHelper.unitatsOrganitzativesFindByPare(
					entitat.getUnitatArrel(),
					entitat.getDataActualitzacio(),
					entitat.getDataSincronitzacio());
			
			if (unitatsWS == null || unitatsWS.isEmpty()) {
				return PrediccioSincronitzacio.builder()
						.noCanvis(true)
						.build();
			}

			// Obtenir els òrgans vigents a la BBDD
			List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(entitat.getId(), OrganEstatEnumDto.V);
			logger.debug("Consulta d'unitats vigents a DB");
			for (OrganGestorEntity organVigent : organsVigents) {
				logger.debug(organVigent.toString());
			}

			// Obtenir unitats actualment vigents en BBDD, però marcades com a obsoletes en la sincronització
			List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = getObsoletesFromWS(entitat, unitatsWS, organsVigents);
			List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();

			// Distinció entre divisió i (substitució o fusió)
			Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> splitMap 		= new HashMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto>();
			Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> mergeOrSubstMap = new HashMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto>();

			for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsoleteDto) {
				// Comprovam que no estigui extingida
				int transicionsVigents = 0;
				if (!vigentObsolete.getLastHistoricosUnitats().isEmpty()) {
					boolean extingit = true;
					for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
						if (OrganEstatEnumDto.V.name().equals(hist.getEstat())) {
							transicionsVigents++;
						}
					}
				}

				// En cas de no estar extingida comprovam el tipus de operació
//				if (vigentObsolete.getLastHistoricosUnitats().size() > 1) {
				// ====================  DIVISIONS ================
				if (transicionsVigents > 1) {
					for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
						splitMap.put(vigentObsolete, hist);
					}
//				} else if (vigentObsolete.getLastHistoricosUnitats().size() == 1) {
				// ====================  FUSIONS / SUBSTITUCIONS  ===================
				} else if (transicionsVigents == 1) {
					// check if the map already contains key with this codi
					UnitatOrganitzativaDto mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
					UnitatOrganitzativaDto keyWithTheSameCodi = null;
					Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
					for (UnitatOrganitzativaDto mergeOrSubstKeyMap : keysMergeOrSubst) {
						if (mergeOrSubstKeyMap.getCodi().equals(mergeOrSubstKeyWS.getCodi())) {
							keyWithTheSameCodi = mergeOrSubstKeyMap;
						}
					}
					// if it contains already key with the same codi, assign found key
					if (keyWithTheSameCodi != null) {
						mergeOrSubstMap.put(keyWithTheSameCodi, vigentObsolete);
					} else {
						mergeOrSubstMap.put(mergeOrSubstKeyWS, vigentObsolete);
					}
				// ====================  EXTINGINDES ===================
				} else if (transicionsVigents == 0) {
					unitatsExtingides.add(vigentObsolete);
				}
			}

			// Distinció entre substitució i fusió
			Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
			Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> mergeMap = new HashMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto>();
			Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> substMap = new HashMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto>();
			for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
				List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap.get(mergeOrSubstKey);

				// ==================== FUSIONS ===================
				if (values.size() > 1) {
					for (UnitatOrganitzativaDto value : values) {
						
						boolean isAlreadyAddedToMap = isAlreadyAddedToMap(mergeMap, mergeOrSubstKey, value);
						
						if (!isAlreadyAddedToMap) {
							mergeMap.put(mergeOrSubstKey, value);
						} else {
							//normally this shoudn't duplicate, it is added to deal with the result of call to WS DIR3 PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
							logger.info("Detected duplication of organs in prediction of fusion. Unitat" + value.getCodi() + "already added to fusion into " + mergeOrSubstKey.getCodi() + ". Probably caused by error in DIR3");
						}

					}
				// ==================== SUBSTITUCIONS ===================	
				} else {
					substMap.put(mergeOrSubstKey, values.get(0));
				}
			}

			// Obtenir llistat d'unitats que ara estan vigents en BBDD, i després de la sincronització continuen vigents, però amb les propietats canviades
			// ====================  CANVIS EN ATRIBUTS ===================
			unitatsVigents = getVigentsFromWebService(entitat, unitatsWS, organsVigents);

			// Obtenir el llistat d'unitats que son totalment noves (no existeixen en BBDD): Creació
			// ====================  NOUS ===================
			List<UnitatOrganitzativaDto> unitatsNew = getNewFromWS(entitat, unitatsWS, organsVigents);

			
			return PrediccioSincronitzacio.builder()
					.unitatsVigents(unitatsVigents)
					.unitatsNew(unitatsNew)
					.unitatsExtingides(unitatsExtingides)
					.splitMap(splitMap)
					.substMap(substMap)
					.mergeMap(mergeMap)
					.build();

		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					"No ha estat possible obtenir la predicció de canvis de unitats organitzatives",
					ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean isAlreadyAddedToMap(
			Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> mergeMap,
			UnitatOrganitzativaDto key,
			UnitatOrganitzativaDto value) {

		boolean contains = false;
		UnitatOrganitzativaDto unitat = (UnitatOrganitzativaDto) mergeMap.get(key);
		if (unitat != null) {
			if (unitat.getCodi().equals(value.getCodi())) {
				contains = true;
			}
		}

		return contains;
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String entitatCodi) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitatCodi);
		if (progres != null && progres.isFinished()) {
			progresActualitzacio.remove(entitatCodi);
		}
		return progres;
	}

	private List<UnitatOrganitzativaDto> predictFirstSynchronization(EntitatEntity entitat) throws SistemaExternException {

		List<UnitatOrganitzativa> unitatsVigentsWS = pluginHelper.unitatsOrganitzativesFindByPare(
				entitat.getUnitatArrel(),
				entitat.getDataActualitzacio(),
				entitat.getDataSincronitzacio());
		// converting form UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentWSDto = new ArrayList<>();
		for(UnitatOrganitzativa vigentObsolete : unitatsVigentsWS){
			unitatsVigentWSDto.add(conversioTipusHelper.convertir(
					vigentObsolete,
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentWSDto;
	}

	private List<UnitatOrganitzativaDto> getObsoletesFromWS(
			EntitatEntity entitat,
			List<UnitatOrganitzativa> unitatsWS,
			List<OrganGestorEntity> organsVigents) {

		// Llista d'òrgans obsolets des del servei web, que eren vignets a la última sincronització (vigent a BBDD i obsolet al servei web)
		// No obtenim la llista d'òrgans obsolets directament de BBDD degut a que hi pot haver canvis acumulats:
		// si a la darrere sincrocització la unitat A cavia a B, i després a C, llavors en la BBDD tindrem A(vigent) però des del servei web tindrem: A(Extingit) -> B(Extingit) -> C(Vigent)
		// Només volem retornar A (no volem B) perquè la predicció ha de mostrar la transició (A -> C) [entre A (vigent a BBDD) i C (vigent al servei web)]
		List<UnitatOrganitzativa> organsVigentObsolete = new ArrayList<>();
		for (OrganGestorEntity organVigent : organsVigents) {
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (organVigent.getCodi().equals(unitatWS.getCodi()) && !unitatWS.getEstat().equals("V")
						&& !organVigent.getCodi().equals(entitat.getUnitatArrel())) {
					organsVigentObsolete.add(unitatWS);
				}
			}
		}
		logger.debug("Consulta unitats obsolete ");
		for (UnitatOrganitzativa vigentObsolete : organsVigentObsolete) {
			logger.debug(vigentObsolete.getCodi()+" "+vigentObsolete.getEstat()+" "+vigentObsolete.getHistoricosUO());
		}
		for (UnitatOrganitzativa vigentObsolete : organsVigentObsolete) {

			// Fer que un òrgan obsolet apunti a l'últim òrgan/s al que ha fet la transició
			// El nom del camp historicosUO és totalment erroni, ja que el camp mostra unitats futures, no històric. Però així és com s'anomena al servei web, i no ho podem canviar.
			// El camp lastHistoricosUnitats hauria d'apuntar a la darrera unitat a la que ha fet la trasició. Necessitem trobar la darrera unitat de forma recursiva, perquè és possible que hi hagi canvis acumulats:
			// Si la darrera sincronització de la unitat A canvia a B, i després a C, des del servei web tindrés la unitat A apuntant a B (A -> B) i la unitat B apuntant a C (B -> C)
			// El que volem és afegir un punter directe des de la unitat A a la unitat C (A -> C)
			vigentObsolete.setLastHistoricosUnitats(getLastHistoricos(entitat,vigentObsolete, unitatsWS));
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = new ArrayList<>();
		for(UnitatOrganitzativa vigentObsolete : organsVigentObsolete){
			unitatsVigentObsoleteDto.add(conversioTipusHelper.convertir(
					vigentObsolete,
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentObsoleteDto;
	}

	// Obtenir unitats que no fan cap transició a cap altre unitat, però a la que se'ls canvia alguna propietat
	private List<UnitatOrganitzativaDto> getVigentsFromWebService(
			EntitatEntity entitat,
			List<UnitatOrganitzativa> unitatsWS,
			List<OrganGestorEntity> organsVigents){
		// list of vigent unitats from webservice
		List<UnitatOrganitzativa> unitatsVigentsWithChangedAttributes = new ArrayList<>();
		for (OrganGestorEntity unitatV : organsVigents) {
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
						&& (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty())
						&& !unitatV.getCodi().equals(entitat.getUnitatArrel())) {
					unitatsVigentsWithChangedAttributes.add(unitatWS);
				}
			}
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<>();
		for(UnitatOrganitzativa vigent : unitatsVigentsWithChangedAttributes){
			UnitatOrganitzativaDto unitatOrganitzativaDto = conversioTipusHelper.convertir(
					vigent,
					UnitatOrganitzativaDto.class);
			OrganGestorEntity org = organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), unitatOrganitzativaDto.getCodi());
			unitatOrganitzativaDto.setOldDenominacio(org.getNom());
			unitatsVigentsWithChangedAttributesDto.add(unitatOrganitzativaDto);
			
			
		}
		return unitatsVigentsWithChangedAttributesDto;
	}

	// Obtenir unitats organitzatives noves (No provenen de cap transició d'una altre unitat)
	private List<UnitatOrganitzativaDto> getNewFromWS(
			EntitatEntity entitat,
			List<UnitatOrganitzativa> unitatsWS,
			List<OrganGestorEntity> organsVigents){
		//List of new unitats that are vigent
		List<UnitatOrganitzativa> vigentUnitatsWS = new ArrayList<>();
		//List of new unitats that are vigent and does not exist in database
		List<UnitatOrganitzativa> vigentNotInDBUnitatsWS = new ArrayList<>();
		//List of new unitats (that are vigent, not pointed by any obsolete unitat and does not exist in database)
		List<UnitatOrganitzativa> newUnitatsWS = new ArrayList<>();
		//Filtering to only obtain vigents
		for (UnitatOrganitzativa unitatWS : unitatsWS) {
			if (unitatWS.getEstat().equals("V") && !unitatWS.getCodi().equals(entitat.getUnitatArrel())) {
				vigentUnitatsWS.add(unitatWS);
			}
		}
		// Filtering to only obtain vigents that does not already exist in database
		for (UnitatOrganitzativa vigentUnitat : vigentUnitatsWS) {
			boolean found = false;
			for (OrganGestorEntity vigentUnitatDB : organsVigents) {
				if (vigentUnitatDB.getCodi().equals(vigentUnitat.getCodi())) {
					found = true;
					break;
				}
			}
			if (found == false) {
				vigentNotInDBUnitatsWS.add(vigentUnitat);
			}
		}
		// Filtering to obtain unitats that are vigent, not pointed by any obsolete unitat and does not already exist in database
		for (UnitatOrganitzativa vigentNotInDBUnitatWS : vigentNotInDBUnitatsWS) {
			boolean pointed = false;
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if(unitatWS.getHistoricosUO()!=null){
					for(String novaCodi: unitatWS.getHistoricosUO()){
						if(novaCodi.equals(vigentNotInDBUnitatWS.getCodi())){
							pointed = true;
							break;
						}
					}
				}
				if (pointed) break;
			}
			if (pointed == false) {
				newUnitatsWS.add(vigentNotInDBUnitatWS);
			}
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> newUnitatsDto = new ArrayList<>();
		for (UnitatOrganitzativa vigent : newUnitatsWS){
			newUnitatsDto.add(conversioTipusHelper.convertir(
					vigent,
					UnitatOrganitzativaDto.class));
		}
		return newUnitatsDto;
	}

	// Retorna la/les unitat/s a la que un organ obsolet ha fet la transició
	// Inici de mètode recursiu
	private List<UnitatOrganitzativa> getLastHistoricos(
			EntitatEntity entitat,
			UnitatOrganitzativa unitat,
			List<UnitatOrganitzativa> unitatsFromWebService){

		List<UnitatOrganitzativa> lastHistorcos = new ArrayList<>();
		getLastHistoricosRecursive(
				entitat,
				unitat,
				unitatsFromWebService,
				lastHistorcos);
		return lastHistorcos;
	}

	private void getLastHistoricosRecursive(
			EntitatEntity entitat,
			UnitatOrganitzativa unitat,
			List<UnitatOrganitzativa> unitatsFromWebService,
			List<UnitatOrganitzativa> lastHistorics) {
		
		logger.info("Coloca historics recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");
		
		if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
			lastHistorics.add(unitat);
		} else {
			for (String historicCodi : unitat.getHistoricosUO()) {
				UnitatOrganitzativa unitatFromCodi = getUnitatFromCodi(historicCodi, unitatsFromWebService);
				if (unitatFromCodi == null) {
					// Looks for historico in database
					OrganGestorEntity entity = organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), historicCodi);
					if (entity != null) {
						UnitatOrganitzativa uo = conversioTipusHelper.convertir(entity, UnitatOrganitzativa.class);
						lastHistorics.add(uo);
					} else {
						String errorMissatge = "Error en la sincronització amb DIR3. La unitat orgánica (" + unitat.getCodi()
								+ ") té l'estat (" + unitat.getEstat() + ") i l'històrica (" + historicCodi
								+ ") però no s'ha retornat la unitat orgánica (" + historicCodi
								+ ") en el resultat de la consulta del WS ni en la BBDD.";
						throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
					}
				} else if (historicCodi.equals(unitat.getCodi())) {
					// EXAMPLE:
					//A04032359
					//-A04032359
					//-A04068486
					// if it is transitioning to itself don't add it as last historic
					//this probably shoudn't happen, it is added to deal with the result of call to WS made in PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
					logger.info("Detected organ division with transitioning to itself : " + historicCodi + ". Probably caused by error in DIR3");
				} else {
					getLastHistoricosRecursive(
							entitat,
							unitatFromCodi,
							unitatsFromWebService,
							lastHistorics);
				}
			}
		}
	}

	private UnitatOrganitzativa getUnitatFromCodi(
			String codi,
			List<UnitatOrganitzativa> allUnitats){

		for (UnitatOrganitzativa unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				return unitatWS;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId,
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		// Sempre afegirem el nom com a subordre, si no hi ha cap ordre dona error
		Utils.addSortDefault(paginacioParams, "nom");
		Page<OrganGestorEntity> organs = organGestorRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getCodi() == null || filtre.getCodi().isEmpty(),
				filtre.getCodi() != null ? filtre.getCodi().trim() : "",
				filtre.getNom() == null || filtre.getNom().isEmpty(),
				filtre.getNom() != null ? filtre.getNom().trim() : "",
				filtre.getPareId() == null,
				filtre.getPareId(),
				filtre.getEstat() == null,
				filtre.getEstat(),
				paginacioHelper.toSpringDataPageable(paginacioParams));
		PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(organs, OrganGestorDto.class);
		for (OrganGestorDto organ : paginaOrgans.getContingut()) {
			List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
			organ.setPermisos(permisos);
		}
		return paginaOrgans;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdminOrDisseny(Long entitatId, Long organGestorId) {
		return findAccessiblesUsuariActualRolAdminOrDisseny(entitatId, organGestorId, null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdminOrDisseny(Long entitatId, Long organGestorId, String filter) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAny(
				organGestorId,
				OrganGestorEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.DISSENY },
				auth)) {
			return new ArrayList<OrganGestorDto>();
		}
		OrganGestorEntity organGestor = organGestorRepository.getOne(organGestorId);
		List<OrganGestorEntity> organGestorsCanditats = organGestor.getAllChildren();
		
		// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
		List<List<OrganGestorEntity>> sublists = org.apache.commons.collections4.ListUtils.partition(organGestorsCanditats, 1000);
		List<OrganGestorEntity> filtrats = new ArrayList<>();
		for (List<OrganGestorEntity> list : sublists) {
			filtrats.addAll(
					organGestorRepository.findByCanditatsAndFiltre(
							entitatId,
							list,
							filter == null || filter.isEmpty(),
							filter != null ? filter : ""));
		}

		return conversioTipusHelper.convertirList(filtrats, OrganGestorDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAccessiblesUsuariActualRolUsuari(Long entitatId, String filter, boolean directOrganPermisRequired) {
		
		List<OrganGestorEntity> filtrats = new ArrayList<OrganGestorEntity>();
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, 
				false, 
				false);
		
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIdPermesos = permisosHelper.getObjectsIdsWithPermission(MetaNodeEntity.class, ExtendedPermission.READ);
		
		// Si l'usuari actual te permis direct al metaExpedient, automaticament te permis per tots unitats fills del entitat
		if (metaExpedientIdPermesos != null && !metaExpedientIdPermesos.isEmpty() && !directOrganPermisRequired) {

			filtrats = organGestorRepository.findByEntitatAndFiltre(
					entitat.getId(),
					filter == null || filter.isEmpty(),
					filter != null ? filter : "");
		} else {
			
			List<OrganGestorEntity> organGestorsCanditats = entityComprovarHelper.getOrgansByOrgansAndCombinacioMetaExpedientsOrgansPermissions(entitat);
			organGestorsCanditats = !organGestorsCanditats.isEmpty() ? organGestorsCanditats : null;
			
			if (Utils.isNotEmpty(organGestorsCanditats)) {
				
				// if there are 1000+ values in IN clause, exception is thrown ORA-01795: el número máximo de expresiones en una lista es 1000
				List<List<OrganGestorEntity>> sublists = org.apache.commons.collections4.ListUtils.partition(organGestorsCanditats, 1000);

				for (List<OrganGestorEntity> sublist : sublists) {
					filtrats.addAll(
							organGestorRepository.findByCanditatsAndFiltre(
									entitat.getId(),
									sublist,
									filter == null || filter.isEmpty(),
									filter != null ? filter : ""));
				}
			}
			
		}
		return conversioTipusHelper.convertirList(filtrats, OrganGestorDto.class);
	}
	
	

	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		return conversioTipusHelper.convertirList(
				organGestorHelper.findAmbEntitatPermis(
						entitat,
						ExtendedPermission.ADMINISTRATION),
				OrganGestorDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findOrganismesEntitatAmbPermisCache(Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.findOrganismesEntitatAmbPermis(entitatId, auth.getName());
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findOrganismesEntitatAmbPermisDissenyCache(Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.findOrganismesEntitatAmbPermisDisseny(entitatId, auth.getName());
	}
	
	@Transactional(readOnly = true)
	@Override
	public void evictOrganismesEntitatAmbPermis(Long entitatId, String usuariCodi) {
		cacheHelper.evictOrganismesEntitatAmbPermis(entitatId, usuariCodi);
		cacheHelper.evictOrganismesEntitatAmbPermisDisseny(entitatId, usuariCodi);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrganismeDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			String filter, 
			Long expedientId,
			String rolActual, 
			Long organActualId) {
		return findPermesosByEntitatAndExpedientTipusIdAndFiltre(
				entitatId,
				metaExpedientId,
				expedientId == null ? ExtendedPermission.CREATE : ExtendedPermission.WRITE,
				filter, 
				expedientId,
				rolActual, 
				organActualId);
	}
	
	

	@Transactional(readOnly = true)
	@Override
	public List<PermisOrganGestorDto> findPermisos(Long entitatId) {
		return findPermisos(entitatId, null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<PermisOrganGestorDto> findPermisos(Long entitatId, Long organId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta com a administrador els permisos dels organs gestors de l'entitat (" + "id=" + entitatId + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		List<PermisOrganGestorDto> results = new ArrayList<PermisOrganGestorDto>();
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		if (!esAdministradorEntitat) {
			return results;
		}
		List<OrganGestorDto> organs;
		if (organId == null) {
			organs = findByEntitat(entitatId);	
		} else {
			organs = new ArrayList<OrganGestorDto>();
			organs.add(findItem(organId));
		}
		for (OrganGestorDto o: organs) {
			List<PermisDto> permisosOrgan = permisosHelper.findPermisos(o.getId(), OrganGestorEntity.class);
			for (PermisDto p: permisosOrgan) {
				PermisOrganGestorDto permisOrgan = conversioTipusHelper.convertir(p, PermisOrganGestorDto.class);
				permisOrgan.setOrganGestor(o);
				if (p.getPrincipalTipus() == PrincipalTipusEnumDto.USUARI) {
					try {
						permisOrgan.setPrincipalCodiNom(usuariHelper.getUsuariByCodi(permisOrgan.getPrincipalNom()).getNom() + " (" + permisOrgan.getPrincipalNom() + ")");
					} catch (NotFoundException ex) {
						logger.debug("No s'ha trobat cap usuari amb el codi " + permisOrgan.getPrincipalNom());
						permisOrgan.setPrincipalCodiNom(permisOrgan.getPrincipalNom());
					}
				} else {
					permisOrgan.setPrincipalCodiNom(permisOrgan.getPrincipalNom());
				}
				results.add(permisOrgan);
			}
		}
		return results;
	}

	@Transactional
	@Override
	public void updatePermis(Long id, PermisDto permis, Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Modificació com a administrador del permis de l'entitat (" + "id=" + id + ", " + "permis=" + permis + ")");
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per a gestionar aquest organ gestor");
		}
		permisosHelper.updatePermis(id, OrganGestorEntity.class, permis);
		cacheHelper.evictEntitatsAccessiblesAllUsuaris();
	}
	@Transactional
	@Override
	public void deletePermis(Long id, Long permisId, Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Eliminació del permis de l'òrgan gestor (" + "id=" + id + ", " + "permisId=" + permisId + ")");
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'òrgan gestor (" + "id=" + id + ", " + "usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		permisosHelper.deletePermis(id, OrganGestorEntity.class, permisId);
		cacheHelper.evictEntitatsAccessiblesAllUsuaris();
	}
	@Transactional
	@Override
	public boolean hasPermisAdminComu(Long organId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean hasPermisAdminComu = permisosHelper.isGrantedAll(
				organId,
				OrganGestorEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADM_COMU },
				auth);
		return hasPermisAdminComu;
	}


	@Transactional
	@Override
	public ArbreDto<OrganGestorDto> findOrgansArbreAmbFiltre(
			Long entitatId,
			OrganGestorFiltreDto filtre) {
		
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false,
				false,
				false);
		
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);

		List<OrganGestorDto> organsDto = conversioTipusHelper.convertirList(
				organs,
				OrganGestorDto.class);

		ArbreDto<OrganGestorDto> resposta = new ArbreDto<OrganGestorDto>(false);
		// Cerca l'unitat organitzativa arrel
		OrganGestorDto organGestorArrel = null;
		for (OrganGestorDto organGestor : organsDto) {
			if (entitat.getUnitatArrel().equalsIgnoreCase(organGestor.getCodi())) {
				organGestorArrel = organGestor;
				break;
			}
		}
		if (organGestorArrel != null) {
			// Omple l'arbre d'unitats organitzatives
			resposta.setArrel(organGestorHelper.getNodeArbreUnitatsOrganitzatives(organGestorArrel, organsDto, null));
			
			Set<String> unitatCodiPermesos = organGestorRepository.findAmbFiltre(
					entitat,
					filtre.getCodi() == null || filtre.getCodi().isEmpty(),
					filtre.getCodi() != null ? filtre.getCodi().trim() : "",
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					filtre.getPareId() == null,
					filtre.getPareId(),
					filtre.getEstat() == null,
					filtre.getEstat());
			
			// Calcula els nodes a "salvar" afegint els nodes permesos
			// i tots els seus pares.
			List<ArbreNodeDto<OrganGestorDto>> nodes = resposta.toList();
			Set<String> unitatCodiSalvats = new HashSet<String>();
			for (ArbreNodeDto<OrganGestorDto> node: nodes) {
				if (unitatCodiPermesos.contains(node.dades.getCodi())) {
					unitatCodiSalvats.add(node.dades.getCodi());
					ArbreNodeDto<OrganGestorDto> pare = node.getPare();
					while (pare != null) {
						unitatCodiSalvats.add(pare.dades.getCodi());
						pare = pare.getPare();
					}
				}
			}
			// Esborra els nodes no "salvats"
			for (ArbreNodeDto<OrganGestorDto> node: nodes) {
				if (!unitatCodiSalvats.contains(node.dades.getCodi())) {
					if (node.getPare() != null)
						node.getPare().removeFill(node);
					else
						resposta.setArrel(null);
				}
					
			}
			
			
			return resposta;

		} else {
			return null;
		}
	}
	
	
	@Transactional
	@Override
	public String getOrganCodiFromContingutId(Long contingutId) {
		return organGestorHelper.getOrganCodiFromContingutId(contingutId);
	}
	
	
	@Transactional
	@Override
	public String getOrganCodiFromAnnexId(Long annexId) {
		return organGestorHelper.getOrganCodiFromAnnexId(annexId);
	}
	
	@Transactional
	@Override
	public String getOrganCodiFromMetaDocumentId(Long metaDocumentId) {
		return organGestorHelper.getOrganCodiFromMetaDocumentId(metaDocumentId);
	}
	
	@Transactional
	@Override
	public String getOrganCodiFromMetaExpedientId(Long metaExpedientId) {
		return organGestorHelper.getOrganCodiFromMetaExpedientId(metaExpedientId);
	}


	private List<OrganismeDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			Permission permis,
			String filtre,
			Long expedientId,
			String rolActual,
			Long organActualId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		List<OrganismeDto> organsGestors = null;

		if (RolHelper.isAdminEntitat(rolActual)) {
			organsGestors = organGestorHelper.findArrelFills(entitat.getCodi(), filtre);
		} else if (RolHelper.isAdminOrgan(rolActual)) {
			organsGestors = organGestorHelper.findDescendents(entitat.getCodi(), organActualId, filtre);
		} else {

			if (metaExpedient.getOrganGestor() != null) {
				// S'han de retornar els fills de l'òrgan gestor del metaExpedient si l'usuari actual
				// te permisos per l'òrgan gestor.
				organsGestors = organGestorHelper.findDescendents(entitat.getCodi(), metaExpedient.getOrganGestor().getId(), filtre);
			} else {

				Set<String> organCodis = new HashSet<>();
				// Cercam las parelles metaExpedient-organ amb permisos assignats
				List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors = metaExpedientOrganGestorRepository.findByMetaExpedient(metaExpedient);
				if (metaExpedientOrgansGestors != null && !metaExpedientOrgansGestors.isEmpty()) {
					permisosHelper.filterGrantedAll(
							metaExpedientOrgansGestors,
							MetaExpedientOrganGestorEntity.class,
							new Permission[]{permis});
					if (!metaExpedientOrgansGestors.isEmpty()) {
						organCodis.addAll(metaExpedientOrganGestorRepository.findOrganGestorCodisByMetaExpedientOrganGestors(metaExpedientOrgansGestors));
					}
				}
				// Cercam els òrgans amb permisos per procediments comuns
				if (metaExpedient.getOrganGestor() == null) {
					List<Long> organProcedimentsComunsIds = permisosHelper.getObjectsIdsWithTwoPermissions(OrganGestorEntity.class, ExtendedPermission.COMU, permis);
					if (organProcedimentsComunsIds != null && !organProcedimentsComunsIds.isEmpty()) {
						organCodis.addAll(organGestorRepository.findCodisByIdList(entitat.getId(), organProcedimentsComunsIds));
					}
				}
				organsGestors = organGestorHelper.findDescendents(entitat.getCodi(), new ArrayList<>(organCodis), filtre);

				// Si l'usuari actual te permis direct al metaExpedient, automaticament te permis per tots unitats fills del entitat
				if (organsGestors == null || organsGestors.isEmpty()) {
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					boolean metaNodeHasPermis = permisosHelper.isGrantedAll(
							metaExpedientId,
							MetaNodeEntity.class,
							new Permission[]{permis},
							auth);
					if (metaNodeHasPermis) {
						organsGestors = organGestorHelper.findArrelFills(entitat.getCodi(), filtre);
					}
				}
			}

			// if we modify expedient we have to ensure that we can still see its organ in dropdown even if permissions were removed
			if (expedientId != null) {
				ExpedientEntity expedientEntity = entityComprovarHelper.comprovarExpedient(
						expedientId,
						false,
						false,
						false,
						false,
						false,
						null);

				OrganGestorEntity organGestorEntity = expedientEntity.getOrganGestor();

				if (organsGestors == null) {
					organsGestors = new ArrayList<>();
				}
				boolean alreadyInTheList = false;
				for (OrganismeDto organGestor : organsGestors) {
					if (organGestor.getId().equals(organGestorEntity.getId())) {
						alreadyInTheList = true;
					}
				}
				if (!alreadyInTheList) {
					organsGestors.add(0, cacheHelper.findOrganigramaByEntitat(entitat.getCodi()).get(organGestorEntity.getCodi()));
				}
			}

		}
		return organsGestors;
	}

	private List<OrganGestorDto> findOrganismesByEntitat(String codiDir3) {
		List<OrganGestorDto> organismes = new ArrayList<OrganGestorDto>();
		Map<String, NodeDir3> organigramaDir3 = pluginHelper.getOrganigramaOrganGestor(codiDir3);
		if (organigramaDir3 != null) {
		NodeDir3 arrel = organigramaDir3.get(codiDir3);
		OrganGestorDto organisme = new OrganGestorDto();
		organisme.setCodi(arrel.getCodi());
		organisme.setNom(arrel.getDenominacio());
		organisme.setPareCodi(null);
		organismes.add(organisme);
		findOrganismesFills(arrel, organismes);
		}
		return organismes;
	}
	
	private void findOrganismesFills(NodeDir3 root, List<OrganGestorDto> organismes) {
		for (NodeDir3 fill: root.getFills()) {
			OrganGestorDto organisme = new OrganGestorDto();
			organisme.setCodi(fill.getCodi());
			organisme.setNom(fill.getDenominacio());
			organisme.setPareCodi(root.getCodi());
			organismes.add(organisme);
			findOrganismesFills(fill, organismes);
		}
	}

	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

	@Override
	public List<OrganGestorDto> findOrgansSuperiorByEntitat(Long entitatId) {
		List<OrganGestorEntity> organsSuperiorEntities = new ArrayList<OrganGestorEntity>();
		organsSuperiorEntities = organGestorRepository.findByEntitatAndHasPare(entitatId);
		List<OrganGestorDto> organsSuperior = new ArrayList<OrganGestorDto>();
		for (OrganGestorEntity organ : organsSuperiorEntities) {
			organsSuperior.add(conversioTipusHelper.convertir(organ, OrganGestorDto.class));
		}
		return organsSuperior;
	}

	private String msg(String codi) {
		return messageHelper.getMessage(codi);
	}
	
	private String msg(String codi, Object... params) {
		return messageHelper.getMessage(codi, params);
	}

    @Override
    public Boolean isPermisEnviamentPostalOrganOrAntecesor(Long organGestorId) {
        OrganGestorEntity organGestor = organGestorRepository.findById(organGestorId).get();
        if (organGestor.isPermetreEnviamentPostal()) return true;
        List<OrganGestorEntity> organGestorEntityList = organGestorHelper.findPares(organGestor, false);
        for (OrganGestorEntity organGestorEntity: organGestorEntityList) {
            if (organGestorEntity.isPermetreEnviamentPostal() && organGestorEntity.isPermetreEnviamentPostalDescendents()){
                return true;
            }
        }
        return false;
    }
}
