package es.caib.ripea.core.helper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfBorderArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;

/**
 * Mètodes per generar un índex d'un expedient relacionat en una transacció separada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class IndexBatchHelper {

	private Font frutiger6 = FontFactory.getFont("Frutiger", 6);
	
    private SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat sdtTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private IndexValidacioHelper indexValidacioHelper;
	
	@Transactional(readOnly = true)
	public void processarBatchPDF(
			List<ContingutEntity> continguts, 
			PdfPTable taulaDocuments,
			BigDecimal num,
			BigDecimal sum,
			EntitatEntity entitatActual,
			boolean isRelacio) throws Exception {
		logger.info("Processant nou batch PDF start");
		for (ContingutEntity contingut : continguts) {
			long t1 = System.currentTimeMillis();
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("Processant nou batch PDF start (contingutId=" + contingut.getId() + ")");
			
			if (num.scale() > 0)
				num = num.setScale(0, BigDecimal.ROUND_HALF_UP);
			
			if (contingut instanceof DocumentEntity) {
				DocumentEntity document = (DocumentEntity) contingut;
				if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
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
				CarpetaEntity carpeta = (CarpetaEntity)contingut;
				if (carpeta.getExpedientRelacionat() != null) { // És un expedient importat, mostrar en una fila
					num = crearNovaFila(
							taulaDocuments,
							carpeta,
							entitatActual,
							num,
							isRelacio);
				} else {
					num = crearFilesCarpetaActual(
							num, 
							sum,
							contingut, 
							taulaDocuments, 
							entitatActual, 
							isRelacio);
				}
			}
			
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("Processant nou batch PDF end (contingutId=" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		}
		logger.info("Processant nou batch PDF end");
	}
	
	@Transactional(readOnly = true)
	public int processarBatchXLSX(
			List<DocumentEntity> documentsBatch, 
			Sheet sheet, 
			EntitatEntity entitatActual, 
			Workbook workbook,
			CellStyle cellDataStyle,
			int contentRowIdx) throws NoSuchFileException, IOException {
		logger.info("Processant nou batch XLSX start");
		for (DocumentEntity document : documentsBatch) {
			long t1 = System.currentTimeMillis();
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("Processant nou batch XLSX start (documentId=" + document.getId() + ")");
			if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
				int colIdx = 0;
		        Row dataRow = sheet.createRow(contentRowIdx++);
		        List<String> subTitols = null;
				ArxiuDetallDto arxiuDetall = contingutService.getArxiuDetall(
						entitatActual.getId(),
						document.getId());
					
		        // Nom
		        String nom = document.getNom() != null ? document.getNom() : "";
		        dataRow.createCell(colIdx++).setCellValue(nom);

		        // Descripció
		        String descripcio = document.getDescripcio() != null ? document.getDescripcio() : "";
		        dataRow.createCell(colIdx++).setCellValue(descripcio);
		        
		        
		        // Tipus document
		     	String tipusDocument = document.getDocumentTipus() != null ? messageHelper.getMessage("document.tipus.enum." + document.getDocumentTipus()) : "";
		        
		        if (document.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT)) {
					subTitols = new ArrayList<String>();
					
					if (arxiuDetall != null && arxiuDetall.getMetadadesAddicionals() != null) {
						Object numRegistreMet = arxiuDetall.getMetadadesAddicionals().get("numRegistre");
						Object dataRegistreMet = arxiuDetall.getMetadadesAddicionals().get("dataRegistre");
						if (numRegistreMet != null) {
							String numRegistre = numRegistreMet != null ? numRegistreMet.toString() : "";
							subTitols.add(numRegistre);
						}
						if (dataRegistreMet != null) {
							Date dataRegistre = dataRegistreMet != null ? (Date)dataRegistreMet : null;
							subTitols.add(sdtTime.format(dataRegistre));
						}
					}
					
					if (!subTitols.isEmpty()) {
						String textAddicional = "-----------------------------------\n";
						for (String subTitol: subTitols) {
							textAddicional += subTitol + "\n";
						}
						dataRow.createCell(colIdx++).setCellValue(tipusDocument + "\n" + textAddicional);
					}
				} else {
					dataRow.createCell(colIdx++).setCellValue(tipusDocument);
				}
		        
		        // Nom fitxer
		     	String nombreFichero = document.getFitxerNom();
		        dataRow.createCell(colIdx++).setCellValue(nombreFichero != null ? nombreFichero : "-");
		        
		        // Ubicacó
		        List<ContingutEntity> path = getPathContingut(document);
		        String pathstr = "";
		        if (path.size() > 1) {
		        	pathstr = "/";
			        for (ContingutEntity contigut: path) {
						pathstr += contigut.getNom() + "/";
					}
		        }
		        dataRow.createCell(colIdx++).setCellValue(pathstr);
		        
		        // Tipus documental
		     	String tipusDocumental = document.getNtiTipoDocumental() != null ? messageHelper.getMessage("document.nti.tipdoc.enum." + document.getNtiTipoDocumental()) : "";
		        dataRow.createCell(colIdx++).setCellValue(tipusDocumental);

		        // Origen
		     	String origen = document.getNtiTipoDocumental() != null ? messageHelper.getMessage("expedient.service.exportacio.index.origen." + document.getNtiOrigen()) : "";
		        dataRow.createCell(colIdx++).setCellValue(origen);
		        
		        // Data creació
		     	String dataCreacio = document.getCreatedDate() != null ? sdt.format(document.getCreatedDate().toDate()) : "";
		        dataRow.createCell(colIdx++).setCellValue(dataCreacio);
		        
		     	// Data captura
		     	String dataCaptura = document.getDataCaptura() != null ? sdt.format(document.getDataCaptura()) : "";
		        dataRow.createCell(colIdx++).setCellValue(dataCaptura);
		        
				// Custodiat / Notificat
				DocumentNotificacioEstatEnumCustom estatNotificacio = null;
				List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);		
				boolean hasNotificacions = notificacions != null && !notificacions.isEmpty();
		
				if (hasNotificacions) {
					// Estat darrera notificació
				DocumentNotificacioEstatEnumDto estatLastNotificacio = notificacions.get(0).getNotificacioEstat();
					switch (estatLastNotificacio) {
						case PENDENT:
							estatNotificacio = DocumentNotificacioEstatEnumCustom.PENDENT;
							break;
						case REGISTRADA:
							estatNotificacio = DocumentNotificacioEstatEnumCustom.REGISTRAT;
							break;
						case ENVIADA:
						case ENVIADA_AMB_ERRORS:
							estatNotificacio = DocumentNotificacioEstatEnumCustom.ENVIAT;
							break;
						case FINALITZADA:
						case FINALITZADA_AMB_ERRORS:
							estatNotificacio = DocumentNotificacioEstatEnumCustom.NOTIFICAT;
							break;
						case PROCESSADA:
							estatNotificacio = DocumentNotificacioEstatEnumCustom.NOTIFICAT;
							break;
					}
					
					if (!document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT))
						dataRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.estat." + estatNotificacio));
				}
				
				if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT)) {
					subTitols = new ArrayList<String>();
					Map<Integer, Date> datesFirmes = indexValidacioHelper.recuperarDataFirma(document);
					if (hasNotificacions) {
						String missatgeEstatNotificacio = messageHelper.getMessage("expedient.service.exportacio.index.estat." + estatNotificacio);
						subTitols.add(missatgeEstatNotificacio);
					}
					
					String text = messageHelper.getMessage("expedient.service.exportacio.index.estat.firmat");
					String textAddicional = "";
					for (String subTitol: subTitols) {
						textAddicional += subTitol + "\n";
					}
					dataRow.createCell(colIdx++).setCellValue(text + (textAddicional.isEmpty() ? "" : "\n" + textAddicional));
					

					if (datesFirmes != null && !datesFirmes.isEmpty()) {
						String dataFirma = sdtTime.format(datesFirmes.get(datesFirmes.size()));						
						dataRow.createCell(colIdx++).setCellValue(dataFirma);
					} else {
						dataRow.createCell(colIdx++).setCellValue("-");
					}
				} 
				
				
				if (!hasNotificacions && !document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT)){
					dataRow.createCell(colIdx++).setCellValue("-");
					dataRow.createCell(colIdx++).setCellValue("-");
				}
				
				
//		        // Enllaç directe
		        CellStyle linkStyle = workbook.createCellStyle();
		        org.apache.poi.ss.usermodel.Font linkFont = workbook.createFont();
		        linkFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
		        linkFont.setColor(IndexedColors.BLUE.getIndex());
		        linkStyle.setFont(linkFont);
		        linkStyle.setAlignment(HorizontalAlignment.LEFT);
		        linkStyle.setVerticalAlignment(VerticalAlignment.TOP);
		       
		     	// Enllaç csv
		     	String csv = document.getNtiCsv() != null ? getCsvUrl() + document.getNtiCsv() : "";
		     	if (csv.isEmpty() && arxiuDetall != null && !arxiuDetall.getMetadadesAddicionals().isEmpty()) {
		     		String metadadaAddicionalCsv = (String) arxiuDetall.getMetadadesAddicionals().get("csv");
		     		csv = metadadaAddicionalCsv != null ? getCsvUrl() + metadadaAddicionalCsv : "";
			     }
		        org.apache.poi.ss.usermodel.Cell cellCsv = dataRow.createCell(colIdx++);
		        cellCsv.setCellValue(csv);
		        Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
				hyperlink.setAddress(csv);
				cellCsv.setHyperlink(hyperlink);
				cellCsv.setCellStyle(linkStyle);

				dataRow.setRowStyle(cellDataStyle);
			}
			
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("Processant nou batch XLSX end (documentId=" + document.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		}
		logger.info("Processant nou batch XLSX end");
		return contentRowIdx;
	}
	
	private BigDecimal crearFilesCarpetaActual(
			BigDecimal num, 
			BigDecimal sum, 
			ContingutEntity contingut, 
			PdfPTable taulaDocuments, 
			EntitatEntity entitatActual, 
			boolean isRelacio) throws Exception {
		ContingutEntity carpetaActual = contingut;
		
//		List<ContingutEntity> contingutsCarpetaActual = contingutRepository.findByPareAndEsborrat(
//				carpetaActual, 
//				0, 
//				contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		List<ContingutEntity> contingutsCarpetaActual = new ArrayList<ContingutEntity>();
		if (contingutHelper.isOrdenacioPermesa()) {
			contingutsCarpetaActual = contingutRepository.findByPareAndEsborratAndOrdenatOrdre(carpetaActual, 0);
		} else {
			contingutsCarpetaActual = contingutRepository.findByPareAndEsborratAndOrdenat(carpetaActual, 0);
		}

		for (ContingutEntity contingutCarpetaActual : contingutsCarpetaActual) {
			if (contingutCarpetaActual instanceof CarpetaEntity) {
				CarpetaEntity subCarpeta = (CarpetaEntity)contingutCarpetaActual;
				if (subCarpeta.getExpedientRelacionat() != null) {
					num = crearNovaFila(
							taulaDocuments,
							subCarpeta,
							entitatActual,
							num,
							isRelacio);
				} else {
					num = crearFilesCarpetaActual(
							num, 
							sum,
							contingutCarpetaActual, 
							taulaDocuments, 
							entitatActual,  	
							isRelacio);
				}
			} else {
				DocumentEntity document = (DocumentEntity)contingutCarpetaActual;
				if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
					num = num.add(sum);
					crearNovaFila(
							taulaDocuments,
							document,
							entitatActual,
							num,
							isRelacio);
				}
			}
		}
		return num;
	}
	
	private BigDecimal crearNovaFila(
		PdfPTable taulaDocuments,
		ContingutEntity contingut,
		EntitatEntity entitatActual,
		BigDecimal num,
		boolean isRelacio) throws Exception {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(contingut.getId()));
		if (contingut instanceof DocumentEntity) {
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearNovaFila informació bàsica start (" + contingut.getId() + ")");
	  
			long t1 = System.currentTimeMillis();
			
			DocumentEntity document = (DocumentEntity)contingut;
			logger.debug("Afegint nova fila a la taula de documents...");
			ArxiuDetallDto arxiuDetall = contingutService.getArxiuDetall(
					entitatActual.getId(),
					document.getId());
			List<String> subTitols = null;
			SimpleDateFormat sdtTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			//Nº
			String nextVal = num.scale() > 0 ? String.valueOf(num.doubleValue()) : String.valueOf(num.intValue());
			if (!isRelacio)
				taulaDocuments.addCell(crearCellaContingut(nextVal, null, false));
			
			// Nom document
			String nom = document.getNom() != null ? document.getNom() : "";
			taulaDocuments.addCell(crearCellaContingut(nom, null, false));
			
			if (isMostrarCampsAddicionals() && arxiuDetall != null && arxiuDetall.getMetadadesAddicionals() != null) {
				// Nom natural
				Object tituloDocMet = arxiuDetall.getMetadadesAddicionals().get("tituloDoc");
				String tituloDoc = tituloDocMet != null ? tituloDocMet.toString() : "";
				taulaDocuments.addCell(crearCellaContingut(tituloDoc, null, false));
			}
	
			// Descripció
			String descripcio = document.getDescripcio() != null ? document.getDescripcio() : "";
			taulaDocuments.addCell(crearCellaContingut(descripcio, null, false));
	
			
			// Tipus documental
			String tipusDocumental = document.getNtiTipoDocumental() != null ? messageHelper.getMessage("document.nti.tipdoc.enum." + document.getNtiTipoDocumental()) : "";
			taulaDocuments.addCell(crearCellaContingut(tipusDocumental, null, false));
			
			// Tipus document
			String tipusDocument = document.getDocumentTipus() != null ? messageHelper.getMessage("document.tipus.enum." + document.getDocumentTipus()) : "";
			
			if (document.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT)) {
				subTitols = new ArrayList<String>();
				
				if (arxiuDetall != null && arxiuDetall.getMetadadesAddicionals() != null) {
					Object numRegistreMet = arxiuDetall.getMetadadesAddicionals().get("numRegistre");
					Object dataRegistreMet = arxiuDetall.getMetadadesAddicionals().get("dataRegistre");
					if (numRegistreMet != null) {
						String numRegistre = numRegistreMet != null ? numRegistreMet.toString() : "";
						subTitols.add(numRegistre);
					}
					if (dataRegistreMet != null) {
						Date dataRegistre = dataRegistreMet != null ? (Date)dataRegistreMet : null;
						subTitols.add(sdtTime.format(dataRegistre));
					}
				}
			}
			taulaDocuments.addCell(crearCellaContingut(tipusDocument, subTitols, false));
	
			// Data creació
			SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");
			String dataCreacio = document.getCreatedDate() != null ? sdt.format(document.getCreatedDate().toDate()) : "";
			taulaDocuments.addCell(crearCellaContingut(dataCreacio, null, false));
			
			// Enllaç csv
			String csv = document.getNtiCsv() != null ? getCsvUrl() + document.getNtiCsv() : "";
			if (csv.isEmpty() && arxiuDetall != null && !arxiuDetall.getMetadadesAddicionals().isEmpty()) {
				String metadadaAddicionalCsv = (String) arxiuDetall.getMetadadesAddicionals().get("csv");
				csv = metadadaAddicionalCsv != null ? getCsvUrl() + metadadaAddicionalCsv : "";
			}
			taulaDocuments.addCell(crearCellaContingut(csv, null, true));
			
			// Data captura
			String dataCaptura = document.getDataCaptura() != null ? sdt.format(document.getDataCaptura()) : "";
			taulaDocuments.addCell(crearCellaContingut(dataCaptura, null, false));	
			
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearNovaFila informació bàsica end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");

			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearNovaFila findByDocumentOrderByCreatedDateDesc start (" + contingut.getId() + ")");
	  
			long t2 = System.currentTimeMillis();
			// Custodiat / Notificat
			DocumentNotificacioEstatEnumCustom estatNotificacio = null;
			List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByCreatedDateDesc((DocumentEntity)document);		
			boolean hasNotificacions = notificacions != null && !notificacions.isEmpty();
	
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearNovaFila findByDocumentOrderByCreatedDateDesc end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t2) + " ms");
			
			if (hasNotificacions) {
				// Estat darrera notificació
				DocumentNotificacioEstatEnumDto estatLastNotificacio = notificacions.get(0).getNotificacioEstat();
				switch (estatLastNotificacio) {
					case PENDENT:
						estatNotificacio = DocumentNotificacioEstatEnumCustom.PENDENT;
						break;
					case REGISTRADA:
						estatNotificacio = DocumentNotificacioEstatEnumCustom.REGISTRAT;
					case ENVIADA:
					case ENVIADA_AMB_ERRORS:
						estatNotificacio = DocumentNotificacioEstatEnumCustom.ENVIAT;
						break;
					case FINALITZADA:
					case FINALITZADA_AMB_ERRORS:
						estatNotificacio = DocumentNotificacioEstatEnumCustom.NOTIFICAT;
						break;
					case PROCESSADA:
						estatNotificacio = DocumentNotificacioEstatEnumCustom.NOTIFICAT;
						break;
				}
				
				if (!document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT))
					taulaDocuments.addCell(crearCellaContingut(messageHelper.getMessage("expedient.service.exportacio.index.estat." + estatNotificacio), null, false));
			}
			
			if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT)) {
				subTitols = new ArrayList<String>();
				Map<Integer, Date> datesFirmes = indexValidacioHelper.recuperarDataFirma(document);
				if (datesFirmes != null && !datesFirmes.isEmpty()) {
					String dataFirma = sdtTime.format(datesFirmes.get(datesFirmes.size()));
					subTitols.add(dataFirma);
				} 
				
				if (hasNotificacions) {
					String missatgeEstatNotificacio = messageHelper.getMessage("expedient.service.exportacio.index.estat." + estatNotificacio);
					subTitols.add(missatgeEstatNotificacio);
				}
				taulaDocuments.addCell(crearCellaContingut(messageHelper.getMessage("expedient.service.exportacio.index.estat.firmat"), subTitols, false));
			} 
			
			
			if (!hasNotificacions && !document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT)){
				taulaDocuments.addCell(crearCellaContingut("-", null, false));
			}
		} else {
			CarpetaEntity carpeta = (CarpetaEntity)contingut;
			if (!isRelacio) {
				BigDecimal sum = new BigDecimal(1);
				num = num.add(sum);
				String nextVal = num.scale() > 0 ? String.valueOf(num.doubleValue()) : String.valueOf(num.intValue());
				taulaDocuments.addCell(crearCellaContingut(nextVal, null, false));
			}
			if (carpeta.getExpedientRelacionat() != null)
				taulaDocuments.addCell(crearCellaUnica(carpeta.getNom(), carpeta.getExpedientRelacionat().getId(), isRelacio));
		}
		return num;
	}
	
	private List<ContingutEntity> getPathContingut(ContingutEntity contingut) {
		List<ContingutEntity> path = null;
		ContingutEntity contingutActual = contingut;
		while (contingutActual != null && contingutActual.getPare() != null) {
			if (path == null)
				path = new ArrayList<ContingutEntity>();
			ContingutEntity c = contingutRepository.findOne(contingutActual.getPare().getId());
			path.add(c);
			contingutActual = c;
		}
		if (path != null) {
			Collections.reverse(path);
		}
		return path;
	}
	
	
	private PdfPCell crearCellaUnica(String titol, Long destinationId, boolean isRelacio) throws NoSuchFileException, IOException {
		PdfPCell titolCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph("", frutiger6);
		// Enllaç intern a l'expedient relacionat
		String internalLink = "expedient_" + destinationId;
		Chunk internalLinkChunk = new Chunk(titol);
		internalLinkChunk.setLocalGoto(internalLink);
		titolParagraph.add(internalLinkChunk);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		
		if (!isRelacio && isMostrarCampsAddicionals())
			titolCell.setColspan(9);
		else if (!isRelacio && !isMostrarCampsAddicionals())
			titolCell.setColspan(8);
		else if (isRelacio && isMostrarCampsAddicionals())
			titolCell.setColspan(9);
		else if (isRelacio && !isMostrarCampsAddicionals())
			titolCell.setColspan(8);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBorderWidth((float) 0.5);
		return titolCell;
	}
	
	private PdfPCell crearCellaContingut(String titol, List<String> subTitols, boolean isLink) {
		PdfPCell titolCell = new PdfPCell();
		Paragraph titolParagraph = new Paragraph(titol, frutiger6);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		if (subTitols != null && !subTitols.isEmpty()) {
			for (String subTitol : subTitols) {
				Paragraph subTitolParagraph = new Paragraph(subTitol, frutiger6);
				subTitolParagraph.setAlignment(Element.ALIGN_CENTER);
				titolCell.addElement(subTitolParagraph);
			}
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
	
	private boolean isMostrarCampsAddicionals() throws NoSuchFileException, IOException {
		return configHelper.getAsBoolean("es.caib.ripea.index.expedient.camps.addicionals");
	}

	private String getCsvUrl() throws NoSuchFileException, IOException {
		return configHelper.getConfig("es.caib.ripea.documents.validacio.url");
	}

	protected enum DocumentNotificacioEstatEnumCustom {PENDENT, REGISTRAT, ENVIAT, NOTIFICAT};

	private static final Logger logger = LoggerFactory.getLogger(IndexBatchHelper.class);
}
