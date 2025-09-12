package es.caib.ripea.service.service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.DetallSalut;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.comanda.ms.salut.model.Manual;
import es.caib.comanda.ms.salut.model.MissatgeSalut;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.AvisService;
import es.caib.ripea.service.intf.service.SalutService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalutServiceImpl implements SalutService{

	private final AplicacioService aplicacioService;
	private final AvisService avisService;
	private final EntitatRepository entitatRepository;
	private final MeterRegistry meterRegistry;
	private final JdbcTemplate jdbcTemplate;
	
	@Override
	public List<IntegracioInfo> getIntegracions() {
		List<IntegracioInfo> integracions = new ArrayList<IntegracioInfo>();
		integracions.add(new IntegracioInfo(IntegracioApp.PFI.toString(), IntegracioApp.PFI.getNom())); //portafirmes
		integracions.add(new IntegracioInfo(IntegracioApp.ARX.toString(), IntegracioApp.ARX.getNom())); //Arxiu
		integracions.add(new IntegracioInfo(IntegracioApp.GDC.toString(), IntegracioApp.GDC.getNom())); //Gestor documental
		integracions.add(new IntegracioInfo(IntegracioApp.PBL.toString(), IntegracioApp.PBL.getNom())); //PINBAL
		integracions.add(new IntegracioInfo(IntegracioApp.DIS.toString(), IntegracioApp.DIS.getNom())); //DISTRIBUCIO
		integracions.add(new IntegracioInfo(IntegracioApp.USR.toString(), IntegracioApp.USR.getNom())); //Gestió d'usuaris
		integracions.add(new IntegracioInfo(IntegracioApp.CDO.toString(), IntegracioApp.CDO.getNom())); //Conversió de documents
		integracions.add(new IntegracioInfo(IntegracioApp.DIR.toString(), IntegracioApp.DIR.getNom())); //Dades externes
		integracions.add(new IntegracioInfo(IntegracioApp.NOT.toString(), IntegracioApp.NOT.getNom())); //NOTIB
		if (Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.FIRMA_BIOMETRICA_ACTIVA))) {
			integracions.add(new IntegracioInfo(IntegracioApp.VIF.toString(), IntegracioApp.VIF.getNom())); //Via Firma
		}
		integracions.add(new IntegracioInfo(IntegracioApp.DIB.toString(), IntegracioApp.DIB.getNom())); //Digitalització
		integracions.add(new IntegracioInfo(IntegracioApp.VFI.toString(), IntegracioApp.VFI.getNom())); //Validació firma
		integracions.add(new IntegracioInfo(IntegracioApp.RSC.toString(), IntegracioApp.RSC.getNom())); //ROLSAC
		return integracions;
	}

	@Override
	public List<AppInfo> getSubsistemes() {
		List<AppInfo> subsistemes = new ArrayList<AppInfo>();
		subsistemes.add(AppInfo.builder().codi("EXP").nom("Tramitació d'expedients").build());
		subsistemes.add(AppInfo.builder().codi("PRC").nom("Gestió de procediments").build());
		subsistemes.add(AppInfo.builder().codi("MAS").nom("Accions massives").build());
		subsistemes.add(AppInfo.builder().codi("CPF").nom("Callback PORTAFIB").build());
		subsistemes.add(AppInfo.builder().codi("CNB").nom("Callback NOTIB").build());
		subsistemes.add(AppInfo.builder().codi("CDI").nom("Callback DISTRIBUCIO").build());
		subsistemes.add(AppInfo.builder().codi("GDO").nom("Gestió documental FileSystem").build());
		return subsistemes;
	}

	@Override
	public List<ContextInfo> getContexts(String baseUrl) {
        return List.of(
                ContextInfo.builder()
                        .codi("BACK")
                        .nom("Backoffice")
                        .path(baseUrl + "/ripeaback")
                        .manuals(List.of(
                                Manual.builder().nom("Manual d'usuari").path("https://github.com/GovernIB/ripea/raw/ripea-1.0-dev/doc/pdf/01_ripea_manual_usuari.pdf").build(),
                                Manual.builder().nom("Manual d'administració").path("https://github.com/GovernIB/ripea/raw/ripea-1.0-dev/doc/pdf/02_ripea_manual_administradors.pdf").build()))
                        .build(),
                ContextInfo.builder()
                        .codi("INT")
                        .nom("API interna")
                        .path(baseUrl + "/ripeaapi/interna")
                        .api(baseUrl + "/ripeaapi/interna/rest")
                        .build(),
                ContextInfo.builder()
                        .codi("EXT")
                        .nom("API externa")
                        .path(baseUrl + "/ripeaapi/externa")
                        .api(baseUrl + "/ripeaapi/externa/rest")
                        .build()
        );
	}

	@Override
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		
		Timer timer = meterRegistry.find("ExpedientHelper.create").tags("resultado", "exito").timer();
		long numExpCreats = 0;
		double tempsPromitgExpCreat = 0;
		
		if (timer != null) {
			numExpCreats = timer.count(); // número de ejecuciones
//			double totalTime = timer.totalTime(TimeUnit.MILLISECONDS); // tiempo total
			tempsPromitgExpCreat = timer.mean(TimeUnit.MILLISECONDS); // tiempo promedio
		}
		
		//Estats de salut particulars
		EstatSalut salutDb = checkDatabase();		
		List<IntegracioSalut> salutIntegracions = new ArrayList<IntegracioSalut>();
		List<SubsistemaSalut> subsistemesSalut = new ArrayList<SubsistemaSalut>();
		List<DetallSalut> salutAltres = checkAltres(); 		//Comparar amb MonitorSystemController
		List<MissatgeSalut> missatgesSalut = checkMissatges();
		
		//Estat de salut general (deben de tots els altres)
		EstatSalutEnum estat = EstatSalutEnum.UP;
		EstatSalut estatSalut = EstatSalut.builder()
                .estat(estat)
                .latencia((int)tempsPromitgExpCreat)
                .build();
		
        return SalutInfo.builder()
                .codi("RIP")
                .versio(versio)
                .data(new Date())
                .estat(estatSalut)
                .bd(salutDb)
                .integracions(salutIntegracions)
                .subsistemes(subsistemesSalut)
                .altres(salutAltres)
                .missatges(missatgesSalut)
                .build();
	}
	
    private EstatSalut checkDatabase() {

        try {
            Instant start = Instant.now();
            jdbcTemplate.execute("SELECT COUNT(ID) FROM IPA_EXPEDIENT");
            Instant end = Instant.now();
            return EstatSalut.builder().estat(EstatSalutEnum.UP).latencia((int) Duration.between(start, end).toMillis()).build();
        } catch (Exception e) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    public List<DetallSalut> checkAltres() {

        try {
        	
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            // Nombre de cores (CPU)
            var availableProcessors = osBean.getAvailableProcessors();
            var os = osBean.getName() + " " + osBean.getVersion() + " (" + osBean.getArch() + ")";
        	
            // Càrrega de la CPU (només per la implementació de Sun)
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                var systemCpuLoad = sunOsBean.getSystemCpuLoad() * 100 + "%";
                var processCpuLoad = sunOsBean.getProcessCpuLoad() * 100 + "%";

                var totalSpace = 0L;
                var freeSpace = 0L;
                for (var root : File.listRoots()) {
                    if (root.getTotalSpace() > totalSpace) {
                        totalSpace = root.getTotalSpace();
                        freeSpace = root.getFreeSpace();
                    }
                }

                return List.of(
                        DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
                        DetallSalut.builder().codi("CPU").nom("Càrrega del sistema").valor(systemCpuLoad).build(),
                        DetallSalut.builder().codi("CPU").nom("Càrrega del procés").valor(processCpuLoad).build(),
                        DetallSalut.builder().codi("MED").nom("Memòria disponible").valor((Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : humanReadableByteCount(Runtime.getRuntime().maxMemory()))).build(),
                        DetallSalut.builder().codi("MET").nom("Memòria total").valor(humanReadableByteCount(Runtime.getRuntime().totalMemory())).build(),
                        DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(humanReadableByteCount(totalSpace)).build(),
                        DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(humanReadableByteCount(freeSpace)).build(),
                        DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build()
                );
            }
        } catch (Exception e2) {
            log.error("Salut: No s'ha pogut obtenir informació del sistema amb la implementació de Sun", e2);
        }
        return null;
    }
    
    public static String humanReadableByteCount(long bytes) {

        var unit = 1000;
        if (bytes < unit) {
            return bytes + " B";
        }
        var exp = (int) (Math.log(bytes) / Math.log(unit));
        var pre = "kMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    public List<MissatgeSalut> checkMissatges() {

    	List<MissatgeSalut> missatges = new ArrayList<>();
    	
    	try {
    	
	    	List<AvisDto> avisos = new ArrayList<AvisDto>();
	    	
	    	List<AvisDto> avisosUser = avisService.findActive();
	    	if (avisosUser!=null) {
	    		for (AvisDto avisUsuari: avisosUser) {
	    			avisUsuari.setAssumpte("Avis Usuari. Entitat "+avisUsuari.getEntitatId()+". Assumpte: "+avisUsuari.getAssumpte());
	    			avisos.add(avisUsuari);
	    		}
	    	}
	    	
	    	List<EntitatEntity> entitatsActives = entitatRepository.findByActiva(true);
	    	if (entitatsActives!=null) {
	    		for (EntitatEntity entitat: entitatsActives) {
	    			List<AvisDto> avisosAdmin = avisService.findActiveAdmin(entitat.getId());
	    			for (AvisDto avisAdmin: avisosAdmin) {
	    				avisAdmin.setAssumpte("Avis Admin. Entitat "+avisAdmin.getEntitatId()+". Assumpte: "+avisAdmin.getAssumpte());
		    			avisos.add(avisAdmin);
	    			}
	    		}
	    	}

	    	for (AvisDto avis: avisos) {
	    		MissatgeSalut ms = MissatgeSalut.builder()
	    				.data(avis.getDataInici())
	    				.missatge(avis.getAssumpte()+": "+avis.getMissatge())
	    				.nivell(avis.getAvisNivell().toString())
	    				.build();
	    		missatges.add(ms);
	    	}
    	} catch (Exception ex) {}

    	return missatges;
    }
}