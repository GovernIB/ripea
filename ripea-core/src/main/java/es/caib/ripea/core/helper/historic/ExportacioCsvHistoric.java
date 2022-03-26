package es.caib.ripea.core.helper.historic;

import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricMetriquesEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
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
		String[] columnes = new String[5];
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			columnes[0] = "Date";
		} else {
			columnes[0] = "Mes";
		}
		
		int i=1;
		for (HistoricMetriquesEnumDto metricEnum : metriques) {
			columnes[i] = metricEnum.toString();
			i++;
		}
		csvHelper.afegirLinia(sb, columnes, ';');
		for (HistoricExpedientDto dada : dades) {
			String[] fila = new String[5];
			
			if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
				fila[0] = dateFormat.format(dada.getData());
			} else {
				fila[0] = dada.getMesNom();
			}
			
			int colNum = 1;
			for (HistoricMetriquesEnumDto metricEnum : metriques) {
				fila[colNum] = metricEnum.getValue(dada).toString();
				colNum++;
			}
			csvHelper.afegirLinia(sb, fila, ';');
		}
		return sb.toString().getBytes();

	}
	


}



