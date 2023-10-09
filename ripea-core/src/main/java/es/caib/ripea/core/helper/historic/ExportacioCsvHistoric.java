package es.caib.ripea.core.helper.historic;

import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricMetriquesEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.helper.CsvHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class ExportacioCsvHistoric {

	private HistoricMetriquesEnumDto[] metriques = new HistoricMetriquesEnumDto[] {
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_CREATS_ACUM,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS,
			HistoricMetriquesEnumDto.EXPEDIENTS_TANCATS_ACUM };
	
	
	@Autowired
	private CsvHelper csvHelper;
	
	public ExportacioCsvHistoric() {
		super();
	}
	
	public byte[] convertDadesEntitat(List<HistoricExpedientDto> dades, HistoricTipusEnumDto tipusAgrupament) throws IOException {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
		
		StringBuilder sb = new StringBuilder();
		int i;
		String[] columnes;

		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			columnes = new String[5];
			i = 1;
			columnes[0] = "Date";
			
		} else {
			columnes = new String[6];
			i = 2;
			columnes[0] = "Any";
			columnes[1] = "Mes";
		}
		
		for (HistoricMetriquesEnumDto metricEnum : metriques) {
			columnes[i] = metricEnum.toString();
			i++;
		}
		csvHelper.afegirLinia(sb, columnes, ';');
		for (HistoricExpedientDto dada : dades) {
			
			int colNum = 1;
			String[] fila;
			
			if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
				fila = new String[5];
				colNum = 1;
				fila[0] = dateFormat.format(dada.getData());
			} else {
				fila = new String[6];
				colNum = 2;
				fila[0] = dada.getAny();
				fila[1] = dada.getMesNom();
			}
			
			for (HistoricMetriquesEnumDto metricEnum : metriques) {
				fila[colNum] = metricEnum.getValue(dada).toString();
				colNum++;
			}
			csvHelper.afegirLinia(sb, fila, ';');
		}
		return sb.toString().getBytes();

	}
	


}



