package es.caib.ripea.war.historic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.velocity.tools.generic.DateTool;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

public class ExportacioDocHistoric {

	
	public byte[] convertDadesEntitat(EntitatDto entitat, List<HistoricExpedientDto> dades) throws IOException, XDocReportException {
		// 1) Load ODT file and set Velocity template engine and cache it to the registry
		InputStream in = this.getClass().getResourceAsStream("/es/caib/ripea/war/templates/template_historic_entitat_ca.odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

    	// https://github.com/opensagres/xdocreport/wiki/DocxReportingJavaMainListFieldInTable
		// 2) Create fields metadata to manage lazy loop (#forech velocity) for table row.
		FieldsMetadata metadata = new FieldsMetadata();
		metadata.addFieldAsList("dades.data");
		metadata.addFieldAsList("dades.numExpedientsCreats");
		metadata.addFieldAsList("dades.numExpedientsCreatsTotal");
		metadata.addFieldAsList("dades.numExpedientsTancats");
		metadata.addFieldAsList("dades.numExpedientsTancatsTotal");
		metadata.addFieldAsList("dades.numDocsSignats");
		metadata.addFieldAsList("dades.numDocsNotificats");
		report.setFieldsMetadata(metadata);
		
    	// 3) Create Java model context 
    	IContext context = report.createContext();
    	context.put("entitat", entitat);
    	context.put("dades", dades);
    	context.put("dateFormatter", new DateTool());

    	// 3) Set PDF as format converter
    	Options options = Options.getTo(ConverterTypeTo.PDF);

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.process(context, bos);

    	return bos.toByteArray();
	}
	
	public byte[] convertDadesOrgansGestors(Map<OrganGestorDto, List<HistoricExpedientDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the registry
		InputStream in = this.getClass().getResourceAsStream("/es/caib/ripea/war/templates/template_historic_organ_ca.odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

    	// 2) Create Java model context 
    	IContext context = report.createContext();
    	context.put("dades", dades);
    	context.put("dateFormatter", new DateTool());

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.process(context, bos);

    	return bos.toByteArray();
	}

	public byte[] convertDadesUsuaris(Map<String, List<HistoricUsuariDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the registry
		InputStream in = this.getClass().getResourceAsStream("/es/caib/ripea/war/templates/template_historic_usuaris_ca.odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

    	// 2) Create Java model context 
    	IContext context = report.createContext();
    	context.put("dades", dades);
    	context.put("dateFormatter", new DateTool());

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.process(context, bos);

    	return bos.toByteArray();
	}
	
	public byte[] convertDadesInteressats(Map<String, List<HistoricInteressatDto>> dades) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the registry
		InputStream in = this.getClass().getResourceAsStream("/es/caib/ripea/war/templates/template_historic_interessats_ca.odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

    	// 2) Create Java model context 
    	IContext context = report.createContext();
    	context.put("dades", dades);
    	context.put("dateFormatter", new DateTool());

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.process(context, bos);
    	
    	return bos.toByteArray();
	}
	
}
