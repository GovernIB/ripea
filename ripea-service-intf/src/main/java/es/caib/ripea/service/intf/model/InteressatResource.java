package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.annotation.ResourceArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.EntregaPostalTipusEnum;
import es.caib.ripea.service.intf.dto.EntregaPostalViaTipusEnum;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
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
        quickFilterFields = { "documentNum", "nom", "llinatge1", "llinatge2", "raoSocial", "organCodi", "organNom" },
        descriptionField = "codiNom",
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = InteressatResource.FILTER_CODE,
                        formClass = InteressatResource.UnitatOrganitzativaFormFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_GRUPS_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_REPRESENTANT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_ADRESSA_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = InteressatResource.PERSPECTIVE_PROCEDIMENT_CODE),                
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = InteressatResource.ACTION_EXPORTAR_CODE,
                        formClass = InteressatResource.ExportInteressatsFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_IMPORTAR_CODE,
                        formClass = InteressatResource.ImportarInteressatsFormAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_GUARDAR_ARXIU,
                        formClass = NodeResource.MassiveAction.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_DELETE_INTERESSAT,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_DELETE_REPRESENTANT,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = InteressatResource.ACTION_GESTIONAR_GRUPS,
                        formClass = InteressatResource.GestionarGrupsFrom.class,
                        requiresId = true),
        }
)
public class InteressatResource extends BaseAuditableResource<Long> {

	public static final String PERSPECTIVE_GRUPS_CODE = "GRUPS";
    public static final String PERSPECTIVE_REPRESENTANT_CODE = "REPRESENTANT";
    public static final String PERSPECTIVE_ADRESSA_CODE = "ADRESSA";
    public static final String PERSPECTIVE_PROCEDIMENT_CODE = "PROCEDIMENT";
    public static final String ACTION_EXPORTAR_CODE  = "EXPORTAR";
    public static final String ACTION_IMPORTAR_CODE  = "IMPORTAR";
    public static final String ACTION_GUARDAR_ARXIU  = "GUARDAR_ARXIU";
    public static final String ACTION_DELETE_INTERESSAT  = "DELETE_INTERESSAT";
    public static final String ACTION_DELETE_REPRESENTANT  = "DELETE_REPRESENTANT";
    public static final String ACTION_GESTIONAR_GRUPS  = "GESTIONAR_GRUPS";
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
	@Size(max = 36)
    @ResourceField(onChangeActive = true)
	protected String documentNum;
	@Size(max = 4)
	@ResourceField(enumType = true)
	protected String pais = "724";
	@Size(max = 2)
	@ResourceField(enumType = true)
	protected String provincia = "07";
	@Size(max = 5)
	@ResourceField(enumType = true)
	protected String municipi = "407";
	@Size(max = 5)
	protected String codiPostal;
    @NotNull
	protected EntregaPostalTipusEnum adressaTipus = EntregaPostalTipusEnum.NACIONAL;
	protected EntregaPostalViaTipusEnum adressaTipusVia = EntregaPostalViaTipusEnum.CALLE;
	@Size(max = 250)
	protected String adresa;
	@Size(max = 5)
	protected String adressaNumCasa;
	@Size(max = 3)
	protected String adresaQualificador;
	@Size(max = 10)
	protected String adresaPuntKm;
	@Size(max = 10)
	protected String adresaApartatCorreus;
	@Size(max = 50)
	protected String adresaPortal;
	@Size(max = 50)
	protected String adresaEscala;
	@Size(max = 50)
	protected String adresaPlanta;
	@Size(max = 50)
	protected String adresaPorta;
	@Size(max = 50)
	protected String adresaBloc;
	@Size(max = 250)
	protected String adresaComplement;
	@Size(max = 255)
	protected String adresaPoblacio;
	
	@Size(max = 160)
	protected String email;
	@Size(min = 9, max = 20)
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

    // Importar
    @Transient private Boolean jaExistentExpedient;
    @Transient private Map<String, String> errors = new HashMap<>();

    //Dades per mostrar en lloc dels codis guardats en els camps principals
    @Transient private String paisNom;
    @Transient private String provinciaNom;
    @Transient private String municipiNom;
    @Transient private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    
    protected List<ResourceReference<InteressatGrupResource, Long>> grups;
    
    @Transient
	public String getCodiNom() {
    	return Utils.getCodiNom(this.tipus, this.documentNum, this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organNom);
    }

    @Transient
	public String getNomComplet() {
    	return Utils.getNomComplet(this.tipus,this.nom, this.llinatge1, this.llinatge2, this.raoSocial, this.organNom);
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
        private List<InteressatResource> interessatsFitxer;
        @NotNull
        @NotEmpty
        private List<InteressatResource> interessatsPerImportar;

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
        private String municipiUo;
        private String nif;
        private String nom;
        private boolean unitatArrel;
    }

    @Getter
    @Setter
    @FieldNameConstants
    public static class GestionarGrupsFrom implements Serializable {
        @NotNull
        private List<ResourceReference<InteressatGrupResource, Long>> grups;
    }
}