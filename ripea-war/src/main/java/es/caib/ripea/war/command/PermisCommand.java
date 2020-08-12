/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PermisCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String principalNom;
	@NotNull
	private PrincipalTipusEnumDto principalTipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	private boolean selectAll;

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
}
