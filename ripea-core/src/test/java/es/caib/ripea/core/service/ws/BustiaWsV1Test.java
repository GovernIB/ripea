/**
 * 
 */
package es.caib.ripea.core.service.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.caib.ripea.core.api.registre.RegistreAnnex;
import es.caib.ripea.core.api.registre.RegistreAnotacio;
import es.caib.ripea.core.api.registre.RegistreInteressat;

/**
 * Classe de proves pel registre versió 1.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaWsV1Test extends BustiaBaseTest {


	public static void main(String[] args) {
		try {
			new BustiaWsV1Test().provaEnviamentContingut();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void provaEnviamentContingut() throws Exception {
		
		// Anotació de registre
		Date ara = new Date();
		String numeroIdentificador = new SimpleDateFormat("yyMMddHHmm").format(ara);
		RegistreAnotacio anotacio = new RegistreAnotacio();
		anotacio.setNumero(String.valueOf(ara.getTime()));
		anotacio.setData(ara);
		anotacio.setIdentificador(numeroIdentificador);
		anotacio.setExtracte("Extracte " + numeroIdentificador);
		anotacio.setOficinaCodi("0");
		anotacio.setAssumpteCodi("A1");
		anotacio.setAssumpteTipusCodi("0");
		anotacio.setIdiomaCodi("CA");
		anotacio.setEntitatCodi("?");
		anotacio.setLlibreCodi("?");
		anotacio.setAnnexos(this.getAnnexos());
		anotacio.setJustificant(this.getJustificant());
		
		anotacio.setInteressats(new ArrayList<RegistreInteressat>());
		// Afegeix interessats
		for (int i=0; i<2; i++)
			anotacio.getInteressats().add(getInteressat(i));
		
		// Envia l'anotació de registre.
		getBustiaService().enviarAnotacioRegistreEntrada(
				"A04003003",
				"A04013529",
				anotacio);
	}
	
	private List<RegistreAnnex> getAnnexos() {
		
		List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
		RegistreAnnex annex;
		Date ara = new Date();

		try {
			//Llegeix el contingut del director;
			String directoriPath = "es/caib/ripea/core/annexos/";
			InputStream is = getClass().getClassLoader().getResourceAsStream(directoriPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String arxiuNom;
			while ((arxiuNom = br.readLine()) != null) {
				// Afegeix tots els fitxers del directori com annexos
				System.out.println("Afegint annex \"" + arxiuNom + "\"");
				File arxiu = new File(getClass().getClassLoader().getResource(directoriPath + arxiuNom).getFile());
				File arxiuAux = new File(URLDecoder.decode(arxiu.getAbsolutePath(), "UTF-8")); // Problemes amb els espais				
								
				annex = new RegistreAnnex();
				annex.setTitol(arxiuAux.getName());
				annex.setFitxerNom(arxiuAux.getName());
				annex.setFitxerTamany((int) arxiuAux.length());
				annex.setEniDataCaptura(ara);
				annex.setEniOrigen("0");
				annex.setEniTipusDocumental("TD99");
//				annex.setSicresTipusDocument("03");
				
				// Llegeix el contingut del fitxer i ho guarda en base 64
				byte[] contingut = Files.readAllBytes(arxiuAux.toPath());
				annex.setFitxerContingut(contingut);

				annexos.add(annex);
			}
		} catch (Exception e) {
			System.err.println("Error adjuntant els annexos a l'anotació: " + e.getLocalizedMessage());
			e.printStackTrace();
		}

		return annexos;
	}
	
	private RegistreAnnex getJustificant() {
		
		RegistreAnnex annex = new RegistreAnnex();;
		Date ara = new Date();

		try {
			//Llegeix el contingut del director;
			String directoriPath = "es/caib/ripea/core/justificant/";
			InputStream is = getClass().getClassLoader().getResourceAsStream(directoriPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String arxiuNom;
			while ((arxiuNom = br.readLine()) != null) {
				// Afegeix tots els fitxers del directori com annexos
				System.out.println("Afegint justificant \"" + arxiuNom + "\"");
				File arxiu = new File(getClass().getClassLoader().getResource(directoriPath + arxiuNom).getFile());
				File arxiuAux = new File(URLDecoder.decode(arxiu.getAbsolutePath(), "UTF-8")); // Problemes amb els espais				
								
				annex = new RegistreAnnex();
				annex.setTitol(arxiuAux.getName());
				annex.setFitxerNom(arxiuAux.getName());
				annex.setFitxerTamany((int) arxiuAux.length());
				annex.setEniDataCaptura(ara);
				annex.setEniOrigen("0");
				annex.setEniTipusDocumental("TD99");
				annex.setFitxerArxiuUuid("9f33c5c7-7d0f-4d70-9082-c541a42cc041");
//				annex.setSicresTipusDocument("03");
				
				// Llegeix el contingut del fitxer i ho guarda en base 64
				byte[] contingut = Files.readAllBytes(arxiuAux.toPath());
				annex.setFitxerContingut(contingut);
			}
		} catch (Exception e) {
			System.err.println("Error adjuntant els annexos a l'anotació: " + e.getLocalizedMessage());
			e.printStackTrace();
		}

		return annex;
	}
}
