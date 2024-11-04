/**
 * 
 */
package es.caib.ripea.plugin.caib.conversio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.jdom.Namespace;
import org.jopendocument.dom.ODPackage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

import com.lowagie.text.Chunk;
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

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.conversio.ConversioArxiu;
import es.caib.ripea.plugin.conversio.ConversioPlugin;
import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.document.DocumentKind;

/**
 * Implementació del plugin de conversió de documents
 * emprant XDocReport.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioPluginXdocreport extends RipeaAbstractPluginProperties implements ConversioPlugin {

	private static final int BARCODE_POSITION_TOP = 0;
	private static final int BARCODE_POSITION_BOTTOM = 1;
	private static final int BARCODE_POSITION_LEFT = 2;
	private static final int BARCODE_POSITION_RIGHT = 3;

	
	public ConversioPluginXdocreport() {
		super();
	}
	public ConversioPluginXdocreport(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
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
		if (nomOriginal == null || nomOriginal.lastIndexOf(".") == -1)
			return nomOriginal;
		return nomOriginal.substring(0, nomOriginal.lastIndexOf(".")) + ".pdf";
	}



	private boolean isExtensioPdf(
			ConversioArxiu arxiu) {
		return "pdf".equalsIgnoreCase(arxiu.getArxiuExtensio());
	}

	private DocumentKind getDocumentKind(
			ConversioArxiu arxiu) throws SistemaExternException {
		String extensio = arxiu.getArxiuExtensio();
		if ("odt".equalsIgnoreCase(extensio)) {
			return DocumentKind.ODT;
		} else if ("docx".equalsIgnoreCase(extensio)) {
			return DocumentKind.DOCX;
		} else {
			throw new SistemaExternException(
					"Tipus de document no suportat (arxiuNom=" + arxiu.getArxiuNom() + ")");
		}
	}


	private ConversioArxiu convertirIEstampar(
			ConversioArxiu arxiu,
			String url) throws Exception {
		ConversioArxiu convertit = new ConversioArxiu();
		ByteArrayOutputStream baosConversio = null;
		if (!isExtensioPdf(arxiu)) {
			
			DocumentKind documentKind = getDocumentKind(arxiu);
			
			Options options = Options.getFrom(
					documentKind).to(
					ConverterTypeTo.PDF);
			
			byte[] contingut;
			// xdocreport gives error when trying to convert docx/odt which has hyperlink in header/footer to pdf
			// that's why hyperlinks needs to be removed
			// error is: java.lang.RuntimeException: Not all annotations could be added to the document (the document doesn't have enough pages). at com.lowagie.text.pdf.PdfDocument.close(Unknown Source)
			if (documentKind == DocumentKind.DOCX) {
				contingut = removeLinksHeaderFooterDocx(arxiu.getArxiuContingut());
			} else {
				contingut = removeLinksHeaderFooterOdt(arxiu.getArxiuContingut());
			}
			
			ByteArrayInputStream bais = new ByteArrayInputStream(contingut);
			baosConversio = new ByteArrayOutputStream();
			IConverter converter = ConverterRegistry.getRegistry().getConverter(options);
			converter.convert(bais, baosConversio, options);
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

	

	  
	private byte[] removeLinksHeaderFooterOdt(byte[] contingut) {
		try {

			ODPackage p = new ODPackage(new ByteArrayInputStream(contingut));
			for (Object o1 : p.getStyles().getDocument().getContent()) {
				if (o1 instanceof org.jdom.Element) {
					org.jdom.Element e1 = ((org.jdom.Element) o1);
					for (Object o2 : e1.getChildren()) {
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element e2 = ((org.jdom.Element) o2);
							if (e2.getName().equals("master-styles")) {
								for (Object o3 : e2.getChildren()) {
									if (o3 instanceof org.jdom.Element) {
										org.jdom.Element e3 = ((org.jdom.Element) o3);
										if (e3.getName().equals("master-page")) {
											for (Object o4 : e3.getChildren()) {
												org.jdom.Element e4 = ((org.jdom.Element) o4);
												if (e4.getName().equals("header") || e4.getName().equals("footer")) { 
													for (Object o5 : e4.getChildren()) {
														if (o5 instanceof org.jdom.Element) {
															org.jdom.Element e5 = ((org.jdom.Element) o5);
															if (e5.getName().equals("p")) {
																for (Object o6 : e5.getChildren()) {
																	org.jdom.Element e6 = ((org.jdom.Element) o6);
																	e6.getName();
																}
																org.jdom.Element element = e5.getChild("a", Namespace.getNamespace("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0"));
																if (element != null) {
																	String value = element.getValue();
																	element.setName("span");
																	element.setText(value);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			p.save(out);
			byte[] documentBytes = out.toByteArray();
			out.close();
			return documentBytes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] removeLinksHeaderFooterDocx(byte[] contingut) {
		try {
			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(contingut));
			
			for (XWPFHeader header : document.getHeaderList()) {
				removeLinksDocx(header.getBodyElements());
			}
			for (XWPFFooter footer : document.getFooterList()) {
				removeLinksDocx(footer.getBodyElements());
			}
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			byte[] xwpfDocumentBytes = out.toByteArray();
			out.close();
			document.close();
			return xwpfDocumentBytes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void removeLinksDocx(List<IBodyElement> bodyElements) throws Exception {
		for (IBodyElement bodyElement : bodyElements) {
			if (bodyElement instanceof XWPFParagraph) {
				XWPFParagraph paragraph = (XWPFParagraph) bodyElement;
				
				
				//removes hyperlink created with keyword HYPERLINK
				int runToRemove = -1;
				int i = 0;
				for (IRunElement runElement : paragraph.getIRuns()) {
					if (runElement instanceof XWPFRun) {
						XWPFRun run = (XWPFRun) runElement;
						CTR cTR = run.getCTR();
						boolean containsHyperlink = false;
						for (CTText ctText : cTR.getInstrTextList()) {
							if (ctText.getStringValue().contains("HYPERLINK")) {
								containsHyperlink = true;
							}
						}
						if (containsHyperlink) {
							runToRemove = i;
						}
					} 
					i++;
				}
				if (runToRemove != -1) {
					paragraph.removeRun(runToRemove);
				}
				
				
				//removes hyperlink created with tag <w:hyperlink>
				int j = 0;
				int runRemoved = -1;
				String runRemovedText = "";
				int runRemovedFontSize = 0;
				for (IRunElement runElement : paragraph.getIRuns()) {
					if (runElement instanceof XWPFHyperlinkRun) {
						runRemovedText = runElement.toString();
						runRemovedFontSize = ((XWPFHyperlinkRun) runElement).getFontSize();
		                XmlCursor c = ((XWPFHyperlinkRun) runElement).getCTHyperlink().newCursor();
		                c.removeXml();
		                c.dispose();
		                runRemoved = j;
					}
					j++;
				}
				if (runRemoved != -1) {
					XWPFRun xWPFRun = paragraph.insertNewRun(runRemoved);
					xWPFRun.setText(runRemovedText);
					xWPFRun.setColor("0000EE");
					xWPFRun.setFontSize(runRemovedFontSize);
				}
			}
		}
	}
	
	
	private void estamparBarcodePdf417(
			PdfContentByte contentByte,
			String url,
			int posicio,
			float margin) throws Exception {
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
	@Override
	public String getEndpointURL() {
		return getProperty("plugin.conversio.endpointName"); 
	}
}