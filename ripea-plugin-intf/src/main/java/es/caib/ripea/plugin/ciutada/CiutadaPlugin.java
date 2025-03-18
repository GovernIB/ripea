package es.caib.ripea.plugin.ciutada;

import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a la comunicació amb el ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CiutadaPlugin {

	/**
	 * Crea un nou expedient a la zona personal del ciutadà. Es verifica si la
	 * zona personal està creada i la crea si no hi està.
	 * 
	 * @param expedientIdentificador
	 * @param unitatAdministrativa
	 * @param identificadorProcedimiento
	 * @param idioma
	 * @param descripcio
	 * @param destinatari
	 * @param representat
	 * @param bantelNumeroEntrada
	 * @param avisosHabilitats
	 * @param avisEmail
	 * @param avisMobil
	 * @return les dades de l'expedient creat
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a crear l'expedient
	 */
	public CiutadaExpedientInformacio expedientCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String identificadorProcedimiento,
			String idioma,
			String descripcio,
			CiutadaPersona destinatari,
			CiutadaPersona representat,
			String bantelNumeroEntrada,
			boolean avisosHabilitats,
			String avisEmail,
			String avisMobil) throws SistemaExternException;

	/**
	 * Crea un nou avis per a un expedient de la zona personal del ciutadà.
	 * 
	 * @param expedientIdentificador
	 * @param unitatAdministrativa
	 * @param titol
	 * @param text
	 * @param textSms
	 * @param annexos
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a crear l'avis
	 */
	public void avisCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String titol,
			String text,
			String textSms,
			List<CiutadaDocument> annexos) throws SistemaExternException;

	/**
	 * Envia una notificació telemàtica al ciutadà.
	 * 
	 * @param expedientIdentificador
	 * @param unitatAdministrativa
	 * @param registreOficinaCodi
	 * @param registreOrganCodi
	 * @param destinatari
	 * @param representat
	 * @param assumpteTipus
	 * @param oficiTitol
	 * @param oficiText
	 * @param avisTitol
	 * @param avisText
	 * @param avisTextSms
	 * @param confirmarRecepcio
	 * @param annexos
	 * @return les dades de la notificació creada
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a crear la notificació
	 */
	public CiutadaNotificacioResultat notificacioCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String registreOficinaCodi,
			String registreOrganCodi,
			CiutadaPersona destinatari,
			CiutadaPersona representat,
			String assumpteTipus,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextSms,
			boolean confirmarRecepcio,
			List<CiutadaDocument> annexos) throws SistemaExternException;

	/**
	 * Obté l'estat d'una notificació telemàtica enviada al ciutadà.
	 * 
	 * @param registreNumero
	 * @return la informació sobre l'estat de la notificació
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a obtenir l'estat de la notificació
	 */
	public CiutadaNotificacioEstat notificacioObtenirJustificantRecepcio(
			String registreNumero) throws SistemaExternException;

}
