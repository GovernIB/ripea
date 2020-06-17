/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Calendar;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;

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
	@NotNull
	private Date dataCaducitat;
	private Date dataInici;
	private boolean enviarCorreu;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;

	private String portafirmesFluxId;
	private String portafirmesFluxNom;
	private String portafirmesFluxDescripcio;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	private Long[] annexos;
	
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

	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}

	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}

	public String getPortafirmesFluxNom() {
		return portafirmesFluxNom;
	}

	public void setPortafirmesFluxNom(String portafirmesFluxNom) {
		this.portafirmesFluxNom = portafirmesFluxNom;
	}

	public String getPortafirmesFluxDescripcio() {
		return portafirmesFluxDescripcio;
	}

	public void setPortafirmesFluxDescripcio(String portafirmesFluxDescripcio) {
		this.portafirmesFluxDescripcio = portafirmesFluxDescripcio;
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

	public PortafirmesEnviarCommand() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		dataCaducitat = cal.getTime();
	}

	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	public PortafirmesPrioritatEnumDto getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(PortafirmesPrioritatEnumDto prioritat) {
		this.prioritat = prioritat;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
