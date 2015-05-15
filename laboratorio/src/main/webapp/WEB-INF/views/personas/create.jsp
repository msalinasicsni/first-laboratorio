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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/personas/search" htmlEscape="true "/>"><spring:message code="menu.persons" /></a></li>
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
							<spring:message code="persons" />
						<span> <i class="fa fa-angle-right"></i>  
							<spring:message code="person.create" />
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
								<h2><spring:message code="lbl.person" /> </h2>
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
                                <input id="msg_person_added" type="hidden" value="<spring:message code="msg.person.successfully.added"/>"/>
                                <input id="msg_person_updated" type="hidden" value="<spring:message code="msg.person.successfully.updated"/>"/>
                                <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                                <input id="smallBox_content4s" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                                <input id="idPersona" type="hidden" value="${persona.personaId}"/>
									<form id="create-form" class="smart-form" autocomplete="off">
										<fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                        <spring:message code="person.name1"/>
                                                    </label>
                                                    <div class="">
                                                            <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" id="primerNombre" name="primerNombre" value="${persona.primerNombre}" placeholder=" <spring:message code="person.name1" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nombre1"/>
                                                            </b>
                                                        </label>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name2"/>
                                                    </label>
                                                    <div class="">
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" name="segundoNombre" id="segundoNombre" value="${persona.segundoNombre}" placeholder=" <spring:message code="person.name2" />" />
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nombre2"/>
                                                        </b>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                        <spring:message code="person.lastname1"/>
                                                    </label>
                                                    <div class="">
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" name="primerApellido" id="primerApellido" value="${persona.primerApellido}" placeholder=" <spring:message code="person.lastname1" />" />
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido1"/>
                                                        </b>
                                                        </label>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.lastname2"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" name="segundoApellido" id="segundoApellido" value="${persona.segundoApellido}" placeholder=" <spring:message code="person.lastname2" />"/>
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido2"/>
                                                        </b>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                        <spring:message code="person.fecnac" />
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <input class="form-control date-picker" data-date-end-date="+0d"
                                                               type="text" name="fechaNacimiento" id="fechaNacimiento" value="<fmt:formatDate value="${persona.fechaNacimiento}" pattern="dd/MM/yyyy" />"
                                                        placeholder=" <spring:message code="act.enter" /> <spring:message code="person.fecnac" />">
                                                        <!--<span class="input-group-addon"><i class="fa fa-calendar fa-fw"></i></span>-->
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <spring:message code="lbl.age" />
                                                    </label>
                                                    <div class="">
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <label class="input">
                                                        <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" name="edad" id="edad" readonly
                                                                   placeholder=" <spring:message code="lbl.age" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.edad"/>
                                                            </b>
                                                        </label>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-numeric-asc fa-fw"></i></span>-->
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="person.sexo" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codSexo" name="codSexo">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${sexo}" var="sexoPer">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.sexoCodigo, sexoPer.codigo)}">
                                                                        <option selected value="${sexoPer.codigo}">${sexoPer.valor}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${sexoPer.codigo}">${sexoPer.valor}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.estadocivil" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codEstadoCivil" name="codEstadoCivil">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${estadoCivil}" var="estadoCivil">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.estadoCivilCodigo, estadoCivil.codigo)}">
                                                                        <option selected value="${estadoCivil.codigo}">${estadoCivil.valor}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${estadoCivil.codigo}">${estadoCivil.valor}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.tipo.identificacion" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codTipIdent" name="codTipIdent">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${tipoIdentificacion}" var="tipoIdentificacion">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.identCodigo, tipoIdentificacion.codigo)}">
                                                                        <option selected value="${tipoIdentificacion.codigo}">${tipoIdentificacion.valor}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${tipoIdentificacion.codigo}">${tipoIdentificacion.valor}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <spring:message code="person.num.identificacion" />
                                                    </label>
                                                    <div class="">
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" name="numIdentificacion" id="numIdentificacion" value="${persona.identNumero}"
                                                                   placeholder=" <spring:message code="person.num.identificacion" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.numIdent"/>
                                                            </b>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.etnia" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codEtnia" name="codEtnia" >
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${etnia}" var="etnia">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.etniaCodigo, etnia.codigo)}">
                                                                        <option selected value="${etnia.codigo}">${etnia.valor}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${etnia.codigo}">${etnia.valor}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.escolaridad" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codEscolaridad" name="codEscolaridad">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${escolaridad}" var="escolaridad">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.escolaridadCodigo, escolaridad.codigo)}">
                                                                        <option selected value="${escolaridad.codigo}">${escolaridad.valor}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${escolaridad.codigo}">${escolaridad.valor}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                        </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.ocupacion" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codOcupacion" name="codOcupacion">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${ocupacion}" var="ocupacion">
                                                                <c:choose>
                                                                    <c:when test="${persona.ocupacionCodigo == ocupacion.codigo}">
                                                                        <option selected value="${ocupacion.codigo}">${ocupacion.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${ocupacion.codigo}">${ocupacion.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.tiposeg" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codTipoAseg" name="codTipoAseg" >
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${tipoAsegurado}" var="tipoAsegurado">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.tipoAsegCodigo, tipoAsegurado.codigo)}">
                                                                        <option selected value="${tipoAsegurado.codigo}">${tipoAsegurado.valor}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${tipoAsegurado.codigo}">${tipoAsegurado.valor}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md hidden-xs">
                                                        <spring:message code="person.numeroseguro" />
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                        <!--<span class="input-group-addon"><i class="fa fa-pencil fa-fw"></i></span>-->
                                                        <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" name="numAsegurado" id="numAsegurado" value="${persona.aseguradoNumero}"
                                                                   placeholder=" <spring:message code="person.numeroseguro" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.numAsegurado"/>
                                                            </b>
                                                        </label>
                                                        <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.pais.nacimiento" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codPaisNacimi" name="codPaisNacimi">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${paises}" var="paises">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.paisNacCodigoAlfados, paises.codigoAlfados)}">
                                                                        <option selected value="${paises.codigoAlfados}">${paises.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${paises.codigoAlfados}">${paises.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.depa.nacimiento" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <input type="hidden" id="codDepaNacimiEd" value="${depaNac}"/>
                                                        <select class="select2" id="codDepaNacimi" name="codDepaNacimi" >
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${departReside}" var="departNacimi">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(depaNac, departNacimi.codigoNacional)}">
                                                                        <option selected value="${departNacimi.codigoNacional}">${departNacimi.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${departNacimi.codigoNacional}">${departNacimi.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.municipio.nacimiento" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <input type="hidden" id="codMuniNacimiEd" value="${persona.muniNacCodigoNac}"/>
                                                        <select class="select2" id="codMuniNacimi" name="codMuniNacimi">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${muniNac}" var="muniNac">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.muniNacCodigoNac, muniNac.codigoNacional)}">
                                                                        <option selected value="${muniNac.codigoNacional}">${muniNac.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${muniNac.codigoNacional}">${muniNac.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.person.depart.proc" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codDepaReside" name="codDepaReside">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${departReside}" var="departReside">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(depaResi, departReside.codigoNacional)}">
                                                                        <option selected value="${departReside.codigoNacional}">${departReside.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${departReside.codigoNacional}">${departReside.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.mun.res" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codMuniReside" name="codMuniReside" >
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${muniResi}" var="muniResi">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.muniResiCodigoNac, muniResi.codigoNacional)}">
                                                                        <option selected value="${muniResi.codigoNacional}">${muniResi.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${muniResi.codigoNacional}">${muniResi.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-6">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.com.res" />
                                                    </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon">
                                                            <i class="fa fa-location-arrow fa-fw"></i>
                                                        </span>
                                                        <select class="select2" id="codComuniReside" name="codComuniReside">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <c:forEach items="${comunidadesesRes}" var="comunidadesesRes">
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(persona.comuResiCodigo, comunidadesesRes.codigo)}">
                                                                        <option selected value="${comunidadesesRes.codigo}">${comunidadesesRes.nombre}</option>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <option value="${comunidadesesRes.codigo}">${comunidadesesRes.nombre}</option>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </section>
                                            </div>
                                        <div class="row">
                                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                                <label class="text-left txt-color-blue font-md hidden-xs">
                                                    <spring:message code="person.direccion" />
                                                </label>
                                                <div class="">
                                                    <!--<span class="input-group-addon"><i class="fa fa-map-marker fa-fw"></i></span>-->
                                                        <!--<input class="form-control" type="text" name="direccionReside" id="direccionReside"
                                                               placeholder=" <spring:message code="person.direccion" />"/>-->
                                                    <label class="textarea">
                                                    <i class="icon-prepend fa fa-map-marker fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <textarea class="form-control" rows="3" name="direccionReside" id="direccionReside"
                                                              placeholder=" <spring:message code="person.direccion" />">${persona.direccionResi}</textarea>
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.direccion"/>
                                                        </b>
                                                    </label>
                                                    <!--<span class="input-group-addon"><i class="fa fa-sort-alpha-asc fa-fw"></i></span>-->
                                                </div>
                                            </section>
                                            <section class="col col-sm-6 col-md-3 col-lg-3">
                                                <label class="text-left txt-color-blue font-md hidden-xs">
                                                    <spring:message code="person.telefono" />
                                                </label>
                                                <div class="">
                                                    <!--<span class="input-group-addon"><i class="fa fa-phone fa-fw"></i></span>-->
                                                    <label class="input">
                                                    <i class="icon-prepend fa fa-phone fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                        <input class="form-control" type="text" name="telefReside" id="telefReside" value="${persona.telefonoResi}"
                                                               placeholder=" <spring:message code="person.telefono" />"/>
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.telefono"/>
                                                        </b>
                                                    </label>
                                                    <!--<span class="input-group-addon"><i class="fa fa-sort-numeric-asc fa-fw"></i></span>-->
                                                </div>
                                            </section>
                                            <section class="col col-sm-6 col-md-3 col-lg-3">
                                                <label class="text-left txt-color-blue font-md hidden-xs">
                                                    <spring:message code="person.telefono.movil" />
                                                </label>
                                                <div class="">
                                                    <label class="input">
                                                        <i class="icon-prepend fa fa-mobile fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <!--<span class="input-group-addon"><i class="fa fa-mobile fa-fw"></i></span>-->
                                                        <input class="form-control" type="text" name="teleMovil" id="teleMovil" value="${persona.telefonoMovil}"
                                                               placeholder=" <spring:message code="person.telefono.movil" />"/>
                                                        <b class="tooltip tooltip-bottom-right"> <i
                                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.movil"/>
                                                        </b>
                                                    <!--<span class="input-group-addon"><i class="fa fa-sort-numeric-asc fa-fw"></i></span>-->
                                                    </label>
                                                </div>
                                            </section>
                                        </div>
                                        </fieldset>
										<footer>
                                            <button type="submit" id="create-person" class="btn btn-info"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>
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
    <!-- jQuery Selecte2 Input -->
    <spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
    <script src="${selectPlugin}"></script>
    <!-- bootstrap datepicker -->
    <spring:url value="/resources/js/plugin/bootstrap-datepicker/bootstrap-datepicker.js" var="datepickerPlugin" />
    <script src="${datepickerPlugin}"></script>
    <spring:url value="/resources/js/plugin/bootstrap-datepicker/locales/bootstrap-datepicker.{languagedt}.js" var="datePickerLoc">
        <spring:param name="languagedt" value="${pageContext.request.locale.language}" /></spring:url>
    <script src="${datePickerLoc}"></script>
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
	<spring:url value="/resources/scripts/personas/person-create.js" var="personCreate" />
	<script src="${personCreate}"></script>
    <spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
    <script src="${handleDatePickers}"></script>
    <spring:url value="/resources/scripts/utilidades/calcularEdad.js" var="calculateAge" />
    <script src="${calculateAge}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="agregarPersonaUrl" value="/personas/agregarActualizarPersona"/>
    <c:url var="municipiosURL" value="/api/v1/municipio"/>
    <c:url var="comunidadesURL" value="/api/v1/comunidad"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {sPersonUrl: "${sPersonUrl}",
                sAgreparPersonaUrl : "${agregarPersonaUrl}",
                sMunicipiosUrl : "${municipiosURL}",
                sComunidadesUrl : "${comunidadesURL}",
            blockMess: "${blockMess}"
            };
			CreatePerson.init(parametros);
            handleDatePickers("${pageContext.request.locale.language}");
	    	$("li.mantenimiento").addClass("open");
	    	$("li.personas").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.personas").parents("ul").slideDown(200);
	    	}
            $('#fechaNacimiento').change();
        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>