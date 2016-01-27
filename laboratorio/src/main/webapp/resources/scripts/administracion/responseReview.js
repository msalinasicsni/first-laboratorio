var Responses = function () {
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
			var responsiveHelper_dt_basic = undefined;
			var breakpointDefinition = {
				tablet : 1024,
				phone : 480
			};
			var table1 = $('#test_result').dataTable({
				"sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
					"<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
				"autoWidth" : true,

                "columns": [
                    null, null,
                    {
                        "className":      'addC',
                        "orderable":      false
                    }

                ],
                "preDrawCallback" : function() {
					// Initialize the responsive datatables helper once.
					if (!responsiveHelper_dt_basic) {
						responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#test_result'), breakpointDefinition);
					}
				},
				"rowCallback" : function(nRow) {
					responsiveHelper_dt_basic.createExpandIcon(nRow);
				},
				"drawCallback" : function(oSettings) {
					responsiveHelper_dt_basic.respond();
				},

                fnDrawCallback : function() {
                    $('.addC')
                        .off("click", addCHandler)
                        .on("click", addCHandler);

                }
			});

            function addCHandler() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    $('#div1').hide();
                    $('#theme1').hide();
                    getResponses(data);
                    getExaData(data);
                    $('#infoExa').fadeIn('slow');
                    $('#theme2').fadeIn('slow');
                }
            }

            $("#btnBack").click(function () {
                $('#idExamen').val('');
                $('#nombreExamen2').val('');
                $('#area').val('');
                $('#infoExa').hide();
                $('#div1').fadeIn('slow');
                $('#theme1').fadeIn('slow');
                $('#theme2').hide();
            });

            var table2 = $('#concepts_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "columns": [
                    null,null,null,null,null,null,null,null,
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
                    if (!n2) anularRespuesta(id);
                }
            }

            function editarHandler(){
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    $("#idRespuestaEdit").val(id);
                    getResponse(id);
                    showModalConcept();
                }
            }
            jQuery.validator.addClassRules("valPrueba", {
                required: true,
                minlength: 2
            });
/*
            if (parametros.sFormConcept == 'SI'){
                getResponses();
            }*/
            $('#search-form').validate({
    			// Rules for form validation
                rules: {
                },
    				// Do not change code below
    				errorPlacement : function(error, element) {
    					error.insertAfter(element.parent());
    				},
    				submitHandler: function (form) {
                        table1.fnClearTable();
                        //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                        getTests(false)
                    }
            });

            $('#respuesta-form').validate({
                // Rules for form validation
                rules: {
                    nombreConcepto : {required:true},
                    codConcepto : {required:true},
                    ordenConcepto : {required:true},
                    minimoConcepto : {required:true},
                    maximoConcepto : {required:true}

                },
                // Do not change code below
                errorPlacement : function(error, element) {
                        error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    guardarRespuesta();
                }
            });


            function getExaData(idExamen) {

                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.getExaUrl, {
                    idExamen: idExamen,
                    ajax: 'true'
                }, function (dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $('#idExamen').val(dataToLoad.idExamen);
                        $('#nombreArea').val(dataToLoad.area.nombre);
                        $('#nombreExamen2').val(dataToLoad.nombre);


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


            function getTests(showAll) {
                var pTipoNoti, pIdDx, pNombreEx;
                if (showAll){
                    pTipoNoti = '';
                    pIdDx = '';
                    pNombreEx = '';
                }else {
                    pTipoNoti = ''; //$('#codTipoNoti option:selected').val();
                    pIdDx = ''; //$('#codTipoDx option:selected').val();
                    pNombreEx = $("#nombreExamen").val();
                }
                bloquearUI(parametros.blockMess);
    			$.getJSON(parametros.sBuscarExamenes, {
                    codTipoNoti: pTipoNoti , idDx : pIdDx, nombreExamen : encodeURI(pNombreEx),
    				ajax : 'true'
    			}, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                          //  var actionUrl = parametros.sActionUrl+dataToLoad[i].idExamen; //+","+dataToLoad[i].idDx+","+dataToLoad[i].codNoti;
                            var btnAdd = '<button type="button" class="btn btn-primary btn-xs" data-id="'+dataToLoad[i].idExamen +
                                '" > <i class="fa fa-list"></i>' ;
                            table1.fnAddData(
                                [dataToLoad[i].nombreExamen//,dataToLoad[i].nombreNoti,dataToLoad[i].nombreDx,''
                                , dataToLoad[i].nombreArea, btnAdd]);
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
    			})
                    .fail(function(jqXHR) {
                        desbloquearUI();
                        validateLogin(jqXHR);
                    });
            }

            function getResponses(idExamen) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sRespuestasUrl, {
                    idExamen: idExamen,
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
                                botonEditar = '<a data-toggle="modal" disabled class="btn btn-danger btn-xs" data-id='+dataToLoad[i].idRespuesta+'><i class="fa fa-times"></i></a>';
                            } else {
                                pas = $("#val_no").val();
                                botonEditar = '<a data-toggle="modal" class="btn btn-danger btn-xs" data-id='+dataToLoad[i].idRespuesta+'><i class="fa fa-times"></i></a>';
                            }
                            table2.fnAddData(
                                [dataToLoad[i].nombre,dataToLoad[i].concepto.nombre,dataToLoad[i].orden,req ,pas ,dataToLoad[i].minimo,dataToLoad[i].maximo,dataToLoad[i].descripcion,
                                        '<a data-toggle="modal" class="btn btn-default btn-xs btn-primary" data-id='+dataToLoad[i].idRespuesta+'><i class="fa fa-edit"></i></a>',
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

            function getResponse(idRespuesta) {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sRespuestaUrl, {
                    idRespuesta: idRespuesta ,
                    ajax : 'true'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $("#codConcepto").val(dataToLoad.concepto.idConcepto).change();
                        $("#nombreRespuesta").val(dataToLoad.nombre);
                        $("#ordenRespuesta").val(dataToLoad.orden);
                        $("#checkbox-required").prop('checked', dataToLoad.requerido);
                        $("#checkbox-pasive").prop('checked', dataToLoad.pasivo);
                        $("#minimoRespuesta").val(dataToLoad.minimo);
                        $("#maximoRespuesta").val(dataToLoad.maximo);
                        $("#descRespuesta").val(dataToLoad.descripcion);
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

            $("#all-orders").click(function() {
                getTests(true);
            });

            function guardarRespuesta() {

                var jsonObj = {};
                var respuestaObj = {};
                respuestaObj['idRespuesta']=$("#idRespuestaEdit").val();
                respuestaObj['idExamen']=$("#idExamen").val();
                respuestaObj['nombre']=$("#nombreRespuesta").val();
                respuestaObj['concepto']=$('#codConcepto').find('option:selected').val();
                respuestaObj['orden']=$("#ordenRespuesta").val();
                respuestaObj['requerido']=($('#checkbox-required').is(':checked'));
                respuestaObj['pasivo']=($('#checkbox-pasive').is(':checked'));
                respuestaObj['minimo']=$("#minimoRespuesta").val();
                respuestaObj['maximo']=$("#maximoRespuesta").val();
                respuestaObj['descRespuesta'] = $("#descRespuesta").val();
                jsonObj['respuesta'] = respuestaObj;
                jsonObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sActionUrl2,
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
                                getResponses($('#idExamen').val());
                                var msg;
                                //si es guardar limpiar el formulario
                                if ($("#idRespuestaEdit").val().length <= 0){
                                    limpiarDatosRespuesta();
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

            function anularRespuesta(idRespuesta) {
                var anulacionObj = {};
                var respuestaObj = {};
                respuestaObj['idRespuesta']=idRespuesta;
                respuestaObj['pasivo']='true';
                anulacionObj['respuesta'] = respuestaObj;
                anulacionObj['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sActionUrl2,
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
                                getResponses($('#idExamen').val());
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

            function limpiarDatosRespuesta(){
                $("#nombreRespuesta").val('');
                $("#ordenRespuesta").val('');
                $("#minimoRespuesta").val('');
                $("#maximoRespuesta").val('');
                $("#descRespuesta").val('');
                $("#checkbox-required").attr('checked', false);
                $("#checkbox-pasive").attr('checked', false);
                $("#codConcepto").val("").change();
            }

            function showModalConcept(){
                $("#myModal").modal({
                    show: true
                });
            }

            $("#btnAddConcept").click(function(){
                $("#idRespuestaEdit").val('');
                limpiarDatosRespuesta();
                showModalConcept();
            });

            $('#codConcepto').change(function() {
                $("#minimoRespuesta").val("");
                $("#maximoRespuesta").val("");
                $("#divNumerico").hide();
                if ($(this).val().length > 0) {
                    bloquearUI(parametros.blockMess);
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
                    }).fail(function(jqXHR) {
                        desbloquearUI();
                        validateLogin(jqXHR);
                    });
                }
            });
        }
    };

}();

