package es.caib.ripea.service.intf.base.service;

import es.caib.ripea.service.intf.base.exception.*;
import es.caib.ripea.service.intf.base.model.DownloadableFile;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Mètodes a implementar pels serveis que gestionen un recurs en mode només lectura.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 *
 * @author Límit Tecnologies
 */
public interface ReadonlyResourceService<R extends Resource<? extends Serializable>, ID extends Serializable> {

	/**
	 * Consulta un recurs donada la seva identificació.
	 *
	 * @param id
	 *            clau primària del recurs.
	 * @param perspectives
	 *            llista de perspectives a aplicar.
	 * @return el recurs amb la identificació especificada.
	 * @throws ResourceNotFoundException
	 *             si no s'ha trobat el recurs especificat.
	 */
	R getOne(
			ID id,
			String[] perspectives) throws ResourceNotFoundException;

	/**
	 * Consulta paginada de recursos.
	 *
	 * @param quickFilter
	 *            filtre ràpid en format text.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            llista de perspectives a aplicar.
	 * @param pageable
	 *            paràmetres de paginació i ordenació.
	 * @return la llista de recursos.
	 */
	Page<R> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable);

	/**
	 * Descàrrega del fitxer associat a un camp del recurs.
	 *
	 * @param id
	 *            clau primària del recurs.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param out
	 *            stream a on posar el fitxer generat.
	 * @return el fitxer associat al camp.
	 * @throws ResourceNotFoundException
	 *             si no s'ha trobat el recurs especificat.
	 * @throws ResourceFieldNotFoundException
	 *            si no es troba el camp del recurs.
	 * @throws FieldArtifactNotFoundException
	 *            si el camp no té cap artefacte de descàrrega associat.
	 * @throws IOException
	 *             si es produeix algun error de E/S al descarregar l'arxiu.
	 */
	DownloadableFile fieldDownload(
			ID id,
			String fieldName,
			OutputStream out) throws ResourceNotFoundException, ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException;

	/**
	 * Retorna la llista d'artefactes del tipus especificat als quals l'usuari te accés.
	 *
	 * @param type
	 *            el tipus d'artefacte que es vol consultar (si és null es retornen tots els tipus).
	 * @return la llista dels artefactes permesos.
	 */
	List<ResourceArtifact> artifactFindAll(ResourceArtifactType type);

	/**
	 * Retorna l'artefacte amb el tipus i codi especificat.
	 *
	 * @param type
	 *            el tipus de l'artefacte.
	 * @param code
	 *            el codi de l'artefacte.
	 * @return l'artefacte.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'artefacte amb el tipus i el codi especificat.
	 */
	ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException;

	/**
	 * Processament en el backend dels canvis en els camps del formulari associats
	 * a un artefacte.
	 *
	 * @param type
	 *            el tipus de l'artefacte.
	 * @param code
	 *            el codi de l'artefacte.
	 * @param previous
	 *            informació del recurs abans del canvi.
	 * @param fieldName
	 *            nom del camp que s'ha canviat.
	 * @param fieldValue
	 *            el valor del camp que s'ha canviat.
	 * @param answers
	 *            respostes a les preguntes formulades en el front.
	 * @return un map amb els canvis resultants de processar la petició.
	 * @param <P> la classe del formulari associat a l'artefacte.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'artefacte amb el tipus i el codi especificat.
	 * @throws ResourceFieldNotFoundException
	 *            si no es troba el camp especificat.
	 * @throws AnswerRequiredException
	 *            si es requereix alguna resposta addicional de l'usuari.
	 */
	<P extends Serializable> Map<String, Object> artifactOnChange(
			ResourceArtifactType type,
			String code,
			P previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException;

	/**
	 * Genera l'informe amb el codi especificat.
	 *
	 * @param code
	 *            el codi de l'informe.
	 * @param params
	 *            els paràmetres necessaris per a generar l'informe.
	 * @return les dades de l'informe
	 * @param <P> el tipus dels paràmetres.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'informe amb el codi especificat.
	 * @throws ReportGenerationException
	 *             si es produeix algun error generant l'informe.
	 */
	<P extends Serializable> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException;

}
