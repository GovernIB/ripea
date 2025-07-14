package es.caib.ripea.back.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import es.caib.ripea.back.validation.ArxiuZipNoBuit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Command per al manteniment d'importació de documents de zip.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ToString
@Getter
@Setter
public class ImportacioZipCommand {

	@ArxiuZipNoBuit(groups = { ProcessarZip.class })
	private MultipartFile arxiuZip;
	@Valid
	private List<DocumentCommand> documents = new ArrayList<DocumentCommand>();
	private Long pareId;
	private Long tascaId;
	private Long metaExpedientId;

	public interface ProcessarZip {}

}
