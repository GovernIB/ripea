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
	}

}
