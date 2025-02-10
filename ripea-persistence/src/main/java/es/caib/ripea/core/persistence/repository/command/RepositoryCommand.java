package es.caib.ripea.core.persistence.repository.command;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface RepositoryCommand<T> {

    public List<T> executeList(List<?> sublist);
    public Page<T> executePage(List<?> sublist);

    public Pageable getPageable();
}
