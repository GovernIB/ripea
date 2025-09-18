package es.caib.ripea.service.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.comanda.ms.salut.model.Manual;
import es.caib.comanda.ms.salut.model.MissatgeSalut;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.MetriquesRipeaInfoDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.AvisService;
import es.caib.ripea.service.intf.service.SalutService;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
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
	private final PluginHelper pluginHelper;
	
	//Guardam les dades del enviament anterior, d'aquesta manera podrem calcular la diferencia
	private static List<MetriquesRipeaInfoDto> dadesSalutRipea = new ArrayList<MetriquesRipeaInfoDto>();
	private static Map<String, Map<String, MetriquesRipeaInfoDto>> dadesIntegracionsEndpointsByIntegracioCodi = new HashMap<String, Map<String, MetriquesRipeaInfoDto>>();
	
	public static MetriquesRipeaInfoDto getDadesSalutRipeaByCodi(String codi) {
        for (MetriquesRipeaInfoDto dto : dadesSalutRipea) {
            if (dto.getCodi().equalsIgnoreCase(codi)) {
                return dto;
            }
        }
        return null;
	}
	
	public static MetriquesRipeaInfoDto getDadesIntegracioRipeaByCodiAndEndpoint(String codi, String endpoint) {
		if (dadesIntegracionsEndpointsByIntegracioCodi!=null) {
			Map<String, MetriquesRipeaInfoDto> aux = dadesIntegracionsEndpointsByIntegracioCodi.get(codi);
			if (aux!=null) return aux.get(endpoint);
		}
		return null;
	}
	
	public static void setDadesIntegracionsRipea(String codi, Map<String, MetriquesRipeaInfoDto> dadesIntegracionsRipeaActualitzades) {
		if (dadesIntegracionsEndpointsByIntegracioCodi==null) dadesIntegracionsEndpointsByIntegracioCodi = new HashMap<String, Map<String, MetriquesRipeaInfoDto>>();
        dadesIntegracionsEndpointsByIntegracioCodi.put(codi, dadesIntegracionsRipeaActualitzades);
	}
	
	public static long getDadesIntegracioOkPeriodeByCodiAndEndpoint(String codi, String endpoint, long totalsOk) {
		MetriquesRipeaInfoDto integracioPeticions = getDadesIntegracioRipeaByCodiAndEndpoint(codi, endpoint);
		if (integracioPeticions != null) {
			long resultat = totalsOk-integracioPeticions.getPeticionsOk();
			return resultat>0?resultat:0; //Si hi havia dades anteriors, retornam la resta (les del periode)
		}
		return totalsOk; //Si NO hi havia dades anteriors, retornam les que tenim
	}
	
	public static long getDadesIntegracioErrorPeriodeByCodiAndEndpoint(String codi, String endpoint, long totalsError) {
		MetriquesRipeaInfoDto integracioPeticions = getDadesIntegracioRipeaByCodiAndEndpoint(codi, endpoint);
		if (integracioPeticions != null) {
			long resultat = totalsError-integracioPeticions.getPeticionsError();
			return resultat>0?resultat:0; //Si hi havia dades anteriors, retornam la resta (les del periode)
		}
		return totalsError; //Si NO hi havia dades anteriors, retornam les que tenim
	}
	
	public static int getDadesIntegracioTempsMitgPeriodeByCodiAndEndpoint(String codi, String endpoint, long tempsMitgTotal) {
		MetriquesRipeaInfoDto integracioPeticions = getDadesIntegracioRipeaByCodiAndEndpoint(codi, endpoint);
		if (integracioPeticions != null) {
			long resultat = tempsMitgTotal-integracioPeticions.getTempsMitg();
			return (int) (resultat>0?resultat:0); //Si hi havia dades anteriors, retornam la resta (les del periode)
		}
		return (int) tempsMitgTotal; //Si NO hi havia dades anteriors, retornam les que tenim
	}
	
	public static void actualizarDadesSalutRipeaByCodi(String codi, long totalsOk, long totalsError) {
        MetriquesRipeaInfoDto dto = getDadesSalutRipeaByCodi(codi);
        if (dto != null) {
            dto.setPeticionsOk(totalsOk);
            dto.setPeticionsError(totalsError);
        } else {
        	MetriquesRipeaInfoDto nou = new MetriquesRipeaInfoDto();
        	nou.setCodi(codi);
        	nou.setPeticionsOk(totalsOk);
        	nou.setPeticionsError(totalsError);
            dadesSalutRipea.add(nou);
        }
    }
	
	public static long getDadesOkPeriodeByCodi(String codi, long totalsOk) {
		MetriquesRipeaInfoDto dto = getDadesSalutRipeaByCodi(codi);
		if (dto != null) {
			long resultat = totalsOk-dto.getPeticionsOk();
			return resultat>0?resultat:0; //Si hi havia dades anteriors, retornam la resta (les del periode)
		} else {
			return totalsOk; //Si NO hi havia dades anteriors, retornam les que tenim
		}
	}
	
	public static long getDadesErrorPeriodeByCodi(String codi, long totalsError) {
		MetriquesRipeaInfoDto dto = getDadesSalutRipeaByCodi(codi);
		if (dto != null) {
			long resultat = totalsError-dto.getPeticionsError();
			return resultat>0?resultat:0; //Si hi havia dades anteriors, retornam la resta (les del periode)
		} else {
			return totalsError; //Si NO hi havia dades anteriors, retornam les que tenim
		}
	}
	
	// --------------------FI METODES ESTATICS---------------- //
	
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
		subsistemes.add(AppInfo.builder().codi("SUB_EXP").nom("Tramitació d'expedients").build());
		subsistemes.add(AppInfo.builder().codi("SUB_PRC").nom("Gestió de procediments").build());
		subsistemes.add(AppInfo.builder().codi("SUB_MAS").nom("Accions massives").build());
		subsistemes.add(AppInfo.builder().codi("SUB_CPF").nom("Callback PORTAFIB").build());
		subsistemes.add(AppInfo.builder().codi("SUB_CNB").nom("Callback NOTIB").build());
		subsistemes.add(AppInfo.builder().codi("SUB_CDI").nom("Callback DISTRIBUCIO").build());
		subsistemes.add(AppInfo.builder().codi("SUB_GDO").nom("Gestió documental FileSystem").build());
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
		
		//Estats de salut particulars
		EstatSalut salutDb = checkDatabase();		
		List<IntegracioSalut> salutIntegracions = checkIntegracions();
		List<SubsistemaSalut> subsistemesSalut = checkSubsistemes();
		List<DetallSalut> salutAltres = checkAltres(); 		//Comparar amb MonitorSystemController
		List<MissatgeSalut> missatgesSalut = checkMissatges();
		
		//Estat de salut general (depen de tots els altres)
		EstatSalutEnum estat	= calculaEstatGlobal(salutIntegracions, subsistemesSalut);
		int latenciaGlobal 		= calculaLatenciaGlobal(salutDb, salutIntegracions, subsistemesSalut);
		
		EstatSalut estatSalut = EstatSalut.builder().estat(estat).latencia(latenciaGlobal).build();
		
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
	
	private EstatSalutEnum calculaEstatGlobal(List<IntegracioSalut> salutIntegracions, List<SubsistemaSalut> subsistemesSalut) {
		Long totalPeticionsOk		= 0l;
		Long totalPeticionsError	= 0l;
		if (salutIntegracions.size()>0) {
			for (IntegracioSalut is: salutIntegracions) {
				totalPeticionsOk = totalPeticionsOk + is.getPeticions().getTotalOk();
				totalPeticionsError = totalPeticionsError + is.getPeticions().getTotalError();
			}
		}
		if (subsistemesSalut.size()>0) {
			for (SubsistemaSalut ss: subsistemesSalut) {
				totalPeticionsOk = totalPeticionsOk + ss.getTotalOk();
				totalPeticionsError = totalPeticionsError + ss.getTotalError();
			}
		}
		return calculaEstat(totalPeticionsOk, totalPeticionsError);
	}
	
	private int calculaLatenciaGlobal(
			EstatSalut salutDb,
			List<IntegracioSalut> salutIntegracions,
			List<SubsistemaSalut> subsistemesSalut) {
		
		//JA començam amb les dades de la petició a BBDD
		Long totalPeticionsOk		= 1l;
		int totalLatenciaPeticionsOk= salutDb.getLatencia();
		
		if (salutIntegracions.size()>0) {
			for (IntegracioSalut is: salutIntegracions) {
				totalPeticionsOk = totalPeticionsOk + is.getPeticions().getTotalOk();
//				is.getPeticions().getPeticionsPerEntorn().get("").get
			}
		}
		if (subsistemesSalut.size()>0) {
			for (SubsistemaSalut ss: subsistemesSalut) {
				totalPeticionsOk = totalPeticionsOk + ss.getTotalOk();
			}
		}
		return (int)(totalLatenciaPeticionsOk/totalPeticionsOk);
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

    @SuppressWarnings("unused")
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
                        DetallSalut.builder().codi("CRS").nom("Càrrega del sistema").valor(systemCpuLoad).build(),
                        DetallSalut.builder().codi("CPR").nom("Càrrega del procés").valor(processCpuLoad).build(),
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
    
    private Map<String, IntegracioPeticions> getPeticionsPerEntorn(String codiIntegracio, String[] codesFS) {
    	
		List<String> endpoints = new ArrayList<String>();
		Map<String, IntegracioPeticions> peticionsPerEntorn = new HashMap<>();
		Map<String, MetriquesRipeaInfoDto> acumulacioMetriquesPerEndpoint = new HashMap<String, MetriquesRipeaInfoDto>();
		
		//Recorrem els diferents codis de metrica (per exemple: portafib, firmaServidor i firmaWeb, son de la mateixa integració)
    	for (String codiMetrica: codesFS) {
    		
    		//Obtenir els diferents endpoints de cada metrica de la integració
    		endpoints = getEndpointNames(codiMetrica);
    		
    		for (String endpoint: endpoints) {
    		
	    		Timer timerExpedientExito = meterRegistry.find(codiMetrica).tags("resultado", "exito", "endpoint", endpoint).timer();
	    		Timer timerExpedientError = meterRegistry.find(codiMetrica).tags("resultado", "error", "endpoint", endpoint).timer();
	    		long exitoAux	= timerExpedientExito!=null?timerExpedientExito.count():0;
	    		long errorAux	= timerExpedientError!=null?timerExpedientError.count():0;
	    		int promitgAux	= (int)(timerExpedientExito!=null?timerExpedientExito.mean(TimeUnit.MILLISECONDS):0);
	        	
	    		MetriquesRipeaInfoDto aux = acumulacioMetriquesPerEndpoint.get(endpoint)!=null
	    				?acumulacioMetriquesPerEndpoint.get(endpoint)
	    						:new MetriquesRipeaInfoDto();
	    		
	    		aux.setCodi(codiIntegracio);
	    		aux.setPeticionsError(aux.getPeticionsError()+errorAux);
	    		aux.setPeticionsOk(aux.getPeticionsOk()+exitoAux);
	    		//Acumulam promitjos de les peticions de diferents codis de metrica. Despres els haurem de dividir per el nombre total de codis.
	    		aux.setTempsMitg(aux.getTempsMitg()+promitgAux);

	    		//Anam recollint, per el mateix endpoint, les dades dels diferents codis de metrica que tenim per una integració
	    		acumulacioMetriquesPerEndpoint.put(endpoint, aux);
    		}
    	}
    	
    	//Recorrem la llista de peticions acumulades per endpoint dels diferents codis de metrica per la integració
    	//Transformam el mapa <String, MetriquesRipeaInfoDto> en el objecte de retorn <String, IntegracioPeticions>
    	for (Map.Entry<String, MetriquesRipeaInfoDto> entry : acumulacioMetriquesPerEndpoint.entrySet()) {
    		
    		IntegracioPeticions integracioPeticioEndpoint = new IntegracioPeticions();
        	integracioPeticioEndpoint.setEndpoint(entry.getKey());
        	
        	//DADES TOTALS DESDE EL INICI DE LA RECOLLIDA DE DADES (REINICI DEL SERVIDOR)
        	integracioPeticioEndpoint.setTotalOk(entry.getValue().getPeticionsOk()); //Total de Oks fins al moment actual per aquest endpoint
        	integracioPeticioEndpoint.setTotalError(entry.getValue().getPeticionsError()); //Total de errors fins al moment actual per aquest endpoint
        	//Promitg acumulat per el endpoint, de les diferents métriques
        	integracioPeticioEndpoint.setTotalTempsMig(entry.getValue().getTempsMitg()/codesFS.length);
        	
        	//DADES PARCIALS = DADES TOTALS - DARRERES DADES ENVIADES (VARIABLES STATIC)
        	integracioPeticioEndpoint.setPeticionsOkUltimPeriode(getDadesIntegracioOkPeriodeByCodiAndEndpoint(
        			codiIntegracio,
        			entry.getKey(),
        			entry.getValue().getPeticionsOk()));
        	integracioPeticioEndpoint.setPeticionsErrorUltimPeriode(getDadesIntegracioErrorPeriodeByCodiAndEndpoint(
        			codiIntegracio,
        			entry.getKey(),
        			entry.getValue().getPeticionsError()));
        	integracioPeticioEndpoint.setTempsMigUltimPeriode(getDadesIntegracioTempsMitgPeriodeByCodiAndEndpoint(
        			codiIntegracio,
        			entry.getKey(),
        			entry.getValue().getTempsMitg()));
        	
        	//Afegim al mapa de peticions per endpoint, les dades 
        	peticionsPerEntorn.put(entry.getKey(), integracioPeticioEndpoint);
    	}
    	
    	//Actualitzam les dades estátiques per la seguent petició
    	setDadesIntegracionsRipea(codiIntegracio, acumulacioMetriquesPerEndpoint);
    	
    	return peticionsPerEntorn;
    }
    
    private IntegracioSalut getIntegracioSalutAmbTotals(
    		String codiIntegracio,
    		IntegracioPeticions ip) {
    	//Cálcul de totals per les dades generals de IntegracioPeticions
		long integracio_total_exito		= 0;
		long integracio_total_error		= 0;
		int  integracio_temps_promitg	= 0;
    	String endpointBase = "";
    	long numUsos = 0;
    	if (ip.getPeticionsPerEntorn()!=null) {
    		//Sumam els valors totals dels diferents endpoints del codi de integració
	    	for (Map.Entry<String, IntegracioPeticions> entry : ip.getPeticionsPerEntorn().entrySet()) {
	    		ip.setTotalOk(ip.getTotalOk()+entry.getValue().getTotalOk());
	    		ip.setTotalError(ip.getTotalError()+entry.getValue().getTotalError());
	    		ip.setTotalTempsMig(ip.getTotalTempsMig()+entry.getValue().getTotalTempsMig());
	    		ip.setPeticionsOkUltimPeriode(ip.getPeticionsOkUltimPeriode()+entry.getValue().getPeticionsOkUltimPeriode());
	    		ip.setPeticionsErrorUltimPeriode(ip.getPeticionsErrorUltimPeriode()+entry.getValue().getPeticionsErrorUltimPeriode());
	    		ip.setTempsMigUltimPeriode(ip.getTempsMigUltimPeriode()+entry.getValue().getTempsMigUltimPeriode());
	    		//Com a endpoint base per el objecte principal de IntegracioSalut, ens quedam amb el que té mes usos
	    		if (entry.getValue().getPeticionsOkUltimPeriode()+entry.getValue().getPeticionsErrorUltimPeriode()>numUsos) {
	    			numUsos = entry.getValue().getPeticionsOkUltimPeriode()+entry.getValue().getPeticionsErrorUltimPeriode();
	    			endpointBase = ip.getEndpoint();
	    		}
	    	}
    	}
    	ip.setEndpoint(endpointBase);
    	
    	return IntegracioSalut.builder()
                .codi(codiIntegracio)
                .latencia(ip.getTotalTempsMig()/ip.getPeticionsPerEntorn().size())
                .estat(calculaEstat(ip.getTotalOk(), ip.getTotalError()))
                .peticions(ip)
                .build();
    }
    
    public List<IntegracioSalut> checkIntegracions() {
    	
    	//Timer.Sample sample = Timer.start(aplicacioService.getMeterRegistry());
    	//String endpoint = dadesUsuariPlugin.getEndpointURL();
    	//applicationHelper.stopTimer(sample, "METRICS@Integracions.dadesUsuari", "resultado", "exito", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/A");
    	//applicationHelper.stopTimer(sample, "METRICS@Integracions.dadesUsuari", "resultado", "error", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/A");
    	
    	List<IntegracioSalut> integracionsSalut = new ArrayList<>();
    	
		String[] codesFSUSR = {"METRICS@Integracions.dadesUsuari"};
		IntegracioPeticions ipUSR = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.USR.toString(), codesFSUSR));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.USR.toString(), ipUSR));
		
		String[] codesFSPFI = {
				"METRICS@Integracions.portafirmes",
				"METRICS@Integracions.portafirmesFlux",
				"METRICS@Integracions.firmaServidor",
				"METRICS@Integracions.firmaSimpleWeb"};
		IntegracioPeticions ipPFI = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.PFI.toString(), codesFSPFI));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.PFI.toString(), ipPFI));
    	
		String[] codesFSARX = {"METRICS@Integracions.arxiu"};
		IntegracioPeticions ipARX = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.ARX.toString(), codesFSARX)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.ARX.toString(), ipARX));
		
		String[] codesFSPBL = {"METRICS@Integracions.pinbal"};
		IntegracioPeticions ipPBL = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.PBL.toString(), codesFSPBL)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.PBL.toString(), ipPBL));

		String[] codesFSDIS = {"METRICS@Integracions.distribucio"};
		IntegracioPeticions ipDIS = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.DIS.toString(), codesFSDIS)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.DIS.toString(), ipDIS));
		
		String[] codesFSCDO = {"METRICS@Integracions.conversio"};
		IntegracioPeticions ipCDO = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.CDO.toString(), codesFSCDO));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.CDO.toString(), ipCDO));
		
		String[] codesFSDIR = {"METRICS@Integracions.dir3"}; //getUnitatsOrganitzativesPlugin, getDadesExternesPlugin
		IntegracioPeticions ipDIR = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.DIR.toString(), codesFSDIR));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.DIR.toString(), ipDIR));
		
		String[] codesFSNOT = {"METRICS@Integracions.notib"};
		IntegracioPeticions ipNOT = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.NOT.toString(), codesFSNOT)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.NOT.toString(), ipNOT));
		
		if (Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.FIRMA_BIOMETRICA_ACTIVA))) {
			String[] codesFSVIF = {"METRICS@Integracions.viafirma"};
			IntegracioPeticions ipVIF = new IntegracioPeticions();
			ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.VIF.toString(), codesFSVIF)); 
			integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.VIF.toString(), ipVIF));
		}
		
		String[] codesFSDIB = {"METRICS@Integracions.digitalitzacio"};
		IntegracioPeticions ipDIB = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.DIB.toString(), codesFSDIB));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.DIB.toString(), ipDIB));
		
		String[] codesFSVFI = {"METRICS@Integracions.validaFirma"};
		IntegracioPeticions ipVFI = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.VFI.toString(), codesFSVFI)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.VFI.toString(), ipVFI));
		
		String[] codesFSRSC = {"METRICS@Integracions.rolsac"}; //getProcedimentPlugin
		IntegracioPeticions ipRSC = new IntegracioPeticions();
		ipUSR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.RSC.toString(), codesFSRSC)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.RSC.toString(), ipRSC));
    	
    	return integracionsSalut;
    }
    
    private List<String> getEndpointNames(String codiMetrica) {
    	List<String> endpoints = new ArrayList<String>();
		for (Meter meter : meterRegistry.getMeters()) {
		    if (meter instanceof Timer && meter.getId().getName().startsWith(codiMetrica)) {
		    	if (meter.getId().getTags()!=null) {
			        for (Tag tag : meter.getId().getTags()) {
			        	if ("endpoint".equals(tag.getKey()) && !endpoints.contains(tag.getValue())) {
			        		endpoints.add(tag.getValue());
			        	}
			        }
		    	}
		    }
		}
		return endpoints;
    }
    
    public List<SubsistemaSalut> checkSubsistemes() {
    	
    	String jsonMetrics;
		try {
			jsonMetrics = aplicacioService.getMetriquesJSON();
	        InputStream contingut = new ByteArrayInputStream(jsonMetrics.getBytes(StandardCharsets.UTF_8));
	        String baseDir = aplicacioService.propertyFindByNom(PropertyConfig.GESDOC_PLUGIN_FILESYSTEM_PATH);
	        String agrupacio = "METRICS";
			if (baseDir.endsWith("/")) {
				baseDir = baseDir + agrupacio;
			} else {
				baseDir = baseDir + "/" + agrupacio;
			}
	    	pluginHelper.gestioDocumentalCreate("JSONSS", contingut);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	List<SubsistemaSalut> salutSubsistemes = new ArrayList<SubsistemaSalut>();
    	
    	/**
    	 * T R A M I T A C I O     E X P E D I E N T S
    	 */

    	String[] codesEXP = {"METRICS@Subsystem_Expedient.create"
    			, "METRICS@Subsystem_Expedient.list"
    			, "METRICS@Subsystem_Expedient.tasquesUserList"
    			, "METRICS@Subsystem_Expedient.createTasca"
    			, "METRICS@Subsystem_Expedient.canviEstatTasca"};
    	
		//TOTALS
		long subsistema_total_exito = 0;
		long subsistema_total_error = 0;
		int tempsPromitgSubsistema	= 0;
    	
    	for (String codiMetrica: codesEXP) {
    		Timer timerExpedientExito = meterRegistry.find(codiMetrica).tags("resultado", "exito").timer();
    		Timer timerExpedientError = meterRegistry.find(codiMetrica).tags("resultado", "error").timer();
    		long exitoAux	= timerExpedientExito!=null?timerExpedientExito.count():0;
    		long errorAux	= timerExpedientError!=null?timerExpedientError.count():0;
    		int promitgAux	= (int)(timerExpedientExito!=null?timerExpedientExito.mean(TimeUnit.MILLISECONDS):0);
    		//Afegir als totals
    		subsistema_total_exito = subsistema_total_exito + exitoAux;
    		subsistema_total_error = subsistema_total_error + errorAux;
    		tempsPromitgSubsistema = tempsPromitgSubsistema + promitgAux;
    	}
    	
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_EXP")
                .latencia(tempsPromitgSubsistema/codesEXP.length)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_EXP", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_EXP", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_EXP", subsistema_total_exito, subsistema_total_error);
		
    	/**
    	 * G E S T I O     P R O C E D I M E N T S
    	 */
    	String[] codesPRC = {"METRICS@Subsystem_Procediment.create"
    			, "METRICS@Subsystem_Procediment.list"
    			, "METRICS@Subsystem_Procediment.import"
    			, "METRICS@Subsystem_Procediment.metaDoc"
    			, "METRICS@Subsystem_Procediment.metaDada"
    			, "METRICS@Subsystem_Procediment.metaTasca"};
    	
		//TOTALS
		subsistema_total_exito = 0;
		subsistema_total_error = 0;
		tempsPromitgSubsistema = 0;
    	
    	for (String codiMetrica: codesPRC) {
    		Timer timerExpedientExito = meterRegistry.find(codiMetrica).tags("resultado", "exito").timer();
    		Timer timerExpedientError = meterRegistry.find(codiMetrica).tags("resultado", "error").timer();
    		long exitoAux	= timerExpedientExito!=null?timerExpedientExito.count():0;
    		long errorAux	= timerExpedientError!=null?timerExpedientError.count():0;
    		int promitgAux	= (int)(timerExpedientExito!=null?timerExpedientExito.mean(TimeUnit.MILLISECONDS):0);
    		//Afegir als totals
    		subsistema_total_exito = subsistema_total_exito + exitoAux;
    		subsistema_total_error = subsistema_total_error + errorAux;
    		tempsPromitgSubsistema = tempsPromitgSubsistema + promitgAux;
    	}
 		
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_PRC")
                .latencia(tempsPromitgSubsistema/codesPRC.length)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_PRC", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_PRC", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_PRC", subsistema_total_exito, subsistema_total_error);

    	/**
    	 * A C C I O N S     M A S S I V E S
    	 */
    	String[] codesMAS = {"METRICS@Subsystem_Background.userMassiveAction"
    			, "METRICS@Subsystem_Background.consultarIGuardarAnotacions"
    			, "METRICS@Subsystem_Background.guardarEnArxiuContingutsPendents"
    			, "METRICS@Subsystem_Background.enviarEmailsAgrupats"};
    	
		//TOTALS
		subsistema_total_exito = 0;
		subsistema_total_error = 0;
		tempsPromitgSubsistema = 0;
    	
    	for (String codiMetrica: codesMAS) {
    		Timer timerExpedientExito = meterRegistry.find(codiMetrica).tags("resultado", "exito").timer();
    		Timer timerExpedientError = meterRegistry.find(codiMetrica).tags("resultado", "error").timer();
    		long exitoAux	= timerExpedientExito!=null?timerExpedientExito.count():0;
    		long errorAux	= timerExpedientError!=null?timerExpedientError.count():0;
    		int promitgAux	= (int)(timerExpedientExito!=null?timerExpedientExito.mean(TimeUnit.MILLISECONDS):0);
    		//Afegir als totals
    		subsistema_total_exito = subsistema_total_exito + exitoAux;
    		subsistema_total_error = subsistema_total_error + errorAux;
    		tempsPromitgSubsistema = tempsPromitgSubsistema + promitgAux;
    	}
 		
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_MAS")
                .latencia(tempsPromitgSubsistema/codesMAS.length)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_MAS", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_MAS", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_MAS", subsistema_total_exito, subsistema_total_error);	
		
    	/**
    	 * F I L E     S Y S T E M
    	 */
		String[] codesFS = {"METRICS@Subsystem_FileSystem.create", "METRICS@Subsystem_FileSystem.get"};
    	
		//TOTALS
		subsistema_total_exito = 0;
		subsistema_total_error = 0;
		tempsPromitgSubsistema = 0;
    	
    	for (String codiMetrica: codesFS) {
    		Timer timerExpedientExito = meterRegistry.find(codiMetrica).tags("resultado", "exito").timer();
    		Timer timerExpedientError = meterRegistry.find(codiMetrica).tags("resultado", "error").timer();
    		long exitoAux	= timerExpedientExito!=null?timerExpedientExito.count():0;
    		long errorAux	= timerExpedientError!=null?timerExpedientError.count():0;
    		int promitgAux	= (int)(timerExpedientExito!=null?timerExpedientExito.mean(TimeUnit.MILLISECONDS):0);
    		//Afegir als totals
    		subsistema_total_exito = subsistema_total_exito + exitoAux;
    		subsistema_total_error = subsistema_total_error + errorAux;
    		tempsPromitgSubsistema = tempsPromitgSubsistema + promitgAux;
    	}
 		
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_GDO")
                .latencia(tempsPromitgSubsistema/codesFS.length)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_GDO", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_GDO", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_GDO", subsistema_total_exito, subsistema_total_error);	
		
    	/**
    	 * C A L L B A C K     D I S T R I B U C I O     (Event controlador)
    	 */
    	
		String code = "METRICS@Subsystem_Callback_Distribucio.event";
    	
		Timer timerSubsistemExito = meterRegistry.find(code).tags("resultado", "exito").timer();
		Timer timerSubsistemError = meterRegistry.find(code).tags("resultado", "error").timer();
		
		subsistema_total_exito = timerSubsistemExito!=null?timerSubsistemExito.count():0;
		subsistema_total_error = timerSubsistemError!=null?timerSubsistemError.count():0;
		
		tempsPromitgSubsistema = (int)(timerSubsistemExito!=null?timerSubsistemExito.mean(TimeUnit.MILLISECONDS):0);
    	
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_CDI")
                .latencia(tempsPromitgSubsistema)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_CDI", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_CDI", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_CDI", subsistema_total_exito, subsistema_total_error);	
		
    	/**
    	 * C A L L B A C K     N O T I B     (Event controlador)
    	 */
    	
    	code = "METRICS@Subsystem_Callback_Notib.notificaCanvi";
    	
		timerSubsistemExito = meterRegistry.find(code).tags("resultado", "exito").timer();
		timerSubsistemError = meterRegistry.find(code).tags("resultado", "error").timer();
		
		subsistema_total_exito = timerSubsistemExito!=null?timerSubsistemExito.count():0;
		subsistema_total_error = timerSubsistemError!=null?timerSubsistemError.count():0;
		
		tempsPromitgSubsistema = (int)(timerSubsistemExito!=null?timerSubsistemExito.mean(TimeUnit.MILLISECONDS):0);
    	
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_CNB")
                .latencia(tempsPromitgSubsistema)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_CNB", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_CNB", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_CNB", subsistema_total_exito, subsistema_total_error);
		
    	/**
    	 * C A L L B A C K     P O R T A F I B     (Event controlador)
    	 */
    	
    	code = "METRICS@Subsystem_Callback_Portafib.event";
    	
		timerSubsistemExito = meterRegistry.find(code).tags("resultado", "exito").timer();
		timerSubsistemError = meterRegistry.find(code).tags("resultado", "error").timer();
		
		subsistema_total_exito = timerSubsistemExito!=null?timerSubsistemExito.count():0;
		subsistema_total_error = timerSubsistemError!=null?timerSubsistemError.count():0;
		
		tempsPromitgSubsistema = (int)(timerSubsistemExito!=null?timerSubsistemExito.mean(TimeUnit.MILLISECONDS):0);
    	
		salutSubsistemes.add(SubsistemaSalut.builder()
                .codi("SUB_CPF")
                .latencia(tempsPromitgSubsistema)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(getDadesOkPeriodeByCodi("SUB_CPF", subsistema_total_exito))
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi("SUB_CPF", subsistema_total_error))
                .build());
		
		actualizarDadesSalutRipeaByCodi("SUB_CPF", subsistema_total_exito, subsistema_total_error);	
		
    	return salutSubsistemes;
    }

    private static EstatSalutEnum calculaEstat(Long totalPeticionsOk, Long totalPeticionsError) {
        
    	final long ok = (totalPeticionsOk != null) ? totalPeticionsOk : 0L;
        final long ko = (totalPeticionsError != null) ? totalPeticionsError : 0L;
        final long total = ok + ko;

        if (total==0) {
        	return EstatSalutEnum.UNKNOWN;
        }
        
        // Percentatge d'errors arrodonit correctament evitant divisió d'enters
        final int errorRatePct = (int) Math.round((ko * 100.0) / Math.max(1L, total));

        if (errorRatePct >= 100) {
        	return EstatSalutEnum.DOWN;
        } else if (errorRatePct > 30) {
        	return EstatSalutEnum.ERROR;
        } else if (errorRatePct > 10) {
        	return EstatSalutEnum.DEGRADED;
        } else if (errorRatePct < 5) {
        	return EstatSalutEnum.UP;
        } else {
            return EstatSalutEnum.WARN; // 5-10%
        }
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