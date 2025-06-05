package es.caib.ripea.service.historic;

import java.io.IOException;
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

import es.caib.ripea.service.intf.dto.historic.HistoricInteressatDto;
import es.caib.ripea.service.intf.dto.historic.HistoricMetriquesEnumDto;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;

public class ExportacioExcelInteressatsHistoric extends ExportacioExcelHistoric {

	private HistoricMetriquesEnumDto[] metriques = new HistoricMetriquesEnumDto[] {
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS_ACUM,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS_ACUM };

	public ExportacioExcelInteressatsHistoric() {
		super();
	}

	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws IOException {

		for (String codiUsuari : dades.keySet()) {
			HSSFSheet sheet = wb.createSheet(codiUsuari);
			tableHeader(sheet, tipusAgrupament);
			int rowNum = 1;
			for (HistoricInteressatDto historic : dades.get(codiUsuari)) {
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
							"Export Excel: No s'ha pogut crear la línia: " + rowNum + " - del dia: " +
									historic.getData(),
							e);
				}
			}
			for (int i = 0; i < metriques.length + 1; i++)
				sheet.autoSizeColumn(i);
		}
		
		byte[] bytes = wb.getBytes();

		wb.close();

		return bytes;
	}
	
	private void tableHeader(HSSFSheet sheet, HistoricTipusEnumDto tipusAgrupament) {
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

		for (HistoricMetriquesEnumDto metrica : metriques) {
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(metrica.toString()));
			cell.setCellStyle(headerStyle);
		}
	}

}
