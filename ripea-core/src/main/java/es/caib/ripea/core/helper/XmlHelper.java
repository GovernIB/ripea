package es.caib.ripea.core.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utilitat per tractar documents XML i el seu contingut.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class XmlHelper {

	/** Obté tots els nodes en la ruta assenyalada dins el node passat com a paràmetre
	 * 
	 * @param node
	 * 			Node on cercar l'element assenyalat.
	 * @param name
	 * 			Nom o ruta separada per punts per recuperar la llista de nodes.
	 * @return
	 * 			Retorna la llista de nodes o null si no es correspon amb el nom.
	 */
	public static NodeList getNodes(Node node, String name) {
		NodeList ret = null;
		if (node != null && name != null) {
			if(node.getNodeType() == Node.ELEMENT_NODE){
	            Element element = (Element) node;
				String[] names = name.split("\\.");
	    		NodeList list = element.getElementsByTagName(names[0]);
	    		if (names.length > 1) {
    				// Recursiu fins al darrer node
	    			node = list.item(0);
	    			ret = getNodes(node, name.replace(names[0] + ".", ""));
	    		} else {
	    			ret = list;
	    		}
	        }
		}		
		return ret;
	}
	
	/** Obté el node fill amb el nom o la ruta passada com a paràmetre
	 * 
	 * @param node
	 * 			Node arrel on cercar el node fill.
	 * @param name
	 * 			Nom del node fill o ruta separada per punt "." fins al node a cercar.
	 * @return
	 * 			Retorna el node de tipus element amb el nom especificat o null si no s'ha trobat.
	 */
	public static Node getNode(Node node, String name) {
		Node ret = null;
		NodeList nodes = getNodes(node, name);
		if (nodes != null && nodes.getLength() > 0)
			ret = nodes.item(0);
		return ret;
	}
	
	/** Retorna el valor del node asssenyalat dins del node passat per paràmetre.
	 * 
	 * @param node
	 * 			Node on cercar el node especificat pel nom.
	 * @param name
	 * 			Nom del node fill o ruta separada per punts "." fins al node fill.
	 * @return
	 * 			Valor String contingut en el node assenyalat. Retonra null si no té valor o no s'ha trobat el node fill.
	 */
	public static String getNodeValue(Node node, String name) {
		String ret = null;
		node = getNode(node, name);
		if(node != null){
            ret = node.getFirstChild().getNodeValue();
        }
		return ret;
	}

	/** Obté l'objecte document a partir de l'XML d'entrada.
	 * 
	 * @param xml
	 * 			Contingut XML per convertir en un document.
	 * @return
	 * @throws ParserConfigurationException Si no es pot obtenir el Document
	 * @throws IOException Si no es pot interpretar bé l'xml.
	 * @throws SAXException Si l'xml no és correcte.
	 */
	public static Document getDocumentFromContent(byte[] content) 
			throws ParserConfigurationException, 
			SAXException, 
			IOException {		
		final InputStream stream = new ByteArrayInputStream(content);
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(stream);		
		return doc;
	}

}
