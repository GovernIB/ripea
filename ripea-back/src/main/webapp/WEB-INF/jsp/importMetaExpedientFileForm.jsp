<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="maxFileSize"><%=es.caib.ripea.back.config.WebMvcConfig.MAX_UPLOAD_SIZE%></c:set>

<html>
<head>
	<title><spring:message code="metaexpedient.import.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	
	<script type="text/javascript">
	
		var maxTamanyFitxerUpload = ${maxFileSize};
	
		$(document).ready(function() {
			  $('#file').change(function(){
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
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/importFitxer"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="fileCommand" role="form" enctype="multipart/form-data">
		<rip:inputFile 
			name="file"
			textKey="metaexpedient.import.form.file"
			comment="contingut.document.MAX_UPLOAD_SIZE"
			required="true"/>
		<div style="min-height: 20px;"></div>
		<div id="modal-botons">
			<button type="submit" data-toggle="modal" class="btn btn-success"><span class="fa fa-arrow-right"></span> <spring:message code="comu.boto.next"/>...</button>
			<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>