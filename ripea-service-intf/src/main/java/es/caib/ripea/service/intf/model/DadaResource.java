package es.caib.ripea.service.intf.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "metaDada", "ordre", "valor" }, descriptionField = "metaDadaOrdreValor")
public class DadaResource extends BaseAuditableResource<Long> {

    protected String valor;
    protected Integer ordre;

    @NotNull
    private ResourceReference<MetaDadaResource, Long> metaDada;
    @NotNull
    protected ResourceReference<NodeResource, Long> node;

    @Transient private String text;
    @Transient private Date data;
    @Digits(integer = 13, fraction = 2)
    @Transient private Float importe;
    @Transient private Integer sencer;
    @Transient private Float flotant;
    @Transient private boolean boolea;
//    @Transient private ResourceReference<?, Long> domini;

    public String getValueByFieldName(MetaDadaTipusEnumDto fieldName) {
        switch (fieldName) {
            case TEXT:
                return this.text;
            case DATA:
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                return df.format(this.data);
            case IMPORT:
                return String.valueOf(this.importe);
            case SENCER:
                return String.valueOf(this.sencer);
            case FLOTANT:
                return String.valueOf(this.flotant);
            case BOOLEA:
                return String.valueOf(this.boolea);
//            case DOMINI:
//                return this.domini.getId().toString();
        }
        return null;
    }
    public void setValueByFieldName(MetaDadaTipusEnumDto fieldName, String value) {
        switch (fieldName) {
            case TEXT:
                this.text = String.valueOf(value);
                break;
            case DATA:
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    this.data = formatter.parse(value);
                }catch (ParseException e) {}
                break;
            case IMPORT:
                this.importe = Float.parseFloat(value);
                break;
            case SENCER:
                this.sencer = Integer.valueOf(value);
                break;
            case FLOTANT:
                this.flotant = Float.parseFloat(value);
                break;
            case BOOLEA:
                this.boolea = Boolean.parseBoolean(value);
                break;
//            case DOMINI:
//                this.DOMINI = ResourceReference.<?, Long>toResourceReference(Long.valueOf(value));
//                break;
        }
    }

    public String getMetaDadaOrdreValor(){
        return metaDada.getDescription() + " - Nº" + ordre + " ( " + valor + " )";
    }
}