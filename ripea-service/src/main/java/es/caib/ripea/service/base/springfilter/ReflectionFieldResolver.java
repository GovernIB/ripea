package es.caib.ripea.service.base.springfilter;

import java.lang.reflect.Field;
import org.springframework.util.ReflectionUtils;

public class ReflectionFieldResolver {

    /**
     * Obtiene el Field correspondiente a un nombre de campo anidado, como "metaExpedient.metaExpedientOrganGestors.id".
     *
     * @param rootClass Clase raíz desde la cual comenzar la búsqueda.
     * @param nestedFieldName Nombre del campo anidado, separado por puntos.
     * @return Field correspondiente al último campo en la cadena.
     * @throws NoSuchFieldException si algún campo intermedio no existe.
     */
    public static Field findNestedField(Class<?> rootClass, String nestedFieldName) {
        String[] fieldParts = nestedFieldName.split("\\.");
        Class<?> currentClass = rootClass;
        Field currentField = null;

        for (String part : fieldParts) {
            currentField = ReflectionUtils.findField(currentClass, part);
            if (currentField == null) {
                return null;
            }
            currentField.setAccessible(true);
            currentClass = currentField.getType();
        }

        return currentField;
    }
}
