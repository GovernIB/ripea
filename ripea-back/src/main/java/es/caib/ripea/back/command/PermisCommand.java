package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.PermisMetaExpedient;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PrincipalTipusEnumDto;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@PermisMetaExpedient
public class PermisCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String principalNom;
	private String principalCodiNom;
	@NotNull
	private PrincipalTipusEnumDto principalTipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	private boolean statistics;
	private boolean selectAll;
	private boolean procedimentsComuns;
	private boolean administrationComuns;
	private boolean disseny;
	private Long organGestorId;

	public static List<PermisCommand> toPermisCommands(
			List<PermisDto> dtos) {
		List<PermisCommand> commands = new ArrayList<PermisCommand>();
		for (PermisDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							PermisCommand.class));
		}
		return commands;
	}

	public static PermisCommand asCommand(PermisDto dto) {
		PermisCommand permisCommand = ConversioTipusHelper.convertir(
				dto,
				PermisCommand.class);
		permisCommand.setSelectAll(false);
		if (permisCommand.isCreate() &&
			permisCommand.isDelete() &&
			permisCommand.isRead() &&
			permisCommand.isWrite())
			permisCommand.setSelectAll(true);
		return permisCommand;
	}
	public static PermisDto asDto(PermisCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				PermisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public void setId(Long id) {
		this.id = id;
	}

	public void setPrincipalNom(String principalNom) {
		this.principalNom = principalNom != null ? principalNom.trim() : null;
	}

	public void setPrincipalTipus(PrincipalTipusEnumDto principalTipus) {
		this.principalTipus = principalTipus;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public void setAdministration(boolean administration) {
		this.administration = administration;
	}

	public void setStatistics(boolean statistics) {
		this.statistics = statistics;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}

	public void setPrincipalCodiNom(String principalCodiNom) {
		this.principalCodiNom = principalCodiNom;
	}

	public void setProcedimentsComuns(boolean procedimentsComuns) {
		this.procedimentsComuns = procedimentsComuns;
	}

	public void setAdministrationComuns(boolean administrationComuns) {
		this.administrationComuns = administrationComuns;
	}

	public void setDisseny(boolean disseny) { this.disseny = disseny; }	
}