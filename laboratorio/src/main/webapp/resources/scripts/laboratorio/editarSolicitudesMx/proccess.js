/**
 * Created by FIRSTICT on 7/26/2017.
 */
var EditarMxLab = function () {
    var bloquearUI = function (mensaje) {
        var loc = window.location;
        var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
        var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif>' + mensaje;
        $.blockUI({ message: mess,
            css: {
                border: 'none',
                padding: '15px',
                backgroundColor: '#000',
                '-webkit-border-radius': '10px',
                '-moz-border-radius': '10px',
                opacity: .5,
                color: '#fff'
            },
            baseZ: 1051 // para que se muestre bien en los modales
        });
    };

    var desbloquearUI = function () {
        setTimeout($.unblockUI, 500);
    };
    return {
        //main function to initiate the module
        init: function (parametros) {

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();

            var table2 = $('#examenes_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "paging": false,
                "ordering": false,
                "searching": false,
                "lengthChange": false,
                "columns": [
                    null, null, null, null, null, null, null,null,
                    {
                        "className": 'cancelar',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#examenes_list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            var table3 = $('#solicitudes_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "paging": false,
                "ordering": false,
                "searching": false,
                "lengthChange": false,
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#solicitudes_list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            <!-- formulario para agregar solicitud -->
            $('#addDx-form').validate({
                // Rules for form validation
                rules: {
                    codDXNuevo: {required: true},
                    codEstudioNuevo: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarSolicitud();
                }
            });

            <!-- formulario para agregar examen -->
            $('#AgregarExamen-form').validate({
                // Rules for form validation
                rules: {
                    codDX: {required: true},
                    codExamen: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarExamen();
                }
            });

            <!-- formulario para anular examen -->
            $('#override-sol-form').validate({
                // Rules for form validation
                rules: {
                    causaAnulacion: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    anularSolicitud($("#idSolicitud").val());
                }
            });

            <!-- formulario para anular examen -->
            $('#override-ex-form').validate({
                // Rules for form validation
                rules: {
                    causaAnulacionEx: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    anularExamen($("#idOrdenExamen").val());
                }
            });

            function showModalOverrideRequest() {
                $("#causaAnulacion").val('');
                $("#modalOverrideSoli").modal({
                    show: true
                });
            }

            function hideModalOverrideRequest() {
                $('#modalOverrideSoli').modal('hide');
            }

            function showModalOverrideTest() {
                $("#causaAnulacionEx").val('');
                $("#modalOverride").modal({
                    show: true
                });
            }

            function hideModalOverrideTest() {
                $('#modalOverride').modal('hide');
            }

            $("#btnAddDx").click(function () {
                /*if ($("#esEstudio").val() == 'true') {
                    getEstudios($("#idTipoMx").val(), $("#codTipoNoti").val(),true);
                } else {
                    getDiagnosticos($("#idTipoMx").val(), $("#codTipoNoti").val(),true);
                }*/
                $("#modalSolicitudes").modal({
                    show: true
                });
            });

            $("#btnAddTest").click(function () {
                if ($("#esEstudio").val() == 'true') {
                    getEstudios($("#idTipoMx").val(), $("#codTipoNoti").val(),false);
                } else {
                    getDiagnosticos($("#idTipoMx").val(), $("#codTipoNoti").val(),false);
                }
                $("#myModal").modal({
                    show: true
                });
            });

            <!-- Al seleccionar diagnóstico-->
            $('#codDX').change(function () {
                if ($(this).val().length > 0) {
                    getExamenesDeDx($(this).val());
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codExamen').html(html);
                }
                $('#codExamen').val('').change();
            });

            <!-- Al seleccionar estudio-->
            $('#codEstudio').change(function () {
                if ($(this).val().length > 0) {
                    var elementosEstudio = $(this).val().split("-");
                    var idSolicitud = elementosEstudio[0];
                    if (elementosEstudio[1] === "R"){
                        getExamenesDeDx(idSolicitud);
                    }else{
                        getExamenesDeEstudio(idSolicitud);
                    }
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codExamen').html(html);
                }
                $('#codExamen').val('').change();
            });

            function getExamenesDeDx(idDx) {
                $.getJSON(parametros.sExamenesURL, {
                    idDx: idDx,
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idExamen + '">'
                            + data[i].nombre
                            + '</option>';
                        html += '</option>';
                    }
                    $('#codExamen').html(html);
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }
            function getExamenesDeEstudio(idEstudio) {
                $.getJSON(parametros.sExamenesEstURL, {
                    idEstudio: idEstudio,
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idExamen + '">'
                            + data[i].nombre
                            + '</option>';
                        html += '</option>';
                    }
                    $('#codExamen').html(html);
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }

            function getRequest() {
                if (parametros.sGetSolicitudesUrl!=null) {
                    $.getJSON(parametros.sGetSolicitudesUrl, {
                        idTomaMx: $("#idTomaMx").val(),
                        ajax: 'true'
                    }, function (response) {
                        table3.fnClearTable();
                        var len = Object.keys(response).length;
                        var idExamenes = "";
                        for (var i = 0; i < len; i++) {
                            if (i==0) {
                                idExamenes = response[i].idSolicitud;
                            } else {
                                idExamenes = idExamenes +','+response[i].idSolicitud;
                            }
                            var aprobada;
                            if (response[i].aprobada==='true'){
                                aprobada = $("#lbl_yes").val();
                            }else{
                                aprobada = $("#lbl_no").val();
                            }
                            table3.fnAddData(
                                [response[i].tipo,response[i].nombre, response[i].fechaSolicitud, response[i].nombreAreaPrc, response[i].cc, response[i].resultados, aprobada,
                                        '<a data-toggle="modal" title="Cancelar solicitud" class="btn btn-danger btn-xs anularSolicitud" data-id=' + response[i].idSolicitud +'*'+response[i].aprobada+'><i class="fa fa-times"></i></a>']);

                        }
                        $(".anularSolicitud").on("click", function () {
                            var datos = $(this).data('id').split("*");
                            if (datos[1]==='true'){
                                var opcSi = $("#lbl_yes").val();
                                var opcNo = $("#lbl_no").val();
                                $.SmartMessageBox({
                                    title: $("#msg_confirm_title").val(),
                                    content: $("#msg_confirm_content").val(),
                                    buttons: '[' + opcSi + '][' + opcNo + ']'
                                }, function (ButtonPressed) {
                                    if (ButtonPressed === opcSi) {
                                        $("#idSolicitud").val(datos[0]);
                                        showModalOverrideRequest();
                                    }
                                    if (ButtonPressed === opcNo) {
                                        $.smallBox({
                                            title: $("#msg_override_cancel").val(),
                                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                            color: "#3276B1",
                                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                            timeout: 4000
                                        });
                                    }

                                });
                            }else {
                                $("#idSolicitud").val(datos[0]);
                                showModalOverrideRequest();
                            }
                        });

                        //al paginar se define nuevamente la función de cargar el detalle
                        $(".dataTables_paginate").on('click', function () {
                            $(".anularSolicitud").on('click', function () {
                                $("#idSolicitud").val($(this).data('id'));
                                showModalOverrideRequest();
                            });
                        });
                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                }
            }

            /***************REGLAS EXAMENES***********************/
            function getRulesTest(idExamenes) {
                $.getJSON(parametros.sReglasExamenesURL, {
                    idExamenes: idExamenes,
                    ajax: 'true'
                }, function (response) {
                    var len = Object.keys(response).length;
                    var idExamenActual;
                    var htmlDivReglas = '';
                    for (var i = 0; i < len; i++) {
                        if (idExamenActual==response[i].examen.idExamen) {
                            htmlDivReglas = htmlDivReglas + '<br/>';
                            htmlDivReglas = htmlDivReglas + '<i class="fa fa-minus icon-red"></i> ';
                            htmlDivReglas = htmlDivReglas + response[i].descripcion;
                        } else {
                            if (i>0) {
                                htmlDivReglas = htmlDivReglas + '<br/><br/>';
                            }
                            htmlDivReglas = htmlDivReglas + '<h5>'+response[i].examen.nombre+'</h5>';
                            htmlDivReglas = htmlDivReglas + '<i class="fa fa-minus icon-red"></i> ';
                            htmlDivReglas = htmlDivReglas + response[i].descripcion;
                        }
                        idExamenActual = response[i].examen.idExamen;
                    }
                    if (htmlDivReglas=='')
                        htmlDivReglas = '<h3>'+parametros.noRules+'</h3>';

                    $("#divReglas").html(htmlDivReglas);
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function getOrdersReview() {
                if (parametros.sgetOrdenesExamenUrl!=null) {
                    $.getJSON(parametros.sgetOrdenesExamenUrl, {
                        idTomaMx: $("#idTomaMx").val(),
                        ajax: 'true'
                    }, function (response) {
                        table2.fnClearTable();
                        var len = Object.keys(response).length;
                        var idExamenes = "";
                        for (var i = 0; i < len; i++) {
                            if (i==0) {
                                idExamenes = response[i].idExamen;
                            } else {
                                idExamenes = idExamenes +','+response[i].idExamen;
                            }
                            table2.fnAddData(
                                [response[i].nombreExamen, response[i].nombreAreaPrc, response[i].tipo, response[i].nombreSolic, response[i].fechaSolicitud, response[i].cc, response[i].externo,response[i].resultado,
                                        '<a data-toggle="modal" title="Cancelar Examen" class="btn btn-danger btn-xs anularExamen" data-id=' + response[i].idOrdenExamen + '><i class="fa fa-times"></i></a>']);

                        }
                        $(".anularExamen").on("click", function () {
                            //anularExamen($(this).data('id'));
                            $("#idOrdenExamen").val($(this).data('id'));
                            showModalOverrideTest();
                        });

                        //al paginar se define nuevamente la función de cargar el detalle
                        $(".dataTables_paginate").on('click', function () {
                            $(".anularExamen").on('click', function () {
                                //anularExamen($(this).data('id'));
                                $("#idOrdenExamen").val($(this).data('id'));
                                showModalOverrideTest();
                            });
                        });

                        getRulesTest(idExamenes);
                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                }
            }

            <!-- cargar dx -->
            function getDiagnosticos(idTipoMx, codTipoNoti, esSolicitud) {
                if (esSolicitud) {
                    $.getJSON(parametros.sDxURL, {
                        codMx: idTipoMx, tipoNoti: codTipoNoti, idTomaMx: "",
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].diagnostico.idDiagnostico + '">'
                                + data[i].diagnostico.nombre
                                + '</option>';
                        }
                            $('#codDXNuevo').html(html);
                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                }else {
                    $.getJSON(parametros.sDxURL, {
                        codMx: idTipoMx, tipoNoti: codTipoNoti, idTomaMx: $("#idTomaMx").val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].diagnostico.idDiagnostico + '">'
                                + data[i].diagnostico.nombre
                                + '</option>';
                        }
                            $('#codDX').html(html);

                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                }
            }

            <!-- cargar estudios y diagnÃ³sticos permitidos para la mx -->
            function getEstudios(idTipoMx, codTipoNoti, esSolicitud) {
                $.getJSON(parametros.sDxEstURL, {
                    codMx: idTipoMx, tipoNoti: codTipoNoti, idTomaMx: $("#idTomaMx").val(),
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = Object.keys(data).length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].id + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    if (!esSolicitud) {
                        $('#codEstudio').html(html);
                        $('#codEstudio').val('').change();
                    } else {
                        $('#codEstudioNuevo').html(html);
                        $('#codEstudioNuevo').val('').change();
                    }
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }

            function guardarSolicitud() {
                var nuevaSolicitudObj = {};
                nuevaSolicitudObj['idTomaMx'] = $("#idTomaMx").val();
                if ($("#esEstudio").val() == 'true') { //cuando es estudio se puede agregar rutina, no solo estudio
                    var estudioNuevo = $('#codEstudioNuevo').find('option:selected').val();
                    var elementosEstudio = estudioNuevo.split("-");
                    if (elementosEstudio[1] === "R"){ //es rutina
                        nuevaSolicitudObj['idDiagnostico'] =  elementosEstudio[0];
                        nuevaSolicitudObj['idEstudio'] = null;
                        nuevaSolicitudObj['esEstudio'] = 'false';
                    }else { //es estudio
                        nuevaSolicitudObj['idDiagnostico'] =  null;
                        nuevaSolicitudObj['idEstudio'] = elementosEstudio[0];
                        nuevaSolicitudObj['esEstudio'] = $('#esEstudio').val();
                    }
                }else{
                    nuevaSolicitudObj['idDiagnostico'] = $('#codDXNuevo').find('option:selected').val();
                    nuevaSolicitudObj['idEstudio'] = $('#codEstudioNuevo').find('option:selected').val();
                    nuevaSolicitudObj['esEstudio'] = $('#esEstudio').val();
                }

                nuevaSolicitudObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sAgregarSolicitudUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(nuevaSolicitudObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: unicodeEscape(data.mensaje),
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                getRequest();
                                var msg = $("#msg_request_added").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function guardarExamen() {
                var ordenExamenObj = {};
                if ($("#esEstudio").val() == 'true') {
                    var estudio = $('#codEstudio').find('option:selected').val();
                    var elementosEstudio = estudio.split("-");
                    if (elementosEstudio[1] === "R"){
                        ordenExamenObj['idDiagnostico'] =  elementosEstudio[0];
                        ordenExamenObj['idEstudio'] = null;
                        ordenExamenObj['esEstudio'] = 'false';//aunque la mx es originalmente para estudio, indicar que la nueva solicitud no es estudio
                    }else {
                        ordenExamenObj['idDiagnostico'] =  null;
                        ordenExamenObj['idEstudio'] = elementosEstudio[0];
                        ordenExamenObj['esEstudio'] = $('#esEstudio').val();
                    }
                }else{
                    ordenExamenObj['idDiagnostico'] = $('#codDX').find('option:selected').val();
                    ordenExamenObj['idEstudio'] = $('#codEstudio').find('option:selected').val();
                    ordenExamenObj['esEstudio'] = $('#esEstudio').val();
                }
                ordenExamenObj['idTomaMx'] = $("#idTomaMx").val();
                ordenExamenObj['idExamen'] = $('#codExamen').find('option:selected').val();
                ordenExamenObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sAgregarOrdenExamenUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(ordenExamenObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: unicodeEscape(data.mensaje),
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                getOrdersReview();
                                var msg = $("#msg_review_added").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function anularSolicitud(idSolicitud) {
                var anulacionObj = {};
                anulacionObj['idSolicitud'] = idSolicitud;
                anulacionObj['causaAnulacion'] = $("#causaAnulacion").val();
                anulacionObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sAnularSolicitudUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                getRequest();
                                getOrdersReview();
                                hideModalOverrideRequest();
                                var msg = $("#msg_request_cancel").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function anularExamen(idOrdenExamen) {
                var anulacionObj = {};
                anulacionObj['idOrdenExamen'] = idOrdenExamen;
                anulacionObj['causaAnulacion'] = $("#causaAnulacionEx").val();
                anulacionObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sAnularExamenUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                getOrdersReview();
                                hideModalOverrideTest();
                                var msg = $("#msg_review_cancel").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            getRequest();
            getOrdersReview();
        }
    };

}();
