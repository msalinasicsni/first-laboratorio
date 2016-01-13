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
        .well {
            margin: 10px 5px 5px 5px;
            padding: 10px 22px;
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
                                        <c:if test="${not empty recepcionMx.tomaMx.idNotificacion.persona}">
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
                                        </c:if>
                                        <c:if test="${not empty recepcionMx.tomaMx.idNotificacion.solicitante}">
                                        <div class="row">
                                            <section class="col col-sm-6 col-md-6 col-lg-6">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.applicant"/>
                                                </label>
                                                <div class="">
                                                    <label class="input">
                                                        <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" id="nombre" name="nombre" disabled value="${recepcionMx.tomaMx.idNotificacion.solicitante.nombre}" placeholder=" <spring:message code="lbl.applicant.name" />">
                                                        <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.applicant.name"/>
                                                        </b>
                                                    </label>
                                                </div>
                                            </section>
                                            <section class="col col-sm-6 col-md-6 col-lg-6">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.contact.name"/>
                                                </label>
                                                <div class="">
                                                    <label class="input">
                                                        <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" name="contacto" id="contacto" disabled value="${recepcionMx.tomaMx.idNotificacion.solicitante.nombreContacto}" placeholder=" <spring:message code="lbl.contact.name" />" />
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.contact.name"/>
                                                        </b>
                                                    </label>
                                                </div>
                                            </section>
                                        </div>
                                        </c:if>
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
                                                        <spring:message code="lbl.requests" />
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
                                        <c:if test="${not empty datosList}">
                                            <div>
                                                <header>
                                                    <label class="text-left txt-color-blue" style="font-weight: bold">
                                                        <spring:message code="lbl.data" />
                                                    </label>
                                                </header>
                                                <br/>
                                                <br/>
                                                <div class="widget-body no-padding">
                                                    <table id="datosrecepcion_list" class="table table-striped table-bordered table-hover" width="100%">
                                                        <thead>
                                                            <tr>
                                                                <c:forEach items="${datosList}" var="record">
                                                                    <th><c:out value="${record.datoSolicitud.nombre}" /></th>
                                                                </c:forEach>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <tr>
                                                                <c:forEach items="${datosList}" var="record">
                                                                    <td>${record.valor}</td>
                                                                </c:forEach>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </c:if>
                                            <div>
                                                <header>
                                                    <label class="text-left txt-color-blue" style="font-weight: bold">
                                                        <spring:message code="lbl.header.receipt.lab" />
                                                    </label>
                                                </header>
                                                <br/>
                                                <br/>
                                                <div class="widget-body no-padding">
                                                    <table id="examenes_list" class="table table-striped table-bordered table-hover" width="100%">
                                                        <thead>
                                                        <tr>
                                                            <th data-class="expand"><spring:message code="lbl.receipt.test"/></th>
                                                            <th data-hide="phone"><spring:message code="lbl.receipt.pcr.area"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.type"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.desc.request"/></th>
                                                            <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.DateTime"/></th>
                                                            <th data-hide="phone"><spring:message code="lbl.cc"/></th>
                                                            <th data-hide="phone"><spring:message code="lbl.transfer.external"/></th>
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
                                        <div class="row">
                                            <div class="col col-sm-12 col-md-12 col-lg-12">
                                                <div class="well well-lg text-danger text-left" id="divReglas">

                                                </div>
                                            </div>
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
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.sample.quality" /> </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                       <select id="codCalidadMx" name="codCalidadMx"
                                                                        class="select2">
                                                        <option value=""><spring:message code="lbl.select" />...</option>
                                                        <c:forEach items="${calidadMx}" var="calidadMx">
                                                            <option value="${calidadMx.codigo}">${calidadMx.valor}</option>
                                                        </c:forEach>
                                                    </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.sample.condition" /> </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                        <select id="condicionMx" name="condicionMx"
                                                                class="select2">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${condicionesMx}" var="condicion">
                                                                <option value="${condicion.codigo}">${condicion.valor}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <div class="col col-sm-12 col-md-6 col-lg-6" id="dvCausa">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.cause.rejection" /> </label>
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
                <div class="alert alert-info">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="lbl.receipt.widgettitle.modal.test" />
                    </h4>
                </div>
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
                <button type="submit" class="btn btn-success" id="btnAgregarExamen">
                    <i class="fa fa-save"></i> <spring:message code="act.save" />
                </button>
                <button type="button" class="btn btn-danger" data-dismiss="modal">
                    <i class="fa fa-times"></i> <spring:message code="act.end" />
                </button>

            </footer>

            </form>
            </div>
            </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->

            <!-- Modal -->
            <div class="modal fade" id="modalOverride" aria-hidden="true" data-backdrop="static">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <div class="alert alert-info">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                    &times;
                                </button>
                                <h4 class="modal-title">
                                    <i class="fa-fw fa fa-times"></i>
                                    <spring:message code="lbl.override" /> <spring:message code="lbl.test2" />
                                </h4>
                            </div>
                        </div>
                        <div class="modal-body"> <!--  no-padding -->
                            <form id="override-ex-form" class="smart-form" novalidate="novalidate">
                                <input id="idOrdenExamen" type="hidden" value=""/>
                                <fieldset>
                                    <div class="row">
                                        <section class="col col-sm-12 col-md-12 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.annulment.cause" /> </label>
                                            <div class="">
                                                <label class="textarea">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <textarea class="form-control" rows="3" name="causaAnulacionEx" id="causaAnulacionEx"
                                                              placeholder="<spring:message code="lbl.annulment.cause" />"></textarea>
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.annulment.cause"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                    </div>
                                </fieldset>
                                <footer>
                                    <button type="submit" class="btn btn-success">
                                        <i class="fa fa-save"></i> <spring:message code="act.ok" />
                                    </button>
                                    <button type="button" class="btn btn-danger" data-dismiss="modal">
                                        <i class="fa fa-times"></i> <spring:message code="act.cancel" />
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
    <spring:url value="/resources/scripts/utilidades/unicodeEscaper.js" var="unicodeEscaper" />
    <script src="${unicodeEscaper}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:set var="noRules"><spring:message code="msg.no.rules"/></c:set>

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
    <c:url var="sReglasExamenesURL" value="/administracion/examenes/obtenerReglasExamenes"/>

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
                sExamenesEstURL : "${sExamenesEstURL}",
                sReglasExamenesURL : "${sReglasExamenesURL}",
                noRules : "${noRules}"
            };
			ReceiptOrders.init(parametros);
	    	$("li.laboratorio").addClass("open");
	    	$("li.receiptLab").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.receiptLab").parents("ul").slideDown(200);
	    	}
            $('#condicionMx').change();

        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>