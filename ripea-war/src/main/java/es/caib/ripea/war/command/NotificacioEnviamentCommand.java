package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;

public class NotificacioEnviamentCommand {

	private InteressatCommand titular;
	private InteressatCommand destinatari;
	private ServeiTipusEnumDto serveiTipusEnum;
	private boolean entregaPostal;


	
	public boolean getEntregaPostal() {
		return entregaPostal;
	}

	public void setEntregaPostal(boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}

	public InteressatCommand getTitular() {
		return titular;
	}

	public void setTitular(InteressatCommand titular) {
		this.titular = titular;
	}

	public InteressatCommand getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(InteressatCommand destinatari) {
		this.destinatari = destinatari;
	}

	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}

	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
	}

}