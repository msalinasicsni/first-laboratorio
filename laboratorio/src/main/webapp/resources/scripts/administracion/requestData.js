/**
 * Created by souyen-ics on 03-02-15.
 */

var RequestData = function(){
    var bloquearUI = function(mensaje){
        var loc = window.location;
        var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
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

    var desbloquearUI = function() {
        setTimeout($.unblockUI, 500);
    };

    return {
        //main function to initiate the module
        init: function (parametros) {


            /****************************************************************
             * DATOS INGRESO
             ******************************************************************/
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };

              /*var idDx = $('#idDx').val();
              var idEstudio = $('#idEstudio').val();
                if(idDx != null){
                    $('#dRutina').show();
                }else{
                    $('#dEstudio').show();
                }*/

            var table2 = $('#concepts_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "columns": [
                    null,null,null,null,null,null,
                    {
                        "className":      'editarConcepto',
                        "orderable":      false
                    },
                    {
                        "className":      'anularConcepto',
                        "orderable":      false
                    }
                ],
                "order": [ 2, 'asc' ],
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#concepts_list'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.anularConcepto')
                        .off("click", anularHandler)
                        .on("click", anularHandler);
                    $('.editarConcepto')
                        .off("click", editarHandler)
                        .on("click", editarHandler)
                }
            });


            function anularHandler(){
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    var disabled = this.innerHTML;
                    var n2 = (disabled.indexOf("disabled") > -1);
                    if (!n2) anularDatoRecepcion(id);
                }
            }

            function editarHandler(){
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $("#idDatoEdit").val(id);
                    getData(id);
                    showModalConcept();
                }
            }

            getResponses();

            $('#respuesta-form').validate({
                // Rules for form validation
                rules: {
                    nombreConcepto : {required:true},
                    codConcepto : {required:true},
                    ordenConcepto : {required:true}

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

            function getResponses() {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sDatosUrl, {
                    idSolicitud: $('#idSolicitud').val() ,
                    ajax : 'true'
                }, function(dataToLoad) {
                    table2.fnClearTable();
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
                                botonEditar = '<a data-toggle="modal" class="btn btn-danger btn-xs" data-id='+dataToLoad[i].idConceptoSol+'><i class="fa fa-times"></i></a>';
                            }
                            table2.fnAddData(
                                [dataToLoad[i].nombre,dataToLoad[i].concepto.nombre,dataToLoad[i].orden,req ,pas ,dataToLoad[i].descripcion,
                                        '<a data-toggle="modal" class="btn btn-default btn-xs btn-primary" data-id='+dataToLoad[i].idConceptoSol+'><i class="fa fa-edit"></i></a>',
                                    botonEditar]);
                        }
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


            function getData(idConceptoSol) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sDatoUrl, {
                    idConceptoSol: idConceptoSol ,
                    ajax : 'true'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $("#codConcepto").val(dataToLoad.concepto.idConcepto).change();
                        $("#nombreDato").val(dataToLoad.nombre);
                        $("#ordenDato").val(dataToLoad.orden);
                        $("#checkbox-required").attr('checked', dataToLoad.requerido);
                        $("#checkbox-pasive").attr('checked', dataToLoad.pasivo);
                        //$("#minimoRespuesta").val(dataToLoad.minimo);
                        //$("#maximoRespuesta").val(dataToLoad.maximo);
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
                datoRecepcionObj['idSolicitud']=$('#idSolicitud').val();
                datoRecepcionObj['nombre']=$("#nombreDato").val();
                datoRecepcionObj['concepto']=$('#codConcepto').find('option:selected').val();
                datoRecepcionObj['orden']=$("#ordenDato").val();
                datoRecepcionObj['requerido']=($('#checkbox-required').is(':checked'));
                datoRecepcionObj['pasivo']=($('#checkbox-pasive').is(':checked'));
                //datoRecepcionObj['minimo']=$("#minimoRespuesta").val();
                //datoRecepcionObj['maximo']=$("#maximoRespuesta").val();
                datoRecepcionObj['descripcion'] = $("#descDato").val();
                jsonObj['respuesta'] = datoRecepcionObj;
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                getResponses();
                                var msg;
                                //si es guardar limpiar el formulario
                                if ($("#idDatoEdit").val().length <= 0){
                                    limpiarCampoDatoRecepcion();
                                    msg = $("#msg_response_added").val();
                                }else{
                                    msg = $("#msg_response_updated").val();
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
                        url: parametros.actionUrl,
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
                                getResponses();
                                var msg = $("#msg_response_cancel").val();
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
                console.log("limpiando datos");
                $("#nombreDato").val('');
                $("#ordenDato").val('').change();
                //$("#minimoRespuesta").val('');
                ///$("#maximoRespuesta").val('');
                $("#descDato").val('').change();
                $("#checkbox-required").attr('checked', false);
                $("#checkbox-pasive").attr('checked', false);
                $("#codConcepto").val("").change();
                console.log("termina limpiando datos");
            }

            function showModalConcept(){
                $("#myModal").modal({
                    show: true
                });
            }

            $("#btnAddConcept").click(function(){
                $("#idDatoEdit").val('');
                limpiarCampoDatoRecepcion();
                showModalConcept();
            });
        }
    }
}();
