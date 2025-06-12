package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrganGestorCacheHelper {

    @Autowired private CacheHelper cacheHelper;
    @Autowired OrganGestorCacheHelper self;
    @Autowired private OrganGestorRepository organGestorRepository;
    @Autowired private EntityComprovarHelper entityComprovarHelper;

    @Cacheable(value = "codisOrgansFills", key="#codiEntitat.concat('-').concat(#codiDir3Organ)")
    public List<String> getCodisOrgansFills(String codiEntitat, String codiDir3Organ) {
        Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiEntitat);
        List<String> unitatsEntitat = new ArrayList<String>();
        unitatsEntitat.addAll(getCodisOrgansGestorsFills(organigramaEntitat, codiDir3Organ));
        return unitatsEntitat;
    }

    @CacheEvict(value = "codisOrgansFills", allEntries = true)
    public void evictGetCodisOrgansFills() {
    }

    private List<String> getCodisOrgansGestorsFills(Map<String, OrganismeDto> organigrama, String codiDir3) {

        List<String> unitats = new ArrayList<String>();
        unitats.add(codiDir3);
        OrganismeDto organisme = organigrama.get(codiDir3);
        if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
            for (String fill: organisme.getFills()) {
                unitats.addAll(getCodisOrgansGestorsFills(organigrama, fill));
            }
        }
        return unitats;
    }

    public List<Long> getIdsOrgansFills(String codiEntitat, String codiDir3Organ) {
        List<Long> resultat = new ArrayList<Long>();
        List<String> organs = getCodisOrgansFills(codiEntitat, codiDir3Organ);
        if (organs!=null) {
        	EntitatEntity ee = entityComprovarHelper.comprovarEntitat(codiEntitat);
            for (String organ: organs) {
                resultat.add(organGestorRepository.findByEntitatIdAndCodi(ee.getId(), organ).getId());
            }
        }
        return resultat;
    }

    public List<String> getCodisOrgansFills(String codiEntitat, List<String> codiDir3Organs) {

        Set<String> organsSet = new HashSet<>();
        for(String codiDir3Organ: codiDir3Organs) {
            organsSet.addAll(self.getCodisOrgansFills(codiEntitat, codiDir3Organ));
        }
        return new ArrayList<>(organsSet);
    }

    @Cacheable(value = "organCodisAncestors", key="#codiEntitat.concat('-').concat(#codiDir3Organ)")
    public List<String> getCodisOrgansAncestors(String codiEntitat, String codiDir3Organ) {
        Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiEntitat);
        OrganismeDto currentNode = organigramaEntitat.get(codiDir3Organ);
        if (currentNode == null) { // organ obsolet
            return new ArrayList<>();
        }

        List<String> pares = new ArrayList<>();
        while(currentNode != null && !currentNode.getCodi().equals(currentNode.getPare())) {
            pares.add(currentNode.getCodi());
            currentNode = organigramaEntitat.get(currentNode.getPare());
        }
        return pares;
    }

    @CacheEvict(value = "organCodisAncestors", allEntries = true)
    public void evictGetCodisOrgansAncestors() {
    }

    @Cacheable(value = "organismes", key="#entitatcodi")
    public List<OrganismeDto> findOrganismesByEntitat(String entitatcodi) {

        List<OrganismeDto> organismes = new ArrayList<>();
        Map<String, OrganismeDto> organigramaDir3 = cacheHelper.findOrganigramaByEntitat(entitatcodi);
        if (organigramaDir3 == null || organigramaDir3.isEmpty()) {
            return organismes;
        }
        organismes = new ArrayList<>(organigramaDir3.values());
        Collections.sort(organismes, new OrganismeDtoComparator());
        return organismes;
    }

    @CacheEvict(value = "organismes", key="#entitatcodi")
    public void evictFindOrganismesByEntitat(String entitatcodi) {
    }

    @Cacheable(value = "descendents", key="#codiEntitat.concat('-').concat(#codiDir3Organ)")
    public List<OrganismeDto> getOrganismesDescendentsByOrgan(String codiEntitat, String codiDir3Organ) {

        Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiEntitat);
        List<OrganismeDto> organismes= new ArrayList<>();
        organismes.addAll(getOrgansGestorsFills(organigramaEntitat, codiDir3Organ));
        return organismes;
    }

    @CacheEvict(value = "descendents", allEntries = true)
    public void evictDescendents() {
    }

    private List<OrganismeDto> getOrgansGestorsFills(Map<String, OrganismeDto> organigrama, String codiDir3) {

        List<OrganismeDto> organismes = new ArrayList<>();
        OrganismeDto organisme = organigrama.get(codiDir3);
        organismes.add(organisme);
        if (organisme == null || organisme.getFills() == null || organisme.getFills().isEmpty()) {
            return organismes;
        }
        for (String fill: organisme.getFills()) {
            organismes.addAll(getOrgansGestorsFills(organigrama, fill));
        }
        return organismes;
    }

    public List<OrganismeDto> getOrganismesDescendentsByOrgans(String codiEntitat, List<String> codiDir3Organs) {

        Set<OrganismeDto> organismesSet = new HashSet<>();
        for(String codiDir3Organ: codiDir3Organs) {
            organismesSet.addAll(self.getOrganismesDescendentsByOrgan(codiEntitat, codiDir3Organ));
        }
        List<OrganismeDto> organismes = new ArrayList<>(organismesSet);
        Collections.sort(organismes, new OrganismeDtoComparator());
        return organismes;
    }

    private static class OrganismeDtoComparator implements Comparator<OrganismeDto> {
        @Override
        public int compare(OrganismeDto o1, OrganismeDto o2) {
            return o1.getCodi().compareTo(o2.getCodi());
        }
    }
}
