<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<!-- BEGIN HEAD -->
<head>
	<jsp:include page="../fragments/headTag.jsp" />
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
	<!-- #HEADER -->
	<jsp:include page="../fragments/bodyHeader.jsp" />
	<!-- #NAVIGATION -->
	<jsp:include page="../fragments/bodyNavigation.jsp" />
	<!-- MAIN PANEL -->
	<div id="main" data-role="main">
		<!-- RIBBON -->
		<div id="ribbon">
			<span class="ribbon-button-alignment"> 
				<span id="refresh" class="btn btn-ribbon" data-action="resetWidgets" data-placement="bottom" data-original-title="<i class='text-warning fa fa-warning'></i> <spring:message code="msg.reset" />" data-html="true">
					<i class="fa fa-refresh"></i>
				</span> 
			</span>
			<!-- breadcrumb -->
			<ol class="breadcrumb">
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/febriles/create" htmlEscape="true "/>"><spring:message code="menu.special.case" /></a></li>
			</ol>
			<!-- end breadcrumb -->
			<jsp:include page="../fragments/layoutOptions.jsp" />
		</div>
		<!-- END RIBBON -->
		<!-- MAIN CONTENT -->
		<div id="content">
			<!-- row -->
			<div class="row">
				<!-- col -->
				<div class="col-xs-12 col-sm-7 col-md-7 col-lg-4">
					<h1 class="page-title txt-color-blueDark">
						<!-- PAGE HEADER -->
						<i class="fa-fw fa fa-fire"></i> 
							<spring:message code="lbl.notification.prev" />
					</h1>
				</div>
				<!-- end col -->
			</div>
			<!-- end row -->
			<!-- widget grid -->
			<section id="widget-grid" class="">
				<!-- row -->
				<div class="row">
					<!-- a blank row to get started -->
				</div>
				<!-- end row -->
				<!-- row -->
				<div class="row">
					<!-- NEW WIDGET START -->
					<article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
						<!-- Widget ID (each widget will need unique ID)-->
						<div class="jarviswidget jarviswidget-color-darken" id="wid-id-0">
							<header>
								<span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
								<h2><spring:message code="lbl.notification.selectev" /> </h2>
							</header>
							<!-- widget div-->
							<div>
								<!-- widget edit box -->
								<div class="jarviswidget-editbox">
									<!-- This area used as dropdown edit box -->
									<input class="form-control" type="text">	
								</div>
								<!-- end widget edit box -->
								<!-- widget content -->
								<div class="widget-body no-padding">
									<table id="fichas_result" class="table table-striped table-bordered table-hover" width="100%">
										<thead>			                
											<tr>
												<th data-hide="phone"><i class="fa fa-fw fa-calendar txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.register.date"/></th>
												<th data-hide="phone"><i class="fa fa-fw fa-user txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.name1"/></th>
												<th data-hide="phone"><i class="fa fa-fw fa-user txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.lastname1"/></th>
												<th data-hide="phone,tablet"><i class="fa fa-fw fa-user txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.lastname2"/></th>
												<th></th>
												<th></th>
											</tr>
										</thead>
										<tbody>
										<c:forEach items="${notificaciones}" var="noti">
											<tr>
												<td><c:out value="${noti.fechaRegistro}" /></td>
												<td><c:out value="${noti.persona.primerNombre}" /></td>
												<td><c:out value="${noti.persona.primerApellido}" /></td>
												<td><c:out value="${noti.persona.segundoApellido}" /></td>
                                                        <spring:url value="/tomaMx/create/{idNotificacion}" var="editUrl">
                                                            <spring:param name="idNotificacion" value="${noti.idNotificacion}" />
                                                        </spring:url>
												<spring:url value="/febriles/delete/{idNotificacion}" var="deleteUrl">
													<spring:param name="idNotificacion" value="${noti.idNotificacion}" />
												</spring:url>
												<td><c:if test="${noti.pasivo==false}"><a href="${fn:escapeXml(editUrl)}" class="btn btn-default btn-xs"><i class="fa fa-edit"></i></a></c:if></td>
                                                <td><c:if test="${noti.pasivo==false}"><a href="${fn:escapeXml(deleteUrl)}" class="btn btn-danger btn-xs"><i class="fa fa-times"></i></a></c:if></td>
											</tr>
										</c:forEach>
										</tbody>
									</table>
								</div>
								<!-- end widget content -->
							</div>
							<!-- end widget div -->
							<div style="border: none" class="row">
                                <spring:url value="/tomaMx/create/{idPersona}" var="newUrl">
                                    <spring:param name="idPersona" value="${personaId}" />
                                </spring:url>
                                <a href="${fn:escapeXml(newUrl)}"
									class="btn btn-default btn-large btn-primary pull-right"><i
									class="fa fa-plus"></i> <spring:message
										code="lbl.add.notification" /> </a>
							</div>
						</div>
						<!-- end widget -->
					</article>
					<!-- WIDGET END -->
				</div>
				<!-- end row -->
			</section>
			<!-- end widget grid -->
		</div>
		<!-- END MAIN CONTENT -->
	</div>
	<!-- END MAIN PANEL -->
	<!-- BEGIN FOOTER -->
	<jsp:include page="../fragments/footer.jsp" />
	<!-- END FOOTER -->
	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<jsp:include page="../fragments/corePlugins.jsp" />
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<spring:url value="/resources/js/plugin/datatables/jquery.dataTables.min.js" var="dataTables" />
	<script src="${dataTables}"></script>
	<spring:url value="/resources/js/plugin/datatables/dataTables.colVis.min.js" var="dataTablesColVis" />
	<script src="${dataTablesColVis}"></script>
	<spring:url value="/resources/js/plugin/datatables/dataTables.tableTools.min.js" var="dataTablesTableTools" />
	<script src="${dataTablesTableTools}"></script>
	<spring:url value="/resources/js/plugin/datatables/dataTables.bootstrap.min.js" var="dataTablesBootstrap" />
	<script src="${dataTablesBootstrap}"></script>
	<spring:url value="/resources/js/plugin/datatable-responsive/datatables.responsive.min.js" var="dataTablesResponsive" />
	<script src="${dataTablesResponsive}"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<!-- END PAGE LEVEL SCRIPTS -->
	<script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
	    	$("li.recepcion").addClass("open");
            $("li.tomaMx").addClass("active");
            if ("top" != localStorage.getItem("sm-setmenu")) {
                $("li.tomaMx").parents("ul").slideDown(200);
            }

		});
		var responsiveHelper_dt_basic = undefined;
		var breakpointDefinition = {
			tablet : 1024,
			phone : 480
		};
		var table1 = $('#fichas_result').dataTable({
			"sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
				"t"+
				"<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
			"autoWidth" : true,
			"preDrawCallback" : function() {
				// Initialize the responsive datatables helper once.
				if (!responsiveHelper_dt_basic) {
					responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#fichas_result'), breakpointDefinition);
				}
			},
			"rowCallback" : function(nRow) {
				responsiveHelper_dt_basic.createExpandIcon(nRow);
			},
			"drawCallback" : function(oSettings) {
				responsiveHelper_dt_basic.respond();
			}
		});
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>