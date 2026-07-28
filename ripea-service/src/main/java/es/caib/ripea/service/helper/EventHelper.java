package es.caib.ripea.service.helper;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.*;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.joda.time.DateTime;

import es.caib.ripea.service.intf.model.sse.AnotacionsPendentsEvent;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioChangedEvent;
import es.caib.ripea.service.intf.model.sse.FirmaEstatCanviatEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.NotificacioEstatCanviatEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.TasquesPendentsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class EventHelper {

    private static final int CONFIG_DIES_AVIS_EXPEDIENT_ANTIC_DEFAULT = 90;

	@Autowired private JmsTemplate jmsTemplate;
	@Autowired private AvisRepository avisRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
    @Autowired private ExpedientRepository expedientRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private EmailHelper emailHelper;
    @Autowired private ConfigHelper configHelper;
    @Autowired private MessageHelper messageHelper;
    @Autowired private ExpedientTascaRepository expedientTascaRepository;

    public void notifyAvisosActius() {
    	// El missatge d'avisos viatja buit (és només un trigger): la data la re-consulta el consumidor
    	// (handleEventAvisos -> getAvisosActiusPerUsuariCodi) en una transacció nova. Si publiquéssim el
    	// missatge dins la transacció encara oberta del canvi d'avís, el consumidor podria llegir l'estat
    	// antic (race condition). Per això diferim l'enviament a afterCommit, quan el canvi ja és visible.
    	TransactionAfterCommitUtils.run(() -> {
        	try {
    	        log.debug("notifyAvisosActius a clients");
    	        jmsTemplate.convertAndSend("avisos", new AvisosActiusEvent(null, null));
        	} catch (Exception ex) {
        		log.error("Error al notifyAvisosActius a clients", ex);
        	}
    	});
    }

    public void notifyAnotacionsPendents(Long anotacioId) {
    	notifyAnotacionsPendents(emailHelper.dadesUsuarisAfectatsAnotacio(anotacioId));
    }

    public void notifyAnotacionsPendents(List<UsuariAnotacioDto> usuarisAfectats) {
    	//Aquesta funció es crida desde EmailHelper. Notificam als mateixos que rebràn el mail. TODO: ¿eliminar enviament de mail?
    	//Grup, organ gestor i tenint en compte rols.
    	try {
    		log.debug("notifyAnotacionsPendents a clients");
    		Map<String, Long> anotacioUsuaris = new HashMap<String, Long>();
    		if (usuarisAfectats!=null) {
    			for (UsuariAnotacioDto usuari: usuarisAfectats) {
    				anotacioUsuaris.put(usuari.getCodi(), getAnotacionsPendents(usuari));
    			}
    		}
    		AnotacionsPendentsEvent resultat = new AnotacionsPendentsEvent(anotacioUsuaris);
    		jmsTemplate.convertAndSend("anotacions", resultat);
    	} catch (Exception ex) {
    		log.error("Error al notifyAnotacionsPendents a clients", ex);
    	}
    }

    public void notifyErrorsValidacio(ErrorsValidacioChangedEvent errors) {
    	try {
    		log.debug("notifyErrorsValidacio a expedient");
    		jmsTemplate.convertAndSend("errorsValidacioExp", errors);
    	} catch (Exception ex) {
    		log.error("Error al notifyErrorsValidacio a expedient", ex);
    	}
    }

    /**
     * Notifica als clients subscrits a l'expedient que ha canviat l'estat de firma d'un document.
     *
     * A diferència de notifyErrorsValidacio (que viatja amb les dades ja calculades dins la mateixa
     * transacció), aquest event és només un senyal: el client hi reacciona refrescant la graella, és a dir,
     * tornant a llegir de BBDD. Per això l'enviament es difereix a afterCommit, com a notifyAvisosActius;
     * si el publicàssim amb la transacció encara oberta, el refresc podria llegir l'estat anterior.
     */
    public void notifyFirmaEstatCanviat(FirmaEstatCanviatEvent firmaEvent) {
    	TransactionAfterCommitUtils.run(() -> {
        	try {
        		log.debug("notifyFirmaEstatCanviat a expedients suscrits");
        		jmsTemplate.convertAndSend("firmaEstat", firmaEvent);
        	} catch (Exception ex) {
        		log.error("Error al notifyFirmaEstatCanviat a expedients suscrits", ex);
        	}
    	});
    }

    /**
     * Notifica als clients subscrits a l'expedient que ha canviat l'estat d'una notificació.
     * Es difereix a afterCommit pel mateix motiu que notifyFirmaEstatCanviat.
     */
    public void notifyNotificacioEstatCanviat(NotificacioEstatCanviatEvent notificacioEvent) {
    	TransactionAfterCommitUtils.run(() -> {
        	try {
        		log.debug("notifyNotificacioEstatCanviat a expedients suscrits");
        		jmsTemplate.convertAndSend("notificacioEstat", notificacioEvent);
        	} catch (Exception ex) {
        		log.error("Error al notifyNotificacioEstatCanviat a expedients suscrits", ex);
        	}
    	});
    }

    public List<ValidacioErrorDto> getValidacionsInicialsExpedient(Long expedientId) {
    	return cacheHelper.findErrorsValidacioPerNode(expedientId);
    }

    public void notifyTasquesPendents(List<String> usuarisAfectats) {
    	try {
    		log.debug("notifyTasquesPendents a clients");
    		Map<String, Long> tasquesUsuaris = new HashMap<String, Long>();
    		if (usuarisAfectats!=null) {
    			for (String usuari: usuarisAfectats) {
    				tasquesUsuaris.put(usuari, getTasquesPendents(usuari));
    			}
    		}
    		TasquesPendentsEvent resultat = new TasquesPendentsEvent(tasquesUsuaris);
    		jmsTemplate.convertAndSend("tasques", resultat);
    	} catch (Exception ex) {
    		log.error("Error al notifyTasquesPendents a clients", ex);
    	}
    }

    public void notifyFluxFirmaFinalitzat(CreacioFluxFinalitzatEvent fluxEvent) {
    	try {
    		jmsTemplate.convertAndSend("flux", fluxEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFluxFirmaFinalitzat a expedients suscrits", ex);
    	}
    }

    public void notifyFluxFirmaCreat(CreacioFluxFinalitzatEvent fluxEvent) {
    	try {
    		jmsTemplate.convertAndSend("fluxCreatEditat", fluxEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFluxFirmaFinalitzat a expedients suscrits", ex);
    	}
    }

    public void notifyFirmaNavegadorFinalitzada(FirmaFinalitzadaEvent firmaEvent) {
    	try {
    		jmsTemplate.convertAndSend("firmaNavegadorFinalitzada", firmaEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFirmaNavegadorFinalitzada a usuaris suscrits", ex);
    	}
    }

    public void notifyScanFinalitzat(ScanFinalitzatEvent scanEvent) {
    	try {
    		jmsTemplate.convertAndSend("scan", scanEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyScanFinalitzat a expedients suscrits", ex);
    	}
    }

	public long getAnotacionsPendents(UsuariAnotacioDto usuariCodi) {
		try {
			EntitatEntity entitatEntity = null;
			if (usuariCodi.getEntitatId()!=null) {
				entitatEntity = entitatRepository.findById(usuariCodi.getEntitatId()).get();
			} else {
				entitatEntity = metaExpedientRepository.findById(usuariCodi.getMetaExpedientId()).get().getEntitat();
			}
			if (entitatEntity!=null) {

				OrganGestorEntity organGestorEntity = null;
				if (usuariCodi.getOrganId()!=null)
					organGestorEntity = organGestorRepository.findById(usuariCodi.getOrganId()).orElse(null);

				UsuariEntity usuari = usuariRepository.getOne(usuariCodi.getCodi());

				// El comptador depèn dels permisos ACL de l'usuari AFECTAT, que es resolen des de la
				// SecurityContext del fil. Aquest mètode es crida sovint des d'un context aliè
				// (WS de Distribució, un altre usuari acceptant/rebutjant...), de manera que cal
				// suplantar la identitat de l'usuari afectat perquè getObjectsIdsWithPermission
				// resolgui els SEUS permisos i no els del fil que dispara l'esdeveniment.
				Authentication authPrevia = SecurityContextHolder.getContext().getAuthentication();
				try {
					List<GrantedAuthority> authorities = usuari.getRolActual()!=null
							? Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority(usuari.getRolActual()))
							: Collections.<GrantedAuthority>emptyList();
					User principal = new User(usuari.getCodi(), "", authorities);
					SecurityContextHolder.getContext().setAuthentication(
							new UsernamePasswordAuthenticationToken(principal, null, authorities));

					return cacheHelper.countAnotacionsPendents(
							entitatEntity,
							usuari.getRolActual(),
							usuariCodi.getCodi(),
							organGestorEntity!=null?organGestorEntity.getId():null);
				} finally {
					SecurityContextHolder.getContext().setAuthentication(authPrevia);
				}
			}
		} catch (Exception ex) {}

		return 0l;
	}

	public long getTasquesPendents(String usuariCodi) {
		try {
			return cacheHelper.countTasquesPendents(usuariCodi);
		} catch (Exception ex) {
			return 0l;
		}
	}

    public AvisosActiusEvent getAvisosActiusEvent() {
        var avisosUsuari = conversioTipusHelper.convertirList(
                avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)),
                AvisDto.class);

        Map<String, List<AvisDto>> avisosAdmin = new HashMap<>();
        entitatRepository.findByActiva(true).forEach(entitat -> {
            var avisos = conversioTipusHelper.convertirList(
                    avisRepository.findActiveAdmin(DateUtils.truncate(new Date(), Calendar.DATE), entitat.getId()),
                    AvisDto.class);
            if (avisos != null && !avisos.isEmpty())
                avisosAdmin.put(entitat.getCodi(), avisos);
        });

        var event = AvisosActiusEvent.builder()
                .avisosUsuari(avisosUsuari)
                .avisosAdmin(avisosAdmin)
                .build();
        return event;
    }

	private Map<String, List<AvisDto>> obtenirAvisosAdmin() {
		Map<String, List<AvisDto>> avisosAdmin = new HashMap<>();
		entitatRepository.findByActiva(true).forEach(entitat -> {
			var avisos = conversioTipusHelper.convertirList(
					avisRepository.findActiveAdmin(DateUtils.truncate(new Date(), Calendar.DATE), entitat.getId()),
					AvisDto.class);
			if (avisos != null && !avisos.isEmpty())
				avisosAdmin.put(entitat.getCodi(), avisos);
		});
		return avisosAdmin;
	}

    /**
     * Calcula (sense persistir) un avís de tipus Warning si l'usuari actual té expedients agafats
     * de fa més de X dies (propietat es.caib.ripea.dies.avis.expedient.antic) amb tots els documents
     * obligatoris del procediment (multiplicitat M_1 / M_1_N) ja afegits.
     */
    private AvisDto getAvisExpedientsAntics(String usuariCodi) {
        if (usuariCodi == null) {
            return null;
        }
        try {
            UsuariEntity usuari = usuariRepository.findById(usuariCodi).orElse(null);
            if (usuari == null) {
                return null;
            }

            if (ConfigHelper.getEntitat() == null || ConfigHelper.getEntitat().get() == null) {
                return null;
            }
            Long entitatId = ConfigHelper.getEntitat().get().getId();

            int dies = configHelper.getAsInt(PropertyConfig.CONFIG_DIES_AVIS_EXPEDIENT_ANTIC, CONFIG_DIES_AVIS_EXPEDIENT_ANTIC_DEFAULT);
            LocalDateTime dataLimit = LocalDateTime.now().minusDays(dies);

            List<ExpedientEntity> expedientsAntics = expedientRepository.findExpedientsAntics(usuari, entitatId, dataLimit);
            if (expedientsAntics == null || expedientsAntics.isEmpty()) {
                return null;
            }

            List<GenericDto> expedientsDto = expedientsAntics.stream()
                .map(expedient -> {
                    GenericDto dto = new GenericDto();
                    dto.setId(expedient.getId());
                    dto.setTexte(expedient.getNom());
                    dto.setCodi("(" + expedient.getNumero() + ")");
                    return dto;
                }).collect(Collectors.toList());

            AvisDto avis = new AvisDto();
            avis.setAssumpte(messageHelper.getMessage("avis.expedients.antics.assumpte", new Object[]{expedientsAntics.size(), dies}));
            avis.setExpedients(expedientsDto);
            avis.setAvisNivell(AvisNivellEnumDto.WARNING);
            avis.setActiu(true);
            avis.setDataInici(new Date());
            avis.setId(-1L);
            return avis;
        } catch (Exception ex) {
            log.error("Error calculant l'avis d'expedients antics per l'usuari " + usuariCodi, ex);
            return null;
        }
    }

    @Transactional(readOnly = true)
	public AvisosActiusEvent getAvisosActiusPerUsuari(String rol, Long entitatId, String usuariCodi) {
		List<AvisDto> avisos;
		Date dataActual = DateUtils.truncate(new Date(), Calendar.DATE);

		if (BaseConfig.ROLE_SUPER.equals(rol)) {
			// Només avisos globals
			avisos = conversioTipusHelper.convertirList(
					avisRepository.findActiveGlobal(dataActual),AvisDto.class);
		} else {
			// Avisos globals + de l'entitat
			avisos = conversioTipusHelper.convertirList(
					avisRepository.findActivePerEntitat(dataActual, entitatId), AvisDto.class);
		}

        AvisDto avisExpedientsAntics = getAvisExpedientsAntics(usuariCodi);
        if (avisExpedientsAntics != null) {
            avisos.add(avisExpedientsAntics);
        }

        AvisDto avisTasquesDataLimit = getAvisTasquesDataLimitPropera(usuariCodi);
        if (avisTasquesDataLimit != null) {
            avisos.add(avisTasquesDataLimit);
        }

		return AvisosActiusEvent.builder()
				.avisosUsuari(avisos)
				.avisosAdmin(obtenirAvisosAdmin())
				.build();
	}

	/**
	 * Resol el rol i l'entitat actuals de l'usuari a partir del seu codi i retorna els avisos filtrats.
	 * Pensat per ser cridat des d'un fil de @JmsListener (sense petició web ni caller autenticat),
	 * de manera que el controller no hagi de fer una crida EJB addicional a findUsuariAmbCodi.
	 */
    @Transactional(readOnly = true)
    public AvisosActiusEvent getAvisosActiusPerUsuariCodi(String usuariCodi) {
		UsuariEntity usuari = usuariRepository.findById(usuariCodi).orElse(null);
		if (usuari == null) {
			return null;
		}
		Long entitatId = usuari.getEntitatActual() != null ? usuari.getEntitatActual().getId() : null;
		return getAvisosActiusPerUsuari(usuari.getRolActual(), entitatId, usuariCodi);
	}

    private static final TascaEstatEnumDto[] ESTATS_TASCA_PENDENT = {
        TascaEstatEnumDto.PENDENT, TascaEstatEnumDto.INICIADA, TascaEstatEnumDto.AGAFADA
    };

    /**
     * Calcula (sense persistir) un avís de tipus Warning si l'usuari actual té tasques assignades
     * (com a responsable, observador o delegat) on la data límit de les quals s'acosta, segons el
     * mateix criteri que TascaHelper.shouldNotifyAboutDeadline (propietat TASCA_PREAVIS_DATA_LIMIT).
     */
    private AvisDto getAvisTasquesDataLimitPropera(String usuariCodi) {
        if (usuariCodi == null) {
            return null;
        }
        try {
            UsuariEntity usuari = usuariRepository.findById(usuariCodi).orElse(null);
            if (usuari == null) {
                return null;
            }

            if (ConfigHelper.getEntitat() == null || ConfigHelper.getEntitat().get() == null) {
                return null;
            }
            Long entitatId = ConfigHelper.getEntitat().get().getId();

            List<ExpedientTascaEntity> tasquesAmbDataLimit = expedientTascaRepository.findTasquesAmbDataLimitProperaPerUsuari(
                usuari, ESTATS_TASCA_PENDENT, entitatId, new Date());

            if (tasquesAmbDataLimit == null || tasquesAmbDataLimit.isEmpty()) {
                return null;
            }

            List<ExpedientTascaEntity> tasquesProperes = tasquesAmbDataLimit.stream()
                .filter(tasca -> shouldNotifyAboutDeadline(tasca.getDataLimit()))
                .collect(Collectors.toList());

            if (tasquesProperes.isEmpty()) {
                return null;
            }

            List<GenericDto> expedientsDto = tasquesProperes.stream()
                .map(tasca -> {
                    GenericDto dto = new GenericDto();
                    dto.setId(tasca.getExpedient().getId());
                    String titol = tasca.getTitol() != null ? tasca.getTitol() : tasca.getMetaTasca().getNom();
                    dto.setTexte(titol + ": ");
                    dto.setCodi(tasca.getExpedient().getNomINumero());
                    return dto;
                }).collect(Collectors.toList());

            AvisDto avis = new AvisDto();
            avis.setAssumpte(messageHelper.getMessage("avis.tasques.datalimit.propera.assumpte",new Object[]{tasquesProperes.size()}));
            avis.setExpedients(expedientsDto);
            avis.setAvisNivell(AvisNivellEnumDto.WARNING);
            avis.setActiu(true);
            avis.setDataInici(new Date());
            avis.setId(-2L);
            return avis;
        } catch (Exception ex) {
            log.error("Error calculant l'avis de tasques amb data limit propera per l'usuari " + usuariCodi, ex);
            return null;
        }
    }

    private boolean shouldNotifyAboutDeadline(Date expedientTascaDataLimit) {
        try {
            if (expedientTascaDataLimit == null) {
                return false;
            }
            int preavisDataLimitEnDies = configHelper.getAsInt(PropertyConfig.TASCA_PREAVIS_DATA_LIMIT, 3);
            return new Date().after(new DateTime(expedientTascaDataLimit).minusDays(preavisDataLimitEnDies).toDate());
        } catch (Exception ex) {
            log.error("Error comprovant si cal notificar la data limit de la tasca", ex);
            return false;
        }
    }
}
