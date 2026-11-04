package es.caib.ripea.service.resourceservice;

import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.PinbalServeiResourceEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.PinbalServeiRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.PinbalHelper;
import es.caib.ripea.service.helper.PinbalServeiUtils;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ResourceNotCreatedException;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.dto.PinbalServeiDocPermesEnumDto;
import es.caib.ripea.service.intf.model.PinbalServeiResource;
import es.caib.ripea.service.intf.resourceservice.PinbalServeiResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manteniment dels serveis de PINBAL (superadmin).
 *
 * L'alta d'un servei es fa triant-lo entre els de PINBAL: primer una unitat organitzativa (una entitat amb CIF
 * o un òrgan gestor que consulta PINBAL amb el seu CIF) i després un dels serveis actius de l'entitat de PINBAL
 * amb el mateix CIF que encara no estigui donat d'alta a RIPEA. En triar el servei s'emplenen el codi, el nom,
 * els documents permesos i l'actiu amb les dades de PINBAL
 * (API v2 de recobriment, getServeisPerEntitat).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PinbalServeiResourceServiceImpl extends BaseMutableResourceService<PinbalServeiResource, Long, PinbalServeiResourceEntity> implements PinbalServeiResourceService {

    private final PinbalHelper pinbalHelper;
    private final MessageHelper messageHelper;
    private final PinbalServeiRepository pinbalServeiRepository;
    private final EntitatRepository entitatRepository;
    private final OrganGestorRepository organGestorRepository;

    @PostConstruct
    public void init() {
        register(PinbalServeiResource.Fields.unitatOrganitzativa, new UnitatOrganitzativaOptionsProvider());
        register(PinbalServeiResource.Fields.unitatOrganitzativa, new UnitatOrganitzativaOnChangeLogicProcessor());
        register(PinbalServeiResource.Fields.serveiPinbal, new ServeiPinbalOptionsProvider());
        register(PinbalServeiResource.Fields.serveiPinbal, new ServeiPinbalOnChangeLogicProcessor());
    }

    private void updatePinbalServeiDocPermes(PinbalServeiResourceEntity entity, List<PinbalServeiDocPermesEnumDto> permes) {
        if (permes != null) {
            entity.setPinbalServeiDocPermesDni(permes.contains(PinbalServeiDocPermesEnumDto.DNI));
            entity.setPinbalServeiDocPermesNif(permes.contains(PinbalServeiDocPermesEnumDto.NIF));
            entity.setPinbalServeiDocPermesCif(permes.contains(PinbalServeiDocPermesEnumDto.CIF));
            entity.setPinbalServeiDocPermesNie(permes.contains(PinbalServeiDocPermesEnumDto.NIE));
            entity.setPinbalServeiDocPermesPas(permes.contains(PinbalServeiDocPermesEnumDto.PASSAPORT));
        }
    }

    @Override
    protected void beforeCreateSave(PinbalServeiResourceEntity entity, PinbalServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        // La columna CODI no té restricció d'unicitat i hi ha consultes que esperen un sol servei per codi.
        if (resource.getCodi() != null && pinbalServeiRepository.existsByCodiIgnoreCase(resource.getCodi())) {
            throw new ResourceNotCreatedException(
                    getResourceClass(),
                    messageHelper.getMessage("pinbal.servei.alta.duplicat", new Object[] { resource.getCodi() }));
        }
        this.updatePinbalServeiDocPermes(entity, resource.getPinbalServeiDocPermesEnum());
    }

    @Override
    protected void beforeUpdateSave(PinbalServeiResourceEntity entity, PinbalServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.updatePinbalServeiDocPermes(entity, resource.getPinbalServeiDocPermesEnum());
    }

    @Override
    protected void afterConversion(PinbalServeiResourceEntity entity, PinbalServeiResource resource) {
        if (resource.isPinbalServeiDocPermesDni())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.DNI);
        if (resource.isPinbalServeiDocPermesNif())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.NIF);
        if (resource.isPinbalServeiDocPermesCif())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.CIF);
        if (resource.isPinbalServeiDocPermesNie())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.NIE);
        if (resource.isPinbalServeiDocPermesPas())
            resource.getPinbalServeiDocPermesEnum().add(PinbalServeiDocPermesEnumDto.PASSAPORT);
    }

    /** Unitat organitzativa (entitat o òrgan gestor) des de la qual es consulten els serveis de PINBAL. */
    private static class UnitatOrganitzativa {
        private final String nom;
        private final String cif;
        UnitatOrganitzativa(String nom, String cif) {
            this.nom = nom;
            this.cif = cif;
        }
    }

    /**
     * Cerca la unitat organitzativa per id: primer com a entitat amb CIF i després com a òrgan gestor amb CIF i
     * {@code utilitzarCifPinbal}. La seqüència d'ids és compartida per totes les taules, així que només pot ser una
     * de les dues.
     *
     * @throws ServeisPinbalNoDisponiblesException si l'id no correspon a cap unitat vàlida.
     */
    private UnitatOrganitzativa getUnitatOrganitzativa(String unitatId) {
        Long id = null;
        try {
            id = Long.valueOf(unitatId);
        } catch (NumberFormatException ex) {
            // Un id no numèric es tracta igual que un id inexistent.
        }
        if (id != null) {
            EntitatEntity entitat = entitatRepository.findById(id).orElse(null);
            if (entitat != null && entitat.getCif() != null) {
                return new UnitatOrganitzativa(entitat.getNom(), entitat.getCif());
            }
            OrganGestorEntity organ = organGestorRepository.findById(id).orElse(null);
            if (organ != null && organ.isUtilitzarCifPinbal() && organ.getCif() != null) {
                return new UnitatOrganitzativa(organ.getCodi() + " - " + organ.getNom(), organ.getCif());
            }
        }
        throw new ServeisPinbalNoDisponiblesException(
                messageHelper.getMessage("pinbal.servei.alta.unitat.no.valida", new Object[] { unitatId }));
    }

    /**
     * Serveis actius de PINBAL que encara no estan donats d'alta a RIPEA, de l'entitat de PINBAL que té el mateix
     * CIF que la unitat organitzativa.
     *
     * @throws ServeisPinbalNoDisponiblesException si no hi ha cap entitat a PINBAL amb aquest CIF.
     */
    private List<ServeiBasic> getServeisNous(UnitatOrganitzativa unitat) {
        Entitat entitatPinbal = PinbalServeiUtils.trobarEntitatPerCif(pinbalHelper.getEntitats(), unitat.cif);
        if (entitatPinbal == null) {
            throw new ServeisPinbalNoDisponiblesException(
                    messageHelper.getMessage("pinbal.servei.alta.entitat.no.trobada", new Object[] { unitat.nom, unitat.cif }));
        }
        return PinbalServeiUtils.filtrarServeisNous(
                pinbalHelper.getServeisPerEntitat(entitatPinbal.getCodi()),
                pinbalServeiRepository.findAllCodis());
    }

    /** Missatge per a l'usuari quan no es poden consultar els serveis (unitat no vàlida o sense entitat a PINBAL). */
    private static class ServeisPinbalNoDisponiblesException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        ServeisPinbalNoDisponiblesException(String message) {
            super(message);
        }
    }

    /** Missatge per a l'usuari quan falla la consulta dels serveis d'una unitat organitzativa. */
    private String getMissatgeError(String unitatId, Exception ex) {
        if (ex instanceof ServeisPinbalNoDisponiblesException) {
            return ex.getMessage();
        }
        log.error("No s'han pogut consultar a PINBAL els serveis de la unitat organitzativa " + unitatId, ex);
        return messageHelper.getMessage("pinbal.servei.alta.error.serveis", new Object[] { unitatId, ex.getMessage() });
    }

    private static String getParametre(Map<String, String[]> requestParameterMap, String nom) {
        String[] valors = requestParameterMap != null ? requestParameterMap.get(nom) : null;
        return valors != null && valors.length > 0 && valors[0] != null && !valors[0].isEmpty() ? valors[0] : null;
    }

    private static void buidarDadesServei(PinbalServeiResource target) {
        target.setCodi(null);
        target.setNom(null);
        target.setPinbalServeiDocPermesEnum(new ArrayList<>());
        target.setActiu(false);
    }

    /**
     * Entitats amb CIF i òrgans gestors amb CIF que consulten PINBAL amb el seu propi CIF. El valor és l'id, que és
     * únic entre les dues taules.
     */
    private class UnitatOrganitzativaOptionsProvider implements FieldOptionsProvider {
        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> opcions = new ArrayList<>();
            entitatRepository.findByCifIsNotNullOrderByNomAsc().forEach(e -> opcions.add(new FieldOption(
                    String.valueOf(e.getId()),
                    e.getNom() + " (" + e.getCif() + ")")));
            organGestorRepository.findByUtilitzarCifPinbalTrueAndCifIsNotNullOrderByNomAsc().forEach(o -> opcions.add(new FieldOption(
                    String.valueOf(o.getId()),
                    o.getCodi() + " - " + o.getNom() + " (" + o.getCif() + ")")));
            return opcions;
        }
    }

    /** En canviar la unitat es buida el servei triat i s'avisa si no té cap servei nou a PINBAL o si la consulta falla. */
    private class UnitatOrganitzativaOnChangeLogicProcessor implements OnChangeLogicProcessor<PinbalServeiResource> {
        @Override
        public void onChange(Serializable id, PinbalServeiResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, PinbalServeiResource target) {
            target.setServeiPinbal(null);
            buidarDadesServei(target);
            target.setAvisPinbal(null);
            if (fieldValue == null) {
                return;
            }
            String unitatId = fieldValue.toString();
            try {
                UnitatOrganitzativa unitat = getUnitatOrganitzativa(unitatId);
                if (getServeisNous(unitat).isEmpty()) {
                    target.setAvisPinbal(messageHelper.getMessage("pinbal.servei.alta.sense.serveis", new Object[] { unitat.nom }));
                }
            } catch (Exception ex) {
                target.setAvisPinbal(getMissatgeError(unitatId, ex));
            }
        }
    }

    private class ServeiPinbalOptionsProvider implements FieldOptionsProvider {
        @Override
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            String unitatId = getParametre(requestParameterMap, PinbalServeiResource.Fields.unitatOrganitzativa);
            if (unitatId == null) {
                return new ArrayList<>();
            }
            try {
                String sufixAmbPermis = " " + messageHelper.getMessage("pinbal.servei.alta.opcio.amb.permis");
                return getServeisNous(getUnitatOrganitzativa(unitatId)).stream()
                        .map(s -> new FieldOption(
                                s.getCodi(),
                                s.getCodi() + " - " + s.getDescripcio() + (Boolean.TRUE.equals(s.getPermis()) ? sufixAmbPermis : "")))
                        .collect(Collectors.toList());
            } catch (Exception ex) {
                // L'avís ja el mostra el formulari (UnitatOrganitzativaOnChangeLogicProcessor).
                getMissatgeError(unitatId, ex);
                return new ArrayList<>();
            }
        }
    }

    /** En triar el servei s'emplenen el codi, el nom, els documents permesos i l'actiu amb les dades de PINBAL. */
    private class ServeiPinbalOnChangeLogicProcessor implements OnChangeLogicProcessor<PinbalServeiResource> {
        @Override
        public void onChange(Serializable id, PinbalServeiResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, PinbalServeiResource target) {
            buidarDadesServei(target);
            if (fieldValue == null) {
                // No es toca l'avís: quan el servei es buida perquè ha canviat la unitat, l'avís és el de la unitat.
                return;
            }
            String serveiCodi = fieldValue.toString();
            String unitatId = previous != null ? previous.getUnitatOrganitzativa() : null;
            if (unitatId == null) {
                return;
            }
            try {
                UnitatOrganitzativa unitat = getUnitatOrganitzativa(unitatId);
                ServeiBasic servei = getServeisNous(unitat).stream()
                        .filter(s -> serveiCodi.equals(s.getCodi()))
                        .findFirst()
                        .orElse(null);
                if (servei == null) {
                    target.setAvisPinbal(messageHelper.getMessage("pinbal.servei.alta.servei.no.disponible", new Object[] { serveiCodi, unitat.nom }));
                    return;
                }
                target.setCodi(servei.getCodi());
                target.setNom(PinbalServeiUtils.retallarNom(servei.getDescripcio()));
                target.setPinbalServeiDocPermesEnum(PinbalServeiUtils.toDocsPermesos(servei.getDocumentsTipusPermesos()));
                target.setActiu(Boolean.TRUE.equals(servei.getActiu()));
                target.setAvisPinbal(null);
            } catch (Exception ex) {
                target.setAvisPinbal(getMissatgeError(unitatId, ex));
            }
        }
    }

}
