package es.caib.ripea.back.resourcecontroller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.ExpedientHelper;
import es.caib.ripea.back.helper.FluxFirmaHelper;
import es.caib.ripea.back.helper.MetaExpedientHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.back.helper.SeguimentEnviamentsUsuariHelper;
import es.caib.ripea.service.intf.base.permission.UserPermissionInfo;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.GrupService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Servei REST de gestió d'usuaris.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/usuari")
@Tag(name = "Usuaris", description = "Servei de gestió d'usuaris")
public class UsuariResourceController extends BaseMutableResourceController<UsuariResource, String> {

    @Value("${es.caib.ripea.develope.mode:false}")
    private boolean developmentMode;
    @Value("${es.caib.ripea.develope.user:rip_admin}")
    private String developmentUser;
    @Value("${es.caib.ripea.develope.roles:IPA_SUPER,IPA_ADMIN,IPA_ORGAN_ADMIN,tothom}")
    private String developmentRoles;

    private final MetaExpedientService metaExpedientService;
    private final EntitatService entitatService;
    private final OrganGestorService organGestorService;
    private final AplicacioService aplicacioService;
    private final EventService eventService;
    private final GrupService grupService;
    private final SseResourceController sseResourceController;

    private static final Logger logger = LoggerFactory.getLogger(UsuariResourceController.class);

    @Hidden
    @GetMapping("/actual/securityInfo")
    @PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
    public ResponseEntity<UserPermissionInfo> getUsuariActualSecurityInfo(HttpServletRequest request) throws MethodArgumentNotValidException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (developmentMode && (auth == null || "anonymousUser".equals(auth.getName()))) {

            // 1. Crear un Authentication personalitzat
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    developmentUser,
                    null,
                    Arrays.stream(developmentRoles.split(","))
                            .map(String::trim)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );

            // 2. Assignar el Authentication al SecurityContext
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 3. Sincronitzar el SecurityContext amb la sessió HTTP
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication().getName() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
        entitatActual.setOrgansGestors(EntitatHelper.findOrganismesEntitatAmbPermisCacheByRol(request, organGestorService, entitatActual));
        String rolActual = RolHelper.getRolActual(request);
        aplicacioService.actualitzarRolThreadLocal(rolActual);
        entitatService.setConfigEntitat(entitatActual);
        OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
        
        List<String> roles = RolHelper.getRolsUsuariActual(request);
        List<String> rolesAuth = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("IPA_") || role.equals("tothom"))
                .collect(Collectors.toList());

        UserPermissionInfo userPermissionInfo = ((UsuariResourceService) readonlyResourceService).getCurrentUserPermissionInfo();
        userPermissionInfo.setEntitatActualId(entitatActual != null ? entitatActual.getId() : null);
        userPermissionInfo.setOrganActualId(organActual != null ? organActual.getId() : null);
        userPermissionInfo.setRolActual(rolActual);
        userPermissionInfo.setRols(roles);
        userPermissionInfo.setAuth(rolesAuth);
        userPermissionInfo.setSessionScope(getUsuariActualAdditionalInfo(request, userPermissionInfo, organActual));
        
        return ResponseEntity.ok(userPermissionInfo);
    }

    @Hidden
    @PostMapping("/actual/changeInfo")
    @PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
    public ResponseEntity<UserPermissionInfo> postActualInfo(HttpServletRequest request, @RequestBody Map<String, Object> response) throws MethodArgumentNotValidException {

        // Les claus absents al cos de la petició no s'han de tractar com a canvis: el front React envia
        // només la clau modificada (canviEntitat, canviOrganGestor o canviRol). Convertim-les a null real
        // (i no a la cadena "null") perquè cada helper processi i notifiqui únicament la dimensió canviada.
        String canviEntitat = response.get("canviEntitat") != null ? String.valueOf(response.get("canviEntitat")) : null;
        String canviOrganGestor = response.get("canviOrganGestor") != null ? String.valueOf(response.get("canviOrganGestor")) : null;
        String canviRol = response.get("canviRol") != null ? String.valueOf(response.get("canviRol")) : null;
        Boolean canviInformacioExpandit = response.get("canviInformacioExpandit") != null
                ? Boolean.valueOf(String.valueOf(response.get("canviInformacioExpandit")))
                : null;

        EntitatHelper.processarCanviEntitats(request, canviEntitat, entitatService, aplicacioService, organGestorService, eventService);
        EntitatHelper.findOrganismesEntitatAmbPermisCache(request, organGestorService);
        EntitatHelper.processarCanviOrganGestor(request, canviOrganGestor, aplicacioService, eventService);
        EntitatHelper.findEntitatsAccessibles(request, entitatService);
        RolHelper.processarCanviRols(request, canviRol, aplicacioService, organGestorService, eventService);
        RolHelper.setRolActualFromDb(request, aplicacioService);

        if (canviInformacioExpandit != null) {
            aplicacioService.setInformacioExpedientExpandit(canviInformacioExpandit);
        }

        try {
            String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
            sseResourceController.actualitzarAvisosPerUsuari(usuariCodi);
        } catch (Exception e) {
            logger.error("Error en enviar l'actualització d'avisos per SSE al canviar d'entitat", e);
        }

        return getUsuariActualSecurityInfo(request);
    }

    @Hidden
    private Map<String, Object> getUsuariActualAdditionalInfo(
    		HttpServletRequest request,
    		UserPermissionInfo userPermissionInfo,
    		OrganGestorDto organActual) {
    	
        Map<String, Object> response = new HashMap<>();

        response.put("organsNoSincronitzats", MetaExpedientHelper.getOrgansNoSincronitzats(request));
        response.put("urlsInstruccioActiu", ExpedientHelper.isUrlsInstruccioActiu(request));
        response.put("isCreacioFluxUsuariActiu", FluxFirmaHelper.isCreacioFluxUsuariActiu(request));
        response.put("teAccesEstadistiques", ExpedientHelper.teAccesEstadistiques(request));
        response.put("isMostrarSeguimentEnviamentsUsuariActiu", SeguimentEnviamentsUsuariHelper.isMostrarSeguimentEnviamentsUsuariActiu(request));

        // Optimització: recuperam totes les propietats de configuració necessàries en una sola crida
        // (2 consultes batch a BD, no una per propietat), amb la mateixa semàntica de resolució que
        // propertyFindByNom (òrgan → entitat → general). Evita ~30 preses de connexió del pool per petició.
        Properties props = aplicacioService.getConfigs(Arrays.asList(
                PropertyConfig.CONVERSIO_DEFINITIU,
                PropertyConfig.VALIDACIO_URL_IMPRIMIBLES,
                PropertyConfig.DOCUMENTS_GENERALS_ACTIUS,
                PropertyConfig.TIPUS_DOCUMENT_ACTIUS,
                PropertyConfig.GENERAR_URL_INSTRUCCIO,
                PropertyConfig.MAX_UPLOAD_FILE,
                PropertyConfig.DOMINIS_HABILITATS,
                PropertyConfig.EXPORTACIO_EXCEL,
                PropertyConfig.EXPORTACIO_INSIDE,
                PropertyConfig.IMPRIMIBLE_NO_FIRMAT_ACTIU,
                PropertyConfig.PUBLICAR_DOCUMENTS_ACTIVA,
                PropertyConfig.MOURER_DOCUMENTS_ACTIU,
                PropertyConfig.VINCULAR_DOCUMENTS_ACTIU,
                PropertyConfig.REOBRIR_EXPEDIENT_TANCAT,
                PropertyConfig.TANCAMENT_LOGIC,
                PropertyConfig.CARPETES_LOGIQUES_ACTIVES,
                PropertyConfig.MODIFICAR_DOCUMENTS_CUSTODIATS,
                PropertyConfig.CARPETES_CREACIO_ACTIVA,
                PropertyConfig.IMPORTACIO_ACTIVA,
                PropertyConfig.INCORPORAR_JUSTIFICANT,
                PropertyConfig.IMPORTACIO_RELACIONATS_ACTIVA,
                PropertyConfig.PORTAFIB_PLUGIN_USUARISPF_WS,
                PropertyConfig.ORDENACIO_CONTINGUT_ACTIU,
                PropertyConfig.CARPETA_DETALL_ACCES_ACTIVA,
                PropertyConfig.MOURE_MATEIX_EXPEDIENTS,
                PropertyConfig.PERMATRE_ESBORRAR_FINAL,
                PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA,
                PropertyConfig.MOURE_TOT,
                PropertyConfig.PROPAGAR_METADADES,
                PropertyConfig.CARPETES_PER_DEFECTE,
                PropertyConfig.CARPETES_RESTRINGIR_ACTIU,
                PropertyConfig.PERMETRE_OBLIGAR_INTERESSAT,
                PropertyConfig.MAX_RESULTS_SELECT));

        response.put("isConvertirDefinitiuActiu", Boolean.parseBoolean(props.getProperty(PropertyConfig.CONVERSIO_DEFINITIU)));
        response.put("isUrlValidacioDefinida", props.getProperty(PropertyConfig.VALIDACIO_URL_IMPRIMIBLES)!=null);
        response.put("isDocumentsGeneralsEnabled", Boolean.parseBoolean(props.getProperty(PropertyConfig.DOCUMENTS_GENERALS_ACTIUS)));
        response.put("isTipusDocumentsEnabled", Boolean.parseBoolean(props.getProperty(PropertyConfig.TIPUS_DOCUMENT_ACTIUS)));       
        response.put("isUrlInstruccioEnabled", Boolean.parseBoolean(props.getProperty(PropertyConfig.GENERAR_URL_INSTRUCCIO)));
        response.put("maxUploadFileSize", props.getProperty(PropertyConfig.MAX_UPLOAD_FILE));
        response.put("isDominisEnabled", Boolean.parseBoolean(props.getProperty(PropertyConfig.DOMINIS_HABILITATS)));
        response.put("isExportacioExcelActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.EXPORTACIO_EXCEL)));
        response.put("isExportacioInsideActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.EXPORTACIO_INSIDE)));
        response.put("imprimibleNoFirmats", Boolean.parseBoolean(props.getProperty(PropertyConfig.IMPRIMIBLE_NO_FIRMAT_ACTIU)));
        response.put("isMostrarPublicar", Boolean.parseBoolean(props.getProperty(PropertyConfig.PUBLICAR_DOCUMENTS_ACTIVA)));
        response.put("isMostrarCopiar", Boolean.parseBoolean(props.getProperty(PropertyConfig.MOURER_DOCUMENTS_ACTIU)));
        response.put("isMostrarVincular", Boolean.parseBoolean(props.getProperty(PropertyConfig.VINCULAR_DOCUMENTS_ACTIU)));
        response.put("isReobrirPermes", Boolean.parseBoolean(props.getProperty(PropertyConfig.REOBRIR_EXPEDIENT_TANCAT)));
        response.put("isTancamentLogicActiu", Boolean.parseBoolean(props.getProperty(PropertyConfig.TANCAMENT_LOGIC)));
        response.put("isCreacioCarpetesLogica", Boolean.parseBoolean(props.getProperty(PropertyConfig.CARPETES_LOGIQUES_ACTIVES)));
        response.put("isPermesModificarCustodiats", Boolean.parseBoolean(props.getProperty(PropertyConfig.MODIFICAR_DOCUMENTS_CUSTODIATS)));
        response.put("isCreacioCarpetesActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.CARPETES_CREACIO_ACTIVA)));
        response.put("isMostrarImportacio", Boolean.parseBoolean(props.getProperty(PropertyConfig.IMPORTACIO_ACTIVA)));
        response.put("isIncorporacioJustificantActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.INCORPORAR_JUSTIFICANT)));
        response.put("isImportacioRelacionatsActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.IMPORTACIO_RELACIONATS_ACTIVA)));
        response.put("isWsUsuariEntitatActiu", Boolean.parseBoolean(props.getProperty(PropertyConfig.PORTAFIB_PLUGIN_USUARISPF_WS)));
        response.put("ordenacioContingutPermesa", Boolean.parseBoolean(props.getProperty(PropertyConfig.ORDENACIO_CONTINGUT_ACTIU)));
        response.put("isContingutCarpetaDetallAccesActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.CARPETA_DETALL_ACCES_ACTIVA)));
        response.put("moureMateixExpedients", Boolean.parseBoolean(props.getProperty(PropertyConfig.MOURE_MATEIX_EXPEDIENTS)));
        response.put("permesEsborrarFinals", Boolean.parseBoolean(props.getProperty(PropertyConfig.PERMATRE_ESBORRAR_FINAL)));
        response.put("isRevisioActiva", Boolean.parseBoolean(props.getProperty(PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA)));
        response.put("isExpedientMoureTotActiu", Boolean.parseBoolean(props.getProperty(PropertyConfig.MOURE_TOT)));
        response.put("isPropagarMetadades", Boolean.parseBoolean(props.getProperty(PropertyConfig.PROPAGAR_METADADES)));
        response.put("isCarpetesDefecte", Boolean.parseBoolean(props.getProperty(PropertyConfig.CARPETES_PER_DEFECTE)));
        response.put("isRestringirCarpetesActiu", Boolean.parseBoolean(props.getProperty(PropertyConfig.CARPETES_RESTRINGIR_ACTIU)));
        response.put("isObligarInteressatActiu", Boolean.parseBoolean(props.getProperty(PropertyConfig.PERMETRE_OBLIGAR_INTERESSAT)));
        String maxResultsSelects = props.getProperty(PropertyConfig.MAX_RESULTS_SELECT);
        response.put("maxResultSelects", maxResultsSelects!=null?Integer.parseInt(maxResultsSelects):30);

        if ("IPA_ADMIN".equals(userPermissionInfo.getRolActual()) && userPermissionInfo.getEntitatActualId()!=null) {
        	try {
        		response.put("numProcsPendentsRevisio", metaExpedientService.countMetaExpedientsPendentRevisar(userPermissionInfo.getEntitatActualId()));
        	} catch (Exception ex) {}
        }
        		
        List<GrupDto> grupsPermesos = grupService.findGrupsPermesosProcedimentsGestioActiva(
	        		userPermissionInfo.getEntitatActualId(),
	        		userPermissionInfo.getRolActual(),
	        		organActual!=null ? organActual.getId() : null);
        
        response.put("isFiltreGrupsVisible", (grupsPermesos!=null && !grupsPermesos.isEmpty()));
        
        return response;
    }

}