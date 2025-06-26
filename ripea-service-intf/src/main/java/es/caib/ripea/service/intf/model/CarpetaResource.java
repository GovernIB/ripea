package es.caib.ripea.service.intf.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.DocumentResource.MoureFormAction.Action;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ContingutResource.PERSPECTIVE_PATH_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = CarpetaResource.ACTION_MODIFICAR_NOM,
                        formClass = CarpetaResource.ModificarFormAction.class,
                        requiresId = true),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = CarpetaResource.ACTION_EXPORTAR_INDEX_PDF,
                        requiresId = true),
				@ResourceConfigArtifact(
						type = ResourceArtifactType.REPORT,
						code = CarpetaResource.ACTION_EXPORTAR_INDEX_XLS,
                        requiresId = true),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = CarpetaResource.ACTION_MOURE_COPIAR,
                        formClass = CarpetaResource.MoureCopiarFormAction.class,
                        requiresId = true),		
        }
)
public class CarpetaResource extends ContingutResource {
	
	public static final String ACTION_MODIFICAR_NOM			= "MODIFICAR_NOM";
	public static final String ACTION_MOURE_COPIAR			= "MOURE_COPIAR";
	public static final String ACTION_EXPORTAR_INDEX_PDF	= "EXPORTAR_INDEX_PDF";
	public static final String ACTION_EXPORTAR_INDEX_XLS	= "EXPORTAR_INDEX_XLS";
	
	private ResourceReference<ExpedientResource, Long> expedientRelacionat;
	
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
        @NotNull
        private Action action = Action.MOURE;

        public enum Action { MOURE, COPIAR }
    }
}