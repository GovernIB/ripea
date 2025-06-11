package es.caib.ripea.service.intf.model;

import java.util.Date;
import java.util.List;

import es.caib.ripea.service.intf.base.model.ResourceReference;
import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "identificador" }, descriptionField = "destiCodiINom")
public class RegistreResource extends BaseAuditableResource<Long> {

    private String aplicacioCodi;
    private String aplicacioVersio;
    private String assumpteCodiCodi;
    private String assumpteCodiDescripcio;
    private String assumpteTipusCodi;
    private String assumpteTipusDescripcio;
    private Date data;
    private String docFisicaCodi;
    private String docFisicaDescripcio;
    private String entitatCodi;
    private String entitatDescripcio;
    private String expedientNumero;
    private String exposa;
    private String extracte;
    private String procedimentCodi;
    private String identificador;
    private String idiomaCodi;
    private String idiomaDescripcio;
    private String llibreCodi;
    private String llibreDescripcio;
    private String observacions;
    private String oficinaCodi;
    private String oficinaDescripcio;
    private Date origenData;
    private String origenRegistreNumero;
    private String refExterna;
    private String solicita;
    private String transportNumero;
    private String transportTipusCodi;
    private String transportTipusDescripcio;
    private String usuariCodi;
    private String usuariNom;
    private String destiCodi;
    private String destiDescripcio;
    private String justificantArxiuUuid;

    private List<ResourceReference<RegistreInteressatResource, Long>> interessats;
    
    @Transient private RegistreAnnexResource justificant;
    
    public String getDestiCodiINom() {
        return destiCodi + " - " + destiDescripcio;
    }
}