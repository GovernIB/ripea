/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientNoRepetit;

/**
 * Command per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiMetaExpedientNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId")
public class MetaExpedientCommand {

	private Long id;

	@NotEmpty @Size(max = 64)
	private String codi;
	@NotEmpty @Size(max = 256)
	private String nom;
	@Size(max=1024)
	private String descripcio;
	@NotEmpty @Size(max = 30)
	private String classificacioSia;
	@NotEmpty @Size(max = 30)
	private String serieDocumental;
	@Size(max = 100)
	private String expressioNumero;
	private boolean notificacioActiva;
	@Size(max = 44)
	private String notificacioSeuProcedimentCodi;
	@Size(max = 5)
	private String notificacioSeuRegistreLlibre;
	@Size(max = 9)
	private String notificacioSeuRegistreOficina;
	@Size(max = 9)
	private String notificacioSeuRegistreOrgan;
	@Size(max = 9)
	private String notificacioSeuExpedientUnitatOrganitzativa;
	@Size(max = 256)
	private String notificacioAvisTitol;
	@Size(max = 1024)
	private String notificacioAvisText;
	@Size(max = 200)
	private String notificacioAvisTextMobil;
	@Size(max = 256)
	private String notificacioOficiTitol;
	@Size(max = 1024)
	private String notificacioOficiText;
	private Long pareId;
	private Long entitatId;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getClassificacioSia() {
		return classificacioSia;
	}
	public void setClassificacioSia(String classificacioSia) {
		this.classificacioSia = classificacioSia;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	public String getExpressioNumero() {
		return expressioNumero;
	}
	public void setExpressioNumero(String expressioNumero) {
		this.expressioNumero = expressioNumero;
	}
	public boolean isNotificacioActiva() {
		return notificacioActiva;
	}
	public void setNotificacioActiva(boolean notificacioActiva) {
		this.notificacioActiva = notificacioActiva;
	}
	public String getNotificacioSeuProcedimentCodi() {
		return notificacioSeuProcedimentCodi;
	}
	public void setNotificacioSeuProcedimentCodi(String notificacioSeuProcedimentCodi) {
		this.notificacioSeuProcedimentCodi = notificacioSeuProcedimentCodi;
	}
	public String getNotificacioSeuRegistreLlibre() {
		return notificacioSeuRegistreLlibre;
	}
	public void setNotificacioSeuRegistreLlibre(String notificacioSeuRegistreLlibre) {
		this.notificacioSeuRegistreLlibre = notificacioSeuRegistreLlibre;
	}
	public String getNotificacioSeuRegistreOficina() {
		return notificacioSeuRegistreOficina;
	}
	public void setNotificacioSeuRegistreOficina(String notificacioSeuRegistreOficina) {
		this.notificacioSeuRegistreOficina = notificacioSeuRegistreOficina;
	}
	public String getNotificacioSeuRegistreOrgan() {
		return notificacioSeuRegistreOrgan;
	}
	public void setNotificacioSeuRegistreOrgan(String notificacioSeuRegistreOrgan) {
		this.notificacioSeuRegistreOrgan = notificacioSeuRegistreOrgan;
	}
	public String getNotificacioSeuExpedientUnitatOrganitzativa() {
		return notificacioSeuExpedientUnitatOrganitzativa;
	}
	public void setNotificacioSeuExpedientUnitatOrganitzativa(String notificacioSeuExpedientUnitatOrganitzativa) {
		this.notificacioSeuExpedientUnitatOrganitzativa = notificacioSeuExpedientUnitatOrganitzativa;
	}
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public void setNotificacioAvisTitol(String notificacioAvisTitol) {
		this.notificacioAvisTitol = notificacioAvisTitol;
	}
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public void setNotificacioAvisText(String notificacioAvisText) {
		this.notificacioAvisText = notificacioAvisText;
	}
	public String getNotificacioAvisTextMobil() {
		return notificacioAvisTextMobil;
	}
	public void setNotificacioAvisTextMobil(String notificacioAvisTextMobil) {
		this.notificacioAvisTextMobil = notificacioAvisTextMobil;
	}
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public static List<MetaExpedientCommand> toEntitatCommands(
			List<MetaExpedientDto> dtos) {
		List<MetaExpedientCommand> commands = new ArrayList<MetaExpedientCommand>();
		for (MetaExpedientDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							MetaExpedientCommand.class));
		}
		return commands;
	}

	public static MetaExpedientCommand asCommand(MetaExpedientDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				MetaExpedientCommand.class);
	}
	public static MetaExpedientDto asDto(MetaExpedientCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				MetaExpedientDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
