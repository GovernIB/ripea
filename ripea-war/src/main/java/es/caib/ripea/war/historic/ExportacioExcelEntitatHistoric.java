package es.caib.ripea.war.historic;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;

import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricMetriquesEnumDto;

public class ExportacioExcelEntitatHistoric extends ExportacioExcelHistoric {
	private HistoricMetriquesEnumDto[] metriques = new HistoricMetriquesEnumDto[] {
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS_ACUM,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS_ACUM };
	
	public ExportacioExcelEntitatHistoric() {
		super();
	}
	
	public byte[] convertDadesEntitat(List<HistoricExpedientDto> dades) throws IOException {


		createHistoricSheet(dades, "Històric de l'entitat");
		byte[] bytes = wb.getBytes();

		wb.close();

		return bytes;
	}
	
	private HSSFSheet createHistoricSheet(List<HistoricExpedientDto> dades, String sheetname) {
		HSSFSheet sheet = wb.createSheet(sheetname);

		createMetriquesHeader(sheet);

		int rowNum = 1;
		for (HistoricExpedientDto historic : dades) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum);

				HSSFCell cellData = xlsRow.createCell(0);
				cellData.setCellValue(historic.getData());
				cellData.setCellStyle(cellDataStyle);

				int colNum = 1;
				for (HistoricMetriquesEnumDto metricEnum : metriques) {
					HSSFCell cellUnitatNom = xlsRow.createCell(colNum);
					cellUnitatNom.setCellValue(metricEnum.getValue(historic));
					cellUnitatNom.setCellStyle(defaultStyle);
					colNum++;
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
	
	private void createMetriquesHeader(HSSFSheet sheet) {
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
		cell.setCellValue(new HSSFRichTextString("Data"));
		cell.setCellStyle(headerStyle);

		for (HistoricMetriquesEnumDto metricEnum : metriques) {
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(metricEnum.toString()));
			cell.setCellStyle(headerStyle);
		}
	}

}