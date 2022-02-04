/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.ExecucioMassivaException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity.ExecucioMassivaEstat;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity.ExecucioMassivaTipus;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExceptionHelper;
import es.caib.ripea.core.helper.ExecucioMassivaHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.core.repository.ExecucioMassivaRepository;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExecucioMassivaServiceImpl implements ExecucioMassivaService {

	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ExecucioMassivaHelper execucioMassivaHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private MessageHelper messageHelper;

	@Transactional
	@Override
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		
		ExecucioMassivaEntity execucioMassiva = null;
		
		Date dataInici;
		if (dto.getDataInici() == null) {
			dataInici = new Date();
		} else {
			dataInici = dto.getDataInici();
		}
		
		if (dto.getTipus() == ExecucioMassivaTipusDto.PORTASIGNATURES) {
			execucioMassiva = ExecucioMassivaEntity.getBuilder(
					ExecucioMassivaTipus.valueOf(dto.getTipus().toString()),
					dataInici,
					dto.getMotiu(), 
					dto.getPrioritat(),
					dto.getPortafirmesResponsablesString(),
					dto.getPortafirmesSequenciaTipus(),
					dto.getPortafirmesFluxId(),
					dto.getPortafirmesTransaccioId(),
					dto.getDataCaducitat(), 
					dto.getEnviarCorreu(),
					entitat.getId(),
					dto.getRolActual()).build();
		}
		
		int ordre = 0;
		for (Long contingutId: dto.getContingutIds()) {
			ContingutEntity contingut = contingutRepository.findOne(contingutId);
			ExecucioMassivaContingutEntity emc = ExecucioMassivaContingutEntity.getBuilder(
					execucioMassiva, 
					contingut, 
					ordre++).build();
			
			execucioMassiva.addContingut(emc);
		}
		
		execucioMassivaRepository.save(execucioMassiva);
	}
	

	
	@Override
	public void executarExecucioMassiva(Long execucioMassivaContingutId) {
//		execucioMassivaHelper.executarExecucioMassiva(execucioMassivaContingutId);
	}
	

	

	
	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false, false);
		
		Pageable paginacio = new PageRequest(pagina, 8, Direction.DESC, "dataInici");		
		
		List<ExecucioMassivaEntity> exmEntities = new ArrayList<ExecucioMassivaEntity>();
		if (usuari == null) {
			exmEntities = execucioMassivaRepository.findByEntitatId(entitat.getId(), paginacio);
		} else {
			UsuariEntity usuariEntity = usuariRepository.findByCodi(usuari.getCodi());
			exmEntities = execucioMassivaRepository.findByCreatedByAndEntitatId(usuariEntity, entitat.getId(), paginacio);
		}
		
		return recompteErrors(exmEntities);
	}

	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesGlobals() throws NotFoundException {
		List<ExecucioMassivaEntity> entities = execucioMassivaRepository.findAll();
		return recompteErrors(entities);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findOne(exm_id);
		if (execucioMassiva == null)
			throw new NotFoundException(exm_id, ExecucioMassivaEntity.class);
		
		List<ExecucioMassivaContingutEntity> continguts = execucioMassivaContingutRepository.findByExecucioMassivaOrderByOrdreAsc(execucioMassiva);
		List<ExecucioMassivaContingutDto> dtos = conversioTipusHelper.convertirList(continguts, ExecucioMassivaContingutDto.class);
		
		return dtos;
	}

	@Transactional
	@Override
	public void comprovarExecucionsMassives() {
		logger.trace("Execució tasca periòdica: Execucions massives");

		try {
			List<ExecucioMassivaEntity> massives = execucioMassivaRepository.getMassivesPerProcessar(new Date());

			if (massives != null) {
				for (ExecucioMassivaEntity execucioMassiva : massives) {
					if (execucioMassiva.getContinguts() != null) {
						for (ExecucioMassivaContingutEntity execucioMassivaEntity : execucioMassiva.getContinguts()) {
							executarExecucioMassivaContingut(execucioMassivaEntity.getId());
						}
					}
					execucioMassiva.updateDataFi(new Date());
					execucioMassivaRepository.save(execucioMassiva);
				}
			}

		} catch (Exception e) {
			logger.error("Error al fer execucio massiva", e);
		}
	}
	
	public Throwable executarExecucioMassivaContingut(Long execucioMassivaContingutId) {

		Throwable throwable = execucioMassivaHelper.executarExecucioMassivaContingutNewTransaction(execucioMassivaContingutId);
		
		if (throwable != null) {
			ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(execucioMassivaContingutId);
			
			Throwable excepcioRetorn = ExceptionHelper.getRootCauseOrItself(throwable);
			ExecucioMassivaException execucioMassivaException = new ExecucioMassivaException(
					emc.getContingut().getId(),
					emc.getContingut().getNom(),
					emc.getContingut().getTipus(),
					emc.getExecucioMassiva().getId(),
					emc.getId(),
					excepcioRetorn);
			
			
			emc.updateError(
					new Date(), 
					execucioMassivaException.getMessage().length() < 2045 ? execucioMassivaException.getMessage() : execucioMassivaException.getMessage().substring(0, 2045));
			
			execucioMassivaContingutRepository.save(emc);
			
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.executar.execucio.massiva.error",
							new Object[] {execucioMassivaContingutId}),
					execucioMassivaException,
					emc.getContingut().getId());

			}

		return throwable;
	}
	




	private List<ExecucioMassivaDto> recompteErrors(List<ExecucioMassivaEntity> exmEntities) {
		List<ExecucioMassivaDto> dtos = new ArrayList<ExecucioMassivaDto>();
		for (ExecucioMassivaEntity exm: exmEntities) {
			ExecucioMassivaDto dto = conversioTipusHelper.convertir(exm, ExecucioMassivaDto.class);
			int errors = 0;
			Long pendents = 0L;
			for (ExecucioMassivaContingutEntity emc: exm.getContinguts()) {
				if (emc.getEstat() == ExecucioMassivaEstat.ESTAT_ERROR)
					errors ++;
				if (emc.getDataFi() == null)
					pendents++;
				dto.getContingutIds().add(emc.getId());
			}
			dto.setErrors(errors);
			Long total = new Long(dto.getContingutIds().size());
			dto.setExecutades(getPercent((total - pendents), total));
			dtos.add(dto);
		}
		return dtos;
	}

	private double getPercent(Long value, Long total) {
		if (total == 0)
			return 100L;
		else if (value == 0L)
			return 0L;
	    return Math.round(value * 100 / total);
	}





	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
