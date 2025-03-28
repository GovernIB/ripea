package es.caib.ripea.service.base.helper;

import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.ExportFileType;
import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generació d'informes amb Jasper Reports.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
public class JasperReportsHelper {

	private static final String EXPORT_REPORT_PATH = "/export_report.jrxml";

	public DownloadableFile export(
			Class<?> resourceClass,
			List<?> resultats,
			String[] fields,
			ExportFileType fileType,
			OutputStream out) throws ReportGenerationException {
		try {
			DynamicReports.report().columns();
			JasperReport report = getCompiledJasperReport(resourceClass);
			Map<String, Object> params = new HashMap<>();
			params.put("fields", fields);
			JRDataSource dataSource = new JRBeanCollectionDataSource(resultats);
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					report,
					params,
					dataSource);
			return generateDownloadableFile(
					resourceClass,
					jasperPrint,
					fileType,
					out);
		} catch (JRException | IOException ex) {
			throw new ReportGenerationException(
					resourceClass,
					"Couldn't generate export file",
					ex);
		}
	}

	private JasperReport getCompiledJasperReport(
			Class<?> resourceClass) throws JRException, IOException, ReportGenerationException {
		URL reportUrl = getClass().getResource(EXPORT_REPORT_PATH);
		if (reportUrl == null) {
			throw new ReportGenerationException(
					resourceClass,
					"Couldn't find report resource in classpath: " + EXPORT_REPORT_PATH);
		}
		URLConnection urlConnection = reportUrl.openConnection();
		return JasperCompileManager.compileReport(urlConnection.getInputStream());
	}

	private DownloadableFile generateDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			ExportFileType fileType,
			OutputStream out) throws JRException, ReportGenerationException {
		if (ExportFileType.CSV.equals(fileType)) {
			return generateCSVDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ExportFileType.ODS.equals(fileType)) {
			return generateOdsDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ExportFileType.ODT.equals(fileType)) {
			return generateOdtDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ExportFileType.XLSX.equals(fileType)) {
			return generateXlsxDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ExportFileType.DOCX.equals(fileType)) {
			return generateDocxDownloadableFile(resourceClass, jasperPrint, out);
		} else if (ExportFileType.PDF.equals(fileType)) {
			return generatePdfDownloadableFile(resourceClass, jasperPrint, out);
		} else {
			throw new ReportGenerationException(
					resourceClass,
					"Unsupported export file type: " + fileType);
		}
	}

	private DownloadableFile generateCSVDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRCsvExporter exporter = new JRCsvExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
		SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
		configuration.setWriteBOM(Boolean.TRUE);
		configuration.setRecordDelimiter("\r\n");
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.csv",
				"text/csv",
				null);
	}

	private DownloadableFile generateOdsDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JROdsExporter exporter = new JROdsExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleOdsExporterConfiguration configuration = new SimpleOdsExporterConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.ods",
				"application/vnd.oasis.opendocument.spreadsheet",
				null);
	}

	private DownloadableFile generateOdtDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JROdtExporter exporter = new JROdtExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleOdtReportConfiguration configuration = new SimpleOdtReportConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.odt",
				"application/vnd.oasis.opendocument.text",
				null);
	}

	private DownloadableFile generateXlsxDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				null);
	}

	private DownloadableFile generateDocxDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRDocxExporter exporter = new JRDocxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimpleDocxReportConfiguration configuration = new SimpleDocxReportConfiguration();
		exporter.setConfiguration(configuration);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				null);
	}

	private DownloadableFile generatePdfDownloadableFile(
			Class<?> resourceClass,
			JasperPrint jasperPrint,
			OutputStream out) throws JRException {
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);
		exporter.setConfiguration(reportConfig);
		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(exportConfig);
		exporter.exportReport();
		return new DownloadableFile(
				resourceClass.getSimpleName() + "_export.pdf",
				"application/pdf",
				null);
	}

}
