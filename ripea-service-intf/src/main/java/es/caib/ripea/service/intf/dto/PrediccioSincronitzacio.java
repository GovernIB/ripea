package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
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
public class PrediccioSincronitzacio implements Serializable {
	private static final long serialVersionUID = -4009291731120001972L;
	MultiValuedMap splitMap;
	MultiValuedMap mergeMap;
	MultiValuedMap substMap;
    List<UnitatOrganitzativaDto> unitatsVigents;
    List<UnitatOrganitzativaDto> unitatsNew;
    List<UnitatOrganitzativaDto> unitatsExtingides;
    boolean isFirstSincronization;
    boolean noCanvis;
}