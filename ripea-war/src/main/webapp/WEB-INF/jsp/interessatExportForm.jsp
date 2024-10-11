<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="formAction"><rip:modalUrl value="/expedient/${expedientId}/interessat/exportar"/></c:set>

<html>
<head>
    <title><spring:message code="contingut.title.exportar.interessats" /></title>
    <link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
    <script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <rip:modalHead />
</head>
<body>

<form:form id="exportIntForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="interessatExportCommand" role="form">

    <form:hidden path="expedientId" />

    <c:if test="${interessatExportCommand.hasInteressats}">
        <table id="interessats-importacio" class="table table-bordered table-striped">
            <thead><tr>
                <td width="90%" colspan="3"><strong><spring:message code="contingut.title.table.exportar.interessats"/></strong></td>
                <td width="10%"><strong><spring:message code="contingut.exportar.interessat"/></strong></td>
            </tr></thead>
            <tbody>
            <c:forEach items="${interessatExportCommand.interessatsFisica}" varStatus="vs" var="intFis">
                <tr>
                    <td width="18%"><spring:message code="registre.interessat.tipus.enum.${intFis.tipus}"/></td>
                    <td width="18%">${intFis.documentNum}</td>
                    <td width="54%">${intFis.nomComplet}<c:if test="${intFis.representant!=null}"><br/><b>Representant:</b> ${intFis.representant.nomComplet}</c:if></td>
                    <td width="10%" style="padding: 8px 24px;">
                        <form:hidden	path="interessatsFisica[${vs.index}].id" value="${intFis.id}"/>
						<form:checkbox	path="interessatsFisica[${vs.index}].exporta"/>
                    </td>
                </tr>
            </c:forEach>
            <c:forEach items="${interessatExportCommand.interessatsJuridi}" varStatus="vs" var="infJur">
                <tr>
                    <td width="18%"><spring:message code="registre.interessat.tipus.enum.${infJur.tipus}"/></td>
					<td width="18%">${infJur.documentNum}</td>
                    <td width="54%">${infJur.nomComplet}<c:if test="${infJur.representant!=null}"><br/><b>Representant:</b> ${infJur.representant.nomComplet}</c:if></td>
                    <td width="10%" style="padding: 8px 24px;">
                        <form:hidden	path="interessatsJuridi[${vs.index}].id" value="${infJur.id}"/>
                        <form:checkbox	path="interessatsJuridi[${vs.index}].exporta"/>
                    </td>
                </tr>
            </c:forEach>
            <c:forEach items="${interessatExportCommand.interessatsAdmini}" varStatus="vs" var="intAdm">
                <tr>
                    <td width="18%"><spring:message code="registre.interessat.tipus.enum.${intAdm.tipus}"/></td>
					<td width="18%">${intAdm.documentNum}</td>
                    <td width="54%">${intAdm.nomComplet}<c:if test="${intAdm.representant!=null}"><br/><b>Representant:</b> ${intAdm.representant.nomComplet}</c:if></td>
                    <td width="10%" style="padding: 8px 24px;">
                        <form:hidden	path="interessatsAdmini[${vs.index}].id" value="${intAdm.id}"/>
						<form:checkbox	path="interessatsAdmini[${vs.index}].exporta"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <div id="modal-botons" class="well">
        <button type="submit" class="btn btn-success">
           <span class="fa fa-upload"></span>
           <spring:message code="contingut.exportar.interessat"/>
        </button>
    </div>
</form:form>

</body>
</html>
