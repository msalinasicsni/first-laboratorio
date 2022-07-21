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
            <!-- formulario de b�squeda de ordenes -->
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

            <!-- formulario de recepci�n general -->
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

            <!-- formulario de recepci�n en laboratorio -->
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
                    codEstudio: {required: true},
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
                    if ($('#esCC').val()!=undefined){
                        mxFiltros['controlCalidad'] = $('#esCC').val()=='true';
                    }else{
                        mxFiltros['controlCalidad'] = '';
                    }
                    mxFiltros['codigoVIH'] = '';

                } else {
                    mxFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    if ($('#txtEsLaboratorio').val() == 'true') {
                        mxFiltros['fechaInicioRecep'] = $('#fecInicioTomaMx').val();
                        mxFiltros['fechaFinRecepcion'] = $('#fecFinTomaMx').val();
                        mxFiltros['controlCalidad'] = $('#quality').find('option:selected').val();
                    }else {
                        mxFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                        mxFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                        if ($('#esCC').val()!=undefined){
                            mxFiltros['controlCalidad'] = $('#esCC').val()=='true';
                        }else{
                            mxFiltros['controlCalidad'] = '';
                        }
                    }
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    mxFiltros['codTipoSolicitud'] = $('#tipo').find('option:selected').val();
                    mxFiltros['nombreSolicitud'] = $('#nombreSoli').val();
                    mxFiltros['codigoVIH'] = $('#codigoVIH').val();
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
                                    [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + idLoad + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].dias, dataToLoad[i].codSilais, dataToLoad[i].persona, dataToLoad[i].traslado, dataToLoad[i].origen,dataToLoad[i].embarazada, dataToLoad[i].urgente, dataToLoad[i].solicitudes, '<a target="_blank" title="Ver Detalle" href=' + actionUrl + ' class="btn btn-primary btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                                //[dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + idLoad + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].traslado, dataToLoad[i].origen, dataToLoad[i].embarazada, dataToLoad[i].hospitalizado, dataToLoad[i].urgente, '<a href=' + actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                            }else if ($('#esCC').val() == 'true') {
                                idLoad = dataToLoad[i].idTomaMx;
                                actionUrl = actionUrl + idLoad;
                                table1.fnAddData(
                                    [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + idLoad + "'/>", dataToLoad[i].origen, dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].solicitudes, '<a title="Ver Detalle" href=' + actionUrl + ' class="btn btn-primary btn-xs"><i class="fa fa-mail-forward"></i></a>']);

                            }else {
                                idLoad = dataToLoad[i].idTomaMx;
                                actionUrl = actionUrl + idLoad;
                                table1.fnAddData(
                                    [dataToLoad[i].tipoMuestra + " <input type='hidden' value='" + idLoad + "'/>", dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].persona, dataToLoad[i].traslado, dataToLoad[i].origen, dataToLoad[i].embarazada, dataToLoad[i].hospitalizado, dataToLoad[i].urgente, '<a title="Ver Detalle" href=' + actionUrl + ' class="btn btn-primary btn-xs"><i class="fa fa-mail-forward"></i></a>']);
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
                            var btnAnular = '<a data-toggle="modal" title="Cancelar solicitud" class="btn btn-danger btn-xs anularSolicitud" data-id=' + response[i].idSolicitud +'><i class="fa fa-times"></i></a>';
                            if (response[i].permiso==="false"){
                                btnAnular = '<button type="button" title="Cancelar solicitud" disabled class="btn btn-danger btn-xs anularSolicitud" data-id=' + response[i].idSolicitud +'><i class="fa fa-times"></i></button>';
                            }
                            table3.fnAddData(
                                [response[i].tipo,response[i].nombre, response[i].fechaSolicitud, response[i].nombreAreaPrc, response[i].cc, btnAnular]);

                        }
                        $(".anularSolicitud").on("click", function () {
                            $("#idSolicitud").val($(this).data('id'));
                            showModalOverrideRequest();
                        });

                        //al paginar se define nuevamente la funci�n de cargar el detalle
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
                                        '<a data-toggle="modal" title="Cancelar examen" class="btn btn-danger btn-xs anularExamen" data-id=' + response[i].idOrdenExamen + '><i class="fa fa-times"></i></a>']);

                        }
                        $(".anularExamen").on("click", function () {
                            //anularExamen($(this).data('id'));
                            $("#idOrdenExamen").val($(this).data('id'));
                            showModalOverrideTest();
                        });

                        //al paginar se define nuevamente la funci�n de cargar el detalle
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
/*
                mostrarPopUpCodigos('abc-1-17*parasitosis,abc2-1-17*parasitosis2,abcccc-1-17*parasitosis3,abc4-1-17*parasitosis4,abc5-1-17*parasitosis5,abc6-1-17*parasitosis6,abc7-1-17*parasitosis7,abc8-1-17*parasitosis8,abc9-1-17*parasitosis9,abc10-1-17*parasitosis10,abc11-1-17*parasitosis11,abc12-1-17*parasitosis12,abc13-1-17*parasitosis13,abc14-1-17*parasitosis14,abc15-1-17*parasitosis15',
                    'nombre nombre apellido apellido1,nombre nombre apellido apellido2,nombre nombre apellido apellido3,nombre nombre apellido apellido4,nombre nombre apellido apellido5,nombre nombre apellido apellido6,nombre nombre apellido apellido7,nombre nombre apellido apellido8,nombre nombre apellido apellido9,nombre nombre apellido apellido10,nombre nombre apellido apellido11,nombre nombre apellido apellido12,nombre nombre apellido apellido13,nombre nombre apellido apellido14,nombre nombre apellido apellido15'
                ,' ,02/10/2017,03/10/2017,04/10/2017,05/10/2017,06/10/2017,07/10/2017, ,09/10/2017,10/10/2017,11/10/2017,12/10/2017,13/10/2017,14/10/2017, ');
*/
                var oTT = TableTools.fnGetInstance('orders_result');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                var copias = 2;
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
                                            //codUnicosFormat = reemplazar(data.codigosUnicosMx, ".", "*");
                                            if (data.esSarsCov2.length > 0) copias = 4; //4 copias del codigo para Sars-Cov-2 vigilancia. 07/2022
                                            codUnicosFormat = data.codigosUnicosMx;
                                            mostrarPopUpCodigos(data.codigosUnicosMx, data.nombresCodigosLabMx, data.fechasNacimiento);
                                            getMxs(false);
                                        }
                                        desbloquearUI();
                                    },
                                    error: function (jqXHR) {
                                        desbloquearUI();
                                        validateLogin(jqXHR);
                                    }
                                });
                            imprimir2(codUnicosFormat, copias);
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

            function mostrarPopUpCodigos (codigos, nombres, fechasnac) {
                if ($("#mostrarPopUpMx").val()==='true') {
                    var wnd = window.open("about:blank", "", "top=200,left=300,width=600,height=450,_blank");
                    var textoHtml = '<table style="width:100%;border: 1px solid black; border-collapse: collapse;">' +
                        '<tr><th style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#lblPersona").val() + '</th>' +
                        '<th style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#lblCodigo").val() + '</th>' +
                        '<th style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + $("#lblArea").val() + '</th></tr>';
                    var lstCodigos = codigos.split(',');
                    var lstNombres = nombres.split(',');
                    var lstFechas = fechasnac.split(',');
                    for(var row in lstCodigos){
                        var partes = lstCodigos[row].split('*');
                        textoHtml = textoHtml +'<tr><td style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' + unicodeEscape(lstNombres[row]) + '</br>' + lstFechas[row]+ '</td>' +
                            '<td style="border: 1px solid black;padding: 10px;border-collapse: collapse;">' +partes[0] + '</td>' +
                            '<td style="border: 1px solid black;padding: 10px; border-collapse: collapse;">' + unicodeEscape(partes[1]) + '</td></tr>';
                    }

                    textoHtml = textoHtml + '</table>';
                    wnd.document.write(textoHtml);
                }
            }
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


            <!-- para guardar recepci�n general -->
            function guardarRecepcion() {
                bloquearUI(parametros.blockMess);
                var codUnicoFormat = '';
                var copias = 2;
                var recepcionObj = {};
                recepcionObj['idRecepcion'] = '';
                recepcionObj['mensaje'] = '';
                recepcionObj['esSarsCov2'] = '';
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
                                if (data.esSarsCov2.length > 0) copias = 4; //4 copias del codigo para Sars-Cov-2 vigilancia. 07/2022
                                //codUnicoFormat = reemplazar(data.codigoUnicoMx, ".", "*");
                                codUnicoFormat = data.codigoUnicoMx+"*"+data.area;
                                mostrarPopUpCodigos(codUnicoFormat, $("#primerNombre").val() + ' ' + $("#segundoNombre").val() + ' ' + $("#primerApellido").val() + ' ' + $("#segundoApellido").val(), $("#fechaNac").val());
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
                imprimir2(codUnicoFormat, copias);
            }

            function imprimir2(strBarCodes, numCopias){
                console.log(numCopias);
                $.getJSON("http://localhost:13001/print", {
                    barcodes: unicodeEscape(strBarCodes),
                    copias: numCopias,//2 de cada estiquer, 4 para sars-cov-2
                    ajax:'false'
                }, function (data) {
                    console.log(data);
                    $.smallBox({
                        title: "etiquetas impresas",
                        content: $("#disappear").val(),
                        color: "#739E73",
                        iconSmall: "fa fa-success",
                        timeout: 4000
                    });
                }).fail(function (jqXHR) {
                    console.log(jqXHR);
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
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

            <!-- para guardar recepci�n en laboratorio -->
            function guardarRecepcionLab() {
                bloquearUI(parametros.blockMess);
                var recepcionObj = {};
                recepcionObj['mensaje'] = '';
                recepcionObj['idRecepcion'] = $("#idRecepcion").val();
                recepcionObj['calidadMx'] = $('#codCalidadMx option:selected').val();
                recepcionObj['condicionMx'] = $('#condicionMx option:selected').val();
                recepcionObj['causaRechazo'] = $('#causaRechazo').val();
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
                        success: function (data) {
                            console.log(data);
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
                                //limpiarDatosRecepcion();
                                setTimeout(function () {
                                    window.close(); //window.location.href = parametros.sSearchReceiptUrl
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
                if ($("#esEstudio").val() == 'true') {
                    var estudioNuevo = $('#codEstudioNuevo').find('option:selected').val();
                    var elementosEstudio = estudioNuevo.split("-");
                    if (elementosEstudio[1] === "R"){
                        nuevaSolicitudObj['idDiagnostico'] =  elementosEstudio[0];
                        nuevaSolicitudObj['idEstudio'] = null;
                        nuevaSolicitudObj['esEstudio'] = 'false';
                    }else {
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
                if ($("#esEstudio").val() == 'true') {
                    var estudio = $('#codEstudio').find('option:selected').val();
                    var elementosEstudio = estudio.split("-");
                    if (elementosEstudio[1] === "R"){
                        ordenExamenObj['idDiagnostico'] =  elementosEstudio[0];
                        ordenExamenObj['idEstudio'] = null;
                        ordenExamenObj['esEstudio'] = 'false';
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

            //En recepci�n general Se valida que si selecciona "Si" en Mx Inadecuada se solicite causa de rechazo
            $("input[name$='rdMxInadequate']").click(function () {
                var valor = $(this).val();
                $('#causaRechazo').val('');
                if (valor == 'true')
                    $('#dvCausa').show();
                else
                    $('#dvCausa').hide();
            });

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
                    if (!esSolicitud) {
                        $('#codDX').html(html);
                        $('#codDX').val('').change();
                    }
                    else {
                        $('#codDXNuevo').html(html);
                        $('#codDXNuevo').val('').change();
                    }
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            <!-- cargar estudios y diagnósticos permitidos para la mx -->
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
                    }
                    else {
                        $('#codEstudioNuevo').html(html);
                        $('#codEstudioNuevo').val('').change();
                    }
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }

            <!-- Al seleccionar diagn�stico-->
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
            <!-- para buscar c�digo de barra -->
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

