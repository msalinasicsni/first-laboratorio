/**
 * Created by souyen-ics on 03-02-15.
 */

var DxAnswers = function () {
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

            var bloquearConceptoEnPop = false;

            /****************************************************************
             * Diagnósticos
             ******************************************************************/

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var table1 = $('#records_dx').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,


                "columns": [
                    null, null,
                    {
                        "className":      'addD',
                        "orderable":      false
                    },
                    {
                        "className":      'addC',
                        "orderable":      false
                    }

                ],

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#records_dx'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback : function() {
                    $('.addC')
                        .off("click", addCHandler)
                        .on("click", addCHandler);

                    $('.addD')
                        .off("click", addDHandler)
                        .on("click", addDHandler);
                }

            });

            function addCHandler(){
                var data =  $(this.innerHTML).data('id');
                if (data != null) {
                    $('#div1').hide();
                    $('#div11').hide();
                    $('#div2').show();
                    $('#divInfo').show();
                    $('#dButton1').show();
                    getRequestData(data);
                    getResponses(data);

                }
            }

            function addDHandler(){
                var data =  $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    $('#div1').hide();
                    $('#div11').hide();
                    $('#div3').show();
                    $('#divInfo').show();
                    $('#dButton2').show();
                    getRequestData(data);
                    getSamplingData(id);
                }
            }


            $('#search-form').validate({
                // Rules for form validation
                rules: {
                    tipo: {required: true}

                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getDx(false)
                }
            });

            $("#all-request").click(function () {
                getDx(true);
            });


            function getDx(showAll) {
                var pNombre;
                var pTipo;

                if (showAll) {
                    pTipo = '';
                    pNombre = '';
                } else {
                    pTipo = $('#tipo option:selected').val();
                    pNombre = $('#nombre').val();
                }
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.searchDxUrl, {
                    nombre: encodeURI(pNombre),
                    tipo: pTipo,
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {

                            var btnAdd = '<button type="button" title="Respuestas resultado" class="btn btn-primary btn-xs" data-id="'+dataToLoad[i].idDx + "," + dataToLoad[i].tipoSolicitud +
                                '" > <i class="fa fa-list"></i>' ;
                            //       var actionUrl = parametros.sActionUrl + dataToLoad[i].idDx + "," + dataToLoad[i].tipoSolicitud;

                            var btnDataEntry = '<button type="button" title="Datos ingreso recepción paciente" class="btn btn-primary btn-xs" data-id="'+dataToLoad[i].idDx + "," + dataToLoad[i].tipoSolicitud +
                                '" > <i class="fa fa-list"></i>' ;
                            if (dataToLoad[i].tipoSolicitud == 'Estudio') {
                                btnDataEntry = '<button type="button" disabled class="btn btn-primary btn-xs" data-id="'+dataToLoad[i].idDx + "," + dataToLoad[i].tipoSolicitud +
                                    '" > <i class="fa fa-list"></i>';
                            }
                            //   var action2Url = parametros.sDataConcepstUrl + dataToLoad[i].idDx + "," + dataToLoad[i].tipoSolicitud;

                            table1.fnAddData(
                                [dataToLoad[i].nombreDx, dataToLoad[i].nombreArea, btnDataEntry, btnAdd ]);
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
                    desbloquearUI();
                }).fail(function (jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            /****************************************************************
             * Respuestas
             ******************************************************************/

            function getRequestData(strParametros) {

                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.getRequestDataUrl, {
                    strParametros: strParametros,
                    ajax: 'true'
                }, function (dataToLoad) {

                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $('#requestName').val(dataToLoad[0].nombre);
                        $('#area').val(dataToLoad[0].area);
                        $('#idRequest').val(dataToLoad[0].id);
                        $('#tipoR').val(dataToLoad[0].tipo);

                    } else {
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                }).fail(function (jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }



            var table2 = $('#concepts_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null, null,
                    {
                        "className": 'editarConcepto',
                        "orderable": false
                    },
                    {
                        "className": 'anularConcepto',
                        "orderable": false
                    }
                ],
                "order": [ 2, 'asc' ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#concepts_list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback: function () {
                    $('.anularConcepto')
                        .off("click", anularHandler)
                        .on("click", anularHandler);
                    $('.editarConcepto')
                        .off("click", editarHandler)
                        .on("click", editarHandler)
                }
            });


            function anularHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) anularRespuesta(id);
                }
            }

            function editarHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $("#idRespuestaEdit").val(id);
                    bloquearConceptoEnPop = false;
                    getResponse(id);
                    showModalConcept();
                }
            }

            jQuery.validator.addClassRules("valPrueba", {
                required: true,
                minlength: 2
            });

            /*  if (parametros.sFormConcept == 'SI') {
             getResponses();
             }*/

            $('#respuesta-form').validate({
                // Rules for form validation
                rules: {
                    nombreConcepto: {required: true},
                    codConcepto: {required: true},
                    ordenConcepto: {required: true},
                    minimoConcepto: {required: true},
                    maximoConcepto: {required: true}

                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    guardarRespuesta();
                }
            });

            function getResponses(strParametros) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sRespuestasUrl, {
                    strParametros: strParametros,
                    ajax: 'true'
                }, function (dataToLoad) {
                    table2.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var req, pas, botonEditar;
                            if (dataToLoad[i].requerido == true)
                                req = $("#val_yes").val();
                            else
                                req = $("#val_no").val();
                            if (dataToLoad[i].pasivo == true) {
                                pas = $("#val_yes").val();
                                botonEditar = '<a data-toggle="modal" disabled class="btn btn-danger btn-xs" data-id=' + dataToLoad[i].idRespuesta + '><i class="fa fa-times"></i></a>';
                            } else {
                                pas = $("#val_no").val();
                                botonEditar = '<a data-toggle="modal" title="Editar" class="btn btn-danger btn-xs" data-id=' + dataToLoad[i].idRespuesta + '><i class="fa fa-times"></i></a>';
                            }
                            table2.fnAddData(
                                [dataToLoad[i].nombre, dataToLoad[i].concepto.nombre, dataToLoad[i].orden, req , pas , dataToLoad[i].minimo, dataToLoad[i].maximo, dataToLoad[i].descripcion,
                                        '<a data-toggle="modal" title="Anular" class="btn btn-default btn-xs btn-primary" data-id=' + dataToLoad[i].idRespuesta + '><i class="fa fa-edit"></i></a>',
                                    botonEditar]);
                        }
                    } else {
                        $.smallBox({
                            title: $("#msg_dxno_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                }).fail(function (jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }


            function getResponse(idRespuesta) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sRespuestaUrl, {
                    idRespuesta: idRespuesta,
                    ajax: 'true'
                }, function (dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $("#codConcepto").val(dataToLoad.concepto.idConcepto).change();
                        $("#nombreRespuesta").val(dataToLoad.nombre);
                        $("#ordenRespuesta").val(dataToLoad.orden);
                        $("#checkbox-required").attr('checked', dataToLoad.requerido);
                        $("#checkbox-pasive").attr('checked', dataToLoad.pasivo);
                        $("#minimoRespuesta").val(dataToLoad.minimo);
                        $("#maximoRespuesta").val(dataToLoad.maximo);
                        $("#descRespuesta").val(dataToLoad.descripcion);
                    } else {
                        $.smallBox({
                            title: $("#msg_dxno_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                    bloquearConceptoEnPop = true;
                }).fail(function (jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function guardarRespuesta() {

                var jsonObj = {};
                var respuestaObj = {};
                respuestaObj['idRespuesta'] = $("#idRespuestaEdit").val();
                respuestaObj['idRequest'] = $('#idRequest').val();
                respuestaObj['tipo'] = $('#tipoR').val();
                respuestaObj['nombre'] = $("#nombreRespuesta").val();
                respuestaObj['concepto'] = $('#codConcepto').find('option:selected').val();
                respuestaObj['orden'] = $("#ordenRespuesta").val();
                respuestaObj['requerido'] = ($('#checkbox-required').is(':checked'));
                respuestaObj['pasivo'] = ($('#checkbox-pasive').is(':checked'));
                respuestaObj['minimo'] = $("#minimoRespuesta").val();
                respuestaObj['maximo'] = $("#maximoRespuesta").val();
                respuestaObj['descRespuesta'] = $("#descRespuesta").val();
                jsonObj['respuesta'] = respuestaObj;
                jsonObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.actionUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(jsonObj),
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
                                var strParametros = $('#idRequest').val() + ',' + $('#tipoR').val();
                                getResponses(strParametros);
                                var msg;
                                //si es guardar limpiar el formulario
                                if ($("#idRespuestaEdit").val().length <= 0) {
                                    limpiarDatosRespuesta();
                                    msg = $("#msg_response_added").val();
                                } else {
                                    msg = $("#msg_response_updated").val();
                                }


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

            function anularRespuesta(idRespuesta) {
                var anulacionObj = {};
                var respuestaObj = {};
                respuestaObj['idRespuesta'] = idRespuesta;
                respuestaObj['pasivo'] = 'true';
                anulacionObj['respuesta'] = respuestaObj;
                anulacionObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.actionUrl,
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
                                var strParametros = $('#idRequest').val() + ',' + $('#tipoR').val();
                                getResponses(strParametros);
                                var msg = $("#msg_response_cancel").val();
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

            function limpiarDatosRespuesta() {
                $("#nombreRespuesta").val('');
                $("#ordenRespuesta").val('');
                $("#minimoRespuesta").val('');
                $("#maximoRespuesta").val('');
                $("#descRespuesta").val('');
                $("#checkbox-required").attr('checked', false);
                $("#checkbox-pasive").attr('checked', false);
                $("#codConcepto").val("").change();
            }

            function showModalConcept() {
                $("#myModal").modal({
                    show: true
                });
            }

            $("#btnAddConcept").click(function () {
                $("#idRespuestaEdit").val('');
                bloquearConceptoEnPop = true;
                limpiarDatosRespuesta();
                showModalConcept();
            });

            $('#codConcepto').change(function () {
                $("#minimoRespuesta").val("");
                $("#maximoRespuesta").val("");
                $("#divNumerico").hide();
                if ($(this).val().length > 0) {
                    if (bloquearConceptoEnPop) {
                        bloquearUI(parametros.blockMess);
                    }
                    $.getJSON(parametros.sTipoDatoUrl, {
                        idTipoDato: $(this).val(),
                        ajax: 'true'
                    }, function (dataToLoad) {
                        var len = Object.keys(dataToLoad).length;
                        if (len > 0) {
                            if (dataToLoad.tipo.codigo != $("#codigoDatoNumerico").val()) {
                                $("#divNumerico").hide();
                            } else {
                                $("#divNumerico").show();
                            }

                        }
                        desbloquearUI();
                    }).fail(function (jqXHR) {
                        desbloquearUI();
                        validateLogin(jqXHR);
                    });
                }
            });


            $("#btnBack").click(function () {
                $('#idRequest').val('');
                $('#requestName').val('');
                $('#area').val('');
                $('#divInfo').hide();
                $('#div2').hide();
                $('#div1').show();
                $('#div11').show();
                $('#dButton1').hide();
            });


            /****************************************************************
             * Datos Ingreso
             ******************************************************************/

            $("#btnBack2").click(function () {
                $('#idRequest').val('');
                $('#requestName').val('');
                $('#area').val('');
                $('#divInfo').hide();
                $('#div3').hide();
                $('#div1').show();
                $('#div11').show();
                $('#dButton2').hide();
            });

            var dataEntryTable = $('#concepts_list2').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "columns": [
                    null,null,null,null,null,null,
                    {
                        "className":      'editarConcepto2',
                        "orderable":      false
                    },
                    {
                        "className":      'anularConcepto2',
                        "orderable":      false
                    }
                ],
                "order": [ 2, 'asc' ],
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#concepts_list2'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.anularConcepto2')
                        .off("click", anularHandler2)
                        .on("click", anularHandler2);
                    $('.editarConcepto2')
                        .off("click", editarHandler2)
                        .on("click", editarHandler2)
                }
            });


            function anularHandler2(){
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) anularDatoRecepcion(id);
                }
            }

            function editarHandler2(){
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $("#idDatoEdit").val(id);
                    getData(id);
                    showModalConcept2();
                }
            }


            function getSamplingData(id) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sDatosUrl, {
                    idSolicitud: id ,
                    ajax : 'true'
                }, function(dataToLoad) {
                    dataEntryTable.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var req, pas, botonEditar;
                            if (dataToLoad[i].requerido==true)
                                req = $("#val_yes").val();
                            else
                                req = $("#val_no").val();
                            if (dataToLoad[i].pasivo==true) {
                                pas = $("#val_yes").val();
                                botonEditar = '<a data-toggle="modal" disabled class="btn btn-danger btn-xs" data-id='+dataToLoad[i].idConceptoSol+'><i class="fa fa-times"></i></a>';
                            } else {
                                pas = $("#val_no").val();
                                botonEditar = '<a data-toggle="modal" title="Editar" class="btn btn-danger btn-xs" data-id='+dataToLoad[i].idConceptoSol+'><i class="fa fa-times"></i></a>';
                            }
                            dataEntryTable.fnAddData(
                                [dataToLoad[i].nombre,dataToLoad[i].concepto.nombre,dataToLoad[i].orden,req ,pas ,dataToLoad[i].descripcion,
                                        '<a data-toggle="modal" title="Anular" class="btn btn-default btn-xs btn-primary" data-id='+dataToLoad[i].idConceptoSol+'><i class="fa fa-edit"></i></a>',
                                    botonEditar]);
                        }
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found2").val() ,
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }


            $('#respuesta-form2').validate({
                // Rules for form validation
                rules: {
                    nombreDato : {required:true},
                    codConcepto2 : {required:true},
                    ordenDato : {required:true}

                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    guardarDatoRecepcion();
                }
            });

            function getData(idConceptoSol) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sDatoUrl, {
                    idConceptoSol: idConceptoSol ,
                    ajax : 'true'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $("#codConcepto2").val(dataToLoad.concepto.idConcepto).change();
                        $("#nombreDato").val(dataToLoad.nombre);
                        $("#ordenDato").val(dataToLoad.orden);
                        $("#checkbox-required2").attr('checked', dataToLoad.requerido);
                        $("#checkbox-pasive2").attr('checked', dataToLoad.pasivo);
                        $("#descDato").val(dataToLoad.descripcion);
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function guardarDatoRecepcion() {
                var jsonObj = {};
                var datoRecepcionObj = {};
                datoRecepcionObj['idDato']=$("#idDatoEdit").val();
                datoRecepcionObj['idSolicitud']=$('#idRequest').val();
                datoRecepcionObj['nombre']=$("#nombreDato").val();
                datoRecepcionObj['concepto']=$('#codConcepto2').find('option:selected').val();
                datoRecepcionObj['orden']=$("#ordenDato").val();
                datoRecepcionObj['requerido']=($('#checkbox-required2').is(':checked'));
                datoRecepcionObj['pasivo']=($('#checkbox-pasive2').is(':checked'));
                datoRecepcionObj['descripcion'] = $("#descDato").val();
                jsonObj['respuesta'] = datoRecepcionObj;
                jsonObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.actionUrl2,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(jsonObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                getSamplingData( $('#idRequest').val());
                                var msg;
                                //si es guardar limpiar el formulario
                                if ($("#idDatoEdit").val().length <= 0){
                                    limpiarCampoDatoRecepcion();
                                    msg = $("#msg_response_added2").val();
                                }else{
                                    msg = $("#msg_response_updated2").val();
                                }


                                $.smallBox({
                                    title: msg ,
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

            function anularDatoRecepcion(idDato) {
                var anulacionObj = {};
                var respuestaObj = {};
                respuestaObj['idDato']=idDato;
                respuestaObj['pasivo']='true';
                anulacionObj['respuesta'] = respuestaObj;
                anulacionObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.actionUrl2,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                getSamplingData( $('#idRequest').val());
                                var msg = $("#msg_response_cancel2").val();
                                $.smallBox({
                                    title: msg ,
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

            function limpiarCampoDatoRecepcion(){
                $("#nombreDato").val('');
                $("#ordenDato").val('').change();
                $("#descDato").val('').change();
                $("#checkbox-required2").attr('checked', false);
                $("#checkbox-pasive2").attr('checked', false);
                $("#codConcepto2").val("").change();

            }

            function showModalConcept2(){
                $("#myModal2").modal({
                    show: true
                });
            }

            $("#btnAddConcept2").click(function(){
                $("#idDatoEdit").val('');
                limpiarCampoDatoRecepcion();
                showModalConcept2();
            });

        }
    }
}();
