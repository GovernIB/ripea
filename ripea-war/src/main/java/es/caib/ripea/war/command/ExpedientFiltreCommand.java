/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
public class ExpedientFiltreCommand {

	private Long arxiuId;
	private Long organGestorId;
	private Long metaExpedientId;
	private String metaExpedientDominiCodi;
	private String nom;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private String numero;
	private Long expedientEstatId;
	private Date dataTancatInici;
	private Date dataTancatFi;
	private boolean meusExpedients;
	private String agafatPer;

	private Long tipusId;
	private String interessat;
	private String metaExpedientDominiValor;


	public static ExpedientFiltreCommand asCommand(ExpedientFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ExpedientFiltreCommand.class);
	}
	public static ExpedientFiltreDto asDto(ExpedientFiltreCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientFiltreDto.class);
	}
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public void setArxiuId(Long arxiuId) {
		this.arxiuId = arxiuId;
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public void setMetaExpedientDominiCodi(String metaExpedientDominiCodi) {
		this.metaExpedientDominiCodi = Utils.getTrimOrNull(metaExpedientDominiCodi);
	}
	public void setNom(String nom) {
		this.nom = Utils.getTrimOrNull(nom);
	}
	public void setDataCreacioInici(Date dataCreacioInici) {
		this.dataCreacioInici = dataCreacioInici;
	}
	public void setDataCreacioFi(Date dataCreacioFi) {
		this.dataCreacioFi = dataCreacioFi;
	}
	public void setNumero(String numero) {
		this.numero = Utils.getTrimOrNull(numero);
	}
	public void setExpedientEstatId(Long expedientEstatId) {
		this.expedientEstatId = expedientEstatId;
	}
	public void setDataTancatInici(Date dataTancatInici) {
		this.dataTancatInici = dataTancatInici;
	}
	public void setDataTancatFi(Date dataTancatFi) {
		this.dataTancatFi = dataTancatFi;
	}
	public void setMeusExpedients(boolean meusExpedients) {
		this.meusExpedients = meusExpedients;
	}
	public void setTipusId(Long tipusId) {
		this.tipusId = tipusId;
	}
	public void setInteressat(String interessat) {
		this.interessat = Utils.getTrimOrNull(interessat);
	}
	public void setMetaExpedientDominiValor(String metaExpedientDominiValor) {
		this.metaExpedientDominiValor = Utils.getTrimOrNull(nom);
	}
	public void setAgafatPer(String agafatPer) {
		this.agafatPer = Utils.getTrimOrNull(agafatPer);
	}

}
