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
		})
public class UsuariResource extends BaseResource<String> {

	private static final long serialVersionUID = -3198881636735062393L;

	// Manteniment d'usuaris i visor de permisos (només superusuari)
	public static final String ACTION_BAIXA = "BAIXA";
	public static final String ACTION_ALTA = "ALTA";
	public static final String ACTION_REVOCAR_PERMIS = "REVOCAR_PERMIS";
	public static final String REPORT_PERMISOS_RESUM = "PERMISOS_RESUM";
	public static final String REPORT_PERMISOS_DETALL = "PERMISOS_DETALL";

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
}
