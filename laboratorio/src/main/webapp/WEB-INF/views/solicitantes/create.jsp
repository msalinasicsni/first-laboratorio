<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<!-- BEGIN HEAD -->
<head>
	<jsp:include page="../fragments/headTag.jsp" />
    <style>
        textarea {
            resize: none;
        }
    </style>
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
							<spring:message code="lbl.applicant" />
						<span> <i class="fa fa-angle-right"></i>  
							${accion}
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
								<span class="widget-icon"> <i class="fa fa-edit"></i> </span>
								<h2><spring:message code="lbl.applicant" /> </h2>
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
                                <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                                <input id="msg_added" type="hidden" value="<spring:message code="msg.applicant.successfully.added"/>"/>
                                <input id="msg_updated" type="hidden" value="<spring:message code="msg.applicant.successfully.updated"/>"/>
                                <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                                <input id="smallBox_content4s" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                                <input id="idSolicitante" type="hidden" value="${solicitante.idSolicitante}"/>
									<form id="create-form" class="smart-form" autocomplete="off">
										<fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-5 col-lg-5">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                        <spring:message code="lbl.applicant.name"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" id="nombre" name="nombre" value="${solicitante.nombre}" placeholder=" <spring:message code="lbl.applicant.name" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.applicant.name"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-4 col-lg-4">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <spring:message code="lbl.address" />
                                                    </label>
                                                    <div class="">
                                                        <label class="textarea">
                                                            <i class="icon-prepend fa fa-map-marker fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <textarea class="form-control" rows="3" name="direccion" id="direccion"
                                                                      placeholder=" <spring:message code="lbl.address" />">${solicitante.direccion}</textarea>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.direccion"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                        <spring:message code="lbl.telephone"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-phone fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" name="telefono" id="telefono" value="${solicitante.telefono}"
                                                                   placeholder=" <spring:message code="lbl.telephone" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.telefono"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-5 col-lg-5">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.contact.name"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" name="contacto" id="contacto" value="${solicitante.nombreContacto}" placeholder=" <spring:message code="lbl.contact.name" />" />
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.contact.name"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-4 col-lg-4">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <spring:message code="lbl.contact.email" />
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-at fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" name="correoContacto" id="correoContacto" value="${solicitante.correoContacto}" placeholder=" <spring:message code="lbl.contact.email" />" />
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.contact.email"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.contact.movil" />
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-mobile fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" name="telefonoContacto" id="telefonoContacto" value="${solicitante.telefonoContacto}"
                                                                   placeholder=" <spring:message code="lbl.contact.movil" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.contact.movil"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-2 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.enabled"/>
                                                    </label>
                                                    <div class="row">
                                                        <div class="col col-4">
                                                            <label class="checkbox">
                                                                <c:choose>
                                                                    <c:when test="${solicitante.pasivo}">
                                                                        <input type="checkbox" name="checkbox-enable" id="checkbox-enable">
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <input type="checkbox" name="checkbox-enable" id="checkbox-enable" checked>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <i></i>
                                                            </label>
                                                        </div>
                                                    </div>
                                                </section>
                                            </div>
                                        </fieldset>
										<footer>
                                            <button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>
                                            <spring:url value="/solicitante/search" var="solicitantesUrl"/>
                                            <button type="reset"
                                                    onclick="location.href='${fn:escapeXml(solicitantesUrl)}'"
                                                    class="btn btn-danger">
                                                <i class="fa fa-times"></i>  <spring:message code="users.cancel" />
                                            </button>
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
	<spring:url value="/resources/scripts/solicitante/create.js" var="solCreate" />
	<script src="${solCreate}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
    <c:url var="solicitanteUrl" value="/solicitante/search/"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="saveUrl" value="/solicitante/save"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {solicitanteUrl: "${solicitanteUrl}",
                saveUrl : "${saveUrl}",
                blockMess: "${blockMess}"
            };
			CreateApplicant.init(parametros);
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