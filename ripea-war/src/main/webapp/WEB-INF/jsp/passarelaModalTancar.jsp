<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script>
let parentIframe = window.frameElement;
let buttonClose;
if (parentIframe && parentIframe.parentElement && parentIframe.parentElement.parentElement) {
	buttonClose = rootIframe.parentElement.parentElement.querySelector('button.close');
}
if (!buttonClose && window.parent) {
	let parentParentIframe = window.parent.frameElement;
	if (parentParentIframe && parentParentIframe.parentElement && parentParentIframe.parentElement.parentElement) {
		buttonClose = parentParentIframe.parentElement.parentElement.querySelector('button.close');
	}
}
if (buttonClose) {
	buttonClose.click();
} else {
	console.error('No s\'ha pogut trobar el botó per tancar la modal');
}
</script>
</head>
<body></body>
</html>
