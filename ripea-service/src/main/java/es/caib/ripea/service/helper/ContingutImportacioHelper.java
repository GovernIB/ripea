package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.ImportacioRegistreParamsDto;
import es.caib.ripea.service.intf.dto.InteressatAdministracioDto;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.dto.ProgresImportacioSgdDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.exception.DocumentAlreadyImportedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.registre.RegistreInteressat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
public class ContingutImportacioHelper {

    @Autowired private DocumentHelper documentHelper;
    @Autowired private PluginHelper pluginHelper;
    @Autowired private ContingutHelper contingutHelper;
    @Autowired private ConfigHelper configHelper;
    @Autowired private ExpedientInteressatHelper expedientInteressatHelper;
    @Autowired private ExpedientHelper expedientHelper;
    @Autowired private CarpetaHelper carpetaHelper;
    @Autowired private MessageHelper messageHelper;
    
    private final ConcurrentMap<Long, ProgresImportacioSgdDto> progressos = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, AtomicBoolean> cancelacions = new ConcurrentHashMap<>();

    /** 
     * Versió asíncrona importació de documents (REACT)
     * 
     * @param usuari
     * @param entitat
     * @param rolActual
     * @param expedientId
     * @param params
     */
    @Async
    public void processarRegistreAsync(
            UsuariDto usuari,
            EntitatDto entitat,
            String rolActual,
            Long expedientId,
            ImportacioRegistreParamsDto params) {
    	Locale.setDefault(new Locale(usuari.getIdioma()));
    	
    	ProgresImportacioSgdDto progress = progressos.get(expedientId);

        try {
            inicialitzarContext(usuari, entitat);

            if (isCancelat(expedientId)) {
                progress.addError(messageHelper.getMessage("contingut.importacio.progres.interessats.cancelat"));
                progress.done();
                return;
            }

            List<InteressatDto> interessats = processarInteressats(params.getInteressats());
            List<ContingutArxiu> documentsTrobats = cercarDocumentsDinsSgd(params);

            int totalOperacions = interessats.size() + documentsTrobats.size();
            progress.start(totalOperacions);

            processarInteressatsAsync(entitat.getId(), expedientId, rolActual, interessats, progress);

            if (isCancelat(expedientId)) {
                progress.addError(messageHelper.getMessage("contingut.importacio.progres.documents.cancelat"));
                progress.done();
                return;
            }

            processarDocumentsAsync(entitat.getId(), expedientId, params, progress);

            expedientHelper.updateRegistresImportats(expedientId, params.getNumeroRegistre());

        } catch (Exception e) {
            progress.addError(e.getMessage());
            progress.done();
            throw e;
        } finally {
            progressos.put(expedientId, progress);
        }
    }

    public ProgresImportacioSgdDto inicialitzarProgres(Long contingutId) {
    	setCancelat(contingutId, false);
        ProgresImportacioSgdDto progress = new ProgresImportacioSgdDto();
        progressos.put(contingutId, progress);
        return progress;
    }

    public ProgresImportacioSgdDto obtenirProgresActual(Long contingutId) {
        ProgresImportacioSgdDto progress = progressos.get(contingutId);
        if (progress == null) {
            ProgresImportacioSgdDto buit = new ProgresImportacioSgdDto();
            buit.done();
            buit.setProgres(100);
            return buit;
        }
        return progress;
    }

    public void cancelarProcessament(Long contingutId) {
    	setCancelat(contingutId, true);
    	cancelacions.remove(contingutId);
        progressos.remove(contingutId);
    }

    /**
     * Versió síncrona importació de documents (JSP)
     * 
     * @param entitatId
     * @param contingutId
     * @param params
     * @param documentAlreadyHasExpedient
     * @param expedientsWithImportacio
     * @return
     */
    public int importarDocuments(
            Long entitatId,
            Long contingutId,
            ImportacioRegistreParamsDto params,
            Map<String, String> documentAlreadyHasExpedient,
            List<DocumentDto> expedientsWithImportacio) {

        ContingutEntity pareActual = contingutHelper.comprovarContingutDinsExpedientModificable(
                entitatId, contingutId, false, false, false, false, false, true, null);

        ExpedientEntity expedientSuperior = ContingutTipusEnumDto.EXPEDIENT.equals(pareActual.getTipus())
                ? (ExpedientEntity) pareActual
                : pareActual.getExpedient();

        List<ContingutArxiu> documentsTrobats = cercarDocumentsDinsSgd(params);
        List<Document> documents = documentsTrobats.stream()
                .map(arxiu -> pluginHelper.arxiuDocumentConsultar(null, arxiu.getIdentificador(), null, true, false))
                .collect(Collectors.toList());

        documents = findAndCorrectDuplicates(documents);

        int documentsRepetits = 0;
        FitxerDto fitxer = new FitxerDto();

        for (Document documentArxiu : documents) {
            fitxer.setNom(documentArxiu.getNom());
            fitxer.setContentType(documentArxiu.getContingut().getTipusMime());
            fitxer.setContingut(documentArxiu.getContingut().getContingut());
            fitxer.setTamany(documentArxiu.getContingut().getTamany());

            List<DocumentDto> jaImportats = documentHelper.findByArxiuUuid(documentArxiu.getIdentificador());
            if (jaImportats != null && !jaImportats.isEmpty() && !isIncorporacioDuplicadaPermesa()) {
                expedientsWithImportacio.addAll(jaImportats);
                documentsRepetits += jaImportats.size();
                continue;
            }

            prepararEstructuraCarpetes(entitatId, expedientSuperior.getId(), params);

            documentHelper.procesarDocumentImportacioNewTransaction(
            		entitatId,
            		contingutId,
            		documentArxiu,
            		buildFitxer(documentArxiu),
            		null,
                    params);

        }
        
        expedientHelper.updateRegistresImportats(expedientSuperior.getId(), params.getNumeroRegistre());
        return documentsRepetits;
    }

    private void prepararEstructuraCarpetes(
            Long entitatId,
            Long expedientId,
            ImportacioRegistreParamsDto params) {

        if (params.getEstructuraCarpetes() == null) {
            return;
        }

        Map<String, Long> carpetesCreades = carpetaHelper.crearEstructuraCarpetesNewTransaction(
                entitatId,
                params.getEstructuraCarpetes(),
                expedientId,
                params.getDestiId());

        Long destiId = resoldreDestiId(params.getDestiId(), carpetesCreades);
        if (destiId != null) {
            params.setDestiId(String.valueOf(destiId));
        }
        params.setEstructuraCarpetes(null);
    }

    private Long resoldreDestiId(String destiId, Map<String, Long> carpetesCreades) {
        try {
            return Long.valueOf(destiId);
        } catch (NumberFormatException nfe) {
            // Destí encara amb l'id temporal de jstree: es correspon amb una carpeta nova
            return carpetesCreades.get(destiId);
        }
    }

    private void processarInteressatsAsync(
            Long entitatId,
            Long expedientId,
            String rolActual,
            List<InteressatDto> interessats,
            ProgresImportacioSgdDto progress) {

        for (InteressatDto interessat : interessats) {
            try {
                if (isCancelat(expedientId)) {
                    progress.addError(messageHelper.getMessage("contingut.importacio.progres.interessats.cancelat.durant"));
                    progress.done();
                    return;
                }
                
	            expedientInteressatHelper.importarInteressatsNewTransaction(
	                    entitatId,
	                    expedientId,
	                    rolActual,
	                    List.of(interessat));

	            progress.addInteressatImportat();
	            progress.step(messageHelper.getMessage("contingut.importacio.progres.interessats.complet", new Object[] {interessat.getNomComplet()}));
            } catch (Exception e) {
            	String msg = interessat.getNomComplet() + ": " + e.getMessage();
            	progress.addError(msg);
            	progress.step(msg);
			}
        }
    }

    private void processarDocumentsAsync(
            Long entitatId,
            Long expedientId,
            ImportacioRegistreParamsDto params,
            ProgresImportacioSgdDto progress) {
        List<ContingutArxiu> contingutsArxiu = cercarDocumentsDinsSgd(params);
        List<Document> documents = contingutsArxiu.stream()
                .map(arxiu -> pluginHelper.arxiuDocumentConsultar(null, arxiu.getIdentificador(), null, true, false))
                .collect(Collectors.toList());

        documents = findAndCorrectDuplicates(documents);

        for (Document documentArxiu : documents) {
            try {
                if (isCancelat(expedientId)) {
                    progress.addError(messageHelper.getMessage("contingut.importacio.progres.cancelat"));
                    break;
                }
                
                validarDuplicats(documentArxiu);
                
                documentHelper.procesarDocumentImportacioNewTransaction(
                		entitatId,
                		expedientId,
                		documentArxiu, 
                		buildFitxer(documentArxiu),
                		progress,
                        params);

            	progress.addDocumentImportat();
                progress.step(messageHelper.getMessage("contingut.importacio.progres.documents.complet", new Object[] {documentArxiu.getNom()}));
            } catch (DocumentAlreadyImportedException e) {
            	String msg = messageHelper.getMessage("contingut.importacio.progres.documents.duplicat", new Object[] {documentArxiu.getNom()});
            	progress.addError(msg);
            	progress.step(msg);
            } catch (Exception e) {
            	String msg = documentArxiu.getNom() + ": " + e.getMessage();
            	progress.addError(msg);
            	progress.step(msg);
			}
        }

        progress.done();
    }

    private FitxerDto buildFitxer(Document documentArxiu) {
        FitxerDto fitxer = new FitxerDto();
        fitxer.setNom(documentArxiu.getNom());
        fitxer.setContentType(documentArxiu.getContingut().getTipusMime());
        fitxer.setContingut(documentArxiu.getContingut().getContingut());
        fitxer.setTamany(documentArxiu.getContingut().getTamany());
        return fitxer;
    }

    private void validarDuplicats(Document documentArxiu) {
        List<?> jaImportats = documentHelper.findByArxiuUuid(documentArxiu.getIdentificador());
        if (jaImportats != null && !jaImportats.isEmpty() && !isIncorporacioDuplicadaPermesa()) {
            throw new DocumentAlreadyImportedException();
        }
    }

    private boolean isIncorporacioDuplicadaPermesa() {
        return configHelper.getAsBoolean(PropertyConfig.INCORPORACIO_ANOTACIO_DUPLICADA);
    }

    private List<ContingutArxiu> cercarDocumentsDinsSgd(ImportacioRegistreParamsDto params) {
        List<ContingutArxiu> documents = pluginHelper.importarDocumentsArxiu(params);
        if (documents == null || documents.isEmpty()) {
            throw new ValidationException("No s'han trobat registres amb les dades especificades");
        }
        return documents;
    }

    private List<Document> findAndCorrectDuplicates(List<Document> documents) {
        int idx = 1;
        List<Document> corrected = new ArrayList<>();
        Set<String> uniques = new HashSet<>();

        for (Document document : documents) {
            String tituloDoc = (String) document.getMetadades().getMetadadaAddicional("tituloDoc");
            if (!uniques.add(tituloDoc)) {
                document.getMetadades().addMetadadaAddicional("tituloDoc", tituloDoc + "_" + idx);
                idx++;
            }
            corrected.add(document);
        }
        return corrected;
    }
    
    private void inicialitzarContext(UsuariDto usuari, EntitatDto entitat) {
        createAuthenticationContext(usuari);
        ConfigHelper.setEntitat(entitat);
    }

    private void createAuthenticationContext(UsuariDto usuariActual) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) return;

        List<SimpleGrantedAuthority> authorities = Arrays.stream(usuariActual.getRols())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = new User(usuariActual.getCodi(), "", authorities);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<InteressatDto> processarInteressats(List<RegistreInteressat> interessats) {
        List<InteressatDto> interessatsDto = new ArrayList<>();
        if (interessats == null) return interessatsDto;

        for (RegistreInteressat registre : interessats) {
            interessatsDto.add(toInteressatDto(registre));
        }
        return interessatsDto;
    }

    private InteressatDto toInteressatDto(RegistreInteressat registre) {
        if (registre == null) return null;
        switch (registre.getTipus()) {
            case "1":
                InteressatAdministracioDto adm = new InteressatAdministracioDto();
                adm.setTipus(InteressatTipusEnumDto.ADMINISTRACIO);
                adm.setDocumentTipus(toInteressatDocumentTipusEnumDto(registre.getDocumentTipus()));
                adm.setDocumentNum(registre.getDocumentNum());
                adm.setOrganNom(registre.getNom());
                adm.setOrganCodi(registre.getDocumentNum());
                adm.setPais(registre.getPais());
                adm.setProvincia(registre.getProvincia());
                adm.setMunicipi(registre.getMunicipi());
                adm.setAdresa(registre.getAdresa());
                adm.setCodiPostal(registre.getCodiPostal());
                adm.setEmail(registre.getEmail());
                adm.setTelefon(registre.getTelefon());
                adm.setObservacions(registre.getObservacions());
                adm.setRepresentant(toInteressatDto(registre.getRepresentant()));
                return adm;
            case "2":
                InteressatPersonaFisicaDto pf = new InteressatPersonaFisicaDto();
                pf.setTipus(InteressatTipusEnumDto.PERSONA_FISICA);
                pf.setDocumentTipus(toInteressatDocumentTipusEnumDto(registre.getDocumentTipus()));
                pf.setDocumentNum(registre.getDocumentNum());
                pf.setNom(registre.getNom());
                pf.setLlinatge1(registre.getLlinatge1());
                pf.setLlinatge2(registre.getLlinatge2());
                pf.setPais(registre.getPais());
                pf.setProvincia(registre.getProvincia());
                pf.setMunicipi(registre.getMunicipi());
                pf.setAdresa(registre.getAdresa());
                pf.setCodiPostal(registre.getCodiPostal());
                pf.setEmail(registre.getEmail());
                pf.setTelefon(registre.getTelefon());
                pf.setObservacions(registre.getObservacions());
                pf.setRepresentant(toInteressatDto(registre.getRepresentant()));
                return pf;
            case "3":
                InteressatPersonaJuridicaDto pj = new InteressatPersonaJuridicaDto();
                pj.setTipus(InteressatTipusEnumDto.PERSONA_JURIDICA);
                pj.setDocumentTipus(toInteressatDocumentTipusEnumDto(registre.getDocumentTipus()));
                pj.setDocumentNum(registre.getDocumentNum());
                pj.setRaoSocial(registre.getNom());
                pj.setPais(registre.getPais());
                pj.setProvincia(registre.getProvincia());
                pj.setMunicipi(registre.getMunicipi());
                pj.setAdresa(registre.getAdresa());
                pj.setCodiPostal(registre.getCodiPostal());
                pj.setEmail(registre.getEmail());
                pj.setTelefon(registre.getTelefon());
                pj.setObservacions(registre.getObservacions());
                pj.setRepresentant(toInteressatDto(registre.getRepresentant()));
                return pj;
            default:
                return null;
        }
    }

    private InteressatDocumentTipusEnumDto toInteressatDocumentTipusEnumDto(String documentTipus) {
        if (documentTipus == null) return null;
        switch (documentTipus) {
            case "N": case "C": return InteressatDocumentTipusEnumDto.NIF;
            case "P": return InteressatDocumentTipusEnumDto.PASSAPORT;
            case "E": return InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS;
            case "X": return InteressatDocumentTipusEnumDto.ALTRES_DE_PERSONA_FISICA;
            case "O": return InteressatDocumentTipusEnumDto.CODI_ORIGEN;
            default: return null;
        }
    }
    
    private boolean isCancelat(Long expedientId) {
    	return cancelacions != null && (cancelacions.get(expedientId) == null || cancelacions.get(expedientId).get());
    }
    
    private void setCancelat(Long expedientId, boolean cancelat) {
    	cancelacions.put(expedientId, new AtomicBoolean(cancelat));
    }
	
    @Getter
    @Setter
    @AllArgsConstructor
    private class DestinacioImportacio {
        private ContingutEntity contenidor;
        private ExpedientEntity expedient;
        private boolean carpeta;
    }
}
