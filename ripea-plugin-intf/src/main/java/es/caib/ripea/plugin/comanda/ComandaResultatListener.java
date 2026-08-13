package es.caib.ripea.plugin.comanda;

/**
 * Resultat d'una crida a comanda feta en segon pla.
 *
 * Les crides asíncrones de {@link ComandaCaibPlugin} no retornen res a qui les demana, així que és per aquí que el
 * resultat arriba a qui l'ha de registrar (el monitor d'integracions). Els mètodes s'executen al fil de segon pla,
 * un cop acabada la crida, i per tant no tenen ni el context de seguretat ni la transacció de la petició original:
 * qui els implementi ha de portar-hi tot el que necessiti.
 */
public interface ComandaResultatListener {

	/**
	 * @param tempsResposta mil·lisegons que ha trigat la crida.
	 */
	void onOk(long tempsResposta);

	/**
	 * @param tempsResposta mil·lisegons transcorreguts fins a l'error.
	 * @param excepcio error de la crida.
	 */
	void onError(long tempsResposta, Exception excepcio);
}
