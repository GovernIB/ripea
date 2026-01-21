package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
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
    public static final String ACTION_CHANGE_REVISIO_CODE	= "CHANGE_REVISIO";
    public static final String ACTION_VINCULAR_GRUP_CODE	= "VINCULAR_GRUP";
    public static final String ACTION_IMPORT_ROLSAC_CODE	= "IMPORT_ROLSAC";
    public static final String ACTION_IMPORT_FITXER_CODE	= "IMPORT_FITXER";
    public static final String REPORT_EXPORT_JSON 			= "REPORT_EXPORT_JSON";

	@Size(max = 64)
	private String codiPropi;
	@NotNull
	@ResourceField(onChangeActive = true)
	private TipusClassificacioEnumDto tipusClassificacio;
	@NotNull
	@Size(max = 30)
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

	private ResourceReference<MetaExpedientResource, Long> pare;
	@ResourceField(onChangeActive = true)
	private ResourceReference<OrganGestorResource, Long> organGestor;
	private ResourceReference<GrupResource, Long> grupPerDefecte;

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
    public static class VincularGrupFormAction implements Serializable {
		private static final long serialVersionUID = 3621908428141475760L;
		private ResourceReference<GrupResource, Long> grup;
    	private ResourceReference<OrganGestorResource, Long> organGestor;
    	private boolean perDefecte;
    }
    
    @Getter
    @Setter
    public static class ImportarRolsacFormAction implements Serializable {
		private static final long serialVersionUID = 2247344473674559400L;
		@NotEmpty
    	private String codiSia;
    }
    
    @Getter
    @Setter
    public static class ImportarFitxerFormAction extends MetaExpedientResource {

		private static final long serialVersionUID = -7203619944391181991L;

		@NotEmpty
		@ResourceField(onChangeActive = true)
    	private FileReference importJson;

        private List<ResourceReference<MetaDocumentResource, Long>> metaDocuments = new ArrayList<>();
        private List<ResourceReference<MetaDadaResource, Long>> metaDades = new ArrayList<>();
//        private List<ResourceReference<MetaExpedientEstatResource, Long>> estats = new ArrayList<>(); //Ya esta en el padre
        private List<ResourceReference<MetaExpedientTascaResource, Long>> tasques = new ArrayList<>();
//        private List<ResourceReference<GrupResource, Long>> grups = new ArrayList<>(); //No se importan grupos
        private List<ResourceReference<MetaExpedientCarpetaResource, Long>> carpetes = new ArrayList<>();
    }
    
    private static final long serialVersionUID = -7526532893601431955L;
}