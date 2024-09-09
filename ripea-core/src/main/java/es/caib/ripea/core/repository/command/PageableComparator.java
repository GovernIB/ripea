package es.caib.ripea.core.repository.command;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Comparator;

public class PageableComparator<T> implements Comparator<T> {

    private final Pageable pageable;

    public PageableComparator(Pageable pageable) {
        this.pageable = pageable;
    }

    public PageableComparator(Sort.Order order) {
        this.pageable = new PageRequest(0, Integer.MAX_VALUE, new Sort(order));
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compare(T o1, T o2) {
        Sort sort = pageable.getSort();
        if (sort == null || !sort.iterator().hasNext()) {
            return 0;
        }

        for (Sort.Order order : sort) {
            Comparable key1 = getNestedFieldValue(o1, order.getProperty());
            Comparable key2 = getNestedFieldValue(o2, order.getProperty());

            int comparison = 0;

            if (key1 == null) {
                if (key2 != null) { comparison = -1; }
            } else if (key2 == null) {
                comparison = 1;
            } else if (key1 instanceof String) {
                comparison = ((String) key1).compareToIgnoreCase((String) key2);
            } else {
                comparison = key1.compareTo(key2);
            }

            if (comparison != 0) {
                return order.isAscending() ? comparison : -comparison;
            }
        }

        return 0;
    }

    // Helper method to extract value for nested properties
    private Comparable getNestedFieldValue(Object item, String fieldName) {
        String[] fieldNames = fieldName.split("\\.");
        Object value = item;

        for (String name : fieldNames) {
            if (value == null) {
                return null;
            }
            value = getFieldValue(value, name);
        }
        return (Comparable) value;
    }

    // Helper method to extract field value
    private Object getFieldValue(Object item, String fieldName) {
        try {
            Field field = getFieldFromClassHierarchy(item, fieldName);
            field.setAccessible(true);
            return field.get(item);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid sort property: " + fieldName, e);
        }
    }

    public Field getFieldFromClassHierarchy(Object item, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = item.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                // Continuar buscant a la superclasse
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy of " + item.getClass().getName());
    }

}
