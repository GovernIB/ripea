package es.caib.ripea.service.historic;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportacioExcelHistoric {

	protected HSSFWorkbook wb;
	private HSSFFont bold;
	private HSSFFont greyFont;
	private DataFormat format;
	protected HSSFCellStyle cellDataStyle;
	protected HSSFCellStyle defaultStyle;

	public ExportacioExcelHistoric() {

		wb = new HSSFWorkbook();

		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);

		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
		greyFont.setCharSet(HSSFFont.ANSI_CHARSET);
		format = wb.createDataFormat();

		cellDataStyle = wb.createCellStyle();
		cellDataStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));
		cellDataStyle.setWrapText(true);

		defaultStyle = wb.createCellStyle();
		defaultStyle.setDataFormat(format.getFormat("0"));
	}


	protected static final Logger logger = LoggerFactory.getLogger(ExportacioExcelHistoric.class);

}
