package es.caib.ripea.core.helper;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.ProcesosInicialsEntity;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.ProcessosInicialsRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConfigService configService;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ExpedientRepository expedientRepository;
    @Autowired
    private ProcessosInicialsRepository processosInicialsRepository;
    @Autowired
    private ApplicationHelper applicationHelper;
    @Autowired
    private HistoricHelper historicHelper;
    @Autowired
    private GrupHelper grupHelper;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private GrupRepository grupRepository;

    public static int counter = 0;

    private Authentication auth;

    @Synchronized
    @Override 
    public void onApplicationEvent(ContextRefreshedEvent event) {

    	counter++;
    	
    	log.info("onApplicationEvent: " + counter);
    	
    	if (counter == 1) {
    		
    		log.info("Executant processos inicials.");
            addCustomAuthentication();
            try {
            	// ========================================================= EXECUTE PROCESS ONLY ONCE =================================================
                List<ProcesosInicialsEntity> processos = processosInicialsRepository.findProcesosInicialsEntityByInitTrue();
                for (ProcesosInicialsEntity proces : processos) {
                    log.info("Executant procés inicial: {}",  proces.getCodi());
                    switch (proces.getCodi()) {
                        case PROPIETATS_CONFIG_ENTITATS:
//                            configService.crearPropietatsConfigPerEntitats(); it must be executed on every startup because it must create configuration per entitats for properties added in the future
                            break;
                        case GENERAR_EXPEDIENT_NUMERO:
                        	generateNumeroAllExpedients();
                          break;          
                        case GENERAR_MISSING_HISTORICS:
                        	generateMissingHistorics();
                          break;            
                        case ORGANS_DESCARREGAR_NOM_CATALA:
                            organsDescarregarNomCatala();
                        case GRUPS_PERMISOS:
                        	crearPermisosDeGrupsJaCreats();
                          break;                              
                        default:
                            log.error("Procés inicial no definit");
                            break;
                    }
                    applicationHelper.setProcessAsProcessed(proces.getId());
                }
                
                // ===================================================== EXECUTE PROCESS ON EVERY STARTUP OF APPLICATION ==================================
                configService.crearPropietatsConfigPerEntitats();
                configService.actualitzarPropietatsJBossBdd();

                
                
            } catch (Exception ex) {
                log.error("Errror executant els processos inicials", ex);
            }
            restoreAuthentication();
			
		}
    }
    

    

    private void addCustomAuthentication() {

        auth = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = new Principal() {
            public String getName() {
                return "INIT";
            }
        };
        List<GrantedAuthority> rols = new ArrayList<>();
        rols.add(new SimpleGrantedAuthority("IPA_SUPER"));
        rols.add(new SimpleGrantedAuthority("IPA_ADMIN"));
        rols.add(new SimpleGrantedAuthority("tothom"));
        Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, "N/A", rols);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void restoreAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    

	public void generateMissingHistorics() {
    	
		try {
			
	    	log.info("Generating missing logs. Start");
	    	
//			Date day = DateHelper.getDay(21, 11, 2021);
//			computeData(day, HistoricTipusEnumDto.DIARI);
//	
//			Date month = DateHelper.getMonth(11, 2022);	
//			computeData(month, HistoricTipusEnumDto.MENSUAL);
	    	
	    	
//	    	delete from ipa_hist_expedient;
//	    	delete from ipa_hist_exp_interessat;
//	    	delete from ipa_hist_exp_usuari;
//	    	delete from ipa_historic;
	    	

	    	// generating historics for days 15/06/2022 - 13/09/2022
			Date day_15_06_2022 = DateHelper.getDay(15, 6, 2022);
			Date day_13_09_2022 = DateHelper.getDay(13, 9, 2022);
			List<Date> days = DateHelper.getDatesBetween(day_15_06_2022, day_13_09_2022);
			
			for (Date date : days) {
				if (historicHelper.checkIfHistoricsExist(date, HistoricTipusEnumDto.DIARI)) {
					log.info("Historics already exist for day: "  + DateHelper.getDayString(date));                        
				} else {
					log.info("Generating historics for day: " + DateHelper.getDayString(date));
					historicHelper.computeData(date, HistoricTipusEnumDto.DIARI);
				}
			}
			
			
			// generating historics for months 06/2022 - 08/2022
			Date month_06_2022 = DateHelper.getMonth(6, 2022);
			Date month_07_2022 = DateHelper.getMonth(7, 2022);
			Date month_08_2022 = DateHelper.getMonth(8, 2022);
			
			List<Date> months = Arrays.asList(month_06_2022, month_07_2022, month_08_2022);
			for (Date month : months) {
				if (historicHelper.checkIfHistoricsExist(month, HistoricTipusEnumDto.MENSUAL)) {
					log.info("Historics already exist for month: "  + DateHelper.getMonthString(month));                        
				} else {
					log.info("Generating historics for month: " + DateHelper.getMonthString(month));
					historicHelper.computeData(month, HistoricTipusEnumDto.MENSUAL);
				}
			}
			
			
	    	log.info("Generating missing logs. End");
		} catch (Exception e) {
			log.error("Error on generating missing logs", e);
		}
		
    }
    
	
    private void organsDescarregarNomCatala(){
    	try {
			organGestorHelper.organsDescarregarNomCatala();
		} catch (Exception e) {
			log.error("Error al descarregar noms catalans", e);
		}
    }
    
    private void crearPermisosDeGrupsJaCreats(){
    	try {
    		
    		List<GrupEntity> grups = grupRepository.findAll();
    		
    		for (GrupEntity grup : grups) {
        		PermisDto dto = new PermisDto();
        		dto.setRead(true);
        		dto.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
        		dto.setPrincipalNom(grup.getRol());
        		grupHelper.crearPermisosDeGrup(grup.getId(), dto);
			}
    		
		} catch (Exception e) {
			log.error("Error al afegir permisos als grups", e);
		}
    }
	
    
    private void generateNumeroAllExpedients(){
    	
		List<Long> ids = expedientRepository.findAllIdsNumeroNotNull();
		if (ids != null) {
			int i = 0;
			int size = ids.size();
			for (Long id : ids) {
				try {
					expedientHelper.generateNumeroExpedient(id);
					i++;
					if (i % 100 == 0) {
						log.info("Generant números per expedients: " + i + "/" + size + "...");
					}
				} catch (Exception e) {
					log.error("Error al generar numero expedient: " + id);
				}
				
			}
		}
    }
}
