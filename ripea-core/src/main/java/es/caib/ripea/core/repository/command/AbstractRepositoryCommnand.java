package es.caib.ripea.core.repository.command;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AbstractRepositoryCommnand {

    private static final int MAX_IN_CLAUSE_SIZE = 1000;

    protected  <T>List<T> getList(RepositoryCommand<T> command, List<?> list) {
        return getList(command, list,null, false);
    }
    protected  <T>List<T> getList(RepositoryCommand<T> command, List<?> list, Sort.Order order) {
        return getList(command, list,null, false);
    }
    protected  <T>List<T> getList(RepositoryCommand<T> command, List<?> list, boolean executarAmbNull) {
        return getList(command, list,null, executarAmbNull);
    }
    protected  <T> List<T> getList(RepositoryCommand<T> command, List<?> list, Sort.Order order, boolean executarAmbNull) {

        List<T> llistaResultats = new ArrayList<>();

        // Si no ens passen una llista, no es produirà l'error per massa elements a la clàusula IN.
        // Si es permet la llista nul·la, es realitza una única execució
        if (list == null || list.isEmpty()) {
            if (executarAmbNull)
                return command.executeList(null);
            return llistaResultats;
        }

        // Si la llista té menys del màxim d'elements permesos no es produirà l'error per massa elements a la clàusula IN.
        // Executam una única execució passant la llista sencera
        int numElements = list.size();
        if (numElements <= MAX_IN_CLAUSE_SIZE)
            return command.executeList(list);

        // Recuperar les dades per lots
        int inici = 0;
        int fi = Math.min(MAX_IN_CLAUSE_SIZE, numElements);

        while (inici < numElements) {
            List<?> subList = list.subList(inici, fi);
            if (!subList.isEmpty())
                llistaResultats.addAll(command.executeList(subList));
            inici = fi;
            fi = Math.min(fi + MAX_IN_CLAUSE_SIZE, numElements);
        }

        if (order != null) {
            Collections.sort(llistaResultats, new PageableComparator<T>(order));
        }
        return llistaResultats;
    }

    protected  <T> Page<T> getPage(RepositoryCommand<T> command, List<?> list) {
        return getPage(command, list, false);
    }
    protected  <T> Page<T> getPage(RepositoryCommand<T> command, List<?> list, boolean executarAmbNull) {

        List<T> llistaResultats = new ArrayList<>();
        Pageable pageable = command.getPageable();

        // Si no ens passen una llista, no es produirà l'error per massa elements a la clàusula IN.
        // Si es permet la llista nul·la, es realitza una única execució
        if (list == null || list.isEmpty()) {
            if (executarAmbNull)
                return command.executePage(null);
            return new PageImpl<>(llistaResultats, pageable, 0);
        }

        // Si la llista té menys del màxim d'elements permesos a la clàusula IN, executam una única execució
        int numElements = list.size();
        if (numElements <= MAX_IN_CLAUSE_SIZE)
            return command.executePage(list);

        // Recuperar les dades per lots
        int inici = 0;
        int fi = Math.min(MAX_IN_CLAUSE_SIZE, numElements);

        while (inici < numElements) {
            List<?> subList = list.subList(inici, fi);
            if (!subList.isEmpty())
                llistaResultats.addAll(command.executeList(subList));
            inici = fi;
            fi = Math.min(fi + MAX_IN_CLAUSE_SIZE, numElements);
        }

        // Mantenir l'ordre original dels valors
        Collections.sort(llistaResultats, new PageableComparator<T>(pageable));

        // Crear la pàgina basada en el Pageable proporcionat
        inici = pageable.getOffset();
        fi = Math.min((inici + pageable.getPageSize()), llistaResultats.size());
        List<T> paginatedResults = llistaResultats.subList(inici, fi);
        return new PageImpl<>(paginatedResults, pageable, llistaResultats.size());
    }

    protected static boolean isListOfType(List<?> list, Class<?> clazz) {
        if (list == null || list.isEmpty()) {
            return true;
        }

        if (!clazz.isInstance(list.get(0)))
            return false;
        return true;
    }

}
