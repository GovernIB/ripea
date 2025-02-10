package es.caib.ripea.service.intf.dto.historic;

public enum HistoricMetriquesEnumDto {
	EXPEDIENTS_CREATS,
	EXPEDIENTS_CREATS_ACUM,
//	EXPEDIENTS_OBERTS,
//	EXPEDIENTS_OBERTS_ACUM,
	EXPEDIENTS_TANCATS,
	EXPEDIENTS_TANCATS_ACUM,
//	EXPEDIENTS_AMB_ALERTES,
//	EXPEDIENTS_AMB_ERRORS_VALID,
//	DOCUMENTS_PENDENTS_SIGNAR,
	DOCUMENTS_SIGNATS,
//	DOCUMENTS_PENDENTS_NOTIFICAR,
	DOCUMENTS_NOTIFICATS,
	TASQUES_TRAMITADES;
	
	public Long getValue(HistoricDto historic) {
		Long value = 0L;
		switch (this) {
		case EXPEDIENTS_CREATS:
			value = historic.getNumExpedientsCreats();
			break;
		case EXPEDIENTS_CREATS_ACUM:
			value = historic.getNumExpedientsCreatsTotal();
			break;
//		case EXPEDIENTS_OBERTS:
//			value = historic.getNumExpedientsOberts();
//			break;
//		case EXPEDIENTS_OBERTS_ACUM:
//			value = historic.getNumExpedientsObertsTotal();
//			break;
		case EXPEDIENTS_TANCATS:
			value = historic.getNumExpedientsTancats();
			break;
		case EXPEDIENTS_TANCATS_ACUM:
			value = historic.getNumExpedientsTancatsTotal();
			break;
//		case EXPEDIENTS_AMB_ALERTES:
//			value = historic.getNumExpedientsAmbAlertes();
//			break;
//		case EXPEDIENTS_AMB_ERRORS_VALID:
//			value = historic.getNumExpedientsAmbErrorsValidacio();
//			break;
//		case DOCUMENTS_PENDENTS_SIGNAR:
//			value = historic.getNumDocsPendentsSignar();
//			break;
		case DOCUMENTS_SIGNATS:
			value = ((HistoricExpedientDto) historic).getNumDocsSignats();
			break;
//		case DOCUMENTS_PENDENTS_NOTIFICAR:
//			value = historic.getNumDocsPendentsNotificar();
//			break;
		case DOCUMENTS_NOTIFICATS:
			value = ((HistoricExpedientDto) historic).getNumDocsNotificats();
			break;	
		case TASQUES_TRAMITADES:
			value = ((HistoricUsuariDto) historic).getNumTasquesTramitades();
			break;			
		default:
			break;
		}
		return value;		
	}
	
	public String toString() {
		String value = "";
		switch (this) {
		case EXPEDIENTS_CREATS:
			value = "Nombre d'expedients creats";
			break;
		case EXPEDIENTS_CREATS_ACUM:
			value = "Nombre d'expedients creats acumulats";
			break;
//		case EXPEDIENTS_OBERTS:
//			value = historic.getNumExpedientsOberts();
//			break;
//		case EXPEDIENTS_OBERTS_ACUM:
//			value = historic.getNumExpedientsObertsTotal();
//			break;
		case EXPEDIENTS_TANCATS:
			value = "Nombre d'expedients tancats";
			break;
		case EXPEDIENTS_TANCATS_ACUM:
			value = "Nombre d'expedients tancats acumulats";
			break;
//		case EXPEDIENTS_AMB_ALERTES:
//			value = historic.getNumExpedientsAmbAlertes();
//			break;
//		case EXPEDIENTS_AMB_ERRORS_VALID:
//			value = historic.getNumExpedientsAmbErrorsValidacio();
//			break;
//		case DOCUMENTS_PENDENTS_SIGNAR:
//			value = historic.getNumDocsPendentsSignar();
//			break;
		case DOCUMENTS_SIGNATS:
			value = "Nombre de documents signats";
			break;
//		case DOCUMENTS_PENDENTS_NOTIFICAR:
//			value = historic.getNumDocsPendentsNotificar();
//			break;
		case DOCUMENTS_NOTIFICATS:
			value = "Nombre de documents notificats";
			break;		
		case TASQUES_TRAMITADES:
			value = "Nombre de tasques tramitades";
			break;
		default:
			break;
		}
		return value;		
	}
}
