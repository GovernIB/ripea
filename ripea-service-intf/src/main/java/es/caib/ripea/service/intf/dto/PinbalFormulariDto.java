package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Formulari de dades específiques d'un servei de PINBAL, construït amb els camps (getDadesEspecifiques) i l'arbre de
 * grups (getGrups) de l'API v2 de recobriment. El pinta la interfície REACT en el modal de nova consulta PINBAL.
 *
 * La secció arrel no té nom: conté els camps sense grup i, a continuació, els grups de primer nivell. Cada secció
 * conté primer els seus camps i després les seves subseccions, uns i altres ja ordenats. Els camps ocults
 * (visible=false) no hi són: el servidor hi posa el seu valor per defecte en enviar la petició.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
public class PinbalFormulariDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String serveiCodi;
	/** Nom del servei a RIPEA (IPA_PINBAL_SERVEI), per als títols del formulari. */
	private String serveiNom;
	private Seccio arrel;

	/** Tipus de camp del formulari: el tipus del camp a PINBAL, deduït de les propietats tipus i enumTipus de l'API. */
	public enum CampTipus {
		TEXT,
		NUMERIC,
		DATA,
		BOOLEA,
		DOC_IDENT,
		ENUM,
		PAIS,
		PROVINCIA,
		MUNICIPI_3,
		MUNICIPI_5,
		/** Text informatiu (el comentari del camp): no té valor i no s'envia. */
		ETIQUETA,
		/** Fitxer adjunt (ADJUNT_BINARI o ADJUNT_XML: l'API no els distingeix). S'envia en base64. */
		ADJUNT
	}

	/** Tipus de validació d'un camp a PINBAL (Validacio de l'API v2): només n'hi ha per a camps TEXT, NUMERIC i DATA. */
	public enum ValidacioTipus {
		/** El valor sencer ha de coincidir amb l'expressió regular (camps TEXT). */
		EXPRESSIO_REGULAR,
		/** Nombre enter igual o superior al mínim (camps NUMERIC). */
		NUM_MINIM,
		/** Nombre enter igual o inferior al màxim (camps NUMERIC). */
		NUM_MAXIM,
		/** Nombre enter entre el mínim i el màxim (camps NUMERIC). */
		NUM_RANG,
		/** Comparació de la data amb la d'un altre camp (camps DATA). */
		DATA_COMPARACIO,
		/** Comparació de la diferència entre la data i la d'un altre camp amb una durada (camps DATA). */
		DATA_DIFERENCIA
	}

	/** Operació de les validacions de dates: aquest camp (o la diferència) respecte de l'altre (o la durada). */
	public enum ValidacioOperacio {
		LT,
		LTE,
		GT,
		GTE,
		EQ,
		NEQ
	}

	/** Unitat de la durada de les validacions DATA_DIFERENCIA. */
	public enum ValidacioUnitat {
		DIES,
		MESOS,
		ANYS
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Seccio implements Serializable {
		private static final long serialVersionUID = 1L;
		/** Codi del grup a PINBAL: noms dels grups des de l'arrel separats per '/'. Null a la secció arrel. */
		private String codi;
		private String nom;
		private String ajuda;
		private List<Camp> camps = new ArrayList<>();
		private List<Seccio> subseccions = new ArrayList<>();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Camp implements Serializable {
		private static final long serialVersionUID = 1L;
		/** Path de la dada específica a PINBAL (DatosEspecificos/...): és la clau del valor a la petició. */
		private String codi;
		/** Text que es mostra al camp: l'etiqueta del camp a PINBAL o, si no en té, el nom o el codi. */
		private String text;
		private CampTipus tipus;
		private String comentari;
		private boolean obligatori;
		private boolean modificable;
		/**
		 * Valor amb què s'inicialitza el camp en el format del component del formulari (dates en ISO,
		 * yyyy-MM-dd'T'HH:mm:ss; booleans "true"/"false"): el valor per defecte de PINBAL o, si no n'hi ha, el de
		 * RIPEA per a províncies (07) i països (724).
		 */
		private String valorInicial;
		/** Per als municipis: codi del camp del formulari amb la província de la qual s'han de carregar. */
		private String campProvincia;
		/** Per als municipis sense camp de província al formulari: la província de la qual s'han de carregar. */
		private String provincia;
		/**
		 * Validació del camp a PINBAL, per mostrar la restricció i comprovar el valor al formulari. Només per als camps
		 * modificables; PINBAL torna a validar la petició.
		 */
		private Validacio validacio;
	}

	/**
	 * Validació d'un camp. Només s'informen les propietats del tipus de validació: expressioRegular (EXPRESSIO_REGULAR);
	 * minim i/o maxim (NUM_*); operacio, campComparacio i campComparacioText (DATA_*); diferencia i unitat
	 * (DATA_DIFERENCIA).
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Validacio implements Serializable {
		private static final long serialVersionUID = 1L;
		private ValidacioTipus tipus;
		/** Expressió regular de Java que ha de complir el valor sencer. */
		private String expressioRegular;
		private Integer minim;
		private Integer maxim;
		private ValidacioOperacio operacio;
		/** Codi (path) del camp amb què es compara la data: sempre és un camp visible del formulari. */
		private String campComparacio;
		/** Text del camp amb què es compara la data. */
		private String campComparacioText;
		private Integer diferencia;
		private ValidacioUnitat unitat;
	}

}
