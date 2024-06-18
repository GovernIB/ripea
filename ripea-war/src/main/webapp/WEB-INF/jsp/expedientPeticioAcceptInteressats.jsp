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

		// $("select").change(function (e) {
		// 	updateInteressatsRipeaTable($(this).closest('tr').find('td:first').data('docnum'));
		// });

		// updateAccionsInteressatsDistribucio();
	});

	// const updateInteressatsRipeaTable = (docnum) => {
	<%--	if (${!isCrearNewExpedient}) {--%>
	// 		const interessatDistribucio = getInteressatDistribucio(docnum);
	// 		const representantDistribucio = interessatDistribucio.representant;
	// 		const row = getRowWithInteressatDoc(docnum)
	// 		if (row === null) {
	// 			// Afegir fila
	// 			const interessat = interessatDistribucio.documentNom;
	// 			const representant = isNullOrEmpty(representantDistribucio) ? '--' : representantDistribucio.documentNom;
	// 			const fila = $('<tr><td data-docnum="' + docnum + '">' + interessat + '</td><td>' + representant + '</td>')
	// 			$("#interessats-ripea>tbody").append(fila);
	// 		} else {
	// 			// Actualitzar fila
	// 			row.find('td:first').text(interessatDistribucio.documentNom);
	// 			if (isNullOrEmpty(representantDistribucio)) {
	// 				row.find('td:nth-child(2)').text('--');
	// 			} else {
	// 				row.find('td:nth-child(2)').text(representantDistribucio.documentNom);
	// 			}
	// 		}
	// 	}
	// }

	// const getInteressatDistribucio = (docnum) => {
	// 	var result = $.grep(interessatsDistr, function (e) {
	// 		return e.documentNumero == docnum;
	// 	});
	// 	if (result.length == 0) {
	// 		return null;
	// 	} else {
	// 		return result[0];
	// 	}
	// }

	// const getRowWithInteressatDoc = (docnum) => {
	// 	let selectedRow = null;
	// 	$("#interessats-ripea>tbody>tr").each(function () {
	// 		if ($(this).find("td:first").data('docnum') == docnum) {
	// 			selectedRow = $(this);
	// 		}
	// 	});
	// 	return selectedRow;
	// }

	// const updateAccionsInteressatsDistribucio = () => {
	<%--	if (${isCrearNewExpedient}) {--%>
	// 		$('#interessats-distribucio>tbody>tr').each(function () {
	// 			const selectAccions = $(this).find('td:nth-child(3)').find('select');
	// 			$('option[value="ASSOCIAR_SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	// 			$('option[value="SOBREESCRIURE"]', selectAccions).remove();
	// 			$('option[value="SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	// 		});
	// 	} else {
	// 		$('#interessats-distribucio>tbody>tr').each(function () {
	// 			const interessatDocNum = $(this).find('td:first').data("docnum");
	// 			const representantDocNum = $(this).find('td:nth-child(2)').data("docnum");
	// 			const interessatOvewritten = getInteressatRipea(interessatDocNum);
	// 			const isRepresentantOvewritten = $.inArray(representantDocNum, interessatsOverw) !== -1;
	//
	// 			const selectAccions = $(this).find('td:nth-child(3)').find('select');
	//
	// 			if (interessatOvewritten != null) {
	//				// Si existeix l'interessat eliminam la opció d'associar nou interessat
	//				$('option[value="ASSOCIAR"]', selectAccions).remove();
	// 				$('option[value="ASSOCIAR_SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	//				// Si l'interessat té un representant diferent al que arriba,
	//				// o actualment té representant, i arriba sense, eliminam la opció de Actualitzar dades
	// 				const representantRipea = interessatOvewritten.representant;
	//
	// 				if (!sameRepresentant(representantRipea, representantDocNum)) {
	// 					$('option[value="SOBREESCRIURE"]', selectAccions).remove();
	// 				} else {
	// 					$('option[value="SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	// 				}
	// 			} else if (isRepresentantOvewritten) {
	//				// Si el representant existeix deixam únicament les opcions d'Associar nou interessat i actualitzar representant i no associar
	// 				$('option[value="ASSOCIAR"]', selectAccions).remove();
	// 				$('option[value="SOBREESCRIURE"]', selectAccions).remove();
	// 				$('option[value="SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	// 			} else {
	// 				$('option[value="ASSOCIAR_SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	// 				$('option[value="SOBREESCRIURE"]', selectAccions).remove();
	// 				$('option[value="SOBREESCRIURE_REPRESENTANT"]', selectAccions).remove();
	// 			}
	// 		});
	// 	}
	// }

	// const getInteressatRipea = (docnum) => {
	// 	var result = $.grep(interessatsRipea, function (e) {
	// 		return e.documentNum == docnum;
	// 	});
	// 	if (result.length == 0) {
	// 		return null;
	// 	} else {
	// 		return result[0];
	// 	}
	// }
	// const sameRepresentant = (representantRipea, codiRepresentantDistribucio) => {
	// 	if (representantRipea === null || Object.keys(representantRipea).length === 0) {
	// 		return isNullOrEmpty(codiRepresentantDistribucio);
	// 	} else {
	// 		return representantRipea.documentNum === codiRepresentantDistribucio;
	// 	}
	// }

	// const isAttrributeNullOrEmpty = (obj, attr) => {
	// 	return (obj[attr] === null || obj[attr] === '' || typeof obj[attr] === 'undefined');
	// }
	// const isNullOrEmpty = (obj) => {
	// 	return (obj === null || obj === '' || typeof obj === 'undefined');
	// }
</script>

</head>
<body>
	<c:if test="${!isCrearNewExpedient}">
		<div class="well b40">
			<h4>Interessats associats a l'expedient:</h4>
			<table id="interessats-ripea" class="table table-bordered table-striped font-decresed">
				<thead>
				<tr>
					<td><spring:message code="contingut.interessat.info.interessat"/></td>
					<td><spring:message code="contingut.interessat.info.representant"/></td>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${interessatsRipea}" var="interessatRipea" varStatus="index">
					<c:if test="${!interessatRipea.esRepresentant}">
						<c:set var="interessatsOv" value="${documentInteressatsOverwritten}" />
						<tr <c:if test="${fn:contains(interessatsOv, interessatRipea.documentNum) or (interessatRipea.representant != null and fn:contains(interessatsOv, interessatRipea.representant.documentNum))}">class="int-ov"</c:if>>
							<td data-docnum="${interessatRipea.documentNum}">${interessatRipea.nomCompletAmbDocument}</td>
							<td>
								<c:choose>
									<c:when test="${!empty interessatRipea.representant}">
										${interessatRipea.representant.nomCompletAmbDocument}
									</c:when>
									<c:otherwise>
										--
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
	<form:form id="interessatsForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientPeticioAcceptarCommand">
		<c:choose>
			<c:when test="${!empty interessatsDistribucio}">
				<table id="interessats-distribucio" class="table table-bordered table-striped">
					<thead>
					<tr>
						<td style="width: 35%;"><spring:message code="contingut.interessat.info.interessat"/></td>
						<td style="width: 35%;"><spring:message code="contingut.interessat.info.representant"/></td>
						<td></td>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="${interessatsDistribucio}" var="interessatDistribucio" varStatus="status">
						<tr>
							<td data-docnum="${interessatDistribucio.documentNumero}">${interessatDistribucio.documentNom}</td>
							<c:choose>
								<c:when test="${!empty interessatDistribucio.representant}">
									<td data-docnum="${interessatDistribucio.representant.documentNumero}">${interessatDistribucio.representant.documentNom}</td>
								</c:when>
								<c:otherwise>
									<td data-docnum="">--</td>
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
					<spring:message code="registre.annex.buit"/>
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

