package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Builder;
import lombok.Getter;

public class BaseApiInternaController {
    
	@Autowired private ServletContext servletContext;
	
	protected ManifestInfo buildManifestInfo() throws IOException {

        ManifestInfo manifestInfo = ManifestInfo.builder().build();
        var manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
        var manifestAtributs = manifest.getMainAttributes();
        Map<String, Object>manifestAtributsMap = new HashMap<>();
        for (var key: new HashMap<>(manifestAtributs).keySet()) {
            manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
        }
        if (!manifestAtributsMap.isEmpty()) {
            var version = manifestAtributsMap.get("Implementation-Version");
            var buildDate = manifestAtributsMap.get("Build-Timestamp");
            var buildJDK = manifestAtributsMap.get("Build-Jdk-Spec");
            var buildScmBranch = manifestAtributsMap.get("Implementation-SCM-Branch");
            var buildScmRevision = manifestAtributsMap.get("Implementation-SCM-Revision");
            manifestInfo = ManifestInfo.builder()
                    .version(version != null ? version.toString() : null)
                    .buildDate(buildDate != null ? getDate(buildDate.toString()) : null)
                    .buildJDK(buildJDK != null ? buildJDK.toString() : null)
                    .buildScmBranch(buildScmBranch != null ? buildScmBranch.toString() : null)
                    .buildScmRevision(buildScmRevision != null ? buildScmRevision.toString() : null)
                    .build();
        }
        return manifestInfo;
    }
	
    private Date getDate(String isoDate) {
        try {
            Instant instant = Instant.parse(isoDate);
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            System.out.println("El format de la data és incorrecte: " + e.getMessage());
            return null;
        }
    }
    
    @Builder
    @Getter
    public static class ManifestInfo {
        private final String version;
        private final Date buildDate;
        private final String buildJDK;
        private final String buildScmBranch;
        private final String buildScmRevision;
    }
}