package es.caib.ripea.plugin.caib.unitat;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utilitat per cercar fitxers, per exemple .class, en un directori de forma recursiva i cercant també dins jars i ears. 
 *
 */
public class FindFiles {
	
	public static void main(String[] args) {

		String startDir = "C:\\jboss-eap-5.2-dgtic";
		String className = "ApplicationContext.class";

		File root = new File(startDir);
		if (root.exists() && root.isDirectory()) {
			Set<String> results = new TreeSet<>();
			searchInDirectory(root, className, results);
			for (String aux: results) {
				System.out.println(aux);
			}
		} else {
			System.out.println("La ruta especificada no es válida.");
		}
	}

	private static void searchInDirectory(File dir, String className, Set<String> results) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					searchInDirectory(file, className, results);
				} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".ear")) {
					searchInArchive(file, className, results);
				}
			}
		}
	}

	private static void searchInArchive(File file, String className, Set<String> results) {
		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().endsWith(className)) {
					results.add("Encontrado: " + entry.getName() + " en " + file.getPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}