/**
 * 
 */
package es.caib.ripea.plugin.caib.conversio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.conversio.ConversioArxiu;
import es.caib.ripea.plugin.conversio.ConversioPlugin;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Implementació del plugin de conversió de documents
 * emprant un servidor OpenOffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioPluginOpenOffice implements ConversioPlugin {

	private static final int BARCODE_POSITION_TOP = 0;
	private static final int BARCODE_POSITION_BOTTOM = 1;
	private static final int BARCODE_POSITION_LEFT = 2;
	private static final int BARCODE_POSITION_RIGHT = 3;

	private DocumentConverter documentConverter;
	private DocumentFormatRegistry documentFormatRegistry = new DefaultDocumentFormatRegistry();



	@Override
	public ConversioArxiu convertirPdf(
			ConversioArxiu arxiu) throws SistemaExternException {
		try {
			return convertirIEstampar(arxiu, null);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut convertir l'arxiu a format PDF (" +
					"arxiuNom=" + arxiu.getArxiuNom() + ", " +
					"arxiuTamany=" + arxiu.getArxiuContingut().length + ")",
					ex);
		}
	}

	@Override
	public ConversioArxiu convertirPdfIEstamparUrl(
			ConversioArxiu arxiu,
			String url) throws SistemaExternException {
		try {
			return convertirIEstampar(arxiu, url);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut convertir l'arxiu a format PDF (" +
					"arxiuNom=" + arxiu.getArxiuNom() + ", " +
					"arxiuTamany=" + arxiu.getArxiuContingut().length + ")",
					ex);
		}
	}

	@Override
	public String getNomArxiuConvertitPdf(String nomOriginal) {
		return nomOriginal.substring(0, nomOriginal.lastIndexOf(".")) + ".pdf";
	}



	private boolean isExtensioPdf(
			ConversioArxiu arxiu) {
		return "pdf".equalsIgnoreCase(arxiu.getArxiuExtensio());
	}

	private ConversioArxiu convertirIEstampar(
			ConversioArxiu arxiu,
			String url) throws IOException, DocumentException {
		ConversioArxiu convertit = new ConversioArxiu();
		ByteArrayOutputStream baosConversio = null;
		if (!isExtensioPdf(arxiu)) {
			ByteArrayInputStream bais = new ByteArrayInputStream(
					arxiu.getArxiuContingut());
			baosConversio = new ByteArrayOutputStream();
			if (arxiu.getArxiuContingut() != null) {
				DocumentFormat inputFormat = formatPerNomArxiu(
						arxiu.getArxiuNom());
				DocumentFormat outputFormat = documentFormatRegistry.getFormatByFileExtension("pdf");
				if (outputFormat == null)
					throw new IllegalArgumentException("format de sortida no suportat");
				getDocumentConverter().convert(
						bais,
						inputFormat,
						baosConversio,
						outputFormat);
			}
		}
		if (url != null) {
			PdfReader pdfReader;
			if (baosConversio != null)
				pdfReader = new PdfReader(baosConversio.toByteArray());
			else
				pdfReader = new PdfReader(arxiu.getArxiuContingut());
			ByteArrayOutputStream baosEstampacio = new ByteArrayOutputStream();
			PdfStamper pdfStamper = new PdfStamper(pdfReader, baosEstampacio);
			for (int i = 0; i < pdfReader.getNumberOfPages(); i++) {
				PdfContentByte over = pdfStamper.getOverContent(i + 1);
				estamparBarcodePdf417(
						over,
						url,
						BARCODE_POSITION_LEFT,
						10);
			}
			pdfStamper.close();
			convertit.setArxiuContingut(baosEstampacio.toByteArray());
		} else {
			if (baosConversio != null)
				convertit.setArxiuContingut(baosConversio.toByteArray());
			else
				convertit.setArxiuContingut(arxiu.getArxiuContingut());
		}
		convertit.setArxiuNom(
				getNomArxiuConvertitPdf(arxiu.getArxiuNom()));
		return convertit;
	}

	private void estamparBarcodePdf417(
			PdfContentByte contentByte,
			String url,
			int posicio,
			float margin) throws DocumentException {
		float paddingUrl = 5;
		// Calcula les dimensions de la pàgina i la taula
		Rectangle page = contentByte.getPdfDocument().getPageSize();
		float pageWidth = page.getWidth();
		float pageHeight = page.getHeight();
		if (posicio == BARCODE_POSITION_TOP || posicio == BARCODE_POSITION_BOTTOM) {
			float ampladaTaulaMax = pageWidth - (2 * margin);
			// Crea la cel·la del codi de barres
			BarcodePDF417 pdf417 = new BarcodePDF417();
			pdf417.setText(url);
			Image img = pdf417.getImage();
			PdfPCell pdf417Cell = new PdfPCell(img);
			pdf417Cell.setBorder(0);
			pdf417Cell.setFixedHeight(img.getHeight());
			float imgCellWidth = img.getWidth();
			// Crea la cel·la amb la url
			Font urlFont = new Font(Font.HELVETICA, 6);
			Chunk urlChunk = new Chunk(url, urlFont);
			Phrase urlPhrase = new Phrase(urlChunk);
			PdfPCell urlCell = new PdfPCell(urlPhrase);
			urlCell.setPadding(0);
			urlCell.setBorder(0);
			urlCell.setFixedHeight(img.getHeight());
			urlCell.setUseAscender(true);
			urlCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			urlCell.setPaddingLeft(paddingUrl);
			float urlWidth = urlChunk.getWidthPoint() + 5;
			float urlCellWidth = (imgCellWidth + urlWidth > ampladaTaulaMax) ? ampladaTaulaMax - imgCellWidth : urlWidth;
			// Estampa el codi de barres en la posició elegida
			PdfPTable table = new PdfPTable(2);
			table.addCell(pdf417Cell);
			table.addCell(urlCell);
			float ampladaTaula = imgCellWidth + urlCellWidth;
			table.setWidths(new float[]{img.getWidth(), ampladaTaula - img.getWidth()});
			table.setTotalWidth(ampladaTaula);
			if (posicio == BARCODE_POSITION_TOP) {
				table.writeSelectedRows(0, -1, (pageWidth / 2) - (ampladaTaula / 2), pageHeight - margin, contentByte);
			} else {
				table.writeSelectedRows(0, -1, (pageWidth / 2) - (ampladaTaula / 2), margin + img.getHeight(), contentByte);
			}
		} else if (posicio == BARCODE_POSITION_LEFT || posicio == BARCODE_POSITION_RIGHT) {
			float ampladaTaulaMax = pageHeight - (2 * margin);
			// Crea la cel·la del codi de barres
			BarcodePDF417 pdf417 = new BarcodePDF417();
			pdf417.setText(url);
			Image img = pdf417.getImage();
			PdfPCell pdf417Cell = new PdfPCell(img);
			pdf417Cell.setBorder(1);
			pdf417Cell.setFixedHeight(img.getWidth());
			pdf417Cell.setRotation(90);
			float imgCellWidth = img.getWidth();
			// Crea la cel·la amb la url
			Font urlFont = new Font(Font.HELVETICA, 6);
			Chunk urlChunk = new Chunk(url, urlFont);
			Phrase urlPhrase = new Phrase(urlChunk);
			PdfPCell urlCell = new PdfPCell(urlPhrase);
			urlCell.setPadding(0);
			urlCell.setBorder(0);
			urlCell.setUseAscender(true);
			urlCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			urlCell.setPaddingBottom(paddingUrl);
			urlCell.setRotation(90);
			float urlWidth = urlChunk.getWidthPoint() + 5;
			float urlCellWidth = (imgCellWidth + urlWidth > ampladaTaulaMax) ? ampladaTaulaMax - imgCellWidth : urlWidth;
			urlCell.setFixedHeight(urlCellWidth);
			// Estampa el codi de barres en la posició elegida
			PdfPTable table = new PdfPTable(1);
			table.addCell(urlCell);
			table.addCell(pdf417Cell);
			table.setWidths(new float[]{img.getHeight()});
			table.setTotalWidth(img.getHeight());
			float ampladaTaula = imgCellWidth + urlCellWidth;
			if (posicio == BARCODE_POSITION_LEFT) {
				table.writeSelectedRows(0, -1, margin, pageHeight - (pageHeight / 2) + (ampladaTaula / 2), contentByte);
			} else {
				table.writeSelectedRows(0, -1, pageWidth - img.getHeight() - margin , pageHeight - (pageHeight / 2) + (ampladaTaula / 2), contentByte);
			}
		}
	}

	private DocumentFormat formatPerNomArxiu(String arxiuNom) {
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = arxiuNom.substring(indexPunt + 1);
			DocumentFormat format = documentFormatRegistry.getFormatByFileExtension(extensio);
			if (format == null)
				throw new IllegalArgumentException("Extensio no suportada per aquest plugin (extensio=" + extensio + ")");
			return format;
		} else {
			throw new IllegalArgumentException("No es pot convertir un document sense extensió (arxiuNom=" + arxiuNom + ")");
		}
	}

	private DocumentConverter getDocumentConverter() {
		if (documentConverter == null) {
			documentConverter = new StreamOpenOfficeDocumentConverter(
					new SocketOpenOfficeConnection(
							getOpenOfficeHost(),
							getOpenOfficePort()),
					documentFormatRegistry);
		}
		return documentConverter;
	}

	private String getOpenOfficeHost() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.conversio.ooffice.host");
	}
	private int getOpenOfficePort() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.ripea.plugin.conversio.ooffice.port");
	}

}
