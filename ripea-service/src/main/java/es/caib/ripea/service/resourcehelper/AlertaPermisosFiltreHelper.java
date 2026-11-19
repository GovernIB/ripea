package es.caib.ripea.service.resourcehelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;

/**
 * Filtre (springfilter) de permisos de la consulta REACT «Alertes d'expedients».
 *
 * Una alerta es veu si i només si es veu el seu expedient: s'apliquen les mateixes vies i restriccions que
 * el llistat d'expedients (ExpedientPermisosFiltreHelper amb prefix "expedient."), incloent-hi l'entitat
 * actual i que l'expedient no estigui esborrat. És el mateix criteri que EnviamentPermisosFiltreHelper
 * aplica als enviaments a Portafirmes i NOTIB.
 *
 * El camí "expedient." de l'alerta només existeix quan l'alerta és de l'expedient mateix (veure
 * AlertaResourceEntity.expedient): el filtre deixa fora, per tant, les alertes dels documents.
 *
 * @author Límit Tecnologies
 */
@Component
public class AlertaPermisosFiltreHelper {

	// En cas de no disposar d'entitat actual, filtrarem per un string amb una mida superior a la mida màxima
	// del camp codi de manera que asseguram que no es retornin resultats un cop aplicat el filtre
	private static final String ENTITAT_INEXISTENT = "................................................................................";

	@Autowired private ConfigHelper configHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private ExpedientPermisosFiltreHelper expedientPermisosFiltreHelper;

	public String generarFiltre() {

		String entitatActualCodi = configHelper.getEntitatActualCodi();
		String organActualCodi	 = configHelper.getOrganActualCodi();
		String rolActual		 = configHelper.getRolActual();
		String prefix			 = ExpedientPermisosFiltreHelper.PREFIX_EXPEDIENT;

		Filter filtreBase = FilterBuilder.and(
				FilterBuilder.equal(prefix + ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi,
						entitatActualCodi != null ? entitatActualCodi : ENTITAT_INEXISTENT),
				FilterBuilder.equal(prefix + ContingutResource.Fields.esborrat, 0));

		if (entitatActualCodi == null) {
			return filtreBase.generate();
		}

		// Mateixa comprovació d'entitat que el llistat d'expedients (usuari, admin o òrgan)
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
		OrganGestorEntity organActual = organGestorRepository.findByEntitatIdAndCodi(entitat.getId(), organActualCodi);
		PermisosPerExpedientsDto permisosPerExpedients = expedientHelper.findPermisosPerExpedients(
				entitat.getId(),
				rolActual,
				organActual != null ? organActual.getId() : null);

		if (expedientPermisosFiltreHelper.isCapPermis(permisosPerExpedients, rolActual)) {
			return FilterBuilder.equal("id", 0).generate();
		}

		return FilterBuilder.and(
				filtreBase,
				expedientPermisosFiltreHelper.filtrePermisos(permisosPerExpedients, rolActual, prefix)).generate();
	}
}
