package es.caib.ripea.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica.EnumTipus;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.GrupDadesEspecifiques;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.Camp;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.CampTipus;
import es.caib.ripea.service.intf.dto.PinbalFormulariDto.Seccio;

/**
 * Formulari dinàmic de consulta PINBAL: construcció del formulari a partir de les dades específiques i els grups d'un
 * servei, i conversió dels valors del formulari a les dades específiques de la petició.
 */
class PinbalFormulariUtilsTest {

	private static final String PATH = "DatosEspecificos/Consulta/";

	@Test
	void tipusCamp_deduitDeTipusIEnumTipus() {
		assertThat(PinbalFormulariUtils.tipusCamp(dada("a", DadaTipusEnum.TEXT))).isEqualTo(CampTipus.TEXT);
		assertThat(PinbalFormulariUtils.tipusCamp(dada("a", DadaTipusEnum.NUMERIC))).isEqualTo(CampTipus.NUMERIC);
		assertThat(PinbalFormulariUtils.tipusCamp(dada("a", DadaTipusEnum.DATE))).isEqualTo(CampTipus.DATA);
		assertThat(PinbalFormulariUtils.tipusCamp(dada("a", DadaTipusEnum.BOOLEAN))).isEqualTo(CampTipus.BOOLEA);
		assertThat(PinbalFormulariUtils.tipusCamp(dada("a", DadaTipusEnum.DOC_IDENTITAT))).isEqualTo(CampTipus.DOC_IDENT);
		assertThat(PinbalFormulariUtils.tipusCamp(dada("a", DadaTipusEnum.FILE))).isEqualTo(CampTipus.ADJUNT);
		assertThat(PinbalFormulariUtils.tipusCamp(llista("a", EnumTipus.ENUM))).isEqualTo(CampTipus.ENUM);
		assertThat(PinbalFormulariUtils.tipusCamp(llista("a", EnumTipus.PAIS))).isEqualTo(CampTipus.PAIS);
		assertThat(PinbalFormulariUtils.tipusCamp(llista("a", EnumTipus.PROVINCIA))).isEqualTo(CampTipus.PROVINCIA);
		assertThat(PinbalFormulariUtils.tipusCamp(llista("a", EnumTipus.MUNICIPI_3))).isEqualTo(CampTipus.MUNICIPI_3);
		assertThat(PinbalFormulariUtils.tipusCamp(llista("a", EnumTipus.MUNICIPI_5))).isEqualTo(CampTipus.MUNICIPI_5);
		// Les etiquetes arriben com a ENUM sense enumTipus
		assertThat(PinbalFormulariUtils.tipusCamp(llista("a", null))).isEqualTo(CampTipus.ETIQUETA);
	}

	@Test
	void textPla_htmlDeLAjudaATextPla() {
		// Ajuda d'un grup del servei SVDDGPRESIDENCIALEGALDOCWS01
		String html = "<div><h3>Dades&nbsp;del&nbsp;passaport</h3></div>"
				+ "<div>Si&nbsp;s&#39;introdueix&nbsp;el&nbsp;tipus&nbsp;de&nbsp;passaport&nbsp;és&nbsp;necessari&nbsp;indicar&nbsp;la&nbsp;nacionalitat<br></div>";
		assertThat(PinbalFormulariUtils.textPla(html)).isEqualTo(
				"Dades del passaport\nSi s'introdueix el tipus de passaport és necessari indicar la nacionalitat");
		assertThat(PinbalFormulariUtils.textPla("Primera línia<br>Segona   línia")).isEqualTo("Primera línia\nSegona línia");
		assertThat(PinbalFormulariUtils.textPla("<ul><li>Un</li><li>Dos</li></ul>")).isEqualTo("Un\nDos");
		assertThat(PinbalFormulariUtils.textPla("<div>&nbsp;</div><p> </p>")).isNull();
	}

	@Test
	void textPla_textSenseHtmlEsDeixaIgual() {
		assertThat(PinbalFormulariUtils.textPla("  Valor < 5 i > 2\nsegona línia ")).isEqualTo("Valor < 5 i > 2\nsegona línia");
		assertThat(PinbalFormulariUtils.textPla(" ")).isNull();
		assertThat(PinbalFormulariUtils.textPla(null)).isNull();
	}

	@Test
	void construirFormulari_ajudaIComentarisEnTextPla() {
		DadaEspecifica camp = dadaGrup(PATH + "Passaport", DadaTipusEnum.TEXT, "Passaport", 0);
		camp.setComentari("<b>Número</b> del passaport");
		DadaEspecifica etiquetaHtmlBuida = llista(PATH + "Etiqueta", null);
		etiquetaHtmlBuida.setComentari("<div><br></div>");
		GrupDadesEspecifiques grup = grup("Passaport", "Passaport", 0);
		grup.setAjuda("<div><h3>Dades&nbsp;del&nbsp;passaport</h3></div>");

		PinbalFormulariDto formulari = PinbalFormulariUtils.construirFormulari(
				"SERVEI",
				Arrays.asList(camp, etiquetaHtmlBuida),
				Collections.singletonList(grup));

		assertThat(formulari.getArrel().getCamps()).isEmpty();
		Seccio seccio = formulari.getArrel().getSubseccions().get(0);
		assertThat(seccio.getAjuda()).isEqualTo("Dades del passaport");
		assertThat(seccio.getCamps().get(0).getComentari()).isEqualTo("Número del passaport");
	}

	@Test
	void textCamp_etiquetaONomOCodi() {
		DadaEspecifica dada = dada(PATH + "Nom", DadaTipusEnum.TEXT);
		dada.setEtiqueta("Nom de la mare");
		dada.setNom("NombreMadre");
		assertThat(PinbalFormulariUtils.textCamp(dada)).isEqualTo("Nom de la mare");

		dada.setEtiqueta(" ");
		assertThat(PinbalFormulariUtils.textCamp(dada)).isEqualTo("NombreMadre");

		dada.setNom(null);
		assertThat(PinbalFormulariUtils.textCamp(dada)).isEqualTo(PATH + "Nom");
	}

	@Test
	void construirFormulari_campsSenseGrupPrimerIGrupsIcampsEnOrdre() {
		DadaEspecifica senseGrup = dada(PATH + "SenseGrup", DadaTipusEnum.TEXT);
		senseGrup.setOrdre(5);
		DadaEspecifica naixementData = dadaGrup(PATH + "Naixement/Fecha", DadaTipusEnum.DATE, "Naixement", 1);
		DadaEspecifica naixementPais = dadaGrup(PATH + "Naixement/Pais", DadaTipusEnum.TEXT, "Naixement", 0);
		DadaEspecifica registre = dadaGrup(PATH + "Registro/Tomo", DadaTipusEnum.TEXT, "Registre", 0);
		DadaEspecifica subgrup = dadaGrup(PATH + "Registro/Hecho/Fecha", DadaTipusEnum.DATE, "Registre/Fet", 0);
		List<GrupDadesEspecifiques> grups = Arrays.asList(
				grup("Naixement", "Naixement", 2),
				GrupDadesEspecifiques.builder()
						.codi("Registre")
						.nom("Registre")
						.ordre(1)
						.ajuda("Dades del registre civil")
						.subgrups(Collections.singletonList(grup("Registre/Fet", "Fet", 0)))
						.build());

		PinbalFormulariDto formulari = PinbalFormulariUtils.construirFormulari(
				"SVDRRCCNACIMIENTOWS01",
				Arrays.asList(naixementData, registre, senseGrup, subgrup, naixementPais),
				grups);

		Seccio arrel = formulari.getArrel();
		assertThat(formulari.getServeiCodi()).isEqualTo("SVDRRCCNACIMIENTOWS01");
		assertThat(codis(arrel.getCamps())).containsExactly(PATH + "SenseGrup");
		assertThat(arrel.getSubseccions()).extracting(Seccio::getCodi).containsExactly("Registre", "Naixement");
		Seccio seccioRegistre = arrel.getSubseccions().get(0);
		assertThat(seccioRegistre.getAjuda()).isEqualTo("Dades del registre civil");
		assertThat(codis(seccioRegistre.getCamps())).containsExactly(PATH + "Registro/Tomo");
		assertThat(seccioRegistre.getSubseccions()).extracting(Seccio::getNom).containsExactly("Fet");
		assertThat(codis(seccioRegistre.getSubseccions().get(0).getCamps())).containsExactly(PATH + "Registro/Hecho/Fecha");
		assertThat(codis(arrel.getSubseccions().get(1).getCamps())).containsExactly(PATH + "Naixement/Pais", PATH + "Naixement/Fecha");
	}

	@Test
	void construirFormulari_senseOcultsNiEtiquetesBuidesNiSeccionsBuides() {
		DadaEspecifica oculta = dadaGrup(PATH + "Oculta", DadaTipusEnum.TEXT, "Ocults", 0);
		oculta.setVisible(false);
		DadaEspecifica etiquetaBuida = llista(PATH + "EtiquetaBuida", null);
		DadaEspecifica etiqueta = llista(PATH + "Etiqueta", null);
		etiqueta.setComentari("Informació per a l'usuari");

		PinbalFormulariDto formulari = PinbalFormulariUtils.construirFormulari(
				"SERVEI",
				Arrays.asList(oculta, etiquetaBuida, etiqueta),
				Collections.singletonList(grup("Ocults", "Ocults", 0)));

		assertThat(formulari.getArrel().getSubseccions()).isEmpty();
		assertThat(formulari.getArrel().getCamps()).extracting(Camp::getTipus).containsExactly(CampTipus.ETIQUETA);
		assertThat(formulari.getArrel().getCamps().get(0).getComentari()).isEqualTo("Informació per a l'usuari");
	}

	@Test
	void construirFormulari_grupQueNoEsALArbre_teSeccioPropiaAlFinal() {
		DadaEspecifica dada = dadaGrup(PATH + "Altres/Camp", DadaTipusEnum.TEXT, "Pare/Altres", 0);

		PinbalFormulariDto formulari = PinbalFormulariUtils.construirFormulari("SERVEI", Collections.singletonList(dada), null);

		assertThat(formulari.getArrel().getSubseccions()).extracting(Seccio::getCodi, Seccio::getNom)
				.containsExactly(tuple("Pare/Altres", "Altres"));
	}

	@Test
	void construirFormulari_senseDades_formulariBuit() {
		PinbalFormulariDto formulari = PinbalFormulariUtils.construirFormulari("SERVEI", null, null);

		assertThat(formulari.getArrel().getCamps()).isEmpty();
		assertThat(formulari.getArrel().getSubseccions()).isEmpty();
	}

	@Test
	void construirFormulari_valorsInicials() {
		DadaEspecifica provincia = llista(PATH + "Provincia", EnumTipus.PROVINCIA);
		DadaEspecifica pais = llista(PATH + "Pais", EnumTipus.PAIS);
		DadaEspecifica paisAmbDefecte = llista(PATH + "PaisDefecte", EnumTipus.PAIS);
		paisAmbDefecte.setValorDefecte("250");
		DadaEspecifica data = dada(PATH + "Data", DadaTipusEnum.DATE);
		data.setValorDefecte("03/11/2026");
		DadaEspecifica boolea = dada(PATH + "Boolea", DadaTipusEnum.BOOLEAN);
		boolea.setValorDefecte("S");
		DadaEspecifica text = dada(PATH + "Text", DadaTipusEnum.TEXT);

		Map<String, String> valors = valorsInicials(PinbalFormulariUtils.construirFormulari(
				"SERVEI",
				Arrays.asList(provincia, pais, paisAmbDefecte, data, boolea, text),
				null));

		assertThat(valors).containsEntry(PATH + "Provincia", "07")
				.containsEntry(PATH + "Pais", "724")
				.containsEntry(PATH + "PaisDefecte", "250")
				.containsEntry(PATH + "Data", "2026-11-03T00:00:00")
				.containsEntry(PATH + "Boolea", "true")
				.containsEntry(PATH + "Text", null);
	}

	@Test
	void construirFormulari_provinciaDelsMunicipis() {
		DadaEspecifica provinciaVisible = llista(PATH + "Naixement/Provincia", EnumTipus.PROVINCIA);
		DadaEspecifica municipiAmbCamp = llista(PATH + "Naixement/Municipio", EnumTipus.MUNICIPI_5);
		municipiAmbCamp.setCampCondicionant(PATH + "Naixement/Provincia");
		DadaEspecifica provinciaOculta = llista(PATH + "Registro/Provincia", EnumTipus.PROVINCIA);
		provinciaOculta.setVisible(false);
		provinciaOculta.setValorDefecte("08");
		DadaEspecifica municipiAmbCampOcult = llista(PATH + "Registro/Municipio", EnumTipus.MUNICIPI_3);
		municipiAmbCampOcult.setCampCondicionant(PATH + "Registro/Provincia");
		DadaEspecifica municipiAmbValor = llista(PATH + "Hecho/Municipio", EnumTipus.MUNICIPI_5);
		municipiAmbValor.setValorCondicionant("35");
		DadaEspecifica municipiSenseRes = llista(PATH + "Altre/Municipio", EnumTipus.MUNICIPI_5);

		Map<String, Camp> camps = camps(PinbalFormulariUtils.construirFormulari(
				"SERVEI",
				Arrays.asList(provinciaVisible, municipiAmbCamp, provinciaOculta, municipiAmbCampOcult, municipiAmbValor, municipiSenseRes),
				null));

		assertThat(camps.get(PATH + "Naixement/Municipio").getCampProvincia()).isEqualTo(PATH + "Naixement/Provincia");
		assertThat(camps.get(PATH + "Naixement/Municipio").getProvincia()).isNull();
		assertThat(camps.get(PATH + "Registro/Municipio").getCampProvincia()).isNull();
		assertThat(camps.get(PATH + "Registro/Municipio").getProvincia()).isEqualTo("08");
		assertThat(camps.get(PATH + "Hecho/Municipio").getProvincia()).isEqualTo("35");
		assertThat(camps.get(PATH + "Altre/Municipio").getProvincia()).isEqualTo("07");
	}

	@Test
	void construirDadesEspecifiques_datesEnFormatDdMmYyyy() {
		DadaEspecifica data = dada(PATH + "Fecha", DadaTipusEnum.DATE);
		data.setFormat("yyyy-MM-dd");
		DadaEspecifica dataText = dada(PATH + "FechaText", DadaTipusEnum.DATE);

		Map<String, String> resultat = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(data, dataText),
				valors(PATH + "Fecha", "2026-11-03T00:00:00", PATH + "FechaText", "3/11/2026"));

		// En ISO, PINBAL la interpretaria com a dd-MM-yyyy (any 9)
		assertThat(resultat).containsEntry(PATH + "Fecha", "03/11/2026")
				.containsEntry(PATH + "FechaText", "03/11/2026");
	}

	@Test
	void construirDadesEspecifiques_booleansSempreTrueOFalse() {
		DadaEspecifica marcat = dada(PATH + "Marcat", DadaTipusEnum.BOOLEAN);
		DadaEspecifica senseValor = dada(PATH + "SenseValor", DadaTipusEnum.BOOLEAN);
		DadaEspecifica text = dada(PATH + "Text", DadaTipusEnum.BOOLEAN);

		Map<String, String> resultat = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(marcat, senseValor, text),
				valors(PATH + "Marcat", Boolean.TRUE, PATH + "Text", "off"));

		assertThat(resultat).containsEntry(PATH + "Marcat", "true")
				.containsEntry(PATH + "SenseValor", "false")
				.containsEntry(PATH + "Text", "false");
	}

	@Test
	void construirDadesEspecifiques_ocultsINoModificables_ambElValorPerDefecte() {
		DadaEspecifica oculta = dada(PATH + "Oculta", DadaTipusEnum.TEXT);
		oculta.setVisible(false);
		oculta.setValorDefecte("DEFECTE");
		DadaEspecifica noModificable = dada(PATH + "NoModificable", DadaTipusEnum.TEXT);
		noModificable.setModificable(false);
		noModificable.setValorDefecte("FIX");
		DadaEspecifica ocultaSenseDefecte = dada(PATH + "OcultaBuida", DadaTipusEnum.BOOLEAN);
		ocultaSenseDefecte.setVisible(false);

		Map<String, String> resultat = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(oculta, noModificable, ocultaSenseDefecte),
				valors(PATH + "Oculta", "DEL FORMULARI", PATH + "NoModificable", "CANVIAT"));

		assertThat(resultat).containsOnly(
				Map.entry(PATH + "Oculta", "DEFECTE"),
				Map.entry(PATH + "NoModificable", "FIX"));
	}

	@Test
	void construirDadesEspecifiques_senseEtiquetesNiBuitsNiCampsDesconeguts() {
		DadaEspecifica etiqueta = llista(PATH + "Etiqueta", null);
		DadaEspecifica buit = dada(PATH + "Buit", DadaTipusEnum.TEXT);
		DadaEspecifica text = dada(PATH + "Text", DadaTipusEnum.TEXT);

		Map<String, String> resultat = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(etiqueta, buit, text),
				valors(PATH + "Etiqueta", "x", PATH + "Buit", "  ", PATH + "Text", " valor ", PATH + "Desconegut", "y"));

		assertThat(resultat).containsOnly(Map.entry(PATH + "Text", "valor"));
	}

	@Test
	void construirDadesEspecifiques_numerosIFitxers() {
		DadaEspecifica enter = dada(PATH + "Enter", DadaTipusEnum.NUMERIC);
		DadaEspecifica decimal = dada(PATH + "Decimal", DadaTipusEnum.NUMERIC);
		DadaEspecifica fitxer = dada(PATH + "Fitxer", DadaTipusEnum.FILE);
		Map<String, Object> fitxerFormulari = new HashMap<>();
		fitxerFormulari.put("name", "document.pdf");
		fitxerFormulari.put("content", "JVBERi0xLjQ=");

		Map<String, String> resultat = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(enter, decimal, fitxer),
				valors(PATH + "Enter", 5.0, PATH + "Decimal", 1234.5, PATH + "Fitxer", fitxerFormulari));

		assertThat(resultat).containsEntry(PATH + "Enter", "5")
				.containsEntry(PATH + "Decimal", "1234.5")
				.containsEntry(PATH + "Fitxer", "JVBERi0xLjQ=");
	}

	@Test
	void construirDadesEspecifiques_documentSenseNumero_noEnviaElTipus() {
		DadaEspecifica tipusDocument = llista(PATH + "TipoDocumentacion", EnumTipus.ENUM);
		DadaEspecifica numeroDocument = dada(PATH + "Documentacion", DadaTipusEnum.DOC_IDENTITAT);
		numeroDocument.setCampCondicionant(PATH + "TipoDocumentacion");

		Map<String, String> senseNumero = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(tipusDocument, numeroDocument),
				valors(PATH + "TipoDocumentacion", "DNI"));
		Map<String, String> ambNumero = PinbalFormulariUtils.construirDadesEspecifiques(
				Arrays.asList(tipusDocument, numeroDocument),
				valors(PATH + "TipoDocumentacion", "DNI", PATH + "Documentacion", "12345678Z"));

		assertThat(senseNumero).isEmpty();
		assertThat(ambNumero).containsEntry(PATH + "TipoDocumentacion", "DNI")
				.containsEntry(PATH + "Documentacion", "12345678Z");
	}

	@Test
	void descripcioError_ambElTextDelCamp() {
		DadaEspecifica dada = dada(PATH + "Nombre", DadaTipusEnum.TEXT);
		dada.setEtiqueta("Nom");
		Map<String, List<String>> errors = new HashMap<>();
		errors.put("solicitud.dadesEspecifiques[" + PATH + "Nombre]", Collections.singletonList("Aquest camp és obligatori"));
		PeticioRespostaSincrona resposta = PeticioRespostaSincrona.builder()
				.error(true)
				.missatge("S'han produït errors en la validació de les dades de la petició.")
				.errorsValidacio(errors)
				.build();

		assertThat(PinbalFormulariUtils.descripcioError(resposta, Collections.singletonList(dada)))
				.isEqualTo("S'han produït errors en la validació de les dades de la petició.; Nom: Aquest camp és obligatori");
		assertThat(PinbalFormulariUtils.descripcioError(null, null)).isNotBlank();
	}

	@Test
	void parseData_formatsAcceptatsIDatesInvalides() {
		assertThat(PinbalFormulariUtils.parseData("2026-11-03", null)).isEqualTo(LocalDate.of(2026, 11, 3));
		assertThat(PinbalFormulariUtils.parseData("03/11/2026", null)).isEqualTo(LocalDate.of(2026, 11, 3));
		assertThat(PinbalFormulariUtils.parseData("03-11-2026", null)).isEqualTo(LocalDate.of(2026, 11, 3));
		assertThat(PinbalFormulariUtils.parseData("20261103", "yyyyMMdd")).isEqualTo(LocalDate.of(2026, 11, 3));
		assertThat(PinbalFormulariUtils.parseData("31/02/2026", null)).isNull();
		assertThat(PinbalFormulariUtils.parseData("no és una data", null)).isNull();
		assertThat(PinbalFormulariUtils.parseData(" ", null)).isNull();
	}

	private static DadaEspecifica dada(String codi, DadaTipusEnum tipus) {
		return DadaEspecifica.builder()
				.codi(codi)
				.nom(codi.substring(codi.lastIndexOf('/') + 1))
				.tipus(tipus)
				.visible(true)
				.modificable(true)
				.build();
	}

	private static DadaEspecifica llista(String codi, EnumTipus enumTipus) {
		DadaEspecifica dada = dada(codi, DadaTipusEnum.ENUM);
		dada.setEnumTipus(enumTipus);
		return dada;
	}

	private static DadaEspecifica dadaGrup(String codi, DadaTipusEnum tipus, String grup, int ordre) {
		DadaEspecifica dada = dada(codi, tipus);
		dada.setGrup(grup);
		dada.setOrdre(ordre);
		return dada;
	}

	private static GrupDadesEspecifiques grup(String codi, String nom, int ordre) {
		return GrupDadesEspecifiques.builder().codi(codi).nom(nom).ordre(ordre).build();
	}

	private static Map<String, Object> valors(Object... clausIValors) {
		Map<String, Object> valors = new HashMap<>();
		for (int i = 0; i < clausIValors.length; i += 2) {
			valors.put((String)clausIValors[i], clausIValors[i + 1]);
		}
		return valors;
	}

	private static List<String> codis(List<Camp> camps) {
		return camps.stream().map(Camp::getCodi).collect(Collectors.toList());
	}

	private static Map<String, Camp> camps(PinbalFormulariDto formulari) {
		Map<String, Camp> camps = new HashMap<>();
		afegirCamps(formulari.getArrel(), camps);
		return camps;
	}

	private static void afegirCamps(Seccio seccio, Map<String, Camp> camps) {
		seccio.getCamps().forEach(c -> camps.put(c.getCodi(), c));
		seccio.getSubseccions().forEach(s -> afegirCamps(s, camps));
	}

	private static Map<String, String> valorsInicials(PinbalFormulariDto formulari) {
		Map<String, String> valors = new HashMap<>();
		camps(formulari).forEach((codi, camp) -> valors.put(codi, camp.getValorInicial()));
		return valors;
	}

}
