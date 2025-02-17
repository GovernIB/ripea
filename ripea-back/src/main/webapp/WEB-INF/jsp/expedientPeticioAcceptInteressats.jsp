<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol">
	<spring:message code="expedient.peticio.form.acceptar.titol" /> - <spring:message code="expedient.peticio.form.acceptar.associar.interessats.titol" />
</c:set>
<html>
<head>
<title>${titol}</title>
<rip:modalHead />

<script src="<c:url value="/webjars/jquery/1.12.4/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
<style type="text/css">
	.fa-circle-o-notch {position: absolute; right: 10px; top: 10px;}
	.rmodal_loading {background: rgba( 255, 255, 255, .8 ) url('<c:url value="/img/loading.gif"/>') 50% 50% no-repeat;}
	#titolINom {cursor: pointer !important;}
	.disabled-icon .form-control:hover {cursor: not-allowed !important;}
	.titolINom_btn {cursor: pointer !important;}
	.disabled-icon .titolINom_btn:hover {cursor: not-allowed !important;}
	.customIcon {font-size: 10px; background-color: transparent !important;}
	.titolINom_btn {background-color: #ccc !important;}
	.titolINom_btn:hover {background-color: #b6b6b6 !important;	border-color: #999;}
	.dl-horizontal dt {width: 200px !important;}
	table.font-decresed {font-size: small; margin-bottom: 0px;}
	table.font-decresed td {padding: 4px 8px !important;}
	td .form-group {margin: 0px;}
	.b40 {padding-bottom: 40px;}
	tr.int-ov {color: brown;}
</style>
<script>
	const interessatsRipea = JSON.parse('${jsonInteressatsRipea}');
	const interessatsDistr = JSON.parse('${jsonInteressatsDistribucio}');
	const interessatsOverw = JSON.parse('${jsonDocumentInteressatsOverwritten}');

	$(document).ready(function () {

		$("button[name=previousPage]").click(function () {
			window.location.href = '<c:url value="/modal/expedientPeticio/previousPage"/>';
		});

		$("button#btnSave").submit(function (e) {
			e.preventDefault();
			$("button#btnSave").attr("disabled", true);
			return true;
		});

	});

</script>

</head>
<body>
	<c:if test="${!isCrearNewExpedient}">
		<div class="well b40">
			<h4>Interessats associats a l'expedient:</h4>
			<table id="interessats-ripea" class="table table-bordered table-striped font-decresed">
				<thead>
				<tr>
					<td colspan="3"><strong><spring:message code="contingut.interessat.info.interessat"/></strong></td>
					<td colspan="3"><strong><spring:message code="contingut.interessat.info.representant"/></strong></td>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${interessatsRipea}" var="interessatRipea" varStatus="index">
					<c:if test="${!interessatRipea.esRepresentant}">
						<c:set var="interessatsOv" value="${documentInteressatsOverwritten}" />
						<tr <c:if test="${fn:contains(interessatsOv, interessatRipea.documentNum) or (interessatRipea.representant != null and fn:contains(interessatsOv, interessatRipea.representant.documentNum))}">class="int-ov"</c:if>>
							<td data-docnum="${interessatRipea.documentNum}" style="width: 140px;"><spring:message code="registre.interessat.tipus.enum.${interessatRipea.tipus}"/></td>
							<td style="width: 120px;">${interessatRipea.documentNum}</td>
							<td style="width: calc(50% - 260px);">${interessatRipea.nomComplet}</td>
							<c:choose>
								<c:when test="${!empty interessatRipea.representant}">
									<td style="width: 140px;"><spring:message code="registre.interessat.tipus.enum.${interessatRipea.representant.tipus}"/></td>
									<td style="width: 120px;">${interessatRipea.representant.documentNum}</td>
									<td style="width: calc(50% - 260px);">${interessatRipea.representant.nomComplet}</td>
								</c:when>
								<c:otherwise>
									<td colspan="3">--</td>
								</c:otherwise>
							</c:choose>
							</td>
						</tr>
					</c:if>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</c:if>

	<c:set var="formAction"><rip:modalUrl value="/expedientPeticio/acceptarAmbInteressats/${expedientPeticioId}" /></c:set>
	<form:form id="interessatsForm" action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="expedientPeticioAcceptarCommand">
		<c:choose>
			<c:when test="${!empty interessatsDistribucio}">
				<table id="interessats-distribucio" class="table table-bordered table-striped">
					<thead>
					<tr>
						<td colspan="3" style="width: calc(50% - 100px);"><strong><spring:message code="contingut.interessat.info.interessat"/></strong></td>
						<td colspan="3" style="width: calc(50% - 100px);"><strong><spring:message code="contingut.interessat.info.representant"/></strong></td>
						<td style="width: 200px;"></td>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="${interessatsDistribucio}" var="interessatDistribucio" varStatus="status">
						<tr>
							<td data-docnum="${interessatDistribucio.documentNumero}" style="width: 140px;"><spring:message code="registre.interessat.tipus.enum.${interessatDistribucio.tipus}"/></td>
							<td style="width: 120px;">${interessatDistribucio.documentNumero}</td>
							<td >${interessatDistribucio.nomSencer}</td>
							<c:choose>
								<c:when test="${!empty interessatDistribucio.representant}">
									<td data-docnum="${interessatDistribucio.representant.documentNumero}" style="width: 140px;"><spring:message code="registre.interessat.tipus.enum.${interessatDistribucio.representant.tipus}"/></td>
									<td style="width: 120px;">${interessatDistribucio.representant.documentNumero}</td>
									<td style="width: calc(50% - 360px);">${interessatDistribucio.representant.nomSencer}</td>
								</c:when>
								<c:otherwise>
									<td data-docnum="" colspan="3">--</td>
								</c:otherwise>
							</c:choose>
							<td style="padding: 8px 24px;">
								<rip:inputHidden name="interessats[${status.index}].interessatDocNumero"/>
								<rip:inputSelect name="interessats[${status.index}].accio" inline="true" optionEnum="InteressatAssociacioAccioEnum"/>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<div class="well"> 
					<spring:message code="registre.interessat.buit"/>
				</div>
				<div style="min-height: 50px;"></div>
			</c:otherwise>
		</c:choose>
		
		<div id="modal-botons" class="well">
			<c:if test="${!isFirst}">
				<button type="button" name="previousPage" class="btn btn-default"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.previous"/></button>
			</c:if>
				
			<button id="btnSave" type="submit" class="btn btn-success">
				<c:choose>
					<c:when test="${isCrearNewExpedient}">
						<span class="fa fa-save"></span> <spring:message code="comu.boto.crear" />
					</c:when>
					<c:otherwise>
						<span class="fa fa-save"></span> <spring:message code="expedient.peticio.accio.enum.INCORPORAR" />
					</c:otherwise>
				</c:choose>
			</button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

