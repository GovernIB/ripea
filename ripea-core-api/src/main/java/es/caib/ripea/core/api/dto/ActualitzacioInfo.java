package es.caib.ripea.core.api.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActualitzacioInfo {

    // Comú
    boolean hasError;
    boolean hasCanvis;
    boolean hasInfo;

    String infoClass;
    String infoTitol;
    String infoText;
    String errorText;

    // Organ
    boolean isOrgan;
    boolean isIsNew;
    String codiOrgan;
    OrganEstatEnumDto estatAntic;
    OrganEstatEnumDto estatNou;

    // Procediments
    boolean exist;
    boolean nomModificat;
    boolean descripcioModificada;
    boolean comuModificat;
    boolean organModificat;

    String codiSia;
    String nomAntic;
    String nomNou;
    String descripcioAntiga;
    String descripcioNova;
    boolean comuAntic;
    boolean comuNou;
    String organAntic;
    String organNou;

    public void setNomNou(String nomNou) {
        this.nomNou = nomNou;
        this.nomModificat = !Objects.equals(this.nomAntic, nomNou);
        if (this.nomModificat)
            this.hasCanvis = true;
    }

    public void setDescripcioNova(String descripcioNova) {
        this.descripcioNova = descripcioNova;
        this.descripcioModificada = !Objects.equals(this.descripcioAntiga, descripcioNova);
        if (this.descripcioModificada)
            this.hasCanvis = true;
    }

    public void setComuNou(boolean comuNou) {
        this.comuNou = comuNou;
        this.comuModificat = comuAntic != comuNou;
        if (this.comuModificat)
            this.hasCanvis = true;
    }

    public void setOrganNou(String organNou) {
        this.organNou = organNou;
        this.organModificat = !Objects.equals(this.organAntic, organNou);
        if (this.organModificat)
            this.hasCanvis = true;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
        this.hasInfo = true;
    }

    public boolean hasChange() {
        return nomModificat || descripcioModificada || comuModificat || organModificat;
    }
}
