package es.caib.ripea.service.helper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlHelper {

	/**
	 * Retorna el valor de text del primer element que coincideix amb la ruta
	 * indicada (separada per punts). Exemple: "root.child" cerca el primer
	 * &lt;child&gt; dins el primer &lt;root&gt;.
	 *
	 * @param content contingut XML
	 * @param tagPath ruta de tags separada per punts
	 * @return text del node o null si no s'ha trobat
	 */
	public static String getNodeValue(byte[] content, String tagPath) throws XMLStreamException {
		String[] path = tagPath.split("\\.");
		XMLStreamReader reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(new ByteArrayInputStream(content));
		try {
			Deque<String> stack = new ArrayDeque<>();
			int depth = 0;
			while (reader.hasNext()) {
				int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					stack.push(reader.getLocalName());
					if (matchesPath(stack, path)) {
						depth = stack.size();
					}
				} else if (event == XMLStreamConstants.CHARACTERS && stack.size() == depth && depth > 0) {
					String text = reader.getText().trim();
					if (!text.isEmpty()) {
						return text;
					}
				} else if (event == XMLStreamConstants.END_ELEMENT) {
					if (stack.size() == depth) {
						depth = 0;
					}
					if (!stack.isEmpty()) {
						stack.pop();
					}
				}
			}
		} finally {
			reader.close();
		}
		return null;
	}

	/**
	 * Retorna una llista de mapes amb els valors dels fills directes de cada
	 * element que coincideixi amb parentTag.
	 * Cada mapa conté: nom del tag fill → valor de text.
	 *
	 * @param content   contingut XML
	 * @param parentTag nom del tag pare
	 * @return llista de mapes fill→valor per a cada ocurrència del pare
	 */
	public static List<Map<String, String>> getChildValueMaps(byte[] content, String parentTag)
			throws XMLStreamException {
		List<Map<String, String>> result = new ArrayList<>();
		XMLStreamReader reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(new ByteArrayInputStream(content));
		try {
			Map<String, String> current = null;
			String currentChild = null;
			while (reader.hasNext()) {
				int event = reader.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					String name = reader.getLocalName();
					if (parentTag.equals(name)) {
						current = new LinkedHashMap<>();
					} else if (current != null) {
						currentChild = name;
					}
				} else if (event == XMLStreamConstants.CHARACTERS && current != null && currentChild != null) {
					String text = reader.getText().trim();
					if (!text.isEmpty()) {
						current.put(currentChild, text);
					}
				} else if (event == XMLStreamConstants.END_ELEMENT) {
					String name = reader.getLocalName();
					if (parentTag.equals(name) && current != null) {
						result.add(current);
						current = null;
					}
					currentChild = null;
				}
			}
		} finally {
			reader.close();
		}
		return result;
	}

	private static boolean matchesPath(Deque<String> stack, String[] path) {
		if (stack.size() != path.length) {
			return false;
		}
		int i = path.length - 1;
		for (String element : stack) {
			if (!element.equals(path[i--])) {
				return false;
			}
		}
		return true;
	}
}
