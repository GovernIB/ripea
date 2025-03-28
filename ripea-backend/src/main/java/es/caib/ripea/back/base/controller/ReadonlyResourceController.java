package es.caib.ripea.back.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.model.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.io.Serializable;

/**
 * Mètodes dels controladors de l'API REST per a consultar un recurs de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
public interface ReadonlyResourceController<R extends Resource<? extends Serializable>, ID extends Serializable> {

	/**
	 * Retorna un recurs donat el seu id.
	 * 
	 * @param id
	 *            id de l'element que es vol consultar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @return la informació del recurs.
	 */
	ResponseEntity<EntityModel<R>> getOne(
			final ID id,
			final String[] perspectives);

	/**
	 * Consulta paginada de recursos.
	 * 
	 * @param quickFilter
	 *            text per a filtrar múltiples camps.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            la llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @param pageable
	 *            informació sobre la pagina de resultats que es vol obtenir.
	 * @return la llista de recusos.
	 */
	ResponseEntity<PagedModel<EntityModel<R>>> find(
			final String quickFilter,
			final String filter,
			final String[] namedQueries,
			final String[] perspectives,
			final Pageable pageable);

	/**
	 * Exportació de recursos.
	 *
	 * @param quickFilter
	 *            text per a filtrar múltiples camps.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            la llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @param sort
	 *            ordenació dels resultats.
	 * @param fields
	 *            camps a exportar (tots si no s'especifica).
	 * @param fileType
	 *            tipus de fitxer que s'ha de generar.
	 * @return el fitxer amb l'exportació.
	 * @throws IOException
	 *             si es produeix algun error al escriure l'arxiu a la resposta.
	 */
	ResponseEntity<InputStreamResource> export(
			final String quickFilter,
			final String filter,
			final String[] namedQueries,
			final String[] perspectives,
			final Sort sort,
			final String[] fields,
			final ExportFileType fileType) throws IOException;

	/**
	 * Descàrrega de l'arxiu associat a un camp del recurs.
	 *
	 * @param id
	 *            id de l'element que es vol consultar.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @return l'arxiu associat al camp.
	 * @throws IOException
	 *             si es produeix algun error al escriure l'arxiu a la resposta.
	 */
	ResponseEntity<InputStreamResource> fieldDownload(
			final ID id,
			final String fieldName) throws IOException;

	/**
	 * Retorna la llista d'artefactes relacionats amb aquest servei.
	 *
	 * @return els artefactes relacionats amb aquest servei.
	 */
	ResponseEntity<CollectionModel<EntityModel<ResourceArtifact>>> artifacts();

	/**
	 * Retorna la informació d'un artefacte.
	 *
	 * @param type
	 *            el tipus de l'artefacte.
	 * @param code
	 *            el codi de l'artefacte.
	 * @return la informació de l'artefacte.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'artefacte especificat.
	 */
	ResponseEntity<EntityModel<ResourceArtifact>> artifactGetOne(
			final ResourceArtifactType type,
			final String code) throws ArtifactNotFoundException;

	/**
	 * Valida el formulari associat a un artefacte.
	 *
	 * @param type
	 *            el tipus de l'artefacte.
	 * @param code
	 *            el codi de l'artefacte.
	 * @param params
	 *            el contingut del formular a validar.
	 * @param bindingResult
	 *            instància de BindingResult per a poder validar el formulari.
	 * @return HTTP 200 si tot ha anat be.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'artefacte amb el codi especificat.
	 * @throws JsonProcessingException
	 *             si es produeix algun error al extreure els paràmetres.
	 * @throws MethodArgumentNotValidException
	 *             si es troben errors de validació en els paràmetres.
	 */
	ResponseEntity<?> artifactFormValidate(
			final ResourceArtifactType type,
			final String code,
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException;

	/**
	 * Processa els canvis en els camps del formulari d'un artefacte.
	 *
	 * @param type
	 *            el tipus de l'artefacte.
	 * @param code
	 *            el codi de l'artefacte.
	 * @param onChangeEvent
	 *            informació de l'event onChange.
	 * @return HTTP 200 si tot ha anat be.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'artefacte amb el codi especificat.
	 * @throws JsonProcessingException
	 *             si es produeix algun error al extreure els paràmetres.
	 */
	ResponseEntity<String> artifactFormOnChange(
			final ResourceArtifactType type,
			final String code,
			final OnChangeEvent onChangeEvent) throws ArtifactNotFoundException, JsonProcessingException;

	/**
	 * Generació d'un informe associat a un recurs.
	 *
	 * @param code
	 *            codi de l'informe a generar.
	 * @param params
	 *            paràmetres per a generar l'informe.
	 * @param bindingResult
	 *            instància de BindingResult per a poder validar els paràmetres.
	 * @return les dades de l'informe.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'informe amb el codi especificat.
	 * @throws JsonProcessingException
	 *             si es produeix algun error al extreure els paràmetres.
	 * @throws MethodArgumentNotValidException
	 *             si es troben errors de validació en els paràmetres.
	 */
	ResponseEntity<CollectionModel<EntityModel<?>>> artifactReportGenerate(
			final String code,
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException;

	/**
	 * Generació d'un informe associat a un recurs.
	 *
	 * @param id
	 *            id de l'element sobre el qual es vol generar l'informe.
	 * @param code
	 *            codi de l'informe a generar.
	 * @param params
	 *            paràmetres per a generar l'informe.
	 * @param bindingResult
	 *            instància de BindingResult per a poder validar els paràmetres.
	 * @return les dades de l'informe.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'informe amb el codi especificat.
	 * @throws JsonProcessingException
	 *             si es produeix algun error al extreure els paràmetres.
	 * @throws MethodArgumentNotValidException
	 *             si es troben errors de validació en els paràmetres.
	 */
	ResponseEntity<CollectionModel<EntityModel<?>>> artifactReportGenerate(
			final ID id,
			final String code,
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException;

	/**
	 * Consulta paginada de les opcions disponibles per a emplenar un camp de
	 * tipus ResourceReference que pertany al formulari de l'informe.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param code
	 *            codi de l'informe.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param quickFilter
	 *            text per a filtrar múltiples camps.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @param pageable
	 *            informació sobre la pagina de resultats que es vol obtenir.
	 * @return la pàgina amb els resultats de la consulta.
	 */
	<RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactReportFieldOptionsFind(
			final String code,
			final String fieldName,
			final String quickFilter,
			final String filter,
			final String[] namedQueries,
			final String[] perspectives,
			final Pageable pageable);

	/**
	 * Consulta d'una de les opcions disponibles per a emplenar un camp de
	 * tipus ResourceReference que pertany al formulari de l'informe.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param <RID>
	 *            Tipus de l'id del recurs (ha d'estendre de Serializable).
	 * @param code
	 *            codi de l'informe.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param id
	 *            id de l'element que es vol consultar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @return L'element amb l'id especificat.
	 */
	<RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactReportFieldOptionsGetOne(
			final String code,
			final String fieldName,
			final RID id,
			final String[] perspectives);

	/**
	 * Consulta paginada de les opcions disponibles per a emplenar un camp de
	 * tipus ResourceReference que pertany al formulari del filtre.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param code
	 *            codi del filtre.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param quickFilter
	 *            text per a filtrar múltiples camps.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @param pageable
	 *            informació sobre la pagina de resultats que es vol obtenir.
	 * @return la pàgina amb els resultats de la consulta.
	 */
	<RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactFilterFieldOptionsFind(
			final String code,
			final String fieldName,
			final String quickFilter,
			final String filter,
			final String[] namedQueries,
			final String[] perspectives,
			final Pageable pageable);

	/**
	 * Consulta d'una de les opcions disponibles per a emplenar un camp de
	 * tipus ResourceReference que pertany al formulari del filtre.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param <RID>
	 *            Tipus de l'id del recurs (ha d'estendre de Serializable).
	 * @param code
	 *            codi del filtre.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param id
	 *            id de l'element que es vol consultar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @return L'element amb l'id especificat.
	 */
	<RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactFilterFieldOptionsGetOne(
			final String code,
			final String fieldName,
			final RID id,
			final String[] perspectives);

}