/**
 * 
 */
package es.caib.ripea.service.helper;

import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Mètodes per a la generació d'un fitxer CSV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CsvHelper {

	private static final char SEPARADOR_DEFAULT = ',';

	public void afegirLinia(
			StringBuilder sb,
			String[] valors) throws IOException {
		afegirLinia(sb, valors, ' ', ' ');
	}
	public void afegirLinia(
			StringBuilder sb,
			String[] valors,
			char separador) throws IOException {
		afegirLinia(sb, valors, separador, ' ');
	}
	public void afegirLinia(
			StringBuilder sb,
			String[] valors,
			char separador,
			char cometa) throws IOException {
        if (separador == ' ') {
        	separador = SEPARADOR_DEFAULT;
        }
        boolean first = true;
        for (String valor: valors) {
            if (!first) {
                sb.append(separador);
            }
            if (cometa == ' ') {
                sb.append(seguirFormatCsv(valor));
            } else {
                sb.append(cometa).append(seguirFormatCsv(valor)).append(cometa);
            }
            first = false;
        }
        sb.append("\n");
	}



	private static String seguirFormatCsv(String valor) {
		if (valor == null) {
			return "";
		} else {
	        String result = valor;
	        if (result.contains("\"")) {
	            result = result.replace("\"", "\"\"");
	        }
	        return result;
		}
    }

}
