package es.caib.ripea.war.historic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.core.api.service.OrganGestorService;

@Component
public class ExportacioActionHistoric {

	@Autowired
	private HistoricService historicService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private ExportacioXMLHistoric exportacioXMLHistoric;

	
	public FitxerDto exportarHistoricEntitat(EntitatDto entitat, HistoricFiltreDto filtre, String format) throws Exception {
		List<HistoricExpedientDto> dades = historicService.getDadesEntitat(entitat.getId(), filtre);
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
		
		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesEntitat(dades);
			fitxer = new FitxerDto("historicEntitat.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelEntitatHistoric()).convertDadesEntitat(dades);
			fitxer = new FitxerDto("historicEntitat.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesEntitat(entitat, dades);
			fitxer = new FitxerDto("historicEntitat.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesEntitat(dades);
			fitxer = new FitxerDto("historicEntitat.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	public FitxerDto exportarHistoricOrgansGestors(HistoricFiltreDto filtre, String format) throws Exception {
		

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		if (format.equals("json")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(filtre);

			fileContent = (new ExportacioJSONHistoric()).convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.json", "application/json", fileContent);

		} else if (format.equals("xlsx")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(filtre);
			List<OrganGestorDto> organsGestors = new ArrayList<>();
			for (Long organId : filtre.getOrganGestorsIds()) {
				OrganGestorDto organGestor = organGestorService.findItem(organId);
				organsGestors.add(organGestor);
			}
			if (filtre.getIncorporarExpedientsComuns()) {
				OrganGestorDto organDto = new OrganGestorDto();
				organDto.setCodi("");
				organDto.setNom("Expedients comuns");
				organsGestors.add(organDto);
			}
			
			fileContent = (new ExportacioExcelOrganGestorHistoric()).convertDadesOrgansGestors(dades, organsGestors);
			fitxer = new FitxerDto("historicOrgansGestors.xls", "application/vnd.ms-excel", fileContent);

		} else if (format.equals("odf")) {
			Map<OrganGestorDto, List<HistoricExpedientDto>> dades = historicService.getHistoricsByOrganGestor(filtre);
			fileContent = (new ExportacioDocHistoric()).convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.ods", "application/vnd.oasis.opendocument.text", fileContent);

		} else if (format.equals("xml")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(filtre);
			fileContent = exportacioXMLHistoric.convertDadesOrgansGestors(dades);
			fitxer = new FitxerDto("historicOrgansGestors.xml", "application/xml", fileContent);

		} else {
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	public FitxerDto exportarHistoricUsuaris(
			String[] usuarisCodi,
			HistoricFiltreDto filtre,
			String format) throws Exception {
		Map<String, List<HistoricUsuariDto>> dades = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			dades.put(codiUsuari, historicService.getDadesUsuari(codiUsuari, filtre));
		}

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesUsuaris(dades);
			fitxer = new FitxerDto("historicUsuaris.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelUsuariHistoric()).convertDadesUsuaris(dades);
			fitxer = new FitxerDto("historicUsuaris.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesUsuaris(dades);
			fitxer = new FitxerDto("historicUsuaris.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesUsuaris(dades);
			fitxer = new FitxerDto("historicUsuaris.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}
		return fitxer;
	}

	public FitxerDto exportarHistoricInteressats(
			String[] interessatsDocNum,
			HistoricFiltreDto filtre,
			String format) throws Exception {
		Map<String, List<HistoricInteressatDto>> dades = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			dades.put(docNum, historicService.getDadesInteressat(docNum, filtre));
		}

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesInteressats(dades);
			fitxer = new FitxerDto("historicInteressats.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelInteressatsHistoric()).convertDadesInteressats(dades);
			fitxer = new FitxerDto("historicInteressats.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesInteressats(dades);
			fitxer = new FitxerDto("historicInteressats.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesInteressats(dades);
			fitxer = new FitxerDto("historicInteressats.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}
}
