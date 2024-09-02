package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class InteressatImportCommand {

    private Long expedientId;
    private String accio = "INTERESSATS"; //INTERESSATS = Recupera interessats, SAVE = Importa els interessats
    private MultipartFile fitxerInteressats;
    private List<InteressatPersonaFisicaDto> interessatsFisica;
    private List<InteressatPersonaJuridicaDto> interessatsJuridi;
    private List<InteressatAdministracioDto> interessatsAdmini;
    private boolean hasInteressats=false;

    public Long getExpedientId() {
        return expedientId;
    }

    public void setExpedientId(Long expedientId) {
        this.expedientId = expedientId;
    }

    public MultipartFile getFitxerInteressats() {
        return fitxerInteressats;
    }

    public void setFitxerInteressats(MultipartFile fitxerInteressats) {
        this.fitxerInteressats = fitxerInteressats;
    }

    public String getAccio() {
        return accio;
    }

    public void setAccio(String accio) {
        this.accio = accio;
    }

    public List<InteressatPersonaFisicaDto> getInteressatsFisica() {
        return interessatsFisica;
    }

    public void setInteressatsFisica(List<InteressatPersonaFisicaDto> interessatsFisica) {
        this.interessatsFisica = interessatsFisica;
    }

    public List<InteressatPersonaJuridicaDto> getInteressatsJuridi() {
        return interessatsJuridi;
    }

    public void setInteressatsJuridi(List<InteressatPersonaJuridicaDto> interessatsJuridi) {
        this.interessatsJuridi = interessatsJuridi;
    }

    public List<InteressatAdministracioDto> getInteressatsAdmini() {
        return interessatsAdmini;
    }

    public void setInteressatsAdmini(List<InteressatAdministracioDto> interessatsAdmini) {
        this.interessatsAdmini = interessatsAdmini;
    }

    public boolean isHasInteressats() {
        return ((this.interessatsFisica!=null && this.interessatsFisica.size()>0) ||
                (this.interessatsJuridi!=null && this.interessatsJuridi.size()>0) ||
                (this.interessatsAdmini!=null && this.interessatsAdmini.size()>0));
    }

    public void setHasInteressats(boolean hasInteressats) {
        this.hasInteressats = hasInteressats;
    }

    public void setInteressatsFromInteressatDto(List<InteressatDto> interessats, List<InteressatDto> listaActual) {
        List<InteressatPersonaFisicaDto> lf = new ArrayList<InteressatPersonaFisicaDto>();
        List<InteressatPersonaJuridicaDto> lj = new ArrayList<InteressatPersonaJuridicaDto>();
        List<InteressatAdministracioDto> la = new ArrayList<InteressatAdministracioDto>();
        if (interessats!=null) {
            for (InteressatDto interessat : interessats) {

                interessat.setJaExistentExpedient(dinsLlistaActual(interessat.getDocumentNum(), listaActual));

                if (interessat instanceof InteressatPersonaFisicaDto) {
                    lf.add((InteressatPersonaFisicaDto) interessat);
                } else if (interessat instanceof InteressatPersonaJuridicaDto) {
                    lj.add((InteressatPersonaJuridicaDto) interessat);
                } else {
                    la.add((InteressatAdministracioDto) interessat);
                }
            }
        }
        this.setInteressatsFisica(lf);
        this.setInteressatsJuridi(lj);
        this.setInteressatsAdmini(la);
    }

    private boolean dinsLlistaActual(String documentNum, List<InteressatDto> listaActual) {
        if (listaActual!=null) {
            for (InteressatDto interessat : listaActual) {
                if (documentNum.equals(interessat.getDocumentNum())) {
                    return true;
                }
            }
        }
        return false;
    }
}