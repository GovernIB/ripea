<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="titol"><spring:message code="firma.info.accio.flux.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	
<style type="text/css">

.container {
	width: 100%;
	display: flex;
	justify-content: center;
}

.blocks_container {
	width: 75%;
	display: flex;
	flex-flow: column;
	align-items: center;
	border: 1px solid #e7f1f1;
	padding: 2%;
	margin-bottom: 1%;
}

.block_container {
	width: 100%;
	display: flex;
	flex-flow: wrap;
	justify-content: center;
	align-items: center;
	border: 2px solid #1f20fd;
	border-radius: 4px;
	padding: 1%;
}

.signer_container {
	display: flex;
	border-radius: 4px;
	padding: 2%;
	margin: 1%;
}

.signed_false {
	background-color: #67bdff;
}

.signed_true {
	background-color: #02cda2;
}

.leyenda {
	width: 25%;
	height: auto;
	display: flex;
	flex-flow: column;
	margin-left: 2%;
}

.leyenda_title {
	font-weight: bold;
}

.leyenda_container {
	height: auto;
	display: flex;
	flex-flow: column;
	border: 1px solid #c7f1f1;
	border-radius: 6px;
	padding: 3%;
}
.leyenda_container div {
	display: flex;
	align-items: center;
}
.leyenda_container > div > span {
	margin-left: 3%;
}

.leyenda_block > div {
	width: 6%;
	height: 5px;
	background-color: #1f20fd;
}

.leyenda_pendent > div {
	width: 6%;
	height: 5px;
	background-color: #67bdff;
}

.leyenda_firmat > div {
	width: 6%;
	height: 5px;
	background-color: #02cda2;
}

.block_arrow {
	color: #1f20fd;
	font-size: 10px;
}

.block_start_end {
	width: 10%;
	background-color: #f5f5f5;
	border: 1px solid #cccccc;
	border-radius: 4px;
	text-align: center;
}

</style>
<script type="text/javascript">

</script>
</head>
<body>
<div class="container">
	<div class="blocks_container">
		<div class="block_start_end"><spring:message code="firma.info.accio.flux.inici"/></div>
		<i class="block_arrow fa fa-ellipsis-v"></i>
		<c:forEach items="${blocks}" var="block" varStatus="status">
			<div class="block_container">
				<c:forEach items="${block.signers}" var="signer">
					<div class="signer_container signed_${signer.signed}">
						<c:choose>
							<c:when test="${not emptysigner.signerNom}">
								${signer.signerNom} (${signer.signerCodi})
							</c:when>
							<c:otherwise>
							${signer.signerCodi}
							</c:otherwise>
						</c:choose><br>
						${signer.signerId}
					</div>
				</c:forEach>
			</div>
			<c:if test="${!status.last}">
				<i class="block_arrow fa fa-arrow-down"></i>
			</c:if>
		</c:forEach>
		<i class="block_arrow fa fa-ellipsis-v"></i>
		<div class="block_start_end"><spring:message code="firma.info.accio.flux.final"/></div>
	</div>
	<div class="leyenda">
		<div class="leyenda_title"><span><spring:message code="firma.info.accio.flux.llegenda"/></span></div>
		<div class="leyenda_container">
			<div class="leyenda_block">
				<div></div><span><spring:message code="firma.info.accio.flux.bloc"/></span>
			</div>
			<div class="leyenda_pendent">
				<div></div><span><spring:message code="firma.info.accio.flux.firma.pendent"/></span>
			</div>
			<div class="leyenda_firmat">
				<div></div><span><spring:message code="firma.info.accio.flux.firma.finalitzada"/></span>
			</div>
		</div>
	</div>
</div>
</body>
</html>
