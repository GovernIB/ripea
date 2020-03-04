<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="fluxid">${fluxId}</c:set>

<html>
<head>
<script>
let rootIframe = window.frameElement;

if (rootIframe) {
	var fluxId = "${fluxId}";
	var FluxError = "${FluxError}";
	var FluxCreat = "${FluxCreat}";
	
	localStorage.setItem('fluxid', fluxId);
	localStorage.setItem('FluxError', FluxError);
	localStorage.setItem('FluxCreat', FluxCreat);
	rootIframe.parentElement.parentElement.parentElement.querySelector('button.close').click();
}
</script>
</head>
</html>
