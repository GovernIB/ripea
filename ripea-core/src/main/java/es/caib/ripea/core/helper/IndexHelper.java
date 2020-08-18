package es.caib.ripea.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.repository.ContingutRepository;

/**
 * Mètodes per generar un índex d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Component
public class IndexHelper {

	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ContingutService contingutService;
	
	public byte[] generarIndexPerExpedient(
			ExpedientEntity expedient, 
			EntitatEntity entitatActual) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			XWPFDocument xwpfDocument = configurarDocument(expedient);
			configurarTaulaDocument(
					xwpfDocument,
					expedient,
					entitatActual);
			xwpfDocument.write(out);
			out.close();
		} catch (Exception ex) {
			throw new RuntimeException(
					"S'ha produït un error generant l'índex de l'expedient",
					ex);
		}

		return out.toByteArray();
	}

	private XWPFDocument configurarDocument(
			ExpedientEntity expedient) throws NoSuchFileException, FileNotFoundException, IOException, InvalidFormatException {
		//plantilla base
		XWPFDocument xwpfDocument = new XWPFDocument(getClass().getResourceAsStream("/es/caib/ripea/core/templates/template_index.docx"));
		
		// Logo
		XWPFParagraph headerLogo = xwpfDocument.createParagraph();
		headerLogo.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun regio_header_logo = headerLogo.createRun();

		if (getLogo() != null) {
			FileInputStream is = new FileInputStream(getLogo());
			regio_header_logo.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, getLogo(), Units.toEMU(400), Units.toEMU(100));
			regio_header_logo.addBreak();
			regio_header_logo.addBreak();
		}
		// Títol
		XWPFParagraph headerParagraph = xwpfDocument.createParagraph();
		headerParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun regio_header_text = headerParagraph.createRun();

		regio_header_text.setText(expedient.getNom());
		regio_header_text.addCarriageReturn();
		regio_header_text.setText(expedient.getCreatedBy().getNom());
		regio_header_text.setBold(true);
		regio_header_text.addBreak();
		regio_header_text.addBreak();
		return xwpfDocument;
	}

	private XWPFTable configurarTaulaDocument(
			XWPFDocument xwpfDocument,
			ExpedientEntity expedient,
			EntitatEntity entitatActual) throws Exception {
		XWPFTable taula = xwpfDocument.createTable();
		
		alinearTaula(
				taula,
				ParagraphAlignment.CENTER);
		configurarCapsaleraTaula(taula);
		configurarContingutTaula(
				taula,
				expedient,
				entitatActual);
		
		return taula;
	}
	
	private void configurarCapsaleraTaula(XWPFTable taula) {
		XWPFTableRow headerTaula = taula.getRow(0);
		// La primera cel·la ve creada
		XWPFParagraph parafraph_nom= headerTaula.getCell(0).getParagraphs().get(0);
		parafraph_nom.setAlignment(ParagraphAlignment.CENTER);
		parafraph_nom.setVerticalAlignment(TextAlignment.TOP);
		XWPFRun regio_num = parafraph_nom.createRun();
		regio_num.setText(messageHelper.getMessage("expedient.service.exportacio.index.numero"));
		regio_num.setBold(true);
		regio_num.setFontSize(8);

		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.nom"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.estat"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.tipusdocument"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.creatper"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.datacreacio"));
//		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.datafirma"));
//		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.dataestat"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.csv"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.link"));
		addNewCellToHeader(headerTaula, messageHelper.getMessage("expedient.service.exportacio.index.datadocument"));

		// background header
		for (XWPFTableCell capsalera_col : headerTaula.getTableCells()) {
			CTTcPr tcpr = capsalera_col.getCTTc().addNewTcPr();
			CTShd ctshd = tcpr.addNewShd();
			ctshd.setFill("969696");
		}
	}

	private void configurarContingutTaula(
			XWPFTable taula, 
			ExpedientEntity expedient,
			EntitatEntity entitatActual) throws Exception {
		List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
				expedient, 
				0, 
				new Sort("createdDate"));
		long num = 0;
		
		for (ContingutEntity contingut : continguts) {
			if (contingut instanceof DocumentEntity) {
				XWPFTableRow bodyTaula = taula.createRow();
				num += 10;
				addAndAdjustFila(
						taula,
						bodyTaula,
						(DocumentEntity) contingut,
						entitatActual,
						num);
			}
			if (contingut instanceof CarpetaEntity) {
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
						XWPFTableRow bodyTaula = taula.createRow();
						num += (document.getNom() == documentsCarpeta.get(0).getNom()) ? 10 : 1; // primer document
						addAndAdjustFila(
								taula,
								bodyTaula,
								document,
								entitatActual,
								num);
					}
					documentsCarpeta = new ArrayList<DocumentEntity>();
					if (darreraCarpeta)
						break;
				}
			}
		}
		ajustarLongitudFiles(taula);
	}

	private void addAndAdjustFila(
			XWPFTable taula,
			XWPFTableRow bodyTaula,
			DocumentEntity document,
			EntitatEntity entitatActual,
			long num) throws Exception {

		addNewCellToBody(bodyTaula.getCell(0), String.valueOf(((num) / 10.0)), false);
		SimpleDateFormat sdt = new SimpleDateFormat("dd-MM-yyyy");

		addNewCellToBody(bodyTaula.getCell(1), document.getNom() != null ? document.getNom() : "", true);
		addNewCellToBody(bodyTaula.getCell(2), document.getEstat() != null ? messageHelper.getMessage("document.estat.enum." + document.getEstat()): "", false);
		addNewCellToBody(bodyTaula.getCell(3), document.getDocumentTipus() != null ? messageHelper.getMessage("document.tipus.enum." + document.getDocumentTipus()) : "", false);
		addNewCellToBody(bodyTaula.getCell(4), document.getCreatedBy() != null ? document.getCreatedBy().getCodi() : "", false);
		addNewCellToBody(bodyTaula.getCell(5), document.getCreatedDate() != null ? sdt.format(document.getCreatedDate().toDate()) : "", false);
//		addNewCellToBody(bodyTaula.getCell(6), "firma data");
//		addNewCellToBody(bodyTaula.getCell(7), "estat data");
		ArxiuDetallDto arxiuDetall = contingutService.getArxiuDetall(
				entitatActual.getId(),
				document.getId());
		if (arxiuDetall != null && arxiuDetall.getMetadadesAddicionals() != null) {
			addNewCellToBody(bodyTaula.getCell(6), arxiuDetall.getMetadadesAddicionals().get("csv") != null ? arxiuDetall.getMetadadesAddicionals().get("csv").toString() : "", false);
			addNewCellToBody(bodyTaula.getCell(7), arxiuDetall.getMetadadesAddicionals().get("csv") != null ? getCsvUrl() + arxiuDetall.getMetadadesAddicionals().get("csv") : "", true);
		}
		addNewCellToBody(bodyTaula.getCell(8), document.getDataCaptura() != null ? sdt.format(document.getDataCaptura()) : "", false);
	}
	
	private void ajustarLongitudFiles(XWPFTable table) {
		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(500));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(800));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1500));
//		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
//		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(1000));
	}

	private void addNewCellToHeader(
			XWPFTableRow row,
			String titol) {
		XWPFParagraph parafraph = row.addNewTableCell().getParagraphs().get(0);
		parafraph.setAlignment(ParagraphAlignment.CENTER);
		parafraph.setVerticalAlignment(TextAlignment.TOP);
		XWPFRun regio = parafraph.createRun();
		regio.setText(titol);
		regio.setBold(true);
		regio.setFontSize(8);
	}

	private void addNewCellToBody(
			XWPFTableCell cell,
			String titol,
			boolean isEnllac) throws Exception {
		XWPFParagraph parafraph= cell.getParagraphs().get(0);
		parafraph.setAlignment(ParagraphAlignment.CENTER);
		parafraph.setVerticalAlignment(TextAlignment.TOP);
		XWPFRun regio = parafraph.createRun();
		regio.setText(titol);
		regio.setFontSize(7);
//		if (isEnllac)
//			crearEnllacPerParagraf(cell.getParagraphs().get(0), regio_nom.getParagraph().getText());
	}

	private void alinearTaula(
			XWPFTable table,
			ParagraphAlignment align) {
		CTTblPr tblPr = table.getCTTbl().getTblPr();
		CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
		STJc.Enum en = STJc.Enum.forInt(align.getValue());
		jc.setVal(en);
	}
	
	@SuppressWarnings("unused")
	private XWPFHyperlinkRun crearEnllacPerParagraf(
			XWPFParagraph paragraph, 
			String uri) throws Exception {
		String rId = paragraph.getPart().getPackagePart().addExternalRelationship(
				uri, 
				XWPFRelation.HYPERLINK.getRelation()).getId();

		CTHyperlink cthyperLink = paragraph.getCTP().addNewHyperlink();
		cthyperLink.setId(rId);
		cthyperLink.addNewR();

		return new XWPFHyperlinkRun(
				cthyperLink, 
				cthyperLink.getRArray(0),
				paragraph);
	}
	
	private String getLogo() throws NoSuchFileException, IOException {
		String filePath = PropertiesHelper.getProperties().getProperty("es.caib.ripea.index.logo");
		return filePath;
	}
	
	private String getCsvUrl() throws NoSuchFileException, IOException {
		String filePath = PropertiesHelper.getProperties().getProperty("es.caib.ripea.documents.validacio.url");
		return filePath;
	}

}
