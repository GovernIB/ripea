package es.caib.ripea.core.helper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class OrganGestorCacheHelper {

//    @Resource
//    private CacheHelper cacheHelper;
//
//    @Cacheable(value = "codisOrgansFills", key="#codiEntitat.concat('-').concat(#codiDir3Organ)")
//    public List<String> getCodisOrgansFills(String codiEntitat, String codiDir3Organ) {
//        Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiEntitat);
//
//        List<String> unitatsEntitat = new ArrayList<String>();
//        unitatsEntitat.addAll(getCodisOrgansGestorsFills(organigramaEntitat, codiDir3Organ));
//
//        return unitatsEntitat;
//    }
//
//    @CacheEvict(value = "codisOrgansFills", allEntries = true)
//    public void evictGetCodisOrgansFills() {
//    }
//
//    private List<String> getCodisOrgansGestorsFills(Map<String, OrganismeDto> organigrama, String codiDir3) {
//
//        List<String> unitats = new ArrayList<String>();
//        unitats.add(codiDir3);
//        OrganismeDto organisme = organigrama.get(codiDir3);
//        if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
//            for (String fill: organisme.getFills()) {
//                unitats.addAll(getCodisOrgansGestorsFills(organigrama, fill));
//            }
//        }
//        return unitats;
//    }
//
//    @Cacheable(value = "organCodisAncestors", key="#codiEntitat.concat('-').concat(#codiDir3Organ)")
//    public List<String> getCodisOrgansAncestors(String codiEntitat, String codiDir3Organ) {
//        Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(codiEntitat);
//        OrganismeDto currentNode = organigramaEntitat.get(codiDir3Organ);
//        if (currentNode == null) { // organ obsolet
//            return new ArrayList<>();
//        }
//
//        List<String> pares = new ArrayList<>();
//        while(currentNode != null && !currentNode.getCodi().equals(currentNode.getPare())) {
//            pares.add(currentNode.getCodi());
//            currentNode = organigramaEntitat.get(currentNode.getPare());
//        }
//        return pares;
//    }
//
//    @CacheEvict(value = "organCodisAncestors", allEntries = true)
//    public void evictGetCodisOrgansAncestors() {
//    }
}
