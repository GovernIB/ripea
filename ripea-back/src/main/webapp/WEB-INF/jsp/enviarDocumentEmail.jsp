<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.document.form.enviar.titol"/></c:set>

<html>
<head>
    <title>${titol}</title>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>
    <rip:modalHead/>
</head>
<body>
<form:form action="" method="post" cssClass="form-horizontal" modelAttribute="enviarDocumentCommand">

    <c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
    <c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
    <rip:inputSuggest
            name="responsablesCodi"
            urlConsultaInicial="${urlConsultaInicial}"
            urlConsultaLlistat="${urlConsultaLlistat}"
            textKey="expedient.tasca.reassignar.camp.responsable"
            suggestValue="codi"
            suggestText="codiAndNom"
            required="true"
            multiple="true"/>
    <div class="row" style="margin-bottom: 60px;"></div>

    <div id="modal-botons">
        <button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.enviar"/></button>
        <a href="<c:url value="/metaDada"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
    </div>
</form:form>
</body>
</html>