package es.caib.ripea.core.firma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.util.Base64;

import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.PluginHelper;
import lombok.Getter;
import lombok.Setter;

@Component
public class DocumentFirmaAppletHelper extends DocumentFirmaHelper {

	public static final String CLAU_SECRETA = "R1p3AR1p3AR1p3AR";

	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;

	public void processarFirmaClient(
			String identificador,
			String arxiuNom,
			byte[] arxiuContingut,
			DocumentEntity document) {
		logger.debug("Custodiar identificador firma applet (" + "identificador=" + identificador + ")");

		// Registra al log la firma del document
		logAll(document, LogTipusEnumDto.FIRMA_CLIENT, null, null);

		// Custodia el document firmat
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(arxiuNom);
		fitxer.setContingut(arxiuContingut);
		fitxer.setContentType("application/pdf");
		document.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
		String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarFirmaPades(document, fitxer);
		document.updateInformacioCustodia(new Date(), custodiaDocumentId, document.getCustodiaCsv());
		documentHelper.actualitzarVersionsDocument(document);

		// Registra al log la custòdia de la firma del document
		logAll(document, LogTipusEnumDto.ARXIU_CUSTODIAT, custodiaDocumentId, null);
	}

	public SecretKeySpec buildKey(String message) throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] key = sha.digest(message.getBytes());
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, "AES");
	}

	public ObjecteFirmaApplet firmaAppletDesxifrar(String missatge, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, buildKey(key));
		ByteArrayInputStream bais = new ByteArrayInputStream(cipher.doFinal(Base64.decode(missatge.getBytes())));
		ObjectInputStream is = new ObjectInputStream(bais);
		Long[] array = (Long[])is.readObject();
		ObjecteFirmaApplet objecte = obtainInstanceObjecteFirmaApplet(array[0], array[1], array[2]);
		is.close();
		return objecte;
	}

	public String firmaClientXifrar(ObjecteFirmaApplet objecte) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		Long[] array = new Long[] { objecte.getSysdate(), objecte.getEntitatId(), objecte.getDocumentId() };
		os.writeObject(array);
		os.close();
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, buildKey(CLAU_SECRETA));
		byte[] xifrat = cipher.doFinal(baos.toByteArray());
		return new String(Base64.encode(xifrat));
	}

	public ObjecteFirmaApplet obtainInstanceObjecteFirmaApplet(Long sysdate, Long entitatId, Long documentId) {
		return new ObjecteFirmaApplet(sysdate, entitatId, documentId);
	}

	@Getter
	@Setter
	public class ObjecteFirmaApplet implements Serializable {

		private Long sysdate;
		private Long entitatId;
		private Long documentId;

		public ObjecteFirmaApplet(Long sysdate, Long entitatId, Long documentId) {
			this.sysdate = sysdate;
			this.entitatId = entitatId;
			this.documentId = documentId;
		}

		private static final long serialVersionUID = -6929597339153341365L;

	}

	/**
	 * Registra el log al document i al expedient on està el document.
	 * 
	 * @param documentPortafirmes
	 * @param tipusLog
	 */
	private void logAll(DocumentEntity document, LogTipusEnumDto tipusLog, String param1, String param2) {
		contingutLogHelper.log(document, tipusLog, param1, param2, false, false);
		logExpedient(document, tipusLog, param1, param2);
	}

	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaAppletHelper.class);

}
