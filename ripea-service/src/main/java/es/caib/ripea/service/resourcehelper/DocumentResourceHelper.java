package es.caib.ripea.service.resourcehelper;

import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.DocumentResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DocumentResourceHelper {

    private final DocumentResourceRepository documentResourceRepository;

    public String getUniqueNameInPare(DocumentResourceEntity entity) {
        List<DocumentResourceEntity> documentResourceEntityList = documentResourceRepository.findAllByPareId(entity.getPare().getId());
        List<String> fitxerNomList = documentResourceEntityList.stream()
                .filter((document)-> Objects.isNull(entity.getId()) || !Objects.equals(entity.getId(), document.getId()))
                .map(DocumentResourceEntity::getFitxerNom)
                .collect(Collectors.toList());

        return getUniqueNameInPare(entity.getFitxerNom(), fitxerNomList);
    }

    public String getUniqueNameInPare(String nomPerComprovar, List<String> noms) {
        int ocurrences = 0;
        noms = noms.stream()
                .map((nom)->nom.substring(0, nom.lastIndexOf('.')))
                .collect(Collectors.toList());
        String newName = nomPerComprovar.substring(0, nomPerComprovar.lastIndexOf('.'));
        while(noms.contains(newName)) {
            ocurrences ++;
            newName = nomPerComprovar + " (" + ocurrences + ")";
        }
        return newName + nomPerComprovar.substring(nomPerComprovar.lastIndexOf('.'));
    }
}
