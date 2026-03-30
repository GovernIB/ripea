package es.caib.ripea.service.service;

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

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.InformacioSistema;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.model.server.monitoring.Manual;
import es.caib.comanda.model.server.monitoring.MissatgeSalut;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SalutNivell;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.salut.helper.EstatHelper;
import es.caib.comanda.ms.salut.helper.IntegracioApp;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.ripea.persistence.entity.AvisEntity;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.AvisNivellEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioEnumDto;
import es.caib.ripea.service.intf.dto.MetriquesRipeaInfoDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.SalutService;
import es.caib.ripea.service.intf.utils.DateUtil;
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
	private final AvisRepository avisRepository;
	private final MeterRegistry meterRegistry;
	private final JdbcTemplate jdbcTemplate;
	private final ConfigHelper configHelper;
	
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
	
	public static int getDadesIntegracioTempsMitgPeriodeByCodiAndEndpoint(String codi, String endpoint, long tempsMitgTotal, long peticionsOkTotals) {
		MetriquesRipeaInfoDto integracioPeticions = getDadesIntegracioRipeaByCodiAndEndpoint(codi, endpoint);
		if (integracioPeticions != null && integracioPeticions.getPeticionsOk()>0) {
			return getPromitgPonderatInvers(tempsMitgTotal, peticionsOkTotals, integracioPeticions.getTempsMitg(), integracioPeticions.getPeticionsOk());
		}
		return (int) tempsMitgTotal; //Si NO hi havia dades anteriors, retornam les que tenim
	}
	
	public static void actualizarDadesSalutRipeaByCodi(String codi, long totalsOk, long totalsError, int tempsPromitg) {
        MetriquesRipeaInfoDto dto = getDadesSalutRipeaByCodi(codi);
        if (dto != null) {
            dto.setPeticionsOk(totalsOk);
            dto.setPeticionsError(totalsError);
            dto.setTempsMitg(tempsPromitg);
        } else {
        	MetriquesRipeaInfoDto nou = new MetriquesRipeaInfoDto();
        	nou.setCodi(codi);
        	nou.setPeticionsOk(totalsOk);
        	nou.setPeticionsError(totalsError);
        	nou.setTempsMitg(tempsPromitg);
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
	
	public static int getDadesTempsPromitgPeriodeByCodi(String codi, int tempsPromitgTotal, long peticionsOkTotals) {
		MetriquesRipeaInfoDto dto = getDadesSalutRipeaByCodi(codi);
		if (dto!=null && dto.getTempsMitg()>0) {
			return getPromitgPonderatInvers(tempsPromitgTotal, peticionsOkTotals, dto.getTempsMitg(), dto.getPeticionsOk());
		} else {
			return tempsPromitgTotal; //Si NO hi havia dades anteriors, retornam les que tenim
		}
	}
	
	/**
	 * Si 3 peticions, han tardat 3653 ms de mitja, i 5 han tardat 2150 ms.
	 * ¿Quant han tardar de mitja en executar-se les 2 noves peticions?
	 */
	private static int getPromitgPonderatInvers(long tempsPromitgTotal, long peticionsOkTotals, long tempsPromitgAnterior, long peticionsOkAnteriors) {
		long novesPeticions = peticionsOkTotals-peticionsOkAnteriors;
		long resultat = 0; //Si no hi ha hagut noves peticions en aquest periode, pero si anteriors, el promitg del periode es zero
		if (novesPeticions>0) {
			resultat = (tempsPromitgTotal*peticionsOkTotals)-(tempsPromitgAnterior*peticionsOkAnteriors) / novesPeticions;
		}
		return (int) (resultat>0?resultat:0);
	}
	
	// --------------------FI METODES ESTATICS---------------- //
	
	@Override
	public List<IntegracioInfo> getIntegracions() {
		List<IntegracioInfo> integracions = new ArrayList<IntegracioInfo>();
		integracions.add(new IntegracioInfo(IntegracioApp.PFI.toString(), IntegracioApp.PFI.getNom())); //portafirmes
		integracions.add(new IntegracioInfo(IntegracioApp.ARX.toString(), IntegracioApp.ARX.getNom())); //Arxiu
		integracions.add(new IntegracioInfo(IntegracioApp.CSV.toString(), IntegracioApp.CSV.getNom())); //ConCSV
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
	public List<SubsistemaInfo> getSubsistemes() {
		List<SubsistemaInfo> subsistemes = new ArrayList<SubsistemaInfo>();
		subsistemes.add(new SubsistemaInfo().codi("SUB_EXP").nom("Tramitació d'expedients"));
		subsistemes.add(new SubsistemaInfo().codi("SUB_PRC").nom("Gestió de procediments"));
		subsistemes.add(new SubsistemaInfo().codi("SUB_MAS").nom("Accions massives"));
		subsistemes.add(new SubsistemaInfo().codi("SUB_CPF").nom("Callback PORTAFIB"));
		subsistemes.add(new SubsistemaInfo().codi("SUB_CNB").nom("Callback NOTIB"));
		subsistemes.add(new SubsistemaInfo().codi("SUB_CDI").nom("Callback DISTRIBUCIO"));
		subsistemes.add(new SubsistemaInfo().codi("SUB_GDO").nom("Gestió documental FileSystem"));
		return subsistemes;
	}

	@Override
	public List<ContextInfo> getContexts(String baseUrl) {
        return List.of(
            new ContextInfo()
                    .codi("BACK")
                    .nom("Backoffice")
                    .path(baseUrl + "/ripeaback")
                    .manuals(List.of(
                            new Manual().nom("Manual d'usuari").path("https://github.com/GovernIB/ripea/raw/ripea-1.0-dev/doc/pdf/01_ripea_manual_usuari.pdf"),
                            new Manual().nom("Manual d'administració").path("https://github.com/GovernIB/ripea/raw/ripea-1.0-dev/doc/pdf/02_ripea_manual_administradors.pdf")))
                    ,
                    new ContextInfo().codi("INT").nom("API interna").path(baseUrl + "/ripeaapi/interna").api(baseUrl + "/ripeaapi/interna/swagger-ui/index.html"),
                    new ContextInfo().codi("EXT").nom("API externa").path(baseUrl + "/ripeaapi/externa").api(null)
        );
	}

	@Override
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		
		//Estats de salut particulars
		EstatSalut salutDb = checkDatabase();		
		List<IntegracioSalut> salutIntegracions = checkIntegracions();
		List<SubsistemaSalut> subsistemesSalut = checkSubsistemes();
		List<MissatgeSalut> missatgesSalut = checkMissatges();
		
		//Estat de salut general (depen de tots els altres)
		EstatSalutEnum estat	= calculaEstatGlobal(salutIntegracions, subsistemesSalut);
		int latenciaGlobal 		= calculaLatenciaGlobal(salutDb, salutIntegracions, subsistemesSalut);
		
		EstatSalut estatSalut = new EstatSalut().estat(estat).latencia(latenciaGlobal);
		InformacioSistema systemInfo = MonitorHelper.getInfoSistema();
        return new SalutInfo()
                .codi(configHelper.getConfig(PropertyConfig.COMANDA_APP_CODI))
                .versio(versio)
                .data(DateUtil.toOffsetDateTime(new Date()))
                .estatGlobal(estatSalut)
                .estatBaseDeDades(salutDb)
                .integracions(salutIntegracions)
                .subsistemes(subsistemesSalut)
                .missatges(missatgesSalut)
                .informacioSistema(systemInfo);
	}
	
	private EstatSalutEnum calculaEstatGlobal(List<IntegracioSalut> salutIntegracions, List<SubsistemaSalut> subsistemesSalut) {
		Long totalPeticionsOk		= 0l;
		Long totalPeticionsError	= 0l;
		if (salutIntegracions.size()>0) {
			for (IntegracioSalut is: salutIntegracions) {
				totalPeticionsOk = totalPeticionsOk + (is.getPeticions().getTotalOk()!=null?is.getPeticions().getTotalOk():0l);
				totalPeticionsError = totalPeticionsError + (is.getPeticions().getTotalError()!=null?is.getPeticions().getTotalError():0l);
			}
		}
		if (subsistemesSalut.size()>0) {
			for (SubsistemaSalut ss: subsistemesSalut) {
				totalPeticionsOk = totalPeticionsOk + (ss.getTotalOk()!=null?ss.getTotalOk():0l);
				totalPeticionsError = totalPeticionsError + (ss.getTotalError()!=null?ss.getTotalError():0l);
			}
		}
		return calculaEstat(totalPeticionsOk, totalPeticionsError);
	}
	
	private int calculaLatenciaGlobal(
			EstatSalut salutDb,
			List<IntegracioSalut> salutIntegracions,
			List<SubsistemaSalut> subsistemesSalut) {

		int totalLatencia= salutDb.getLatencia();
		int numPartsLatencia = 1;
		
		//La latencia de les integracios i subsistemes, es el temps_mitj/numPeticions
		//Per tant nomes hem de dividir les latencies per el nombre total de integracions que han aportat al càlcul.
		if (salutIntegracions.size()>0) {
			int latenciaTotalIntegracions = 0;
			int integracionsAmbPeticions = 0;
			for (IntegracioSalut is: salutIntegracions) {
				int latenciaIntegracio = is.getLatencia()!=null?is.getLatencia():0;
				if (latenciaIntegracio>0) {
					latenciaTotalIntegracions = latenciaTotalIntegracions + (latenciaIntegracio);
					integracionsAmbPeticions++; //Si hi ha latencia es que hi ha hagut alguna petició
				}
			}
			if (integracionsAmbPeticions>0) {
				totalLatencia = totalLatencia + (latenciaTotalIntegracions/integracionsAmbPeticions);
				numPartsLatencia++;
			}
		}
		
		if (subsistemesSalut.size()>0) {
			int latenciaTotalsubsistemes = 0;
			int subsistemesAmbPeticions = 0;
			for (SubsistemaSalut ss: subsistemesSalut) {
				int latenciaSubsistema = ss.getLatencia()!=null?ss.getLatencia():0;
				if (latenciaSubsistema>0) {
					latenciaTotalsubsistemes = latenciaTotalsubsistemes + (latenciaSubsistema);
					subsistemesAmbPeticions++;
				}
			}
			if (subsistemesAmbPeticions>0) {
				totalLatencia = totalLatencia + (latenciaTotalsubsistemes/subsistemesAmbPeticions);
				numPartsLatencia++;
			}
		}
		
		//Latencia mitja entre BBDD, integracions i subsistemes.
		return (int)(totalLatencia/numPartsLatencia);
	}

    private EstatSalut checkDatabase() {

        try {
            Instant start = Instant.now();
            jdbcTemplate.execute("SELECT COUNT(ID) FROM IPA_EXPEDIENT");
            Instant end = Instant.now();
            return new EstatSalut().estat(EstatSalutEnum.UP).latencia((int) Duration.between(start, end).toMillis());
        } catch (Exception e) {
            return new EstatSalut().estat(EstatSalutEnum.DOWN);
        }
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
        			entry.getValue().getTempsMitg(),
        			entry.getValue().getPeticionsOk()));
        	
        	//Afegim al mapa de peticions per endpoint, les dades 
        	peticionsPerEntorn.put(entry.getKey(), integracioPeticioEndpoint);
    	}
    	
    	//Actualitzam les dades estátiques per la seguent petició
    	setDadesIntegracionsRipea(codiIntegracio, acumulacioMetriquesPerEndpoint);
    	
    	return peticionsPerEntorn;
    }
    
    private IntegracioSalut getIntegracioSalutAmbTotals(
    		IntegracioApp codiIntegracio,
    		IntegracioPeticions ip) {
    	String endpointBase = "";
    	long numUsos = 0;
    	if (ip.getPeticionsPerEntorn()!=null && ip.getPeticionsPerEntorn().size()>0) {
    		//Sumam els valors totals dels diferents endpoints del codi de integració
	    	for (Map.Entry<String, IntegracioPeticions> entry : ip.getPeticionsPerEntorn().entrySet()) {
	    		ip.setTotalOk(ip.getTotalOk()!=null?
	    				ip.getTotalOk():0+entry.getValue().getTotalOk());
	    		ip.setTotalError(ip.getTotalError()!=null?
	    				ip.getTotalError():0+entry.getValue().getTotalError());
	    		ip.setTotalTempsMig(ip.getTotalTempsMig()!=null?
	    				ip.getTotalTempsMig():0
	    				+entry.getValue().getTotalTempsMig());
	    		ip.setPeticionsOkUltimPeriode(ip.getPeticionsOkUltimPeriode()!=null?
	    				ip.getPeticionsOkUltimPeriode():0
	    				+entry.getValue().getPeticionsOkUltimPeriode());
	    		ip.setPeticionsErrorUltimPeriode(ip.getPeticionsErrorUltimPeriode()!=null?
	    				ip.getPeticionsErrorUltimPeriode():0
	    				+entry.getValue().getPeticionsErrorUltimPeriode());
	    		ip.setTempsMigUltimPeriode(ip.getTempsMigUltimPeriode()!=null?
	    				ip.getTempsMigUltimPeriode():0
	    				+entry.getValue().getTempsMigUltimPeriode());
	    		//Com a endpoint base per el objecte principal de IntegracioSalut, ens quedam amb el que té mes usos
	    		if (entry.getValue().getPeticionsOkUltimPeriode()+entry.getValue().getPeticionsErrorUltimPeriode()>numUsos) {
	    			numUsos = entry.getValue().getPeticionsOkUltimPeriode()+entry.getValue().getPeticionsErrorUltimPeriode();
	    			endpointBase = ip.getEndpoint();
	    		}
	    	}
	    	
	    	ip.setEndpoint(endpointBase);
	    	
	    	//Per calcular estat de la integracio, recuperar nomes les darreres 10 peticions
	    	//Això no es pot obtenir amb les Métriques de spring, hem de consultar al monitor de integracions.
	    	List<IntegracioAccioDto> darreresIntegracions = aplicacioService.getLastIntegracions(
	    			integracioComandaToRipeaDto(codiIntegracio),
	    			10);
	    	long darreresOk = 0;
	    	long darreresKo = 0;
	    	
	    	for (IntegracioAccioDto ia: darreresIntegracions) {
	    		if (ia.getEstat().equals(IntegracioAccioEstatEnumDto.OK)) {
	    			darreresOk++;
	    		} else {
	    			darreresKo++;
	    		}
	    	}
	    	
	    	return new IntegracioSalut()
	                .codi(codiIntegracio.toString())
	                .latencia(ip.getTotalTempsMig()/ip.getPeticionsPerEntorn().size())
	                .estat(calculaEstat(darreresOk, darreresKo))
	                .peticions(ip);
    	} else {
    		
    		ip.setTotalOk(0l);
    		ip.setTotalError(0l);
    		ip.setTotalTempsMig(0);
    		ip.setPeticionsOkUltimPeriode(0l);
    		ip.setPeticionsErrorUltimPeriode(0l);
    		ip.setTempsMigUltimPeriode(0);
	    	ip.setEndpoint(endpointBase);
	    	
	    	return new IntegracioSalut()
	                .codi(codiIntegracio.toString())
	                .latencia(null)
	                .estat(EstatSalutEnum.UNKNOWN)
	                .peticions(ip);
    	}
    }
    
    private IntegracioEnumDto integracioComandaToRipeaDto(IntegracioApp integracioComanda) {
    	switch (integracioComanda) {
    		case PFI: return IntegracioEnumDto.PFIRMA;
    		case ARX: return IntegracioEnumDto.ARXIU;
    		case CSV: return IntegracioEnumDto.CONCSV;
    		case GDC: return IntegracioEnumDto.GESDOC;
    		case PBL: return IntegracioEnumDto.DADESEXT;
    		case DIS: return IntegracioEnumDto.DISTRIBUCIO;
    		case USR: return IntegracioEnumDto.USUARIS;
    		case CDO: return IntegracioEnumDto.CONVERT;
    		case DIR: return IntegracioEnumDto.DADESEXT;
    		case NOT: return IntegracioEnumDto.NOTIFICACIO;
    		case VIF: return IntegracioEnumDto.VIAFIRMA;
    		case DIB: return IntegracioEnumDto.DIGITALITZACIO;
    		case VFI: return IntegracioEnumDto.VALIDASIG;
    		case RSC: return IntegracioEnumDto.UNITATS;
		default:
			break;
		}
    	return null;
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
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.USR, ipUSR));
		
		String[] codesFSPFI = {
				"METRICS@Integracions.portafirmes",
				"METRICS@Integracions.portafirmesFlux",
				"METRICS@Integracions.firmaServidor",
				"METRICS@Integracions.firmaSimpleWeb"};
		IntegracioPeticions ipPFI = new IntegracioPeticions();
		ipPFI.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.PFI.toString(), codesFSPFI));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.PFI, ipPFI));
    	
		String[] codesFSARX = {"METRICS@Integracions.arxiu"};
		IntegracioPeticions ipARX = new IntegracioPeticions();
		ipARX.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.ARX.toString(), codesFSARX)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.ARX, ipARX));
		
		String[] codesFSCSV = {"METRICS@Integracions.concsv"};
		IntegracioPeticions ipCSV = new IntegracioPeticions();
		ipCSV.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.CSV.toString(), codesFSCSV)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.CSV, ipCSV));
		
		String[] codesFSPBL = {"METRICS@Integracions.pinbal"};
		IntegracioPeticions ipPBL = new IntegracioPeticions();
		ipPBL.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.PBL.toString(), codesFSPBL)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.PBL, ipPBL));

		String[] codesFSDIS = {"METRICS@Integracions.distribucio"};
		IntegracioPeticions ipDIS = new IntegracioPeticions();
		ipDIS.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.DIS.toString(), codesFSDIS)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.DIS, ipDIS));
		
		String[] codesFSCDO = {"METRICS@Integracions.conversio"};
		IntegracioPeticions ipCDO = new IntegracioPeticions();
		ipCDO.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.CDO.toString(), codesFSCDO));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.CDO, ipCDO));
		
		String[] codesFSDIR = {"METRICS@Integracions.dir3"}; //getUnitatsOrganitzativesPlugin, getDadesExternesPlugin
		IntegracioPeticions ipDIR = new IntegracioPeticions();
		ipDIR.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.DIR.toString(), codesFSDIR));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.DIR, ipDIR));
		
		String[] codesFSNOT = {"METRICS@Integracions.notib"};
		IntegracioPeticions ipNOT = new IntegracioPeticions();
		ipNOT.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.NOT.toString(), codesFSNOT)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.NOT, ipNOT));
		
		if (Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.FIRMA_BIOMETRICA_ACTIVA))) {
			String[] codesFSVIF = {"METRICS@Integracions.viafirma"};
			IntegracioPeticions ipVIF = new IntegracioPeticions();
			ipVIF.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.VIF.toString(), codesFSVIF)); 
			integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.VIF, ipVIF));
		}
		
		String[] codesFSDIB = {"METRICS@Integracions.digitalitzacio"};
		IntegracioPeticions ipDIB = new IntegracioPeticions();
		ipDIB.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.DIB.toString(), codesFSDIB));
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.DIB, ipDIB));
		
		String[] codesFSVFI = {"METRICS@Integracions.validaFirma"};
		IntegracioPeticions ipVFI = new IntegracioPeticions();
		ipVFI.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.VFI.toString(), codesFSVFI)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.VFI, ipVFI));
		
		String[] codesFSRSC = {"METRICS@Integracions.rolsac"}; //getProcedimentPlugin
		IntegracioPeticions ipRSC = new IntegracioPeticions();
		ipRSC.setPeticionsPerEntorn(getPeticionsPerEntorn(IntegracioApp.RSC.toString(), codesFSRSC)); 
		integracionsSalut.add(getIntegracioSalutAmbTotals(IntegracioApp.RSC, ipRSC));
    	
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
    
    private SubsistemaSalut getSubsistemaSalut(String codi, String[] codesSubsystemMetrics) {
		
    	long subsistema_total_exito = 0;
		long subsistema_total_error = 0;
		int tempsPromitgSubsistema	= 0;
    	
    	for (String codiMetrica: codesSubsystemMetrics) {
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
    	
    	int		tempsPromitgActual = subsistema_total_exito==0?0:(int)(tempsPromitgSubsistema/subsistema_total_exito);
    	long	peticionsOkDarrerPeriode = getDadesOkPeriodeByCodi(codi, subsistema_total_exito); 
    	SubsistemaSalut resultat = new SubsistemaSalut()
                .codi(codi)
                .totalTempsMig(tempsPromitgActual)
                .estat(calculaEstat(subsistema_total_exito, subsistema_total_error))
                .totalOk(subsistema_total_exito)
                .totalError(subsistema_total_error)
                .peticionsOkUltimPeriode(peticionsOkDarrerPeriode)
                .peticionsErrorUltimPeriode(getDadesErrorPeriodeByCodi(codi, subsistema_total_error))
                .tempsMigUltimPeriode(getDadesTempsPromitgPeriodeByCodi(codi, tempsPromitgActual, subsistema_total_exito));
		
		actualizarDadesSalutRipeaByCodi(codi, subsistema_total_exito, subsistema_total_error, tempsPromitgActual);
		
		return resultat;
    }
    
    public List<SubsistemaSalut> checkSubsistemes() {
    	
    	List<SubsistemaSalut> salutSubsistemes = new ArrayList<SubsistemaSalut>();
    	
    	/**
    	 * T R A M I T A C I O     E X P E D I E N T S
    	 */
    	String[] codesEXP = {"METRICS@Subsystem_Expedient.create"
    			, "METRICS@Subsystem_Expedient.list"
    			, "METRICS@Subsystem_Expedient.tasquesUserList"
    			, "METRICS@Subsystem_Expedient.createTasca"
    			, "METRICS@Subsystem_Expedient.canviEstatTasca"};
    	salutSubsistemes.add(getSubsistemaSalut("SUB_EXP", codesEXP));
		
    	/**
    	 * G E S T I O     P R O C E D I M E N T S
    	 */
    	String[] codesPRC = {"METRICS@Subsystem_Procediment.create"
    			, "METRICS@Subsystem_Procediment.list"
    			, "METRICS@Subsystem_Procediment.import"
    			, "METRICS@Subsystem_Procediment.metaDoc"
    			, "METRICS@Subsystem_Procediment.metaDada"
    			, "METRICS@Subsystem_Procediment.metaTasca"};
    	salutSubsistemes.add(getSubsistemaSalut("SUB_PRC", codesPRC));

    	/**
    	 * A C C I O N S     M A S S I V E S
    	 */
    	String[] codesMAS = {"METRICS@Subsystem_Background.userMassiveAction"
    			, "METRICS@Subsystem_Background.consultarIGuardarAnotacions"
    			, "METRICS@Subsystem_Background.guardarEnArxiuContingutsPendents"
    			, "METRICS@Subsystem_Background.enviarEmailsAgrupats"};
    	salutSubsistemes.add(getSubsistemaSalut("SUB_MAS", codesMAS));
		
    	/**
    	 * F I L E     S Y S T E M
    	 */
		String[] codesFS = {"METRICS@Subsystem_FileSystem.create", "METRICS@Subsystem_FileSystem.get"};
		salutSubsistemes.add(getSubsistemaSalut("SUB_GDO", codesFS));
		
    	/**
    	 * C A L L B A C K     D I S T R I B U C I O     (Event controlador)
    	 */
    	
		String[] codeCDI = {"METRICS@Subsystem_Callback_Distribucio.event"};
		salutSubsistemes.add(getSubsistemaSalut("SUB_CDI", codeCDI));
		
    	/**
    	 * C A L L B A C K     N O T I B     (Event controlador)
    	 */
    	
		String[] codeCNB = {"METRICS@Subsystem_Callback_Notib.notificaCanvi"};
    	salutSubsistemes.add(getSubsistemaSalut("SUB_CNB", codeCNB));
		
    	/**
    	 * C A L L B A C K     P O R T A F I B     (Event controlador)
    	 */
    	
    	String[] codeCPF = {"METRICS@Subsystem_Callback_Portafib.event"};
    	salutSubsistemes.add(getSubsistemaSalut("SUB_CPF", codeCPF));
		
    	return salutSubsistemes;
    }

    private static EstatSalutEnum calculaEstat(Long totalPeticionsOk, Long totalPeticionsError) {
    	final long ok = (totalPeticionsOk != null) ? totalPeticionsOk : 0L;
        final long ko = (totalPeticionsError != null) ? totalPeticionsError : 0L;
        return EstatHelper.calculaEstat(ok, ko);
    }
    
    public List<MissatgeSalut> checkMissatges() {

    	List<MissatgeSalut> missatges = new ArrayList<>();
    	
    	try {

	    	List<AvisEntity> avisosActius = avisRepository.findAllActives();
	    	for (AvisEntity avis: avisosActius) {
	    		String missatge = "";
	    		if (avis.getEntitatId()!=null) {
	    			missatge+="[Entitat "+avis.getEntitatId()+"] ";
	    		}
	    		if (avis.getAssumpte()!=null) {
	    			missatge+=avis.getAssumpte();
	    		}
	    		if (avis.getMissatge()!=null) {
	    			missatge+=": "+avis.getMissatge();
	    		}
	    		MissatgeSalut ms = new MissatgeSalut()
	    				.data(DateUtil.toOffsetDateTime(avis.getDataInici()))
	    				.missatge(missatge)
	    				.nivell(getSalutNivellComanda(avis.getAvisNivell()));
	    		missatges.add(ms);
	    	}
	    	
    	} catch (Exception ex) {}

    	return missatges;
    }
    
	private SalutNivell getSalutNivellComanda(AvisNivellEnumDto avisNivell) {
		switch (avisNivell) {
			case ERROR: return SalutNivell.ERROR;
			case INFO: return SalutNivell.INFO;
			case WARNING: return SalutNivell.WARN;
		}
		return null;
	}
}