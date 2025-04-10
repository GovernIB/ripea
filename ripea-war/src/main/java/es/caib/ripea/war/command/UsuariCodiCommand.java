package es.caib.ripea.war.command;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuariCodiCommand implements Serializable {
	private static final long serialVersionUID = -1425626790043404542L;
 	private String usuarisBatch;
 	private String resultat;
}