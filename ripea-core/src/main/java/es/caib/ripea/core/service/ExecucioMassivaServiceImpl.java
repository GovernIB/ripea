/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.ExecucioMassivaException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity.ExecucioMassivaEstat;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity.ExecucioMassivaTipus;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.HibernateHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.PropertiesHelper;
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
	private EmailHelper mailHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private MessageHelper messageHelper;
	
	@Autowired
	private DocumentService documentService;
	@Autowired
	private PluginHelper pluginHelper;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();

	@Transactional
	@Override
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
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
					dto.getDataCaducitat(), 
					dto.getEnviarCorreu(),
					entitat.getId()).build();
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
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		Date ara = new Date();
		Long execucioMassivaContingutId = null;
		Boolean nextFound = false;
		if (ultimaExecucioMassiva != null) {
			Long nextMassiu = execucioMassivaRepository.getNextMassiu(ultimaExecucioMassiva, ara);
			if (nextMassiu != null) {
				nextFound = true;
				execucioMassivaContingutId = execucioMassivaContingutRepository.findNextExecucioMassivaContingut(nextMassiu);
			}
		}
		
		if (execucioMassivaContingutId == null && !nextFound)
			execucioMassivaContingutId = execucioMassivaContingutRepository.findExecucioMassivaContingutId(ara);
		
		if (execucioMassivaContingutId == null) {
			// Comprobamos si es una ejecución masiva sin expedientes asociados. En ese caso actualizamos la fecha de fin
			Long mas = execucioMassivaRepository.getMinExecucioMassiva(ara);
			if (mas != null) {
				ExecucioMassivaEntity massiva = execucioMassivaRepository.findOne(mas);
				if (massiva != null) {
					massiva.updateDataFi(new Date());
					execucioMassivaRepository.saveAndFlush(massiva);
				}
			}
		}
		return execucioMassivaContingutId;
	}
	
	@Override
	public void executarExecucioMassiva(Long cmasiu_id) {
		ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(cmasiu_id);
		if (emc == null)
			throw new NotFoundException(cmasiu_id, ExecucioMassivaContingutEntity.class);
		ExecucioMassivaEntity exm = emc.getExecucioMassiva();
		ExecucioMassivaTipus tipus = exm.getTipus();
		try {
			Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
			final String user = exm.getCreatedBy().getCodi();
	        Principal principal = new Principal() {
				public String getName() {
					return user;
				}
			};
			List<String> rolsUsuariActual = pluginHelper.rolsUsuariFindAmbCodi(user);
			if (rolsUsuariActual.isEmpty())
				rolsUsuariActual.add("tothom");
	
			List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			for (String rol : rolsUsuariActual) {
				SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(rol);
				authorities.add(simpleGrantedAuthority);
			}
			Authentication authentication =  new UsernamePasswordAuthenticationToken(
					principal, 
					"N/A",
					authorities);
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			if (tipus == ExecucioMassivaTipus.PORTASIGNATURES){
				enviarPortafirmes(emc);
			}
			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
		} catch (Throwable ex) {
			Throwable excepcioRetorn = ExceptionUtils.getRootCause(ex) != null ? ExceptionUtils.getRootCause(ex) : ex;
			saveError(cmasiu_id, excepcioRetorn, exm.getTipus());
			throw new ExecucioMassivaException(
					emc.getContingut().getId(),
					emc.getContingut().getNom(),
					emc.getContingut().getTipus(),
					exm.getId(),
					emc.getId(),
					"Error al executar la acció massiva", 
					ex);
		}
	}
	
	@Override
	public void actualitzaUltimaOperacio(Long emc_id) {
		ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(emc_id);
		if (emc == null)
			throw new NotFoundException(emc_id, ExecucioMassivaContingutEntity.class);
		
		if (emc.getExecucioMassiva().getContinguts().size() == emc.getOrdre() + 1) {
			try {
				ExecucioMassivaEntity em = emc.getExecucioMassiva();
				em.updateDataFi(new Date());
				execucioMassivaRepository.save(em);
			} catch (Exception ex) {
				throw new ExecucioMassivaException(
						emc.getContingut().getId(),
						emc.getContingut().getNom(),
						emc.getContingut().getTipus(),
						emc.getExecucioMassiva().getId(),
						emc.getId(),
						"CONTINGUT MASSIU: No s'ha pogut processar l'execució massiva d'aquest contingut", 
						ex);
			}
			try {
				if (emc.getExecucioMassiva().getEnviarCorreu()) {
					mailHelper.execucioMassivaFinalitzada(emc.getExecucioMassiva());
				}
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", ex);
				
				throw new ExecucioMassivaException(
						emc.getContingut().getId(),
						emc.getContingut().getNom(),
						emc.getContingut().getTipus(),
						emc.getExecucioMassiva().getId(),
						emc.getId(),
						"CONTINGUT MASSIU: No s'ha pogut enviar el correu de finalització", 
						ex);
			}
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generaInformeError(Long emc_id, String error) {
		ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(emc_id);
		if (emc == null)
			throw new NotFoundException(emc_id, ExecucioMassivaContingutEntity.class);
		
		
		Date ara = new Date();
		
		emc.updateError(
				ara, 
				error.length() < 2045 ? error : error.substring(0, 2045));
		
		execucioMassivaContingutRepository.save(HibernateHelper.deproxy(emc));
	}
	
	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true, false);
		
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

	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.ripea.segonpla.massives.periode.comprovacio}")
	public void comprovarExecucionsMassives() {
		boolean active = true;
		Long ultimaExecucioMassiva = null;
		int timeBetweenExecutions = 500;
		try {
			timeBetweenExecutions = Integer.parseInt(
					PropertiesHelper.getProperties().getProperty("es.caib.ripea.plugin.arxiu.class"));
		} catch (Exception ex) {}
		while (active) {
			Long cmasiu_id = null;
			boolean alertat = false;
			try {
				cmasiu_id = getExecucionsMassivesActiva(ultimaExecucioMassiva);
				if (cmasiu_id != null) {
					ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(cmasiu_id);
					try {
						executarExecucioMassiva(cmasiu_id);
						if (emc != null)
							alertaHelper.crearAlerta(
									messageHelper.getMessage(
											"alertes.segon.pla.execucio.massiva",
											new Object[] {cmasiu_id}),
									null,
									emc.getContingut().getId());
					} catch (Exception e) {
						// recuperem l'error de la aplicació
						String errMsg = getError(cmasiu_id);
						if (errMsg == null || "".equals(errMsg))
							errMsg = e.getMessage();
						generaInformeError(cmasiu_id, errMsg);
						if (emc != null)
							alertaHelper.crearAlerta(
									messageHelper.getMessage(
											"alertes.segon.pla.executar.execucio.massiva.error",
											new Object[] {cmasiu_id}),
									e,
									emc.getContingut().getId());
						alertat = true;
					}
					if (emc == null)
						throw new NotFoundException(cmasiu_id, ExecucioMassivaContingutEntity.class);
					ultimaExecucioMassiva = emc.getExecucioMassiva().getId();
					actualitzaUltimaOperacio(emc.getId());
				} else {
					active = false;
				}
				Thread.sleep(timeBetweenExecutions);
			} catch (Exception e) {
				logger.error("La execució de execucions massives ha estat interromput");
				active = false;
				if(!alertat && cmasiu_id != null) {
					ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(cmasiu_id);
					alertaHelper.crearAlerta(
							messageHelper.getMessage(
									"alertes.segon.pla.execucio.massiva.error",
									new Object[] {cmasiu_id}),
							e,
							emc.getContingut().getId());
					alertat = true;
				}
			}
		}
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

	private void enviarPortafirmes(ExecucioMassivaContingutEntity emc) throws Exception {
		ContingutEntity contingut = emc.getContingut();
		
		try {
			emc.updateDataInici(new Date());
			
			ExecucioMassivaEntity em = emc.getExecucioMassiva();
			documentService.portafirmesEnviar(
					contingut.getEntitat().getId(),
					contingut.getId(),
					em.getMotiu(),
					em.getPrioritat(),
					null,
					((DocumentEntity) contingut).getMetaDocument().getPortafirmesResponsables(),
					((DocumentEntity) contingut).getMetaDocument().getPortafirmesSequenciaTipus(),
					((DocumentEntity) contingut).getMetaDocument().getPortafirmesFluxTipus(),
					null,
					null);
				
			emc.updateFinalitzat(new Date());
			execucioMassivaContingutRepository.save(emc);
		} catch (Exception ex) {
			logger.error("CONTINGUT MASSIU:" + emc.getId() + ". No s'ha pogut enviar el document al portasignatures", ex);
			throw ex;
		}
	}

	private static void saveError(Long execucioMassivaContingutId, Throwable error, ExecucioMassivaTipus tipus) {
		StringWriter out = new StringWriter();
		error.printStackTrace(new PrintWriter(out));
		errorsMassiva.put(execucioMassivaContingutId, out.toString());
	}
	private static String getError(Long operacioMassivaId) {
		String error = errorsMassiva.get(operacioMassivaId);
		errorsMassiva.remove(operacioMassivaId);
		return error;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
