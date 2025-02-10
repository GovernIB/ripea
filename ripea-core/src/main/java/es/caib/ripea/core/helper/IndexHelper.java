package es.caib.ripea.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.persistence.CarpetaEntity;
import es.caib.ripea.core.persistence.ContingutEntity;
import es.caib.ripea.core.persistence.DocumentEntity;
import es.caib.ripea.core.persistence.EntitatEntity;
import es.caib.ripea.core.persistence.ExpedientEntity;
import es.caib.ripea.core.persistence.MetaExpedientEntity;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DocumentRepository;

/**
 * Mètodes per generar un índex d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class IndexHelper {

	private Font frutiger7 = FontFactory.getFont("Frutiger", 6, Font.BOLD, new BaseColor(255, 255, 255)); // #7F7F7F
	private Font frutiger11TitolBold = FontFactory.getFont("Frutiger", 11, Font.BOLD);
	private Font frutiger9TitolBold = FontFactory.getFont("Frutiger", 9, Font.BOLD);
	private Font frutiger10Italic = FontFactory.getFont("Frutiger", 10, Font.ITALIC, new BaseColor(160, 160, 160));

    private SimpleDateFormat sdtTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    private static final Integer BATCH_SIZE = 10;
    
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private IndexBatchHelper indexBatchHelper;
	
	//### Genera índex PDF ###
	public byte[] generarIndexPdfPerCarpetes(
			List<CarpetaEntity> carpetes, 
			EntitatEntity entitatActual) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document index = inicialitzaDocument(out);
			
			for (Iterator<CarpetaEntity> it = carpetes.iterator(); it.hasNext();) {
				CarpetaEntity carpeta = it.next();
				crearTitol(
						index, 
						carpeta,
						false);
				
				crearTaulaDocuments(
						index, 
						carpeta, 
						entitatActual,
						false);
			}
			index.close();
		} catch (Exception ex) {
			throw new RuntimeException(
					"S'ha produït un error generant l'índex de la carpeta",
					ex);
		}
		
		return out.toByteArray();
	}
	
	//### Genera índex PDF ###
	public byte[] generarIndexPdfPerExpedient(
			List<ExpedientEntity> expedients, 
			EntitatEntity entitatActual,
			boolean exportar) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document index = inicialitzaDocument(out);
			
			for (Iterator<ExpedientEntity> it = expedients.iterator(); it.hasNext();) {
				ExpedientEntity expedient = it.next();
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
				if ((!expedient.getRelacionatsPer().isEmpty() || !expedient.getRelacionatsAmb().isEmpty()) && indexExpedientsRelacionats()) {
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
					
					if (!expedient.getRelacionatsAmb().isEmpty()) {
	//					## [TÍTOL I TAULA PER CADA RELACIÓ]
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
					if (!expedient.getRelacionatsPer().isEmpty()) {
	//					## [TÍTOL I TAULA PER CADA RELACIÓ]
						for (ExpedientEntity expedient_relacionat: expedient.getRelacionatsPer()) {
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
					

					if (it.hasNext())
						index.add(Chunk.NEXTPAGE);
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
			ContingutEntity contingut,
			boolean isRelacio) {
		logger.debug("Creant el títol de l'índex pel contingut [contingutId=" + contingut.getId() + "]");
		try {
//			## [TAULA QUE CONTÉ TÍTOL I INTRODUCCIÓ]
			PdfPTable titolIntroduccioTable = new PdfPTable(1);
			titolIntroduccioTable.setWidthPercentage(100);
			
//			## [TITOL ÍNDEX]
			PdfPCell titolIntroduccioCell = new PdfPCell();
			titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);

			Paragraph titolParagraph = new Paragraph();
			Chunk localDest = new Chunk(contingut.getNom(), frutiger11TitolBold);
			localDest.setLocalDestination("expedient_" + contingut.getId());
			titolParagraph.add(localDest);
			titolParagraph.setAlignment(Element.ALIGN_CENTER);
			String subtitol = "";
			
			if (contingut instanceof ExpedientEntity) {
				ExpedientEntity expedient = (ExpedientEntity)contingut;
				MetaExpedientEntity metaExpedient = contingut instanceof ExpedientEntity ? ((ExpedientEntity)contingut).getMetaExpedient() : null;
				subtitol = metaExpedient.getNom() + " [" + metaExpedient.getClassificacio() + "] (" + expedient.getNumero() + ")";
			}
			
			Paragraph subTitolParagraph = new Paragraph(subtitol, frutiger9TitolBold);
			subTitolParagraph.setAlignment(Element.ALIGN_CENTER);
			subTitolParagraph.add(Chunk.NEWLINE);
			
			titolIntroduccioCell.addElement(titolParagraph);
			titolIntroduccioCell.addElement(subTitolParagraph);
			
			titolIntroduccioTable.addCell(titolIntroduccioCell);
			titolIntroduccioTable.setSpacingAfter(10f);
			index.add(titolIntroduccioTable);
		} catch (DocumentException ex) {
			logger.error("Hi ha hagut un error generant la introducció de l'índex", ex);
		}
	}
	
	private void crearTaulaDocuments(
			Document index, 
			ContingutEntity contingut,
			EntitatEntity entitatActual,
			boolean isRelacio) {
		logger.debug("Generant la taula amb els documents del contingut [contingutId=" + contingut.getId() + "]");
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
			
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearTaulaDocuments start (" + contingut.getId() + ")");
	    	
			long t1 = System.currentTimeMillis();
			
//			## [CONTINGUT]
			crearContingutTaula(taulaDocuments, contingut, entitatActual, isRelacio);
			
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearTaulaDocuments end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
			
			index.add(taulaDocuments);
			if (!isRelacio)
				index.add(Chunk.NEXTPAGE);
		} catch (Exception ex) {
			logger.error("Hi ha hagut un error generant la taula dels documents", ex);
		}
	}
	
	private void crearContingutTaula(
			PdfPTable taulaDocuments,
			ContingutEntity contingut,
			EntitatEntity entitatActual,
			boolean isRelacio) throws Exception {
		logger.debug("Generant la capçalera de la taula de documents");
//		List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
//			expedient, 
//			0, 
//			contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		
		if (aplicacioService.mostrarLogsRendiment())
    		logger.info("findByPareAndEsborratAndOrdenatOrdre start (" + contingut.getId() + ")");
    	
		long t1 = System.currentTimeMillis();
		
		List<ContingutEntity> continguts = new ArrayList<ContingutEntity>();
		if (contingutHelper.isOrdenacioPermesa()) {
			continguts = contingutRepository.findByPareAndEsborratAndOrdenatOrdre(contingut, 0);
		} else {
			continguts = contingutRepository.findByPareAndEsborratAndOrdenat(contingut, 0);
		}

		if (aplicacioService.mostrarLogsRendiment())
    		logger.info("findByPareAndEsborratAndOrdenatOrdre end (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		
		BigDecimal num = new BigDecimal(0);
		BigDecimal sum = new BigDecimal(1);
		
		List<List<ContingutEntity>> contingutsBatches = Lists.partition(continguts, BATCH_SIZE);
		
		for (List<ContingutEntity> contingutsBatch: contingutsBatches) {
			indexBatchHelper.processarBatchPDF(
					contingutsBatch, 
					taulaDocuments, 
					num, 
					sum, 
					entitatActual, 
					isRelacio);
		}
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
	
	//### Genera índex XSL/XLSX ###
	public byte[] generarIndexXlsxPerCarpetes(
			List<CarpetaEntity> carpetes, 
			EntitatEntity entitatActual) {

		try (Workbook workbook = new XSSFWorkbook()) {			
			for (CarpetaEntity carpeta : carpetes) {
				Sheet sheet = workbook.createSheet(validarNombreHoja(carpeta.getNom()));
	            crearTitol(sheet, carpeta);
	            crearTaulaDocuments(sheet, carpeta, entitatActual, workbook, false, false, true);
	            
	            List<ContingutEntity> fills = contingutRepository.findByPareAndEsborratAndOrdenat(carpeta, 0);
	            for (ContingutEntity fill: fills) {
					if (fill instanceof CarpetaEntity) {
						Sheet sheetFill = workbook.createSheet(validarNombreHoja(fill.getNom()));
			            crearTitol(sheetFill, fill);
			            crearTaulaDocuments(sheetFill, fill, entitatActual, workbook, false, false, true);
			            
			         // Le da formato a la hoja
			            formatExcel(workbook, sheetFill, false, false, true);
					}
				}
	         // Le da formato a la hoja
	            formatExcel(workbook, sheet, false, false, true);
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        workbook.write(out);
	        
	        return out.toByteArray();
	    } catch (Exception ex) {
	        throw new RuntimeException("S'ha produït un error generant l'índex de l'expedient", ex);
	    }
		
	}

	//### Genera índex XSL/XLSX ###
	public byte[] generarIndexXlsxPerExpedient(List<ExpedientEntity> expedients, EntitatEntity entitatActual, boolean exportar) {
	    try (Workbook workbook = new XSSFWorkbook()) {
	        
	        int rowNum = 0;
	        for (ExpedientEntity expedient : expedients) {
	        	boolean hasRelacions = !expedient.getRelacionatsPer().isEmpty() || !expedient.getRelacionatsAmb().isEmpty();
		        Sheet sheet = workbook.createSheet(validarNombreHoja(expedient.getNom()));
	            crearTitol(sheet, expedient);
	            crearTaulaDocuments(sheet, expedient, entitatActual, workbook, false, hasRelacions, false);

	            if ((!expedient.getRelacionatsPer().isEmpty() || !expedient.getRelacionatsAmb().isEmpty()) && indexExpedientsRelacionats()) {
	                crearTitolRelacio(sheet, expedient.getRelacionatsAmb(), expedient.getRelacionatsPer());

	                if (!expedient.getRelacionatsAmb().isEmpty()) {
	                    for (ExpedientEntity expedient_relacionat : expedient.getRelacionatsAmb()) {
	                    	Sheet sheetRelacio = workbook.createSheet(validarNombreHoja(expedient_relacionat.getNom()));
	                        crearTitol(sheetRelacio, expedient_relacionat);
	                        crearTaulaDocuments(sheetRelacio, expedient_relacionat, entitatActual, workbook, true, hasRelacions, false);
	                        
	                        // Le da formato a la hoja
	        	            formatExcel(workbook, sheetRelacio, true, hasRelacions, false);
	                    }
	                }
	                if (!expedient.getRelacionatsPer().isEmpty()) {
	                    for (ExpedientEntity expedient_relacionat : expedient.getRelacionatsPer()) {
	                    	Sheet sheetRelacio = workbook.createSheet(validarNombreHoja(expedient_relacionat.getNom()));
	                        crearTitol(sheetRelacio, expedient_relacionat);
	                        crearTaulaDocuments(sheetRelacio, expedient_relacionat, entitatActual, workbook, true, hasRelacions, false);
	                        
	                        // Le da formato a la hoja
	        	            formatExcel(workbook, sheetRelacio, true, hasRelacions, false);
	                    }
	                }

	                if (rowNum < expedients.size() - 1) {
	                	sheet.createRow(rowNum++).createCell(0).setCellStyle(workbook.createCellStyle());
	                }
	            }
		        
	            // Le da formato a la hoja
	            formatExcel(workbook, sheet, false, hasRelacions, false);
	        }


	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        workbook.write(out);
	        return out.toByteArray();
	    } catch (Exception ex) {
	        throw new RuntimeException("S'ha produït un error generant l'índex de l'expedient", ex);
	    }
	}

	private void formatExcel(Workbook workbook, Sheet sheet, boolean isRelacio, boolean hasRelacions, boolean isCarpeta) {
		
		if (! isCarpeta) {
	        // Combina las celdas título, subtítulo y descripción expedientes relacionados
	        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
	        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));
	        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 8));
	        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 2));
		}
		
        if (hasRelacions && ! isRelacio)
        	sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 8));
        
        // Itera sobre todas las filas con valores en la hoja de trabajo
        for (Row row : sheet) {
            float maxCellHeight = 0; // Ancho máximo de la columna
            
            if (row.getLastCellNum() > -1) {
                for (Cell cell : row) {
                    // Ajusta automáticamente el tamaño de la columna basado en el contenido de la celda
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                    
//                    cell.setCellStyle(cellStyle);
                    
                    // Obtiene el estilo de la celda
                    org.apache.poi.ss.usermodel.Font font = workbook.getFontAt(cell.getCellStyle().getFontIndex());
                    
                    // Estimación aproximada del alto del texto
                    float fontSize = font.getFontHeightInPoints();
                    float lineHeight = (fontSize / 72) * 96; // Convertir el tamaño de la fuente a puntos por pulgada (dpi)
                    int lines = cell.getStringCellValue().split("\n").length;
                    float estimatedTextHeight = lineHeight * lines + 2;
                    
                    if (estimatedTextHeight > maxCellHeight) {
                    	maxCellHeight = estimatedTextHeight;
                    }
                    
                    if ((isRelacio && row.getRowNum() == 8) 
                    		|| (!isRelacio && row.getRowNum() == 10 && hasRelacions) 
                    		|| (!isCarpeta && !isRelacio && row.getRowNum() == 8 && !hasRelacions)
                    		|| (isCarpeta && row.getRowNum() == 3)) {
                		CellStyle headerStyle = workbook.createCellStyle();
                		headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
                		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        headerStyle.setAlignment(HorizontalAlignment.LEFT);
                        headerStyle.setVerticalAlignment(VerticalAlignment.TOP);
                		cell.setCellStyle(headerStyle);
                		
                	    org.apache.poi.ss.usermodel.Font titleFont = sheet.getWorkbook().createFont();
                	    titleFont.setBold(true);
                	    titleFont.setFontName("Arial");
                	    titleFont.setFontHeightInPoints((short) 10);
                	    
                	    headerStyle.setFont(titleFont);
                    }
                }
            }
            
            // Establece la altura de fila estimada
            row.setHeightInPoints(maxCellHeight);
        }
	}
	private void crearTitol(
			Sheet sheet, 
			ContingutEntity contingut) {
	    Row row = sheet.createRow(sheet.getLastRowNum() + 1); //1
	    Cell cell = row.createCell(0);
	    cell.setCellValue(contingut.getNom());

	    // Título
	    CellStyle titleStyle = sheet.getWorkbook().createCellStyle();
	    org.apache.poi.ss.usermodel.Font titleFont = sheet.getWorkbook().createFont();
	    titleFont.setFontName("Arial");
	    titleFont.setBold(true);
	    titleFont.setFontHeightInPoints((short) 14);
	    titleStyle.setFont(titleFont);
	    titleStyle.setWrapText(true); 
	    row.setRowStyle(titleStyle);
	    
	    // El resto de filas
	    CellStyle style = sheet.getWorkbook().createCellStyle();
	    org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
	    font.setFontName("Arial");
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);
	    
	    if (contingut instanceof ExpedientEntity) {
	    	ExpedientEntity expedient = (ExpedientEntity)contingut;
	    	
		    // Número
		    row = sheet.createRow(sheet.getLastRowNum() + 1); //2
		    cell = row.createCell(0);
		    cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.expedient.numero") + (expedient.getNumero() != null ? expedient.getNumero() : expedientHelper.calcularNumero(expedient)));
		    row.setRowStyle(style);
		    
		    // Serie documental
		    row = sheet.createRow(sheet.getLastRowNum() + 1); //3
		    cell = row.createCell(0);
		    cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.expedient.serie") + expedient.getMetaExpedient().getSerieDocumental());
		    row.setRowStyle(style);
		    
		    // Clasificacion
		    row = sheet.createRow(sheet.getLastRowNum() + 1); //4
		    cell = row.createCell(0);
		    cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.expedient.classificacio") + expedient.getMetaExpedient().getClassificacio());
		    row.setRowStyle(style);
		    
		    // Fecha apertura
		    row = sheet.createRow(sheet.getLastRowNum() + 1); //5
		    cell = row.createCell(0);
		    cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.expedient.data") + sdtTime.format(expedient.getNtiFechaApertura()));
		    row.setRowStyle(style);
		    
		    // Estado
		    row = sheet.createRow(sheet.getLastRowNum() + 1); //6
		    cell = row.createCell(0);
		    cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.expedient.estat") + messageHelper.getMessage("expedient.service.exportacio.index.expedient.estat." + expedient.getEstat()));
		    row.setRowStyle(style);
	    
	    }
	    
//	    CellStyle subtitleStyle = sheet.getWorkbook().createCellStyle();
//	    org.apache.poi.ss.usermodel.Font subtitleFont = sheet.getWorkbook().createFont();
//	    subtitleFont.setBold(true);
//	    subtitleFont.setFontHeightInPoints((short) 9);
//	    subtitleStyle.setFont(subtitleFont);
//	    cell.setCellStyle(subtitleStyle);
	}

	private void crearTaulaDocuments(
			Sheet sheet, 
			ContingutEntity contingut, 
			EntitatEntity entitatActual, 
			Workbook workbook, 
			boolean isRelacio, 
			boolean hasRelacions,
			boolean isCarpeta) throws NoSuchFileException, IOException {
        // Crea un estilo de celda
        CellStyle cellDataStyle = workbook.createCellStyle();
        cellDataStyle.setAlignment(HorizontalAlignment.LEFT);
        cellDataStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellDataStyle.setWrapText(true); 
        
        
		// Crear la tabla y establecer los estilos
	    // Agregar las filas y celdas correspondientes a los documentos del expediente
		int rowTitleIdx = isCarpeta ? 3 : (isRelacio || !hasRelacions ? 8 : 10);
		int colIdx = 0;
		Row headerRow = sheet.createRow(rowTitleIdx);
		
	    headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.nom"));
//		if (isMostrarCampsAddicionals())
//			headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.nomnatural"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.descripcio"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.tipusdocument"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.fichero"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.ubicacio"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.tipusdocumental"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.origen"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.datacreacio"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.datadocument"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.firmat"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.datafirma"));
//		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.enllac"));
		headerRow.createCell(colIdx++).setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.link"));
        
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();
		List<DocumentEntity> fillsOrder1 = new ArrayList<DocumentEntity>();
		List<DocumentEntity> fillsOrder2 = new ArrayList<DocumentEntity>();
		
		if (contingut instanceof ExpedientEntity) {
			ExpedientEntity expedient = (ExpedientEntity)contingut;
			fillsOrder1 = documentRepository.findByExpedientAndEsborratAndOrdenat(
					expedient,
					0,
					contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
			
			fillsOrder2 = documentRepository.findByExpedientAndEsborratSenseOrdre(
					expedient,
					0,
					new Sort("createdDate"));
		} else {
			CarpetaEntity carpeta = (CarpetaEntity)contingut;
			fillsOrder1 = documentRepository.findByCarpetaAndEsborratAndOrdenat(
					carpeta,
					0,
					contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
			
			fillsOrder2 = documentRepository.findByCarpetaAndEsborratSenseOrdre(
					carpeta,
					0,
					new Sort("createdDate"));
		}
		
		
		documents.addAll(fillsOrder1);
		documents.addAll(fillsOrder2);
		
		int contentRowIdx = isCarpeta ? 4 : (isRelacio || !hasRelacions ? 9 : 11);
		
		if (! documents.isEmpty()) {
			List<List<DocumentEntity>> documentsBatches = Lists.partition(documents, BATCH_SIZE);
			
			for (List<DocumentEntity> documentsBatch : documentsBatches) {
				contentRowIdx = indexBatchHelper.processarBatchXLSX(
			    		documentsBatch, 
			    		sheet, 
			    		entitatActual, 
			    		workbook, 
			    		cellDataStyle, 
			    		contentRowIdx);
			}
		}
	}
	
	private String validarNombreHoja(String nombreHoja) {
	    // Lista de caracteres no válidos en el nombre de una hoja de cálculo en Excel
	    String caracteresNoValidos = "[\\\\/:*?\\[\\]]";
	    String nombreValido = nombreHoja.replaceAll(caracteresNoValidos, "_");
	    
	    // Limitar la longitud del nombre a 31 caracteres (límite de Excel)
	    if (nombreValido.length() > 31) {
	        nombreValido = nombreValido.substring(0, 31);
	    }
	    
	    return nombreValido;
	}
	
	private void crearTitolRelacio(Sheet sheet, List<ExpedientEntity> relacionatAmb, List<ExpedientEntity> relacionatPer) {
	    Row row = sheet.createRow(8);
	    Cell cell = row.createCell(0);
	    int totalRelacionats = relacionatAmb.size() + relacionatPer.size();
	    
	    if (totalRelacionats == 1)
	    	cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.relacio.excel"));
	    else if (totalRelacionats > 1) 
	    	cell.setCellValue(messageHelper.getMessage("expedient.service.exportacio.index.relacions.excel", new Object[] {totalRelacionats}));
	    
	    CellStyle titleStyle = sheet.getWorkbook().createCellStyle();
	    org.apache.poi.ss.usermodel.Font titleFont = sheet.getWorkbook().createFont();
	    titleFont.setItalic(true);
	    titleFont.setFontName("Arial");
	    titleFont.setFontHeightInPoints((short) 10);
	    titleStyle.setFont(titleFont);
	    cell.setCellStyle(titleStyle);
	}
	
	private InputStream getCapsaleraDefaultLogo() {
		return getClass().getResourceAsStream("/es/caib/ripea/core/templates/govern-logo.png");
	}
	
	private String getLogo() throws NoSuchFileException, IOException {
		return configHelper.getConfig("es.caib.ripea.index.logo");
	}
	
	private boolean indexExpedientsRelacionats() throws NoSuchFileException, IOException {
		return configHelper.getAsBoolean("es.caib.ripea.index.expedients.relacionats");
	}
	
	private boolean isMostrarCampsAddicionals() throws NoSuchFileException, IOException {
		return configHelper.getAsBoolean("es.caib.ripea.index.expedient.camps.addicionals");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(IndexHelper.class);

}
