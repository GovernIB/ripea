package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ActualitzacioInfo;
import es.caib.ripea.core.api.dto.ActualitzacioInfo.ActualitzacioInfoBuilder;
import es.caib.ripea.core.api.dto.ArbreNodeDto;
import es.caib.ripea.core.api.dto.AvisNivellEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.dto.TipusTransicioEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.AvisEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientOrganPareEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.repository.AvisRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientOrganPareRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;

@Component
public class OrganGestorHelper {

	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientOrganPareRepository expedientOrganPareRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private AvisRepository avisRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;

	public static final String ORGAN_NO_SYNC = "Hi ha canvis pendents de sincronitzar a l'organigrama";

	public List<OrganGestorEntity> findAmbEntitatPermis(
			EntitatEntity entitat,
			Permission permis) {
		List<Serializable> objectsIds = permisosHelper.getObjectsIdsWithPermission(
				OrganGestorEntity.class,
				permis);
		if (objectsIds == null || objectsIds.isEmpty()) {
			return new ArrayList<OrganGestorEntity>();
		} else {
			List<Long> objectsIdsTypeLong = new ArrayList<Long>();
			for (Serializable oid: objectsIds) {
				objectsIdsTypeLong.add((Long)oid);
			}
			return organGestorRepository.findByEntitatAndIds(entitat, objectsIdsTypeLong);
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
		} else if (RolHelper.isAdminOrgan(rolActual) && organGestorAmbPares.contains(organGestor)) {
			return true;
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
			ExpedientEntity expedient,
			OrganGestorEntity organGestor) {
		
		for (ExpedientOrganPareEntity expOrgPare : expedient.getOrganGestorPares()) {
			expedientOrganPareRepository.delete(expOrgPare);
		}
		expedient.removeOrganGestorPares();

	}

	public void afegirOrganGestorFillsIds(
			EntitatEntity entitat,
			List<Long> pares) {
		if (pares != null && !pares.isEmpty()) {
			pares.addAll(organGestorRepository.findFillsIds(
					entitat,
					pares));
		}
	}

	public void afegirMetaExpedientOrganGestorFillsIds(
			EntitatEntity entitat,
			List<Long> pares) {
		pares.addAll(metaExpedientOrganGestorRepository.findFillsIds(
				entitat,
				pares));
	}
	
	
	public List<OrganGestorEntity> findArrelFills(
			EntitatEntity entitat,
			String filtre) {
		OrganGestorEntity organGestorEntitat = organGestorRepository.findByEntitatAndCodi(
				entitat,
				entitat.getUnitatArrel());
		List<OrganGestorEntity> organsGestors = organGestorRepository.findByEntitatAndFiltreAndPareIdIn(
				entitat,
				filtre == null,
				filtre != null ? filtre.trim() : "",
				Arrays.asList(organGestorEntitat.getId()));
//		for (OrganGestorEntity organGestorEntity : organsGestors) {
//			logger.info("organ: "+ organGestorEntity.getId() +"  "+ organGestorEntity.getCodi() + " - " + organGestorEntity.getNom());
//		}
		
		organsGestors.remove(organGestorEntitat);
		organsGestors.add(0, organGestorEntitat);
		return organsGestors;
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
		OrganGestorEntity organGestor = organGestorRepository.findOne(organGestorId);
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
				avisRepository.delete(avisosSinc);
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

		EntitatEntity entitat = entitatRepository.findOne(entitatId);
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
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoTitol(msg("unitat.synchronize.titol.transicio", obsoleteUnitat.getCodi(), obsoleteUnitat.getNom())).infoText(infoText).build());
			progres.setProgres(22 + (nombreUnitatsProcessades++ * 5 / nombreUnitatsTotal));
		}

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitat.getId(), OrganGestorHelper.ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.delete(avisosSinc);
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
			ContingutEntity contingut = contingutRepository.findOne(contingutId);
			if (contingut != null) {
				ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
				organCodi =  deproxied.getExpedientPare().getOrganGestor().getCodi();
			}
		}
		return organCodi;
	}
	
	@Transactional
	public String getOrganCodiFromAnnexId(Long annexId) {
		RegistreAnnexEntity annexEntity = registreAnnexRepository.findById(annexId);
		return annexEntity.getRegistre().getDestiCodi();
	}
	
	
	@Transactional
	public String getOrganCodiFromMetaDocumentId(Long metaDocumentId) {
		String organCodi = null;
		MetaDocumentEntity metaDocument = metaDocumentRepository.findOne(metaDocumentId);
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
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
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
    			OrganGestorEntity organ = organGestorRepository.findByCodi(unitatOrganitzativa.getCodi());
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
						.gestioDirect(false)
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
				unitat.addNou(nova);
				nova.addAntic(unitat);
			}
		}
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
