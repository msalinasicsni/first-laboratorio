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
        /*textarea {
            resize: none;
        }*/
        .styleTime {
            orientation: bottom;
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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/recepcionMx/init" htmlEscape="true "/>"><spring:message code="lbl.check-in.samples.from.alerta" /></a></li>
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
						<i class="fa-fw fa fa-eyedropper"></i>
							<spring:message code="lbl.check-in.samples.from.alerta" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.receipt.orders" />
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
                                    <input id="msg_receipt_added" type="hidden" value="<spring:message code="msg.receipt.successfully.added"/>"/>
                                    <input id="msg_receipt_cancel" type="hidden" value="<spring:message code="msg.receipt.cancel"/>"/>
                                    <input id="txtEsLaboratorio" type="hidden" value="true"/>
                                    <form id="receiptOrders-form" class="smart-form" autocomplete="off">
                                        <fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name1"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="primerNombre" name="primerNombre" value="${tomaMx.idNotificacion.persona.primerNombre}" placeholder=" <spring:message code="person.name1" />">
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
                                                            <input class="form-control" type="text" disabled name="segundoNombre" id="segundoNombre" value="${tomaMx.idNotificacion.persona.segundoNombre}" placeholder=" <spring:message code="person.name2" />" />
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
                                                            <input class="form-control" type="text" disabled name="primerApellido" id="primerApellido" value="${tomaMx.idNotificacion.persona.primerApellido}" placeholder=" <spring:message code="person.lastname1" />" />
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
                                                            <input class="form-control" type="text" disabled name="segundoApellido" id="segundoApellido" value="${tomaMx.idNotificacion.persona.segundoApellido}" placeholder=" <spring:message code="person.lastname2" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido2"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.symptoms.start.date.full"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>
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
                                                            <input class="form-control" type="text" disabled id="codTipoMx" name="codTipoMx" value="${tomaMx.codTipoMx.nombre}" placeholder=" <spring:message code="lbl.sample.type" />">
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
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="fechaHoraTomaMx" name="fechaHoraTomaMx" value="<fmt:formatDate value="${tomaMx.fechaHTomaMx}" pattern="dd/MM/yyyy" /> ${tomaMx.horaTomaMx}"
                                                                   placeholder=" <spring:message code="lbl.sampling.datetime" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sampling.datetime"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sample.number.tubes.full"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="cantidadTubos" name="cantidadTubos" value="${tomaMx.canTubos}"
                                                                   placeholder=" <spring:message code="lbl.sample.number.tubes.full" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sample.number.tubes.full"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sample.separation.full"/>
                                                    </label>
                                                    <div class="inline-group">
                                                        <label class="radio state-disabled">
                                                            <c:choose>
                                                                <c:when test="${tomaMx.mxSeparada==true}">
                                                                    <input type="radio" name="radio-inline" disabled checked="checked">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input type="radio" name="radio-inline" disabled>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <i></i><spring:message code="lbl.yes"/></label>
                                                        <label class="radio state-disabled">
                                                            <c:choose>
                                                                <c:when test="${tomaMx.mxSeparada==false}">
                                                                    <input type="radio" name="radio-inline" disabled checked="checked">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input type="radio" name="radio-inline" disabled>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <i></i><spring:message code="lbl.no"/></label>
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
                                                            <input class="form-control" type="text" disabled id="codSilais" name="codSilais" value="${tomaMx.idNotificacion.codSilaisAtencion.nombre}" placeholder=" <spring:message code="lbl.silais" />">
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
                                                            <input class="form-control" type="text" disabled id="codUnidadSalud" name="codUnidadSalud" value="${tomaMx.idNotificacion.codUnidadAtencion.nombre}" placeholder=" <spring:message code="lbl.health.unit" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.health.unit"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div>
                                                <header>
                                                    <label class="text-left txt-color-blue" style="font-weight: bold">
                                                        <spring:message code="lbl.header.receipt.solic" />
                                                    </label>
                                                </header>
                                                <br/>
                                                <br/>
                                                <div class="widget-body no-padding">
                                                    <table id="solicitudes_list" class="table table-striped table-bordered table-hover" width="100%">
                                                        <thead>
                                                        <tr>
                                                            <th data-class="expand"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.type"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.desc.request"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.DateTime"/></th>
                                                            <th data-hide="phone"><spring:message code="lbl.solic.area.prc"/></th>
                                                            <th data-hide="phone"><spring:message code="lbl.cc"/></th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        <c:choose>
                                                            <c:when test="${not empty dxList}">
                                                                <c:forEach items="${dxList}" var="record">
                                                                    <tr>
                                                                        <td><spring:message code="lbl.routine" /></td>
                                                                        <td><c:out value="${record.codDx.nombre}" /></td>
                                                                        <td><fmt:formatDate value="${record.fechaHSolicitud}" pattern="dd/MM/yyyy hh:mm:ss a" /></td>
                                                                        <td><c:out value="${record.codDx.area.nombre}" /></td>
                                                                        <c:choose>
                                                                            <c:when test="${record.controlCalidad}">
                                                                                <td><spring:message code="lbl.yes" /></td>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <td><spring:message code="lbl.no" /></td>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </tr>
                                                                </c:forEach>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:forEach items="${estudiosList}" var="record">
                                                                    <tr>
                                                                        <td><spring:message code="lbl.study" /></td>
                                                                        <td><c:out value="${record.tipoEstudio.nombre}" /></td>
                                                                        <td><fmt:formatDate value="${record.fechaHSolicitud}" pattern="dd/MM/yyyy hh:mm:ss a" /></td>
                                                                        <td><c:out value="${record.tipoEstudio.area.nombre}" /></td>
                                                                        <td><spring:message code="lbl.no" /></td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </fieldset>
                                        <fieldset>
                                            <header>
                                                <label class="text-left txt-color-blue" style="font-weight: bold">
                                                    <spring:message code="lbl.header.receipt.orders.form" />
                                                </label>
                                            </header>
                                            <br/>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.receipt.check.quantity.tubes"/>
                                                    </label>
                                                    <div class="inline-group">
                                                        <label class="radio">
                                                             <input type="radio" name="rdCantTubos" value="true">
                                                            <i></i><spring:message code="lbl.yes"/></label>
                                                        <label class="radio">
                                                            <input type="radio" name="rdCantTubos" value="false">
                                                            <i></i><spring:message code="lbl.no"/></label>
                                                    </div>
                                                    <div hidden="hidden" id="dErrorCantTubos" class="errorDiv txt-color-red"><spring:message code="lbl.required.field"/></div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.receipt.check.typemx"/>
                                                    </label>
                                                    <div class="inline-group">
                                                        <label class="radio">
                                                            <input type="radio" name="rdTipoMx" value="true">
                                                            <i></i><spring:message code="lbl.yes"/></label>
                                                        <label class="radio">
                                                            <input type="radio" name="rdTipoMx" value="false">
                                                            <i></i><spring:message code="lbl.no"/></label>
                                                    </div>
                                                    <div hidden="hidden" id="dErrorTipoMx" class="errorDiv txt-color-red"><spring:message code="lbl.required.field"/></div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sample.inadequate"/>
                                                    </label>
                                                    <div class="inline-group">
                                                        <label class="radio">
                                                            <input type="radio" name="rdMxInadequate" value="true">
                                                            <i></i><spring:message code="lbl.yes"/></label>
                                                        <label class="radio">
                                                            <input type="radio" name="rdMxInadequate" value="false" checked>
                                                            <i></i><spring:message code="lbl.no"/></label>
                                                    </div>
                                                </section>
                                                <div class="col col-sm-12 col-md-6 col-lg-5" id="dvCausa">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.cause.rejection" /> </label>
                                                    <!--<div class="">
                                                        <label class="textarea">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <textarea class="form-control" rows="3" name="causaRechazo" id="causaRechazo"
                                                                      placeholder="<spring:message code="lbl.cause.rejection" />"></textarea>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.cause.rejection"/>
                                                            </b>
                                                        </label>
                                                    </div>-->
                                                    <div class="input-group">
                                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                        <select id="causaRechazo" name="causaRechazo"
                                                                class="select2">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${causasRechazo}" var="causa">
                                                                <option value="${causa.codigo}">${causa.valor}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <section class="col col-3">
                                                    <label  class="text-left txt-color-blue font-md">
                                                       <spring:message code="lbl.received.date" />
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>
                                                            <input name="fechaRec" id="fechaRec" type='text'
                                                                   class="form-control date-picker" data-date-end-date="+0d"
                                                                   placeholder="<spring:message code="lbl.received.date" />"/>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.received.time" />
                                                    </label>
                                                    <div class=''>
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-clock-o fa-fw"></i>
                                                            <input id="horaRec" name="horaRec" type='text'
                                                                   class="form-control styleTime"
                                                                   placeholder="<spring:message code="lbl.received.time" />"/>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-12 col-lg-12">
                                                    <label class="text-left txt-color-red font-md">
                                                        ${inadecuada}
                                                    </label>
                                                </section>
                                            </div>
                                        </fieldset>
                                        <footer>
                                            <input id="idTomaMx" type="hidden" value="${tomaMx.idTomaMx}"/>
                                            <button type="submit" id="receipt-orders" class="btn btn-success btn-lg pull-right header-btn"><i class="fa fa-check"></i> <spring:message code="act.receipt" /></button>
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

    <!-- BEGIN PAGE LEVEL PLUGINS -->
    <!-- datatable -->
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
    <!-- JQUERY VALIDATE -->
    <spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
    <script src="${jqueryValidate}"></script>
    <spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
        <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
    <script src="${jQValidationLoc}"></script>
    <!-- jQuery Select2 Input -->
    <spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
    <script src="${selectPlugin}"></script>
    <!-- jQuery Select2 Locale -->
    <spring:url value="/resources/js/plugin/select2/select2_locale_{language}.js" var="selectPluginLocale">
        <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
    <script src="${selectPluginLocale}"></script>
    <!-- JQUERY BLOCK UI -->
    <spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
    <script src="${jqueryBlockUi}"></script>
    <!-- bootstrap datepicker -->
    <spring:url value="/resources/js/plugin/bootstrap-datepicker/bootstrap-datepicker.js" var="datepickerPlugin" />
    <script src="${datepickerPlugin}"></script>
    <spring:url value="/resources/js/plugin/bootstrap-datepicker/locales/bootstrap-datepicker.{languagedt}.js" var="datePickerLoc">
        <spring:param name="languagedt" value="${pageContext.request.locale.language}" /></spring:url>
    <script src="${datePickerLoc}"></script>
    <!-- END PAGE LEVEL PLUGINS -->
    <!-- BEGIN PAGE LEVEL SCRIPTS -->
    <spring:url value="/resources/scripts/recepcionMx/recepcionar-orders.js" var="receiptOrders" />
    <script src="${receiptOrders}"></script>
    <spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
    <script src="${handleDatePickers}"></script>
    <!-- BEGIN PAGE LEVEL SCRIPTS -->
    <spring:url value="/resources/scripts/utilidades/seleccionUnidad.js" var="selecUnidad" />
    <script src="${selecUnidad}"></script>
    <!-- bootstrap datetimepicker -->
    <spring:url value="/resources/js/plugin/bootstrap-datetimepicker-4/moment-with-locales.js" var="moment" />
    <script src="${moment}"></script>
    <spring:url value="/resources/js/plugin/bootstrap-datetimepicker-4/bootstrap-datetimepicker.js" var="datetimepicker" />
    <script src="${datetimepicker}"></script>
    <!-- JQUERY INPUT MASK -->
    <spring:url value="/resources/js/plugin/jquery-inputmask/jquery.inputmask.bundle.min.js" var="jqueryInputMask" />
    <script src="${jqueryInputMask}"></script>
    <spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
    <script src="${handleInputMask}"></script>
    <spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
    <script src="${handleDatePickers}"></script>

    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="ordersUrl" value="/recepcionMx/searchOrders"/>
    <c:url var="unidadesURL" value="/api/v1/unidadesPrimariasHospSilais"/>
    <c:url var="sAddReceiptUrl" value="/recepcionMx/agregarRecepcion"/>
    <c:url var="sSearchReceiptUrl" value="/recepcionMx/init"/>
    <spring:url var="sPrintUrl" value="/print/barcode"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {sPersonUrl: "${sPersonUrl}",
                sOrdersUrl : "${ordersUrl}",
                sUnidadesUrl : "${unidadesURL}",
                blockMess: "${blockMess}",
                sAddReceiptUrl: "${sAddReceiptUrl}",
                sSearchReceiptUrl : "${sSearchReceiptUrl}",
                language : "${pageContext.request.locale.language}",
                sPrintUrl : "${sPrintUrl}"
            };
			ReceiptOrders.init(parametros);
            handleDatePickers("${pageContext.request.locale.language}");
	    	$("li.recepcion").addClass("open");
            $("li.check-in").addClass("open");
            $("li.receipt").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.receipt").parents("ul").slideDown(200);
	    	}
            $('#dvCausa').hide();
            $('#horaRec').datetimepicker({
                format: 'LT'

            });

        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>