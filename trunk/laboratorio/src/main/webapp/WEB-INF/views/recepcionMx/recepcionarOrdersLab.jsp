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
        .styleButton {

            float: right;
            height: 31px;
            margin: 10px 0px 0px 5px;
            padding: 0px 22px;
            font: 300 15px/29px "Open Sans", Helvetica, Arial, sans-serif;
            cursor: pointer;
        }
        .modal .modal-dialog {
            width: 60%;
        }
        .cancelar {
            padding-left: 0;
            padding-right: 10px;
            text-align: center;
            width: 5%;
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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/recepcionMx/initLab" htmlEscape="true "/>"><spring:message code="menu.receipt.orders.lab" /></a></li>
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
						<i class="fa-fw fa fa-thumbs-up"></i>
							<spring:message code="lbl.receipt.orders.title" />
						<span> <i class="fa fa-angle-right"></i>  
							<spring:message code="lbl.receipt.orders.lab" />
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
                                    <input id="msg_receipt_added" type="hidden" value="<spring:message code="msg.receipt.successfully.added"/>"/>
                                    <input id="msg_review_cancel" type="hidden" value="<spring:message code="msg.receipt.cancel.test"/>"/>
                                    <input id="msg_review_added" type="hidden" value="<spring:message code="msg.receipt.add.test"/>"/>
                                    <input id="msg_receipt_cancel" type="hidden" value="<spring:message code="msg.receipt.cancel"/>"/>
                                    <input id="txtEsLaboratorio" type="hidden" value="true"/>
                                    <form id="receiptOrdersLab-form" class="smart-form" autocomplete="off">
                                        <fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name1"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="primerNombre" name="primerNombre" value="${recepcionMx.tomaMx.idNotificacion.persona.primerNombre}" placeholder=" <spring:message code="person.name1" />">
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
                                                            <input class="form-control" type="text" disabled name="segundoNombre" id="segundoNombre" value="${recepcionMx.tomaMx.idNotificacion.persona.segundoNombre}" placeholder=" <spring:message code="person.name2" />" />
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
                                                            <input class="form-control" type="text" disabled name="primerApellido" id="primerApellido" value="${recepcionMx.tomaMx.idNotificacion.persona.primerApellido}" placeholder=" <spring:message code="person.lastname1" />" />
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
                                                            <input class="form-control" type="text" disabled name="segundoApellido" id="segundoApellido" value="${recepcionMx.tomaMx.idNotificacion.persona.segundoApellido}" placeholder=" <spring:message code="person.lastname2" />"/>
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
                                                            <input class="form-control" type="text" disabled id="codTipoMx" name="codTipoMx" value="${recepcionMx.tomaMx.codTipoMx.nombre}" placeholder=" <spring:message code="lbl.sample.type" />">
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
                                                            <input class="form-control" type="text" disabled id="fechaHoraTomaMx" name="fechaHoraTomaMx" value="<fmt:formatDate value="${recepcionMx.tomaMx.fechaHTomaMx}" pattern="dd/MM/yyyy hh:mm:ss a" />"
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
                                                            <input class="form-control" type="text" disabled id="cantidadTubos" name="cantidadTubos" value="${recepcionMx.tomaMx.canTubos}"
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
                                                                <c:when test="${recepcionMx.tomaMx.mxSeparada==true}">
                                                                    <input type="radio" name="radio-inline" disabled checked="checked">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input type="radio" name="radio-inline" disabled>
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <i></i><spring:message code="lbl.yes"/></label>
                                                        <label class="radio state-disabled">
                                                            <c:choose>
                                                                <c:when test="${recepcionMx.tomaMx.mxSeparada==false}">
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
                                                            <input class="form-control" type="text" disabled id="codSilais" name="codSilais" value="${recepcionMx.tomaMx.idNotificacion.codSilaisAtencion.nombre}" placeholder=" <spring:message code="lbl.silais" />">
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
                                                            <input class="form-control" type="text" disabled id="codUnidadSalud" name="codUnidadSalud" value="${recepcionMx.tomaMx.idNotificacion.codUnidadAtencion.nombre}" placeholder=" <spring:message code="lbl.health.unit" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.health.unit"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.notification.type" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="TipoNoti" name="TipoNoti" value="${recepcionMx.tomaMx.idNotificacion.codTipoNotificacion.valor}" placeholder=" <spring:message code="lbl.sample.type" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sample.type"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div>
                                                <header>
                                                    <label class="text-left txt-color-blue" style="font-weight: bold">
                                                        <spring:message code="lbl.header.receipt.lab" />
                                                    </label>
                                                </header>
                                                <br/>
                                                <br/>
                                                <div class="widget-body no-padding">
                                                    <table id="dx_list" class="table table-striped table-bordered table-hover" width="100%">
                                                        <thead>
                                                        <tr>
                                                            <th data-class="expand"><spring:message code="lbl.receipt.test"/></th>
                                                            <th data-hide="phone"><spring:message code="lbl.receipt.pcr.area"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.type"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.desc.request"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.DateTime"/></th>
                                                            <th><spring:message code="act.cancel"/></th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-12 col-lg-12">
                                                    <button type="button" id="btnAddTest" class="btn btn-primary styleButton" data-toggle="modal"
                                                            data-target="myModal">
                                                        <i class="fa fa-plus icon-white"></i>
                                                        <spring:message code="act.add.test"/>
                                                    </button>
                                                </section>
                                            </div>
                                        </fieldset>
                                        <fieldset>
                                            <header>
                                                <label class="text-left txt-color-blue" style="font-weight: bold">
                                                    <spring:message code="lbl.header.receipt.orders.form" />
                                                </label>
                                            </header>
                                            <br>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-4">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.sample.quality" /> </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                        <label for="codCalidadMx">
                                                        </label><select id="codCalidadMx" name="codCalidadMx"
                                                                class="select2">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${calidadMx}" var="calidadMx">
                                                                <option value="${calidadMx.codigo}">${calidadMx.valor}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <div class="col col-sm-12 col-md-6 col-lg-8" id="dvCausa">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.cause.rejection" /> </label>
                                                    <div class="">
                                                        <label class="textarea">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <textarea class="form-control" rows="3" name="causaRechazo" id="causaRechazo"
                                                                      placeholder="<spring:message code="lbl.cause.rejection" />"></textarea>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.cause.rejection"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </div>
                                                <!--<section class="col col-5">
                                                <label class="text-left txt-color-blue font-md">
                                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>Tipo de Tubo
                                                </label>
                                                <div class="input-group">
                                                    <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                    <select id="codTipoTubo" name="codTipoTubo"
                                                            class="select2">
                                                        <option value=""><spring:message code="lbl.select" />...</option>
                                                        <c:forEach items="${tipoTubo}" var="tipoTubo">
                                                            <option value="${tipoTubo.codigo}">${tipoTubo.valor}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                </section>-->
                                            </div>
                                        </fieldset>
                                        <footer>
                                            <input id="idRecepcion" type="hidden" value="${recepcionMx.idRecepcion}"/>
                                            <input id="idTipoMx" type="hidden" value="${recepcionMx.tomaMx.codTipoMx.idTipoMx}"/>
                                            <input id="codTipoNoti" type="hidden" value="${recepcionMx.tomaMx.idNotificacion.codTipoNotificacion.codigo}"/>
                                            <input id="idTomaMx" type="hidden" value="${recepcionMx.tomaMx.idTomaMx}"/>
                                            <input id="esEstudio" type="hidden" value="${esEstudio}"/>
                                            <button type="submit" id="receipt-orders-lab" class="btn btn-success btn-lg pull-right header-btn"><i class="fa fa-check"></i> <spring:message code="act.receipt" /></button>
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
            <div class="modal fade" id="myModal" aria-hidden="true" data-backdrop="static"> <!--tabindex="-1" role="dialog" -->
            <div class="modal-dialog">
            <div class="modal-content">
            <div class="modal-header">
                <!--<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                    </button>-->
                <h4 class="modal-title">
                    <spring:message code="lbl.receipt.widgettitle.modal.test" />
                </h4>
            </div>
            <div class="modal-body"> <!--  no-padding -->
            <form id="AgregarExamen-form" class="smart-form" novalidate="novalidate">
            <fieldset>
            <div class="row">
                <c:choose>
                    <c:when test="${esEstudio}">
                        <section class="col col-sm-12 col-md-5 col-lg-5">
                            <label class="text-left txt-color-blue font-md">
                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.study.type" />
                            </label>
                            <div class="input-group">
                                <span class="input-group-addon">
                                    <i class="fa fa-location-arrow fa-fw"></i>
                                </span>
                                <select  class="select2" id="codEstudio" name="codEstudio" >
                                    <option value=""><spring:message code="lbl.select" />...</option>
                                </select>
                            </div>
                        </section>
                    </c:when>
                    <c:otherwise>
                        <section class="col col-sm-12 col-md-5 col-lg-5">
                            <label class="text-left txt-color-blue font-md">
                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.dx.type" />
                            </label>
                            <div class="input-group">
                                <span class="input-group-addon">
                                    <i class="fa fa-location-arrow fa-fw"></i>
                                </span>
                                <select  class="select2" id="codDX" name="codDX" >
                                    <option value=""><spring:message code="lbl.select" />...</option>
                                </select>
                            </div>
                        </section>
                    </c:otherwise>
                </c:choose>
                <section class="col col-sm-12 col-md-7 col-lg-7">
                    <label class="text-left txt-color-blue font-md">
                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.receipt.test" />
                    </label>
                    <div class="input-group">
	    					        <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
		    				        </span>
                        <select class="select2" id="codExamen" name="codExamen" >
                            <option value=""><spring:message code="lbl.select" />...</option>
                        </select>
                    </div>
                </section>
            </div>
            </fieldset>

            <footer>
                <button type="submit" class="btn btn-primary" id="btnAgregarExamen">
                    <spring:message code="act.save" />
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    <spring:message code="act.end" />
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
    <!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<spring:url value="/resources/scripts/recepcionMx/recepcionar-orders.js" var="receiptOrders" />
	<script src="${receiptOrders}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="ordersUrl" value="/recepcionMx/searchOrders"/>

    <c:url var="unidadesURL" value="/api/v1/unidadesPrimariasHospSilais"/>
    <c:url var="sAddReceiptUrl" value="/recepcionMx/receiptLaboratory"/>
    <c:url var="sSearchReceiptUrl" value="/recepcionMx/initLab"/>
    <c:url var="sAnularExamenUrl" value="/recepcionMx/anularExamen"/>
    <c:url var="sAgregarOrdenExamenUrl" value="/recepcionMx/agregarOrdenExamen"/>
    <c:url var="sgetOrdenesExamenUrl" value="/recepcionMx/getOrdenesExamen"/>
    <c:url var="sDxURL" value="/api/v1/getDiagnosticos"/>
    <c:url var="sExamenesURL" value="/api/v1/getExamenes"/>
    <c:url var="sEstudiosURL" value="/api/v1/getEstudios"/>
    <c:url var="sExamenesEstURL" value="/api/v1/getExamenesEstudio"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {sPersonUrl: "${sPersonUrl}",
                sOrdersUrl : "${ordersUrl}",
                sUnidadesUrl : "${unidadesURL}",
                blockMess: "${blockMess}",
                sAddReceiptUrl: "${sAddReceiptUrl}",
                sSearchReceiptUrl : "${sSearchReceiptUrl}",
                sgetOrdenesExamenUrl : "${sgetOrdenesExamenUrl}",
                sAnularExamenUrl : "${sAnularExamenUrl}",
                sDxURL : "${sDxURL}",
                sExamenesURL : "${sExamenesURL}",
                sAgregarOrdenExamenUrl : "${sAgregarOrdenExamenUrl}",
                sEstudiosURL : "${sEstudiosURL}",
                sExamenesEstURL : "${sExamenesEstURL}"
            };
			ReceiptOrders.init(parametros);
	    	$("li.laboratorio").addClass("open");
	    	$("li.receiptLab").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.receiptLab").parents("ul").slideDown(200);
	    	}
            $('#codCalidadMx').change();

        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>