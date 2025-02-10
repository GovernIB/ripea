package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter @Getter
public class DocumentNotificacioDto extends DocumentEnviamentDto {

	private Long id;
	private DocumentNotificacioTipusEnumDto tipus;
	private Date dataProgramada;
	private Integer retard;
	private Date dataCaducitat;
	private String notificacioIdentificador;
	private String enviamentReferencia;
	private DocumentNotificacioEstatEnumDto notificacioEstat;
	private ServeiTipusEnumDto serveiTipusEnum;
	private boolean entregaPostal;
	private List<Long> interessatsIds = new ArrayList<Long>();
	private List<InteressatDto> interessats = new ArrayList<InteressatDto>();
	private Set<DocumentEnviamentInteressatDto> documentEnviamentInteressats = new HashSet<DocumentEnviamentInteressatDto>();
	private Date registreData;
	private String registreNumero;
	private String registreNumeroFormatat;
	private boolean ambRegistres;
	private OrganGestorDto emisor;
	private Date dataEnviada;
	private Date dataFinalitzada;
	
	@Override
	public String getDestinatari() {
		return null;
	}
	@Override
	public String getDestinatariAmbDocument() {
		return null;
	}

	public boolean isAmbRegistres() {
		if (documentEnviamentInteressats != null) {
			for (DocumentEnviamentInteressatDto enviament : documentEnviamentInteressats) {
				if (enviament.getRegistreNumeroFormatat() != null && ! enviament.getRegistreNumeroFormatat().isEmpty())
					ambRegistres = true;
				else
					ambRegistres = false;
			}
		}
		return ambRegistres;
	}
	
	public boolean isEnviamentCertificacio() {
		if (Utils.isNotEmpty(documentEnviamentInteressats) && documentEnviamentInteressats.iterator().next().getEnviamentCertificacioData() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Long getEnviamentId() {
		if (Utils.isNotEmpty(documentEnviamentInteressats)) {
			return documentEnviamentInteressats.iterator().next().getId();
		} else {
			return null;
		}
	}

}