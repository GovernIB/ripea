package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.dto.ActualitzacioInfo;
import es.caib.ripea.core.api.dto.ActualitzacioInfo.ActualitzacioInfoBuilder;
import es.caib.ripea.core.api.dto.AvisNivellEnumDto;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.dto.TipusTransicioEnumDto;
import es.caib.ripea.core.entity.AvisEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientOrganPareEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.repository.AvisRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientOrganPareRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class OrganGestorHelper {

	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
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

	public void consultaCanvisOrganigrama(EntitatEntity entitat) {

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

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
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

		// Històrics
		nombreUnitatsProcessades = 0;
		for (UnitatOrganitzativa unidadWS : unitatsWs) {
			OrganGestorEntity unitat = organGestorRepository.findByEntitatAndCodi(entitat, unidadWS.getCodi());
			sincronizarHistoricsUnitat(unitat, unidadWS, entitat);
			progres.setProgres(12 + (nombreUnitatsProcessades++ * 10 / nombreUnitatsTotal));
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoText("Desats històrics de l''òrgan '" + unitat.getCodi() + " - " + unitat.getNom() + "'").build());
		}
		progres.setProgres(22);

		// Definint tipus de transició
		obsoleteUnitats.addAll(organGestorRepository.findByEntitatNoVigent(entitat));
		nombreUnitatsProcessades = 0;
		nombreUnitatsTotal = obsoleteUnitats.size();
		for (OrganGestorEntity obsoleteUnitat : obsoleteUnitats) {
			String infoText = "Calculant tipus de transició de l''òrgan '" + obsoleteUnitat.getCodi() + " - " + obsoleteUnitat.getNom() + "': ";
			if (obsoleteUnitat.getNous().size() > 1) {
				obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.DIVISIO);
				organsDividits.add(obsoleteUnitat);
				infoText += "DIVISIÓ (" + obsoleteUnitat.getCodi() + " --> [" + organsToCodiList(obsoleteUnitat.getNous()) + "])";
			} else {
				if (obsoleteUnitat.getNous().size() == 1) {
					if (obsoleteUnitat.getNous().get(0).getAntics().size() > 1) {
						obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.FUSIO);
						organsFusionats.add(obsoleteUnitat);
						infoText += "FUSIÓ ([" + organsToCodiList(obsoleteUnitat.getNous().get(0).getAntics()) + "] --> " + obsoleteUnitat.getCodi() + ")";
					} else if (obsoleteUnitat.getNous().get(0).getAntics().size() == 1) {
						obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.SUBSTITUCIO);
						organsSubstituits.add(obsoleteUnitat);
						infoText += "SUBSTITUCIÓ (" + obsoleteUnitat.getCodi() + " --> " + obsoleteUnitat.getNous().get(0).getCodi() + ")";
					}
				} else {
					obsoleteUnitat.setTipusTransicio(TipusTransicioEnumDto.EXTINCIO);
					infoText += "EXTINCIÓ";
				}
			}
			progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoText(infoText).build());
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
			entitat.setDataSincronitzacio(ara);
		}
		entitat.setDataActualitzacio(ara);

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

	private ActualitzacioInfo sincronizarUnitat(UnitatOrganitzativa unitatWS, EntitatEntity entitat,Map<String, List<String>> organsParesPendentsAssignar) {
		ActualitzacioInfoBuilder infoBuilder = ActualitzacioInfo.builder().isOrgan(true);
		OrganGestorEntity unitat = null;
		if (unitatWS != null) {
			infoBuilder.infoTitol("Actualitzant unitat '" + unitatWS.getCodi() + "'");
			// checks if unitat already exists in database
			unitat = organGestorRepository.findByCodi(unitatWS.getCodi());
			// TODO: El pare potser encara no existeix. Per tant hem de comprovar si s'ha assignat, i si no, assignar-ho al crear el pare
			OrganGestorEntity organPare = organGestorRepository.findByEntitatAndCodi(entitat, unitatWS.getCodiUnitatSuperior());
			// if not it creates a new one
			if (unitat == null) {
				infoBuilder.isNew(true).codiOrgan(unitatWS.getCodi()).nomNou(unitatWS.getDenominacio()).estatNou(OrganGestorEntity.getEstat(unitatWS.getEstat()));
				// Venen les unitats ordenades, primer el pare i després els fills?
				unitat = OrganGestorEntity.getBuilder(unitatWS.getCodi())
						.entitat(entitat)
						.nom(unitatWS.getDenominacio())
						.pare(organPare)
						.estat(unitatWS.getEstat())
						.gestioDirect(false)
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
				infoBuilder.isNew(false).codiOrgan(unitatWS.getCodi())
						.nomAntic(unitat.getNom()).estatAntic(unitat.getEstat())
						.nomNou(unitatWS.getDenominacio()).estatNou(OrganGestorEntity.getEstat(unitatWS.getEstat()));
				unitat.update(unitatWS.getDenominacio(), unitatWS.getEstat(), organPare);
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteExtingitsNoUtilitzats(List<OrganGestorEntity> obsoleteUnitats, ProgresActualitzacioDto progres) {
		// Eliminar organs no vigents no utilitzats??
		int nombreUnitatsTotal = obsoleteUnitats.size();
		int nombreUnitatsProcessades = 0;

		Iterator<OrganGestorEntity> it = obsoleteUnitats.iterator();
		while (it.hasNext()) {
			OrganGestorEntity organObsolet = it.next();
			progres.setProgres(75 + (nombreUnitatsProcessades++ * 24)/nombreUnitatsTotal);

			Integer nombreProcediments = metaExpedientOrganGestorRepository.countByOrganGestor(organObsolet);
			if (nombreProcediments > 0) {
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoText("No ha estat possible esborrar l''òrgan gestor '" + organObsolet.getCodi() + "' degut a que té " + nombreProcediments + " procediments.").build());
				continue;
			}
			Integer nombreExpedients = expedientRepository.countByOrganGestor(organObsolet);
			if (nombreExpedients > 0) {
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoText("No ha estat possible esborrar l''òrgan gestor '" + organObsolet.getCodi() + "' degut a que té " + nombreExpedients + " expedients.").build());
				continue;
			}
			try {
				permisosHelper.eliminarPermisosOrgan(organObsolet);
				organGestorRepository.delete(organObsolet);
				progres.addInfo(ActualitzacioInfo.builder().hasInfo(true).infoText("L''òrgan gestor '" + organObsolet.getCodi() + "' ha estat esborrat.").build());
			} catch (Exception ex) {
				logger.error("No ha estat possible esborrar l'òrgan gestor.", ex);
				progres.addInfo(ActualitzacioInfo.builder().hasError(true).errorText("No ha estat possible esborrar l''òrgan gestor '" + organObsolet.getCodi() + "': " + ex.getMessage()).build());
			}
		}
	}
}
