package es.caib.ripea.service.intf.dto;

public class NotificacioEnviamentDto {
	private InteressatDto titular;
	private InteressatDto destinatari;
	private ServeiTipusEnumDto serveiTipusEnum;
	private Boolean entregaPostal;

	
	public Boolean getEntregaPostal() {
		return entregaPostal;
	}

	public void setEntregaPostal(Boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}

	public InteressatDto getTitular() {
		return titular;
	}

	public void setTitular(InteressatDto titular) {
		this.titular = titular;
	}

	public InteressatDto getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(InteressatDto destinatari) {
		this.destinatari = destinatari;
	}

	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}

	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
	}





}