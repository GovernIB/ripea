/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.InteressatPersonaJuridicaEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientInteressatHelper;
import es.caib.ripea.core.helper.HibernateHelper;
import es.caib.ripea.core.helper.UnitatOrganitzativaHelper;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.InteressatRepository;

/**
 * Implementació dels mètodes per a gestionar interessats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientInteressatServiceImpl implements ExpedientInteressatService {

	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private ExpedientInteressatHelper expedientInteressatHelper;

	@Override
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			InteressatDto interessat) {
		return create(entitatId, expedientId, null, interessat, true);
	}

	@Override
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessat,
			boolean propagarArxiu) {
		return expedientInteressatHelper.create(
				entitatId,
				expedientId,
				interessatId,
				interessat,
				propagarArxiu);
	}

	@Transactional
	@Override
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			InteressatDto interessat) {
		return update(entitatId, expedientId, null, interessat);
	}

	@Transactional
	@Override
	public InteressatDto update(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessat) {
		if (interessatId != null) {
			logger.debug("Modificant un representant ("
					+ "entitatId=" + entitatId + ", "
					+ "expedientId=" + expedientId + ", "
					+ "interessatId=" + interessatId + ", "
					+ "interessat=" + interessat + ")");
		} else {
			logger.debug("Modificant un interessat ("
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
			if (pare == null || 
				pare.getRepresentant() == null || 
				!pare.getRepresentant().getId().equals(interessat.getId())) {
				throw new NotFoundException(
						interessatId,
						InteressatEntity.class);
			}
		}
		InteressatEntity interessatEntity = null;
		if (interessat.isPersonaFisica()) {
			InteressatPersonaFisicaDto interessatPersonaFisicaDto = (InteressatPersonaFisicaDto)interessat;
			interessatEntity = interessatRepository.findPersonaFisicaById(interessat.getId());
			((InteressatPersonaFisicaEntity)interessatEntity).update(
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
					interessatPersonaFisicaDto.getEntregaDeh(),
					interessatPersonaFisicaDto.getEntregaDehObligat(),
					interessatPersonaFisicaDto.getIncapacitat());
		} else if (interessat.isPersonaJuridica()) {
			InteressatPersonaJuridicaDto interessatPersonaJuridicaDto = (InteressatPersonaJuridicaDto)interessat;
			interessatEntity = interessatRepository.findPersonaJuridicaById(interessat.getId());
			((InteressatPersonaJuridicaEntity)interessatEntity).update(
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
					interessatPersonaJuridicaDto.getEntregaDeh(),
					interessatPersonaJuridicaDto.getEntregaDehObligat(),
					interessatPersonaJuridicaDto.getIncapacitat());
		} else {
			InteressatAdministracioDto interessatAdministracioDto = (InteressatAdministracioDto)interessat;
			interessatEntity = interessatRepository.findAdministracioById(interessat.getId());
			UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findAmbCodi(
					interessatAdministracioDto.getOrganCodi());
			((InteressatAdministracioEntity)interessatEntity).update(
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
					interessatAdministracioDto.getEntregaDeh(),
					interessatAdministracioDto.getEntregaDehObligat(),
					interessatAdministracioDto.getIncapacitat());
		}
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
		if (interessat instanceof InteressatPersonaFisicaDto) {
			return conversioTipusHelper.convertir(
					interessatRepository.save(interessatEntity),
					InteressatPersonaFisicaDto.class);
		} else if (interessat instanceof InteressatPersonaJuridicaDto) {
			return conversioTipusHelper.convertir(
					interessatRepository.save(interessatEntity),
					InteressatPersonaJuridicaDto.class);
		} else {
			return conversioTipusHelper.convertir(
					interessatRepository.save(interessatEntity),
					InteressatAdministracioDto.class);
		}
	}

	@Transactional
	@Override
	public void delete(
			Long entitatId,
			Long expedientId,
			Long interessatId) {
		logger.debug("Esborrant interessat de l'expedient  ("
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
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat != null) {
			interessatRepository.delete(interessat);
			//expedient.deleteInteressat(interessat);
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

	@Transactional
	@Override
	public void delete(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			Long representantId) {
		logger.debug("Esborrant interessat de l'expedient  ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ", "
				+ "interessatId=" + interessatId + ", "
				+ "representantId=" + representantId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				true,
				false,
				true,
				false,
				false, false);
		InteressatEntity interessat = interessatRepository.findOne(interessatId);
		if (interessat != null) {
			if (interessat.getRepresentant() != null && 
				interessat.getRepresentant().getId().equals(representantId)) {
				InteressatEntity representant = interessatRepository.findOne(representantId);
				interessat.updateRepresentant(null);
				interessatRepository.delete(representant);
				//expedient.deleteInteressat(interessat);
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

	@Transactional(readOnly = true)
	@Override
	public InteressatDto findById(Long id) {
		logger.debug("Consulta de l'interessat ("
				+ "id=" + id + ")");
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				null,
				id);
		
		if (interessat instanceof InteressatPersonaFisicaEntity)
			return conversioTipusHelper.convertir(
					interessat,
					InteressatPersonaFisicaDto.class);
		else if (interessat instanceof InteressatPersonaJuridicaEntity)
			return conversioTipusHelper.convertir(
					interessat,
					InteressatPersonaJuridicaDto.class);
		else if (interessat instanceof InteressatAdministracioEntity)
			return conversioTipusHelper.convertir(
					interessat,
					InteressatAdministracioDto.class);
		
		return conversioTipusHelper.convertir(
				interessat,
				InteressatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public InteressatDto findRepresentantById(
			Long interessatId,
			Long id) {
		logger.debug("Consulta de l'interessat ("
				+ "id=" + id + ")");
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				null,
				interessatId);
		if (interessat.getRepresentantId() == null || !interessat.getRepresentantId().equals(id)) {
			throw new ValidationException(
					id,
					InteressatEntity.class,
					"El representant especificat (id=" + id + ") no pertany a l'interessat (id=" + interessatId + ")");
		}
		InteressatEntity representant = HibernateHelper.deproxy(interessat.getRepresentant());
		return conversioTipusHelper.convertir(
				representant,
				InteressatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId,
			boolean nomesAmbNotificacioActiva) {
		logger.debug("Consulta interessats de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false);
		
		List<InteressatEntity> interessats = new ArrayList<>();
		if (nomesAmbNotificacioActiva) {
			interessats = interessatRepository.findByExpedientAndNotRepresentantAndNomesAmbNotificacioActiva(
					expedient);
		} else {
			interessats = interessatRepository.findByExpedientAndNotRepresentant(
					expedient);
		}

		List<InteressatDto> resposta = new ArrayList<InteressatDto>();
		for (InteressatEntity interessat: interessats) {
			resposta.add(conversioTipusHelper.convertir(
					interessat,
					InteressatDto.class));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public long countByExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Consulta interessats de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false);
		return interessatRepository.countByExpedient(
				expedient);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatDto> findAmbDocumentPerNotificacio(
			Long entitatId,
			Long documentId) {
		logger.debug("Consulta interessats del document per notificacions ("
				+ "entitatId=" + entitatId + ", "
				+ "documentId=" + documentId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false);
		DocumentEntity document = entityComprovarHelper.comprovarDocument(
				entitat,
				null,
				documentId,
				false,
				false,
				false,
				false);
		ExpedientEntity expedient = contingutHelper.getExpedientSuperior(
				document,
				false,
				false,
				true, false);
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no pertany a cap expedient");
		}
		List<InteressatEntity> interessats = interessatRepository.findByExpedientPerNotificacions(
				expedient);
		List<InteressatDto> resposta = new ArrayList<InteressatDto>();
		for (InteressatEntity interessat: interessats) {
			if (interessat instanceof InteressatPersonaFisicaEntity)
				resposta.add(conversioTipusHelper.convertir(
						interessat,
						InteressatPersonaFisicaDto.class));
			else if (interessat instanceof InteressatPersonaJuridicaEntity)
				resposta.add(conversioTipusHelper.convertir(
						interessat,
						InteressatAdministracioDto.class));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) {
		logger.debug("Consulta interessats de tipus ciutadà ("
				+ "nom=" + nom + ", "
				+ "documentNum=" + documentNum + ", "
				+ "llinatge1=" + llinatge1 + ", "
				+ "llinatge2=" + llinatge2 + ")");
		
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		return conversioTipusHelper.convertirList(
				interessatRepository.findByFiltrePersonaFisica(
						nom == null || nom.isEmpty(),
						nom,
						documentNum == null || documentNum.isEmpty(),
						documentNum,
						llinatge1 == null || llinatge1.isEmpty(),
						llinatge1,
						llinatge2 == null || llinatge2.isEmpty(),
						llinatge2,
						expedient),
				InteressatPersonaFisicaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) {
		logger.debug("Consulta interessats de tipus ciutadà ("
				+ "raoSocial=" + raoSocial + ", "
				+ "documentNum=" + documentNum + ")");
		
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		return conversioTipusHelper.convertirList(
				interessatRepository.findByFiltrePersonaJuridica(
						documentNum == null || documentNum.isEmpty(),
						documentNum,
						raoSocial == null || raoSocial.isEmpty(),
						raoSocial,
						expedient),
				InteressatPersonaJuridicaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InteressatAdministracioDto> findByFiltreAdministracio(
			String organCodi,
			Long expedientId) {
		logger.debug("Consulta interessats de tipus administració ("
				+ "organCodi=" + organCodi + ")");
		
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		List<InteressatAdministracioEntity> administracions = interessatRepository.findByFiltreAdministracio(
				organCodi == null || organCodi.isEmpty(),
				organCodi,
				expedient);
		return conversioTipusHelper.convertirList(
				administracions,
				InteressatAdministracioDto.class);
	}

	@Override
	public List<InteressatDto> findByExpedientAndDocumentNum(
			String documentNum,
			Long expedientId) throws NotFoundException {
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		return conversioTipusHelper.convertirList(
				interessatRepository.findByExpedientAndDocumentNum(expedient, documentNum),
				InteressatDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatServiceImpl.class);

	@Override
	public List<InteressatDto> findByText(String text) {
		return conversioTipusHelper.convertirList(
				interessatRepository.findByText(text),
				InteressatDto.class);
	}

	@Override
	public InteressatDto findByDocumentNum(String documentNum) throws NotFoundException {
		return conversioTipusHelper.convertir(
				interessatRepository.findByDocumentNum(documentNum).get(0),
				InteressatDto.class);
	}

}
