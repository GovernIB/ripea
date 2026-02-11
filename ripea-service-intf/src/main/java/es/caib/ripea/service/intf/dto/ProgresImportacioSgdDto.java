package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class ProgresImportacioSgdDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer progres = 0;

    private Integer numOperacions = 0;

    private Integer numOperacionsRealitzades = 0;

    private boolean finished = false;
    private boolean error = false;
    private String errorMsg;

    private int interessatsImportats = 0;
    private int documentsImportats = 0;
    private int carpetesCreades = 0;

    private List<String> errorsDetall = new ArrayList<>();
    private List<ProgresInfo> info = new ArrayList<>();

    public void start(int totalOperacions) {
        this.numOperacions = totalOperacions;
        this.numOperacionsRealitzades = 0;
        this.progres = 0;
    }

    public void step(String text) {
        addInfo(text);
        incrementOperacions(1);
    }

    public void incrementOperacions(int num) {
        if (numOperacions == null || numOperacions == 0) return;
        this.numOperacionsRealitzades += num;
        this.progres = (int)
                ((this.numOperacionsRealitzades.doubleValue() / this.numOperacions.doubleValue()) * 100);
    }

    public void addInteressatImportat() {
        this.interessatsImportats++;
    }

    public void addDocumentImportat() {
        this.documentsImportats++;
    }

    public void addCarpetaCreada() {
        this.carpetesCreades++;
    }

    public void addError(String errorText) {
        this.errorsDetall.add(errorText);
        this.error = true;
        this.errorMsg = errorText;
    }

    public void done() {
        this.progres = 100;
        this.finished = true;
    }

    private void addInfo(String text) {
        this.info.add(new ProgresInfo(text));
    }

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor
    public static class ProgresInfo {
        private String text;
    }
    
}
