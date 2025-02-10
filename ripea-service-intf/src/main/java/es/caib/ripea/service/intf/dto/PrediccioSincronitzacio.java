package es.caib.ripea.service.intf.dto;

import lombok.*;
import org.apache.commons.collections.MultiMap;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrediccioSincronitzacio {

    MultiMap splitMap;
    MultiMap mergeMap;
    MultiMap substMap;
    List<UnitatOrganitzativaDto> unitatsVigents;
    List<UnitatOrganitzativaDto> unitatsNew;
    List<UnitatOrganitzativaDto> unitatsExtingides;
    boolean isFirstSincronization;
    boolean noCanvis;
}
