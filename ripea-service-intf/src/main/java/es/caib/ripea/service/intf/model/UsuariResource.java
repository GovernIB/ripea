package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { "codi", "nom", "nif" },
		descriptionField = "codiAndNom",
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UsuariResource.ACTION_BAIXA,
						formClass = UsuariResource.BaixaFormAction.class,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UsuariResource.ACTION_ALTA,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UsuariResource.ACTION_REVOCAR_PERMIS,
						formClass = UsuariResource.RevocarPermisFormAction.class,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = UsuariResource.REPORT_PERMISOS_RESUM,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = UsuariResource.REPORT_PERMISOS_DETALL,
						formClass = UsuariResource.PermisosDetallForm.class,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = UsuariResource.REPORT_SIMULAR_PERMISOS,
						formClass = UsuariResource.SimulacioPermisosForm.class,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UsuariResource.ACTION_REGENERAR_ORGANPARE,
						formClass = UsuariResource.RegenerarOrganpareFormAction.class,
						requiresId = true),
		})
public class UsuariResource extends BaseResource<String> {

	private static final long serialVersionUID = -3198881636735062393L;

	// Manteniment d'usuaris i visor de permisos (només superusuari)
	public static final String ACTION_BAIXA = "BAIXA";
	public static final String ACTION_ALTA = "ALTA";
	public static final String ACTION_REVOCAR_PERMIS = "REVOCAR_PERMIS";
	public static final String REPORT_PERMISOS_RESUM = "PERMISOS_RESUM";
	public static final String REPORT_PERMISOS_DETALL = "PERMISOS_DETALL";
	public static final String REPORT_SIMULAR_PERMISOS = "SIMULAR_PERMISOS";
	/** Regenera la cadena d'òrgans (organpare) de l'expedient simulat (des del simulador de permisos). */
	public static final String ACTION_REGENERAR_ORGANPARE = "REGENERAR_ORGANPARE";
	/** Named query dels selectors d'element del simulador: sense entitat actual ni ACL (només superusuari). */
	public static final String SIMULADOR_PERMISOS_NAMED_QUERY = "SIMULADOR_PERMISOS";

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 200)
	private String nom;
	@NotNull
	@Size(max = 9)
	private String nif;
	@Size(max = 200)
	private String email;
	@Size(max = 200)
	private String emailAlternatiu;
	private IdiomaEnumDto idioma;
	private boolean inicialitzat = false;
	@Size(max = 64)
	private String rolActual;
	private boolean informacioExpedientExpandit = false;
	private ContingutVistaEnumDto vistaActual;

    private TemaAplicacioEnum temaAplicacio;
    private MenuEstilEnum estilMenu;

    @ResourceField(enumType = true)
	private Long numElementsPagina;

    public String getNumElementsPagina() {
        return numElementsPagina != null ? numElementsPagina.toString() : null;
    }
    public void setNumElementsPagina(String value) {
        this.numElementsPagina = (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
    }

    private boolean rebreEmailsGlobal = true;
	private boolean rebreEmailsAgrupats = true;
	private boolean rebreAvisosNovesAnotacions = true;
	private boolean rebreEmailsCanviEstatRevisio = true;
	private boolean rebreEmailsAccioMassiva = true;
	private boolean rebreEmailsMencioComentari = true;
	private boolean expedientListDataDarrerEnviament = false;
	private boolean expedientListAgafatPer = true;
	private boolean expedientListInteressats = true;
	private boolean expedientListComentaris = true;
	private boolean expedientListGrup = false;

	private ResourceReference<MetaExpedientResource, Long> procediment;
	private ResourceReference<EntitatResource, Long> entitatPerDefecte;
	private ResourceReference<EntitatResource, Long> entitatActual;

	private boolean expedientExpandit = true;

    @Transient
    private List<String> rols;

    public String getCodiAndNom() {
    	return nom + " (" + codi +")";
    }

    public String getNomAndNif() {
    	return nom + " (" + Utils.nifMask(nif) +")";
    }

	private MoureDestiVistaEnumDto vistaMoureActual = MoureDestiVistaEnumDto.LLISTA;

	private InterficieUsuariEnumDto interficieUsuari = InterficieUsuariEnumDto.REACT;

	// Baixa lògica (només lectura: es modifiquen amb les accions BAIXA i ALTA)
	private boolean actiu = true;
	private Date baixaData;
	private String baixaUsuari;
	private String baixaMotiu;

	@Override
	public String getId() {
		return this.getCodi();
	}

	@Override
	public void setId(String id) {
		this.codi = id;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class BaixaFormAction implements Serializable {
		@NotNull
		@Size(max = 1024)
		private String motiu;
	}

	/** Revoca tots els permisos d'un SID sobre un objecte (la fila del visor de permisos). */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class RevocarPermisFormAction implements Serializable {
		@NotNull
		private AclSidResource.ClassType tipus;
		@NotNull
		private Long objectId;
		@NotNull
		private Long sidId;
	}

	/** Paràmetres del detall de permisos: els d'una entitat o bé els d'objectes que ja no existeixen. */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class PermisosDetallForm implements Serializable {
		private Long entitatId;
		private boolean orfes;
	}

	/** Resultat de l'informe PERMISOS_RESUM: entitats amb permisos de l'usuari (càrrega inicial del visor). */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class PermisosResum implements Serializable {
		/** Rols de l'usuari amb què s'han resolt els permisos per rol. */
		private List<String> rols;
		/** No s'han pogut consultar els rols de l'usuari: només es mostren els permisos directes. */
		private boolean rolsError;
		private List<PermisosEntitatResum> entitats;
		/** Nombre de permisos sobre objectes que ja no existeixen. */
		private int numOrfes;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class PermisosEntitatResum implements Serializable {
		private Long entitatId;
		private String entitatCodi;
		private String entitatNom;
		/** Permís sobre la mateixa entitat (directe o per rol). */
		private boolean administrador;
		private boolean administradorLectura;
		private boolean usuari;
		/** Nombre de permisos sobre objectes de l'entitat (òrgans, procediments, grups), sense comptar els de l'entitat. */
		private int numPermisos;
	}

	/** Fila del detall de permisos: els permisos d'un SID (l'usuari o un dels seus rols) sobre un objecte. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class PermisDetall implements Serializable {
		private String id;
		private AclSidResource.ClassType tipus;
		private Long objectId;
		private String objecteCodi;
		private String objecteNom;
		/** Òrgan gestor, només per als permisos de procediment per òrgan (MET_EXP_ORG). */
		private String organ;
		private Long sidId;
		private String sid;
		private PrincipalTipusEnumDto principal;
		private List<ExtendedPermissionEnum> permisos;
		/** Només els permisos directes de l'usuari (o qualsevol sobre un objecte inexistent) es poden revocar des del visor. */
		private boolean revocable;
	}

	// ===============================================================================================
	// Simulador de permisos (REPORT_SIMULAR_PERMISOS)
	// ===============================================================================================

	/** Recurs sobre el qual se simula la part de permisos del llistat REACT. */
	public enum SimulacioRecurs { EXPEDIENT, ANOTACIO }

	/** Resultat de cada comprovació del simulador. */
	public enum SimulacioEstat {
		/** La via concedeix o la restricció/requisit es compleix. */
		OK,
		/** La via no concedeix o la restricció/requisit no es compleix. */
		KO,
		/** No intervé amb el rol simulat. */
		NO_APLICA,
		/** Informatiu: no decideix per si sol però pot explicar el resultat. */
		AVIS
	}

	/**
	 * Paràmetres del simulador. L'entitat surt de l'element triat; si no se'n tria cap, s'ha d'indicar.
	 * L'òrgan només és obligatori per als rols que treballen amb òrgan (IPA_ORGAN_ADMIN, IPA_DISSENY).
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class SimulacioPermisosForm implements Serializable {
		@NotNull
		@ResourceField(enumType = true)
		private String rol;
		@NotNull
		private SimulacioRecurs recurs;
		@ResourceField(namedQueries = SIMULADOR_PERMISOS_NAMED_QUERY, descriptionField = "numeroINom")
		private ResourceReference<ExpedientResource, Long> expedient;
		@ResourceField(namedQueries = SIMULADOR_PERMISOS_NAMED_QUERY)
		private ResourceReference<ExpedientPeticioResource, Long> anotacio;
		/** Id de l'entitat (només sense element). */
		@ResourceField(enumType = true)
		private String entitat;
		/** Id de l'òrgan seleccionat a la capçalera (només IPA_ORGAN_ADMIN i IPA_DISSENY). */
		@ResourceField(enumType = true)
		private String organ;
	}

	/** Expedient del qual es regenera la cadena d'òrgans (organpare). */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class RegenerarOrganpareFormAction implements Serializable {
		@NotNull
		private Long expedientId;
	}

	/** Resultat del simulador. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class SimulacioPermisosResultat implements Serializable {
		private String usuariCodi;
		private String rol;
		private SimulacioRecurs recurs;
		private String entitatNom;
		private String organNom;
		/** Id de l'element simulat (null si se simula sobre tota l'entitat). */
		private Long elementId;
		/** Descripció de l'element simulat (null si se simula sobre tota l'entitat). */
		private String elementDescripcio;
		/** Procediment de l'element (buit si l'anotació no en té; null sense element). Es mostra un sol cop a la capçalera. */
		private String procedimentDescripcio;
		private boolean ambElement;
		/** Resultat de la consulta REAL del llistat amb la identitat de l'usuari: 0/1 amb element, total sense. */
		private long totalReal;
		/** Sense element: expedients o anotacions de l'entitat (sense la part de permisos). */
		private Long totalEntitat;
		/** Sense element: expedients o anotacions que concedeix alguna via, abans d'aplicar les restriccions. */
		private Long totalVies;
		/** Error de la consulta real (p. ex. sense accés a l'entitat). */
		private String errorConsulta;
		/** Amb element: el desglose per vies no quadra amb la consulta real (indica que el simulador s'ha de revisar). */
		private boolean discrepancia;
		private List<SimulacioComprovacio> requisits;
		/** Vies: basta que una concedeixi. */
		private List<SimulacioComprovacio> vies;
		/** Restriccions: s'han de complir totes les que apliquen. */
		private List<SimulacioComprovacio> restriccions;
	}

	/** Una comprovació (requisit, via o restricció) del simulador. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class SimulacioComprovacio implements Serializable {
		/** Codi estable de la comprovació (el front en tradueix el títol i l'explicació). */
		private String codi;
		private SimulacioEstat estat;
		/** Elements que hi passen: 0/1 amb element; nombre d'expedients o anotacions sense element. */
		private Long nombre;
		/** Nombre d'objectes amb permís que alimenten la via (procediments, òrgans, parelles, grups...). */
		private Integer nombreObjectes;
		/** Restriccions, sense element: elements que alguna via concedeix i que aquesta restricció exclou. */
		private Long exclosos;
		/** Paràmetres per a la traducció de l'explicació (noms d'objectes, permisos requerits...). */
		private Map<String, String> parametres;
		/**
		 * Variant del suggeriment quan el cas té una causa concreta (p. ex. SENSE_GRUP, NO_COMU, PERMIS_DIRECTE).
		 * Null: suggeriment genèric de la comprovació.
		 */
		private String suggerimentVariant;
		/**
		 * Permisos ACL de l'usuari o dels seus rols sobre els objectes que decideixen la comprovació. Amb element,
		 * només els objectes d'aquell element; sense element, els objectes que alimenten la via.
		 */
		private List<PermisDetall> permisos;
		/** Objectes als quals es refereixen els permisos (p. ex. GRUP_ANOTACIO, ORGANS_FALTANTS), per titular la llista. */
		private String permisosObjecte;
		/** Permisos que demana la comprovació (per destacar-los entre els que té l'usuari). */
		private List<ExtendedPermissionEnum> permisosRequerits;
		/** Cal tenir TOTS els permisos requerits (p. ex. COMU i READ); si és fals, en basta un. */
		private boolean permisosRequeritsTots;
	}
}
