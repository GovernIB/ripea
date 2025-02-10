package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.DigitalitzacioEstatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;

public class DigitalitzacioRespostaCommand {

	private boolean error;
	private String errorDescripcio;
	private DigitalitzacioEstatDto errorTipus;
	private byte[] documentEscanejat;
	private byte[] documentFirmat;

	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio != null ? errorDescripcio.trim() : null;
	}
	public byte[] getDocumentEscanejat() {
		return documentEscanejat;
	}
	public void setDocumentEscanejat(byte[] documentEscanejat) {
		this.documentEscanejat = documentEscanejat;
	}
	public byte[] getDocumentFirmat() {
		return documentFirmat;
	}
	public void setDocumentFirmat(byte[] documentFirmat) {
		this.documentFirmat = documentFirmat;
	}
	public DigitalitzacioEstatDto getErrorTipus() {
		return errorTipus;
	}
	public void setErrorTipus(DigitalitzacioEstatDto errorTipus) {
		this.errorTipus = errorTipus;
	}

	public static DigitalitzacioRespostaCommand asCommand(DigitalitzacioResultatDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				DigitalitzacioRespostaCommand.class);
	}
	public static DigitalitzacioResultatDto asDto(DigitalitzacioRespostaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				DigitalitzacioResultatDto.class);
	}
}
