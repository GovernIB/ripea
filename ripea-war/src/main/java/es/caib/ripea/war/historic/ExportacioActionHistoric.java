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
	@Autowired
	private ExportacioCsvHistoric exportacioCsvHistoric;

	
	public FitxerDto exportarHistoricEntitat(EntitatDto entitat, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		List<HistoricExpedientDto> dades = historicService.getDadesEntitat(entitat.getId(), rolActual, filtre);
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
		
		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesEntitat(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicEntitat.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelEntitatHistoric()).convertDadesEntitat(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicEntitat.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesEntitat(entitat, dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicEntitat.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesEntitat(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicEntitat.xml", "application/xml", fileContent);
			break;
		case "csv":
			fileContent = exportacioCsvHistoric.convertDadesEntitat(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicEntitat.csv", "application/csv", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	public FitxerDto exportarHistoricOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception {
		

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		if (format.equals("json")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(entitatId, rolActual, filtre);

			fileContent = (new ExportacioJSONHistoric()).convertDadesOrgansGestors(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.json", "application/json", fileContent);

		} else if (format.equals("xlsx")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(entitatId, rolActual, filtre);
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
			
			fileContent = (new ExportacioExcelOrganGestorHistoric()).convertDadesOrgansGestors(dades, organsGestors, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.xls", "application/vnd.ms-excel", fileContent);

		} else if (format.equals("odf")) {
			Map<OrganGestorDto, List<HistoricExpedientDto>> dades = historicService.getHistoricsByOrganGestor(entitatId, rolActual, filtre);
			fileContent = (new ExportacioDocHistoric()).convertDadesOrgansGestors(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.ods", "application/vnd.oasis.opendocument.text", fileContent);

		} else if (format.equals("xml")) {
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(entitatId, rolActual, filtre);
			fileContent = exportacioXMLHistoric.convertDadesOrgansGestors(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicOrgansGestors.xml", "application/xml", fileContent);

		} else {
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}

	public FitxerDto exportarHistoricUsuaris(
			String[] usuarisCodi,
			Long entitatId,
			String rolActual,
			HistoricFiltreDto filtre,
			String format) throws Exception {
		Map<String, List<HistoricUsuariDto>> dades = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			dades.put(codiUsuari, historicService.getDadesUsuari(codiUsuari, entitatId, rolActual, filtre));
		}

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesUsuaris(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicUsuaris.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelUsuariHistoric()).convertDadesUsuaris(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicUsuaris.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesUsuaris(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicUsuaris.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesUsuaris(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicUsuaris.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}
		return fitxer;
	}

	public FitxerDto exportarHistoricInteressats(
			String[] interessatsDocNum,
			Long entitatId,
			String rolActual,
			HistoricFiltreDto filtre,
			String format) throws Exception {
		Map<String, List<HistoricInteressatDto>> dades = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			dades.put(docNum, historicService.getDadesInteressat(docNum, entitatId, rolActual, filtre));
		}

		byte[] fileContent = null;
		FitxerDto fitxer = null;
		switch (format) {
		case "json":
			fileContent = (new ExportacioJSONHistoric()).convertDadesInteressats(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicInteressats.json", "application/json", fileContent);
			break;
		case "xlsx":
			fileContent = (new ExportacioExcelInteressatsHistoric()).convertDadesInteressats(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicInteressats.xls", "application/vnd.ms-excel", fileContent);
			break;
		case "odf":
			fileContent = (new ExportacioDocHistoric()).convertDadesInteressats(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicInteressats.ods", "application/vnd.oasis.opendocument.text", fileContent);
			break;
		case "xml":
			fileContent = exportacioXMLHistoric.convertDadesInteressats(dades, filtre.getTipusAgrupament());
			fitxer = new FitxerDto("historicInteressats.xml", "application/xml", fileContent);
			break;
		default:
			throw new Exception("Unsuported file format");
		}

		return fitxer;
	}
}
