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
        .alert {
            margin-bottom: 0px;
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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/recepcionMx/searchOrdersLab" htmlEscape="true "/>"><spring:message code="menu.receipt.orders.lab" /></a></li>
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
							<spring:message code="lbl.result.title" />
						<span> <i class="fa fa-angle-right"></i>  
							<spring:message code="lbl.result.income.subtitle" />
						</span>
					</h1>
				</div>
				<!-- end col -->
				<!-- right side of the page with the sparkline graphs -->
				<!-- col -->
				<div class="col-xs-12 col-sm-5 col-md-5 col-lg-8">
					<!-- sparks -->
					<ul id="sparks">
						<li class="sparks-info">
							<h5> <spring:message code="sp.day" /> <span class="txt-color-greenDark"><i class="fa fa-arrow-circle-down"></i>17</span></h5>
							<div class="sparkline txt-color-blue hidden-mobile hidden-md hidden-sm">
								0,1,3,4,11,12,11,13,10,11,15,14,20,17
							</div>
						</li>
						<li class="sparks-info">
							<h5> <spring:message code="sp.week" /> <span class="txt-color-red"><i class="fa fa-arrow-circle-up"></i>&nbsp;57</span></h5>
							<div class="sparkline txt-color-purple hidden-mobile hidden-md hidden-sm">
								23,32,11,23,33,45,44,54,45,48,57
							</div>
						</li>
						<li class="sparks-info">
							<h5> <spring:message code="sp.month" /> <span class="txt-color-red"><i class="fa fa-arrow-circle-up"></i>&nbsp;783</span></h5>
							<div class="sparkline txt-color-purple hidden-mobile hidden-md hidden-sm">
								235,323,114,231,333,451,444,541,451,483,783
							</div>
						</li>
					</ul>
					<!-- end sparks -->
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
                                <span class="widget-icon"> <i class="fa fa-th"></i> </span>
                                <h2><spring:message code="lbl.receipt.widgettitle" /> </h2>
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
                                    <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                                    <input id="msg_result_added" type="hidden" value="<spring:message code="msg.result.successfully.added"/>"/>
                                    <input id="msg_test_added" type="hidden" value="<spring:message code="msg.testVA.successfully.added"/>"/>

                                    <input id="msg_result_override" type="hidden" value="<spring:message code="msg.result.successfully.canceled"/>"/>
                                    <input id="msg_receipt_cancel" type="hidden" value="<spring:message code="msg.receipt.cancel"/>"/>
                                    <form id="addResult-form" class="smart-form" autocomplete="off">
                                        <fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name1"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="primerNombre" name="primerNombre" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.persona.primerNombre}" placeholder=" <spring:message code="person.name1" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="primerNombre" name="primerNombre" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.persona.primerNombre}" placeholder=" <spring:message code="person.name1" />">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nombre1"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name2"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                            <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                <input class="form-control" type="text" disabled name="segundoNombre" id="segundoNombre" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.persona.segundoNombre}" placeholder=" <spring:message code="person.name2" />" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <input class="form-control" type="text" disabled name="segundoNombre" id="segundoNombre" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.persona.segundoNombre}" placeholder=" <spring:message code="person.name2" />" />
                                                            </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nombre2"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.lastname1"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled name="primerApellido" id="primerApellido" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.persona.primerApellido}" placeholder=" <spring:message code="person.lastname1" />" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled name="primerApellido" id="primerApellido" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.persona.primerApellido}" placeholder=" <spring:message code="person.lastname1" />" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido1"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.lastname2"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled name="segundoApellido" id="segundoApellido" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.persona.segundoApellido}" placeholder=" <spring:message code="person.lastname2" />"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled name="segundoApellido" id="segundoApellido" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.persona.segundoApellido}" placeholder=" <spring:message code="person.lastname2" />"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido2"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.notification.type" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="TipoNoti" name="TipoNoti" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.codTipoNotificacion.valor}" placeholder=" <spring:message code="lbl.sample.type" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="TipoNoti" name="TipoNoti" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.codTipoNotificacion.valor}" placeholder=" <spring:message code="lbl.sample.type" />">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sample.type"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.symptoms.start.date.full"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="fechaIniSintomas" name="fechaIniSintomas" value="<fmt:formatDate value="${fechaInicioSintomas}" pattern="dd/MM/yyyy" />"
                                                                   placeholder=" <spring:message code="lbl.receipt.symptoms.start.date.full" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.receipt.symptoms.start.date.full"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sample.type" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="codTipoMx" name="codTipoMx" value="${ordenExamen.solicitudDx.idTomaMx.codTipoMx.nombre}" placeholder=" <spring:message code="lbl.sample.type" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="codTipoMx" name="codTipoMx" value="${ordenExamen.solicitudEstudio.idTomaMx.codTipoMx.nombre}" placeholder=" <spring:message code="lbl.sample.type" />">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sample.type"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sampling.datetime"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                            <input class="form-control" type="text" disabled id="fechaHoraTomaMx" name="fechaHoraTomaMx" value="<fmt:formatDate value="${ordenExamen.solicitudDx.idTomaMx.fechaHTomaMx}" pattern="dd/MM/yyyy hh:mm:ss a" />"
                                                                </c:when>
                                                                <c:otherwise>
                                                            <input class="form-control" type="text" disabled id="fechaHoraTomaMx" name="fechaHoraTomaMx" value="<fmt:formatDate value="${ordenExamen.solicitudEstudio.idTomaMx.fechaHTomaMx}" pattern="dd/MM/yyyy hh:mm:ss a" />"
                                                                </c:otherwise>
                                                            </c:choose>
                                                                   placeholder=" <spring:message code="lbl.sampling.datetime" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sampling.datetime"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.solic.name"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="tipoDx" name="tipoDx" value="${ordenExamen.solicitudDx.codDx.nombre}" placeholder=" <spring:message code="lbl.dx.type" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="tipoDx" name="tipoDx" value="${ordenExamen.solicitudEstudio.tipoEstudio.nombre}" placeholder=" <spring:message code="lbl.dx.type" />">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.dx.type"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.solic.DateTime"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="fechaHoraDx" name="fechaHoraDx" value="<fmt:formatDate value="${ordenExamen.solicitudDx.fechaHSolicitud}" pattern="dd/MM/yyyy hh:mm:ss a" />"
                                                                           placeholder=" <spring:message code="lbl.dx.solic.datetime" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="fechaHoraDx" name="fechaHoraDx" value="<fmt:formatDate value="${ordenExamen.solicitudEstudio.fechaHSolicitud}" pattern="dd/MM/yyyy hh:mm:ss a" />"
                                                                           placeholder=" <spring:message code="lbl.dx.solic.datetime" />">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.dx.solic.datetime"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.test"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="nombreExamen" name="nombreExamen" value="${ordenExamen.codExamen.nombre}" placeholder=" <spring:message code="lbl.test.name" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.test.name"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.pcr.area" />
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="codAreaPrc" name="codAreaPrc" value="${ordenExamen.codExamen.area.nombre}" placeholder=" <spring:message code="lbl.receipt.pcr.area" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.receipt.pcr.area"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-4 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.silais" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="codSilais" name="codSilais" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.codSilaisAtencion.nombre}"  placeholder=" <spring:message code="lbl.silais" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="codSilais" name="codSilais" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.codSilaisAtencion.nombre}"  placeholder=" <spring:message code="lbl.silais" />">
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.silais"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-8 col-lg-6">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.health.unit" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <c:choose>
                                                                <c:when test="${not empty ordenExamen.solicitudDx}">
                                                                    <input class="form-control" type="text" disabled id="codUnidadSalud" name="codUnidadSalud" value="${ordenExamen.solicitudDx.idTomaMx.idNotificacion.codUnidadAtencion.nombre}" placeholder=" <spring:message code="lbl.health.unit" />">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input class="form-control" type="text" disabled id="codUnidadSalud" name="codUnidadSalud" value="${ordenExamen.solicitudEstudio.idTomaMx.idNotificacion.codUnidadAtencion.nombre}" placeholder=" <spring:message code="lbl.health.unit" />">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.health.unit"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                        </fieldset>
                                        <fieldset>
                                            <header>
                                                <spring:message code="lbl.header.result.orders.form" />
                                            </header>
                                            <br>
                                            <div id="resultados">
                                            </div>
                                         </fieldset>
                                        <footer>
                                            <input id="val_yes" type="hidden" value="<spring:message code="lbl.yes"/>"/>
                                            <input id="val_no" type="hidden" value="<spring:message code="lbl.no"/>"/>
                                            <input id="idExamen" type="hidden" value="${ordenExamen.codExamen.idExamen}"/>
                                            <input id="idOrdenExamen" type="hidden" value="${ordenExamen.idOrdenExamen}"/>
                                            <button type="button" id="override-result" class="btn btn-danger btn-lg pull-right header-btn"><i class="fa fa-times"></i> <spring:message code="act.override" /></button>
                                            <button type="submit" id="save-result" class="btn btn-success btn-lg pull-right header-btn"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>
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
            <!-- Modal -->
            <div class="modal fade" id="myModal" aria-hidden="true" data-backdrop="static">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <!--<h4 class="modal-title">
                    <spring:message code="lbl.response.header.modal.add" />
                </h4>-->
                            <div class="alert alert-info">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                    &times;
                                </button>
                                <h4 class="modal-title">
                                    <i class="fa-fw fa fa-font"></i>
                                    <spring:message code="lbl.result.header.modal.override" />
                                </h4>
                            </div>
                        </div>
                        <div class="modal-body"> <!--  no-padding -->
                            <form id="override-result-form" class="smart-form" novalidate="novalidate">
                                <fieldset>
                                    <div class="row">
                                        <section class="col col-sm-12 col-md-12 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.nullified" /> </label>
                                            <div class="">
                                                <label class="textarea">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <textarea class="form-control" rows="3" name="causaAnulacion" id="causaAnulacion"
                                                              placeholder="<spring:message code="lbl.nullified" />"></textarea>
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nullified"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                    </div>
                                </fieldset>
                                <footer>
                                    <button type="submit" class="btn btn-primary" id="btnAceptarAnulacion">
                                        <spring:message code="act.ok" />
                                    </button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">
                                        <spring:message code="act.cancel" />
                                    </button>

                                </footer>

                            </form>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->
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
    <spring:url value="/resources/js/plugin/datatables/swf/copy_csv_xls_pdf.swf" var="tabletools" />
    <!-- jQuery Selecte2 Input -->
    <spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
    <script src="${selectPlugin}"></script>
    <!-- JQUERY VALIDATE -->
	<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
	<script src="${jqueryValidate}"></script>
	<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
	<spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>				
	<script src="${jQValidationLoc}"></script>
	<!-- JQUERY BLOCK UI -->
	<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
	<script src="${jqueryBlockUi}"></script>
    <!-- JQUERY INPUT MASK -->
    <spring:url value="/resources/js/plugin/jquery-inputmask/jquery.inputmask.bundle.min.js" var="jqueryInputMask" />
    <script src="${jqueryInputMask}"></script>
    <!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<spring:url value="/resources/scripts/resultados/income-Result.js" var="incomeResult" />
	<script src="${incomeResult}"></script>
    <spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
    <script src="${handleInputMask}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="ordersUrl" value="/resultados/searchOrders"/>

    <c:url var="unidadesURL" value="/api/v1/unidadesPrimariasHospSilais"/>
    <c:url var="sRespuestasUrl" value="/administracion/respuestas/getRespuestasActivasExamen"/>
    <c:url var="sListasUrl" value="/resultados/getCatalogosListaConceptoByIdExamen"/>
    <c:url var="sDetResultadosUrl" value="/resultados/getDetallesResultadoByExamen"/>
    <c:url var="sAlicuotasUrl" value="/resultados/init"/>
    <c:url var="sSaveResult" value="/resultados/saveResult"/>
    <c:url var="sOverrideResult" value="/resultados/overrideResult"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {sPersonUrl: "${sPersonUrl}",
                sOrdersUrl : "${ordersUrl}",
                sUnidadesUrl : "${unidadesURL}",
                blockMess: "${blockMess}",
                sConceptosUrl: "${sRespuestasUrl}",
                sAlicuotasUrl : "${sAlicuotasUrl}",
                sSaveResult : "${sSaveResult}",
                sListasUrl : "${sListasUrl}",
                sEsIngreso : 'true',
                sDetResultadosUrl : "${sDetResultadosUrl}",
                sOverrideResult : "${sOverrideResult}"
            };
            IncomeResult.init(parametros);
            handleInputMasks();
            $("li.resultado").addClass("open");
	    	$("li.ingresoResultado").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.ingresoResultado").parents("ul").slideDown(200);
	    	}
            //$('#codCalidadMx').change();
        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>