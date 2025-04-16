package es.caib.ripea.service.intf.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrediccioSincronitzacio {
    Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> splitMap;
    Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> mergeMap;
    Map<UnitatOrganitzativaDto, UnitatOrganitzativaDto> substMap;
    List<UnitatOrganitzativaDto> unitatsVigents;
    List<UnitatOrganitzativaDto> unitatsNew;
    List<UnitatOrganitzativaDto> unitatsExtingides;
    boolean isFirstSincronization;
    boolean noCanvis;
}