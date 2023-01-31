package es.caib.ripea.core.helper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	//private Font frutiger5 = FontFactory.getFont("Frutiger", 5);
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
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;

	public byte[] generarIndexPerExpedient(
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

			Paragraph titolParagraph = new Paragraph();
			Chunk localDest = new Chunk(expedient.getNom(), frutiger11TitolBold);
			localDest.setLocalDestination("expedient_" + expedient.getId());
			titolParagraph.add(localDest);
			titolParagraph.setAlignment(Element.ALIGN_CENTER);
			String subtitol = expedient.getMetaExpedient().getNom() + " [" + expedient.getMetaExpedient().getClassificacioSia() + "] (" + expedient.getNumero() + ")";
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
//		List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
//			expedient, 
//			0, 
//			contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		List<ContingutEntity> continguts = new ArrayList<ContingutEntity>();
		List<ContingutEntity> fillsOrder1 = contingutRepository.findByPareAndEsborratAndOrdenat(
				expedient,
				0,
				contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		
		List<ContingutEntity> fillsOrder2 = contingutRepository.findByPareAndEsborratSenseOrdre(
				expedient,
				0,
				new Sort("createdDate"));
		
		continguts.addAll(fillsOrder1);
		continguts.addAll(fillsOrder2);
		BigDecimal num = new BigDecimal(0);
		BigDecimal sum = new BigDecimal(1);
		for (ContingutEntity contingut : continguts) {
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
		}
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
		List<ContingutEntity> fillsOrder1 = contingutRepository.findByPareAndEsborratAndOrdenat(
				carpetaActual,
				0,
				contingutHelper.isOrdenacioPermesa() ? new Sort("ordre") : new Sort("createdDate"));
		
		List<ContingutEntity> fillsOrder2 = contingutRepository.findByPareAndEsborratSenseOrdre(
				carpetaActual,
				0,
				new Sort("createdDate"));
		
		contingutsCarpetaActual.addAll(fillsOrder1);
		contingutsCarpetaActual.addAll(fillsOrder2);
		
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
					case ENVIADA:
						estatNotificacio = DocumentNotificacioEstatEnumCustom.ENVIAT;
						break;
					case FINALITZADA:
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
				Map<Integer, Date> datesFirmes = null;
				try {
					es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
							document,
							null,
							null,
							true,
							false);
					byte[] contingutArxiu = documentHelper.getContingutFromArxiuDocument(arxiuDocument);
					datesFirmes = getDataFirmaFromDocument(contingutArxiu);
				} catch (Exception ex) {
					logger.error("Hi ha hagut un error recuperant l'hora de firma del document", ex);
				}
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
	
	private Map<Integer, Date> getDataFirmaFromDocument(byte[] content) throws IOException {
		Map<Integer, Date> datesFirmes = new HashMap<Integer, Date>();
		PdfReader reader = new PdfReader(content);
		AcroFields fields = reader.getAcroFields();
		List<String> signatureNames = fields.getSignatureNames();
		Integer idx = 1;
		if (signatureNames != null) {
			for (String name: signatureNames) {
//				### comprovar si és una firma o un segell
				PdfDictionary dictionary = fields.getSignatureDictionary(name);
				if (dictionary != null && dictionary.get(PdfName.TYPE).toString().equals("/Sig")) {
					String dataFirmaStr = dictionary.get(PdfName.M) != null ? dictionary.get(PdfName.M).toString() : null;
					Date dataFirma = dataFirmaStr != null ? PdfDate.decode(dataFirmaStr).getTime() : null;
					datesFirmes.put(idx, dataFirma);
					idx++;
				}
			}
		}
		return datesFirmes;
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
	
	private InputStream getCapsaleraDefaultLogo() {
		return getClass().getResourceAsStream("/es/caib/ripea/core/templates/govern-logo.png");
	}
	
	private String getLogo() throws NoSuchFileException, IOException {
		return configHelper.getConfig("es.caib.ripea.index.logo");
	}
	
	private boolean indexExpedientsRelacionats() throws NoSuchFileException, IOException {
		return configHelper.getAsBoolean("es.caib.ripea.index.expedients.relacionats");
	}
	
	private String getCsvUrl() throws NoSuchFileException, IOException {
		return configHelper.getConfig("es.caib.ripea.documents.validacio.url");
	}
	
	private boolean isMostrarCampsAddicionals() throws NoSuchFileException, IOException {
		return configHelper.getAsBoolean("es.caib.ripea.index.expedient.camps.addicionals");
	}
	
	private enum DocumentNotificacioEstatEnumCustom {PENDENT, REGISTRAT, ENVIAT, NOTIFICAT};
	
	private static final Logger logger = LoggerFactory.getLogger(IndexHelper.class);

}
