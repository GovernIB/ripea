/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles orígens de documents i expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ArxiuFormatExtensio {

	GML 			(".gml"),
	GZ 				(".gz"),
	ZIP 			(".zip"),
	AVI 			(".avi"),
	CSV 			(".csv"),
	HTML 			(".html"),
	HTM 			(".htm"),
	CSS 			(".css"),
	JPG 			(".jpg"),
	JPEG 			(".jpeg"),
	MHTML 			(".mhtml"),
	MHT 			(".mht"),
	ODT 			(".odt"),
	ODS 			(".ods"),
	ODP 			(".odp"),
	ODG 			(".odg"),
	DOCX 			(".docx"),
	XLSX 			(".xlsx"),
	PPTX 			(".pptx"),
	PDF 			(".pdf"),
	PNG 			(".png"),
	RTF 			(".rtf"),
	SVG 			(".svg"),
	TIFF 			(".tiff"),
	TXT 			(".txt"),
	MP3 			(".mp3"),
	OGG 			(".ogg"),
	OGA 			(".oga"),
	MPEG 			(".mpeg"),
	MP4 			(".mp4"),
	WEBM 			(".webm");

	private final String valor;
	private ArxiuFormatExtensio(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, ArxiuFormatExtensio> lookup;
	static {
		lookup = new HashMap<String, ArxiuFormatExtensio>();
		for (ArxiuFormatExtensio s: EnumSet.allOf(ArxiuFormatExtensio.class))
			lookup.put(s.getValor(), s);
	}
	public static ArxiuFormatExtensio valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
