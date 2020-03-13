<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="fluxid">${fluxId}</c:set>

<html>
<head>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script>
let rootIframe = window.frameElement;

if (rootIframe) {
	var digitalizacioError = "${digitalizacioError}";
	var digitalizacioFinalOk = "${digitalizacioFinalOk}";
	var nomDocument = "${nomDocument}";
	
	var idTransaccio = localStorage.getItem('transaccioId');
	var urlDescarrega = "<a class='downloadLink' href='<c:url value='/digitalitzacio/descarregarResultat/" + idTransaccio + "'/>' >" + nomDocument + "</a>"
	var urlCancel = " <br><span class='btn btn-default scan-cancel-btn'>Cancel·lar</span>"

	if (digitalizacioError != null && digitalizacioError != '') {
		alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + digitalizacioError + '</span></div>'
	} else if (digitalizacioFinalOk != null && digitalizacioFinalOk != '') {
		alertDiv = '<div class="alert alert-success" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + digitalizacioFinalOk + '</span></div>'
	}
	$(rootIframe.parentElement.parentElement).append(alertDiv);
	
	$(rootIframe.parentElement.parentElement).append(urlDescarrega);
	$(rootIframe.parentElement.parentElement).append(urlCancel);
	
	$(rootIframe.parentElement).trigger('remove');
	webutilModalAdjustHeight();
}
</script>
</head>
</html>
