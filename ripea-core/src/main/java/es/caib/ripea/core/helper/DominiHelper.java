/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.caib.ripea.core.api.dto.ResultatDominiDto;

/**
 * Helper per recuperar el resultat d'una consulta d'un domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiHelper {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ResultatDominiDto> findDominisByConsutla(String consulta) {
		return jdbcTemplate.query(consulta, new DominiRowMapperHelper());
	}

	public Properties getProperties(String cadena) {
		Properties conProp = new Properties();
		try {
			Document document = XmlHelper.getDocumentFromContent(cadena.getBytes());
			if (document != null) {
				for (int i = 0; i < document.getElementsByTagName("local-tx-datasource").getLength(); i++) {
					NodeList childList = document.getElementsByTagName("local-tx-datasource").item(i).getChildNodes();
					if (childList != null) {
						for (int j = 0; j < childList.getLength(); j++) {
							Node childNode = childList.item(j);
							switch (childNode.getNodeName()) {
							case "connection-url":
								conProp.setProperty("url", childList.item(j).getTextContent().trim());
								break;
							case "driver-class":
								conProp.setProperty("driver", childList.item(j).getTextContent().trim());
								break;
							case "user-name":
								conProp.setProperty("user", childList.item(j).getTextContent().trim());
								break;
							case "password":
								conProp.setProperty("password", childList.item(j).getTextContent().trim());
								break;
							default:
								break;
							}
						}
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conProp;
	}

}
