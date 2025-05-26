package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientTascaDto;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.utils.Utils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TascaHelper {
	
	@Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired private ExpedientTascaRepository expedientTascaRepository;
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private DadaRepository dadaRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private DocumentNotificacioRepository documentNotificacioRepository;
	
	@Autowired private EventService eventService;
	
	@Autowired private EntityComprovarHelper entityComprovarHelper;	
	@Autowired private ConfigHelper configHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private CacheHelper cacheHelper;	

	public List<MetaExpedientTascaValidacioDto> getValidacionsPendentsTasca(Long expedientTascaId) {
		List<MetaExpedientTascaValidacioDto> resultat = new ArrayList<MetaExpedientTascaValidacioDto>();
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		List<MetaExpedientTascaValidacioEntity> validacionsTasca = expedientTascaEntity.getMetaTasca().getValidacions();
		
		if (validacionsTasca!=null && validacionsTasca.size()>0) {
			
			List<DadaEntity> dadesExpedient = dadaRepository.findByNode(expedientTascaEntity.getExpedient());
			List<DocumentEntity> documentsExpedient = documentRepository.findByExpedientAndEsborrat(expedientTascaEntity.getExpedient(), 0);
			
			for (MetaExpedientTascaValidacioEntity validacioTasca: validacionsTasca) {
				
				if (validacioTasca.isActiva()) {
			
					boolean validacioOk = false;
					
					if (ItemValidacioTascaEnum.DADA.equals(validacioTasca.getItemValidacio())) {
						
						//La mateixa funció s'utilitza per guardar els valors de la pipella de dades del expedient.					
						MetaDadaEntity metaDadaProcediment = metaDadaRepository.findById(validacioTasca.getItemId()).orElse(null);
						
						if (metaDadaProcediment == null || !metaDadaProcediment.isActiva()) {
							validacioOk = true; //Si la meta-dada no esta activa actualment al procediment, no es valida perque no es podrá aportar...
						} else {
							for (DadaEntity dadaExp: dadesExpedient) {
								if (dadaExp.getMetaDada().getId().equals(validacioTasca.getItemId())) {
									switch (validacioTasca.getTipusValidacio()) {
									case AP:
										if (Utils.hasValue(dadaExp.getValorComString())) {
											validacioOk = true;
										}
										break;
									default:
										break;
									}
								}
							}
						}
						
					} else if (ItemValidacioTascaEnum.DOCUMENT.equals(validacioTasca.getItemValidacio())) {
						
						//Anam a cercar la dada del expedient, del tipus (metaDocumentId) igual al itemId de la validació
						MetaDocumentEntity metaDocProcediment = metaDocumentRepository.findById(validacioTasca.getItemId()).orElse(null);
						
						if (metaDocProcediment==null || !metaDocProcediment.isActiu()) {
							validacioOk = true; //Si el tipus de document no esta actiu acualment al procediment, no es valida perque no es podrá aportar...
						} else {
							for (DocumentEntity docExp: documentsExpedient) {
								if (docExp.getMetaDocument().getId().equals(validacioTasca.getItemId())) {
									switch (validacioTasca.getTipusValidacio()) {
									case AP:
										//S'ha trobat un document del tipus definit a la validació, no fa falta validar res més
										validacioOk = true;
										break;
									case AP_FI:
										if (docExp.isFirmat()) { validacioOk = true; }
										break;
									case AP_FI_NI:
										DocumentNotificacioEstatEnumDto darreraNot_I = documentNotificacioRepository.findLastEstatNotificacioByDocument(docExp);
										if (darreraNot_I!=null) { validacioOk = true; }
										break;
									case AP_FI_NF:
										DocumentNotificacioEstatEnumDto darreraNot_F = documentNotificacioRepository.findLastEstatNotificacioByDocument(docExp);
										if (DocumentNotificacioEstatEnumDto.FINALITZADA.equals(darreraNot_F) || 
											DocumentNotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(darreraNot_F)) { 
												validacioOk = true;
										}
										break;
									default:
										break;
									}
								}
							}
						}
					}
					
					if (!validacioOk) {
						resultat.add(conversioTipusHelper.convertir(validacioTasca, MetaExpedientTascaValidacioDto.class));
					}
				}
			}
		}		
		
		return resultat;
	}
	
	public boolean shouldNotifyAboutDeadline(ExpedientTascaEntity expedientTascaEntity) {

		try {

			boolean shouldNotifyAboutDeadline = false;
			int preavisDataLimitEnDies = configHelper.getAsInt(PropertyConfig.TASCA_PREAVIS_DATA_LIMIT, 3);

			if (expedientTascaEntity.getDataLimit() != null) {
				if ((new Date()).after(new DateTime(expedientTascaEntity.getDataLimit()).minusDays(preavisDataLimitEnDies).toDate())) {
					shouldNotifyAboutDeadline = true;
				}
			}

			return shouldNotifyAboutDeadline;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public ExpedientTascaEntity comprovarTasca(Long expedientTascaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ExpedientTascaEntity tasca = expedientTascaRepository.findById(expedientTascaId).orElse(null);
		
		if (tasca == null)
			throw new NotFoundException(expedientTascaId, ExpedientTascaEntity.class);
		
		if (tasca.getResponsables() != null) {
			boolean pemitted = false;
			for (UsuariEntity responsable : tasca.getResponsables()) {
				if (responsable.getCodi().equals(auth.getName())) {
					pemitted = true;
				}
			}
			UsuariEntity delegat = tasca.getDelegat();
			if (delegat != null && delegat.getCodi().equals(auth.getName())) {
				pemitted = true;
			}
			if (!pemitted) {
				throw new SecurityException("Sense permisos per accedir la tasca ("
						+ "tascaId=" + tasca.getId() + ", "
						+ "usuari=" + auth.getName() + ")");
			}
		}
		
		return tasca;
	}

	public ExpedientTascaEntity updateDataLimit(Long tascaId, Date dataLimit, Integer duracio) {
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(tascaId);

		//Si no ha canviat res en el DTO respecte del entity (info a BBDD), no fer cap acció
		if (Utils.sonValorsDiferentsControlantNulls(expedientTascaEntity.getDataLimit(), dataLimit) ||
			Utils.sonValorsDiferentsControlantNulls(expedientTascaEntity.getDuracio(), duracio)) {
			expedientTascaEntity.updateDataLimit(dataLimit);
			expedientTascaEntity.setDuracio(duracio);
			emailHelper.enviarEmailModificacioDataLimitTasca(expedientTascaEntity);
		}
		
		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CANVI_DATALIMIT_TASCA);
		
		return expedientTascaEntity;
	}
	
	public void logAccioTasca(ExpedientTascaEntity expedientTascaEntity, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
			expedientTascaEntity.getExpedient(),
			LogTipusEnumDto.MODIFICACIO,
			expedientTascaEntity,
			LogObjecteTipusEnumDto.TASCA,
			tipusLog,
			expedientTascaEntity.getMetaTasca().getNom(),
			expedientTascaEntity.getComentaris().size() == 1 ? expedientTascaEntity.getComentaris().get(0).getText() : null, // expedientTascaEntity.getComentari(),
			false,
			false);
	}
	
	public ExpedientTascaEntity createTasca(Long entitatId, Long expedientId, ExpedientTascaDto expedientTasca) {
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				null);

		MetaExpedientTascaEntity metaExpedientTascaEntity = metaExpedientTascaRepository.getOne(expedientTasca.getMetaExpedientTascaId());
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi : expedientTasca.getResponsablesCodi()) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}

		List<UsuariEntity> observadors = new ArrayList<UsuariEntity>(); // Per coneixement

		if (expedientTasca.getObservadorsCodi() != null) {
			for (String observadorCodi : expedientTasca.getObservadorsCodi()) {
				UsuariEntity observador = usuariHelper.getUsuariByCodiDades(observadorCodi, true, true);
				observadors.add(observador);
			}
		}

		ExpedientTascaEntity expedientTascaEntity = ExpedientTascaEntity.getBuilder(
			expedient,
			metaExpedientTascaEntity,
			responsables,
			observadors,
			expedientTasca.getDataLimit(),
			expedientTasca.getTitol(),
			expedientTasca.getDuracio(),
			expedientTasca.getPrioritat(),
			expedientTasca.getObservacions()).build();

		if (expedientTasca.getComentari() != null && !expedientTasca.getComentari().isEmpty()) {
			ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, expedientTasca.getComentari()).build();
			expedientTascaEntity.addComentari(comentari);
		}

		String titol = expedientTasca.getTitol();
		String observacions = expedientTasca.getObservacions();
		boolean isTitolNotEmtpy = titol != null && !titol.isEmpty();
		boolean isObservacionsNotEmpty = observacions != null && !observacions.isEmpty();

		if (isTitolNotEmtpy || isObservacionsNotEmpty) {
			String comentariTitol = (isTitolNotEmtpy ? "Títol: " + titol + "\n" : "") +
				(isObservacionsNotEmpty ? "\tObservacions: " + observacions + "\n" : "");

			ExpedientTascaComentariEntity comentari = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentariTitol).build();
			expedientTascaEntity.addComentari(comentari);
		}
		if (metaExpedientTascaEntity.getEstatCrearTasca() != null) {
			expedient.updateEstatAdditional(metaExpedientTascaEntity.getEstatCrearTasca());
		}

		for (String responsableCodi : expedientTasca.getResponsablesCodi()) {
			cacheHelper.evictCountTasquesPendents(responsableCodi);
		}

		if (expedientTasca.getObservadorsCodi() != null) {
			for (String observadorCodi : expedientTasca.getObservadorsCodi()) {
				cacheHelper.evictCountTasquesPendents(observadorCodi);
			}
		}
		expedientTascaRepository.save(expedientTascaEntity);
		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CREACIO);
		emailHelper.enviarEmailCanviarEstatTasca(expedientTascaEntity, null);
		
		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(expedientTascaEntity.getResponsablesAndObservadorsCodis(true));
		
		return expedientTascaEntity;
	}
	
	public ExpedientTascaEntity reobrirTasca(
			Long expedientTascaId,
			List<String> responsablesCodi,
			String motiu,
			String rolActual) {
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		if (motiu != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, motiu).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi : responsablesCodi) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}
		expedientTascaEntity.updateResponsables(responsables);
		canviarEstatTasca(expedientTascaId, TascaEstatEnumDto.PENDENT, motiu, rolActual);
		return expedientTascaEntity;
	}
	
	public ExpedientTascaEntity retomarTasca(Long expedientTascaId, String comentari) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		UsuariEntity delegat = expedientTascaEntity.getDelegat();
		expedientTascaEntity.updateDelegat(null);
		if (comentari != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentari).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}
		emailHelper.enviarEmailCancelarDelegacioTasca(expedientTascaEntity, delegat, comentari);
		cacheHelper.evictCountTasquesPendents(auth.getName());
		
		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(List.of(auth.getName()));
		
		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CANCELAR_DELEGACIO_TASCA);
		return expedientTascaEntity;
	}
	
	public ExpedientTascaEntity delegarTasca(Long expedientTascaId, String delegatCodi, String comentari) {
		
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		UsuariEntity delegat = usuariHelper.getUsuariByCodiDades(delegatCodi, true, true);
		expedientTascaEntity.updateDelegat(delegat);

		if (comentari != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentari).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}

		emailHelper.enviarEmailDelegarTasca(expedientTascaEntity);
		cacheHelper.evictCountTasquesPendents(delegat.getCodi());
		
		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(List.of(delegat.getCodi()));
		
		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.DELEGAR_TASCA);
		return expedientTascaEntity;
	}
	
	public ExpedientTascaEntity reassignarTasca(Long expedientTascaId, List<String> responsablesCodi) {
	
		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		for (String responsableCodi : responsablesCodi) {
			UsuariEntity responsable = usuariHelper.getUsuariByCodiDades(responsableCodi, true, true);
			responsables.add(responsable);
		}

		expedientTascaEntity.updateResponsables(responsables);
		emailHelper.enviarEmailReasignarResponsableTasca(expedientTascaEntity);
		for (UsuariEntity responsable : expedientTascaEntity.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());
		}
		
		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(expedientTascaEntity.getResponsablesAndObservadorsCodis(false));
		
		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CANVI_RESPONSABLES);
		return expedientTascaEntity;
	}
	
	public ExpedientTascaEntity canviarEstatTasca(Long tascaId, TascaEstatEnumDto tascaEstat, String motiu, String rolActual) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity responsableActual = usuariHelper.getUsuariByCodiDades(auth.getName(), true, true);
		ExpedientTascaEntity tasca = expedientTascaRepository.getOne(tascaId);

		try {
			tasca = comprovarTasca(tascaId);
		} catch (Exception e) {
			contingutHelper.comprovarContingutDinsExpedientModificable(
				tasca.getExpedient().getEntitat().getId(),
				tasca.getExpedient().getId(),
				false,
				true,
				false,
				false,
				false,
				true,
				rolActual);
		}

		TascaEstatEnumDto tascaEstatAnterior = tasca.getEstat();

		if (tascaEstat == TascaEstatEnumDto.REBUTJADA) {
			tasca.updateRebutjar(motiu);
		} else {
			tasca.updateEstat(tascaEstat);
		}

		if (tascaEstat == TascaEstatEnumDto.FINALITZADA || tascaEstat == TascaEstatEnumDto.CANCELLADA || tascaEstat == TascaEstatEnumDto.REBUTJADA) {
			tasca.updateDelegat(null);
		}

		if (tascaEstat == TascaEstatEnumDto.INICIADA) {
			tasca.updateResponsableActual(responsableActual);
		}

		ExpedientEntity expedientEntity = tasca.getExpedient();

		if (tascaEstat == TascaEstatEnumDto.FINALITZADA && tasca.getMetaTasca().getEstatFinalitzarTasca() != null) {
			expedientEntity.updateEstatAdditional(tasca.getMetaTasca().getEstatFinalitzarTasca());
		}

		// Tornar a l'estat inicial 'OBERT' si no hi ha un estat addicional en finalitzar tasca
		if (tascaEstat == TascaEstatEnumDto.FINALITZADA
			&& tasca.getMetaTasca().getEstatFinalitzarTasca() == null
			&& expedientEntity.getEstatAdditional() != null) {
			expedientEntity.updateEstatAdditional(null);
		}

		emailHelper.enviarEmailCanviarEstatTasca(tasca, tascaEstatAnterior);

		for (UsuariEntity responsable : tasca.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());
		}
		
		if (tasca.getObservadors() != null) {
			for (UsuariEntity observador : tasca.getObservadors()) {
				cacheHelper.evictCountTasquesPendents(observador.getCodi());
			}
		}

		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(tasca.getResponsablesAndObservadorsCodis(true));
		
		logAccioTasca(tasca, LogTipusEnumDto.CANVI_ESTAT);
		
		return tasca;
	}
}
