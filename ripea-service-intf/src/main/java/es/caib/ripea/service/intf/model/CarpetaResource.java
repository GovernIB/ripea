package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.resourcevalidation.RestriccioCarpetaValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        orderField = "ordreLong",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ContingutResource.PERSPECTIVE_PATH_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = CarpetaResource.PERSPECTIVE_RESPONSABLE_RESTRICCIO),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = CarpetaResource.PERSPECTIVE_RESTRICCIONS),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = CarpetaResource.ACTION_MODIFICAR_NOM,
                        formClass = CarpetaResource.ModificarFormAction.class,
                        requiresId = true),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = CarpetaResource.REPORT_EXPORTAR_INDEX_PDF,
                        requiresId = true),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = CarpetaResource.REPORT_EXPORTAR_INDEX_XLS,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = CarpetaResource.ACTION_MOURE_COPIAR,
                        formClass = CarpetaResource.MoureCopiarFormAction.class,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = CarpetaResource.ACTION_GUARDAR_ARXIU,
                        requiresId = true),                
        }
)
@RestriccioCarpetaValid
public class CarpetaResource extends ContingutResource {
	
    public static final String PERSPECTIVE_RESPONSABLE_RESTRICCIO 	= "RESPONSABLE_RESTRICCIO";
    public static final String PERSPECTIVE_RESTRICCIONS 			= "RESTRICCIONS";
	public static final String ACTION_MODIFICAR_NOM					= "MODIFICAR_NOM";
	public static final String ACTION_MOURE_COPIAR					= "MOURE_COPIAR";
	public static final String REPORT_EXPORTAR_INDEX_PDF 			= "EXPORTAR_INDEX_PDF";
	public static final String REPORT_EXPORTAR_INDEX_XLS 			= "EXPORTAR_INDEX_XLS";
	public static final String ACTION_GUARDAR_ARXIU 				= "GUARDAR_ARXIU";
	
	private ResourceReference<ExpedientResource, Long> expedientRelacionat;
	
	private Boolean restringida = false;

	private String motiuRestriccio;
	
    private ResourceReference<UsuariResource, String> responsableRestriccio;    

	private List<ResourceReference<UsuariResource, String>> restriccions;
	
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class ModificarFormAction implements Serializable {
    	@NotNull private String nom;
    }
    
    @Getter
    @Setter
    public static class MoureCopiarFormAction implements Serializable {
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;
        private ResourceReference<CarpetaResource, Long> carpeta;
        private String motiu;
        private String carpetaNova;
        @NotNull
        private Action action = Action.MOURE;

        public enum Action { MOURE, COPIAR }
    }
}