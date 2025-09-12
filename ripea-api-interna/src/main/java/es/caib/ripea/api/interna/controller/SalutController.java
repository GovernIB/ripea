package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.ripea.service.intf.service.SalutService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "Integració comanda - RIPEA", description = "Publicació de dades de salut i informació de l'aplicació")
public class SalutController {

	private final SalutService salutService;
	private final ServletContext servletContext;
	private ManifestInfo manifestInfo;
	
    @GetMapping("/appInfo")
    public AppInfo appInfo(HttpServletRequest request) throws IOException {
    	autenticaAmbRolTothom();
        var manifestInfo = getManifestInfo();
        return AppInfo.builder()
                .codi("RIP")
                .nom("RIPEA")
                .data(manifestInfo.getBuildDate())
                .versio(manifestInfo.getVersion())
                .revisio(manifestInfo.getBuildScmRevision())
                .jdkVersion(manifestInfo.getBuildJDK())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)))
                .build();
    }
    
    @GetMapping("/salut")
    public SalutInfo health(HttpServletRequest request) throws IOException {
    	autenticaAmbRolTothom();
        var manifestInfo = getManifestInfo();
        return salutService.checkSalut(manifestInfo.getVersion(), request.getRequestURL().toString() + "Performance");
    }
    
    // ---------------------------------------------------- //

    private void autenticaAmbRolTothom() {
        User user = new User("$comanda_ripea", "comanda_ripea", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
    public String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null) // elimina el context path "/comandaapi/..."
                .build()
                .toUriString();
    }
    
    private ManifestInfo getManifestInfo() throws IOException {
        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }
        return manifestInfo;
    }
    
    private ManifestInfo buildManifestInfo() throws IOException {

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
    
    public static Date getDate(String isoDate) {
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