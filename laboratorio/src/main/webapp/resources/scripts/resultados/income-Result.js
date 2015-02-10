var IncomeResult = function () {
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
			var table1 = $('#orders_result').dataTable({
				"sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
					"t"+
					"<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
				"autoWidth" : true, //"T<'clear'>"+
                "preDrawCallback" : function() {
					// Initialize the responsive datatables helper once.
					if (!responsiveHelper_dt_basic) {
						responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#orders_result'), breakpointDefinition);
					}
				},
				"rowCallback" : function(nRow) {
					responsiveHelper_dt_basic.createExpandIcon(nRow);
				},
				"drawCallback" : function(oSettings) {
					responsiveHelper_dt_basic.respond();
				}
			});

            var table2 = $('#concepts_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "columns": [
                    null,null,null,null,null,null,null
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
                }
            });

            <!-- formulario de búsqueda de ordenes -->
            $('#searchOrders-form').validate({
    			// Rules for form validation
                rules: {
                    fecFinTomaMx:{required:function(){return $('#fecInicioTomaMx').val().length>0;}},
                    fecInicioTomaMx:{required:function(){return $('#fecFinTomaMx').val().length>0;}}
                },
    				// Do not change code below
    				errorPlacement : function(error, element) {
    					error.insertAfter(element.parent());
    				},
    				submitHandler: function (form) {
                        table1.fnClearTable();
                        //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                        getAlicuotas(false)
                    }
            });
            <!-- formulario de recepción en laboratorio -->
            $('#addResult-form').validate({
                // Rules for form validation
                rules: {
                    codResultado: {required : true},
                    codSerotipo: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                        error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarResultado();
                }
            });

            function getAlicuotas(showAll) {
                var encuestaFiltros = {};
                if (showAll){
                    encuestaFiltros['nombreApellido'] = '';
                    encuestaFiltros['fechaInicioTomaMx'] = '';
                    encuestaFiltros['fechaFinTomaMx'] = '';
                    encuestaFiltros['codSilais'] = '';
                    encuestaFiltros['codUnidadSalud'] = '';
                    encuestaFiltros['codTipoMx'] = '';
                    encuestaFiltros['esLab'] =  $('#txtEsLaboratorio').val();
                }else {
                    encuestaFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    encuestaFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    encuestaFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                    encuestaFiltros['codSilais'] = $('#codSilais option:selected').val();
                    encuestaFiltros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    encuestaFiltros['codTipoMx'] = $('#codTipoMx option:selected').val();
                    encuestaFiltros['esLab'] =  $('#txtEsLaboratorio').val();
                    encuestaFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                }
                bloquearUI(parametros.blockMess);
    			$.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(encuestaFiltros),
    				ajax : 'true'
    			}, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var actionUrl = parametros.sActionUrl+dataToLoad[i].idOrdenExamen;
                            table1.fnAddData(
                                [/*dataToLoad[i].idAlicuota,dataToLoad[i].etiquetaPara,*/ dataToLoad[i].examen, dataToLoad[i].fechaHoraOrden,dataToLoad[i].tipoDx,dataToLoad[i].fechaHoraDx, dataToLoad[i].codigoUnicoMx, dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx ,dataToLoad[i].fechaInicioSintomas,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, '<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                        }
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#smallBox_content").val(),
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
    			})
    			.fail(function() {
                    desbloquearUI();
				    alert( "error" );
				});
            }

            function guardarResultado() {
                bloquearUI(parametros.blockMess);
                var objResultado = {};
                var objDetalle = {};
                var cantRespuestas = 0;
                $.getJSON(parametros.sConceptosUrl, {
                    idExamen: $("#idExamen").val() ,
                    ajax : 'false'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idControlRespuesta;
                            var valorControlRespuesta;
                            switch (dataToLoad[i].concepto.tipo.codigo) {
                                case 'TPDATO|LOG':
                                    console.log('logico');
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    //console.log(idControlRespuesta);
                                    valorControlRespuesta = $('#'+idControlRespuesta).is(':checked');
                                    //console.log(valorControlRespuesta);
                                    break;
                                case 'TPDATO|LIST':
                                    console.log('lista');
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    //console.log(idControlRespuesta);
                                    valorControlRespuesta = $('#'+idControlRespuesta).find('option:selected').val();
                                    //console.log(valorControlRespuesta);
                                    break;
                                case 'TPDATO|TXT':
                                    console.log('texto');
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    //console.log(idControlRespuesta);
                                    valorControlRespuesta = $('#'+idControlRespuesta).val();
                                    //console.log(valorControlRespuesta);
                                    break;
                                case 'TPDATO|NMRO':
                                    console.log('numero');
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    //console.log(idControlRespuesta);
                                    valorControlRespuesta = $('#'+idControlRespuesta).val();
                                    //console.log(valorControlRespuesta);
                                    break;
                                default:
                                    console.log('respuesta sin concepto');
                                    break;

                            }
                            console.log(idControlRespuesta);
                            console.log(valorControlRespuesta);
                            var objConcepto = {};
                            objConcepto["idRespuesta"] = idControlRespuesta;
                            objConcepto["valor"]=valorControlRespuesta;
                            console.log(objConcepto);
                            objDetalle[i] = objConcepto;
                            cantRespuestas ++;
                        }
                        objResultado["idOrdenExamen"] = $("#idOrdenExamen").val();
                        objResultado["strRespuestas"] = objDetalle;
                        objResultado["mensaje"] = '';
                        objResultado["cantRespuestas"] = cantRespuestas;
                        console.log(objDetalle);
                        $.ajax(
                            {
                                url: parametros.sSaveResult,
                                type: 'POST',
                                dataType: 'json',
                                data: JSON.stringify(objResultado),
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
                                        var msg = $("#msg_result_added").val();
                                        $.smallBox({
                                            title: msg ,
                                            content: $("#smallBox_content").val(),
                                            color: "#739E73",
                                            iconSmall: "fa fa-success",
                                            timeout: 4000
                                        });
                                        limpiarDatosRecepcion();
                                        setTimeout(function () {window.location.href = parametros.sAlicuotasUrl},3000);
                                    }
                                    desbloquearUI();
                                },
                                error: function (data, status, er) {
                                    desbloquearUI();
                                    alert("error: " + data + " status: " + status + " er:" + er);
                                }
                            });
                    }else{
                        desbloquearUI();
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#smallBox_content").val(),
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }

                }).fail(function(er) {
                    desbloquearUI();
                    alert( "error "+er );
                });

            }
            jQuery.validator.addClassRules("requiredConcept", {
                required: true
            });

            $("#all-orders").click(function() {
                getAlicuotas(true);
            });

            function limpiarDatosRecepcion(){
                //$("#txtNombreTransporta").val('');
                //$("#txtTemperatura").val('');
                //$("#codLaboratorioProce").val('').change();
            }

            //sólo para demo, no funcional
            <!--al seleccionar calidad de la muestra -->
            $('#codResultado').change(function(){
                $('#codSerotipo').val('').change();
                if ($(this).val().length > 0) {
                    if ($(this).val()=='1'){
                        $("#divSerotipo").show();
                    }else{
                        $("#divSerotipo").hide();
                    }
                }else{
                    $("#divSerotipo").hide();
                }
            });

            <!-- para buscar código de barra -->
            var timer;
            var iniciado=false;
            var contador;
            //var codigo;
            function tiempo(){
                console.log('tiempo');
                contador++;
                if(contador >= 10){
                    clearInterval(timer);
                    iniciado = false;
                    //codigo = $.trim($('#codigo').val());
                    console.log('consulta con tiempo');
                    getAlicuotas(false);

                }
            }
            $('#txtCodUnicoMx').keypress(function(event){
                if(!iniciado){
                    timer    = setInterval(tiempo(),100);
                    iniciado = true;
                }
                contador = 0;

                if (event.keyCode == '13') {
                    clearInterval(timer);
                    iniciado = false;
                    event.preventDefault();
                    //codigo = $.trim($(this).val());
                    getAlicuotas(false);
                    $('#txtCodUnicoMx').val('');
                }
            });
        }
    };

}();

