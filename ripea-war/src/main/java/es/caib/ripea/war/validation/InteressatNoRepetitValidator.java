/**
 * 
 */
package es.caib.ripea.war.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.war.command.InteressatCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatNoRepetitValidator implements ConstraintValidator<InteressatNoRepetit, Object> {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;



	@Override
	public void initialize(final InteressatNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			InteressatCommand interessat = (InteressatCommand)value;
			
			final Long id = interessat.getId();
			if (id != null)
				return true;
			final InteressatTipusEnumDto tipus = interessat.getTipus();
			
			boolean existeix;
			if (InteressatTipusEnumDto.PERSONA_FISICA.equals(tipus)) {
				List<InteressatPersonaFisicaDto> interessats = expedientInteressatService.findByFiltrePersonaFisica(
						interessat.getDocumentNum(), 
						interessat.getNom(), 
						interessat.getLlinatge1(), 
						interessat.getLlinatge2());
				existeix = interessats.size() > 0;
			} else if (InteressatTipusEnumDto.PERSONA_JURIDICA.equals(tipus)) {
				List<InteressatPersonaJuridicaDto> interessats = expedientInteressatService.findByFiltrePersonaJuridica(
						interessat.getDocumentNum(),
						interessat.getRaoSocial());
				existeix = interessats.size() > 0;
			} else if (InteressatTipusEnumDto.ADMINISTRACIO.equals(tipus)) {
				List<InteressatAdministracioDto> interessats = expedientInteressatService.findByFiltreAdministracio(
						interessat.getOrganCodi());
				existeix = interessats.size() > 0;
			} else {
				throw new RuntimeException("No s'ha pogut comprovar si l'interessat ja està donat d'alta: tipus desconegut");
			}
			return !existeix;
		} catch (final Exception ex) {
        	LOGGER.error("Error al comprovar si l'interessat ja està donat d'alta", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatNoRepetitValidator.class);

}
