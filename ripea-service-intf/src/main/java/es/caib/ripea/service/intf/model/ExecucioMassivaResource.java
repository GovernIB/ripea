package es.caib.ripea.service.intf.model;
import java.util.Date;

import org.springframework.data.annotation.Transient;

import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.service.intf.dto.FileNameOption;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class ExecucioMassivaResource extends BaseAuditableResource<Long> {

	private static final long serialVersionUID = -316479369072429836L;
	
    private ExecucioMassivaTipusDto tipus;
    private Date dataInici;
    private Date dataFi;
    //	Paràmetres generació de ZIP
    private Boolean carpetes;
    private Boolean versioImprimible;
    private FileNameOption nomFitxer;
    //	Paràmetres enviament portafirmes
    private String motiu;
    private PortafirmesPrioritatEnumDto prioritat = PortafirmesPrioritatEnumDto.NORMAL;
    private Date dataCaducitat;
    private String[] portafirmesResponsables;
    private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
    private String portafirmesFluxId;
    private String portafirmesTransaccioId;
    private Boolean enviarCorreu;
    private String rolActual;
    private Boolean portafirmesAvisFirmaParcial;
    private Boolean portafirmesFirmaParcial;
    private String documentNom;

    @Transient
    private int finalitzades;
    @Transient
    private int errors;
    @Transient
    private int pendents;
    @Transient
    private int cancelats;

    @Transient
    private double executades;
}