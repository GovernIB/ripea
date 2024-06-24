package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
	private ConfigHelper configHelper;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ContingutHelper contingutHelper;


	@Transactional
	public InteressatEntity create(
			Long expedientId,
			InteressatDto interessat,
			PermissionEnumDto permission,
			String rolActual){

		logger.debug("Creant nou interessatEntity (expedientId=" + expedientId + ", interessat=" + interessat + ")");

		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, false);
		InteressatEntity interessatEntity = createDB(expedient, interessat);

		return interessatEntity;
	}

	@Transactional
	public InteressatDto create(
			Long expedientId,
			InteressatDto interessat,
			boolean propagarArxiu,
			PermissionEnumDto permission, 
			String rolActual, 
			boolean comprovarAgafat){
		
		logger.debug("Creant nou interessat (expedientId=" + expedientId + ", interessat=" + interessat + ")");

		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, comprovarAgafat);
		InteressatEntity interessatEntity = createDB(expedient, interessat);
		
		if (propagarArxiu && expedient.getArxiuUuid() != null) {
			arxiuPropagarInteressats(expedient, interessatEntity);
		}
		
		return conversioTipusHelper.convertir(
							interessatEntity,
							InteressatDto.class);
	}

	@Transactional
	public InteressatEntity createRepresentant(
			Long expedientId,
			Long interessatId, //interessatId to which representant will be related to
			InteressatDto representant,
			PermissionEnumDto permission,
			String rolActual){

		logger.debug("Creant nou representantEntity (expedientId=" + expedientId + ", interessatId=" + interessatId + ", representant=" + representant + ")");

		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, false);
		InteressatEntity representantEntity = createDB(expedient, representant);

		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NotFoundException(interessatId, InteressatEntity.class);
		}
		representantEntity.updateEsRepresentant(true);
		interessat.updateRepresentant(representantEntity);

		return representantEntity;
	}

	@Transactional
	public InteressatDto createRepresentant(
			Long expedientId,
			Long interessatId, //interessatId to which representant will be related to
			InteressatDto representant,
			boolean propagarArxiu, 
			PermissionEnumDto permission, 
			String rolActual, 
			boolean comprovarAgafat){
		

		logger.debug("Creant nou representant ("
				+ "expedientId=" + expedientId + ", "
				+ "interessatId=" + interessatId + ", "
				+ "representant=" + representant + ")");


		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, comprovarAgafat);
		InteressatEntity representantEntity = createDB(expedient, representant);

		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NotFoundException(interessatId, InteressatEntity.class);
		}
		representantEntity.updateEsRepresentant(true);
		interessat.updateRepresentant(representantEntity);
		
		if (propagarArxiu && expedient.getArxiuUuid() != null) {
			arxiuPropagarInteressats(expedient, representantEntity);
		}
		
		return conversioTipusHelper.convertir(
							interessatRepository.save(representantEntity),
							InteressatDto.class);
	}
	
	
	@Transactional
	public InteressatEntity createDB(
			ExpedientEntity expedient,
			InteressatDto interessat){

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
					expedient,
					null,
					interessatPersonaJuridicaDto.getEntregaDeh(),
					interessatPersonaJuridicaDto.getEntregaDehObligat(),
					interessatPersonaJuridicaDto.getIncapacitat()).build();
		} else {
			InteressatAdministracioDto interessatAdministracioDto = (InteressatAdministracioDto)interessat;
			
			UnitatOrganitzativaDto unitat = null;
			if (interessatAdministracioDto.getOrganCodi() != null) {
				unitat = unitatOrganitzativaHelper.findAmbCodi(
						interessatAdministracioDto.getOrganCodi());
			}

			interessatEntity = InteressatAdministracioEntity.getBuilder(
					unitat != null ? unitat.getCodi() : null,
					unitat != null ? unitat.getDenominacio() : null,
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
					expedient,
					null,
					interessatAdministracioDto.getEntregaDeh(),
					interessatAdministracioDto.getEntregaDehObligat(),
					interessatAdministracioDto.getIncapacitat(),
					interessatAdministracioDto.getAmbOficinaSir()).build();
		}
		
		interessatEntity = interessatRepository.save(interessatEntity);
		expedient.addInteressat(interessatEntity);
		
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
		
		return interessatEntity;
	}

	private ExpedientEntity getExpedientComprovantPermisos(Long expedientId, PermissionEnumDto permission, String rolActual, boolean comprovarAgafat) {
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				comprovarAgafat,
				permission.equals(PermissionEnumDto.READ),
				permission.equals(PermissionEnumDto.WRITE),
				permission.equals(PermissionEnumDto.CREATE),
				permission.equals(PermissionEnumDto.DELETE),
				rolActual);
		return expedient;
	}
	
	
//	@Transactional
//	public InteressatDto update(
//			Long entitatId,
//			Long expedientId,
//			Long interessatId,
//			InteressatDto interessatDto,
//			boolean propagarArxiu,
//			InteressatDto representantDto, 
//			String rolActual){
//		
//		logger.debug("Actualitzant interessat ("
//				+ "entitatId=" + entitatId + ", "
//				+ "expedientId=" + expedientId + ", "
//				+ "interessatId=" + interessatId + ")");
//		
//		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
//				entitatId,
//				expedientId,
//				false,
//				false,
//				true,
//				false,
//				false, 
//				false, 
//				rolActual);
//		InteressatEntity interessatEntity = entityComprovarHelper.comprovarInteressat(
//				expedient, 
//				interessatId); 
//		if (interessatEntity == null) {
//			throw new NotFoundException(
//					interessatId,
//					InteressatEntity.class);
//		}
//		
//		//### Actualitza la informació de l'interessat
//		update(
//				entitatId,
//				expedientId,
//				null,
//				interessatDto, 
//				rolActual, 
//				false);
//		
//		//### Actualitza la informació del representant
//		if (representantDto != null && interessatEntity.getRepresentant() != null) {
//			update(
//					entitatId,
//					expedientId,
//					interessatId,
//					representantDto, 
//					rolActual, 
//					false);
//		}
//		
//		//### Crear nou representant de l'interessat
//		if (representantDto != null && interessatEntity.getRepresentant() == null) {
//			create(
//					entitatId,
//					expedientId,
//					interessatId,
//					representantDto,
//					propagarArxiu, 
//					PermissionEnumDto.WRITE, 
//					rolActual, 
//					true);
//		}
//		
//		//### Esborra un representant si no s'ha informat en la petició
//		if (representantDto == null && interessatEntity.getRepresentant() != null) {
//			deleteRepresentant(
//					entitatId, 
//					expedientId, 
//					interessatId, 
//					interessatEntity.getRepresentant().getId(), 
//					rolActual);
//		}
//		
//		if (propagarArxiu) {
//			pluginHelper.arxiuExpedientActualitzar(expedient);
//		}
//		
//		
//		return conversioTipusHelper.convertir(
//							interessatRepository.save(interessatEntity),
//							InteressatDto.class);
//	}
	
	
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessatRepresentant,
			String rolActual, 
			boolean comprovarAgafatPerUsuariActual, 
			boolean propagarArxiu) {
		if (interessatId != null) {
			logger.debug("Modificant un representant ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "interessatId=" + interessatId + ", "
					+ "interessat=" + interessatRepresentant + ")");
		} else {
			logger.debug("Modificant un interessat ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "interessat=" + interessatRepresentant + ")");
		}
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				comprovarAgafatPerUsuariActual,
				false,
				true,
				false,
				false,
				rolActual);
		InteressatEntity interessat = null;
		Boolean associarRepresentant = false;
		if (interessatId != null) {
			interessat = interessatRepository.findOne(interessatId);
			// TODO: Tractar cas en que es canvia de representant, o en modifica el representant
			if (interessat == null) {
				throw new NotFoundException(
						interessatId,
						InteressatEntity.class);
			}
			// Si canviam de representant, l'eliminam de l'interessant actual
			if (interessat.getRepresentant() != null && !interessat.getRepresentant().getId().equals(interessatRepresentant.getId())) {
				removeRepresentant(interessat);
				associarRepresentant = true;
			}
			if (interessat.getRepresentant() == null) {
				associarRepresentant = true;
			}
		}
		InteressatEntity interessatRepresentantEntity = interessatRepository.findOne(interessatRepresentant.getId());
		InteressatEntity deproxied = HibernateHelper.deproxy(interessatRepresentantEntity);
		if (interessatRepresentant.isAdministracio()) {
			updateOrganNom(interessatRepresentant);
		}
		if (sameTipus(interessatRepresentant.getTipus(), deproxied)) {
			deproxied.update(interessatRepresentant);
		} else {
			InteressatEntity representant = deproxied.getRepresentant();
			interessatRepository.delete(deproxied);


			interessatRepresentantEntity = InteressatEntity.getBuilder(interessatRepresentant, expedient, representant).build();
		}

		interessatRepresentantEntity = interessatRepository.save(deproxied);
		if (associarRepresentant) {
			interessat.updateRepresentant(interessatRepresentantEntity);
		}
		// Registra al log la modificació de l'interessat
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				interessatRepresentantEntity,
				LogObjecteTipusEnumDto.INTERESSAT,
				LogTipusEnumDto.MODIFICACIO,
				null,
				null,
				false,
				false);

		
		if (propagarArxiu && expedient.getArxiuUuid() != null) {
			
			arxiuPropagarInteressats(expedient, interessatRepresentantEntity);
		}
		
		return conversioTipusHelper.convertir(
				interessatRepresentantEntity,
				InteressatDto.class);
	}

	private void updateOrganNom(InteressatDto interessatRepresentant) {
		if (interessatRepresentant.isAdministracio()) {
			InteressatAdministracioDto interessatAdministracioDto = (InteressatAdministracioDto) interessatRepresentant;
			UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findAmbCodi(interessatAdministracioDto.getOrganCodi());
			interessatAdministracioDto.setOrganCodi(unitat != null ? unitat.getCodi() : null);
			interessatAdministracioDto.setOrganNom(unitat != null ? unitat.getDenominacio() : null);
		}
	}

	private boolean sameTipus(InteressatTipusEnumDto tipus, InteressatEntity interessat) {
		if (tipus == null || interessat == null) return false;

		return interessat instanceof InteressatPersonaFisicaEntity && InteressatTipusEnumDto.PERSONA_FISICA.equals(tipus) ||
				interessat instanceof InteressatPersonaJuridicaEntity && InteressatTipusEnumDto.PERSONA_JURIDICA.equals(tipus) ||
				interessat instanceof InteressatAdministracioEntity && InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus);
	}

	public InteressatEntity update(
			Long expedientId,
			InteressatDto interessat,
			PermissionEnumDto permission,
			String rolActual) {

		logger.debug("Modificant un interessat (expedientId=" + expedientId + ", interessat=" + interessat + ")");

		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, false);
		InteressatEntity interessatEntity = interessatRepository.findOne(interessat.getId());

//		InteressatEntity deproxied = HibernateHelper.deproxy(interessatEntity);
//		deproxied.update(interessat);
		interessatEntity.update(interessat);
		interessatEntity.updateEsRepresentant(false);
		interessatEntity = interessatRepository.save(interessatEntity);

		// Registra al log la modificació de l'interessat
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				interessatEntity,
				LogObjecteTipusEnumDto.INTERESSAT,
				LogTipusEnumDto.MODIFICACIO,
				null,
				null,
				false,
				false);

		return interessatEntity;
	}

	public InteressatEntity updateRepresentant(
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			PermissionEnumDto permission,
			String rolActual) {

		logger.debug("Modificant un representant (expedientId=" + expedientId + ", representant=" + representant + ")");

		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, false);
		InteressatEntity representantEntity = interessatRepository.findOne(representant.getId());
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat == null) {
			throw new NotFoundException(interessatId, InteressatEntity.class);
		}

		// Actualitzam les dades del representant
		representantEntity.update(representant);
		representantEntity = interessatRepository.save(representantEntity);

		// Si canviam de representant, l'eliminam de l'interessant actual
		if (interessat.getRepresentant() != null && !representantEntity.getDocumentNum().equalsIgnoreCase(interessat.getRepresentant().getDocumentNum())) {
			removeRepresentant(interessat);
		}
		interessat.updateRepresentant(representantEntity);
		interessatRepository.save(interessat);

		// Registra al log la modificació de l'interessat
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				representantEntity,
				LogObjecteTipusEnumDto.INTERESSAT,
				LogTipusEnumDto.MODIFICACIO,
				null,
				null,
				false,
				false);

		return representantEntity;
	}

	// Treure representant de l'interessat, i borrar-lo de BBDD si és necessari
	public void removeRepresentant(InteressatEntity interessat) {
		InteressatEntity representant = interessat.getRepresentant();
		if (representant == null) return;

		Long representantId = representant.getId();
		interessat.updateRepresentant(null);
		interessatRepository.save(interessat);

		// Si el representant també és interessat (esRepresentant == false), no l'eliminam de BBDD
		if (!representant.isEsRepresentant()) return;

		// Si només s'està utilitzant una vegada (en l'interessat, l'eliminam de BBDD
		Integer usosRepresentant = interessatRepository.countByRepresentantId(representantId);
		if (usosRepresentant <= 1) {
			representant.getExpedient().deleteInteressat(representant);
			interessatRepository.delete(representant.getId());
		}

	}
	
	

	public void delete(
			Long entitatId,
			Long expedientId,
			Long interessatId, 
			String rolActual) {
		logger.debug("Esborrant interessat de l'expedient  ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "interessatId=" + interessatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				true,
				false,
				true,
				false,
				false,
				rolActual);
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat != null) {
			// Primer eliminam el representant
			removeRepresentant(interessat);

			// Si aquest interessat s'està utilitzant en més d'un interressat-representant, no es borra de BBDD
			Integer usosRepresentant = interessatRepository.countByRepresentantId(interessatId);
			interessat.updateRepresentant(null);
			// Marcar com a representant si s'està utilitzant com a representant d'altres interessats
			if (usosRepresentant > 0) {
				interessat.updateEsRepresentant(true);
				interessatRepository.save(interessat);
				return;
			}

			interessatRepository.delete(interessat);
			expedient.deleteInteressat(interessat);
			// Registra al log la baixa de l'interessat
			contingutLogHelper.log(
					expedient,
					LogTipusEnumDto.MODIFICACIO,
					interessat,
					LogObjecteTipusEnumDto.INTERESSAT,
					LogTipusEnumDto.ELIMINACIO,
					null,
					null,
					false,
					false);
		} else {
			logger.error("No s'ha trobat l'interessat a l'expedient ("
					+ "expedientId=" + expedientId + ", "
					+ "interessatId=" + interessatId + ")");
			throw new ValidationException(
					interessatId,
					InteressatEntity.class,
					"No s'ha trobat l'interessat a l'expedient (expedientId=" + expedientId + ")");
		}
	}
	
	

	public void deleteRepresentant(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			Long representantId, 
			String rolActual) {
		logger.debug("Esborrant interessat de l'expedient  ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "interessatId=" + interessatId + ", "
				+ "representantId=" + representantId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				true,
				false,
				true,
				false,
				false,
				rolActual);
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		// Si aquest representant s'està utilitzant com a interessat o com a representant d'un altre interessat, no es borra de BBDD
		if (interessat != null) {
			InteressatEntity representant = interessat.getRepresentant();
			if (representant != null && representant.getId().equals(representantId)) {
//				InteressatEntity representant = interessatRepository.findOne(representantId);
				Integer usosRepresentant = interessatRepository.countByRepresentantId(representantId);
				interessat.updateRepresentant(null);
				// representant també és interessat || representant de més d'un interessat
				if (!representant.isEsRepresentant() || usosRepresentant > 1) {
					return;
				}

				interessatRepository.delete(representant);
				expedient.deleteInteressat(representant);
				// Registra al log la baixa de l'interessat
				contingutLogHelper.log(
						expedient,
						LogTipusEnumDto.MODIFICACIO,
						interessat,
						LogObjecteTipusEnumDto.INTERESSAT,
						LogTipusEnumDto.ELIMINACIO,
						null,
						null,
						false,
						false);
			} else {
				logger.error("No s'ha trobat el representant de l'interessat ("
						+ "expedientId=" + expedientId + ", "
						+ "interessatId=" + interessatId + ", "
						+ "representantId=" + representantId + ")");
				throw new ValidationException(
						representantId,
						InteressatEntity.class,
						"No s'ha trobat el representant de l'interessat (interessatId=" + interessatId + ")");
			}
		} else {
			logger.error("No s'ha trobat l'interessat a l'expedient ("
					+ "expedientId=" + expedientId + ", "
					+ "interessatId=" + interessatId + ")");
			throw new ValidationException(
					interessatId,
					InteressatEntity.class,
					"No s'ha trobat l'interessat a l'expedient (expedientId=" + expedientId + ")");
		}
	}
	
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Exception guardarInteressatsArxiu(
			Long expId) {
		
		logger.info("Guardar interessats arxiu (id=" + expId + ", entitatCodi=" + configHelper.getEntitatActualCodi() + ")");
		
		Exception exception = null;
		ExpedientEntity expedient = expedientRepository.findOne(expId);
		
		expedientHelper.concurrencyCheckExpedientJaTancat(expedient);
			
		if (expedient.getArxiuUuid() != null) {
			exception = arxiuPropagarInteressats(expedient, null);
		} else {
			updateArxiuIntentInteressats(
					expedient,
					null,
					false);
			exception = new RuntimeException("Expedient de aquest interessat no es guardat en arxiu");
		}
		return exception;
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Exception guardarExpedientAndInteressatsArxiu(
			Long expId) {
		ExpedientEntity expedient = expedientRepository.findOne(expId);
		expedient.updateArxiuIntent();
		return arxiuPropagarInteressats(expedient, null);
	}

	public Exception arxiuPropagarInteressats(
			ExpedientEntity expedient,
			InteressatEntity interessat) {
		
		Exception exception = null;
		try {
			contingutHelper.arxiuPropagarModificacio(expedient);
			
			updateArxiuIntentInteressats(
					expedient,
					interessat,
					true);

		} catch (Exception e) {
			logger.error("Error al custodiar interessats en arxiu (" +
					"expedient id=" + expedient.getId() + ", entitatCodi=" + configHelper.getEntitatActualCodi() + ")",
					e);
			exception = e;
			
			updateArxiuIntentInteressats(
					expedient,
					interessat,
					false);

		}
		return exception;
	}
	
	
	public void updateArxiuIntentInteressats(
			ExpedientEntity expedient,
			InteressatEntity interessat,
			boolean arxiuPropagat) {

		if (interessat != null) {
			interessat.updateArxiuIntent(arxiuPropagat);
		} else {
			for (InteressatEntity inter : expedient.getInteressatsORepresentants()) {
				inter.updateArxiuIntent(arxiuPropagat);
			}
		}
	}
	
	
	public List<InteressatEntity> findByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(
			ExpedientEntity expedient) {

		List<InteressatEntity> interessats = new ArrayList<>();

		interessats.addAll(interessatRepository.findPersFisicByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(expedient));
		interessats.addAll(interessatRepository.findPersJuridByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(expedient));
		interessats.addAll(interessatRepository.findAdminByExpedientAndNotRepresentantAndAmbDadesPerNotificacio(expedient));
		return interessats;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientHelper.class);
	
	
}