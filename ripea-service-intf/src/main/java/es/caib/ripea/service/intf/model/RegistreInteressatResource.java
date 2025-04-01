package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.ripea.service.intf.registre.RegistreInteressatTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "nom" }, descriptionField = "observacions")
public class RegistreInteressatResource extends BaseAuditableResource<Long> {

    private String adresa;
    private String canal;
    private String cp;
    private String documentNumero;
    private RegistreInteressatDocumentTipusEnum documentTipus;
    private String email;
    private String llinatge1;
    private String llinatge2;
    private String municipiCodi;
    private String nom;
    private String observacions;
    private String paisCodi;
    private String provinciaCodi;
    private String pais;
    private String provincia;
    private String municipi;
    private String raoSocial;
    private String telefon;
    private RegistreInteressatTipusEnum tipus;
    private String organCodi;

    private ResourceReference<RegistreInteressatResource, Long> representant;
    private ResourceReference<RegistreResource, Long> registre;

}
