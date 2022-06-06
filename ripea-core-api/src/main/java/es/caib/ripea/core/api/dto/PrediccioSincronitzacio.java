package es.caib.ripea.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrediccioSincronitzacio {

    MultiMap splitMap = new MultiValueMap();
    MultiMap mergeMap = new MultiValueMap();
    MultiMap substMap = new MultiValueMap();
    List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();
    List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<>();
    List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();
    boolean isFirstSincronization;

}
