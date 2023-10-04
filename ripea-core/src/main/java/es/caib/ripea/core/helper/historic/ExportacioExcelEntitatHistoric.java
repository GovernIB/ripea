package es.caib.ripea.core.helper.historic;

import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricMetriquesEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.IOException;
import java.util.List;

public class ExportacioExcelEntitatHistoric extends ExportacioExcelHistoric {

	private HistoricMetriquesEnumDto[] metriques = new HistoricMetriquesEnumDto[] {
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS_ACUM,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS_ACUM };
	
	public ExportacioExcelEntitatHistoric() {
		super();
	}
	
	public byte[] convertDadesEntitat(List<HistoricExpedientDto> dades, HistoricTipusEnumDto tipusAgrupament) throws IOException {


		createHistoricSheet(dades, "Històric de l'entitat", tipusAgrupament);
		byte[] bytes = wb.getBytes();

		wb.close();

		return bytes;
	}
	
	private HSSFSheet createHistoricSheet(List<HistoricExpedientDto> dades, String sheetname, HistoricTipusEnumDto tipusAgrupament) {
		HSSFSheet sheet = wb.createSheet(sheetname);

		createMetriquesHeader(sheet, tipusAgrupament);

		int rowNum = 1;
		for (HistoricExpedientDto historic : dades) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum);

				int colNum = 0;
				HSSFCell cellData = xlsRow.createCell(colNum++);
				if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
					cellData.setCellValue(historic.getData());
					cellData.setCellStyle(cellDataStyle);
					
				} else {
					cellData.setCellValue(historic.getAny());
					cellData = xlsRow.createCell(colNum++);
					cellData.setCellValue(historic.getMesNom());
				}

			
				for (HistoricMetriquesEnumDto metricEnum : metriques) {
					HSSFCell cellUnitatNom = xlsRow.createCell(colNum++);
					cellUnitatNom.setCellValue(metricEnum.getValue(historic));
					cellUnitatNom.setCellStyle(defaultStyle);
				}

				rowNum++;

			} catch (Exception e) {
				logger.error(
						"Export Excel: No s'ha pogut crear la línia: " + rowNum + " - del dia: " + historic.getData(),
						e);
			}
		}

		for (int i = 0; i < HistoricMetriquesEnumDto.values().length + 1; i++)
			sheet.autoSizeColumn(i);

		return sheet;
	}
	
	@SuppressWarnings("deprecation")
	private void createMetriquesHeader(HSSFSheet sheet, HistoricTipusEnumDto tipusAgrupament) {
		HSSFFont bold;
		HSSFCellStyle headerStyle;

		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);

		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
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
		

		for (HistoricMetriquesEnumDto metricEnum : metriques) {
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(metricEnum.toString()));
			cell.setCellStyle(headerStyle);
		}
	}

}
