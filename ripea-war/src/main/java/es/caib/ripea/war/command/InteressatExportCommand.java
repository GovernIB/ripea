package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.List;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;

public class InteressatExportCommand {

	private Long expedientId;
    private List<InteressatPersonaFisicaDto> interessatsFisica = new ArrayList<InteressatPersonaFisicaDto>();
    private List<InteressatPersonaJuridicaDto> interessatsJuridi = new ArrayList<InteressatPersonaJuridicaDto>();
    private List<InteressatAdministracioDto> interessatsAdmini = new ArrayList<InteressatAdministracioDto>();
    @SuppressWarnings("unused")
	private boolean hasInteressats=false;
    
    
    public Long getExpedientId() {
        return expedientId;
    }

    public void setExpedientId(Long expedientId) {
        this.expedientId = expedientId;
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
    
    public void setInteressatsFromInteressatDto(List<InteressatDto> interessats) {
        List<InteressatPersonaFisicaDto> lf = new ArrayList<InteressatPersonaFisicaDto>();
        List<InteressatPersonaJuridicaDto> lj = new ArrayList<InteressatPersonaJuridicaDto>();
        List<InteressatAdministracioDto> la = new ArrayList<InteressatAdministracioDto>();
        if (interessats!=null) {
            for (InteressatDto interessat : interessats) {
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
    
    public List<Long> getSeleccionats() {
    	List<Long> seleccs = new ArrayList<Long>();
    	for (InteressatPersonaFisicaDto aux: interessatsFisica) {
    		if (aux.isExporta()) { seleccs.add(aux.getId()); }
    	}
    	for (InteressatPersonaJuridicaDto aux: interessatsJuridi) {
    		if (aux.isExporta()) { seleccs.add(aux.getId()); }
    	}
    	for (InteressatAdministracioDto aux: interessatsAdmini) {
    		if (aux.isExporta()) { seleccs.add(aux.getId()); }
    	}
    	return seleccs;
    }
}