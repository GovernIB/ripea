<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="fluxid">${fluxId}</c:set>

<html>
<head>
<script>
let rootIframe = window.frameElement;

if (rootIframe) {
	var nomDocument = "${nomDocument}";
	var idTransaccio = localStorage.getItem('transaccioId');

	var urlDescarrega = "<a href='<c:url value='/digitalitzacio/descarregarResultat/" + idTransaccio + "'/>' >" + nomDocument + "</a>"
	var urlCancel = " <a class='btn btn-default' href='<c:url value='/digitalitzacio/tancarTransaccio/" + idTransaccio + "'/>' >Cancel·lar</a>"
	$(rootIframe.parentElement.parentElement).append(urlDescarrega);
	$(rootIframe.parentElement.parentElement).append(urlCancel);
	rootIframe.parentElement.remove();
}
</script>
</head>
</html>
