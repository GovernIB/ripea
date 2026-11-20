package es.caib.ripea.service.resourcehelper.inici;

import java.util.List;
import java.util.function.Function;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;

/**
 * Llista «Expedients llestos per tancar més antics» de la pantalla d'inici: els mateixos expedients que el llistat de
 * l'acció massiva de tancament, però demanant directament els més antics, sense carregar tots els identificadors.
 */
public interface ExpedientsPerTancarRecurs {

	/**
	 * @param maxim nombre màxim d'expedients.
	 * @param fila  conversió de cada expedient, dins de la transacció.
	 * @return els expedients més antics (per data de creació) que l'usuari pot tancar amb l'acció massiva.
	 */
	<F> List<F> mesAnticsPerTancar(int maxim, Function<ExpedientResourceEntity, F> fila);

}
