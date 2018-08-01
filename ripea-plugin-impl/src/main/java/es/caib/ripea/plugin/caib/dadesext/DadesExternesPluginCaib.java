/**
 * 
 */
package es.caib.ripea.plugin.caib.dadesext;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.dadesext.ComunitatAutonoma;
import es.caib.ripea.plugin.dadesext.DadesExternesPlugin;
import es.caib.ripea.plugin.dadesext.EntitatGeografica;
import es.caib.ripea.plugin.dadesext.Municipi;
import es.caib.ripea.plugin.dadesext.NivellAdministracio;
import es.caib.ripea.plugin.dadesext.Pais;
import es.caib.ripea.plugin.dadesext.Provincia;
import es.caib.ripea.plugin.dadesext.TipusVia;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Implementació del plugin de dades externes que consulta la
 * informació a l'aplicació web de dades comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesExternesPluginCaib implements DadesExternesPlugin {


	@Override
	public List<Pais> paisFindAll() throws SistemaExternException {
		String url = getBaseUrl() + "/services/paisos/format/JSON/idioma/ca";
		String errorDescripcio = "No s'ha pogut consultar la llista de paisos (" +
				"url=" + url + ")";
		try {
			HttpURLConnection httpConnection = (HttpURLConnection)new URL(url).openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			List<DadesComunesPais> paisosJson = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							DadesComunesPais.class));
			Collections.sort(
					paisosJson,
					new Comparator<DadesComunesPais>() {
						@Override
						public int compare(DadesComunesPais p1, DadesComunesPais p2) {
							return p1.getNom().compareToIgnoreCase(p2.getNom());
						}
					});
			List<Pais> paisos = new ArrayList<Pais>();
			for (DadesComunesPais paisJson: paisosJson) {
				Long codiNumeric = 0L;
				if (paisJson.getCodi_numeric() != null) {
					codiNumeric = new Long(paisJson.getCodi_numeric());
				}
				Pais pais = new Pais(
						codiNumeric,
						paisJson.getAlpha2(),
						paisJson.getAlpha3(),
						paisJson.getNom());
				paisos.add(pais);
			}
			return paisos;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}
	
	@Override
	public List<ComunitatAutonoma> comunitatFindAll() throws SistemaExternException {
		String url = getBaseUrl() + "/services/ccaa/format/JSON/idioma/ca";
		String errorDescripcio = "No s'ha pogut consultar la llista de comunitats (" +
				"url=" + url + ")";
		try {
			HttpURLConnection httpConnection = (HttpURLConnection)new URL(url).openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			List<ComunitatAutonoma> comunitats = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							Provincia.class));
			Collections.sort(
					comunitats,
					new Comparator<ComunitatAutonoma>() {
						@Override
						public int compare(ComunitatAutonoma p1, ComunitatAutonoma p2) {
							return p1.getNom().compareToIgnoreCase(p2.getNom());
						}
					});
			
			return comunitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	@Override
	public List<Provincia> provinciaFindAll() throws SistemaExternException {
		String url = getBaseUrl() + "/services/provincies/format/JSON/idioma/ca";
		String errorDescripcio = "No s'ha pogut consultar la llista de províncies (" +
				"url=" + url + ")";
		try {
			HttpURLConnection httpConnection = (HttpURLConnection)new URL(url).openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			List<Provincia> provincies = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							Provincia.class));
			Collections.sort(
					provincies,
					new Comparator<Provincia>() {
						@Override
						public int compare(Provincia p1, Provincia p2) {
							return p1.getNom().compareToIgnoreCase(p2.getNom());
						}
					});
			
			return provincies;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	@Override
	public List<Provincia> provinciaFindByComunitat(
			String comunitatCodi) throws SistemaExternException {
		String url = getBaseUrl() + "/services/provincies/" + comunitatCodi + "/format/JSON/idioma/ca";
		String errorDescripcio = "No s'ha pogut consultar la llista de províncies donada una comunitat (" +
				"url=" + url + ", " +
				"comunitatCodi=" + comunitatCodi + ")";
		try {
			HttpURLConnection httpConnection = (HttpURLConnection)new URL(url).openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			List<Provincia> provincies = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							Provincia.class));
			Collections.sort(
					provincies,
					new Comparator<Provincia>() {
						@Override
						public int compare(Provincia p1, Provincia p2) {
							return p1.getNom().compareToIgnoreCase(p2.getNom());
						}
					});
			
			return provincies;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	@Override
	public List<Municipi> municipiFindByProvincia(
			String provinciaCodi) throws SistemaExternException {
		String url = getBaseUrl() + "/services/municipis/" + provinciaCodi + "/format/JSON";
		String errorDescripcio = "No s'ha pogut consultar la llista de municipis donada una província (" +
				"url=" + url + ", " +
				"provinciaCodi=" + provinciaCodi + ")";
		try {
			HttpURLConnection httpConnection = (HttpURLConnection)new URL(url).openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			List<Municipi> municipis = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							Municipi.class));
			Collections.sort(
					municipis,
					new Comparator<Municipi>() {
						@Override
						public int compare(Municipi m1, Municipi m2) {
							return m1.getNom().compareToIgnoreCase(m2.getNom());
						}
					});
			
			return municipis;
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}
	
	@Override
	public List<EntitatGeografica> entitatGeograficaFindAll() throws SistemaExternException {
		List<EntitatGeografica> entitatsGeografiques = new ArrayList<EntitatGeografica>();
		entitatsGeografiques.add(new EntitatGeografica("NA", "No aplica"));
		entitatsGeografiques.add(new EntitatGeografica("00", "Sin Dato"));
		entitatsGeografiques.add(new EntitatGeografica("01", "Municipio"));
		entitatsGeografiques.add(new EntitatGeografica("02", "Provincia"));
		entitatsGeografiques.add(new EntitatGeografica("03", "Isla"));
		entitatsGeografiques.add(new EntitatGeografica("04", "Entidad Local Menor"));
		entitatsGeografiques.add(new EntitatGeografica("05", "Mancomunidad"));
		entitatsGeografiques.add(new EntitatGeografica("06", "Comarca"));
		entitatsGeografiques.add(new EntitatGeografica("07", "Area Metropolitana"));
		entitatsGeografiques.add(new EntitatGeografica("08", "Otras Agrupaciones"));
		entitatsGeografiques.add(new EntitatGeografica("10", "País"));
		entitatsGeografiques.add(new EntitatGeografica("20", "Comunidad Autónoma"));
		return entitatsGeografiques;
	}

	@Override
	public List<NivellAdministracio> nivellAdministracioFindAll() throws SistemaExternException {
		List<NivellAdministracio> nivellsAdministracio = new ArrayList<NivellAdministracio>();
		nivellsAdministracio.add(new NivellAdministracio(1L, "Administración del Estado"));
		nivellsAdministracio.add(new NivellAdministracio(2L, "Administración Autonómica"));
		nivellsAdministracio.add(new NivellAdministracio(3L, "Administración Local"));
		nivellsAdministracio.add(new NivellAdministracio(4L, "Universidades"));
		nivellsAdministracio.add(new NivellAdministracio(5L, "Otras Instituciones"));
		nivellsAdministracio.add(new NivellAdministracio(6L, "Administración de Justicia"));
		nivellsAdministracio.add(new NivellAdministracio(7L, "Historicos AGE"));
		nivellsAdministracio.add(new NivellAdministracio(8L, "Historicos CCAA"));
		nivellsAdministracio.add(new NivellAdministracio(9L, "Historicos EELL"));
		return nivellsAdministracio;
	}

	@Override
	public List<TipusVia> tipusViaFindAll() throws SistemaExternException {
		List<TipusVia> tipusVia = new ArrayList<TipusVia>();
		tipusVia.add(new TipusVia(1L, "alameda"));
		tipusVia.add(new TipusVia(2L, "calle"));
		tipusVia.add(new TipusVia(3L, "camino"));
		tipusVia.add(new TipusVia(4L, "carrer"));
		tipusVia.add(new TipusVia(5L, "carretera"));
		tipusVia.add(new TipusVia(6L, "glorieta"));
		tipusVia.add(new TipusVia(7L, "kalea"));
		tipusVia.add(new TipusVia(8L, "pasaje"));
		tipusVia.add(new TipusVia(9L, "paseo"));
		tipusVia.add(new TipusVia(10L, "plaça"));
		tipusVia.add(new TipusVia(11L, "plaza"));
		tipusVia.add(new TipusVia(12L, "rambla"));
		tipusVia.add(new TipusVia(13L, "ronda"));
		tipusVia.add(new TipusVia(14L, "rúa"));
		tipusVia.add(new TipusVia(15L, "sector"));
		tipusVia.add(new TipusVia(16L, "travesía"));
		tipusVia.add(new TipusVia(17L, "urbanización"));
		tipusVia.add(new TipusVia(18L, "avenida"));
		tipusVia.add(new TipusVia(19L, "avinguda"));
		tipusVia.add(new TipusVia(20L, "barrio"));
		tipusVia.add(new TipusVia(21L, "calleja"));
		tipusVia.add(new TipusVia(22L, "camí"));
		tipusVia.add(new TipusVia(23L, "campo"));
		tipusVia.add(new TipusVia(24L, "carrera"));
		tipusVia.add(new TipusVia(25L, "cuesta"));
		tipusVia.add(new TipusVia(26L, "edificio"));
		tipusVia.add(new TipusVia(27L, "enparantza"));
		tipusVia.add(new TipusVia(28L, "estrada"));
		tipusVia.add(new TipusVia(29L, "jardines"));
		tipusVia.add(new TipusVia(30L, "jardins"));
		tipusVia.add(new TipusVia(31L, "parque"));
		tipusVia.add(new TipusVia(32L, "passeig"));
		tipusVia.add(new TipusVia(33L, "praza"));
		tipusVia.add(new TipusVia(34L, "plazuela"));
		tipusVia.add(new TipusVia(35L, "placeta"));
		tipusVia.add(new TipusVia(36L, "poblado"));
		tipusVia.add(new TipusVia(37L, "via"));
		tipusVia.add(new TipusVia(38L, "travessera"));
		tipusVia.add(new TipusVia(40L, "passatge"));
		tipusVia.add(new TipusVia(41L, "bulevar"));
		tipusVia.add(new TipusVia(42L, "polígono"));
		tipusVia.add(new TipusVia(99L, "otros"));
		return tipusVia;
	}

	private String getBaseUrl() {
		String baseUrl = PropertiesHelper.getProperties().getProperty(
				"es.caib.ripea.plugin.dadesext.service.url");
		if (baseUrl != null && baseUrl.length() > 0) {
			return baseUrl;
		} else {
			return "https://proves.caib.es/dadescomunsfront";
		}
	}

	public static class DadesComunesPais {
		private String codi_numeric;
		private String alpha2;
		private String alpha3;
		private String nom;
		public String getCodi_numeric() {
			return codi_numeric;
		}
		public void setCodi_numeric(String codi_numeric) {
			this.codi_numeric = codi_numeric;
		}
		public String getAlpha2() {
			return alpha2;
		}
		public void setAlpha2(String alpha2) {
			this.alpha2 = alpha2;
		}
		public String getAlpha3() {
			return alpha3;
		}
		public void setAlpha3(String alpha3) {
			this.alpha3 = alpha3;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
	}

}
