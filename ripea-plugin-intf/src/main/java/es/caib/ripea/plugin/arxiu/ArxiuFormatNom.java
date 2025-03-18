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
public enum ArxiuFormatNom {

	GML 			("GML"),
	WFS				("WFS"),
	WMS 			("WMS"),
	GZIP 			("GZIP"),
	ZIP 			("ZIP"),
	AVI 			("AVI"),
	MP4A			("MPEG-4 MP4 media"),
	CSV 			("Comma Separated Values"),
	HTML 			("HTML"),
	CSS 			("CSS"),
	JPEG 			("JPEG"),
	MHTML 			("MHTML"),
	OASIS12			("ISO/IEC 26300:2006 OASIS 1.2"),
	SOXML 			("Strict Open XML"),
	PDF 			("PDF"),
	PDFA 			("PDF/A"),
	PNG 			("PNG"),
	RTF 			("RTF"),
	SVG 			("SVG"),
	TIFF 			("TIFF"),
	TXT 			("TXT"),
	XHTML 			("XHTML"),
	MP3 			("MP3. MPEG-1 Audio Layer 3"),
	OGG 			("OGG-Vorbis"),
	MP4V 			("MPEG-4 MP4 vídeo"),
	WEBM 			("WebM");

	private final String valor;
	private ArxiuFormatNom(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, ArxiuFormatNom> lookup;
	static {
		lookup = new HashMap<String, ArxiuFormatNom>();
		for (ArxiuFormatNom s: EnumSet.allOf(ArxiuFormatNom.class))
			lookup.put(s.getValor(), s);
	}
	public static ArxiuFormatNom valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
