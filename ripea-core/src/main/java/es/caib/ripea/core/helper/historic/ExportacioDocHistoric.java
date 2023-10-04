package es.caib.ripea.core.helper.historic;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.ITemplateEngine;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.opensagres.xdocreport.template.velocity.internal.VelocityTemplateEngine;
import org.apache.velocity.tools.generic.DateTool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ExportacioDocHistoric {


	public byte[] convertDadesEntitat(
			EntitatDto entitat,
			List<HistoricExpedientDto> dades,
			HistoricTipusEnumDto tipusAgrupament) throws IOException, XDocReportException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/ripea/war/templates/template_historic_entitat_ca.odt");
		
		// https://github.com/opensagres/xdocreport/wiki/DocxReportingJavaMainListFieldInTable
		// 2) Create fields metadata to manage lazy loop (#forech velocity) for table
		// row.
		FieldsMetadata metadata = new FieldsMetadata();
		metadata.setTemplateEngineKind("Velocity");
		metadata.addFieldAsList("dades.data");
		metadata.addFieldAsList("dades.any");
		metadata.addFieldAsList("dades.mesNom");
		metadata.addFieldAsList("dades.numExpedientsCreats");
		metadata.addFieldAsList("dades.numExpedientsCreatsTotal");
		metadata.addFieldAsList("dades.numExpedientsTancats");
		metadata.addFieldAsList("dades.numExpedientsTancatsTotal");
		metadata.addFieldAsList("dades.numDocsSignats");
		metadata.addFieldAsList("dades.numDocsNotificats");
		report.setFieldsMetadata(metadata);

		// 3) Create Java model context
		IContext context = report.createContext();
		context.put("tipusAgrupament", tipusAgrupament);
		context.put("entitat", entitat);
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

		// 3) Set PDF as format converter
//		Options options = Options.getTo(ConverterTypeTo.PDF);

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}

	public byte[] convertDadesOrgansGestors(
			Map<OrganGestorDto, List<HistoricExpedientDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/ripea/war/templates/template_historic_organ_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());
		context.put("tipusAgrupament", tipusAgrupament);

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}

	public byte[] convertDadesUsuaris(
			Map<String, List<HistoricUsuariDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/ripea/war/templates/template_historic_usuaris_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());
		context.put("tipusAgrupament", tipusAgrupament);

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}

	public byte[] convertDadesInteressats(
			Map<String, List<HistoricInteressatDto>> dades, HistoricTipusEnumDto tipusAgrupament) throws XDocReportException, IOException {
		// 1) Load ODT file and set Velocity template engine and cache it to the
		// registry
		IXDocReport report = getReportInstance("/es/caib/ripea/war/templates/template_historic_interessats_ca.odt");

		// 2) Create Java model context
		IContext context = report.createContext();
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());
		context.put("tipusAgrupament", tipusAgrupament);

		// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		report.process(context, bos);

		return bos.toByteArray();
	}

	private IXDocReport getReportInstance(String filename) throws IOException, XDocReportException {
		InputStream in = this.getClass().getResourceAsStream(filename);
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
	
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		properties.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
		
		ITemplateEngine templateEngine = new VelocityTemplateEngine(properties);
				
		report.setTemplateEngine(templateEngine);
		return report;
	}
    
}
