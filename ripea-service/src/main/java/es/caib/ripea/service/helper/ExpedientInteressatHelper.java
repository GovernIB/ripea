package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExpedientInteressatHelper {
	
	@Autowired private InteressatRepository interessatRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private CacheHelper cacheHelper;

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
		
		cacheHelper.evictErrorsValidacioPerNode(expedient);
		
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

		InteressatEntity interessat = interessatRepository.findById(interessatId).orElse(null);
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

		InteressatEntity interessat = interessatRepository.findById(interessatId).orElse(null);
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
			String unitatDenominacio = null;
			if (interessatAdministracioDto.getOrganCodi() != null) {
				unitat = unitatOrganitzativaHelper.findAmbCodi(interessatAdministracioDto.getOrganCodi());
				unitatDenominacio = unitat.getDenominacio();
			}

			interessatEntity = InteressatAdministracioEntity.getBuilder(
					unitat != null ? unitat.getCodi() : null,
					unitatDenominacio,
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
	
	public InteressatDto update(
			Long expedientId,
			Long interessatId,
			InteressatDto interessatRepresentant,
			String rolActual, 
			boolean comprovarAgafatPerUsuariActual, 
			boolean propagarArxiu) {
		if (interessatId != null) {
			logger.debug("Modificant un representant ("
					+ "expedientId=" + expedientId + ", "
					+ "interessatId=" + interessatId + ", "
					+ "interessat=" + interessatRepresentant + ")");
		} else {
			logger.debug("Modificant un interessat ("
					+ "expedientId=" + expedientId + ", "
					+ "interessat=" + interessatRepresentant + ")");
		}

		InteressatEntity interessatRepresentantEntity = updateInteressatRepresentantEntity(
				expedientId,
				interessatId,
				interessatRepresentant,
				rolActual,
				comprovarAgafatPerUsuariActual,
				propagarArxiu);

		return conversioTipusHelper.convertir(
				interessatRepresentantEntity,
				InteressatDto.class);
	}

	//Actualitza les dades que no son null de interessatDto al InteressatEntity amb id interessatId
	public InteressatEntity mergeInteressat(
			Long interessatId,
			InteressatDto interessatDto) {
		InteressatEntity interessat = interessatRepository.getOne(interessatId);
		if (interessat instanceof InteressatPersonaFisicaEntity) {
			((InteressatPersonaFisicaEntity)interessat).merge(interessatDto);
		} else if (interessat instanceof InteressatPersonaJuridicaEntity) {
			((InteressatPersonaJuridicaEntity)interessat).merge(interessatDto);
		} else {
			((InteressatAdministracioEntity)interessat).merge(interessatDto);
		}
		return interessat;
	}
	
	public InteressatEntity updateInteressatRepresentantEntity(
			Long expedientId,
			Long interessatId,
			InteressatDto interessatRepresentant,
			String rolActual,
			boolean comprovarAgafatPerUsuariActual,
			boolean propagarArxiu) {

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
			interessat = interessatRepository.findById(interessatId).orElse(null);
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
		InteressatEntity interessatRepresentantEntity = interessatRepository.getOne(interessatRepresentant.getId());
		InteressatEntity deproxied = HibernateHelper.deproxy(interessatRepresentantEntity);
		if (interessatRepresentant.isAdministracio()) {
			updateOrganNom(interessatRepresentant);
		}
		if (sameTipus(interessatRepresentant.getTipus(), deproxied)) {
			deproxied.update(interessatRepresentant);
			interessatRepresentantEntity = interessatRepository.save(deproxied);
		} else {
			InteressatEntity representant = deproxied.getRepresentant();
			List<InteressatEntity> interessatsAQuiRepresenta = interessatRepository.findByRepresentantId(deproxied.getId());
			// Eliminam l'interessat de totes les representacions
			if (interessatsAQuiRepresenta != null && !interessatsAQuiRepresenta.isEmpty()) {
				for (InteressatEntity inter : interessatsAQuiRepresenta) {
					inter.updateRepresentant(null);
				}
			}
			// Eliminam l'interessat, i el cream de nou.
			interessatRepository.delete(deproxied);
			interessatRepresentantEntity = InteressatEntity.getBuilder(interessatRepresentant, expedient, representant).build();
			interessatRepresentantEntity = interessatRepository.save(interessatRepresentantEntity);
			// Afegim el nou interessat a totes les representacions
			if (interessatsAQuiRepresenta != null && !interessatsAQuiRepresenta.isEmpty()) {
				for (InteressatEntity inter : interessatsAQuiRepresenta) {
					inter.updateRepresentant(interessatRepresentantEntity);
				}
			}
		}

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

		return interessatRepresentantEntity;
	}

	public void updateOrganNom(InteressatDto interessatRepresentant) {
		if (interessatRepresentant.isAdministracio()) {
			InteressatAdministracioDto interessatAdministracioDto = (InteressatAdministracioDto) interessatRepresentant;
			if (interessatAdministracioDto.getOrganCodi() == null)
                return;

            try {
                UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findAmbCodi(interessatAdministracioDto.getOrganCodi());
                if (unitat != null) {
                    interessatAdministracioDto.setOrganCodi(unitat.getCodi());
                    String organRetall = (unitat.getDenominacio()!=null && unitat.getDenominacio().length() > 256) ? unitat.getDenominacio().substring(0, 256) : unitat.getDenominacio();
                    interessatAdministracioDto.setOrganNom(organRetall);
                }
            } catch (Exception e) {
                logger.error("No s'ha pogut actualitzar l'òrgan '" + interessatAdministracioDto.getOrganCodi() + "'de l'interessat/representant");
            }
		}
	}

	private boolean sameTipus(InteressatTipusEnumDto tipus, InteressatEntity interessat) {
		if (tipus == null || interessat == null) return false;

		return interessat instanceof InteressatPersonaFisicaEntity && InteressatTipusEnumDto.PERSONA_FISICA.equals(tipus) ||
				interessat instanceof InteressatPersonaJuridicaEntity && InteressatTipusEnumDto.PERSONA_JURIDICA.equals(tipus) ||
				interessat instanceof InteressatAdministracioEntity && InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus);
	}

	public InteressatEntity updateRepresentant(
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			PermissionEnumDto permission,
			String rolActual) {

		logger.debug("Modificant un representant (expedientId=" + expedientId + ", representant=" + representant + ")");

		ExpedientEntity expedient = getExpedientComprovantPermisos(expedientId, permission, rolActual, false);
		InteressatEntity representantEntity = interessatRepository.getOne(representant.getId());
		InteressatEntity interessat = interessatRepository.findById(interessatId).orElse(null);
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
		if (usosRepresentant < 1) {
			representant.getExpedient().deleteInteressat(representant);
			interessatRepository.deleteById(representant.getId());
			if (interessat.getExpedient().getArxiuUuid() != null) {
				arxiuPropagarInteressats(interessat.getExpedient(), null);
			}
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
		InteressatEntity interessat = interessatRepository.findById(interessatId).orElse(null);
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
			
			if (expedient.getArxiuUuid() != null) {
				arxiuPropagarInteressats(expedient, null);
			}
			
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
			
			cacheHelper.evictErrorsValidacioPerNode(expedient);
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
		InteressatEntity interessat = interessatRepository.findById(interessatId).orElse(null);
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
				
				if (expedient.getArxiuUuid() != null) {
					arxiuPropagarInteressats(expedient, null);
				}
				
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
		ExpedientEntity expedient = expedientRepository.getOne(expId);

		try {
			expedientHelper.concurrencyCheckExpedientJaTancat(expedient);
		} catch (Exception ex) {
			updateArxiuIntentInteressats(expedient, null, false);
			return new RuntimeException("Error guardant interessat en arxiu", ex);
		}
			
		if (expedient.getArxiuUuid() != null) {
			exception = arxiuPropagarInteressats(expedient, null);
		} else {
			updateArxiuIntentInteressats(expedient, null, false);
			exception = new RuntimeException("Error guardant interessat en arxiu: expedient no guardat en arxiu");
		}
		return exception;
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Exception guardarExpedientAndInteressatsArxiu(
			Long expId) {
		ExpedientEntity expedient = expedientRepository.getOne(expId);
		expedient.updateArxiuIntent(true);
		return arxiuPropagarInteressats(expedient, null);
	}

	public String importarInteressats(
			Long entitatId,
			Long expedientId,
			String rolActual,
			List<InteressatDto> interessats,
			List<Long> seleccionats) {
		
		if (seleccionats!=null && seleccionats.size()>0) {

			int numInteressatsUpd = 0;
			int numInteressatsIns = 0;
			Map<String, String> errorsInteressats = new HashMap<String, String>();
			
			if (interessats!=null && interessats.size()>0) {
				
				//Recuperam tots els InteressatDto del expedient, siguin interessats arrel o representants.
				List<InteressatEntity> interessatsActualsExp = interessatRepository.findByExpedientId(expedientId);
				
				//Recorrem els interessats del JSON que s'ha importat
				for (InteressatDto interessat : interessats) {
					logger.debug(" - Importació del interessat "+interessat.getDocumentNum()+" a l'expedient "+expedientId);
					//Si l'usuari ha marcat que el interessat s'ha de importar al expedient actual...
					if (seleccionats.contains(interessat.getId())) {
						
						InteressatEntity interessatProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getDocumentNum());
						if (interessatProcessar==null) {
							//El create, crea el interessat associat al expedient, sense FK cap a representant, i amb es_representant=false
							//És a dir, un interessat arrel del expedient.

							InteressatDto interessatCreatDto = create(expedientId, interessat, true, PermissionEnumDto.WRITE, rolActual, true);
							interessatProcessar = interessatRepository.getOne(interessatCreatDto.getId());
							interessatsActualsExp.add(interessatProcessar);
							numInteressatsIns++;
							logger.debug("   > Interessat creat perque no existia al expedient.");

						} else {
							//El merge no toca ni la FK cap a representant, ni l'atribut es_representant
							//per tant si era interessat haurà actualitzat el interessat, i si era representant, el representant.
							interessatProcessar = mergeInteressat(interessatProcessar.getId(), interessat);
							numInteressatsUpd++;
							logger.debug("   > Interessat mergeat perque ja existia al expedient.");
						}
					}
				}
				
				for (InteressatDto interessat : interessats) {
					if (seleccionats.contains(interessat.getId())) {
						//Un cop actualizades les dades generiques dels interessats, actualitzam les relacions interessat-representant entre ells
						if (interessat.getRepresentant()!=null) {
							InteressatEntity interessatProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getDocumentNum());
							logger.debug(" - Importació del representant "+interessat.getRepresentant().getDocumentNum()+" del interessat "+interessat.getDocumentNum()+" a l'expedient "+expedientId);
							//Si el representant amb numDoc no existeix al expedient (sigui com a representant o com a interessat), es crea com a nou interessat
							InteressatEntity representantProcessar = getInteressatActualExpedientByDocNum(interessatsActualsExp, interessat.getRepresentant().getDocumentNum());
							if (representantProcessar==null) {

								InteressatDto representantCreatDto = create(expedientId, interessat.getRepresentant(), true, PermissionEnumDto.WRITE, rolActual, true);
								representantProcessar = interessatRepository.getOne(representantCreatDto.getId());
								representantProcessar.updateEsRepresentant(true);
								logger.debug("   > S'ha creat el representant perque no existia al expedient.");

							} else {
								
								representantProcessar = mergeInteressat(representantProcessar.getId(), interessat.getRepresentant());
								logger.debug("   > S'ha mergeat el representant perque ja existia al expedient.");
							}
							
							//Ara tenim el representant actualitzat o creat, pero encara no apunta al interessat que estam important
							interessatProcessar.setRepresentant(representantProcessar);
							logger.debug("   > El representant "+interessat.getRepresentant().getDocumentNum()+" s'ha associat al interessat "+interessatProcessar.getDocumentNum()+".");
						}
					}
				}
			}
			
			String resultatStr = "S'han importat <b>"+numInteressatsIns+"</b> nous interessats, i <b>"+numInteressatsUpd+"</b> s'han actualitzat.";
			if (errorsInteressats.size()>0) {
				resultatStr+="<br/>Els seguents interessats no s'han pogut importar:";
				for (Map.Entry<String, String> entry : errorsInteressats.entrySet()) {
					resultatStr+="<br/> - "+entry.getKey()+": "+entry.getValue();
				}
			}
			return resultatStr;
			
		} else {
			return "No s'ha seleccionat interessats per importar.";
		}
	}
	
	private InteressatEntity getInteressatActualExpedientByDocNum(List<InteressatEntity> interessatsActualsExp, String docNum) {
		if (interessatsActualsExp!=null) {
			for (InteressatEntity interessatExistent : interessatsActualsExp) {
				if (interessatExistent.getDocumentNum().equalsIgnoreCase(docNum)) {
					return interessatExistent;
				}
			}
		}
		return null;
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
            expedient.updateArxiuIntent(arxiuPropagat);
            
            //SI s'ha propagat correctament l'expedient, es que els interessats han quedat propagats també
            if (arxiuPropagat) {
            	if (expedient.getInteressatsORepresentants()!=null) {
            		for (InteressatEntity interessatExp: expedient.getInteressatsORepresentants()) {
            			interessatExp.updateArxiuIntent(arxiuPropagat);
            		}
            	}
            }
		}
	}

	public List<InteressatDto> findByIds(List<Long> ids) {
		List<InteressatDto> interessatsExportar = new ArrayList<InteressatDto>();
		for (Long interessatId: ids) {
			interessatsExportar.add(conversioTipusHelper.convertir(interessatRepository.findById(interessatId), InteressatDto.class));
		}
		return interessatsExportar;
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