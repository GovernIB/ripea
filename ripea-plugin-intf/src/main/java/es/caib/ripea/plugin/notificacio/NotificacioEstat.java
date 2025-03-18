package es.caib.ripea.plugin.notificacio;
import java.io.Serializable;
public enum NotificacioEstat implements Serializable {
	PENDENT, 
	ENVIADA,
	REGISTRADA, 
	FINALITZADA, 
	PROCESSADA,
	ENVIADA_AMB_ERRORS,
	FINALITZADA_AMB_ERRORS
}