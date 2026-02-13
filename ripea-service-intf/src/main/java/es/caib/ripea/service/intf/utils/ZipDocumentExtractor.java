package es.caib.ripea.service.intf.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
     * Procesa un archivo ZIP desde un array de bytes y extrae todos los documentos recursivamente
     * 
     * @param zipBytes Array de bytes del archivo ZIP
     * @return Lista de ImportacioZipDocument
     * @throws IOException Si hay error al leer el ZIP
     */
    public List<ImportacioZipDocument> extractDocuments(byte[] zipBytes) throws IOException {
        List<ImportacioZipDocument> documents = new ArrayList<>();
        int contador = 0;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
             ZipInputStream zipInputStream = new ZipInputStream(bais)) {
            
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Ignorar directorios vacíos
                if (entry.isDirectory()) {
                    zipInputStream.closeEntry();
                    continue;
                }
                
                // Leer el contenido del archivo
                byte[] fileContent = readFileContent(zipInputStream);
                
                ImportacioZipDocument document = createDocumentFromEntry(entry, fileContent, contador++);
                documents.add(document);
                
                zipInputStream.closeEntry();
            }
        }
        
        return documents;
    }
    
    /**
     * Lee el contenido completo de un archivo del ZIP
     */
    private byte[] readFileContent(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        
        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Versión alternativa que acepta un File (mantiene compatibilidad)
     */
    public List<ImportacioZipDocument> extractDocuments(java.io.File zipFile) throws IOException {
        return extractDocuments(java.nio.file.Files.readAllBytes(zipFile.toPath()));
    }
    
    /**
     * Versión alternativa que acepta una ruta como String (mantiene compatibilidad)
     */
    public List<ImportacioZipDocument> extractDocuments(String zipFilePath) throws IOException {
        return extractDocuments(new java.io.File(zipFilePath));
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
     * Versión que filtra por extensiones permitidas
     */
    public List<ImportacioZipDocument> extractDocuments(byte[] zipBytes, List<String> allowedExtensions) throws IOException {
        List<ImportacioZipDocument> allDocuments = extractDocuments(zipBytes);
        
        if (allowedExtensions == null || allowedExtensions.isEmpty()) {
            return allDocuments;
        }
        
        List<ImportacioZipDocument> filteredDocuments = new ArrayList<>();
        for (ImportacioZipDocument doc : allDocuments) {
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
    
    //Extraer un ZipEntry del zip por nombre completo de la ruta
    public byte[] extractSpecificFile(byte[] zipBytes, String rutaCompleta) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
             ZipInputStream zipInputStream = new ZipInputStream(bais)) {
            
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