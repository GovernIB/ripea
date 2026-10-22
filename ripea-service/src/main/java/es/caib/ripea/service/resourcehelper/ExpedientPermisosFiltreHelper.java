package es.caib.ripea.service.resourcehelper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.service.intf.dto.PermisosPerExpedientsDto;
import es.caib.ripea.service.intf.model.DocumentEnviamentResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.MetaExpedientOrganGestorResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.utils.Utils;

/**
 * Fragments de filtre (springfilter) de la part de permisos del llistat d'expedients REACT.
 *
 * Cada via (OR que concedeix) i cada restricció (AND que retalla) es construeix per separat perquè la
 * consulta del llistat (ExpedientResourceServiceImpl.additionalSpringFilter) i el simulador de permisos
 * del superusuari en comparteixin exactament la mateixa definició. Un canvi de permisos s'ha de fer
 * aquí perquè arribi a tots dos.
 *
 * Mapa complet de vies i restriccions: les llistes les calcula ExpedientHelper.findPermisosPerExpedients;
 * una llista null desactiva la seva via (el fragment retorna null).
 *
 * Cada fragment admet un prefix de camí (p. ex. "expedient.") per aplicar els mateixos permisos des d'un
 * recurs que penja d'un expedient (enviaments a Portafirmes i NOTIB): l'element es veu si i només si es
 * veu el seu expedient. Sense prefix el filtre generat és el del llistat d'expedients.
 *
 * @author Límit Tecnologies
 */
@Component
public class ExpedientPermisosFiltreHelper {

	/** Prefix de camí per filtrar recursos que pengen d'un expedient pel camp "expedient". */
	public static final String PREFIX_EXPEDIENT = DocumentEnviamentResource.Fields.expedient + ".";

	/**
	 * Sense cap permís per cap via (i el rol n'aplica): el llistat no ha de retornar res. Els cridadors
	 * ho resolen amb un filtre "id = 0".
	 */
	public boolean isCapPermis(PermisosPerExpedientsDto permisosPerExpedients, String rolActual) {
		return isAplicaVies(rolActual) && permisosPerExpedients.capPermis();
	}

	/**
	 * Filtre complet de permisos sobre l'expedient (vies + restriccions A, B i C segons el rol), el mateix que
	 * aplica el llistat d'expedients REACT. No inclou el filtre d'entitat ni el d'expedients esborrats, ni el
	 * cas sense cap permís ({@link #isCapPermis}), que resol cada cridador.
	 *
	 * @param prefix camí fins a l'expedient des de l'arrel de la consulta ("" per al llistat d'expedients).
	 */
	public Filter filtrePermisos(PermisosPerExpedientsDto permisosPerExpedients, String rolActual, String prefix) {
		Filter filtreVies = null;
		Filter filtreRestriccioPermisDirecte = null;
		Filter filtreRestriccioGrups = null;
		if (isAplicaVies(rolActual)) {
			filtreVies = filtreVies(permisosPerExpedients, prefix);
			//RESTRICCIO A: permis directe a procediments (issue #1633)
			if (isAplicaRestriccioPermisDirecte(rolActual)) {
				filtreRestriccioPermisDirecte = filtreRestriccioPermisDirecte(permisosPerExpedients, prefix);
			}
			//RESTRICCIO C: grups
			if (isAplicaRestriccioGrups(rolActual)) {
				filtreRestriccioGrups = filtreRestriccioGrups(permisosPerExpedients, prefix);
			}
		}
		//RESTRICCIO B: organs permesos (nomes si la llista no es null: admin d'organ i dissenyador)
		Filter filtreRestriccioOrgans = filtreRestriccioOrgans(permisosPerExpedients, prefix);
		return FilterBuilder.and(
				filtreVies,
				filtreRestriccioPermisDirecte,
				filtreRestriccioOrgans,
				filtreRestriccioGrups);
	}

	/** Els administradors d'entitat (i de lectura) veuen tots els expedients de l'entitat: no s'aplica cap via. */
	public boolean isAplicaVies(String rolActual) {
		return !rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_ADMIN_LECTURA");
	}

	/** Restricció A (permís directe, issue #1633): exempts els administradors d'entitat i el superusuari. */
	public boolean isAplicaRestriccioPermisDirecte(String rolActual) {
		return !rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_SUPER") && !rolActual.equals("IPA_ADMIN_LECTURA");
	}

	/** Restricció C (grups): exempts els administradors d'entitat i d'òrgan. */
	public boolean isAplicaRestriccioGrups(String rolActual) {
		return !rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_ORGAN_ADMIN") && !rolActual.equals("IPA_ADMIN_LECTURA");
	}

	/** OR de les 5 vies. */
	public Filter filtreVies(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreVies(permisosPerExpedients, "");
	}

	public Filter filtreVies(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {

		/**
	 	"and (" +
		"     (:esNullIdsMetaExpedientsPermesos = false and (e.metaExpedient.id in (:idsMetaExpedientsPermesos0))) " +
		"     or (:esNullIdsOrgansPermesos = false and (meogp.organGestor.id in (:idsOrgansPermesos0))) " +
		"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
		"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and e.metaExpedient.id in (:idsProcedimentsComuns))
		) " +
		 */

		return FilterBuilder.or(
				filtreVia1Procediments(permisosPerExpedients, prefix),
				filtreVia2Organs(permisosPerExpedients, prefix),
				filtreVia3ParellesProcedimentOrgan(permisosPerExpedients, prefix),
				filtreVia4ComunsPerOrgan(permisosPerExpedients, prefix),
				filtreVia5Grups(permisosPerExpedients, prefix));
	}

	/** VIA 1: permís sobre el procediment. */
	public Filter filtreVia1Procediments(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreVia1Procediments(permisosPerExpedients, "");
	}

	public Filter filtreVia1Procediments(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		/** (:esNullIdsMetaExpedientsPermesos = false and (e.metaExpedient.id in (:idsMetaExpedientsPermesos0))) " */
		Filter filtreMetaExpedientsPermesos = null;
		String procedimentId = prefix + ExpedientResource.Fields.metaExpedient + ".id";
		List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsMetaExpedientsPermesos());
		if (permesosClausulesIn!=null) {
			for (String aux: permesosClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreMetaExpedientsPermesos = FilterBuilder.or(filtreMetaExpedientsPermesos, Filter.parse(procedimentId + " IN (" + aux + ")"));
				}
			}
		}
		return filtreMetaExpedientsPermesos;
	}

	/**
	 * VIA 2: òrgan de capçalera i descendents (només admin d'òrgan i dissenyador).
	 *
	 * (:esNullIdsOrgansPermesos = false and (e.organGestor.id in (:idsOrgansPermesos0)))
	 */
	public Filter filtreVia2Organs(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreVia2Organs(permisosPerExpedients, "");
	}

	public Filter filtreVia2Organs(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		//Organs gestors permesos (nomes admin organ/dissenyador): Organ actual capçalera + fills.
		//idsOrgansPermesos ja ve expandit amb TOTS els descendents (getIdsOrgansFills, recursiu). Com que
		//organpare(E) = {organ directe + ancestres}, navegar la col·leccio per veure si un ancestre es permes
		//es EQUIVALENT a comprovar directament si l'organ de l'expedient hi es: (∃ ancestre ∈ L) ⟺ (organ ∈ L)
		//quan L es downward-closed. Per tant comprovem l'organ directe (to-one sobre l'arrel), evitant el exists()
		//correlat: pla set-based, usa l'index IPA_EXPEDIENT_ORGAN_FK_I i es immune a organpare buida (organGestor
		//es NOT NULL, no navega col·leccio ni descarta files germanes de l'OR).
		return filtreOrganGestor(permisosPerExpedients, prefix);
	}

	/** VIA 3: permís sobre la parella procediment-òrgan (només usuaris tothom). */
	public Filter filtreVia3ParellesProcedimentOrgan(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreVia3ParellesProcedimentOrgan(permisosPerExpedients, "");
	}

	public Filter filtreVia3ParellesProcedimentOrgan(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		//MetaExpedientOrganPairsPermesos (nomes usuaris tothom): ExtendedPermission.READ --> MetaExpedientOrganGestorEntity.class
		//Permisos que s'han donat sobre procediments comuns, a on es pot seleccionar organ gestor.
		/** (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) */
		Filter filtreMetaExpedientOrganPairsPermesos = null;
		String campMetaExpOrganId = prefix + ExpedientResource.Fields.metaexpedientOrganGestorPares + ".id";
		List<String> organsMetaExpClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos());
		if (organsMetaExpClausulesIn!=null) {
			for (String aux: organsMetaExpClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					//Aquesta via matcheja per id de la parella (procediment,organ) —meogp.id—, no per l'organ,
					//aixi que NO es reduible a l'organ directe de l'expedient com les vies 2/4. Es manté el exists(...)
					//correlat (immune al tipus de join sobre la col·leccio, evita que un organpare buit descarti files
					//germanes de l'OR). Pendent d'analitzar per separat.
					filtreMetaExpedientOrganPairsPermesos = FilterBuilder.or(filtreMetaExpedientOrganPairsPermesos, Filter.parse("exists(" + campMetaExpOrganId + " IN (" + aux + "))"));
				}
			}
		}
		return filtreMetaExpedientOrganPairsPermesos;
	}

	/** VIA 4: procediments comuns per òrgan (COMU + READ sobre l'òrgan; només usuaris tothom). */
	public Filter filtreVia4ComunsPerOrgan(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreVia4ComunsPerOrgan(permisosPerExpedients, "");
	}

	public Filter filtreVia4ComunsPerOrgan(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		//OrgansAmbProcedimentsComunsPermesos (nomes usuaris tothom): ExtendedPermission.COMU + ExtendedPermission.READ --> OrganGestorEntity.class
		//Permisos que s'han donat sobre OrganGestor
		/** (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos)
		 * 	and e.metaExpedient.id in (:idsProcedimentsComuns)) */
		//exists(...) correlat sobre la col·leccio, IDENTIC al predicat de la query JSP.
		//NO es reduible a l'organ directe de l'expedient amb una llista expandida (getIdsOrgansFills): aquella llista
		//es calcula sobre l'organigrama de VIGENTS (CacheHelper.findOrganigramaByEntitat nomes carrega estat='V' i talla
		//el subarbre al primer organ no vigent), mentre que organpare materialitza la cadena d'ancestres de BD del dia
		//de la creacio, sense filtrar per estat. Amb un organ intermedi extingit -cas real despres d'una sincronitzacio
		//DIR3, on nomes els expedients OBERTS es reassignen al successor (OrganGestorHelper.actualitzarExpedientsOberts
		//AmbOrgansObsolets)- la llista expandida perd l'expedient i el JSP no. Verificat en local: extingint un organ
		//intermedi amb les dues taules congruents, REACT deixava de mostrar l'expedient i JSP el seguia mostrant.
		Filter filtreOrgansAmbProcedimentsComunsPermesos = null;
		String campMetaExpOrganComuId = prefix + ExpedientResource.Fields.metaexpedientOrganGestorPares + "." + MetaExpedientOrganGestorResource.Fields.organGestor + ".id";
		List<String> organsMetaExpComunsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsOrgansAmbProcedimentsComunsPermesos());
		if (organsMetaExpComunsClausulesIn!=null) {
			for (String aux: organsMetaExpComunsClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					//exists(...) nomes sobre la part d'organ (col·leccio). El "metaExpedient.id IN (...)" de mes avall
					//es to-one sobre l'arrel i es queda fora, combinat amb AND com fins ara.
					filtreOrgansAmbProcedimentsComunsPermesos = FilterBuilder.or(filtreOrgansAmbProcedimentsComunsPermesos, Filter.parse("exists(" + campMetaExpOrganComuId + " IN (" + aux + "))"));
				}
			}

			Filter filtreIdsProcedimentsComuns = null;
			String campMetaExpId = prefix + ExpedientResource.Fields.metaExpedient + ".id";
			List<String> idsProcedimentsComunsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsProcedimentsComuns());
			if (idsProcedimentsComunsClausulesIn!=null) {
				for (String aux: idsProcedimentsComunsClausulesIn) {
					if (aux != null && !aux.isEmpty()) {
						filtreIdsProcedimentsComuns = FilterBuilder.or(filtreIdsProcedimentsComuns, Filter.parse(campMetaExpId + " IN (" + aux + ")"));
					}
				}
			}

			if (filtreOrgansAmbProcedimentsComunsPermesos!=null) {
				// La VIA 4 nomes dona acces si l'expedient pertany a un procediment comu permes. Si no hi ha cap
				// procediment comu (filtreIdsProcedimentsComuns == null), la via no ha d'aportar resultats, igual que
				// fa la query JSP, on "e.metaExpedient.id in (:idsProcedimentsComuns)" amb llista buida no retorna res.
				// Sense aquest else, FilterBuilder.and(x, null) deixaria nomes el match d'organ i ampliaria el resultat.
				if (filtreIdsProcedimentsComuns!=null) {
					filtreOrgansAmbProcedimentsComunsPermesos = FilterBuilder.and(filtreOrgansAmbProcedimentsComunsPermesos, filtreIdsProcedimentsComuns);
				} else {
					filtreOrgansAmbProcedimentsComunsPermesos = null;
				}
			}
		}
		return filtreOrgansAmbProcedimentsComunsPermesos;
	}

	/**
	 * VIA 5: el grup de l'expedient dona accés, però només per procediments SENSE permís directe.
	 * Els procediments amb permisDirecte=true s'accedeixen exclusivament per la VIA 1 (permís directe real de
	 * l'usuari), validada a més per la restricció de permís directe.
	 */
	public Filter filtreVia5Grups(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreVia5Grups(permisosPerExpedients, "");
	}

	public Filter filtreVia5Grups(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		/** (:esNullIdsGrupsPermesos = false and e.grup.id in (:idsGrupsPermesos) and e.metaExpedient.permisDirecte = false) */
		Filter filtreGrupsAccess = null;
		String grupAccessId = prefix + ExpedientResource.Fields.grup + ".id";
		List<String> grupsAccessClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsGrupsPermesos());
		if (grupsAccessClausulesIn!=null) {
			for (String aux: grupsAccessClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreGrupsAccess = FilterBuilder.or(filtreGrupsAccess, Filter.parse(grupAccessId + " IN (" + aux + ")"));
				}
			}
		}
		if (filtreGrupsAccess != null) {
			String campPermisDirecte = prefix + ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.permisDirecte;
			filtreGrupsAccess = FilterBuilder.and(filtreGrupsAccess, Filter.parse(campPermisDirecte + "!true"));
		}
		return filtreGrupsAccess;
	}

	/**
	 * RESTRICCIÓ A: permís directe sobre el procediment (issue #1633). Només s'ha d'aplicar si
	 * {@link #isAplicaRestriccioPermisDirecte(String)}.
	 *
	 * Son permis directe sobre el procediment el permis sobre el MetaExpedient (VIA 1) i el permis sobre la
	 * parella procediment-organ (VIA 3, procediments comuns amb organ). NO ho es el permis sobre l'organ gestor
	 * (vies 2/4), que es precisament el cas que aquesta restriccio ha de tallar. Es el mateix criteri que aplica
	 * EntityComprovarHelper.comprovarPermisExpedient al detall de l'expedient. Amb les llistes buides el filtre
	 * NO es desactiva: nomes passen els procediments sense permisDirecte.
	 */
	public Filter filtreRestriccioPermisDirecte(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreRestriccioPermisDirecte(permisosPerExpedients, "");
	}

	public Filter filtreRestriccioPermisDirecte(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		String campPermisDir = prefix + ExpedientResource.Fields.metaExpedient + "." + MetaExpedientResource.Fields.permisDirecte;
		String procedimentId = prefix + ExpedientResource.Fields.metaExpedient + ".id";

		Filter filtreProcedimentPermisDirecte = Filter.parse(campPermisDir + "!true");
		Filter filtreProcediments = null;
		List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsMetaExpedientsPermesos());
		if (permesosClausulesIn!=null) {
			for (String aux: permesosClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreProcediments = FilterBuilder.or(filtreProcediments, Filter.parse(procedimentId + " IN (" + aux + ")"));
				}
			}
		}

		//Parelles procediment-organ permeses: compten com a permis directe sobre el procediment.
		//exists(...) correlat, igual que la VIA 3.
		String campParellaId = prefix + ExpedientResource.Fields.metaexpedientOrganGestorPares + ".id";
		Filter filtreParelles = null;
		List<String> parellesClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsMetaExpedientOrganPairsPermesos());
		if (parellesClausulesIn!=null) {
			for (String aux: parellesClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreParelles = FilterBuilder.or(filtreParelles, Filter.parse("exists(" + campParellaId + " IN (" + aux + "))"));
				}
			}
		}

		return FilterBuilder.or(
				filtreProcedimentPermisDirecte,
				FilterBuilder.or(filtreProcediments, filtreParelles));
	}

	/**
	 * RESTRICCIÓ B: òrgan directe de l'expedient dins dels òrgans permesos. Només es retorna si la llista
	 * d'òrgans permesos no és null (admin d'òrgan i dissenyador); en aquest cas fa la VIA 2 redundant.
	 */
	public Filter filtreRestriccioOrgans(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreRestriccioOrgans(permisosPerExpedients, "");
	}

	public Filter filtreRestriccioOrgans(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		return filtreOrganGestor(permisosPerExpedients, prefix);
	}

	/**
	 * RESTRICCIÓ C: grups. Només s'ha d'aplicar si {@link #isAplicaRestriccioGrups(String)}.
	 *
	 * (:noFiltreGrups = true or (e.grup is null or (:esNullIdsGrupsPermesos = false and e.grup.id in (:idsGrupsPermesos))))
	 */
	public Filter filtreRestriccioGrups(PermisosPerExpedientsDto permisosPerExpedients) {
		return filtreRestriccioGrups(permisosPerExpedients, "");
	}

	public Filter filtreRestriccioGrups(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		Filter filtreGrupsPermesos = null;
		String grupNull = prefix + ExpedientResource.Fields.grup + " IS NULL";
		String grupId = prefix + ExpedientResource.Fields.grup + ".id";
		List<String> grupsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsGrupsPermesos());
		if (grupsClausulesIn!=null && grupsClausulesIn.size()>0) {
			for (String aux: grupsClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreGrupsPermesos = FilterBuilder.or(filtreGrupsPermesos, Filter.parse(grupId + " IN (" + aux + ")"));
				}
			}
			filtreGrupsPermesos = FilterBuilder.or(Filter.parse(grupNull), filtreGrupsPermesos);
		} else {
			filtreGrupsPermesos = Filter.parse(grupNull);
		}
		return filtreGrupsPermesos;
	}

	/** organGestor.id IN (idsOrgansPermesos): compartit per la VIA 2 i la RESTRICCIÓ B. */
	private Filter filtreOrganGestor(PermisosPerExpedientsDto permisosPerExpedients, String prefix) {
		Filter filtreOrgansPermesos = null;
		String campOrganId = prefix + ExpedientResource.Fields.organGestor + ".id";
		List<String> organsClausulesIn = Utils.getIdsEnGruposMil(permisosPerExpedients.getIdsOrgansPermesos());
		if (organsClausulesIn!=null) {
			for (String aux: organsClausulesIn) {
				if (aux != null && !aux.isEmpty()) {
					filtreOrgansPermesos = FilterBuilder.or(filtreOrgansPermesos, Filter.parse(campOrganId + " IN (" + aux + ")"));
				}
			}
		}
		return filtreOrgansPermesos;
	}
}
