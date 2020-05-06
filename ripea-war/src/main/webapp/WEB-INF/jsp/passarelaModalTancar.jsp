<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script>
let rootIframe = window.frameElement;
let rootParentIframe = window.parent.frameElement;

if (rootIframe) {
	rootIframe.parentElement.parentElement.querySelector('button.close').click();
}

if (rootParentIframe) {
	rootParentIframe.parentElement.parentElement.querySelector('button.close').click();
	//Forçar refresh
	//let currentHref = window.parent.frameElement.src.substring(0,window.parent.frameElement.src.lastIndexOf("/"));
	//location.reload(currentHref);;
}
</script>
</head>
<body></body>
</html>
