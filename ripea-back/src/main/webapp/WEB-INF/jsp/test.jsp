<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<html>
<head>
	<title>Test</title>
	


<style>




</style>


<script>

</script>


</head>

<body>


	<form:form action="${formAction}" method="post"  commandName="testCommand" role="form" >

		

		
 		<form:input path="dataSingle"/>
		
		<form:input path="dates"/> 
		
 		<form:input path="text"/> 

	</form:form>

</body>
</html>

