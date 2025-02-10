package es.caib.ripea.back.command;

import org.springframework.web.multipart.MultipartFile;


public class FileCommand {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}


}
