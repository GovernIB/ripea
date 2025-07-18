package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.service.intf.dto.InteressatImportacioTipusDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.resourcevalidation.InteressatValid;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@InteressatValid(groups = {Resource.OnCreate.class, Resource.OnUpdate.class})
@ResourceConfig(
        quickFilterFields = { "documentNum", "nom" },
        descriptionField = "codiNom",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = InteressatResource.FILTER_CODE,
                        formClass = InteressatResource.UnitatOrganitzativaFormFilter.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_REPRESENTANT_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_ADRESSA_CODE),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = InteressatResource.ACTION_EXPORTAR_CODE,
                        formClass = InteressatResource.ExportInteressatsFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_IMPORTAR_CODE,
                        formClass = InteressatResource.ImportarInteressatsFormAction.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_GUARDAR_ARXIU,
                        requiresId = true),
        }
)
public class InteressatResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_REPRESENTANT_CODE = "REPRESENTANT";
    public static final String PERSPECTIVE_ADRESSA_CODE = "ADRESSA";
    public static final String ACTION_EXPORTAR_CODE  = "EXPORTAR";
    public static final String ACTION_IMPORTAR_CODE  = "IMPORTAR";
    public static final String ACTION_GUARDAR_ARXIU  = "GUARDAR_ARXIU";
    public static final String FILTER_CODE = "UNITAT_ORGANITZATIVA_FILTER";

	@NotNull
    @ResourceField(onChangeActive = true)
	protected InteressatTipusEnum tipus = InteressatTipusEnum.InteressatPersonaFisicaEntity;
	@Size(max = 30)
	protected String nom;
	@Size(max = 30)
	protected String llinatge1;
	@Size(max = 30)
	protected String llinatge2;
	@Size(max = 80)
	protected String raoSocial;

	@Size(max = 9)
	@ResourceField(enumType = true, onChangeActive = true)
	protected String organCodi;
	@Size(max = 256)
	protected String organNom;
	protected Boolean ambOficinaSir;
	
	@NotNull
	protected InteressatDocumentTipusEnumDto documentTipus = InteressatDocumentTipusEnumDto.NIF;
	@Size(max = 17)
    @ResourceField(onChangeActive = true)
	protected String documentNum;
	@Size(max = 4)
	@ResourceField(enumType = true)
	protected String pais;
	@Size(max = 2)
	@ResourceField(enumType = true)
	protected String provincia;
	@Size(max = 5)
	@ResourceField(enumType = true)
	protected String municipi;
	@Size(max = 160)
	protected String adresa;
	@Size(max = 5)
	protected String codiPostal;
	@Size(max = 160)
	protected String email;
	@Size(max = 20)
	protected String telefon;
	@Size(max = 160)
	protected String observacions;
    @NotNull
	protected InteressatIdiomaEnumDto preferenciaIdioma = InteressatIdiomaEnumDto.CA;
	@NotNull
	protected boolean notificacioAutoritzat;
	@NotNull
	protected boolean esRepresentant;
	protected Boolean entregaDeh;
	protected Boolean entregaDehObligat;
	protected Boolean incapacitat;
	protected boolean arxiuPropagat;
	protected Date arxiuIntentData;
	protected int arxiuReintents;
	
	@NotNull
	private ResourceReference<ExpedientResource, Long> expedient;
	private ResourceReference<InteressatResource, Long> representant;
    
	@Transient private InteressatResource representantInfo;
    @Transient private ResourceReference<InteressatResource, Long> representat;
    @Transient private boolean hasRepresentats;

    //Dades per mostrar en lloc dels codis guardats en els camps principals
    @Transient private String paisNom;
    @Transient private String provinciaNom;
    @Transient private String municipiNom;
    
    @Transient
	public String getCodiNom() {
    	return Utils.getCodiNom(this.tipus, this.documentNum, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
    }

    @Transient
	public String getNomComplet() {
    	return Utils.getNomComplet(this.tipus,this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organCodi);
	}

    @Getter
    @Setter
    @FieldNameConstants
    public static class ImportarInteressatsFormAction implements Serializable {
        @NotNull
    	private InteressatImportacioTipusDto tipusImportacio = InteressatImportacioTipusDto.JSON;
        @NotNull
        @ResourceField(onChangeActive = true)
        private FileReference fitxerJsonInteressats;
        private List<InteressatDto> interessatsFitxer;
        @NotNull
        @NotEmpty
        private List<InteressatDto> interessatsPerImportar;

        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;
    }

    @Getter
    @Setter
    @FieldNameConstants
    public static class ExportInteressatsFormAction extends NodeResource.MassiveAction {
        @NotNull
        private ResourceReference<ExpedientResource, Long> expedient;
    }

    @Getter
    @Setter
    @FieldNameConstants
    public static class UnitatOrganitzativaFormFilter implements Serializable {
        @ResourceField(enumType = true)
        private String nivell;
        @ResourceField(enumType = true)
        private String comunitatAutonoma;
        @ResourceField(enumType = true)
        private String provincia;
        @ResourceField(enumType = true)
        private String municipi;
        private String nif;
        private String nom;
        private boolean unitatArrel;
    }
    
//    public InteressatDto toDocumentDto() {
//    	InteressatDto resultat = null;
//    	switch (this.tipus) {
//		case InteressatPersonaFisicaEntity:
//			resultat = new InteressatPersonaFisicaDto();
//			break;
//		case InteressatPersonaJuridicaEntity:
//			resultat = new InteressatPersonaJuridicaDto();
//			break;
//		case InteressatAdministracioEntity:
//			resultat = new InteressatAdministracioDto();
//			break;
//    	}
//        return resultat;
//    }
}