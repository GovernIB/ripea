
package es.caib.ripea.plugin.caib.cws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b14002
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Cws", targetNamespace = "http://www.indra.es/portafirmasws/cws")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Cws {


    /**
     * 
     * @param uploadRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.UploadResponse
     */
    @WebMethod(operationName = "UploadDocument", action = "UploadDocument")
    @WebResult(name = "upload-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "upload-response")
    public UploadResponse uploadDocument(
        @WebParam(name = "upload-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "upload-request")
        UploadRequest uploadRequest);

    /**
     * 
     * @param downloadRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.DownloadResponse
     */
    @WebMethod(operationName = "DownloadDocument", action = "DownloadDocument")
    @WebResult(name = "download-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "download-response")
    public DownloadResponse downloadDocument(
        @WebParam(name = "download-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "download-request")
        DownloadRequest downloadRequest);

    /**
     * 
     * @param updateRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.UpdateResponse
     */
    @WebMethod(operationName = "UpdateDocument", action = "UpdateDocument")
    @WebResult(name = "update-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "update-response")
    public UpdateResponse updateDocument(
        @WebParam(name = "update-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "update-request")
        UpdateRequest updateRequest);

    /**
     * 
     * @param deleteRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.DeleteResponse
     */
    @WebMethod(operationName = "DeleteDocuments", action = "DeleteDocuments")
    @WebResult(name = "delete-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "delete-response")
    public DeleteResponse deleteDocuments(
        @WebParam(name = "delete-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "delete-request")
        DeleteRequest deleteRequest);

    /**
     * 
     * @param listRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.ListResponse
     */
    @WebMethod(operationName = "ListDocuments", action = "ListDocuments")
    @WebResult(name = "list-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "list-response")
    public ListResponse listDocuments(
        @WebParam(name = "list-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "list-request")
        ListRequest listRequest);

    /**
     * 
     * @param searchRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.SearchResponse
     */
    @WebMethod(operationName = "SearchDocuments", action = "SearchDocuments")
    @WebResult(name = "search-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "search-response")
    public SearchResponse searchDocuments(
        @WebParam(name = "search-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "search-request")
        SearchRequest searchRequest);

    /**
     * 
     * @param listTypeRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.ListTypeResponse
     */
    @WebMethod(operationName = "ListTypeDocuments", action = "ListTypeDocuments")
    @WebResult(name = "listType-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "listType-response")
    public ListTypeResponse listTypeDocuments(
        @WebParam(name = "listType-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "listType-request")
        ListTypeRequest listTypeRequest);

    /**
     * 
     * @param listServerSignersRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.ListServerSignersResponse
     */
    @WebMethod(operationName = "ListServerSigners", action = "ListServerSigners")
    @WebResult(name = "listServerSigners-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "listServerSigners-response")
    public ListServerSignersResponse listServerSigners(
        @WebParam(name = "listServerSigners-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "listServerSigners-request")
        ListServerSignersRequest listServerSignersRequest);

    /**
     * 
     * @param downloadFileRequest
     * @return
     *     returns es.caib.ripea.plugin.caib.cws.DownloadFileResponse
     */
    @WebMethod(operationName = "DownloadFile", action = "DownloadFile")
    @WebResult(name = "download-file-response", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "download-file-response")
    public DownloadFileResponse downloadFile(
        @WebParam(name = "download-file-request", targetNamespace = "http://www.indra.es/portafirmasws/cws", partName = "download-file-request")
        DownloadFileRequest downloadFileRequest);

}