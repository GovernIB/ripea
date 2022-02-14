/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'una notificació d'un document a un ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter @Getter
public class DocumentNotificacioDto extends DocumentEnviamentDto {

	private Long id;
	private DocumentNotificacioTipusEnumDto tipus;
	private Date dataProgramada;
	private Integer retard;
	private Date dataCaducitat;
	private String enviamentIdentificador;
	private String enviamentReferencia;
	private List<Long> interessatsIds = new ArrayList<Long>();
	private Set<DocumentEnviamentInteressatDto> documentEnviamentInteressats = new HashSet<DocumentEnviamentInteressatDto>();
	private List<NotificacioEnviamentDto> enviaments = new ArrayList<NotificacioEnviamentDto>();
	private DocumentNotificacioEstatEnumDto notificacioEstat;
	private ServeiTipusEnumDto serveiTipusEnum;
	private boolean entregaPostal;
	private List<InteressatDto> interessats = new ArrayList<InteressatDto>();
	private Date registreData;
	private String registreNumero;
	private String registreNumeroFormatat;
	
	private boolean ambRegistres;
	private OrganGestorDto emisor;
	
	
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

}
