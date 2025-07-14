<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="potModificar">${potModificar == null || potModificar == true ? true : false}</c:set>
<c:set var="formAction"><rip:modalUrl value="/expedient/${expedientId}/interessat/importar"/></c:set>
<c:set var="maxFileSize"><%=es.caib.ripea.back.config.WebMvcConfig.MAX_UPLOAD_SIZE%></c:set>

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
    
	<script type="text/javascript">
	
		var maxTamanyFitxerUpload = ${maxFileSize};
	
		$(document).ready(function() {
			  $('#fitxerInteressats').change(function(){
				    let tamany = $(this)[0].files[0].size;
				    var pare = $(this).closest('.fileinput').parent();
					if (tamany>maxTamanyFitxerUpload) {
						$(pare).find('div.alert.alert-danger').remove();
						$(pare).append('<div class="alert alert-danger" style="padding-top: 5px; padding-bottom: 5px; padding-left: 10px; margin-bottom: 0px;" role="alert"><span><spring:message code="MaxFileUploadSize"/></span></div>');
					} else {
						$(pare).find('div.alert.alert-danger').remove();
					}
			  });
		});
	</script>    
    
</head>
<body>

<form:form id="importIntForm" action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="interessatImportCommand" role="form" enctype="multipart/form-data">
    
    <form:hidden path="expedientId" />
    <form:hidden path="accio" />
    
    <c:if test="${interessatImportCommand.accio=='INTERESSATS'}">
    	<rip:inputSelect name="tipus" optionEnum="InteressatImportacioTipusDto" textKey="contingut.camp.importar.tipus"/>
   		<rip:inputFile 
   			name="fitxerInteressats"
   			comment="contingut.document.MAX_UPLOAD_SIZE"
   			textKey="contingut.camp.importar.interessats"
   			doNotShowErrors="1"/>
   	</c:if>

    <c:if test="${interessatImportCommand.hasInteressats}">
        <table id="interessats-importacio" class="table table-bordered table-striped">
            <thead><tr>
                <td width="90%" colspan="3"><strong><spring:message code="contingut.title.table.interessat.interessats"/></strong></td>
                <td width="10%"><strong><spring:message code="contingut.importar.interessat"/></strong></td>
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
                    <td width="54%">${intFis.nomComplet}<c:if test="${intFis.representant!=null}"><br/><b>Representant:</b> ${intFis.representant.nomComplet}</c:if></td>
                    <td width="10%" style="padding: 8px 24px;">
                        <form:hidden	path="interessatsFisica[${vs.index}].id" value="${intFis.id}"/>
                        <form:checkbox	path="interessatsFisica[${vs.index}].exporta"/>
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
                    <td width="54%">${infJur.nomComplet}<c:if test="${infJur.representant!=null}"><br/><b>Representant:</b> ${infJur.representant.nomComplet}</c:if></td>
                    <td width="10%" style="padding: 8px 24px;">
                        <form:hidden	path="interessatsJuridi[${vs.index}].id" value="${infJur.id}"/>
                        <form:checkbox	path="interessatsJuridi[${vs.index}].exporta"/>
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
