/**
 *
 */
package es.caib.ripea.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.FluxFirmaUsuariDto;
import es.caib.ripea.core.api.dto.FluxFirmaUsuariFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxReviserDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxSignerDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.FluxFirmaUsuariService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.FluxFirmaUsuariEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació del servei de gestió de fluxos de firma d'un usuari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class FluxFirmaUsuariServiceImpl implements FluxFirmaUsuariService {

	@Autowired
	private FluxFirmaUsuariRepository fluxFirmaUsuariRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private MessageHelper messageHelper;
	
	@Transactional
	@Override
	public FluxFirmaUsuariDto create(
			Long entitatId,
			FluxFirmaUsuariDto flux,
			PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		logger.debug("Creant un nou flux (" +
				"flux=" + flux + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false, 
				false, 
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		
		FluxFirmaUsuariEntity entity = FluxFirmaUsuariEntity.getBuilder(
				flux.getNom(), 
				flux.getDescripcio(), 
				flux.getPortafirmesFluxId(),
				entitat,
				usuari).build();
		
		if (fluxDetall != null && fluxDetall.getDestinataris() != null) {
			String destinataris = obtenirDestinataris(fluxDetall.getDestinataris());
			entity.update(destinataris);
		}
		
		return conversioTipusHelper.convertir(
				fluxFirmaUsuariRepository.save(entity), 
				FluxFirmaUsuariDto.class);
	}

	@Transactional
	@Override
	public FluxFirmaUsuariDto update(
			Long id,
			Long entitatId, 
			PortafirmesFluxInfoDto fluxDetall) throws NotFoundException {
		logger.debug("Actualitzant flux existent (" +
				"flux=" + id + ")");

		FluxFirmaUsuariEntity entity = fluxFirmaUsuariRepository.findOne(id);
	
		if (fluxDetall != null && fluxDetall.getDestinataris() != null) {
			String destinataris = obtenirDestinataris(fluxDetall.getDestinataris());
			entity.update(destinataris);
		}
		
		return conversioTipusHelper.convertir(
				entity,
				FluxFirmaUsuariDto.class);
	}

	@Transactional
	@Override
	public FluxFirmaUsuariDto delete(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Esborrant flux (" +
				"id=" + id +  ")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		FluxFirmaUsuariEntity entity = fluxFirmaUsuariRepository.findOne(id);
		fluxFirmaUsuariRepository.delete(entity);

		pluginHelper.portafirmesEsborrarPlantillaFirma(idioma, entity.getPortafirmesFluxId());
		
		return conversioTipusHelper.convertir(
				entity,
				FluxFirmaUsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public FluxFirmaUsuariDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consulta de la url (" +
				"id=" + id + ")");
		
		FluxFirmaUsuariEntity entity = fluxFirmaUsuariRepository.findOne(id);
		FluxFirmaUsuariDto dto = conversioTipusHelper.convertir(
				entity,
				FluxFirmaUsuariDto.class);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<FluxFirmaUsuariDto> findByEntitatAndUsuariPaginat(
			Long entitatId, 
			FluxFirmaUsuariFiltreDto filtre,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		logger.debug("Consulta de totes les avisos paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<FluxFirmaUsuariDto> resposta;
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false, 
				false, 
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		
		resposta = paginacioHelper.toPaginaDto(
				fluxFirmaUsuariRepository.findByEntitatAndUsuari(
						entitat, 
						usuari,
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
						filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
						filtre.getDescripcio() != null ? filtre.getDescripcio().trim() : "",
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				FluxFirmaUsuariDto.class);
		return resposta;
	}

	@Transactional(readOnly = true )
	@Override
	public List<FluxFirmaUsuariDto> findByEntitatAndUsuari(Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false,
				false,
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		
		return conversioTipusHelper.convertirList(
				fluxFirmaUsuariRepository.findByEntitatAndUsuari(entitat, usuari), 
				FluxFirmaUsuariDto	.class);
	}
	
	private String obtenirDestinataris(List<PortafirmesFluxSignerDto> destinataris) {
		String destinatarisStr = "";

		for (PortafirmesFluxSignerDto destinatari : destinataris) {
			if (destinatari.getNom() != null) {
				destinatarisStr += "- " + destinatari.getNom();
				destinatarisStr += destinatari.getLlinatges() != null ? " " + destinatari.getLlinatges() : "";
				destinatarisStr += destinatari.getNif() != null ? " - " + destinatari.getNif() : "";
				destinatarisStr += destinatari.isObligat() ? " - <span class='firma-obligat'>" + messageHelper.getMessage("flux.firma.usuari.firma.obligat") + "</span>" : "";
				
				destinatarisStr += !destinatari.getRevisors().isEmpty() ? " [" : "";

				int index = 0;
				for (PortafirmesFluxReviserDto revisor : destinatari.getRevisors()) {
					destinatarisStr += revisor.getNom();
					destinatarisStr += revisor.getLlinatges() != null ? " " + revisor.getLlinatges() : "";
					destinatarisStr += revisor.getNif() != null ? " - " + revisor.getNif() : "";

					destinatarisStr += revisor.isObligat() ? " - <span class='firma-obligat'>" + messageHelper.getMessage("flux.firma.usuari.firma.obligat") + "</span>" : "";
					
					if (index < destinatari.getRevisors().size() - 1) {
						destinatarisStr += ", ";
					}

					index++;
				}

				destinatarisStr += !destinatari.getRevisors().isEmpty() ? "]" : "";

				destinatarisStr += "<br>";
			}
		}
		return destinatarisStr;
	}

	private static final Logger logger = LoggerFactory.getLogger(FluxFirmaUsuariServiceImpl.class);
	
}