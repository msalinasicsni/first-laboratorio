<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
    <spring:url value="/solicitante/update" var="sActionUrl"/>
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
                <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/solicitante/search" htmlEscape="true "/>"><spring:message code="menu.applicants" /></a></li>
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
						<i class="fa-fw fa fa-group"></i> 
							<spring:message code="applicants" />
						<span> <i class="fa fa-angle-right"></i>  
							<spring:message code="act.search" />
						</span>
					</h1>
				</div>
				<!-- end col -->
			</div>
			<!-- end row -->
			<!-- widget grid -->
			<section id="widget-grid" class="">
				<!-- row -->
				<div class="row">
					<!-- NEW WIDGET START -->
					<article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
						<!-- Widget ID (each widget will need unique ID)-->
						<div class="jarviswidget jarviswidget-color-darken" id="wid-id-0">
							<header>
								<span class="widget-icon"> <i class="fa fa-search"></i> </span>
								<h2><spring:message code="lbl.parameters" /> </h2>				
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
                                    <input id="blockUI_message" type="hidden" value="<spring:message code="blockUI.message"/>"/>
									<form id="search-form" class="smart-form" autocomplete="off">
										<fieldset>
											<section>
												<label class="input"> <i class="icon-append fa fa-check"></i>
													<input type="text" id="filtro" name="filtro" placeholder="<spring:message code="lbl.applicant.search.parameters"/>">
													<b class="tooltip tooltip-bottom-right"><spring:message code="lbl.applicant.search.parameters"/></b> </label>
											</section>
										</fieldset>
										<footer>
                                            <button type="button" id="create-person" class="btn btn-info"><i class="fa fa-file"></i> <spring:message code="act.add" /></button>
											<button type="submit" id="search-person" class="btn btn-info"><i class="fa fa-search"></i> <spring:message code="act.search" /></button>
										</footer>
									</form>						
								</div>
								<!-- end widget content -->
							</div>
							<!-- end widget div -->
						</div>
						<!-- end widget -->
					</article>
					<!-- WIDGET END -->
					<!-- NEW WIDGET START -->
					<article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
						<!-- Widget ID (each widget will need unique ID)-->
						<div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
							<header>
								<span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
								<h2><spring:message code="lbl.results" /> </h2>				
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
									<table id="applicants_result" class="table table-striped table-bordered table-hover" width="100%">
										<thead>			                
											<tr>
												<th data-class="expand"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.applicant.name"/></th>
												<th data-hide="phone"><i class="fa fa-fw fa-phone text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.telephone"/></th>
												<th data-hide="phone,tablet"><i class="fa fa-check fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.enabled"/></th>
												<th data-hide="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.contact.name"/></th>
												<th data-hide="phone,tablet"><i class="fa fa-fw fa-at text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.contact.email"/></th>
												<th data-hide="phone"><i class="fa fa-fw fa-phone txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.contact.movil"/></th>
												<th data-hide="phone,tablet"><spring:message code="lbl.actions"/></th>
											</tr>
										</thead>
                                        <c:if test="${not empty solicitante}">
                                            <tbody>
                                            <tr>
                                                <td>${solicitante.nombre}</td>
                                                <td>${solicitante.telefono}</td>
                                                <td align="center">
                                                    <c:choose>
                                                        <c:when test="${solicitante.pasivo}">
                                                            <span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${solicitante.nombreContacto}</td>
                                                <td>${solicitante.correoContacto}</td>
                                                <td>${solicitante.telefonoContacto}</td>
                                                <td><a href="${sActionUrl}/${solicitante.idSolicitante}" class="btn btn-primary btn-xs"><i class="fa fa-edit"></i></a></td>
                                            </tr>
                                            </tbody>
                                        </c:if>
									</table>
								</div>
								<!-- end widget content -->
							</div>
							<!-- end widget div -->
						</div>
						<!-- end widget -->
					</article>
					<!-- WIDGET END -->
				</div>
				<!-- end row -->
				<!-- row -->
				<div class="row">
					<!-- a blank row to get started -->
					<div class="col-sm-12">
						<!-- your contents here -->
					</div>
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
	<!-- JQUERY VALIDATE -->
	<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
	<script src="${jqueryValidate}"></script>
	<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
	<spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>				
	<script src="${jQValidationLoc}"></script>
	<!-- JQUERY BLOCK UI -->
	<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
	<script src="${jqueryBlockUi}"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<spring:url value="/resources/scripts/solicitante/search.js" var="solSearch" />
	<script src="${solSearch}"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
    <c:url var="createUrl" value="/solicitante/create"/>
    <c:url var="searchUrl" value="/solicitante/getSolicitantes"/>
	<script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {searchUrl: "${searchUrl}", sActionUrl: "${sActionUrl}",
                createUrl: "${createUrl}", blockMess : $("#blockUI_message").val()};
			SearchApplicant.init(parametros);
	    	$("li.mantenimiento").addClass("open");
	    	$("li.solicitantes").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.solicitantes").parents("ul").slideDown(200);
	    	}
		});
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>