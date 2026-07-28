package es.caib.ripea.service.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import es.caib.ripea.persistence.entity.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.repository.DadaRepository;
import es.caib.ripea.persistence.repository.DocumentNotificacioRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientTascaRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
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
import io.micrometer.core.instrument.Timer;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TascaHelper {

	@Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired private ExpedientTascaRepository expedientTascaRepository;
	@Autowired private MetaDadaRepository metaDadaRepository;
	@Autowired private DadaRepository dadaRepository;
	@Autowired private MetaDocumentRepository metaDocumentRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private EventService eventService;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private ApplicationHelper applicationHelper;
	@Autowired private PluginHelper pluginHelper;

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
								if (docExp.getMetaDocument()!=null && docExp.getMetaDocument().getId().equals(validacioTasca.getItemId())) {
									switch (validacioTasca.getTipusValidacio()) {
									case AP:
										//S'ha trobat un document del tipus definit a la validació, no fa falta validar res més
										validacioOk = true;
										break;
									case AP_FI:
										if (docExp.isFirmat()) { validacioOk = true; }
										break;
									case AP_FI_NI:
										DocumentNotificacioEstatEnumDto darreraNot_I = documentNotificacioRepository.findLastEstatNotificacioByDocumentId(docExp.getId());
										if (darreraNot_I!=null) { validacioOk = true; }
										break;
									case AP_FI_NF:
										DocumentNotificacioEstatEnumDto darreraNot_F = documentNotificacioRepository.findLastEstatNotificacioByDocumentId(docExp.getId());
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

	public boolean shouldNotifyAboutDeadline(Date expedientTascaDataLimit) {

		try {

			boolean shouldNotifyAboutDeadline = false;
			int preavisDataLimitEnDies = configHelper.getAsInt(PropertyConfig.TASCA_PREAVIS_DATA_LIMIT, 3);

			if (expedientTascaDataLimit != null) {
				if ((new Date()).after(new DateTime(expedientTascaDataLimit).minusDays(preavisDataLimitEnDies).toDate())) {
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

		Calendar c = Calendar.getInstance();
		c.setTime(expedientTascaEntity.getDataLimit());

		//Si no ha canviat res en el DTO respecte del entity (info a BBDD), no fer cap acció
		if (Utils.sonValorsDiferentsControlantNulls(expedientTascaEntity.getDataLimit(), dataLimit) ||
			Utils.sonValorsDiferentsControlantNulls(expedientTascaEntity.getDuracio(), duracio)) {
			expedientTascaEntity.updateDataLimit(dataLimit);
			expedientTascaEntity.setDuracio(duracio);
			emailHelper.enviarEmailModificacioDataLimitTasca(expedientTascaEntity);
		}

		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CANVI_DATALIMIT_TASCA, c.getTime());

		pluginHelper.comandaTascaSend(expedientTascaEntity);

		return expedientTascaEntity;
	}

	private void logAccioTasca(
			ExpedientTascaEntity expedientTascaEntity,
			LogTipusEnumDto tipusLog) {
		logAccioTasca(expedientTascaEntity, tipusLog, null, null, null, null);
	}

	private void logAccioTasca(
			ExpedientTascaEntity expedientTascaEntity,
			LogTipusEnumDto tipusLog,
			Date dataLimitAnterior) {
		logAccioTasca(expedientTascaEntity, tipusLog, null, null, dataLimitAnterior, null);
	}

	private void logAccioTasca(
			ExpedientTascaEntity expedientTascaEntity,
			LogTipusEnumDto tipusLog,
			TascaEstatEnumDto tascaEstat) {
		logAccioTasca(expedientTascaEntity, tipusLog, null, null, null, tascaEstat);
	}

	private void logAccioTasca(
			ExpedientTascaEntity expedientTascaEntity,
			LogTipusEnumDto tipusLog,
			List<UsuariEntity> responsablesAnteriors,
			UsuariEntity delegatAnterior,
			Date dataLimitAnterior,
			TascaEstatEnumDto tascaEstatAnterior) {

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

			String parametreCanviTasca1 = "";
			String parametreCanviTasca2 = "";

			try {

				switch (tipusLog) {
				case CANVI_ESTAT:
					parametreCanviTasca1 = tascaEstatAnterior!=null?tascaEstatAnterior.toString():"Sense estat anterior";
					parametreCanviTasca2 = expedientTascaEntity.getEstat()!=null?expedientTascaEntity.getEstat().toString():"Sense estat actual";
					break;
				case DELEGAR_TASCA:
					parametreCanviTasca1 = expedientTascaEntity.getDelegat()!=null?expedientTascaEntity.getDelegat().getCodiAndNom():"Sense delegat.";
					if (expedientTascaEntity.getComentaris()!=null && expedientTascaEntity.getComentaris().size()>0) {
						parametreCanviTasca2 = expedientTascaEntity.getComentaris().get(expedientTascaEntity.getComentaris().size()-1).getText();
					} else {
						parametreCanviTasca2 = "No s'ha indicat cap comentari.";
					}
					break;
				case CANVI_RESPONSABLES:
					parametreCanviTasca1 = responsablesToString(responsablesAnteriors);
					parametreCanviTasca2 = responsablesToString(expedientTascaEntity.getResponsables());
					break;
				case CANCELAR_DELEGACIO_TASCA:
					parametreCanviTasca1 = delegatAnterior!=null?delegatAnterior.getCodiAndNom():"Sense delegat anterior.";
					parametreCanviTasca2 = expedientTascaEntity.getDelegat()!=null?expedientTascaEntity.getDelegat().getCodiAndNom():"Sense delegat actual.";
					break;
				case CANVI_DATALIMIT_TASCA:
					parametreCanviTasca1 = new SimpleDateFormat("dd/MM/yyyy").format(dataLimitAnterior);
					parametreCanviTasca2 = new SimpleDateFormat("dd/MM/yyyy").format(expedientTascaEntity.getDataLimit());
					break;
				case CREACIO:
					parametreCanviTasca1 = expedientTascaEntity.getTitol()!=null?expedientTascaEntity.getTitol():expedientTascaEntity.getMetaTasca().getNom();
					parametreCanviTasca2 = responsablesToString(expedientTascaEntity.getResponsables());
					break;
				default:
					parametreCanviTasca1 = expedientTascaEntity.getTitol()!=null?expedientTascaEntity.getTitol():expedientTascaEntity.getMetaTasca().getNom();
					parametreCanviTasca2 = expedientTascaEntity.getResponsableActual()!=null?expedientTascaEntity.getResponsableActual().getCodi():"Sense responsable actual.";
					break;
				}
			} catch (Exception e) {}

			contingutLogHelper.logTasca(expedientTascaEntity.getId(), tipusLog, parametreCanviTasca1, parametreCanviTasca2);
	}

	private String responsablesToString(List<UsuariEntity> responsables) {
	    if (responsables == null || responsables.isEmpty()) {
	        return "";
	    }
	    return responsables.stream()
	            .map(u -> u.getCodi() + " - " + u.getNom())
	            .collect(Collectors.joining(", "));
	}

	public ExpedientTascaEntity createTasca(Long entitatId, Long expedientId, ExpedientTascaDto expedientTasca) {
    	Timer.Sample sample = Timer.start(applicationHelper.getMeterRegistry());
    	try {
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
				UsuariEntity responsable = usuariRepository.findById(responsableCodi).orElse(null);
				if (responsable==null) throw new NotFoundException(responsableCodi, UsuariEntity.class);
				responsables.add(responsable);
			}

			List<UsuariEntity> observadors = new ArrayList<UsuariEntity>(); // Per coneixement

			if (expedientTasca.getObservadorsCodi() != null) {
				for (String observadorCodi : expedientTasca.getObservadorsCodi()) {
					UsuariEntity observador = usuariRepository.findById(observadorCodi).orElse(null);
					if (observador==null) throw new NotFoundException(observadorCodi, UsuariEntity.class);
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

			pluginHelper.comandaTascaSend(expedientTascaEntity);

			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Expedient.createTasca", "resultado", "exito");

    		return expedientTascaEntity;
    	} catch (Exception e) {
    		applicationHelper.stopTimer(sample, "METRICS@Subsystem_Expedient.createTasca", "resultado", "error");
			throw e;
    	}
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
			UsuariEntity responsable = usuariRepository.findById(responsableCodi).orElse(null);
			if (responsable==null) throw new NotFoundException(responsableCodi, UsuariEntity.class);
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

		pluginHelper.comandaTascaSend(expedientTascaEntity);

		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CANCELAR_DELEGACIO_TASCA, null, delegat, null, null);
		return expedientTascaEntity;
	}

	public ExpedientTascaEntity delegarTasca(Long expedientTascaId, String delegatCodi, String comentari) {

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		UsuariEntity delegat = usuariRepository.findById(delegatCodi).orElse(null);
		if (delegat==null) throw new NotFoundException(delegatCodi, UsuariEntity.class);
		expedientTascaEntity.updateDelegat(delegat);

		if (comentari != null) {
			ExpedientTascaComentariEntity comentariTasca = ExpedientTascaComentariEntity.getBuilder(expedientTascaEntity, comentari).build();
			expedientTascaEntity.addComentari(comentariTasca);
		}

		emailHelper.enviarEmailDelegarTasca(expedientTascaEntity);
		cacheHelper.evictCountTasquesPendents(delegat.getCodi());

		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(List.of(delegat.getCodi()));

		pluginHelper.comandaTascaSend(expedientTascaEntity);

		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.DELEGAR_TASCA);
		return expedientTascaEntity;
	}

	public ExpedientTascaEntity reassignarTasca(Long expedientTascaId, List<String> responsablesCodi) {

		ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.getOne(expedientTascaId);
		List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
		List<UsuariEntity> responsablesAnteriors = new ArrayList<>(expedientTascaEntity.getResponsables());
		for (String responsableCodi : responsablesCodi) {
			UsuariEntity responsable = usuariRepository.findById(responsableCodi).orElse(null);
			if (responsable==null) throw new NotFoundException(responsableCodi, UsuariEntity.class);
			responsables.add(responsable);
		}

		expedientTascaEntity.updateResponsables(responsables);
		emailHelper.enviarEmailReasignarResponsableTasca(expedientTascaEntity);
		for (UsuariEntity responsable : expedientTascaEntity.getResponsables()) {
			cacheHelper.evictCountTasquesPendents(responsable.getCodi());
		}

		//Notificar event als usuaris afectats
		eventService.notifyTasquesPendents(expedientTascaEntity.getResponsablesAndObservadorsCodis(false));

		pluginHelper.comandaTascaSend(expedientTascaEntity);

		logAccioTasca(expedientTascaEntity, LogTipusEnumDto.CANVI_RESPONSABLES, responsablesAnteriors, null, null, null);

		return expedientTascaEntity;
	}

	public ExpedientTascaEntity canviarEstatTasca(Long tascaId, TascaEstatEnumDto tascaEstat, String motiu, String rolActual) {

		Timer.Sample sample = Timer.start(applicationHelper.getMeterRegistry());

		try {

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UsuariEntity responsableActual = usuariRepository.findById(auth.getName()).orElse(null);
			if (responsableActual==null) throw new NotFoundException(auth.getName(), UsuariEntity.class);
			ExpedientTascaEntity tascaEntity = expedientTascaRepository.getOne(tascaId);
			TascaEstatEnumDto tascaEstatAnterior = tascaEntity.getEstat();

			try {
				tascaEntity = comprovarTasca(tascaId);
			} catch (Exception e) {
				contingutHelper.comprovarContingutDinsExpedientModificable(
					tascaEntity.getExpedient().getEntitat().getId(),
					tascaEntity.getExpedient().getId(),
					false,
					true,
					false,
					false,
					false,
					true,
					rolActual);
			}

			if (tascaEstat == TascaEstatEnumDto.REBUTJADA) {
				tascaEntity.updateRebutjar(motiu);
			} else {
				tascaEntity.updateEstat(tascaEstat);
			}

			if (tascaEstat == TascaEstatEnumDto.FINALITZADA || tascaEstat == TascaEstatEnumDto.CANCELLADA || tascaEstat == TascaEstatEnumDto.REBUTJADA) {
				tascaEntity.updateDelegat(null);
			}

			if (tascaEstat == TascaEstatEnumDto.INICIADA) {
				tascaEntity.updateResponsableActual(responsableActual);
			}

			ExpedientEntity expedientEntity = tascaEntity.getExpedient();

			if (tascaEstat == TascaEstatEnumDto.FINALITZADA && tascaEntity.getMetaTasca().getEstatFinalitzarTasca() != null) {
				expedientEntity.updateEstatAdditional(tascaEntity.getMetaTasca().getEstatFinalitzarTasca());
			}

			// Tornar a l'estat inicial 'OBERT' si no hi ha un estat addicional en finalitzar tasca
			if (tascaEstat == TascaEstatEnumDto.FINALITZADA
				&& tascaEntity.getMetaTasca().getEstatFinalitzarTasca() == null
				&& expedientEntity.getEstatAdditional() != null) {
				expedientEntity.updateEstatAdditional(null);
			}

			emailHelper.enviarEmailCanviarEstatTasca(tascaEntity, tascaEstatAnterior);

			for (UsuariEntity responsable : tascaEntity.getResponsables()) {
				cacheHelper.evictCountTasquesPendents(responsable.getCodi());
			}

			if (tascaEntity.getObservadors() != null) {
				for (UsuariEntity observador : tascaEntity.getObservadors()) {
					cacheHelper.evictCountTasquesPendents(observador.getCodi());
				}
			}

			//Notificar event als usuaris afectats
			eventService.notifyTasquesPendents(tascaEntity.getResponsablesAndObservadorsCodis(true));

			pluginHelper.comandaTascaSend(tascaEntity);

			logAccioTasca(tascaEntity, LogTipusEnumDto.CANVI_ESTAT, tascaEstatAnterior);

			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Expedient.canviEstatTasca", "resultado", "exito");

			return tascaEntity;

		} catch (Exception e) {
			applicationHelper.stopTimer(sample, "METRICS@Subsystem_Expedient.canviEstatTasca", "resultado", "error");
			throw e;
		}
	}

    @Transactional
    public MetaExpedientTascaEntity moveTo(
        Long entitatId,
        Long metaExpedientId,
        Long tascaId,
        int posicio,
        String rolActual) throws NotFoundException {

        // Obtenir la tasca específica
        MetaExpedientTascaEntity tasca = metaExpedientTascaRepository.findById(tascaId)
            .orElseThrow(() -> new NotFoundException("Tasca no trobada: " + tascaId, MetaExpedientTascaEntity.class));

        // Obtenir totes les tasques del mateix procediment ordenades
        List<MetaExpedientTascaEntity> tasques = metaExpedientTascaRepository
            .findByMetaExpedientOrderByOrdreAsc(tasca.getMetaExpedient());

        // Trobar l'índex actual
        int anteriorIndex = -1;
        for (int i = 0; i < tasques.size(); i++) {
            if (tasques.get(i).getId().equals(tasca.getId())) {
                anteriorIndex = i;
                break;
            }
        }

        if (anteriorIndex == -1) {
            throw new NotFoundException("La tasca no pertany a la llista del procediment", MetaExpedientTascaEntity.class);
        }

        // Reordenar la llista a memòria
        tasques.add(posicio, tasques.remove(anteriorIndex));

        // Actualitzar el camp ordre de cada entitat
        for (int i = 0; i < tasques.size(); i++) {
            tasques.get(i).setOrdre(i);
        }

        return tasca;
    }

    /**
    * Crea automàticament les tasques del procediment marcades com "actives" i "inicialitzar automàticament" quan es crea un expedient.
    * Creació silenciosa: no envia emails, no genera comentaris ni notifica events.
    * Cada tasca comença (dataInici) on acaba (dataLimit) la tasca anterior; la primera comença avui.
    */
    @Transactional
    public void crearTasquesExpedient(ExpedientEntity expedient) {

        MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();

        List<MetaExpedientTascaEntity> metaTasques = metaExpedientTascaRepository
            .findByMetaExpedientAndActivaTrueOrderByOrdreAsc(metaExpedient);

        if (metaTasques == null || metaTasques.isEmpty()) {
            return;
        }

        // Usuari de creació de l'expedient
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuariEntity usuariCreador = usuariRepository.findById(auth.getName()).orElse(null);

        int diesPerDefecte = configHelper.getAsInt(PropertyConfig.TASCA_DURACIO_DEFAULT, 10);

        // Data d'inici de la primera tasca de la cadena: avui
        Date dataIniciSeguent = new Date();

        for (MetaExpedientTascaEntity metaTasca : metaTasques) {

            if (!metaTasca.isInicialitzarAutomaticament()) {
                continue;
            }

            // Responsable: el de la meta-tasca (procediment) si existeix, sinó l'usuari creador de l'expedient
            UsuariEntity responsable = null;
            if (metaTasca.getResponsable() != null && !metaTasca.getResponsable().isEmpty()) {
                responsable = usuariRepository.findById(metaTasca.getResponsable()).orElse(null);
            }
            if (responsable == null) {
                responsable = usuariCreador;
            }

            List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
            if (responsable != null) {
                responsables.add(responsable);
            }

            // Durada: la de la meta-tasca si en té, sinó dies per defecte de configuració
            Integer duracio = metaTasca.getDuracio();
            int diesDuracio = (duracio != null) ? duracio : diesPerDefecte;

            Date dataInici = dataIniciSeguent;

            Calendar cal = Calendar.getInstance();
            cal.setTime(dataInici);
            cal.add(Calendar.DAY_OF_YEAR, diesDuracio);
            Date dataLimit = cal.getTime();

            ExpedientTascaEntity expedientTascaEntity = ExpedientTascaEntity.getBuilder(
                expedient,
                metaTasca,
                responsables,
                new ArrayList<UsuariEntity>(), // observadors
                dataLimit,
                metaTasca.getDescripcio(), // titol
                diesDuracio,
                metaTasca.getPrioritat(),
                null // observacions
            ).build();

            // Com que el builder assigna dataInici=new Date() interalment, la corregim aquí a la data de la cadena
            expedientTascaEntity.updateDataInici(dataInici);

            expedientTascaRepository.save(expedientTascaEntity);

            // La següent tasca de la cadena començarà on acaba aquesta
            dataIniciSeguent = dataLimit;
        }
    }
}
