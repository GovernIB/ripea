<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="potModificar">${potModificar == null || potModificar == true ? true : false}</c:set>
<c:set var="formAction"><rip:modalUrl value="/expedient/${expedientId}/interessat/importar"/></c:set>
<html>
<head>
    <title><spring:message code="contingut.title.importar.interessats" /></title>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
    <script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <rip:modalHead />
</head>
<body>

<form:form id="importIntForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="interessatImportCommand" role="form" enctype="multipart/form-data">
    <form:hidden path="expedientId" />
    <form:hidden path="accio" />
    <rip:inputFile name="fitxerInteressats" textKey="contingut.camp.importar.interessats" doNotShowErrors="1"/>

    <c:if test="${interessatImportCommand.hasInteressats}">
        <table id="interessats-importacio" class="table table-bordered table-striped">
            <thead><tr>
                <td width="80%" colspan="3"><strong><spring:message code="contingut.interessat.info.interessat"/></strong></td>
                <td width="20%"></td>
            </tr></thead>
            <tbody>
            <c:forEach items="${interessatImportCommand.interessatsFisica}" varStatus="vs" var="intFis">
                <tr>
                    <td width="18%"><spring:message code="registre.interessat.tipus.enum.${intFis.tipus}"/></td>
                    <td width="18%">
                            ${intFis.documentNum}
                            <c:if test="${intFis.jaExistentExpedient}">
                                <span class="fa fa-exclamation-triangle alert-warning" title="<spring:message code="contingut.title.interessaT.jaExistent"/>"></span>
                            </c:if>
                    </td>
                    <td width="44%">${intFis.nomComplet}</td>
                    <td width="20%" style="padding: 8px 24px;">
                        <form:hidden path="interessatsFisica[${vs.index}].id" value="${intFis.id}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].tipus" value="${intFis.tipus}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].documentTipus" value="${intFis.documentTipus}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].documentNum" value="${intFis.documentNum}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].pais" value="${intFis.pais}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].paisNom" value="${intFis.paisNom}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].provincia" value="${intFis.provincia}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].provinciaNom" value="${intFis.provinciaNom}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].municipi" value="${intFis.municipi}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].municipiNom" value="${intFis.municipiNom}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].adresa" value="${intFis.adresa}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].codiPostal" value="${intFis.codiPostal}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].email" value="${intFis.email}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].telefon" value="${intFis.telefon}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].observacions" value="${intFis.observacions}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].preferenciaIdioma" value="${intFis.preferenciaIdioma}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].representantId" value="${intFis.representantId}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].representantIdentificador" value="${intFis.representantIdentificador}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].identificador" value="${intFis.identificador}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].esRepresentant" value="${intFis.esRepresentant}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].arxiuPropagat" value="${intFis.arxiuPropagat}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].representantArxiuPropagat" value="${intFis.representantArxiuPropagat}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].expedientArxiuPropagat" value="${intFis.expedientArxiuPropagat}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].entregaDeh" value="${intFis.entregaDeh}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].entregaDehObligat" value="${intFis.entregaDehObligat}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].incapacitat" value="${intFis.incapacitat}"/>
                        
                        <form:hidden path="interessatsFisica[${vs.index}].nom" value="${intFis.nom}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].llinatge1" value="${intFis.llinatge1}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].llinatge2" value="${intFis.llinatge2}"/>
                        <form:hidden path="interessatsFisica[${vs.index}].jaExistentExpedient" value="${intFis.jaExistentExpedient}"/>
                        
                        <rip:inputSelect name="interessatsFisica[${vs.index}].accio" inline="true" optionEnum="InteressatAssociacioAccioEnum"/>
                    </td>
                </tr>
            </c:forEach>
            <c:forEach items="${interessatImportCommand.interessatsJuridi}" varStatus="vs" var="infJur">
                <tr>
                    <td width="18%"><spring:message code="registre.interessat.tipus.enum.${infJur.tipus}"/></td>
                    <td width="18%">
                            ${infJur.documentNum}
                        <c:if test="${infJur.jaExistentExpedient}">
                            <span class="fa fa-exclamation-triangle alert-warning" title="<spring:message code="contingut.title.interessaT.jaExistent"/>"></span>
                        </c:if>
                    </td>
                    <td width="44%">${infJur.nomComplet}</td>
                    <td width="20%" style="padding: 8px 24px;">
                        <form:hidden path="interessatsJuridi[${vs.index}].id" value="${infJur.id}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].tipus" value="${infJur.tipus}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].documentTipus" value="${infJur.documentTipus}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].documentNum" value="${infJur.documentNum}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].pais" value="${infJur.pais}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].paisNom" value="${infJur.paisNom}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].provincia" value="${infJur.provincia}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].provinciaNom" value="${infJur.provinciaNom}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].municipi" value="${infJur.municipi}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].municipiNom" value="${infJur.municipiNom}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].adresa" value="${infJur.adresa}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].codiPostal" value="${infJur.codiPostal}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].email" value="${infJur.email}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].telefon" value="${infJur.telefon}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].observacions" value="${infJur.observacions}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].preferenciaIdioma" value="${infJur.preferenciaIdioma}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].representantId" value="${infJur.representantId}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].representantIdentificador" value="${infJur.representantIdentificador}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].identificador" value="${infJur.identificador}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].esRepresentant" value="${infJur.esRepresentant}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].arxiuPropagat" value="${infJur.arxiuPropagat}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].representantArxiuPropagat" value="${infJur.representantArxiuPropagat}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].expedientArxiuPropagat" value="${infJur.expedientArxiuPropagat}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].entregaDeh" value="${infJur.entregaDeh}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].entregaDehObligat" value="${infJur.entregaDehObligat}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].incapacitat" value="${infJur.incapacitat}"/>

                        <form:hidden path="interessatsJuridi[${vs.index}].raoSocial" value="${infJur.raoSocial}"/>
                        <form:hidden path="interessatsJuridi[${vs.index}].jaExistentExpedient" value="${infJur.jaExistentExpedient}"/>

                        <rip:inputSelect name="interessatsJuridi[${vs.index}].accio" inline="true" optionEnum="InteressatAssociacioAccioEnum"/>
                    </td>
                </tr>
            </c:forEach>
            <c:forEach items="${interessatImportCommand.interessatsAdmini}" varStatus="vs" var="intAdm">
                <tr>
                    <td width="18%"><spring:message code="registre.interessat.tipus.enum.${intAdm.tipus}"/></td>
                    <td width="18%">
                            ${intAdm.documentNum}
                        <c:if test="${intAdm.jaExistentExpedient}">
                            <span class="fa fa-exclamation-triangle alert-warning" title="<spring:message code="contingut.title.interessaT.jaExistent"/>"></span>
                        </c:if>
                    </td>
                    <td width="44%">${intAdm.nomComplet}</td>
                    <td width="20%" style="padding: 8px 24px;">
                        <form:hidden path="interessatsAdmini[${vs.index}].id" value="${intAdm.id}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].tipus" value="${intAdm.tipus}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].documentTipus" value="${intAdm.documentTipus}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].documentNum" value="${intAdm.documentNum}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].pais" value="${intAdm.pais}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].paisNom" value="${intAdm.paisNom}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].provincia" value="${intAdm.provincia}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].provinciaNom" value="${intAdm.provinciaNom}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].municipi" value="${intAdm.municipi}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].municipiNom" value="${intAdm.municipiNom}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].adresa" value="${intAdm.adresa}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].codiPostal" value="${intAdm.codiPostal}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].email" value="${intAdm.email}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].telefon" value="${intAdm.telefon}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].observacions" value="${intAdm.observacions}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].preferenciaIdioma" value="${intAdm.preferenciaIdioma}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].representantId" value="${intAdm.representantId}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].representantIdentificador" value="${intAdm.representantIdentificador}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].identificador" value="${intAdm.identificador}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].esRepresentant" value="${intAdm.esRepresentant}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].arxiuPropagat" value="${intAdm.arxiuPropagat}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].representantArxiuPropagat" value="${intAdm.representantArxiuPropagat}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].expedientArxiuPropagat" value="${intAdm.expedientArxiuPropagat}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].entregaDeh" value="${intAdm.entregaDeh}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].entregaDehObligat" value="${intAdm.entregaDehObligat}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].incapacitat" value="${intAdm.incapacitat}"/>

                        <form:hidden path="interessatsAdmini[${vs.index}].organCodi" value="${intAdm.organCodi}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].organNom" value="${intAdm.organNom}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].ambOficinaSir" value="${intAdm.ambOficinaSir}"/>
                        <form:hidden path="interessatsAdmini[${vs.index}].jaExistentExpedient" value="${intAdm.jaExistentExpedient}"/>

                        <rip:inputSelect name="interessatsAdmini[${vs.index}].accio" inline="true" optionEnum="InteressatAssociacioAccioEnum"/>
                    </td>
                </tr>
            </c:forEach>            
            </tbody>
        </table>
    </c:if>
    <div id="modal-botons" class="well">
        <button type="submit" class="btn btn-success">
            <c:if test="${interessatImportCommand.accio=='SAVE'}">
                <span class="fa fa-download"></span>
                <spring:message code="contingut.boto.importar.interessats"/>
            </c:if>
            <c:if test="${interessatImportCommand.accio=='INTERESSATS'}">
                <span class="fa fa-cog"></span>
                <spring:message code="contingut.boto.processar.interessats"/>
            </c:if>
        </button>
    </div>
</form:form>

</body>
</html>
