/**
 * 
 */
package es.caib.ripea.core.helper;

import es.caib.distribucio.rest.client.integracio.domini.Annex;
import es.caib.distribucio.rest.client.integracio.domini.AnnexEstat;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.distribucio.rest.client.integracio.domini.Interessat;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioInfoDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreInteressatRepository;
import es.caib.ripea.core.repository.RegistreRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Mètodes per a la gestió de peticions de crear expedients 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientPeticioHelper {
	
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreInteressatRepository registreInteressatRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;  
	@Autowired
	private EntitatRepository entitatRepository; 
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Resource
	private OrganGestorHelper organGestorHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private PluginHelper pluginHelper;
    @Autowired
    private OrganGestorCacheHelper organGestorCacheHelper;

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void crearExpedientPeticion(es.caib.distribucio.ws.backoffice.AnotacioRegistreId anotacioRegistreId) {
			
		logger.info("Creant comunicació de l'anotació: " + anotacioRegistreId.getIndetificador());
		
		ExpedientPeticioEntity expedientPeticioEntity = ExpedientPeticioEntity.getBuilder(
				anotacioRegistreId.getIndetificador(),
				anotacioRegistreId.getClauAcces(),
				new Date(),
				ExpedientPeticioEstatEnumDto.CREAT).build();

		expedientPeticioRepository.save(expedientPeticioEntity);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void resetExpedientPeticion(Long peticioId) {
			
		ExpedientPeticioEntity peticio = expedientPeticioRepository.findOne(peticioId);
		
		if (peticio.getEstat() == ExpedientPeticioEstatEnumDto.PENDENT || peticio.getEstat() == ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT || peticio.getEstat() == ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT) {
			logger.info("No es pot netejar anotació en estat: " + peticio.getEstat() + ", " + peticio.getId() + ", " + peticio.getIdentificador());

		} else {
			logger.info("Netejant l'anotació: " + peticio.getId() + ", " + peticio.getIdentificador());
			RegistreEntity registre = peticio.getRegistre();
			if (registre != null) {
				peticio.updateRegistre(null);
				registreRepository.delete(registre);
			}
			peticio.updateConsultaWsError(false);
			peticio.updateConsultaWsErrorDate(null);
			peticio.updateConsultaWsErrorDesc(null);
			peticio.updateEstat(ExpedientPeticioEstatEnumDto.CREAT);
		}
	}


	public void canviEstatExpedientPeticio(ExpedientPeticioEntity expedientPeticioEntity, ExpedientPeticioEstatEnumDto expedientPeticioEstatEnumDto) {

		expedientPeticioEntity.updateEstat(expedientPeticioEstatEnumDto);
		if (expedientPeticioEstatEnumDto == ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT) {
			expedientPeticioEntity.setDataActualitzacio(new Date());
			expedientPeticioEntity.setUsuariActualitzacio(usuariRepository.findByCodi(SecurityContextHolder.getContext().getAuthentication().getName()));
		}
		EntitatEntity entitatAnotacio = expedientPeticioEntity.getRegistre().getEntitat();
		if (entitatAnotacio != null) {
			cacheHelper.evictAllCountAnotacionsPendents();
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void canviEstatExpedientPeticioNewTransaction(
			Long expedientPeticioId,
			ExpedientPeticioEstatEnumDto expedientPeticioEstatEnumDto) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		canviEstatExpedientPeticio(expedientPeticioEntity, expedientPeticioEstatEnumDto);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExpedientPeticioConsultaError(
			Long expedientPeticioId,
			String errorDescription) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateConsultaWsError(true);
		expedientPeticioEntity.updateConsultaWsErrorDesc(errorDescription);
		expedientPeticioEntity.updateConsultaWsErrorDate(new Date());
	}

	@Transactional
	public void removeExpedientPeticioConsultaError(
			Long expedientPeticioId,
			String errorDescription) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateConsultaWsError(false);
		expedientPeticioEntity.updateConsultaWsErrorDesc(null);
		expedientPeticioEntity.updateConsultaWsErrorDate(null);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExpedientPeticioInfoDto getExpedeintPeticiInfo(Long expedientPeticioId) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		return new ExpedientPeticioInfoDto(expedientPeticioEntity.getIdentificador(), expedientPeticioEntity.getClauAcces(), expedientPeticioEntity.getEstat());
		
	}
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setEstatCanviatDistribucioNewTransaction(Long expedientPeticioId, boolean canviat) {
		
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.setEstatCanviatDistribucio(canviat);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void crearRegistrePerPeticio(AnotacioRegistreEntrada registreEntrada, Long expedientPeticioId) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		EntitatEntity entitat = entitatRepository.findByUnitatArrel(
				registreEntrada.getEntitatCodi());
		if (entitat == null) {
			throw new NotFoundException(entitat, EntitatEntity.class);
		}
		
//		System.out.println("crearRegistrePerPeticio before getBuilder, identificador: " + registreEntrada.getIdentificador());
		
		RegistreEntity registreEntity = RegistreEntity.getBuilder(
				registreEntrada.getAssumpteTipusCodi(),
				registreEntrada.getData(),
				registreEntrada.getEntitatCodi(),
				registreEntrada.getIdentificador(),
				registreEntrada.getIdiomaCodi(),
				registreEntrada.getLlibreCodi(),
				registreEntrada.getOficinaCodi(),
				registreEntrada.getDestiCodi(),
				entitat).
				aplicacioCodi(registreEntrada.getAplicacioCodi()).
				aplicacioVersio(registreEntrada.getAplicacioVersio()).
				assumpteCodiCodi(registreEntrada.getAssumpteCodiCodi()).
				assumpteCodiDescripcio(registreEntrada.getAssumpteCodiDescripcio()).
				assumpteTipusDescripcio(registreEntrada.getAssumpteTipusDescripcio()).
				docFisicaCodi(registreEntrada.getDocFisicaCodi()).
				docFisicaDescripcio(registreEntrada.getDocFisicaDescripcio()).
				entitatDescripcio(registreEntrada.getEntitatDescripcio()).
				expedientNumero(registreEntrada.getExpedientNumero()).
				exposa(registreEntrada.getExposa()).
	 			extracte(registreEntrada.getExtracte()).
	 			procedimentCodi(registreEntrada.getProcedimentCodi()).
				idiomaDescripcio(registreEntrada.getIdomaDescripcio()).
				llibreDescripcio(registreEntrada.getLlibreDescripcio()).
				observacions(registreEntrada.getObservacions()).
				oficinaDescripcio(registreEntrada.getOficinaDescripcio()).
				origenData(registreEntrada.getOrigenData() != null ? registreEntrada.getOrigenData() : null).
				origenRegistreNumero(registreEntrada.getOrigenRegistreNumero()).
				refExterna(registreEntrada.getRefExterna()).
				solicita(registreEntrada.getSolicita()).
				transportNumero(registreEntrada.getTransportNumero()).
				transportTipusCodi(registreEntrada.getTransportTipusCodi()).
				transportTipusDescripcio(registreEntrada.getTransportTipusDescripcio()).
				usuariCodi(registreEntrada.getUsuariCodi()).
				usuariNom(registreEntrada.getUsuariNom()).
				destiDescripcio(registreEntrada.getDestiDescripcio()).
				justificantArxiuUuid(registreEntrada.getJustificantFitxerArxiuUuid()).
				build();
		registreRepository.save(registreEntity);
		expedientPeticioEntity.updateRegistre(registreEntity);
		
//		System.out.println("crearRegistrePerPeticio before findByEntitatAndClassificacioSia, identificador: " + registreEntrada.getIdentificador());
		
		// set metaexpedient to which expedient will belong if peticion is accepted
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacio(
				entitat,
				expedientPeticioEntity.getRegistre().getProcedimentCodi());
		MetaExpedientEntity metaExpedientEntity = null;
		if (!metaExpedients.isEmpty()) {
			metaExpedientEntity = metaExpedients.get(0);
		}
		expedientPeticioEntity.updateMetaExpedient(
				metaExpedientEntity);
		
		calcularGrup(expedientPeticioEntity);
		
		ExpedientEntity expedientEntity = null;
		if (metaExpedientEntity != null) {
			expedientEntity = expedientRepository.findByEntitatAndMetaNodeAndNumero(
					entitat,
					metaExpedientEntity, 
					expedientPeticioEntity.getRegistre().getExpedientNumero());
		}
		// set accion to be performed if peticion is accepted
		if (expedientEntity != null) {
			expedientPeticioEntity.updateExpedientPeticioAccioEnumDto(
					ExpedientPeticioAccioEnumDto.INCORPORAR);
		} else {
			expedientPeticioEntity.updateExpedientPeticioAccioEnumDto(
					ExpedientPeticioAccioEnumDto.CREAR);
		}
		expedientPeticioRepository.save(expedientPeticioEntity);
		
//		System.out.println("crearRegistrePerPeticio before interessats, identificador: " + registreEntrada.getIdentificador());
		
		for (Interessat interessat: registreEntrada.getInteressats()) {
			registreEntity.getInteressats().add(
					crearInteressatEntity(
							interessat,
							registreEntity));
		}
//		System.out.println("crearRegistrePerPeticio before annexos, identificador: " + registreEntrada.getIdentificador());
		for (Annex annex: registreEntrada.getAnnexos()) {
			
			// Guardar annexos de les anotacions en FileSystem (instal·lació de Ripea i Distribució en servidors separats)
			if (getPropertyGuardarContingutAnnexosDistribucio()) {
				
				EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
				ConfigHelper.setEntitat(entitatDto);
				
				// Crear contenidor annexos Distribució en FileSystem
				String uuidExpedient = pluginHelper.arxiuExpedientDistribucioCrear(
						registreEntity.getIdentificador(), 
						registreEntity.getExpedientNumero(),
						registreEntity.getDestiCodi());
				
				// Crear annex Distribució en FileSystem dins contenidor anterior
				String uuidDocument = pluginHelper.arxiuAnnexDistribucioCrear(
						annex, 
						registreEntity.getDestiCodi(), 
						uuidExpedient);
				
				annex.setUuid(uuidDocument);
			}
			
			registreEntity.getAnnexos().add(
					crearAnnexEntity(
							annex,
							registreEntity));
			
		}
		
//		System.out.println("crearRegistrePerPeticio before canviEstat, identificador: " + registreEntrada.getIdentificador());
		// change state of expedient peticio to pendent de processar
		canviEstatExpedientPeticio(
				expedientPeticioEntity,
				ExpedientPeticioEstatEnumDto.PENDENT);
		
//		System.out.println("crearRegistrePerPeticio metod finished, identificador: " + registreEntrada.getIdentificador());
		
	}
	
	private void calcularGrup(ExpedientPeticioEntity expedientPeticioEntity) {
		
		if (cacheHelper.mostrarLogsGrups())
			logger.info("calcularGrupAlRecibirAnotacio start (expedientPeticio=" + expedientPeticioEntity.getId() + " - " + expedientPeticioEntity.getIdentificador());
		
		GrupEntity grup = null;
		MetaExpedientEntity metaExpedient = expedientPeticioEntity.getMetaExpedient();
		
		if (cacheHelper.mostrarLogsGrups())
			logger.info("metaExpedient= " + metaExpedient.getId() + " - " + metaExpedient.getCodi());
		
		if (metaExpedient != null && metaExpedient.isGestioAmbGrupsActiva()) {
			
			if (cacheHelper.mostrarLogsGrups())
				logger.info("gestioAmbGrupsActiva");
			
			List<GrupEntity> grups = metaExpedient.getGrups();
			if (Utils.isNotEmpty(grups)) {
				
				if (cacheHelper.mostrarLogsGrups())
					logger.info("grupsNotEmpty");
				
				OrganGestorEntity org = organGestorRepository.findByCodi(expedientPeticioEntity.getRegistre().getDestiCodi());
				
				while (grup == null && org != null) {
					
					if (cacheHelper.mostrarLogsGrups())
						logger.info("organ=" + org.getId() + " - " + org.getCodi());
					
					for (GrupEntity grupEntity : grups) {
						if (cacheHelper.mostrarLogsGrups())
							logger.info("grup=" + grupEntity.getId() + " - " + grupEntity.getCodi() + " - " + grupEntity.getOrganGestor());
						
						if (grupEntity.getOrganGestor() != null && grupEntity.getOrganGestor().getId().equals(org.getId())) {
							grup = grupEntity;
							if (cacheHelper.mostrarLogsGrups())
								logger.info("grup trobat per organ=" + grup.getId());
							break;
						}
					}
					org = org.getPare();
				}
				
				if (grup == null ) {
					grup = metaExpedient.getGrupPerDefecte();
				}
			}
		}
		
		expedientPeticioEntity.setGrup(grup);
		
		if (cacheHelper.mostrarLogsGrups())
			logger.info("calcularGrupAlRecibirAnotacio end (expedientPeticio=" + expedientPeticioEntity.getId() + ", " + expedientPeticioEntity.getIdentificador());
	}
	
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Exception reintentarCanviEstatDistribucio(Long id) {

		
		ExpedientPeticioEntity pendent = expedientPeticioRepository.findOne(id);
		
		long t2 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("reintentarCanviEstatDistribucio start (" + pendent.getIdentificador() + ", " + id + ")");
		
		
		Exception exception = null;
		AnotacioRegistreId anotacio = new AnotacioRegistreId();
		anotacio.setIndetificador(pendent.getIdentificador());
		anotacio.setClauAcces(pendent.getClauAcces());
		try {
			
			Estat estat = null;
			String observacions = "";
			switch (pendent.getEstat()) {
			case CREAT:
				estat = Estat.ERROR;
				observacions = pendent.getConsultaWsErrorDesc();
				break;
			case PENDENT:
				estat = Estat.REBUDA;
				break;
			case PROCESSAT_PENDENT:
			case PROCESSAT_NOTIFICAT:
				estat = Estat.PROCESSADA;
				break;
			case REBUTJAT:
				estat = Estat.REBUTJADA;
				break;
			}
			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("Canviant estat (" + pendent.getIdentificador() + "," + pendent.getClauAcces() + ", " + estat + "," + observacions + ")");
			DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(anotacio, estat, observacions);
			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("Estat canviat (" + pendent.getIdentificador() + "," + pendent.getClauAcces() + ", " + estat + "," + observacions + ")");
			pendent.setEstatCanviatDistribucio(true);
			
			
		} catch (Exception ex) {
			logger.error("Error al reintentar canvi estat a Distribució de anotacio amb id " + pendent.getId(), ex);
			exception = ex;
			pendent.setEstatCanviatDistribucio(false, true);
		}
		
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("reintentarCanviEstatDistribucio end (" + pendent.getIdentificador() + ", " + id + "):  " + (System.currentTimeMillis() - t2) + " ms");
		return exception;
	}
	
	public PermisosPerAnotacions findPermisosPerAnotacions(
			Long entitatId,
			String rolActual, 
			Long organActualId) {
		PermisosPerAnotacions permisosPerAnotacionsDto = new PermisosPerAnotacions();
		
		if (rolActual.equals("IPA_ADMIN")) {
			// in this case all annotations of entitat are permitted, it is not equal to annotations belonging to any procediment of entitat because some of the annotations might not have procediment assigned
			// so this is wrong -> permisosPerAnotacionsDto.setProcedimentsPermesos(metaExpedientRepository.findByEntitatId(entitatId));
		} else if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			EntitatEntity entitat = entitatRepository.getOne(entitatId);
			OrganGestorEntity organGestor = organGestorRepository.getOne(organActualId);
			permisosPerAnotacionsDto.setAdminOrganHasPermisAdminComu(organGestorHelper.hasPermisAdminComu(organActualId));
			permisosPerAnotacionsDto.setAdminOrganCodisOrganAmbDescendents(organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organGestor.getCodi()));
			permisosPerAnotacionsDto.setProcedimentsPermesos(metaExpedientHelper.findProcedimentsDeOrganIDeDescendentsDeOrgan(organActualId));
		} else if (rolActual.equals("tothom")) {
			permisosPerAnotacionsDto.setProcedimentsPermesos(metaExpedientHelper.getCreateWritePermesos(entitatId));
			
			List<Long> idsGrupsPermesos = Utils.toListLong(permisosHelper.getObjectsIdsWithPermission(
					GrupEntity.class,
					ExtendedPermission.READ));
			permisosPerAnotacionsDto.setIdsGrupsPermesos(idsGrupsPermesos);
		}


		return permisosPerAnotacionsDto;
		
	}
	
	public boolean getPropertyGuardarContingutAnnexosDistribucio() {
		return configHelper.getAsBoolean("es.caib.ripea.anotacions.annexos.save");
	}

	private RegistreInteressatEntity crearInteressatEntity(
			Interessat interessat,
			RegistreEntity registreEntity) {
		RegistreInteressatEntity representantEntity = null;
		if (interessat.getRepresentant() != null){
			representantEntity = RegistreInteressatEntity.getBuilder(	
		 			interessat.getRepresentant().getTipus()).
		 			adresa (interessat.getRepresentant().getAdresa()).
		 			canal (interessat.getRepresentant().getCanal()).
		 			cp (interessat.getRepresentant().getCp()).
		 			documentNumero (interessat.getRepresentant().getDocumentNumero()).
		 			documentTipus (interessat.getRepresentant().getDocumentTipus()).
		 			email (interessat.getRepresentant().getEmail()).
		 			llinatge1 (interessat.getRepresentant().getLlinatge1()).
		 			llinatge2 (interessat.getRepresentant().getLlinatge2()).
		 			municipiCodi (interessat.getRepresentant().getMunicipiCodi()).
		 			nom (interessat.getRepresentant().getNom()).
		 			observacions (interessat.getRepresentant().getObservacions()).
		 			paisCodi (interessat.getRepresentant().getPaisCodi()).
		 			provinciaCodi (interessat.getRepresentant().getProvinciaCodi()).
		 			raoSocial (interessat.getRepresentant().getRaoSocial()).
		 			telefon (interessat.getRepresentant().getTelefon()).
		 			pais (interessat.getRepresentant().getPais()).
		 			provincia (interessat.getRepresentant().getProvincia()).
		 			municipi (interessat.getRepresentant().getMunicipi()).
		 			organCodi(interessat.getRepresentant().getOrganCodi()).
					build();
		}
		RegistreInteressatEntity interessatEntity = RegistreInteressatEntity.getBuilder(	
		 			interessat.getTipus()).
		 			adresa (interessat.getAdresa()).
		 			canal (interessat.getCanal()).
		 			cp (interessat.getCp()).
		 			documentNumero (interessat.getDocumentNumero()).
		 			documentTipus (interessat.getDocumentTipus()).
		 			email (interessat.getEmail()).
		 			llinatge1 (interessat.getLlinatge1()).
		 			llinatge2 (interessat.getLlinatge2()).
		 			municipiCodi (interessat.getMunicipiCodi()).
		 			nom (interessat.getNom()).
		 			observacions (interessat.getObservacions()).
		 			paisCodi (interessat.getPaisCodi()).
		 			provinciaCodi (interessat.getProvinciaCodi()).
		 			pais (interessat.getPais()).
		 			provincia (interessat.getProvincia()).
		 			municipi (interessat.getMunicipi()).		 			
		 			raoSocial (interessat.getRaoSocial()).
		 			telefon (interessat.getTelefon()).
		 			organCodi(interessat.getOrganCodi()).
		 			representant(representantEntity).
		 			registre(registreEntity).
					build();
		registreInteressatRepository.save(interessatEntity);
		return interessatEntity;
	}

	private RegistreAnnexEntity crearAnnexEntity(Annex annex, RegistreEntity registreEntity) {
		RegistreAnnexEntity annexEntity = RegistreAnnexEntity.getBuilder(
				annex.getNom(),
				annex.getNtiFechaCaptura(),
				annex.getNtiOrigen(),
				annex.getNtiTipoDocumental(),
				annex.getSicresTipoDocumento(),
				annex.getTitol(),
				registreEntity,
				annex.getNtiEstadoElaboracion(),
				annex.getTamany()).
				contingut(annex.getContingut()).
				firmaContingut(annex.getFirmaContingut()).
				ntiTipoDocumental(annex.getNtiTipoDocumental()).
				sicresTipoDocumento(annex.getSicresTipoDocumento()).
				observacions(annex.getObservacions()).
				sicresValidezDocumento(annex.getSicresValidezDocumento()).
				tipusMime(annex.getTipusMime()).
				uuid(annex.getUuid()).
				firmaNom(annex.getFirmaNom()).
				validacioFirmaCorrecte(annex.isDocumentValid()).
				validacioFirmaErrorMsg(annex.getDocumentError()).
				annexArxiuEstat(getAnnexEstat(annex.getEstat())).
				build();
		if (annex.getFirmaTipus() != null) {
			annexEntity.updateFirmaTipus(annex.getFirmaTipus());
		}
		if (annex.getFirmaPerfil() != null) {
			annexEntity.updateFirmaPerfil(annexEntity.getFirmaPerfil());
		}
		registreAnnexRepository.save(annexEntity);
		return annexEntity;
	}

	private ArxiuEstatEnumDto getAnnexEstat(AnnexEstat estat) {
		if (AnnexEstat.ESBORRANY.equals(estat))
			return ArxiuEstatEnumDto.ESBORRANY;
		return ArxiuEstatEnumDto.DEFINITIU;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioHelper.class);

}
	
	
	