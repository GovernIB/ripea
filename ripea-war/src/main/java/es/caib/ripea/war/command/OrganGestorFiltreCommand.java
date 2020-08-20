/**
 * 
 */
package es.caib.ripea.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter @Setter
public class OrganGestorFiltreCommand {
		
		private String codi;
		private String nom;
//		
//		public static OrganGestorFiltreCommand asCommand(OrganGestorFiltreDto dto) {
//			if (dto == null) {
//				return null;
//			}
//			OrganGestorFiltreCommand command = ConversioTipusHelper.convertir(
//					dto,
//					OrganGestorFiltreCommand.class );
//			return command;
//		}
//		public static OrganGestorFiltreDto asDto(OrganGestorFiltreCommand command) {
//			if (command == null) {
//				return null;
//			}
//			OrganGestorFiltreDto dto = ConversioTipusHelper.convertir(
//					command,
//					OrganGestorFiltreDto.class);
//			return dto;
//		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}