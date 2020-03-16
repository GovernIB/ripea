/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Arrays;
import java.util.Date;

/**
 * Informació de l'enviament d'un document al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentPortafirmesDto extends DocumentEnviamentDto {

	private PortafirmesPrioritatEnumDto prioritat;
	private Date caducitatData;
	private String documentTipus;
	private String[] responsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto sequenciaTipus;
	private MetaDocumentFirmaFluxTipusEnumDto fluxTipus;
	private String fluxId;
	private String portafirmesId;
	private PortafirmesCallbackEstatEnumDto callbackEstat;

	public PortafirmesPrioritatEnumDto getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(PortafirmesPrioritatEnumDto prioritat) {
		this.prioritat = prioritat;
	}
	public Date getCaducitatData() {
		return caducitatData;
	}
	public void setCaducitatData(Date caducitatData) {
		this.caducitatData = caducitatData;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String[] getResponsables() {
		return responsables;
	}
	public void setResponsables(String[] responsables) {
		this.responsables = responsables;
	}
	public MetaDocumentFirmaSequenciaTipusEnumDto getSequenciaTipus() {
		return sequenciaTipus;
	}
	public void setSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto sequenciaTipus) {
		this.sequenciaTipus = sequenciaTipus;
	}
	public MetaDocumentFirmaFluxTipusEnumDto getFluxTipus() {
		return fluxTipus;
	}
	public void setFluxTipus(MetaDocumentFirmaFluxTipusEnumDto fluxTipus) {
		this.fluxTipus = fluxTipus;
	}
	public String getFluxId() {
		return fluxId;
	}
	public void setFluxId(String fluxId) {
		this.fluxId = fluxId;
	}
	public String getPortafirmesId() {
		return portafirmesId;
	}
	public void setPortafirmesId(String portafirmesId) {
		this.portafirmesId = portafirmesId;
	}
	public PortafirmesCallbackEstatEnumDto getCallbackEstat() {
		return callbackEstat;
	}
	public void setCallbackEstat(PortafirmesCallbackEstatEnumDto callbackEstat) {
		this.callbackEstat = callbackEstat;
	}

	@Override
	public String getDestinatari() {
		return Arrays.toString(responsables);
	}
	@Override
	public String getDestinatariAmbDocument() {
		return Arrays.toString(responsables);
	}

}
