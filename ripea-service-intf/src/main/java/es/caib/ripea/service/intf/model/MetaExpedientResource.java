package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.dto.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
		quickFilterFields = { "codi", "nom" },
		descriptionField = "nomClassificacio",
        artifacts = {
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = MetaExpedientResource.PERSPECTIVE_AUDIT_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = MetaExpedientResource.PERSPECTIVE_COMMENTS_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = MetaExpedientResource.PERSPECTIVE_PERMISOS_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = MetaExpedientResource.PERSPECTIVE_ELEMENTS_CODE),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = MetaExpedientResource.PERSPECTIVE_ROLSAC_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = MetaExpedientResource.FILTER_REVISIO_CODE,
                        formClass = MetaExpedientResource.GestioRevisioFormFilter.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = MetaExpedientResource.FILTER_GESTIO_CODE,
                        formClass = MetaExpedientResource.GestioRevisioFormFilter.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_CHANGE_REVISIO_CODE,
                        formClass = MetaExpedientResource.RevisioChangeFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_VINCULAR_GRUP_CODE,
                        formClass = MetaExpedientResource.VincularGrupFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_DESVINCULAR_GRUP_CODE,
                        formClass = MetaExpedientResource.DesVincularGrupFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_TOGGLE_REGLA_CODE,
                        formClass = MetaExpedientResource.ToggleReglaRolsacFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_TOGGLE_GRUP_DEF_CODE,
                        formClass = MetaExpedientResource.ToggleGrupDefecteFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_CREAR_REGLA_CODE,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_CANVIAR_DISSENY_CODE,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_UPDATE_ROLSAC_CODE,
                        formClass = NodeResource.MassiveAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_IMPORT_ROLSAC_CODE,
                        formClass = MetaExpedientResource.ImportarRolsacFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaExpedientResource.ACTION_IMPORT_FITXER_CODE,
                        formClass = MetaExpedientResource.ImportarFitxerFormAction.class),                
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = MetaExpedientResource.REPORT_EXPORT_JSON,
                        requiresId = true),
        },
		defaultSortFields = { @ResourceConfig.ResourceSort(field = "nom", direction = Sort.Direction.ASC) }
)
public class MetaExpedientResource extends MetaNodeResource {

    public static final String FILTER_REVISIO_CODE 			= "FILTER_REVISIO";
    public static final String FILTER_GESTIO_CODE 			= "FILTER_GESTIO";
    public static final String PERSPECTIVE_AUDIT_CODE 		= "AUDITORIA";
    public static final String PERSPECTIVE_COMMENTS_CODE	= "COMENTARIS";
    public static final String PERSPECTIVE_PERMISOS_CODE	= "PERMISOS";
    public static final String PERSPECTIVE_ELEMENTS_CODE	= "ELEMENTS_COUNT";
    public static final String PERSPECTIVE_ROLSAC_CODE		= "REGLA_ROLSAC";
    public static final String ACTION_CHANGE_REVISIO_CODE	= "CHANGE_REVISIO";
    public static final String ACTION_VINCULAR_GRUP_CODE	= "VINCULAR_GRUP";
    public static final String ACTION_DESVINCULAR_GRUP_CODE	= "DESVINCULAR_GRUP";
    public static final String ACTION_TOGGLE_GRUP_DEF_CODE	= "TOGGLE_GRUP_DEF";
    public static final String ACTION_TOGGLE_REGLA_CODE		= "TOGGLE_REGLA_ROLSAC";
    public static final String ACTION_CREAR_REGLA_CODE		= "CREAR_REGLA_ROLSAC";
    public static final String ACTION_UPDATE_ROLSAC_CODE	= "UPDATE_ROLSAC";
    public static final String ACTION_CANVIAR_DISSENY_CODE	= "CANVIAR_DISSENY";
    public static final String ACTION_IMPORT_ROLSAC_CODE	= "IMPORT_ROLSAC";
    public static final String ACTION_IMPORT_FITXER_CODE	= "IMPORT_FITXER";
    public static final String REPORT_EXPORT_JSON 			= "REPORT_EXPORT_JSON";

	@Size(max = 64)
	private String codiPropi;
	@NotNull
	@ResourceField(onChangeActive = true)
	private TipusClassificacioEnumDto tipusClassificacio;
	@NotNull
	@Size(max = 46)
	@ResourceField(onChangeActive = true)
	private String classificacio;
	@Transient private String msgSiaRolsac;
	
	@NotNull
	@Size(max = 30)
	private String serieDocumental;
	@Size(max = 100)
	private String expressioNumero;
	private boolean notificacioActiva;
	private boolean permetMetadocsGenerals;
	private boolean gestioAmbGrupsActiva;
	private boolean permisDirecte = false;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Transient private MetaExpedientRevisioEstatEnumDto estatAnterior;
	@Size(max = 1024)
	private String revisioComentari;
	
	@Transient private boolean crearReglaDistribucio = true;
	private CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat;
	@Size(max = 1024)
	private String crearReglaDistribucioError;
	
	private boolean organNoSincronitzat;
	private boolean interessatObligatori;

    private TipusProcedimentServeiEnum tipusProcedimentServei = TipusProcedimentServeiEnum.PROCEDIMENT;

	private ResourceReference<MetaExpedientResource, Long> pare;
	@ResourceField(onChangeActive = true)
	private ResourceReference<OrganGestorResource, Long> organGestor;
	private ResourceReference<GrupResource, Long> grupPerDefecte;
	private ResourceReference<EntitatResource, Long> entitatPropia;

    @ResourceField(onChangeActive = true)
    @Transient private boolean procedimentComu;
	@Transient private List<ResourceReference<MetaExpedientEstatResource, Long>> estats;
	@Transient private List<ResourceReference<MetaExpedientOrganGestorResource, Long>> metaExpedientOrganGestors;

    @Transient private int numComentaris;
    @Transient private int numPermisos;
    @Transient private int numMetaDocument;
    @Transient private int numMetaDada;
    @Transient private int numEstat;
    @Transient private int numTasca;
    @Transient private int numGrup;
    @Transient private int numCarpetes;
    
    @Transient private ReglaDistribucioDto regla;

    public String getNomClassificacio() {
        return nom + " (" + classificacio +")";
    }

    @Getter
    @Setter
    public static class GestioRevisioFormFilter implements Serializable {
		private static final long serialVersionUID = -4055886032912956357L;
		private String codi;
        private String classificacio;
        private String nom;
        private MetaExpedientRevisioEstatEnumDto revisioEstat;
        private ResourceReference<OrganGestorResource, Long> organGestor;
        private Boolean actiu = true;
        private MetaExpedientAmbitEnumDto ambit;
        private boolean permisDirecte;
        private TipusProcedimentServeiEnum tipus;
    }
    
    @Getter
    @Setter
    public static class RevisioChangeFormAction implements Serializable {
		private static final long serialVersionUID = -973615025357818666L;
		private MetaExpedientRevisioEstatEnumDto revisioEstat;
        private String revisioComentari;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    public static class VincularGrupFormAction implements Serializable {
		private static final long serialVersionUID = 3621908428141475760L;
		@NotNull @ResourceField(onChangeActive = true)
        private ResourceReference<GrupResource, Long> grup;
    	private ResourceReference<OrganGestorResource, Long> organGestor;
    	private boolean perDefecte;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    public static class DesVincularGrupFormAction implements Serializable {
        private ResourceReference<GrupResource, Long> grup;
    }
    
    @Getter
    @Setter
    public static class ToggleReglaRolsacFormAction implements Serializable {
        private boolean activa;
    }
    
    @Getter
    @Setter
    public static class ToggleGrupDefecteFormAction implements Serializable {
        private Long grupId;
    }
    
    @Getter
    @Setter
    public static class ImportarRolsacFormAction implements Serializable {
		private static final long serialVersionUID = 2247344473674559400L;
		@NotNull @NotEmpty
    	private String codiSia;
    }
    
    @Getter
    @Setter
    @FieldNameConstants
    public static class ImportarFitxerFormAction extends MetaExpedientResource {

		private static final long serialVersionUID = -7203619944391181991L;

        @NotNull
		@ResourceField(onChangeActive = true)
    	private FileReference importJson;
		
		@ResourceField(onChangeActive = true)
		private ResourceReference<MetaExpedientResource, Long> procediment;

        private List<MetaDocumentResource> metaDocumentsImportats = new ArrayList<>();
        private List<MetaDadaResource> metaDadesImportats = new ArrayList<>();
        private List<MetaExpedientEstatResource> estatsImportats = new ArrayList<>();
        private List<MetaExpedientTascaResource> tasquesImportats = new ArrayList<>();
        private List<GrupResource> grupsImportats = new ArrayList<>();
        private List<MetaExpedientCarpetaResource> carpetesImportats = new ArrayList<>();

        @Transient private boolean importar;
        @Transient private ResourceReference<UsuariResource, String> responsable;
        @Transient private List<ResourceReference<UsuariResource, String>> portafirmesResponsables;
    }
    
    private static final long serialVersionUID = -7526532893601431955L;
}