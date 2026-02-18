package es.caib.ripea.service.intf.utils;
									
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import es.caib.ripea.service.intf.model.ImportacioZipDocument;

public class ZipDocumentExtractor {

    /**
     * Procesa un archivo ZIP desde un File en disco y extrae todos los documentos
     * Esta es ahora la versión principal recomendada para archivos grandes
     * 
     * @param zipFile Archivo ZIP en disco
     * @return Lista de ImportacioZipDocument
     * @throws IOException Si hay error al leer el ZIP
     */
    public List<ImportacioZipDocument> extractDocuments(File zipFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            return extractDocuments(fis);
        }
    }
    
    /**
     * Procesa un archivo ZIP desde un InputStream y extrae todos los documentos
     * 
     * @param inputStream InputStream del archivo ZIP
     * @return Lista de ImportacioZipDocument
     * @throws IOException Si hay error al leer el ZIP
     */
    public List<ImportacioZipDocument> extractDocuments(InputStream inputStream) throws IOException {
        List<ImportacioZipDocument> documents = new ArrayList<>();
        int contador = 0;
																			
																		
        
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Ignorar directorios vacíos
                if (entry.isDirectory()) {
                    zipInputStream.closeEntry();
                    continue;
                }
                
                // Leer el contenido del archivo en chunks
                byte[] fileContent = readFileContent(zipInputStream);
                
                ImportacioZipDocument document = createDocumentFromEntry(entry, fileContent, contador++);
                documents.add(document);
                
                zipInputStream.closeEntry();
            }
        }
        
        return documents;
    }
    
    /**
     * Versión que acepta ruta como String
     * 
     * @param zipFilePath Ruta del archivo ZIP
     * @return Lista de ImportacioZipDocument
     * @throws IOException Si hay error al leer el ZIP
     */
    public List<ImportacioZipDocument> extractDocuments(String zipFilePath) throws IOException {
        return extractDocuments(new File(zipFilePath));
    }
    
    /**
     * Versión legacy que acepta byte[] para mantener compatibilidad con código existente
     * NOTA: No recomendado para archivos grandes
     * 
     * @deprecated Usar {@link #extractDocuments(File)} o {@link #extractDocuments(InputStream)} en su lugar
     * @param zipBytes Array de bytes del archivo ZIP
     * @return Lista de ImportacioZipDocument
     * @throws IOException Si hay error al leer el ZIP
     */
    @Deprecated
    public List<ImportacioZipDocument> extractDocumentsFromBytes(byte[] zipBytes) throws IOException {
        try (InputStream is = new java.io.ByteArrayInputStream(zipBytes)) {
            return extractDocuments(is);
        }
    }
    
    /**
     * Lee el contenido completo de un archivo del ZIP en chunks
     * Optimizado para no consumir demasiada memoria de golpe
     */
    private byte[] readFileContent(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; // Buffer de 8KB
        int bytesRead;
        
        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        
        return baos.toByteArray();
    }
    
    /**
																		
	   
																								  
																					
	 
	
	   
																					 
	   
																								
															   
	 
	
	   
     * Crea un ImportacioZipDocument desde un ZipEntry y su contenido
     */
    private ImportacioZipDocument createDocumentFromEntry(ZipEntry entry, byte[] fileContent, int counter) {
        ImportacioZipDocument document = new ImportacioZipDocument();
        
        //ID para el front, no es ningun ID de BBDD
        document.setId(Calendar.getInstance().getTimeInMillis()+"_"+counter);
        
        // Nombre completo del archivo con su ruta
        String fullPath = entry.getName();
        Path path = Paths.get(fullPath);
        
        // Extraer el nombre del archivo completo
        String fullFileName = path.getFileName().toString();
        
        // Separar nombre y extensión
        String[] nameAndExtension = splitNameAndExtension(fullFileName);
        String fileName = nameAndExtension[0];
        String fileExtension = nameAndExtension[1];
        
        // Extraer la ruta (carpetas contenedoras)
        String folderPath = extractFolderPath(fullPath, fullFileName);
        
        // Rellenar el objeto
        document.setImportar(true);
        document.setRuta(folderPath);
        document.setNom(fileName);
        document.setExtensio(fileExtension);
        document.setContingut(fileContent);
        document.setMida(fileContent.length);
        document.setTipusDocument(null);
        
        return document;
    }
    
    /**
     * Separa el nombre del archivo de su extensión
     * 
     * @param fullFileName Nombre completo del archivo (ej: "documento.pdf" o "archivo.tar.gz")
     * @return Array con [nombre, extensión] (ej: ["documento", "pdf"])
     */
    private String[] splitNameAndExtension(String fullFileName) {
        int lastDotIndex = fullFileName.lastIndexOf('.');
        
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            // No hay extensión o el archivo empieza con punto (ej: ".gitignore")
            return new String[]{fullFileName, ""};
        }
        
        String name = fullFileName.substring(0, lastDotIndex);
        String extension = fullFileName.substring(lastDotIndex + 1);
        
        return new String[]{name, extension};
    }
    
    /**
     * Extrae la ruta de carpetas del path completo
     * 
     * @param fullPath Ruta completa (ej: "carpeta1/carpeta2/archivo.pdf")
     * @param fileName Nombre del archivo (ej: "archivo.pdf")
     * @return Ruta de carpetas (ej: "carpeta1/carpeta2") o cadena vacía si no hay carpetas
     */
    private String extractFolderPath(String fullPath, String fileName) {
        if (fullPath.equals(fileName)) {
            return ""; // Archivo en la raíz del ZIP
        }
        
        // Remover el nombre del archivo del path completo
        int lastSeparator = fullPath.lastIndexOf('/');
        if (lastSeparator == -1) {
            lastSeparator = fullPath.lastIndexOf('\\');
        }
        
        if (lastSeparator != -1) {
            return fullPath.substring(0, lastSeparator);
        }
        
        return "";
    }
    
    /**
     * Versión que filtra por extensiones permitidas - ahora acepta File
     */
    public List<ImportacioZipDocument> extractDocuments(File zipFile, List<String> allowedExtensions) throws IOException {
        List<ImportacioZipDocument> allDocuments = extractDocuments(zipFile);
        return filterByExtensions(allDocuments, allowedExtensions);
    }
    
    /**
     * Versión que filtra por extensiones permitidas - ahora acepta InputStream
     */
    public List<ImportacioZipDocument> extractDocuments(InputStream inputStream, List<String> allowedExtensions) throws IOException {
        List<ImportacioZipDocument> allDocuments = extractDocuments(inputStream);
        return filterByExtensions(allDocuments, allowedExtensions);
    }
    
    /**
     * Filtra documentos por extensiones permitidas
     */
    private List<ImportacioZipDocument> filterByExtensions(List<ImportacioZipDocument> documents, List<String> allowedExtensions) {
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            return documents;
        }
        
        List<ImportacioZipDocument> filteredDocuments = new ArrayList<>();
        for (ImportacioZipDocument doc : documents) {
            if (hasAllowedExtension(doc.getExtensio(), allowedExtensions)) {
                filteredDocuments.add(doc);
            }
        }
        
        return filteredDocuments;
    }
    
    /**
     * Verifica si una extensión está permitida
     */
    private boolean hasAllowedExtension(String extension, List<String> allowedExtensions) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        
        String lowerExtension = extension.toLowerCase();
        for (String ext : allowedExtensions) {
            String normalizedExt = ext.startsWith(".") ? ext.substring(1).toLowerCase() : ext.toLowerCase();
            if (lowerExtension.equals(normalizedExt)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extraer un archivo específico del ZIP por nombre completo de ruta
     * Ahora acepta File en lugar de byte[]
     */
    public byte[] extractSpecificFile(File zipFile, String rutaCompleta) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            return extractSpecificFile(fis, rutaCompleta);
        }
    }
    
    /**
     * Extraer un archivo específico del ZIP por nombre completo de ruta
     * Versión con InputStream
     */
    public byte[] extractSpecificFile(InputStream inputStream, String rutaCompleta) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(rutaCompleta)) {
                    return readFileContent(zipInputStream);
                }
                zipInputStream.closeEntry();
            }
        }
        throw new IOException("Archivo no encontrado: " + rutaCompleta);
    }
}