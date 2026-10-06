package es.caib.ripea.service.resourcehelper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.service.helper.PermisosPerAnotacions;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.RegistreResource;

/**
 * Fragments de filtre (springfilter) de la part de permisos del llistat d'anotacions REACT (named query
 * LLISTAT_ANOTACIONS de ExpedientPeticioResourceServiceImpl).
 *
 * Es comparteixen amb el simulador de permisos del superusuari perquè tots dos apliquin exactament la
 * mateixa definició. Les llistes les calcula ExpedientPeticioHelper.findPermisosPerAnotacions.
 *
 * @author Límit Tecnologies
 */
@Component
public class AnotacioPermisosFiltreHelper {

	/** L'administrador d'entitat veu totes les anotacions de l'entitat (també les que no tenen procediment). */
	public boolean isAplicaFiltrePermisos(String rolActual) {
		return !"IPA_ADMIN".equals(rolActual);
	}

	/** L'administrador d'òrgan filtra per l'òrgan destí del registre; la resta de rols, per procediment. */
	public boolean isFiltrePerOrganDesti(String rolActual) {
		return "IPA_ORGAN_ADMIN".equals(rolActual);
	}

	/** Filtre complet de permisos (només s'ha d'aplicar si {@link #isAplicaFiltrePermisos(String)}). */
	public Filter filtrePermisos(String rolActual, PermisosPerAnotacions permisosPerAnotacions) {
		//Aplica filtres de permisos per organ
		if (isFiltrePerOrganDesti(rolActual)) {
			Filter filtreOrgansPermesos = filtreOrgansDesti(permisosPerAnotacions);
			//Sense òrgans permesos no es retornen resultats (igual que la consulta antiga),
			//evitant que un filtre nul es perdi a l'AND final.
			return filtreOrgansPermesos!=null ? filtreOrgansPermesos : FilterBuilder.equal("id", 0);
		} else { //Aplica filtres de permisos per procediment
			Filter filtreProcedimentsPermesos = filtreProcediments(permisosPerAnotacions);
			//Sense procediments permesos no es retornen resultats (igual que la consulta antiga).
			//Així evitam que FilterBuilder.and(null, ...) elimini la restricció per procediment.
			if (filtreProcedimentsPermesos!=null) {
				return FilterBuilder.and(filtreProcedimentsPermesos, filtreGestioGrups(permisosPerAnotacions));
			} else {
				return FilterBuilder.equal("id", 0);
			}
		}
	}

	/** Administrador d'òrgan: òrgan destí del registre dins l'òrgan actual i els seus descendents. */
	public Filter filtreOrgansDesti(PermisosPerAnotacions permisosPerAnotacions) {
		String ogId = ExpedientPeticioResource.Fields.registre + "." + RegistreResource.Fields.destiCodi;
		Filter filtreOrgansPermesos = null;
		List<String> grupsOrgansPermesosClausulesIn = permisosPerAnotacions.getIdsOrganGestorsGruposMil();
		if (grupsOrgansPermesosClausulesIn!=null) {
			for (String aux: grupsOrgansPermesosClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(ogId + " IN (" + aux + ")"));
				}
			}
		}
		return filtreOrgansPermesos;
	}

	/** Procediment de l'anotació dins dels procediments permesos. */
	public Filter filtreProcediments(PermisosPerAnotacions permisosPerAnotacions) {
		String prId = ExpedientPeticioResource.Fields.metaExpedient + ".id";
		Filter filtreProcedimentsPermesos = null;
		List<String> grupsProcsPermesosClausulesIn = permisosPerAnotacions.getIdsProcedimentsGruposMil();
		if (grupsProcsPermesosClausulesIn!=null) {
			for (String aux: grupsProcsPermesosClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreProcedimentsPermesos = FilterBuilder.or(filtreProcedimentsPermesos, Filter.parse(prId + " IN (" + aux + ")"));
				}
			}
		}
		return filtreProcedimentsPermesos;
	}

	/** Procediments amb gestió per grups activa: el grup de l'anotació ha d'estar entre els permesos. */
	public Filter filtreGestioGrups(PermisosPerAnotacions permisosPerAnotacions) {
		String grId = ExpedientPeticioResource.Fields.grup + ".id";
		Filter filtregrupsPermesos = null;
		List<String> grupsgrupsPermesosClausulesIn = permisosPerAnotacions.getIdsGrupsGruposMil();
		if (grupsgrupsPermesosClausulesIn!=null) {
			for (String aux: grupsgrupsPermesosClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtregrupsPermesos = FilterBuilder.or(filtregrupsPermesos, Filter.parse(grId + " IN (" + aux + ")"));
				}
			}
		}

		String grAct = ExpedientPeticioResource.Fields.metaExpedient +"."+ MetaExpedientResource.Fields.gestioAmbGrupsActiva;
		Filter notGestioGrupsActiva = FilterBuilder.equal(grAct, false);
		return FilterBuilder.or(notGestioGrupsActiva, filtregrupsPermesos);
	}
}
