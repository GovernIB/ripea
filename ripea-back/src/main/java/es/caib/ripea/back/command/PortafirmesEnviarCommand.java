/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command per a enviar documents al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesEnviarCommand {

	@NotEmpty @Size(max=256)
	private String motiu;
	@NotNull
	private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
	//@NotNull
	//private Date dataCaducitat;
	private Date dataInici;
	private boolean enviarCorreu;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;

	private String portafirmesEnviarFluxId;
	private String portafirmesFluxNom;
	private String portafirmesFluxDescripcio;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	private Long[] annexos;
	private boolean firmaParcial;
	private boolean avisFirmaParcial;
	
	public String[] getPortafirmesResponsables() {
		return portafirmesResponsables;
	}

	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}

	public MetaDocumentFirmaSequenciaTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}

	public void setPortafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus) {
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
	}

	public String getPortafirmesEnviarFluxId() {
		return portafirmesEnviarFluxId;
	}

	public void setPortafirmesEnviarFluxId(String portafirmesEnviarFluxId) {
		this.portafirmesEnviarFluxId = portafirmesEnviarFluxId != null ? portafirmesEnviarFluxId.trim() : null;
	}

	public String getPortafirmesFluxNom() {
		return portafirmesFluxNom;
	}

	public void setPortafirmesFluxNom(String portafirmesFluxNom) {
		this.portafirmesFluxNom = portafirmesFluxNom != null ? portafirmesFluxNom.trim() : null;
	}

	public String getPortafirmesFluxDescripcio() {
		return portafirmesFluxDescripcio;
	}

	public void setPortafirmesFluxDescripcio(String portafirmesFluxDescripcio) {
		this.portafirmesFluxDescripcio = portafirmesFluxDescripcio != null ? portafirmesFluxDescripcio.trim() : null;
	}

	public MetaDocumentFirmaFluxTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}

	public void setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}

	public Long[] getAnnexos() {
		return annexos;
	}

	public void setAnnexos(Long[] annexos) {
		this.annexos = annexos;
	}

//	public PortafirmesEnviarCommand() {
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, 7);
//		cal.set(Calendar.HOUR, 23);
//		cal.set(Calendar.MINUTE, 59);
//		cal.set(Calendar.SECOND, 59);
//		cal.set(Calendar.MILLISECOND, 999);
//		dataCaducitat = cal.getTime();
//	}

	public String getMotiu() {
		return motiu;
	}
	public boolean isAvisFirmaParcial() {
		return avisFirmaParcial;
	}

	public void setAvisFirmaParcial(boolean avisFirmaParcial) {
		this.avisFirmaParcial = avisFirmaParcial;
	}

	public void setMotiu(String motiu) {
		if (motiu != null && motiu.length() > 255)
			motiu = motiu.substring(0, 251).trim() + "...]";
		this.motiu = motiu != null ? motiu.trim() : null;
	}
	public PortafirmesPrioritatEnumDto getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(PortafirmesPrioritatEnumDto prioritat) {
		this.prioritat = prioritat;
	}

	public Date getDataInici() {
		return dataInici;
	}

	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	public boolean isEnviarCorreu() {
		return enviarCorreu;
	}

	public void setEnviarCorreu(boolean enviarCorreu) {
		this.enviarCorreu = enviarCorreu;
	}

	public boolean isFirmaParcial() {
		return firmaParcial;
	}

	public void setFirmaParcial(boolean firmaParcial) {
		this.firmaParcial = firmaParcial;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
