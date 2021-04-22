package es.caib.ripea.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.repository.InteressatRepository;

@Component
public class ExpedientInteressatHelper {
	
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	
	@Transactional
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId, //interessatId to which representant will be related to
			InteressatDto interessat,
			boolean propagarArxiu){
		
		if (interessatId != null) {
			logger.debug("Creant nou representant ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "interessatId=" + interessatId + ", "
					+ "interessat=" + interessat + ")");
		} else {
			logger.debug("Creant nou interessat ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "interessat=" + interessat + ")");
		}
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				true,
				false,
				true,
				false,
				false, false);
		InteressatEntity pare = null;
		if (interessatId != null) {
			pare = interessatRepository.findOne(interessatId);
			if (pare == null) {
				throw new NotFoundException(
						interessatId,
						InteressatEntity.class);
			}
		}
		InteressatEntity interessatEntity = null;
		if (interessat.isPersonaFisica()) {
			InteressatPersonaFisicaDto interessatPersonaFisicaDto = (InteressatPersonaFisicaDto)interessat;
			interessatEntity = InteressatPersonaFisicaEntity.getBuilder(
					interessatPersonaFisicaDto.getNom(),
					interessatPersonaFisicaDto.getLlinatge1(),
					interessatPersonaFisicaDto.getLlinatge2(),
					interessatPersonaFisicaDto.getDocumentTipus(),
					interessatPersonaFisicaDto.getDocumentNum(),
					interessatPersonaFisicaDto.getPais(),
					interessatPersonaFisicaDto.getProvincia(),
					interessatPersonaFisicaDto.getMunicipi(),
					interessatPersonaFisicaDto.getAdresa(),
					interessatPersonaFisicaDto.getCodiPostal(),
					interessatPersonaFisicaDto.getEmail(),
					interessatPersonaFisicaDto.getTelefon(),
					interessatPersonaFisicaDto.getObservacions(),
					interessatPersonaFisicaDto.getPreferenciaIdioma(),
					interessatPersonaFisicaDto.getNotificacioAutoritzat(),
					expedient,
					null,
					interessatPersonaFisicaDto.getEntregaDeh(),
					interessatPersonaFisicaDto.getEntregaDehObligat(),
					interessatPersonaFisicaDto.getIncapacitat()).build();
		} else if (interessat.isPersonaJuridica()) {
			InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = (InteressatPersonaJuridicaDto)interessat;
			interessatEntity = InteressatPersonaJuridicaEntity.getBuilder(
					interessatPersonaJuridicaDto.getRaoSocial(),
					interessatPersonaJuridicaDto.getDocumentTipus(),
					interessatPersonaJuridicaDto.getDocumentNum(),
					interessatPersonaJuridicaDto.getPais(),
					interessatPersonaJuridicaDto.getProvincia(),
					interessatPersonaJuridicaDto.getMunicipi(),
					interessatPersonaJuridicaDto.getAdresa(),
					interessatPersonaJuridicaDto.getCodiPostal(),
					interessatPersonaJuridicaDto.getEmail(),
					interessatPersonaJuridicaDto.getTelefon(),
					interessatPersonaJuridicaDto.getObservacions(),
					interessatPersonaJuridicaDto.getPreferenciaIdioma(),
					interessatPersonaJuridicaDto.getNotificacioAutoritzat(),
					expedient,
					null,
					interessatPersonaJuridicaDto.getEntregaDeh(),
					interessatPersonaJuridicaDto.getEntregaDehObligat(),
					interessatPersonaJuridicaDto.getIncapacitat()).build();
		} else {
			InteressatAdministracioDto interessatAdministracioDto = (InteressatAdministracioDto)interessat;
			UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findAmbCodi(
					interessatAdministracioDto.getOrganCodi());
			interessatEntity = InteressatAdministracioEntity.getBuilder(
					unitat.getCodi(),
					unitat.getDenominacio(),
					interessatAdministracioDto.getDocumentTipus(),
					interessatAdministracioDto.getDocumentNum(),
					interessatAdministracioDto.getPais(),
					interessatAdministracioDto.getProvincia(),
					interessatAdministracioDto.getMunicipi(),
					interessatAdministracioDto.getAdresa(),
					interessatAdministracioDto.getCodiPostal(),
					interessatAdministracioDto.getEmail(),
					interessatAdministracioDto.getTelefon(),
					interessatAdministracioDto.getObservacions(),
					interessatAdministracioDto.getPreferenciaIdioma(),
					interessatAdministracioDto.getNotificacioAutoritzat(),
					expedient,
					null,
					interessatAdministracioDto.getEntregaDeh(),
					interessatAdministracioDto.getEntregaDehObligat(),
					interessatAdministracioDto.getIncapacitat()).build();
		}
		if (pare != null) {
			interessatEntity.updateEsRepresentant(true);
		}
		interessatEntity = interessatRepository.save(interessatEntity);
		if (pare != null) {
			pare.updateRepresentant(interessatEntity);
		}
		expedient.addInteressat(interessatEntity);
		
		if (propagarArxiu) {
			pluginHelper.arxiuExpedientActualitzar(expedient);
		}
		
		// Registra al log la creació de l'interessat
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				interessatEntity,
				LogObjecteTipusEnumDto.INTERESSAT,
				LogTipusEnumDto.CREACIO,
				interessatEntity.getIdentificador(),
				null,
				false,
				false);
		
		return conversioTipusHelper.convertir(
							interessatRepository.save(interessatEntity),
							InteressatDto.class);
	}
	
	@Transactional
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessatDto,
			boolean propagarArxiu,
			InteressatDto representantDto){
		
		logger.debug("Actualitzant interessat ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "interessatId=" + interessatId + ")");
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				true,
				false,
				true,
				false,
				false, false);
		InteressatEntity interessatEntity = entityComprovarHelper.comprovarInteressat(
				expedient, 
				interessatId); 
		if (interessatEntity == null) {
			throw new NotFoundException(
					interessatId,
					InteressatEntity.class);
		}
		
		//### Actualitza la informació de l'interessat
		expedientInteressatService.update(
				entitatId,
				expedientId,
				interessatDto);
		
		//### Actualitza la informació del representant
		if (representantDto != null && interessatEntity.getRepresentant() != null) {
			expedientInteressatService.update(
					entitatId,
					expedientId,
					interessatId,
					representantDto);
		}
		
		//### Crear nou representant de l'interessat
		if (representantDto != null && interessatEntity.getRepresentant() == null) {
			expedientInteressatService.create(
					entitatId,
					expedientId,
					interessatId,
					representantDto,
					true);
		}
		
		//### Esborra un representant si no s'ha informat en la petició
		if (representantDto == null && interessatEntity.getRepresentant() != null) {
			expedientInteressatService.delete(
					entitatId, 
					expedientId, 
					interessatId, 
					interessatEntity.getRepresentant().getId());
		}
		
		if (propagarArxiu) {
			pluginHelper.arxiuExpedientActualitzar(expedient);
		}
		
		// Registra al log la modificació de l'interessat
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				interessatEntity,
				LogObjecteTipusEnumDto.INTERESSAT,
				LogTipusEnumDto.MODIFICACIO,
				interessatEntity.getIdentificador(),
				null,
				false,
				false);
		
		return conversioTipusHelper.convertir(
							interessatRepository.save(interessatEntity),
							InteressatDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);
	
	
}