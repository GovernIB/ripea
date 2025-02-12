package es.caib.ripea.service.helper;

import com.itextpdf.text.pdf.*;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IndexValidacioHelper {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	
	public Map<Integer, Date> recuperarDataFirma(DocumentEntity document) {
		Map<Integer, Date> datesFirmes = null;
		try {
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearNovaFila getDataFirmaFromDocument start (" + document.getId() + ")");
	  
			long t3 = System.currentTimeMillis();
			
			es.caib.plugins.arxiu.api.Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					document,
					null,
					null,
					true,
					false);
			byte[] contingutArxiu = documentHelper.getContingutFromArxiuDocument(arxiuDocument);
			datesFirmes = getDataFirmaFromDocument(contingutArxiu);
			
			if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("crearNovaFila getDataFirmaFromDocument end (" + document.getId() + "):  " + (System.currentTimeMillis() - t3) + " ms");
		} catch (Exception ex) {
			logger.error("Hi ha hagut un error recuperant l'hora de firma del document", ex);
		}
		
		return datesFirmes;
	}
	
	private Map<Integer, Date> getDataFirmaFromDocument(byte[] content) throws IOException {
		Map<Integer, Date> datesFirmes = new HashMap<Integer, Date>();
		PdfReader reader = new PdfReader(content);
		AcroFields fields = reader.getAcroFields();
		List<String> signatureNames = fields.getSignatureNames();
		Integer idx = 1;
		if (signatureNames != null) {
			for (String name: signatureNames) {
//				### comprovar si és una firma o un segell
				PdfDictionary dictionary = fields.getSignatureDictionary(name);
				if (dictionary != null && dictionary.get(PdfName.TYPE) != null && dictionary.get(PdfName.TYPE).toString().equalsIgnoreCase("/Sig")) {
					String dataFirmaStr = dictionary.get(PdfName.M) != null ? dictionary.get(PdfName.M).toString() : null;
					Date dataFirma = dataFirmaStr != null ? PdfDate.decode(dataFirmaStr).getTime() : null;
					datesFirmes.put(idx, dataFirma);
					idx++;
				}
			}
		}
		return datesFirmes;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(IndexValidacioHelper.class);
}
