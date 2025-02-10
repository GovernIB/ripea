package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.ExpedientFiltreDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

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
	private boolean ambFirmaPendent;
	private String numeroRegistre;	
	private boolean expedientsSeguits;	
	private Long grupId;
	
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
		this.metaExpedientDominiCodi = Utils.trim(metaExpedientDominiCodi);
	}
	public void setNom(String nom) {
		this.nom = Utils.trim(nom);
	}
	public void setDataCreacioInici(Date dataCreacioInici) {
		this.dataCreacioInici = dataCreacioInici;
	}
	public void setDataCreacioFi(Date dataCreacioFi) {
		this.dataCreacioFi = dataCreacioFi;
	}
	public void setNumero(String numero) {
		this.numero = Utils.trim(numero);
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
		this.interessat = Utils.trim(interessat);
	}
	public void setMetaExpedientDominiValor(String metaExpedientDominiValor) {
		this.metaExpedientDominiValor = Utils.trim(metaExpedientDominiValor);
	}
	public void setAgafatPer(String agafatPer) {
		this.agafatPer = Utils.trim(agafatPer);
	}
	public void setAmbFirmaPendent(boolean ambFirmaPendent) {
		this.ambFirmaPendent = ambFirmaPendent;
	}
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = Utils.trim(numeroRegistre);
	}
	public void setGrupId(Long grupId) {
		this.grupId = grupId;
	}
	public void setExpedientsSeguits(
			boolean expedientsSeguits) {
		this.expedientsSeguits = expedientsSeguits;
	}
}