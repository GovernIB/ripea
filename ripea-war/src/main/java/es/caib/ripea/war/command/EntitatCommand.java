/**
 * 
 */
package es.caib.ripea.war.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiEntitatNoRepetit;
import es.caib.ripea.war.validation.DocumentIdentitat;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	private String capsaleraColorFons;
	private String capsaleraColorLletra;
	
	
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
		return ConversioTipusHelper.convertir(
				dto,
				EntitatCommand.class);
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
