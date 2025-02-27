/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.CodiEntitatNoRepetit;
import es.caib.ripea.back.validation.DocumentIdentitat;
import es.caib.ripea.service.intf.dto.EntitatDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@CodiEntitatNoRepetit(campId = "id", campCodi = "codi")
public class EntitatCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotEmpty @Size(max=9) @DocumentIdentitat
	private String cif;
	@NotEmpty @Size(max=9)
	private String unitatArrel;
	private MultipartFile logoImg;
	private boolean logo;
	private String capsaleraColorFons;
	private String capsaleraColorLletra;
	private boolean permetreEnviamentPostal;

	
	public String getCapsaleraColorLletra() {
		return capsaleraColorLletra;
	}
	public void setCapsaleraColorLletra(String capsaleraColorLletra) {
		this.capsaleraColorLletra = capsaleraColorLletra != null ? capsaleraColorLletra.trim() : null;
	}
	public String getCapsaleraColorFons() {
		return capsaleraColorFons;
	}
	public void setCapsaleraColorFons(String capsaleraColorFons) {
		this.capsaleraColorFons = capsaleraColorFons != null ? capsaleraColorFons.trim() : null;
	}
	public MultipartFile getLogoImg() {
		return logoImg;
	}
	public void setLogoImg(MultipartFile logoImg) {
		this.logoImg = logoImg;
	}
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
		this.codi = codi != null ? codi.trim() : null;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif != null ? cif.trim() : null;
	}
	public String getUnitatArrel() {
		return unitatArrel;
	}
	public void setUnitatArrel(String unitatArrel) {
		this.unitatArrel = unitatArrel != null ? unitatArrel.trim() : null;
	}
	public boolean isLogo() {
		return logo;
	}
	public void setLogo(
			boolean logo) {
		this.logo = logo;
	}
	public static List<EntitatCommand> toEntitatCommands(
			List<EntitatDto> dtos) {
		List<EntitatCommand> commands = new ArrayList<EntitatCommand>();
		for (EntitatDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {
		EntitatCommand entitat = ConversioTipusHelper.convertir(
				dto,
				EntitatCommand.class);
		entitat.setLogo(dto.getLogoImgBytes() != null ? true : false);
		return entitat;
	}
	public static EntitatDto asDto(EntitatCommand command) throws IOException {
		EntitatDto entitat = ConversioTipusHelper.convertir(
				command,
				EntitatDto.class);
		entitat.setLogoImgBytes(command.getLogoImg() != null ? command.getLogoImg().getBytes() : null);
		return entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
