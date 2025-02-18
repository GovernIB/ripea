/**
 * 
 */
package es.caib.ripea.service.helper;

import es.caib.distribucio.rest.client.integracio.domini.*;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.*;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Mètodes per a la gestió de peticions de crear expedients 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientPeticioHelper {
	
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired private RegistreRepository registreRepository;
	@Autowired private RegistreInteressatRepository registreInteressatRepository;
	@Autowired private RegistreAnnexRepository registreAnnexRepository;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private PluginHelper pluginHelper;
    @Autowired private OrganGestorCacheHelper organGestorCacheHelper;
	@Autowired private DistribucioHelper distribucioHelper;

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
			
		ExpedientPeticioEntity peticio = expedientPeticioRepository.getOne(peticioId);
		
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
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
		canviEstatExpedientPeticio(expedientPeticioEntity, expedientPeticioEstatEnumDto);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExpedientPeticioConsultaError(
			Long expedientPeticioId,
			String errorDescription) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
		expedientPeticioEntity.updateConsultaWsError(true);
		expedientPeticioEntity.updateConsultaWsErrorDesc(errorDescription);
		expedientPeticioEntity.updateConsultaWsErrorDate(new Date());
	}

	@Transactional
	public void removeExpedientPeticioConsultaError(
			Long expedientPeticioId,
			String errorDescription) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
		expedientPeticioEntity.updateConsultaWsError(false);
		expedientPeticioEntity.updateConsultaWsErrorDesc(null);
		expedientPeticioEntity.updateConsultaWsErrorDate(null);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExpedientPeticioInfoDto getExpedeintPeticiInfo(Long expedientPeticioId) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
		return new ExpedientPeticioInfoDto(expedientPeticioEntity.getIdentificador(), expedientPeticioEntity.getClauAcces(), expedientPeticioEntity.getEstat());
		
	}
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setEstatCanviatDistribucioNewTransaction(Long expedientPeticioId, boolean canviat) {
		
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
		expedientPeticioEntity.setEstatCanviatDistribucio(canviat);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void crearRegistrePerPeticio(AnotacioRegistreEntrada registreEntrada, Long expedientPeticioId) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.getOne(expedientPeticioId);
		EntitatEntity entitat = entitatRepository.findByUnitatArrel(
				registreEntrada.getEntitatCodi());
		if (entitat == null) {
			throw new NotFoundException(entitat, EntitatEntity.class);
		}
		
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
		
		// set metaexpedient to which expedient will belong if peticion is accepted
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacioOrderByNomAsc(
				entitat,
				expedientPeticioEntity.getRegistre().getProcedimentCodi());
		MetaExpedientEntity metaExpedientEntity = null;
		if (!metaExpedients.isEmpty()) {
			metaExpedientEntity = metaExpedients.get(0);
		}
		expedientPeticioEntity.updateMetaExpedient(metaExpedientEntity);
		
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
		
		for (Interessat interessat: registreEntrada.getInteressats()) {
			registreEntity.getInteressats().add(
					crearInteressatEntity(
							interessat,
							registreEntity));
		}

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

		canviEstatExpedientPeticio(
				expedientPeticioEntity,
				ExpedientPeticioEstatEnumDto.PENDENT);
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

		
		ExpedientPeticioEntity pendent = expedientPeticioRepository.getOne(id);
		
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
			distribucioHelper.getBackofficeIntegracioRestClient().canviEstat(anotacio, estat, observacions);
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
		return configHelper.getAsBoolean(PropertyConfig.GUARDAR_CONTINGUT_ANNEXOS_DISTRIBUCIO);
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

		annexEntity.updateFirmaTipus(annex.getFirmaTipus());
		annexEntity.updateFirmaPerfil(annexEntity.getFirmaPerfil());
		registreAnnexRepository.save(annexEntity);
		return annexEntity;
	}

	private ArxiuEstatEnumDto getAnnexEstat(AnnexEstat estat) {
		if (AnnexEstat.ESBORRANY.equals(estat))
			return ArxiuEstatEnumDto.ESBORRANY;
		return ArxiuEstatEnumDto.DEFINITIU;
	}

/*	private FirmaTipus firmaTipusRipeaToDistribucio(ArxiuFirmaTipusEnumDto tipusRipea) {
		if (tipusRipea!=null) {
			switch (tipusRipea) {
				case CADES_ATT: return FirmaTipus.CADES_ATT;
				case CADES_DET: return FirmaTipus.CADES_DET;
				case CSV: return FirmaTipus.CSV;
				case ODT: return FirmaTipus.ODT;
				case OOXML: return FirmaTipus.OOXML;
				case PADES: return FirmaTipus.PADES;
				case SMIME: return FirmaTipus.SMIME;
				case XADES_DET: return FirmaTipus.XADES_DET;
				case XADES_ENV: return FirmaTipus.XADES_ENV;
			}
		}
		return null;
	}
	
	private FirmaPerfil firmaPerfilRipeaToDistribucio(ArxiuFirmaPerfilEnumDto tipusRipea) {
		if (tipusRipea!=null) {
			switch (tipusRipea) {
				case A: return FirmaPerfil.A;
				case BASELINE_B_LEVEL: return FirmaPerfil.BASELINE_B_LEVEL;
				case BASELINE_LT_LEVEL: return FirmaPerfil.BASELINE_LT_LEVEL;
				case BASELINE_LTA_LEVEL: return FirmaPerfil.BASELINE_LTA_LEVEL;
				case BASELINE_T: return FirmaPerfil.BASELINE_T;
				case BASELINE_T_LEVEL: return FirmaPerfil.BASELINE_T_LEVEL;
				case BASIC: return FirmaPerfil.BASIC;
				case Basic: return FirmaPerfil.BASIC;
				case BES: return FirmaPerfil.BES;
				case C: return FirmaPerfil.C;
				case EPES: return FirmaPerfil.EPES;
				case LTA: return FirmaPerfil.LTA;
				case LTV: return FirmaPerfil.LTV;
				case T: return FirmaPerfil.T;
				case X: return FirmaPerfil.X;
				case XL: return FirmaPerfil.XL;
			}
		}
		return null;
	}
	*/
	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioHelper.class);
}