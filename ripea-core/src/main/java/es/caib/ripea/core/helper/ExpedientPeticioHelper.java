/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Annex;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.Interessat;
import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.RegistreEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreInteressatRepository;
import es.caib.ripea.core.repository.RegistreRepository;

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

	/*
	 * Crear peticions de creació d’expedients amb estat pendent d'aprovació
	 */
	@Transactional
	public void crearExpedientsPeticions(List<AnotacioRegistreId> ids) {
		for (AnotacioRegistreId anotacioRegistreId : ids) {
			ExpedientPeticioEntity peticio = expedientPeticioRepository.findByIdentificador(anotacioRegistreId.getIndetificador());
			// only create peticions that were not created before
			// distribucio will be resending ids until ripea call Distribucio WS method canviEstat(BACK_REBUDA)
			if (peticio == null) {
				logger.debug("Creant una petició de creació d’expedient (" 
						+ "identificador=" + anotacioRegistreId.getIndetificador() + ", " 
						+ "clauAcces=" + anotacioRegistreId.getClauAcces() + ")");
				
				ExpedientPeticioEntity expedientPeticioEntity = ExpedientPeticioEntity.getBuilder(
						anotacioRegistreId.getIndetificador(),
						anotacioRegistreId.getClauAcces(),
						new Date(),
						ExpedientPeticioEstatEnumDto.CREAT).
						build();
				
				expedientPeticioRepository.save(expedientPeticioEntity);
			} else {
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
	}

	@Transactional
	public void canviEstatExpedientPeticio(
			Long expedientPeticioId,
			ExpedientPeticioEstatEnumDto expedientPeticioEstatEnumDto) {
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientPeticioEntity.updateEstat(
				expedientPeticioEstatEnumDto);
	}

	@Transactional
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

	@Transactional
	public void crearRegistrePerPeticio(AnotacioRegistreEntrada registreEntrada, ExpedientPeticioEntity expedientPeticioEntity) {
		EntitatEntity entitat = entitatRepository.findByUnitatArrel(
				registreEntrada.getEntitatCodi());
		if (entitat == null) {
			throw new NotFoundException(entitat, EntitatEntity.class);
		}
		RegistreEntity registreEntity = RegistreEntity.getBuilder(
				registreEntrada.getAssumpteTipusCodi(),
				registreEntrada.getData().toGregorianCalendar().getTime(),
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
				origenData(registreEntrada.getOrigenData() != null ? registreEntrada.getOrigenData().toGregorianCalendar().getTime() : null).
				origenRegistreNumero(registreEntrada.getOrigenRegistreNumero()).
				refExterna(registreEntrada.getRefExterna()).
				solicita(registreEntrada.getSolicita()).
				transportNumero(registreEntrada.getTransportNumero()).
				transportTipusCodi(registreEntrada.getTransportTipusCodi()).
				transportTipusDescripcio(registreEntrada.getTransportTipusDescripcio()).
				usuariCodi(registreEntrada.getUsuariCodi()).
				usuariNom(registreEntrada.getUsuariNom()).
				destiDescripcio(registreEntrada.getDestiDescripcio()).
				build();
		registreRepository.save(registreEntity);
		expedientPeticioEntity.updateRegistre(registreEntity);
		// set metaexpedient to which expedient will belong if peticion is accepted
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacioSia(
				entitat,
				expedientPeticioEntity.getRegistre().getProcedimentCodi());
		MetaExpedientEntity metaExpedientEntity = null;
		String metaExpedientNom = null;
		if (!metaExpedients.isEmpty()) {
			metaExpedientEntity = metaExpedients.get(0);
			metaExpedientNom = metaExpedientEntity.getNom();
		}
		expedientPeticioEntity.updateMetaExpedientNom(
				metaExpedientNom);
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
			registreEntity.getAnnexos().add(
					crearAnnexEntity(
							annex,
							registreEntity));
		}
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
				annex.getNtiFechaCaptura().toGregorianCalendar().getTime(),
				annex.getNtiOrigen(),
				annex.getNtiTipoDocumental(),
				annex.getSicresTipoDocumento(),
				annex.getTitol(),
				registreEntity,
				annex.getNtiEstadoElaboracion()).
				contingut(annex.getContingut()).
				firmaContingut(annex.getFirmaContingut()).
				ntiTipoDocumental(annex.getNtiTipoDocumental()).
				sicresTipoDocumento(annex.getSicresTipoDocumento()).
				observacions(annex.getObservacions()).
				sicresValidezDocumento(annex.getSicresValidezDocumento()).
				tipusMime(annex.getTipusMime()).
				uuid(annex.getUuid()).
				firmaNom(annex.getFirmaNom()).
				build();
		registreAnnexRepository.save(annexEntity);
		return annexEntity;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioHelper.class);

}
	
	
	

