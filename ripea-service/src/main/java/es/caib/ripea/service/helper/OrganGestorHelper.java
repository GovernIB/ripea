package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.AvisEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientOrganPareEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.RegistreAnnexEntity;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientOrganPareRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.RegistreAnnexRepository;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.service.intf.dto.ActualitzacioInfo;
import es.caib.ripea.service.intf.dto.ActualitzacioInfo.ActualitzacioInfoBuilder;
import es.caib.ripea.service.intf.dto.ArbreNodeDto;
import es.caib.ripea.service.intf.dto.AvisNivellEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.intf.dto.PrediccioSincronitzacio;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.intf.dto.TipusTransicioEnumDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;

@Component
public class OrganGestorHelper {

	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private ExpedientOrganPareRepository expedientOrganPareRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private AvisRepository avisRepository;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private MessageHelper messageHelper;
	@Autowired private ContingutRepository contingutRepository;
	@Autowired private RegistreAnnexRepository registreAnnexRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
    @Autowired private ConversioTipusHelper conversioTipusHelper;
    @Autowired private EntityComprovarHelper entityComprovarHelper;
    
	public static final String ORGAN_NO_SYNC = "Hi ha canvis pendents de sincronitzar a l'organigrama";
    @Autowired private OrganGestorCacheHelper organGestorCacheHelper;

    @SuppressWarnings("unchecked")
    public PrediccioSincronitzacio predictSyncDir3OrgansGestors(EntitatEntity entitat) throws Exception {

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
			MultiValuedMap splitMap = new ArrayListValuedHashMap();
			MultiValuedMap mergeOrSubstMap = new ArrayListValuedHashMap();

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
			MultiValuedMap mergeMap = new ArrayListValuedHashMap();
			MultiValuedMap substMap = new ArrayListValuedHashMap();
			
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
	private boolean isAlreadyAddedToMap(MultiValuedMap mergeMap, UnitatOrganitzativaDto key, UnitatOrganitzativaDto value) {
		boolean contains = false;
		List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeMap.get(key);
		if (values != null) {
			for (UnitatOrganitzativaDto unitat : values) {
				if (unitat.getCodi().equals(value.getCodi())) {
					contains = true;
				}
			}
		}
		return contains;
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
	
	private UnitatOrganitzativa getUnitatFromCodi(String codi, List<UnitatOrganitzativa> allUnitats){
		for (UnitatOrganitzativa unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				return unitatWS;
			}
		}
		return null;
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
    
	public List<OrganGestorEntity> findAmbEntitatPermis(
			EntitatEntity entitat,
			Permission permis) {
		List<Long> objectsIds = permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				permis);
		if (objectsIds == null || objectsIds.isEmpty()) {
			return new ArrayList<OrganGestorEntity>();
		} else {
			return organGestorRepository.findByEntitatAndIds(entitat, objectsIds);
		}
	}

	/**
	 * Comprova si es te permís sobre un òrgan gestor, o un pare d'aquest
	 *
	 * @param organGestor
	 * @param permis
	 * @return
	 */
	public boolean isOrganGestorPermes(
			OrganGestorEntity organGestor,
			Permission permis) {

		boolean permes = false;

		boolean granted = permisosHelper.isGrantedAll(
				organGestor.getId(),
				OrganGestorEntity.class,
				new Permission[] {permis},
				SecurityContextHolder.getContext().getAuthentication());
		if (granted) {
			permes = true;
		} else {
			List<OrganGestorEntity> organGestorAmbPares = findPares(
					organGestor,
					true);
			for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
				if (permisosHelper.isGrantedAny(
						organGestorActual.getId(),
						OrganGestorEntity.class,
						new Permission[] { permis },
						SecurityContextHolder.getContext().getAuthentication())) {
					permes = true;
					break;
				}
			}
		}
		return permes;
	}

	public boolean isOrganGestorPermes(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor,
			Permission permis, 
			String rolActual) {
		
		List<OrganGestorEntity> organGestorAmbPares = findPares(
				organGestor,
				true);
		
		if (RolHelper.isAdminEntitat(rolActual)) {
			return true;
		} else if (RolHelper.isAdminOrgan(rolActual)) {
			boolean permes = false;
			for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
				if (permisosHelper.isGrantedAny(
						organGestorActual.getId(),
						OrganGestorEntity.class,
						new Permission[] { ExtendedPermission.ADMINISTRATION },
						SecurityContextHolder.getContext().getAuthentication())) {
					permes = true;
					break;
				}
			}
			if (permes) {
				return true;
			}

		} else {
			boolean permes = false;
			
			boolean granted = permisosHelper.isGrantedAll(
					metaExpedient.getId(),
					MetaNodeEntity.class,
					new Permission[] {permis},
					SecurityContextHolder.getContext().getAuthentication());
			if (granted) {
				permes = true;
			} else {

				for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
					if (permisosHelper.isGrantedAny(
							organGestorActual.getId(),
							OrganGestorEntity.class,
							new Permission[] { permis },
							SecurityContextHolder.getContext().getAuthentication())) {
						permes = true;
						break;
					}
					MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
							metaExpedient,
							organGestorActual);
					if (metaExpedientOrganGestor != null && permisosHelper.isGrantedAny(
								metaExpedientOrganGestor.getId(),
								MetaExpedientOrganGestorEntity.class,
								new Permission[] { permis },
								SecurityContextHolder.getContext().getAuthentication())) {
						permes = true;
						break;
					}
				}
			}
			return permes;
		}
		return false;
	}

	public void crearExpedientOrganPares(
			ExpedientEntity expedient,
			OrganGestorEntity organGestor) {
		List<OrganGestorEntity> organGestorAmbPares = findPares(
				organGestor,
				true);
		for (OrganGestorEntity organGestorActual: organGestorAmbPares) {
			MetaExpedientOrganGestorEntity metaExpedientOrganGestor = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
					expedient.getMetaExpedient(),
					organGestorActual); 
			if (metaExpedientOrganGestor == null) {
				logger.debug("meteaxp-organ created,  metaexp: " + expedient.getMetaExpedient().getId() + ", organ: " + organGestorActual.getId() + " " + organGestorActual.getNom());
				metaExpedientOrganGestor = metaExpedientOrganGestorRepository.save(
						MetaExpedientOrganGestorEntity.getBuilder(
								expedient.getMetaExpedient(),
								organGestorActual).build());
			}
			ExpedientOrganPareEntity expedientOrganPare = ExpedientOrganPareEntity.getBuilder(
					expedient,
					metaExpedientOrganGestor).build();
			
			logger.debug("expedient-organ-pare created, expedient: " + expedient.getId() +  ", organ: " + organGestorActual.getId() + " " + organGestorActual.getNom());
			expedientOrganPareRepository.save(expedientOrganPare);
		}
	}
	
	public void removeOldExpedientOrganPares(
			ExpedientEntity expedient) {
		
		for (ExpedientOrganPareEntity expOrgPare : expedient.getOrganGestorPares()) {
			expedientOrganPareRepository.delete(expOrgPare);
		}
		expedient.removeOrganGestorPares();

	}


	
	public List<OrganismeDto> findArrelFills(String entitatCodi, String filtre) {
		List<OrganismeDto> organismesByEntitat = organGestorCacheHelper.findOrganismesByEntitat(entitatCodi);
		return filtrarOrganismes(filtre, organismesByEntitat);
	}

	public List<OrganismeDto> findDescendents(String entitatCodi, Long organId, String filtre) {
		if (organId == null) {
			return new ArrayList<>();
		}
		OrganGestorEntity organ = organGestorRepository.getOne(organId);
		List<OrganismeDto> organismesByEntitat = organGestorCacheHelper.getOrganismesDescendentsByOrgan(entitatCodi, organ.getCodi());
		return filtrarOrganismes(filtre, organismesByEntitat);
	}

	public List<OrganismeDto> findDescendents(
			String entitatCodi,
			List<String> organsCodis,
			String filtre) {
		if (organsCodis == null || organsCodis.isEmpty())
			return new ArrayList<>();

		List<OrganismeDto> organismesByEntitat = organGestorCacheHelper.getOrganismesDescendentsByOrgans(entitatCodi, organsCodis);
		return filtrarOrganismes(filtre, organismesByEntitat);
	}

	public List<String> findCodisDescendents(
			String entitatCodi,
			Long organId) {
		if (organId == null)
			return new ArrayList<>();

		OrganGestorEntity organ = organGestorRepository.getOne(organId);
		return organGestorCacheHelper.getCodisOrgansFills(entitatCodi, organ.getCodi());
	}

	private List<OrganismeDto> filtrarOrganismes(String filtre, List<OrganismeDto> organismes) {
		if (StringUtils.isEmpty(filtre))
			return organismes;

		List<OrganismeDto> filteredOrganismes = new ArrayList<>();
		if (organismes != null) {
			for (OrganismeDto orgDto : organismes) {
				if (orgDto.getNom().contains(filtre) || orgDto.getCodi().contains(filtre)) {
					filteredOrganismes.add(orgDto);
				}
			}
//			// Ens assegurem que sempre hi sigui l'organisme arrel. Per què???
//			OrganismeDto organismeEntitat = organismes.get(0);
//			filteredOrganismes.remove(organismeEntitat);
//			filteredOrganismes.add(0, organismeEntitat);
		}
		return filteredOrganismes;
	}


	public List<OrganGestorEntity> findPares(
			OrganGestorEntity organGestor,
			boolean incloureOrganGestor) {
		List<OrganGestorEntity> pares = new ArrayList<OrganGestorEntity>();
		if (incloureOrganGestor) {
			pares.add(organGestor);
		}
		OrganGestorEntity organGestorActual = organGestor;
		while (organGestorActual.getPare() != null) {
			organGestorActual = organGestorActual.getPare();
			pares.add(organGestorActual);
		}
		return pares;
	}
	
	public List<Long> findParesIds(
			Long organGestorId,
			boolean incloureOrganGestor) {
		OrganGestorEntity organGestor = organGestorRepository.getOne(organGestorId);
		List<Long> pares = new ArrayList<Long>();
		if (incloureOrganGestor) {
			pares.add(organGestor.getId());
		}
		OrganGestorEntity organGestorActual = organGestor;
		while (organGestorActual.getPare() != null) {
			organGestorActual = organGestorActual.getPare();
			pares.add(organGestorActual.getId());
		}
		return pares;
	}
	
	

	/**
	 * 
	 * @param organGestor - in first call it is unitat arrel, later the children nodes
	 * @param organGestors
	 * @param pare - in first call it is null, later pare
	 * @return
	 */
	public ArbreNodeDto<OrganGestorDto> getNodeArbreUnitatsOrganitzatives(
			OrganGestorDto organGestor,
			List<OrganGestorDto> organGestors,
			ArbreNodeDto<OrganGestorDto> pare) {
		
		// creating current arbre node and filling it with pare arbre node and dades as current unitat
		ArbreNodeDto<OrganGestorDto> currentArbreNode = new ArbreNodeDto<OrganGestorDto>(
				pare,
				organGestor);
		String codiUnitat = (organGestor != null) ? organGestor.getCodi() : null;
		
		// for every child of current unitat call recursively getNodeArbreUnitatsOrganitzatives()
		for (OrganGestorDto uo : organGestors) {
			// searches for children of current unitat
			if ((codiUnitat == null && uo.getPareCodi() == null) || (uo.getPareCodi() != null && uo.getPareCodi().equals(codiUnitat))) {
				
				currentArbreNode.addFill(
						getNodeArbreUnitatsOrganitzatives(
								uo,
								organGestors,
								currentArbreNode));
			}
		}
		return currentArbreNode;
	}
	

	public void consultaCanvisOrganigrama(EntitatEntity entitat) {

		try {
			Date ara = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ara);
			calendar.add(Calendar.YEAR, 1);

			List<UnitatOrganitzativa> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(
					entitat.getUnitatArrel(),
					entitat.getDataActualitzacio(),
					entitat.getDataSincronitzacio());

			List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), ORGAN_NO_SYNC);
			if (avisosSinc != null && !avisosSinc.isEmpty()) {
				avisRepository.deleteAll(avisosSinc);
			}

			if (unitatsWs != null && !unitatsWs.isEmpty()) {
				AvisEntity avis = AvisEntity.getBuilder(
						ORGAN_NO_SYNC,
						"Realitzi el procés de sincronització d'òrgans gestors per a disposar dels òrgans gestors actuals.",
						ara,
						calendar.getTime(),
						AvisNivellEnumDto.ERROR,
						true,
						entitat.getId()).build();
				avisRepository.save(avis);
			}
		} catch (Exception e) {
			logger.error("Error al consultar canvis organigrama en segon pla", e);
		}

	}

	public void sincronitzarOrgans(Long entitatId,
								   List<UnitatOrganitzativa> unitatsWs,
								   List<OrganGestorEntity> obsoleteUnitats,
								   List<OrganGestorEntity> organsDividits,
								   List<OrganGestorEntity> organsFusionats,
								   List<OrganGestorEntity> organsSubstituits,
								   ProgresActualitzacioDto progres) {

		Map<String, List<String>> organsParesPendentsAssignar = new HashMap<>();

		EntitatEntity entitat = entitatRepository.getOne(entitatId);
		int nombreUnitatsTotal = unitatsWs.size();
		int nombreUnitatsProcessades = 0;

		// Agafa totes les unitats del WS i les guarda a BBDD. Si la unitat no existeix la crea, i si existeix la sobreescriu.
		for (UnitatOrganitzativa unitatWS: unitatsWs) {
			ActualitzacioInfo info = sincronizarUnitat(unitatWS, entitat, organsParesPendentsAssignar);
			progres.addInfo(info);
			progres.setProgres(2 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
		}
		progres.setProgres(12);

		List<String> unitatsWsCodis = new  ArrayList<String>();
		
		// Històrics
		nombreUnitatsProcessades = 0;
		for (UnitatOrganitzativa unitatWS : unitatsWs) {
			unitatsWsCodis.add(unitatWS.getCodi());
			OrganGestorEntity unitat = organGestorRepository.findByEntitatAndCodi(entitat, unitatWS.getCodi());
			sincronizarHistoricsUnitat(unitat, unitatWS, entitat);
			progres.setProgres(12 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
			if (unitatWS.getHistoricosUO()!=null && !unitatWS.getHistoricosUO().isEmpty()) {
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.historic", unitatWS.getCodi())).infoText(msg("unitat.synchronize.info.historic", unitat.getCodi(), unitat.getNom())).build());
			}
		}
		progres.setProgres(22);

		// Definint tipus de transició
		obsoleteUnitats.addAll(organGestorRepository.findByEntitatNoVigent(entitat));//TODO: should only take unitats obsolets in last sincronization (should be taken from unitatsWs), now it takes from DB so it takes all obsoletes (even from previous syncronizations) 
		
		nombreUnitatsProcessades = 0;
		nombreUnitatsTotal = obsoleteUnitats.size();
		for (OrganGestorEntity obsoleteUnitat : obsoleteUnitats) {
			String infoText = "";
			obsoleteUnitat.setEstat(OrganEstatEnumDto.E);

			if (obsoleteUnitat.getNous().size() > 1) {
				obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.DIVISIO);
				organsDividits.add(obsoleteUnitat);
				infoText = msg("unitat.synchronize.info.transicio.divisio", obsoleteUnitat.getCodi(), organsToCodiList(obsoleteUnitat.getNous()));
			} else {
				if (obsoleteUnitat.getNous().size() == 1) {
					if (obsoleteUnitat.getNous().get(0).getAntics().size() > 1) {
						obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.FUSIO);
						organsFusionats.add(obsoleteUnitat);
						infoText = msg("unitat.synchronize.info.transicio.fusio", organsToCodiList(obsoleteUnitat.getNous().get(0).getAntics()), obsoleteUnitat.getNous().get(0).getCodi());
					} else if (obsoleteUnitat.getNous().get(0).getAntics().size() == 1) {
						obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.SUBSTITUCIO);
						organsSubstituits.add(obsoleteUnitat);
						infoText = msg("unitat.synchronize.info.transicio.substitucio", obsoleteUnitat.getCodi(), obsoleteUnitat.getNous().get(0).getCodi());
					}
				} else {
					obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.EXTINCIO);
					infoText = msg("unitat.synchronize.info.transicio.extincio");
				}
			}
			
			List<OrganGestorEntity> nous = obsoleteUnitat.getNous();
			if (nous != null && contains(nous, obsoleteUnitat)) {
				logger.info("Unitat dividida o fusionada cap a ella mateixa " + obsoleteUnitat.getCodi() + " - " + obsoleteUnitat.getNom() + "This is probably the error of DIR3CAIB");
				obsoleteUnitat.setEstat(OrganEstatEnumDto.V);
			}
			
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.transicio", obsoleteUnitat.getCodi(), obsoleteUnitat.getNom())).infoText(infoText).build());
			progres.setProgres(22 + (nombreUnitatsProcessades++ * 5 / nombreUnitatsTotal));
		}

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), OrganGestorHelper.ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}
		progres.setProgres(27);

		Date ara = new Date();
		
		// Si és la primera sincronització
		if (entitat.getDataSincronitzacio() == null) {
			
			List<OrganGestorEntity> organs =  organGestorRepository.findByEntitat(entitat);
			
			for (OrganGestorEntity organ : organs) {
				if (!unitatsWsCodis.contains(organ.getCodi())) {
					logger.info("Primera sync. Organ WS no exisiteix en DB : " + organ.getCodi());
					organ.updateEstat(OrganEstatEnumDto.E);
				}
			}
			entitat.setDataSincronitzacio(ara);
		}
		entitat.setDataActualitzacio(ara);

	}
	
	
    public void actualitzarExpedientsObertsAmbOrgansObsolets(
			List<OrganGestorEntity> organsFusionatsISubstituits,
			ProgresActualitzacioDto progres) {

		int nombreOrgansTotal = organsFusionatsISubstituits.size();
		int nombreOrgansProcessades = 0;

		for (OrganGestorEntity organFusionatISubstituit : organsFusionatsISubstituits) {
			
			OrganGestorEntity organDesti = organFusionatISubstituit.getNous().get(0);
			List<ExpedientEntity> expedients = expedientRepository.findByOrganGestorAndEstat(organFusionatISubstituit, ExpedientEstatEnumDto.OBERT);
			
			logger.info("Modifying organ of expedients from " + organFusionatISubstituit.getCodi() + " to " + organDesti.getCodi());
			for (ExpedientEntity expedient : expedients) {
				
				logger.info("Organ of expedient " + expedient.getId() + " " + expedient.getNumero() + " " + expedient.getNom());
				expedient.updateOrganGestor(organDesti);
				removeOldExpedientOrganPares(
						expedient);
				crearExpedientOrganPares(
						expedient,
						organDesti);
			}
			
			progres.setProgres(75 + (24 * nombreOrgansProcessades++ / nombreOrgansTotal));
		}
    }

	public void actualitzarOrganCodi(String organCodi) {
		if (organCodi != null) {
			ConfigHelper.setOrganCodi(organCodi);
		}
	}
	
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
	public String getOrganCodiFromContingutId(Long contingutId) {

		String organCodi = null;
		if (contingutId != null) {
			ContingutEntity contingut = contingutRepository.findById(contingutId).orElse(null);
			if (contingut != null) {
				ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
				organCodi =  deproxied.getExpedientPare().getOrganGestor().getCodi();
			}
		}
		return organCodi;
	}
	
	@Transactional
	public String getOrganCodiFromAnnexId(Long annexId) {
		RegistreAnnexEntity annexEntity = registreAnnexRepository.getOne(annexId);
		return annexEntity.getRegistre().getDestiCodi();
	}
	
	
	@Transactional
	public String getOrganCodiFromMetaDocumentId(Long metaDocumentId) {
		String organCodi = null;
		MetaDocumentEntity metaDocument = metaDocumentRepository.getOne(metaDocumentId);
		MetaExpedientEntity metaExpedient = metaDocument.getMetaExpedient();
		if (metaExpedient != null) {
			OrganGestorEntity organGestor = metaExpedient.getOrganGestor();
			if (organGestor != null) {
				organCodi = organGestor.getCodi();
			}
		}
		return organCodi;
	}
	
	@Transactional
	public String getOrganCodiFromMetaExpedientId(Long metaExpedientId) {
		String organCodi = null;
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(metaExpedientId);
		OrganGestorEntity organGestor = metaExpedient.getOrganGestor();
		if (organGestor != null) {
			organCodi = organGestor.getCodi();
		}
		return organCodi;
	}
	
	@Transactional
	public void organsDescarregarNomCatala() {
    	List<EntitatEntity> entitats = entitatRepository.findAll();
    	for (EntitatEntity entitat : entitats) {
    		ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
    		List<UnitatOrganitzativa> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getUnitatArrel(), null, null);
    		for (UnitatOrganitzativa unitatOrganitzativa : unitatsWs) {
    			OrganGestorEntity organ = organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), unitatOrganitzativa.getCodi());
				if (organ != null && Utils.isNotEmpty(unitatOrganitzativa.getDenominacioCooficial())) {
					organ.updateNomCatala(unitatOrganitzativa.getDenominacioCooficial());
				} 
			}
		}
    }
	

	private String organsToCodiList(List<OrganGestorEntity> organs) {
		String text = "";
		for (OrganGestorEntity organ: organs) {
			text += organ.getCodi() + ", ";
		}
		if (text.length() > 2) {
			text = text.substring(0, text.length() - 2);
		}
		return text;
	}

	private ActualitzacioInfo sincronizarUnitat(UnitatOrganitzativa unitatWS, EntitatEntity entitat, Map<String, List<String>> organsParesPendentsAssignar) {
		ActualitzacioInfoBuilder infoBuilder = ActualitzacioInfo.builder().isOrgan(true);
		OrganGestorEntity unitat = null;
		if (unitatWS != null) {
			// checks if unitat already exists in database
			unitat = organGestorRepository.findByEntitatAndCodi(entitat, unitatWS.getCodi());
			// TODO: El pare potser encara no existeix. Per tant hem de comprovar si s'ha assignat, i si no, assignar-ho al crear el pare
			OrganGestorEntity organPare = organGestorRepository.findByEntitatAndCodi(entitat, unitatWS.getCodiUnitatSuperior());
			// if not it creates a new one
			if (unitat == null) {
				logger.info("Unitat WS:" + unitatWS + "\n\t Unitat DB no existe");
				
				infoBuilder
					.infoTitol(msg("unitat.synchronize.titol.organ.crear", unitatWS.getCodi()))
					.isIsNew(true)
					.codiOrgan(unitatWS.getCodi())
					.nomNou(Utils.isNotEmpty(unitatWS.getDenominacioCooficial()) ? unitatWS.getDenominacioCooficial() : unitatWS.getDenominacio())
					.estatNou(OrganGestorEntity.getEstat(unitatWS.getEstat()));
				
				// Venen les unitats ordenades, primer el pare i després els fills?
				unitat = OrganGestorEntity.getBuilder(unitatWS.getCodi())
						.entitat(entitat)
						.nomEspanyol(unitatWS.getDenominacio())
						.nom(Utils.isNotEmpty(unitatWS.getDenominacioCooficial()) ? unitatWS.getDenominacioCooficial() : unitatWS.getDenominacio())
						.pare(organPare)
						.estat(unitatWS.getEstat())
						.cif(unitatWS.getNifCif())
						.build();
				organGestorRepository.save(unitat);

				// Comprovam si l'òrgan que acabam de crear té fills pendents
				if (organsParesPendentsAssignar.containsKey(unitatWS.getCodi())) {
					List<String> codisFills = organsParesPendentsAssignar.get(unitatWS.getCodi());
					for (String codiFill: codisFills) {
						OrganGestorEntity fill = organGestorRepository.findByEntitatAndCodi(entitat, codiFill);
						fill.setPare(unitat);
					}
				}
			} else {
				logger.info("Unitat WS:" + unitatWS + "\n\t Unitat DB: " + unitat);
				infoBuilder
						.infoTitol(msg("unitat.synchronize.titol.organ", unitatWS.getCodi()))
						.isIsNew(false)
						.codiOrgan(unitatWS.getCodi())
						.nomAntic(unitat.getNom())
						.estatAntic(unitat.getEstat())
						.nomNou(Utils.isNotEmpty(unitatWS.getDenominacioCooficial()) ? unitatWS.getDenominacioCooficial() : unitatWS.getDenominacio())
						.estatNou(OrganGestorEntity.getEstat(unitatWS.getEstat()));
				
				unitat.update(
						Utils.isNotEmpty(unitatWS.getDenominacioCooficial()) ? unitatWS.getDenominacioCooficial() : unitatWS.getDenominacio(),
						unitatWS.getDenominacio(),
						unitatWS.getEstat(),
						organPare,
						unitatWS.getNifCif());
			}

			// Si el pare encara no existeix ho ficam en un mapa de pendents
			if (organPare == null) {
				if (organsParesPendentsAssignar.containsKey(unitatWS.getCodiUnitatSuperior())) {
					organsParesPendentsAssignar.get(unitatWS.getCodiUnitatSuperior()).add(unitatWS.getCodi());
				} else {
					List<String> fills = new ArrayList<>();
					fills.add(unitatWS.getCodi());
					organsParesPendentsAssignar.put(unitatWS.getCodiUnitatSuperior(), fills);
				}
			}

		}
		return infoBuilder.build();
	}

	private void sincronizarHistoricsUnitat(
			OrganGestorEntity unitat,
			UnitatOrganitzativa unidadWS,
			EntitatEntity entitat) {

		if (unidadWS.getHistoricosUO()!=null && !unidadWS.getHistoricosUO().isEmpty()) {
			for (String historicoCodi : unidadWS.getHistoricosUO()) {
				OrganGestorEntity nova = organGestorRepository.findByEntitatAndCodi(entitat, historicoCodi);
				
				boolean isAlreadyAddedToList = contains(unitat.getNous(), nova);
				if (!isAlreadyAddedToList) {
					unitat.addNou(nova);
					nova.addAntic(unitat);
				} else {
					//normally this shoudn't duplicate, it is added to deal with the result of call to WS DIR3 PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
					logger.info("Detected duplication of transtition in DB. Unitat" + unitat.getCodi() + "already transitioned into " + nova.getCodi() + ". Probably caused by error in DIR3");
				}

			}
		}
	}
	// TODO add equals method to OrganGestorEntity and replace by List#contains
	private boolean contains(
			List<OrganGestorEntity> organs,
			OrganGestorEntity organ) {
		boolean contains = false;
		for (OrganGestorEntity organGestorEntity : organs) {
			if (organGestorEntity.getId().equals(organ.getId())) {
				contains = true;
			}
		}
		return contains;
	}

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorHelper.class);

		// Eliminar organs no vigents no utilitzats??
	public void deleteExtingitsNoUtilitzats(List<OrganGestorEntity> obsoleteUnitats, ProgresActualitzacioDto progres) {
		int nombreUnitatsTotal = obsoleteUnitats.size();
		int nombreUnitatsProcessades = 0;

		Iterator<OrganGestorEntity> it = obsoleteUnitats.iterator();
		while (it.hasNext()) {
			OrganGestorEntity organObsolet = it.next();
			
			if (organObsolet.getAntics() != null && !organObsolet.getAntics().isEmpty()) {
				continue;
			}
			
			progres.setProgres(75 + (nombreUnitatsProcessades++ * 24)/nombreUnitatsTotal);

			Integer nombreProcediments = metaExpedientOrganGestorRepository.countByOrganGestor(organObsolet) + metaExpedientRepository.countByOrganGestor(organObsolet);
			if (nombreProcediments > 0) {
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.obsolet", organObsolet.getCodi(), organObsolet.getNom())).infoText(msg("unitat.synchronize.info.obsolets.procediment.error", organObsolet.getCodi(), nombreProcediments)).build());
				continue;
			}
			Integer nombreExpedients = expedientRepository.countByOrganGestor(organObsolet);
			if (nombreExpedients > 0) {
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.obsolet", organObsolet.getCodi(), organObsolet.getNom())).infoText(msg("unitat.synchronize.info.obsolets.expedient.error", organObsolet.getCodi(), nombreExpedients)).build());
				continue;
			}
			try {
				logger.info("Eliminant organ: " + organObsolet);
				permisosHelper.eliminarPermisosOrgan(organObsolet);
				organGestorRepository.delete(organObsolet);
				organGestorRepository.flush();
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.obsolet", organObsolet.getCodi(), organObsolet.getNom())).infoText(msg("unitat.synchronize.info.obsolets.eliminat", organObsolet.getCodi())).build());
			} catch (Exception ex) {
				logger.error("No ha estat possible esborrar l'òrgan gestor: " + organObsolet, ex);
				progres.addInfo(ActualitzacioInfo.builder().hasError(true).infoTitol(msg("unitat.synchronize.titol.obsolet", organObsolet.getCodi(), organObsolet.getNom())).infoText(msg("unitat.synchronize.info.obsolets.error", organObsolet.getCodi(), ex.getMessage())).build());
			}
		}
	}

	private String msg(String codi) {
		return messageHelper.getMessage(codi);
	}
	private String msg(String codi, Object... params) {
		return messageHelper.getMessage(codi, params);
	}
}
