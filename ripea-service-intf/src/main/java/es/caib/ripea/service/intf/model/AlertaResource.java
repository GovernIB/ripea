package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.AlertaEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = {"text"},
        descriptionField = "text",
        artifacts = {
        		@ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AlertaResource.ACTION_MASSIVE_LLEGIT_CODE,
                        formClass = ExpedientResource.MassiveAction.class),
        		@ResourceArtifact(
        				type = ResourceArtifactType.PERSPECTIVE,
        				code = AlertaResource.PERSPECTIVE_EXPEDIENT_CODE),
        		@ResourceArtifact(
        				type = ResourceArtifactType.FILTER,
        				code = AlertaResource.FILTER_CONSULTA_CODE,
        				formClass = AlertaResource.ConsultaFilter.class),
        }
)
public class AlertaResource extends BaseAuditableResource<Long> {

    public static final String ACTION_MASSIVE_LLEGIT_CODE = "LLEGIT";
    /** Omple les dades de l'expedient de l'alerta (número, títol i procediment). */
    public static final String PERSPECTIVE_EXPEDIENT_CODE = "EXPEDIENT";
    /** Filtre de la pantalla «Alertes d'expedients». */
    public static final String FILTER_CONSULTA_CODE = "CONSULTA_ALERTES";
    /**
     * Consulta «Alertes d'expedients»: només les alertes de l'expedient mateix (les que fan aparèixer la icona
     * del llistat d'expedients i l'avís del detall) i només dels expedients que l'usuari veu al llistat.
     */
    public static final String NAMED_QUERY_CONSULTA = "CONSULTA_ALERTES";

    private String text;
    private String error;
    private boolean llegida;

    private ResourceReference<ContingutResource, Long> contingut;

    // Dades de l'expedient de l'alerta (perspectiva EXPEDIENT)
    @Transient private String expedientNumero;
    @Transient private String expedientNom;
    @Transient private String procedimentNom;

    @Getter
    @Setter
    public static class ConsultaFilter implements Serializable {
        private AlertaEstatEnumDto estat;
        /** Número o títol de l'expedient. */
        private String expedient;
        private ResourceReference<MetaExpedientResource, Long> procediment;
        private String text;
        private Date dataInici;
        private Date dataFi;
    }

}
