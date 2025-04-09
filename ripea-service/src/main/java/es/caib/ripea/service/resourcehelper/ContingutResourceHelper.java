package es.caib.ripea.service.resourcehelper;

import es.caib.plugins.arxiu.api.*;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContingutResourceHelper {

    private final TipusDocumentalRepository tipusDocumentalRepository;
    private final OrganGestorRepository organGestorRepository;

    private final EntityComprovarHelper entityComprovarHelper;
    private final PluginHelper pluginHelper;
    private final ConversioTipusHelper conversioTipusHelper;
    private final ContingutHelper contingutHelper;

    public ArxiuDetallDto getArxiuDetall(Long entitatId, Long contingutId) {
        ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
                entitatId, contingutId, true, false);

        if (contingut instanceof ExpedientEntity) {
            // ##################### EXPEDIENT ##################################
            Expedient arxiuExpedient = pluginHelper.arxiuExpedientConsultar(
                    (ExpedientEntity)contingut);
            return getArxiuExpedientDetall(arxiuExpedient);
        } else if (contingut instanceof DocumentEntity) {
            // ##################### DOCUMENT ##################################
            Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
                    (DocumentEntity) contingut,
                    null,
                    null,
                    true);
            return getArxiuDocumentDetall(arxiuDocument, entitatId);
        } else if (contingut instanceof CarpetaEntity) {
            // ##################### CARPETA ##################################
            Carpeta arxiuCarpeta = pluginHelper.arxiuCarpetaConsultar(
                    (CarpetaEntity)contingut);
            return getArxiuCarpeta(arxiuCarpeta);
        } else {
            throw new ValidationException(
                    contingutId,
                    ContingutEntity.class,
                    "Tipus de contingut desconegut: " + contingut.getClass().getName());
        }
    }

    public ArxiuDetallDto getArxiuExpedientDetall(Expedient arxiuExpedient){
        ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();

        arxiuDetall.setFills(getArxiuContinguts(arxiuExpedient.getContinguts()));
        arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
        arxiuDetall.setNom(arxiuExpedient.getNom());
        ExpedientMetadades metadades = arxiuExpedient.getMetadades();
        if (metadades != null) {
            arxiuDetall.setEniVersio(metadades.getVersioNti());
            arxiuDetall.setEniIdentificador(metadades.getIdentificador());
            arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
            arxiuDetall.setEniDataObertura(metadades.getDataObertura());
            arxiuDetall.setEniClassificacio(metadades.getClassificacio());
            if (metadades.getEstat() != null) {
                switch (metadades.getEstat()) {
                    case OBERT:
                        arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
                        break;
                    case TANCAT:
                        arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
                        break;
                    case INDEX_REMISSIO:
                        break;
                }
            }
            arxiuDetall.setEniInteressats(metadades.getInteressats());
            arxiuDetall.setEniOrgans(getOrgansAmbNoms(metadades.getOrgans()));
            arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
        }
        return arxiuDetall;
    }
    public ArxiuDetallDto getArxiuDocumentDetall(Document arxiuDocument, Long entitatId){
        ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();

        arxiuDetall.setFirmes(getArxiuFirma(arxiuDocument.getFirmes()));

        arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
        arxiuDetall.setNom(arxiuDocument.getNom());
        DocumentMetadades metadades = arxiuDocument.getMetadades();
        if (metadades != null) {
            arxiuDetall.setEniVersio(metadades.getVersioNti());
            arxiuDetall.setEniIdentificador(metadades.getIdentificador());
            arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
            arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());

            arxiuDetall.setEniOrigen(ArxiuConversions.getOrigen(metadades.getOrigen()));

            arxiuDetall.setEniEstatElaboracio(ArxiuConversions.getEstatElaboracio(metadades.getEstatElaboracio()));

            if (metadades.getTipusDocumental() != null) {
                List<TipusDocumentalEntity> tipos = tipusDocumentalRepository.findByCodi(metadades.getTipusDocumental().toString());
                if (Utils.isNotEmpty(tipos)) {
                    TipusDocumentalDto tipus = conversioTipusHelper.convertir(tipos.get(0), TipusDocumentalDto.class);
                    arxiuDetall.setEniTipusDocumental(tipus.getCodiNom());
                } else {
                    arxiuDetall.setEniTipusDocumental(metadades.getTipusDocumental().toString());
                }
            }

            if (metadades.getTipusDocumental() == null && metadades.getTipusDocumentalAddicional() != null) {
                EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
                        entitatId,
                        false,
                        false,
                        false,
                        true, false);

                TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
                        metadades.getTipusDocumentalAddicional(),
                        entitat);

                if (tipusDocumental != null) {
                    arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
                } else {
                    List<TipusDocumentalDto> docsAddicionals = pluginHelper.documentTipusAddicionals();

                    for (TipusDocumentalDto docAddicional : docsAddicionals) {
                        if (docAddicional.getCodi().equals(metadades.getTipusDocumentalAddicional())) {
                            arxiuDetall.setEniTipusDocumentalAddicional(docAddicional.getNom());
                        }
                    }
                }
            }

            arxiuDetall.setEniOrgans(getOrgansAmbNoms(metadades.getOrgans()));
            if (metadades.getFormat() != null) {
                arxiuDetall.setEniFormat(metadades.getFormat().toString());
            }
            arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());
            arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());

            if (arxiuDocument.getContingut() != null) {
                arxiuDetall.setContingutArxiuNom(
                        arxiuDocument.getContingut().getArxiuNom());
                arxiuDetall.setContingutTipusMime(
                        arxiuDocument.getContingut().getTipusMime());
            }

        }
        if (arxiuDocument.getEstat() != null) {
            if (DocumentEstat.ESBORRANY.equals(arxiuDocument.getEstat()))
                arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.ESBORRANY);
            else if (DocumentEstat.DEFINITIU.equals(arxiuDocument.getEstat()))
                arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
        }

        return arxiuDetall;
    }
    public ArxiuDetallDto getArxiuCarpeta(Carpeta arxiuCarpeta){
        ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();

        arxiuDetall.setFills(getArxiuContinguts(arxiuCarpeta.getContinguts()));
        arxiuDetall.setIdentificador(arxiuCarpeta.getIdentificador());
        arxiuDetall.setNom(arxiuCarpeta.getNom());

        return arxiuDetall;
    }

    private List<ArxiuFirmaDto> getArxiuFirma(List<Firma> firmes){
        // ##################### FIRMES ##################################
        List<ArxiuFirmaDto> dtos = new ArrayList<ArxiuFirmaDto>();
        if (firmes != null) {
            for (Firma firma: firmes) {
                ArxiuFirmaDto dto = new ArxiuFirmaDto();

                if (firma.getTipus() != null) {
                    switch (firma.getTipus()) {
                        case CSV:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.CSV);
                            break;
                        case XADES_DET:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
                            break;
                        case XADES_ENV:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
                            break;
                        case CADES_DET:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
                            break;
                        case CADES_ATT:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
                            break;
                        case PADES:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.PADES);
                            break;
                        case SMIME:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
                            break;
                        case ODT:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.ODT);
                            break;
                        case OOXML:
                            dto.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
                            break;
                    }
                }
                if (firma.getPerfil() != null) {
                    switch (firma.getPerfil()) {
                        case BES:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
                            break;
                        case EPES:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
                            break;
                        case LTV:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTV);
                            break;
                        case T:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.T);
                            break;
                        case C:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.C);
                            break;
                        case X:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.X);
                            break;
                        case XL:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.XL);
                            break;
                        case A:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.A);
                            break;
                        case BASIC:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASIC);
                            break;
                        case Basic:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.Basic);
                            break;
                        case BASELINE_B_LEVEL:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_B_LEVEL);
                            break;
                        case BASELINE_LTA_LEVEL:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LTA_LEVEL);
                            break;
                        case BASELINE_LT_LEVEL:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LT_LEVEL);
                            break;
                        case BASELINE_T:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T);
                            break;
                        case BASELINE_T_LEVEL:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T_LEVEL);
                            break;
                        case LTA:
                            dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTA);
                            break;
                    }
                }
                dto.setFitxerNom(firma.getFitxerNom());
                if (ArxiuFirmaTipusEnumDto.CSV.equals(dto.getTipus())) {
                    dto.setContingut(firma.getContingut());
                }
                dto.setTipusMime(firma.getTipusMime());
                dto.setCsvRegulacio(firma.getCsvRegulacio());
                dtos.add(dto);
            }
        }
        return dtos;
    }
    private List<ArxiuContingutDto> getArxiuContinguts(List<ContingutArxiu> continguts){
        List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
        if (continguts != null) {
            for (ContingutArxiu cont: continguts) {
                ArxiuContingutDto detallFill = new ArxiuContingutDto();
                detallFill.setIdentificador(
                        cont.getIdentificador());
                detallFill.setNom(
                        cont.getNom());
                if (cont.getTipus() != null) {
                    switch (cont.getTipus()) {
                        case EXPEDIENT:
                            detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
                            break;
                        case DOCUMENT:
                            detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
                            break;
                        case CARPETA:
                            detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
                            break;
                    }
                }
                detallFills.add(detallFill);
            }
        }
        return detallFills;
    }
    private List<String> getOrgansAmbNoms(List<String> organsCodis) {
        List<String> organsCodisNoms = new ArrayList<>();
        if (Utils.isNotEmpty(organsCodis)) {
            for (String organCodi : organsCodis) {
                OrganGestorEntity organ = organGestorRepository.findByCodi(organCodi);
                if (organ != null) {
                    organsCodisNoms.add(organ.getCodiINom());
                } else {
                    organsCodisNoms.add(organCodi);
                }
            }
        }

        return organsCodisNoms;
    }
}
