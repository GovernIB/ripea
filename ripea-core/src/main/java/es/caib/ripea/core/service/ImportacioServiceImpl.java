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

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.PluginHelper;

/**
 * Implementació dels mètodes per importar documents desde l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ImportacioServiceImpl implements ImportacioService {

	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;

	@Transactional
	@Override
	public List<DocumentDto> getDocuments(
			Long entitatId,
			Long contingutId,
			String numeroRegistre) {
		logger.debug("Important documents de l'arxiu digital (" +
				"numeroRegistre=" + numeroRegistre + ")");
		ExpedientEntity expedientSuperior;
		FitxerDto fitxer = new FitxerDto();;
		List<DocumentDto> listDto = new ArrayList<DocumentDto>();
		
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				false);
		
		List<ContingutArxiu> documentsArxiu = pluginHelper.getCustodyIdDocuments(numeroRegistre);
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingut.getTipus())) {
			expedientSuperior = (ExpedientEntity)contingut;
		} else {
			expedientSuperior = contingut.getExpedient();
		}
		
		for (ContingutArxiu documentArxiu : documentsArxiu) {
			
			Document document = pluginHelper.importarDocument(
					expedientSuperior.getArxiuUuid(),
					documentArxiu.getIdentificador(),
					true);
			
			fitxer.setNom(document.getNom());
			fitxer.setContentType(document.getContingut().getTipusMime());
			fitxer.setContingut(document.getContingut().getContingut());
			
			DocumentEntity entity = documentHelper.crearNouDocument(
					DocumentTipusEnumDto.DIGITAL,
					document.getNom(),
					document.getMetadades().getDataCaptura(),
					document.getMetadades().getDataCaptura(),
					//Només hi ha un òrgan
					document.getMetadades().getOrgans().get(0),
					getOrigen(document),
					getEstatElaboracio(document),
					getTipusDocumental(document),
					null,
					contingut,
					contingut.getEntitat(),
					expedientSuperior,
					null,
					document.getIdentificador());
			
			if (fitxer != null) {
				entity.updateFitxer(
						fitxer.getNom(),
						fitxer.getContentType(),
						fitxer.getContingut());
			}
			entity.updateArxiu(document.getIdentificador());
			contingutLogHelper.logCreacio(
					entity,
					true,
					true);
			listDto.add(toDocumentDto(entity));
		}
		return listDto;
	}

	private DocumentDto toDocumentDto(
			DocumentEntity document) {
		return (DocumentDto)contingutHelper.toContingutDto(
				document,
				false,
				false,
				false,
				false,
				true,
				true,
				false);
	}
	
	private static NtiOrigenEnumDto getOrigen(Document document) {
		NtiOrigenEnumDto origen = null; 
		
		switch (document.getMetadades().getOrigen()) {
		case CIUTADA:
			origen = NtiOrigenEnumDto.O0;
			break;
		case ADMINISTRACIO:
			origen = NtiOrigenEnumDto.O1;
			break;
		}
		return origen;
	}
	
	private static DocumentNtiEstadoElaboracionEnumDto getEstatElaboracio(Document document) {
		DocumentNtiEstadoElaboracionEnumDto estatElaboracio = null;
		
		switch (document.getMetadades().getEstatElaboracio()) {
		case ORIGINAL:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE01;
			break;
		case COPIA_CF:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE02;
			break;
		case COPIA_DP:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE03;
			break;
		case COPIA_PR:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE04;
			break;
		case ALTRES:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE99;
			break;
		}
		return estatElaboracio;
	}

	private static DocumentNtiTipoDocumentalEnumDto getTipusDocumental(Document document) {
		DocumentNtiTipoDocumentalEnumDto tipusDocumental = null;
		
		switch (document.getMetadades().getTipusDocumental()) {
		case RESOLUCIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD01;
			break;
		case ACORD:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD02;
			break;
		case CONTRACTE:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD03;
			break;
		case CONVENI:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD04;
			break;
		case DECLARACIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD05;
			break;
		case COMUNICACIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD06;
			break;
		case NOTIFICACIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD07;
			break;
		case PUBLICACIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD08;
			break;
		case JUSTIFICANT_RECEPCIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD09;
			break;
		case ACTA:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD10;
			break;
		case CERTIFICAT:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD11;
			break;
		case DILIGENCIA:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD12;
			break;
		case INFORME:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD13;
			break;
		case SOLICITUD:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD14;
			break;
		case DENUNCIA:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD15;
			break;
		case ALEGACIO:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD16;
			break;
		case RECURS:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD17;
			break;
		case COMUNICACIO_CIUTADA:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD18;
			break;
		case FACTURA:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD19;
			break;
		case ALTRES_INCAUTATS:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD20;
			break;
		case ALTRES:
			tipusDocumental = DocumentNtiTipoDocumentalEnumDto.TD99;
			break;
		}
		return tipusDocumental;
	}
	private static final Logger logger = LoggerFactory.getLogger(ImportacioServiceImpl.class);

}
