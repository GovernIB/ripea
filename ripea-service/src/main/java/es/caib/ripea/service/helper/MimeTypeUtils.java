package es.caib.ripea.service.helper;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class MimeTypeUtils {

    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
            // Microsoft Word
            Map.entry("doc", "application/msword"),
            Map.entry("dot", "application/msword"),
            Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            Map.entry("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template"),
            Map.entry("docm", "application/vnd.ms-word.document.macroEnabled.12"),
            Map.entry("dotm", "application/vnd.ms-word.template.macroEnabled.12"),

            // Microsoft Excel
            Map.entry("xls", "application/vnd.ms-excel"),
            Map.entry("xlt", "application/vnd.ms-excel"),
            Map.entry("xla", "application/vnd.ms-excel"),
            Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            Map.entry("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template"),
            Map.entry("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12"),
            Map.entry("xltm", "application/vnd.ms-excel.template.macroEnabled.12"),
            Map.entry("xlam", "application/vnd.ms-excel.addin.macroEnabled.12"),
            Map.entry("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12"),

            // Microsoft PowerPoint
            Map.entry("ppt", "application/vnd.ms-powerpoint"),
            Map.entry("pot", "application/vnd.ms-powerpoint"),
            Map.entry("pps", "application/vnd.ms-powerpoint"),
            Map.entry("ppa", "application/vnd.ms-powerpoint"),
            Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            Map.entry("potx", "application/vnd.openxmlformats-officedocument.presentationml.template"),
            Map.entry("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow"),
            Map.entry("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12"),
            Map.entry("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12"),
            Map.entry("potm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12"),
            Map.entry("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"),

            // Open Document Format (ODF / OpenOffice / LibreOffice)
            Map.entry("odt", "application/vnd.oasis.opendocument.text"),
            Map.entry("ott", "application/vnd.oasis.opendocument.text-template"),
            Map.entry("oth", "application/vnd.oasis.opendocument.text-web"),
            Map.entry("odm", "application/vnd.oasis.opendocument.text-master"),
            Map.entry("odg", "application/vnd.oasis.opendocument.graphics"),
            Map.entry("otg", "application/vnd.oasis.opendocument.graphics-template"),
            Map.entry("odp", "application/vnd.oasis.opendocument.presentation"),
            Map.entry("otp", "application/vnd.oasis.opendocument.presentation-template"),
            Map.entry("ods", "application/vnd.oasis.opendocument.spreadsheet"),
            Map.entry("ots", "application/vnd.oasis.opendocument.spreadsheet-template"),
            Map.entry("odc", "application/vnd.oasis.opendocument.chart"),
            Map.entry("odf", "application/vnd.oasis.opendocument.formula"),
            Map.entry("odb", "application/vnd.oasis.opendocument.database"),
            Map.entry("odi", "application/vnd.oasis.opendocument.image"),
            Map.entry("oxt", "application/vnd.openofficeorg.extension"),
            
            // PDF
            Map.entry("pdf", "application/pdf")
        );
    
	public static String getMimeType(String fileName) {
		String ext = FilenameUtils.getExtension(fileName).toLowerCase();
		return MIME_TYPES.getOrDefault(ext, "application/octet-stream");
	}
}
