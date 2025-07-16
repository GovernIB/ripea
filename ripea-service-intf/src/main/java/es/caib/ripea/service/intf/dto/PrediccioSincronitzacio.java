package es.caib.ripea.service.intf.dto;

import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;

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
	MultiValuedMap splitMap;
	MultiValuedMap mergeMap;
	MultiValuedMap substMap;
    List<UnitatOrganitzativaDto> unitatsVigents;
    List<UnitatOrganitzativaDto> unitatsNew;
    List<UnitatOrganitzativaDto> unitatsExtingides;
    boolean isFirstSincronization;
    boolean noCanvis;
}