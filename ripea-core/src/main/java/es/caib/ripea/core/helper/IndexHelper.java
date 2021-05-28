package es.caib.ripea.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfBorderArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;

/**
 * Mètodes per generar un índex d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class IndexHelper {

	private Font frutiger7 = FontFactory.getFont("Frutiger", 6, Font.BOLD, new BaseColor(255, 255, 255)); // #7F7F7F
	private Font frutiger6 = FontFactory.getFont("Frutiger", 6);
	private Font frutiger11TitolBold = FontFactory.getFont("Frutiger", 11, Font.BOLD);
	private Font frutiger9TitolBold = FontFactory.getFont("Frutiger", 9, Font.BOLD);
	private Font frutiger10Italic = FontFactory.getFont("Frutiger", 10, Font.ITALIC, new BaseColor(160, 160, 160));
	
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	
	public byte[] generarIndexPerExpedient(
			ExpedientEntity expedient, 
			EntitatEntity entitatActual) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document index = inicialitzaDocument(out);
			
			crearTitol(
					index, 
					expedient,
					false);
			
			crearTaulaDocuments(
					index, 
					expedient, 
					entitatActual,
					false);
			
//			## Crear un índex per cada expedient relacionat
			if (!expedient.getRelacionatsAmb().isEmpty() && indexExpedientsRelacionats()) {
//				## [TAULA QUE CONTÉ EL TÍTOL 'EXPEDIENTS RELACIONATS']
				PdfPTable titolRelacioTable = new PdfPTable(1);
				titolRelacioTable.setWidthPercentage(100);

//				## [TITOL EXPEDIENTS RELACIONATS]
				PdfPCell relacioTitolCell = new PdfPCell();
				relacioTitolCell.setBorder(Rectangle.BOTTOM);
				relacioTitolCell.setBorderColor(new BaseColor(160, 160, 160));
				Paragraph relacioTitol = new Paragraph(messageHelper.getMessage("expedient.service.exportacio.index.relacions"), frutiger10Italic);
				relacioTitol.add(Chunk.NEWLINE);
				relacioTitolCell.addElement(relacioTitol);
				titolRelacioTable.addCell(relacioTitolCell);
				index.add(titolRelacioTable);
				
//				## [TÍTOL I TAULA PER CADA RELACIÓ]
				for (ExpedientEntity expedient_relacionat: expedient.getRelacionatsAmb()) {
					crearTitol(
							index, 
							expedient_relacionat,
							true);
					crearTaulaDocuments(
							index, 
							expedient_relacionat, 
							entitatActual,
							true);
				}
			}
			
			index.close();
		} catch (Exception ex) {
			throw new RuntimeException(
					"S'ha produït un error generant l'índex de l'expedient",
					ex);
		}

		return out.toByteArray();
	}
	
	private void crearTitol(
			Document index,
			ExpedientEntity expedient,
			boolean isRelacio) {
		logger.debug("Creant el títol de l'índex per l'expedient [expedientId=" + expedient.getId() + "]");
		try {
//			## [TAULA QUE CONTÉ TÍTOL I INTRODUCCIÓ]
			PdfPTable titolIntroduccioTable = new PdfPTable(1);
			titolIntroduccioTable.setWidthPercentage(100);
			
//			## [TITOL ÍNDEX]
			PdfPCell titolIntroduccioCell = new PdfPCell();
			titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);
			
			Paragraph indexTitol = new Paragraph(expedient.getNom(), frutiger11TitolBold);
			indexTitol.setAlignment(Element.ALIGN_CENTER);
			String subtitol = expedient.getMetaExpedient().getNom() + " [" + expedient.getMetaExpedient().getClassificacioSia() + "] (" + expedientHelper.calcularNumero(expedient) + ")";
			Paragraph indexSubtitol = new Paragraph(subtitol, frutiger9TitolBold);
			indexSubtitol.setAlignment(Element.ALIGN_CENTER);
			indexSubtitol.add(Chunk.NEWLINE);
			
			titolIntroduccioCell.addElement(indexTitol);
			titolIntroduccioCell.addElement(indexSubtitol);
			
			titolIntroduccioTable.addCell(titolIntroduccioCell);
			titolIntroduccioTable.setSpacingAfter(10f);
			index.add(titolIntroduccioTable);
		} catch (DocumentException ex) {
			logger.error("Hi ha hagut un error generant la introducció de l'índex", ex);
		}
	}
	
	private void crearTaulaDocuments(
			Document index, 
			ExpedientEntity expedient,
			EntitatEntity entitatActual,
			boolean isRelacio) {
		logger.debug("Generant la taula amb els documents de l'expedient [expedientId=" + expedient.getId() + "]");
		try {
//			## [DEFINICIÓ TAULA]
			float [] pointColumnWidths;
			PdfPTable taulaDocuments;
			if (!isRelacio) {
				if (isMostrarCampsAddicionals()) {
					pointColumnWidths = new float[] {3f, 10f, 10f, 14f, 10f, 12f, 10f, 19f, 10f, 11f};
					taulaDocuments = new PdfPTable(10);
				} else {
					pointColumnWidths = new float[] {3f, 10f, 14f, 10f, 12f, 10f, 19f, 10f, 11f};
					taulaDocuments = new PdfPTable(9);

				}
				
			} else {
				if (isMostrarCampsAddicionals()) {
					pointColumnWidths = new float[] {12f, 12f, 17f, 13f, 13f, 11f, 19f, 11f, 11f};
					taulaDocuments = new PdfPTable(9);
				} else {
					pointColumnWidths = new float[] {12f, 17f, 13f, 13f, 11f, 19f, 11f, 11f};
					taulaDocuments = new PdfPTable(8);

				}
			}
			taulaDocuments.setWidthPercentage(100f);
			taulaDocuments.setWidths(pointColumnWidths);
			
//			## [TÍTOL]
			crearCapsaleraTaula(taulaDocuments, isRelacio);
			
//			## [CONTINGUT]
			crearContingutTaula(taulaDocuments, expedient, entitatActual, isRelacio);
			
			index.add(taulaDocuments);
			if (!isRelacio)
				index.add(Chunk.NEXTPAGE);
		} catch (Exception ex) {
			logger.error("Hi ha hagut un error generant la taula dels documents", ex);
		}
	}
	
	private void crearContingutTaula(
			PdfPTable taulaDocuments,
			ExpedientEntity expedient,
			EntitatEntity entitatActual,
			boolean isRelacio) throws Exception {
		logger.debug("Generant la capçalera de la taula de documents");
		List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
			expedient, 
			0, 
			new Sort("createdDate"));
		BigDecimal num = new BigDecimal(0);
		
		for (ContingutEntity contingut : continguts) {
			if (num.scale() > 0)
				num = num.setScale(0, BigDecimal.ROUND_HALF_UP);
			
			if (contingut instanceof DocumentEntity) {
				DocumentEntity document = (DocumentEntity) contingut;
				if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
					BigDecimal sum = new BigDecimal(1);
					num = num.add(sum);
					crearNovaFila(
							taulaDocuments,
							document,
							entitatActual,
							num,
							isRelacio);
				}
			}
			if (contingut instanceof CarpetaEntity) {
				BigDecimal sum = new BigDecimal(1);
				num = num.add(sum);
				
				List<String> estructuraCarpetes = new ArrayList<String>();
				List<DocumentEntity> documentsCarpeta = new ArrayList<DocumentEntity>();
				ContingutEntity carpetaActual = contingut;
				while (carpetaActual instanceof CarpetaEntity) {
					boolean darreraCarpeta = true;
					estructuraCarpetes.add(carpetaActual.getNom());
					
					for (ContingutEntity contingutCarpetaActual : carpetaActual.getFills()) {
						if (contingutCarpetaActual instanceof CarpetaEntity) {
							carpetaActual = contingutCarpetaActual;
							darreraCarpeta = false;
						} else {
							documentsCarpeta.add((DocumentEntity) contingutCarpetaActual);
						}
					}
					for (DocumentEntity document : documentsCarpeta) {
						if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
							BigDecimal sum2 = new BigDecimal(0.1);
							num = num.add(sum2);
							crearNovaFila(
									taulaDocuments,
									document,
									entitatActual,
									num,
									isRelacio);
						}
					}
					documentsCarpeta = new ArrayList<DocumentEntity>();
					if (darreraCarpeta)
						break;
				}
			}
		}
	}
	
	private void crearNovaFila(
		PdfPTable taulaDocuments,
		DocumentEntity document,
		EntitatEntity entitatActual,
		BigDecimal num,
		boolean isRelacio) throws Exception {
		logger.debug("Afegint nova fila a la taula de documents...");
		ArxiuDetallDto arxiuDetall = contingutService.getArxiuDetall(
				entitatActual.getId(),
				document.getId());
		
//		Nº
		String nextVal = num.scale() > 0 ? String.valueOf(num.doubleValue()) : String.valueOf(num.intValue());
		if (!isRelacio)
			taulaDocuments.addCell(crearCellaContingut(nextVal, null, false));
		
//		Nom document
		String nom = document.getNom() != null ? document.getNom() : "";
		taulaDocuments.addCell(crearCellaContingut(nom, null, false));
		
		if (isMostrarCampsAddicionals() && arxiuDetall != null && arxiuDetall.getMetadadesAddicionals() != null) {
//			Nom natural
			String tituloDoc = arxiuDetall.getMetadadesAddicionals().get("tituloDoc") != null ? arxiuDetall.getMetadadesAddicionals().get("tituloDoc").toString() : "";
			taulaDocuments.addCell(crearCellaContingut(tituloDoc, null, false));
		}

//		Descripció
		String descripcio = document.getDescripcio() != null ? document.getDescripcio() : "";
		taulaDocuments.addCell(crearCellaContingut(descripcio, null, false));

		
//		Tipus documental
		String tipusDocumental = document.getNtiTipoDocumental() != null ? messageHelper.getMessage("document.nti.tipdoc.enum." + document.getNtiTipoDocumental()) : "";
		taulaDocuments.addCell(crearCellaContingut(tipusDocumental, null, false));
		
//		Tipus document
		String tipusDocument = document.getDocumentTipus() != null ? messageHelper.getMessage("document.tipus.enum." + document.getDocumentTipus()) : "";
		taulaDocuments.addCell(crearCellaContingut(tipusDocument, null, false));

//		Data creació
		SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");
		String dataCreacio = document.getCreatedDate() != null ? sdt.format(document.getCreatedDate().toDate()) : "";
		taulaDocuments.addCell(crearCellaContingut(dataCreacio, null, false));
		
//		Enllaç csv
		String csv = document.getNtiCsv() != null ? getCsvUrl() + document.getNtiCsv() : "";
		if (csv.isEmpty() && arxiuDetall != null && !arxiuDetall.getMetadadesAddicionals().isEmpty()) {
			String metadadaAddicionalCsv = (String) arxiuDetall.getMetadadesAddicionals().get("csv");
			csv = metadadaAddicionalCsv != null ? getCsvUrl() + metadadaAddicionalCsv : "";
		}
		taulaDocuments.addCell(crearCellaContingut(csv, null, true));
		
//		Data captura
		String dataCaptura = document.getDataCaptura() != null ? sdt.format(document.getDataCaptura()) : "";
		taulaDocuments.addCell(crearCellaContingut(dataCaptura, null, false));	
		
//		Custodiat / Notificat
		List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);		
		boolean hasNotificacions = notificacions != null && !notificacions.isEmpty();
		
		if (hasNotificacions) {
			taulaDocuments.addCell(crearCellaContingut(messageHelper.getMessage("expedient.service.exportacio.index.estat.notificat"), null, false));
		} else if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT)) {
			Date dataFirma = null;
			try {
				if (pluginHelper.isArxiuPluginActiu()) {
					es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
							document,
							null,
							null,
							true,
							false);
					byte[] contingut = documentHelper.getContingutFromArxiuDocument(arxiuDocument);
					dataFirma = getDataFirmaFromDocument(contingut);
				}
			} catch (Exception ex) {
				logger.error("Hi ha hagut un error recuperant l'hora de firma del document", ex);
			}
			if (dataFirma != null) {
//				Data firma
				SimpleDateFormat sdtTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String dataFirmaFormatted = sdtTime.format(dataFirma);
				taulaDocuments.addCell(crearCellaContingut(messageHelper.getMessage("expedient.service.exportacio.index.estat.firmat"), "(" + dataFirmaFormatted + ")", false));
			} else {
				taulaDocuments.addCell(crearCellaContingut(messageHelper.getMessage("expedient.service.exportacio.index.estat.firmat"), null, false));
			}
		} else {
			taulaDocuments.addCell(crearCellaContingut("-", null, false));
		}
	}
	
	private Date getDataFirmaFromDocument(byte[] content) throws IOException {
		Date dataFirma = null;
		PdfReader reader = new PdfReader(content);
		AcroFields fields = reader.getAcroFields();
		
		List<String> signatureNames = fields.getSignatureNames();
		if (signatureNames != null) {
			for (String name: signatureNames) {
//				### comprovar si és una firma o un segell
				PdfDictionary dictionary = fields.getSignatureDictionary(name);
				if (dictionary != null && dictionary.get(PdfName.TYPE).toString().equals("/Sig")) {
					String dataFirmaStr = dictionary.get(PdfName.M) != null ? dictionary.get(PdfName.M).toString() : null;
					dataFirma = dataFirmaStr != null ? PdfDate.decode(dataFirmaStr).getTime() : null;
					break;
				}
			}
		}
		return dataFirma;
	}
	
	private Document inicialitzaDocument(
			ByteArrayOutputStream out) throws DocumentException {
		logger.debug("Inicialitzant el document...");
//		## [Event per crear el header]
		HeaderPageEvent headerEvent = new HeaderPageEvent();
		
	    Document index = new Document(PageSize.A4.rotate(), 36, 36, 35 + headerEvent.getTableHeight(), 36);
		PdfWriter writer = PdfWriter.getInstance(index, out);
//		writer.setViewerPreferences(PdfWriter.ALLOW_PRINTING);
		
		writer.setPageEvent(headerEvent);
		
		index.open();
		index.addAuthor("Ripea");
		index.addCreationDate();
		index.addCreator("iText library");

		return index;
	}
	
	private class HeaderPageEvent extends PdfPageEventHelper {
		private PdfPTable header;
		private float tableHeight;
	    
		
	    public float getTableHeight() {
			return tableHeight;
		}

		public void onEndPage(PdfWriter writer, Document index) {
			header.writeSelectedRows(
					0, 
					-1,
					index.left(),
					505 + ((index.topMargin() + tableHeight) / 2),
                    writer.getDirectContent());
	    }
		
		private HeaderPageEvent() {
			try {
				PdfPCell cellDireccio = new PdfPCell();
				header = new PdfPTable(2);
				header.setTotalWidth(523);
				header.setLockedWidth(true);
				Image logoCapsalera = null;
				
	//			## [LOGO ENTITAT]
				if (getLogo() != null && !getLogo().isEmpty()) {
					logoCapsalera = Image.getInstance(getLogo());
				} else {
					byte[] logoBytes = IOUtils.toByteArray(getCapsaleraDefaultLogo());
					logoCapsalera = Image.getInstance(logoBytes);
				}
				
				if (logoCapsalera != null) {
					logoCapsalera.scaleToFit(120f, 50f);
					PdfPCell cellLogo = new PdfPCell(logoCapsalera);
					cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
					cellLogo.setBorder(Rectangle.NO_BORDER);
					header.addCell(cellLogo);
				}
	
				cellDireccio.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellDireccio.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellDireccio.setBorder(Rectangle.NO_BORDER);
				header.addCell(cellDireccio);
				tableHeight = header.getTotalHeight();
			} catch (Exception ex) {
				logger.error("Hi ha hagut un error generant el header del document", ex);
			}
		}
	}

	private void crearCapsaleraTaula(PdfPTable taulaDocuments, boolean isRelacio) throws NoSuchFileException, IOException {
		logger.debug("Generant la capçalera de la taula de documents");
		if (!isRelacio)
			taulaDocuments.addCell(crearCellaCapsalera("Nº"));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.nom")));
		if (isMostrarCampsAddicionals())
			taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.nomnatural")));

		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.descripcio")));

		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.tipusdocumental")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.tipusdocument")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.datacreacio")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.link")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.datadocument")));
		taulaDocuments.addCell(crearCellaCapsalera(messageHelper.getMessage("expedient.service.exportacio.index.estat")));
	}
	
	private PdfPCell crearCellaCapsalera(String titol) {
		PdfPCell titolCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph(titol, frutiger7);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBackgroundColor(new BaseColor(166, 166, 166));
		titolCell.setBorderWidth((float) 0.5);
		return titolCell;
	}
	
	private PdfPCell crearCellaContingut(String titol, String subTitol, boolean isLink) {
		PdfPCell titolCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph(titol, frutiger6);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		if (subTitol != null) {
			Paragraph subTitolParagraph = new Paragraph(subTitol, frutiger6);
			subTitolParagraph.setAlignment(Element.ALIGN_CENTER);
			titolCell.addElement(subTitolParagraph);
		}
		titolCell.setPaddingBottom(6f);
		titolCell.setBorderWidth((float) 0.5);
		if (titol != null && !titol.isEmpty() && isLink) {
			titolCell.setCellEvent(new LinkInCell(titol));
		}
		return titolCell;
	}
	
	private class LinkInCell implements PdfPCellEvent {
	    protected String url;
	    public LinkInCell(String url) {
	        this.url = url;
	    }
	    
	    public void cellLayout(
	    		PdfPCell cell, 
	    		Rectangle position,
	    		PdfContentByte[] canvases) {
	    	PdfWriter writer = canvases[0].getPdfWriter();
	    	PdfAction action = new PdfAction(url);
	        PdfAnnotation link = PdfAnnotation.createLink(writer, position, PdfAnnotation.HIGHLIGHT_NONE, action);
	        link.setBorder(new PdfBorderArray(0f, 0f, 0f));
	        writer.addAnnotation(link);
	    }
	}
	
	private InputStream getCapsaleraDefaultLogo() {
		return getClass().getResourceAsStream("/es/caib/ripea/core/templates/govern-logo.png");
	}
	
	private String getLogo() throws NoSuchFileException, IOException {
		String filePath = PropertiesHelper.getProperties().getProperty("es.caib.ripea.index.logo");
		return filePath;
	}
	
	private boolean indexExpedientsRelacionats() throws NoSuchFileException, IOException {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.ripea.index.expedients.relacionats");
	}
	
	private String getCsvUrl() throws NoSuchFileException, IOException {
		String filePath = PropertiesHelper.getProperties().getProperty("es.caib.ripea.documents.validacio.url");
		return filePath;
	}
	
	private boolean isMostrarCampsAddicionals() throws NoSuchFileException, IOException {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.ripea.index.expedient.camps.addicionals");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(IndexHelper.class);

}
