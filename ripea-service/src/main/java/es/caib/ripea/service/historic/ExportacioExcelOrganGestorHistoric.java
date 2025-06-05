package es.caib.ripea.service.historic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.historic.HistoricExpedientDto;
import es.caib.ripea.service.intf.dto.historic.HistoricMetriquesEnumDto;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;

public class ExportacioExcelOrganGestorHistoric extends ExportacioExcelHistoric {

	private HistoricMetriquesEnumDto[] metriques = new HistoricMetriquesEnumDto[] {
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS_ACUM,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS_ACUM,
			HistoricMetriquesEnumDto.DOCUMENTS_SIGNATS,
			HistoricMetriquesEnumDto.DOCUMENTS_NOTIFICATS };

	public ExportacioExcelOrganGestorHistoric() {
		super();
	}


	public byte[] convertDadesOrgansGestors(
			Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades,
			List<OrganGestorDto> organsGestors, HistoricTipusEnumDto tipusAgrupament) throws IOException {

		for (HistoricMetriquesEnumDto metricEnum : metriques) {
			HSSFSheet sheet = wb.createSheet(metricEnum.toString());
			createOrgansGestorsHeader(organsGestors, sheet, tipusAgrupament);
			List<Date> dates = new ArrayList<Date>(dades.keySet());
			int rowNum = 1;
			for (Date data : dates) {
				try {
					HSSFRow xlsRow = sheet.createRow(rowNum);
					int colNum = 0;
					HSSFCell cellData = xlsRow.createCell(colNum++);
					if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
						cellData.setCellValue(data);
						cellData.setCellStyle(cellDataStyle);
					} else {
						cellData.setCellValue(ExportacioHelper.getAny(data));
						cellData = xlsRow.createCell(colNum++);
						cellData.setCellValue(ExportacioHelper.getMesNom(data));
					}

					for (OrganGestorDto organGestor : organsGestors) {
						HistoricExpedientDto historic = dades.get(data).get(organGestor);
						HSSFCell cellUnitatNom = xlsRow.createCell(colNum++);
						cellUnitatNom.setCellValue(metricEnum.getValue(historic));
						cellUnitatNom.setCellStyle(defaultStyle);
						
					}

					rowNum++;

				} catch (Exception e) {
					logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - del dia: " + data, e);
				}
			}

			for (int i = 0; i < organsGestors.size() + 1; i++)
				sheet.autoSizeColumn(i);

		}

		byte[] bytes = wb.getBytes();

		wb.close();

		return bytes;
	}

	private void createOrgansGestorsHeader(Collection<OrganGestorDto> organsGestors, HSSFSheet sheet, HistoricTipusEnumDto tipusAgrupament) {
		HSSFFont bold;
		HSSFCellStyle headerStyle;

		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(IndexedColors.WHITE.getIndex());

		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
		headerStyle.setFillBackgroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		headerStyle.setFont(bold);
		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);
		HSSFCell cell;

		cell = xlsRow.createCell(colNum++);
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			cell.setCellValue(new HSSFRichTextString("Data"));
			cell.setCellStyle(headerStyle);
		} else {
			cell.setCellValue(new HSSFRichTextString("Any"));
			cell.setCellStyle(headerStyle);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString("Mes"));
			cell.setCellStyle(headerStyle);
		}
		cell.setCellStyle(headerStyle);

		for (OrganGestorDto organGestor : organsGestors) {
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(organGestor.getNom()));
			cell.setCellStyle(headerStyle);
		}
	}


}
