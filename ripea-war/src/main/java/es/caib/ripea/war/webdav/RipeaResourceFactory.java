/**
 * 
 */
package es.caib.ripea.war.webdav;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.EscriptoriDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.EntitatService;
import io.milton.common.Path;
import io.milton.http.ResourceFactory;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.Resource;

/**
 * ResourceFactory per a crear els recursos per al servidor
 * WEBDAV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RipeaResourceFactory implements ResourceFactory {

	private ContingutService contenidorService;
	private DocumentService documentService;
	private CarpetaService carpetaService;
	private EntitatService entitatService;
	private AplicacioService aplicacioService;

	public RipeaResourceFactory(
			ContingutService contenidorService,
			DocumentService documentService,
			CarpetaService carpetaService,
			EntitatService entitatService,
			AplicacioService aplicacioService) {
		this.contenidorService = contenidorService;
		this.documentService = documentService;
		this.carpetaService = carpetaService;
		this.entitatService = entitatService;
		this.aplicacioService = aplicacioService;
	}

	@Override
	public Resource getResource(
			String host,
			String path) throws NotAuthorizedException, BadRequestException {
		Path p = Path.path(path);
		p = p.getStripFirst().getStripFirst();
		logger.debug("[F] getResource (host=" + host + ", path=" + path + ")");
		if (p.isRoot()) {
			return contenidorToResource(
					null,
					getContenidorArrel(),
					path,
					true);
		} else {
			String entitatNom = p.getFirst();
			Path pathActual = p.getStripFirst();
			EntitatDto entitatActual = null;
			List<EntitatDto> entitats = entitatService.findAccessiblesUsuariActual();
			for (EntitatDto entitat: entitats) {
				if (entitatNom.equals(entitat.getNom())) {
					entitatActual = entitat;
					break;
				}
			}
			if (entitatActual != null) {
				try {
					ContingutDto contingut = contenidorService.getContingutAmbFillsPerPath(
							entitatActual.getId(),
							pathActual.toString());
					return contenidorToResource(
							entitatActual,
							contingut,
							path,
							false);
				} catch (NotFoundException ex) {
					logger.debug("[F] getResource error (host=" + host + ", path=" + path + "): No s'ha trobat el contenidor");
				}
			}
			return null;
		}
	}



	private Resource contenidorToResource(
			EntitatDto entitat,
			ContingutDto contenidor,
			String path,
			boolean esEntitat) {
		if (contenidor instanceof EscriptoriDto) {
			logger.debug("[F] getResource resultat: Folder (escriptori)");
			EscriptoriDto escriptori = (EscriptoriDto)contenidor;
			RipeaFolderResource resource = new RipeaFolderResource(
					entitat,
					toContingutDto(
							entitat,
							escriptori),
					new ServiceHolder(
							contenidorService,
							documentService,
							carpetaService));
			resource.setContenidorEntitat(esEntitat);
			resource.setContenidorEscriptori(true);
			return resource;
		} else if (contenidor instanceof ExpedientDto) {
			logger.debug("[F] getResource resultat: Folder (expedient)");
			ExpedientDto expedient = (ExpedientDto)contenidor;
			RipeaFolderResource resource = new RipeaFolderResource(
					entitat,
					expedient,
					new ServiceHolder(
							contenidorService,
							documentService,
							carpetaService));
			resource.setContenidorEntitat(esEntitat);
			resource.setContenidorEscriptori(false);
			return resource;
		} else if (contenidor instanceof CarpetaDto) {
			logger.debug("[F] getResource resultat: Folder (carpeta)");
			CarpetaDto carpeta = (CarpetaDto)contenidor;
			RipeaFolderResource resource = new RipeaFolderResource(
					entitat,
					carpeta,
					new ServiceHolder(
							contenidorService,
							documentService,
							carpetaService));
			resource.setContenidorEntitat(esEntitat);
			resource.setContenidorEscriptori(false);
			return resource;
		} else if (contenidor instanceof DocumentDto) {
			DocumentDto document = (DocumentDto)contenidor;
			String contenidorPath = contenidor.getPathAsStringWebdavAmbNom();
			if (path.endsWith("/"))
				contenidorPath += "/";
			if (path.endsWith(contenidorPath)) {
				logger.debug("[F] getResource resultat: Folder (document)");
				RipeaFolderResource resource = new RipeaFolderResource(
						entitat,
						document,
						new ServiceHolder(
								contenidorService,
								documentService,
								carpetaService));
				resource.setContenidorEntitat(esEntitat);
				resource.setContenidorEscriptori(false);
				return resource;
			} else {
				logger.debug("[F] getResource resultat: File (document)");
				return new RipeaFileResource(
						entitat,
						document,
						new ServiceHolder(
								contenidorService,
								documentService,
								carpetaService));
			}
		} else {
			return null;
		}
	}

	private static ContingutDto toContingutDto(
			EntitatDto entitat,
			EscriptoriDto escriptori) {
		ContingutDto contingutDto = new CarpetaDto();
		contingutDto.setId(escriptori.getId());
		contingutDto.setFills(escriptori.getFills());
		contingutDto.setPath(escriptori.getPath());
		contingutDto.setCreatedBy(escriptori.getCreatedBy());
		contingutDto.setCreatedDate(escriptori.getCreatedDate());
		contingutDto.setLastModifiedBy(escriptori.getLastModifiedBy());
		contingutDto.setLastModifiedDate(escriptori.getLastModifiedDate());
		contingutDto.setNom(entitat.getNom());
		return contingutDto;
	}

	private static ContingutDto toContenidorDto(EntitatDto entitat) {
		ContingutDto contingutDto = new CarpetaDto();
		contingutDto.setId(entitat.getId());
		contingutDto.setCreatedBy(entitat.getCreatedBy());
		contingutDto.setCreatedDate(entitat.getCreatedDate());
		contingutDto.setLastModifiedBy(entitat.getLastModifiedBy());
		contingutDto.setLastModifiedDate(entitat.getLastModifiedDate());
		contingutDto.setNom(entitat.getNom());
		return contingutDto;
	}

	private ContingutDto getContenidorArrel() {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		ContingutDto contenidorDto = new CarpetaDto();
		contenidorDto.setId(new Long(-1));
		contenidorDto.setNom(usuari.getNom());
		List<EntitatDto> entitats = entitatService.findAccessiblesUsuariActual();
		List<ContingutDto> fills = new ArrayList<ContingutDto>();
		for (EntitatDto entitat: entitats) {
			fills.add(toContenidorDto(entitat));
		}
		contenidorDto.setFills(fills);
		contenidorDto.setPath(new ArrayList<ContingutDto>());
		Date ara = new Date();
		contenidorDto.setCreatedBy(usuari);
		contenidorDto.setCreatedDate(ara);
		contenidorDto.setLastModifiedBy(usuari);
		contenidorDto.setLastModifiedDate(ara);
		return contenidorDto;
	}

	private static final Logger logger = LoggerFactory.getLogger(RipeaResourceFactory.class);

}
