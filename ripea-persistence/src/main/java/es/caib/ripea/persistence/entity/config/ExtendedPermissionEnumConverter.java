package es.caib.ripea.persistence.entity.config;

import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExtendedPermissionEnumConverter implements AttributeConverter<ExtendedPermissionEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ExtendedPermissionEnum attribute) {
        return (attribute != null) ? attribute.getCodi() : null;
    }

    @Override
    public ExtendedPermissionEnum convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        for (ExtendedPermissionEnum e : ExtendedPermissionEnum.values()) {
            if (e.getCodi().equals(dbData)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Código inválido para ExtendedPermissionEnum: " + dbData);
    }
}
