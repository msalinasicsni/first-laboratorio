var ReceiptOrders = function () {
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
            var table1 = $('#orders_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#orders_result'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "oTableTools": {
                    "sSwfPath": parametros.sTableToolsPath,
                    "sRowSelect": "multi",
                    "aButtons": [
                        {"sExtends": "select_all", "sButtonText": text_selected_all},
                        {"sExtends": "select_none", "sButtonText": text_selected_none}
                    ]
                }
            });
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
                        null, null, null, null, null, null, null,
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

            var table4 = $('#datosrecepcion_list').dataTable({
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#datosrecepcion_list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            if ($("#txtEsLaboratorio").val() == 'true') {
                getRequest();
                getOrdersReview();
            }
            <!-- formulario de búsqueda de ordenes -->
            $('#searchOrders-form').validate({
                // Rules for form validation
                rules: {
                    fecFinTomaMx: {required: function () {
                        return $('#fecInicioTomaMx').val().length > 0;
                    }},
                    fecInicioTomaMx: {required: function () {
                        return $('#fecFinTomaMx').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getMxs(false)
                }
            });

            <!-- formulario de recepción general -->
            $('#receiptOrders-form').validate({
                // Rules for form validation
                rules: {
                    rdCantTubos: {required: true},
                    rdTipoMx: {required: true},
                    causaRechazo: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    if (element.attr("name") === "rdCantTubos") {
                        $("#dErrorCantTubos").fadeIn('slow');
                    } else if (element.attr("name") === "rdTipoMx") {
                        $("#dErrorTipoMx").fadeIn('slow');
                    } else {
                        error.insertAfter(element.parent());
                    }
                },
                submitHandler: function (form) {
                    $("#dErrorCantTubos").fadeOut('slow');
                    $("#dErrorTipoMx").fadeOut('slow');
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    guardarRecepcion();
                }
            });

            <!-- formulario de recepción en laboratorio -->
            $('#receiptOrdersLab-form').validate({
                // Rules for form validation
                rules: {
                    codCalidadMx: {required: true},
                    condicionMx: {required: true},
                    causaRechazo: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarRecepcionLab();
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

            function showModalOverrideRequest() {
                $("#causaAnulacion").val('');
                $("#modalOverrideSoli").modal({
                    show: true
                });
            }

            function hideModalOverrideRequest() {
                $('#modalOverrideSoli').modal('hide');
            }

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

            function showModalOverrideTest() {
                $("#causaAnulacionEx").val('');
                $("#modalOverride").modal({
                    show: true
                });
            }

            function hideModalOverrideTest() {
                $('#modalOverride').modal('hide');
            }

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



            function blockUI() {
                var loc = window.location;
                var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
                //var mess = $("#blockUI_message").val()+' <img src=' + pathName + 'resources/img/loading.gif>';
                var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif> ' + parametros.blockMess;
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
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            function getMxs(showAll) {
                var mxFiltros = {};
                if (showAll) {
                    mxFiltros['nombreApellido'] = '';
                    mxFiltros['fechaInicioTomaMx'] = '';
                    mxFiltros['fechaFinTomaMx'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';
                    mxFiltros['controlCalidad'] = '';
                } else {
                    mxFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    mxFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    mxFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    mxFiltros['codTipoSolicitud'] = $('#tipo').find('option:selected').val();
                    mxFiltros['nombreSolicitud'] = $('#nombreSoli').val();
                    mxFiltros['controlCalidad'] = $('#quality').find('option:selected').val();
                }
                blockUI();
                $.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var actionUrl = parametros.sActionUrl;
                            var idLoad;
                            if ($('#txtEsLaboratorio').val() == 'true') {
                                idLoad = dataToLoad[i].idRecepcion;
                                actionUrl = actionUrl + idLoad;
                                table1.fnAddData(
                                    [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + idLoad + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].traslado, dataToLoad[i].origen, dataToLoad[i].embarazada, dataToLoad[i].hospitalizado, dataToLoad[i].urgente, '<a href=' + actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                            } else {
                                idLoad = dataToLoad[i].idTomaMx;
                                actionUrl = actionUrl + idLoad;
                                table1.fnAddData(
                                    [dataToLoad[i].tipoMuestra + " <input type='hidden' value='" + idLoad + "'/>", dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].traslado, dataToLoad[i].origen, dataToLoad[i].embarazada, dataToLoad[i].hospitalizado, dataToLoad[i].urgente, '<a href=' + actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                            }


                        }
                    } else {
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    unBlockUI();
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }

            function getRequest() {
                if (parametros.sGetSolicitudesUrl!=null) {
                    $.getJSON(parametros.sGetSolicitudesUrl, {
                        idTomaMx: $("#idTomaMx").val(),
                        contentType: "charset=ISO-8859-1",
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
                            table3.fnAddData(
                                [response[i].tipo,response[i].nombre, response[i].fechaSolicitud, response[i].nombreAreaPrc, response[i].cc,
                                        '<a data-toggle="modal" class="btn btn-danger btn-xs anularSolicitud" data-id=' + response[i].idSolicitud +'><i class="fa fa-times"></i></a>']);

                        }
                        $(".anularSolicitud").on("click", function () {
                            $("#idSolicitud").val($(this).data('id'));
                            showModalOverrideRequest();
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

            function getOrdersReview() {
                if (parametros.sgetOrdenesExamenUrl!=null) {
                    $.getJSON(parametros.sgetOrdenesExamenUrl, {
                        idTomaMx: $("#idTomaMx").val(),
                        contentType: "charset=ISO-8859-1",
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
                                [response[i].nombreExamen, response[i].nombreAreaPrc, response[i].tipo, response[i].nombreSolic, response[i].fechaSolicitud, response[i].cc, response[i].externo,
                                        '<a data-toggle="modal" class="btn btn-danger btn-xs anularExamen" data-id=' + response[i].idOrdenExamen + '><i class="fa fa-times"></i></a>']);

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

            $("#all-orders").click(function () {
                getMxs(true);
            });

            $("#receipt-mxs").click(function () {
                var oTT = TableTools.fnGetInstance('orders_result');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_confirm_title").val(),
                        content: $("#msg_confirm_content").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            var codUnicosFormat = '';
                            bloquearUI(parametros.blockMess);
                            var idRecepciones = {};
                            //el input hidden debe estar siempre en la primera columna
                            for (var i = 0; i < len; i++) {
                                var texto = aSelectedTrs[i].firstChild.innerHTML;
                                var input = texto.substring(texto.lastIndexOf("<"), texto.length);
                                idRecepciones[i] = $(input).val();
                            }
                            var muestrasObj = {};
                            muestrasObj['strMuestras'] = idRecepciones;
                            muestrasObj['mensaje'] = '';
                            muestrasObj['cantMuestras'] = len;
                            muestrasObj['cantMxProc'] = '';
                            muestrasObj['codigosUnicosMx'] = '';
                            $.ajax(
                                {
                                    url: parametros.sCreateReceiptMassUrl,
                                    type: 'POST',
                                    dataType: 'json',
                                    data: JSON.stringify(muestrasObj),
                                    contentType: 'application/json',
                                    mimeType: 'application/json',
                                    async: false,
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
                                            var msg = $("#msg_reception_lab_success").val();
                                            msg = msg.replace(/\{0\}/, data.cantMxProc);
                                            $.smallBox({
                                                title: msg,
                                                content: $("#smallBox_content").val(),
                                                color: "#739E73",
                                                iconSmall: "fa fa-success",
                                                timeout: 4000
                                            });
                                            codUnicosFormat = reemplazar(data.codigosUnicosMx, ".", "*");
                                            getMxs(false);
                                        }
                                        desbloquearUI();
                                    },
                                    error: function (jqXHR) {
                                        desbloquearUI();
                                        validateLogin(jqXHR);
                                    }
                                });
                            imprimir(codUnicosFormat);
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_reception_cancel").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                color: "#C46A69",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                } else {
                    $.smallBox({
                        title: $("#msg_select_receipt").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                        color: "#C46A69",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }
            });

            $("#recep-orders-lab").click(function () {
                var oTT = TableTools.fnGetInstance('orders_result');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_confirm_title").val(),
                        content: $("#msg_confirm_content").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            bloquearUI(parametros.blockMess);
                            var idRecepciones = {};
                            //el input hidden debe estar siempre en la primera columna
                            for (var i = 0; i < len; i++) {
                                var texto = aSelectedTrs[i].firstChild.innerHTML;
                                var input = texto.substring(texto.lastIndexOf("<"), texto.length);
                                idRecepciones[i] = $(input).val();
                            }
                            var recepcionesObj = {};
                            recepcionesObj['strRecepciones'] = idRecepciones;
                            recepcionesObj['mensaje'] = '';
                            recepcionesObj['cantRecepciones'] = len;
                            recepcionesObj['cantRecepProc'] = '';
                            $.ajax(
                                {
                                    url: parametros.sCreateReceiptMassUrl,
                                    type: 'POST',
                                    dataType: 'json',
                                    data: JSON.stringify(recepcionesObj),
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
                                            var msg = $("#msg_reception_lab_success").val();
                                            msg = msg.replace(/\{0\}/, data.cantRecepProc);
                                            $.smallBox({
                                                title: msg,
                                                content: $("#smallBox_content").val(),
                                                color: "#739E73",
                                                iconSmall: "fa fa-success",
                                                timeout: 4000
                                            });
                                            getMxs(false);
                                        }
                                        desbloquearUI();
                                    },
                                    error: function (jqXHR) {
                                        desbloquearUI();
                                        validateLogin(jqXHR);
                                    }
                                });
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_reception_cancel").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                color: "#C46A69",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                } else {
                    $.smallBox({
                        title: $("#msg_select_receipt").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                        color: "#C46A69",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }
            });


            <!-- para guardar recepción general -->
            function guardarRecepcion() {
                bloquearUI(parametros.blockMess);
                var codUnicoFormat = '';
                var recepcionObj = {};
                recepcionObj['idRecepcion'] = '';
                recepcionObj['mensaje'] = '';
                recepcionObj['idTomaMx'] = $("#idTomaMx").val();
                recepcionObj['verificaCantTb'] = $('input[name="rdCantTubos"]:checked', '#receiptOrders-form').val();
                recepcionObj['verificaTipoMx'] = $('input[name="rdTipoMx"]:checked', '#receiptOrders-form').val();
                recepcionObj['causaRechazo'] = $('#causaRechazo').find('option:selected').val();//$('#causaRechazo').val();
                recepcionObj['codigoUnicoMx'] = '';
                recepcionObj['fechaRecibido']= $('#fechaRec').val() ;
                recepcionObj['horaRecibido']= $('#horaRec').val() ;

                $.ajax(
                    {
                        url: parametros.sAddReceiptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(recepcionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        async: false,
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
                                var msg = $("#msg_receipt_added").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                codUnicoFormat = reemplazar(data.codigoUnicoMx, ".", "*");
                                limpiarDatosRecepcion();
                                setTimeout(function () {
                                    window.location.href = parametros.sSearchReceiptUrl
                                }, 4000);
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
                imprimir(codUnicoFormat);
            }

            function imprimir(strBarCodes){
                $.getJSON(parametros.sPrintUrl, {
                    strBarCodes: strBarCodes,
                    ajax: 'true'
                }, function (data) {
                    var len = Object.keys(data).length;
                    console.log(data);
                    if (len > 0) {
                        console.log(data.respuesta.length);
                        if (data.respuesta.length>0){
                            $.smallBox({
                                title: data.respuesta,
                                content: $("#smallBox_content").val(),
                                color: "#C46A69",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }else{
                            $.smallBox({
                                title: "etiquetas impresas",
                                content: $("#smallBox_content").val(),
                                color: "#739E73",
                                iconSmall: "fa fa-success",
                                timeout: 4000
                            });
                        }

                    }
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            <!-- para guardar recepción en laboratorio -->
            function guardarRecepcionLab() {
                bloquearUI(parametros.blockMess);
                var recepcionObj = {};
                recepcionObj['mensaje'] = '';
                recepcionObj['idRecepcion'] = $("#idRecepcion").val();
                recepcionObj['calidadMx'] = $('#codCalidadMx option:selected').val();
                recepcionObj['condicionMx'] = $('#condicionMx option:selected').val();
                recepcionObj['causaRechazo'] = $('#causaRechazo').val();
                $.ajax(
                    {
                        url: parametros.sAddReceiptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(recepcionObj),
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
                                var msg = $("#msg_receipt_added").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                limpiarDatosRecepcion();
                                setTimeout(function () {
                                    window.location.href = parametros.sSearchReceiptUrl
                                }, 2000);
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });

            }

            function limpiarDatosRecepcion() {
                //$("#txtNombreTransporta").val('');
                //$("#txtTemperatura").val('');
                //$("#codLaboratorioProce").val('').change();
            }

            function anularExamen(idOrdenExamen) {
                var anulacionObj = {};
                anulacionObj['idOrdenExamen'] = idOrdenExamen;
                anulacionObj['causaAnulacion'] = $("#causaAnulacionEx").val();
                anulacionObj['mensaje'] = '';
                blockUI();
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
                            unBlockUI();
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
                blockUI();
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
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function guardarSolicitud() {
                var nuevaSolicitudObj = {};
                nuevaSolicitudObj['idTomaMx'] = $("#idTomaMx").val();
                nuevaSolicitudObj['idDiagnostico'] = $('#codDXNuevo').find('option:selected').val();
                nuevaSolicitudObj['idEstudio'] = $('#codEstudioNuevo').find('option:selected').val();
                nuevaSolicitudObj['esEstudio'] = $('#esEstudio').val();
                nuevaSolicitudObj['mensaje'] = '';
                blockUI();
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
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function guardarExamen() {
                var ordenExamenObj = {};
                ordenExamenObj['idTomaMx'] = $("#idTomaMx").val();
                ordenExamenObj['idDiagnostico'] = $('#codDX').find('option:selected').val();
                ordenExamenObj['idEstudio'] = $('#codEstudio').find('option:selected').val();
                ordenExamenObj['idExamen'] = $('#codExamen').find('option:selected').val();
                ordenExamenObj['esEstudio'] = $('#esEstudio').val();
                ordenExamenObj['mensaje'] = '';
                blockUI();
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
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            <!--al seleccionar calidad de la muestra -->
            $('#condicionMx').change(function () {
                $('#causaRechazo').val('');
                if ($(this).val().length > 0) {
                    if ($(this).val() == 'CONDICIONMX|IDC') {
                        $("#dvCausa").show();
                    } else {
                        $("#dvCausa").hide();
                    }
                } else {
                    $("#dvCausa").hide();
                }
            });

            <!-- al seleccionar SILAIS -->
            $('#codSilais').change(function () {
                blockUI();
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sUnidadesUrl, {
                        codSilais: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].codigo + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#codUnidadSalud').html(html);
                    })
                        .fail(function (jqXHR) {
                            setTimeout($.unblockUI, 10);
                            validateLogin(jqXHR);
                        });
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadSalud').html(html);
                }
                $('#codUnidadSalud').val('').change();
                unBlockUI();
            });

            //En recepción general Se valida que si selecciona "Si" en Mx Inadecuada se solicite causa de rechazo
            $("input[name$='rdMxInadequate']").click(function () {
                var valor = $(this).val();
                $('#causaRechazo').val('');
                if (valor == 'true')
                    $('#dvCausa').show();
                else
                    $('#dvCausa').hide();
            });

            $("#btnAddDx").click(function () {
                if ($("#esEstudio").val() == 'true') {
                    getEstudios($("#idTipoMx").val(), $("#codTipoNoti").val(),true);
                } else {
                    getDiagnosticos($("#idTipoMx").val(), $("#codTipoNoti").val(),true);
                }
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

            <!-- cargar dx -->
            function getDiagnosticos(idTipoMx, codTipoNoti, esSolicitud) {
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
                    if (!esSolicitud)
                        $('#codDX').html(html);
                    else
                        $('#codDXNuevo').html(html);
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            <!-- cargar estudios -->
            function getEstudios(idTipoMx, codTipoNoti, esSolicitud) {
                $.getJSON(parametros.sEstudiosURL, {
                    codMx: idTipoMx, tipoNoti: codTipoNoti, idTomaMx: $("#idTomaMx").val(),
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].estudio.idEstudio + '">'
                            + data[i].estudio.nombre
                            + '</option>';
                    }
                    if (!esSolicitud)
                        $('#codEstudio').html(html);
                    else
                        $('#codEstudioNuevo').html(html);
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }

            <!-- Al seleccionar diagnóstico-->
            $('#codDX').change(function () {
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sExamenesURL, {
                        idDx: $(this).val(),
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
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codExamen').html(html);
                }
                $('#codExamen').val('').change();
            });

            <!-- Al seleccionar estudio-->
            $('#codEstudio').change(function () {
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sExamenesEstURL, {
                        idEstudio: $(this).val(),
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
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codExamen').html(html);
                }
                $('#codExamen').val('').change();
            });

            <!-- para buscar código de barra -->
            var timer;
            var iniciado = false;
            var contador;
            //var codigo;
            function tiempo() {
                console.log('tiempo');
                contador++;
                if (contador >= 10) {
                    clearInterval(timer);
                    iniciado = false;
                    //codigo = $.trim($('#codigo').val());
                    console.log('consulta con tiempo');
                    getMxs(false);

                }
            }

            $('#txtCodUnicoMx').keypress(function (event) {
                if (!iniciado) {
                    timer = setInterval(tiempo(), 100);
                    iniciado = true;
                }
                contador = 0;

                if (event.keyCode == '13') {
                    clearInterval(timer);
                    iniciado = false;
                    event.preventDefault();
                    //codigo = $.trim($(this).val());
                    getMxs(false);
                    $('#txtCodUnicoMx').val('');
                }
            });

            function reemplazar(texto, buscar, nuevo) {
                var temp = '';
                var long = texto.length;
                for (j = 0; j < long; j++) {
                    if (texto[j] == buscar) {
                        temp += nuevo;
                    } else
                        temp += texto[j];
                }
                return temp;
            }

            /***************REGLAS EXAMENES***********************/
            function getRulesTest(idExamenes) {
                $.getJSON(parametros.sReglasExamenesURL, {
                    idExamenes: idExamenes,
                    contentType: "charset=ISO-8859-1",
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
        }
    };

}();

