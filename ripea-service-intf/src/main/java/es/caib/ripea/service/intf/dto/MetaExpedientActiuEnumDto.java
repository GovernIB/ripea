package es.caib.ripea.service.intf.dto;

import lombok.Getter;

public enum MetaExpedientActiuEnumDto {

	ACTIU(true), INACTIU(false);

	@Getter
	public final Boolean value;

	private MetaExpedientActiuEnumDto(Boolean value) {
		this.value = value;

	}

}
