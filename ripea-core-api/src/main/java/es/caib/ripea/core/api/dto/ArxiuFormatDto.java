package es.caib.ripea.core.api.dto;

public enum ArxiuFormatDto {
	
	ZIP ("application/zip");
	
	private final String text;

	ArxiuFormatDto(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}

	public static ArxiuFormatDto toEnum(String text) {
		if (text == null)
			return null;
		for (ArxiuFormatDto valor : ArxiuFormatDto.values()) {
			if (text.equals(valor.getText())) {
				return valor;
			}
		}
		throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + ArxiuFormatDto.class.getName() + " per al text " + text);
	}
}
