/**
 * 
 */
package es.caib.ripea.war.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.service.DocumentService;
import io.milton.http.Auth;
import io.milton.http.FileItem;
import io.milton.http.Range;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.CollectionResource;
import io.milton.resource.FileResource;
import io.milton.resource.ReplaceableResource;

/**
 * Resource que representa un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RipeaFileResource implements ReplaceableResource, FileResource {

	private EntitatDto entitat;
	private DocumentDto document;
	private ServiceHolder serviceHolder;

	private FitxerDto fitxer;



	public RipeaFileResource(
			EntitatDto entitat,
			DocumentDto document,
			ServiceHolder serviceHolder) {
		this.entitat = entitat;
		this.document = document;
		this.serviceHolder = serviceHolder;
	}

	@Override
	public String getUniqueId() {
		//logger.debug("[A] getUniqueId " + getIdentificadorPerLog() + ": " + document.getId().toString());
		return document.getId().toString();
	}

	@Override
	public String getName() {
		//logger.debug("[A] getName " + getIdentificadorPerLog() + ": " + document.getDarreraVersio().getArxiuNom());
		return document.getFitxerNom();
	}

	@Override
	public Object authenticate(String user, String password) {
		//logger.debug("[A] authenticate " + getIdentificadorPerLog() + "");
		return null;
	}

	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		//logger.debug("[A] authorise " + getIdentificadorPerLog() + ": true");
		return true;
	}

	@Override
	public String getRealm() {
		//logger.debug("[A] getRealm " + getIdentificadorPerLog() + "");
		return "Govern de les Illes Balears";
	}

	@Override
	public Date getModifiedDate() {
		//logger.debug("[A] getModifiedDate " + getIdentificadorPerLog() + ": " + document.getDarreraVersio().getLastModifiedDate());
		return document.getLastModifiedDate();
	}

	@Override
	public String checkRedirect(
			Request request) throws NotAuthorizedException, BadRequestException {
		//logger.debug("[A] checkRedirect " + getIdentificadorPerLog() + ": null");
		return null;
	}

	@Override
	public Date getCreateDate() {
		//logger.debug("[A] getCreateDate " + getIdentificadorPerLog() + ": " + document.getDarreraVersio().getCreatedDate());
		return document.getCreatedDate();
	}

	@Override
	public void sendContent(
			OutputStream out,
			Range range,
			Map<String, String> params,
			String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		logger.debug("[A] sendContent");
		out.write(getFitxer().getContingut());
		out.flush();
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		//logger.debug("[A] getMaxAgeSeconds " + getIdentificadorPerLog() + "");
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		//logger.debug("[A] getContentType " + getIdentificadorPerLog() + ": " + document.getDarreraVersio().getArxiuContentType());
		return document.getFitxerContentType();
	}

	@Override
	public Long getContentLength() {
		//logger.debug("[A] getContentLength " + getIdentificadorPerLog() + ": " + document.getDarreraVersio().getArxiuContentLength());
		return new Long(document.getFitxerContingut().length);
	}

	@Override
	public void copyTo(
			CollectionResource toCollection,
			String name) throws NotAuthorizedException, BadRequestException, ConflictException {
		// TODO Auto-generated method stub
		logger.debug("[A] copyTo " + getIdentificadorPerLog() + "");
	}

	@Override
	public void moveTo(
			CollectionResource rDest,
			String name) throws ConflictException, NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub
		logger.debug("[A] moveTo " + getIdentificadorPerLog() + "");
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException,
			BadRequestException {
		// TODO Auto-generated method stub
		logger.debug("[A] delete " + getIdentificadorPerLog() + "");
	}

	@Override
	public String processForm(
			Map<String, String> parameters,
			Map<String, FileItem> files) throws BadRequestException, NotAuthorizedException, ConflictException {
		// TODO Auto-generated method stub
		logger.debug("[A] processForm " + getIdentificadorPerLog() + "");
		return null;
	}

	@Override
	public void replaceContent(
			InputStream in,
			Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
		logger.debug("[A] replaceContent " + getIdentificadorPerLog() + " (entitatId=" + document.getEntitat().getId() + ", documentId=" + document.getId() + ")");
//		DocumentVersioDto darreraVersio = getDocumentService().findDarreraVersio(
//				document.getEntitat().getId(),
//				document.getId());
		try {
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setContingut(IOUtils.toByteArray(in));
			getDocumentService().update(
					document.getEntitat().getId(),
					document.getId(),
					document);
		} catch (IOException ex) {
			throw new BadRequestException("Error al llegir les dades de l'arxiu", ex);
		}
	}



	private FitxerDto getFitxer() {
		if (fitxer == null) {
			fitxer = getDocumentService().descarregar(
					entitat.getId(),
					document.getId(),
					document.getVersioDarrera());
		}
		return fitxer;
	}

	private String getIdentificadorPerLog() {
		return "(id=" + getUniqueId() + ", name=" + getName() + ")";
	}

	private DocumentService getDocumentService() {
		return serviceHolder.getDocumentService();
	}

	private static final Logger logger = LoggerFactory.getLogger(RipeaFileResource.class);

}
