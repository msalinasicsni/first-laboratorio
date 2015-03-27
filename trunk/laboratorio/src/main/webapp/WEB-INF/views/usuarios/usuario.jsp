<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<jsp:include page="../fragments/headTag.jsp" />
</head>
<body>
	<jsp:include page="../fragments/bodyHeader.jsp" />
	<jsp:include page="../fragments/bodyNavigation.jsp" />
	<!-- Main bar -->
	<div class="mainbar">

        <!-- RIBBON -->
        <div id="ribbon">
			<span class="ribbon-button-alignment">
				<span id="refresh" class="btn btn-ribbon" data-action="resetWidgets" data-placement="bottom" data-original-title="<i class='text-warning fa fa-warning'></i> <spring:message code="msg.reset" />" data-html="true">
					<i class="fa fa-refresh"></i>
				</span>
			</span>
            <!-- breadcrumb -->
            <ol class="breadcrumb">
                <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/resultadoFinal/init" htmlEscape="true "/>"><spring:message code="menu.enter.final.result" /></a></li>
            </ol>
            <!-- end breadcrumb -->
            <jsp:include page="../fragments/layoutOptions.jsp" />
        </div>
        <!-- END RIBBON -->
		<!-- Matter -->

		<div class="matter">
			<div class="container">

				<!-- Table -->

				<div class="row">

					<div class="col-md-12">

						<div class="widget">

							<div class="widget-head">
								<div class="pull-left">
									Usuario
								</div>
								<div class="widget-icons pull-right">
									<a href="#" class="wminimize"><i class="icon-chevron-up"></i></a>
									<a href="#" class="wclose"><i class="icon-remove"></i></a>
								</div>
								<div class="clearfix"></div>
							</div>

							<div class="widget-content">
								<h3>
									<c:out value="${SUCCESS}" />
								</h3>
								<c:set var="rolesString">
									<c:forEach var="rol" items="${user.authorities}">
										<c:out value="${rol.authId.authority}" />
									</c:forEach>
								</c:set>
								<br />
								<table class="table table-striped table-bordered table-hover"
									id="tabla">
									<tr>
										<th>Nombre Usuario</th>
										<td><b><c:out value="${user.username}" /></b></td>
									</tr>
									<tr>
										<th>Descripción</th>
										<td><c:out value="${user.completeName}" /></td>
									</tr>
									<tr>
										<th>Habilitado</th>
										<td><c:out value="${user.enabled}" /></td>
									</tr>
									<tr>
										<th>Roles</th>
										<td><c:out value="${rolesString}" /></td>
									</tr>

								</table>
								<div class="widget-foot">
									<table class="table table-striped table-bordered table-hover"
										id="tabla2">
										<tr class="warning">
											<td><spring:url value="{username}/edit" var="editUrl">
													<spring:param name="username" value="${user.username}" />
												</spring:url>
												<button onclick="location.href='${fn:escapeXml(editUrl)}'"
													class="btn btn-info">
													Editar
												</button>
											<td><spring:url value="{username}/chgpass"
													var="chgpassUrl">
													<spring:param name="username" value="${user.username}" />
												</spring:url>
												<button
													onclick="location.href='${fn:escapeXml(chgpassUrl)}'"
													class="btn btn-info">
													Cambiar Contraseña
												</button> <c:choose>
													<c:when test="${user.enabled}">
														<td><spring:url value="{username}/disable"
																var="disableUrl">
																<spring:param name="username" value="${user.username}" />
															</spring:url>
															<button
																onclick="location.href='${fn:escapeXml(disableUrl)}'"
																class="btn btn-info">
																Deshabilitar
															</button>
													</c:when>
													<c:otherwise>
														<td><spring:url value="{username}/enable"
																var="enableUrl">
																<spring:param name="username" value="${user.username}" />
															</spring:url>
															<button
																onclick="location.href='${fn:escapeXml(enableUrl)}'"
																class="btn btn-info">
																Habilitar
															</button>
													</c:otherwise>
												</c:choose>
											<td><spring:url value="/usuarios/list" var="listUrl"></spring:url>
												<button onclick="location.href='${fn:escapeXml(listUrl)}'"
													class="btn btn-info">
													Ver Todos
												</button></td>
										</tr>
										<tr class="info">
											<c:choose>
												<c:when test="${fn:contains(rolesString,'ROLE_ADMIN')}">
													<td><spring:url value="{username}/noadmin"
															var="noadminUrl">
															<spring:param name="username" value="${user.username}" />
														</spring:url>
														<button
															onclick="location.href='${fn:escapeXml(noadminUrl)}'"
															class="btn btn-danger">
															No Admin
														</button>
												</c:when>
												<c:otherwise>
													<td><spring:url value="{username}/mkadmin"
															var="adminUrl">
															<spring:param name="username" value="${user.username}" />
														</spring:url>
														<button
															onclick="location.href='${fn:escapeXml(adminUrl)}'"
															class="btn btn-success">
															Admin
														</button>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test="${fn:contains(rolesString,'ROLE_SUPER')}">
													<td><spring:url value="{username}/nosup"
															var="nosupUrl">
															<spring:param name="username" value="${user.username}" />
														</spring:url>
														<button
															onclick="location.href='${fn:escapeXml(nosupUrl)}'"
															class="btn btn-danger">
															No Supervisor
														</button>
												</c:when>
												<c:otherwise>
													<td><spring:url value="{username}/mksup" var="supUrl">
															<spring:param name="username" value="${user.username}" />
														</spring:url>
														<button onclick="location.href='${fn:escapeXml(supUrl)}'"
															class="btn btn-success">
															Supervisor
														</button>
												</c:otherwise>
											</c:choose>
											<td></td>
										</tr>
									</table>
								</div>
							</div>
						</div>

					</div>
				</div>


			</div>
			<!-- Matter ends -->
		</div>
		<!-- Mainbar ends -->
		<div class="clearfix"></div>

	</div>

	<!-- Content ends -->
	<!-- Footer starts -->
	<jsp:include page="../fragments/footer.jsp" />
	<!-- Scroll to top -->
	<span class="totop"><a href="#"><i class="icon-chevron-up"></i></a></span>
	<jsp:include page="../fragments/corePlugins.jsp" />
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			$('#tabla').dataTable();
		});
	</script>
</body>
</html>