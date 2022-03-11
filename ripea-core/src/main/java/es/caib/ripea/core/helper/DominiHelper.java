/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.exception.CipherException;
import es.caib.ripea.core.api.exception.DominiException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.DominiEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.repository.DominiRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;
import es.caib.ripea.core.service.DominiServiceImpl;

/**
 * Helper per recuperar el resultat d'una consulta d'un domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DominiHelper {
	
	
	@Autowired
	private DominiRepository dominiRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	
	
	public DominiDto create(
			Long entitatId,
			DominiDto domini, 
			boolean xifrarContrasenya) throws NotFoundException {
		logger.debug("Creant un nou domini per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);
		DominiEntity entity = DominiEntity.getBuilder(
				domini.getCodi(),
				domini.getNom(),
				domini.getDescripcio(),
				domini.getConsulta(),
				domini.getCadena(),
				xifrarContrasenya ? xifrarContrasenya(domini.getContrasenya()) : domini.getContrasenya(),
				entitat).build();
		DominiDto dominiDto = conversioTipusHelper.convertir(
				dominiRepository.save(entity),
				DominiDto.class);
		return dominiDto;
	}
	
	

	public JdbcTemplate setDataSource(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	public Properties getProperties(DominiDto domini) {
		Properties conProps = new Properties();
		try {
			String cadena = domini.getCadena();
			String password = domini.getContrasenya();
			Document document = XmlHelper.getDocumentFromContent(cadena.getBytes());
			if (document != null) {
				for (int i = 0; i < document.getElementsByTagName("local-tx-datasource").getLength(); i++) {
					NodeList childList = document.getElementsByTagName("local-tx-datasource").item(i).getChildNodes();
					if (childList != null) {
						for (int j = 0; j < childList.getLength(); j++) {
							Node childNode = childList.item(j);
							switch (childNode.getNodeName()) {
							case "connection-url":
								conProps.setProperty("url", childList.item(j).getTextContent().trim());
								break;
							case "driver-class":
								conProps.setProperty("driver", childList.item(j).getTextContent().trim());
								break;
							case "user-name":
								conProps.setProperty("user", childList.item(j).getTextContent().trim());
								break;
							default:
								break;
							}
						}
					}
				}
			}
			if (password != null && !password.isEmpty()) {
				password = desxifrarContrasenya(password);
				conProps.setProperty("password", password);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ValidationException(e.getMessage());
		}
		return conProps;
	}
	
	public String xifrarContrasenya(String contrasenya) {
		byte[] xifrat = null;
		if (contrasenya != null) {
			byte[] bytes = contrasenya.getBytes();
			Cipher cipher;
			try {
				cipher = Cipher.getInstance("RC4");
				SecretKeySpec rc4Key = new SecretKeySpec("dom1n1".getBytes(),"RC4");
				cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
				xifrat = cipher.doFinal(bytes);
			} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
				throw new CipherException(
						"Hi ha hagut un error xifrant la contrasenya del domini", 
						e.getCause());
			} 
		}
		return new String(Base64.encodeBase64(xifrat));
	}
	
	public String desxifrarContrasenya(String contrasenya) {	
		byte[] desxifrat = null;
		try {
			Cipher cipher = Cipher.getInstance("RC4");
			SecretKeySpec rc4Key = new SecretKeySpec("dom1n1".getBytes(),"RC4");
			cipher.init(Cipher.DECRYPT_MODE, rc4Key);
			desxifrat = cipher.doFinal(Base64.decodeBase64(contrasenya.getBytes()));
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
			throw new CipherException(
					"Hi ha hagut un error desxifrant la contrasenya del domini", 
					e.getCause());
		} 
		return new String(desxifrat);
	}
	
	public DataSource createDominiConnexio(
			String entitatCodi,
			Properties conProps) {
		DataSource dataSource = null;
		try {
			dataSource = new DriverManagerDataSource(
					conProps.getProperty("url"),
					conProps);
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut crear el datasource " + e.getMessage(),
					e.getCause());
		}
		return dataSource;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(DominiHelper.class);
	
}
