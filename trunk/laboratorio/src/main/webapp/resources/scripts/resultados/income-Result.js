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
            }
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
                        getOrders(false)
                    }
            });
            <!-- formulario de recepción en laboratorio -->
            $('#receiptOrdersLab-form').validate({
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
                    //guardarRecepcionLab();
                    $.smallBox({
                        title: "Resultado registrado exitosamente" ,
                        content: $("#smallBox_content").val(),
                        color: "#739E73",
                        iconSmall: "fa fa-success",
                        timeout: 4000
                    });
                    setTimeout(function () {window.location.href = parametros.sSearchReceiptUrl},2000);
                }
            });

            function blockUI(){
                var loc = window.location;
                var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
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
                    }});
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            function getOrders(showAll) {
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
                }
                blockUI();
    			$.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(encuestaFiltros),
    				ajax : 'true'
    			}, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idLoad;
                            if ($('#txtEsLaboratorio').val()=='true'){
                                idLoad =dataToLoad[i]. idRecepcion;
                            }else{
                                idLoad = dataToLoad[i].idOrdenExamen;
                            }
                            var actionUrl = parametros.sActionUrl+idLoad;
                            /*table1.fnAddData(
                                [dataToLoad[i].tipoMuestra +" <input type='hidden' value='"+dataToLoad[i].idOrdenExamen+"'/>",dataToLoad[i].tipoExamen,dataToLoad[i].fechaHoraOrden, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].separadaMx, dataToLoad[i].cantidadTubos,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, dataToLoad[i].edad,'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                            */
                            table1.fnAddData(
                                [dataToLoad[i].tipoMuestra,dataToLoad[i].tipoExamen,dataToLoad[i].fechaHoraOrden, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].separadaMx, dataToLoad[i].cantidadTubos,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, '<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
                            /*table1.fnAddData(
                                [dataToLoad[i].fechaHOrden, '', dataToLoad[i].idTomaMx.fechaHTomaMx, dataToLoad[i].idTomaMx.idNotificacion.codSilaisAtencion.nombre, dataToLoad[i].idTomaMx.idNotificacion.codUnidadAtencion.nombre,
                                    dataToLoad[i].idTomaMx.idNotificacion.persona.primerNombre, dataToLoad[i].codEstado.valor, "<input type='hidden' id='idOrden"+i+"' value='"+dataToLoad[i].idOrdenExamen+"'/>"]);
                            */
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
                    unBlockUI();
    			})
    			.fail(function() {
                    unBlockUI();
				    alert( "error" );
				});
            }

            $("#all-orders").click(function() {
                getOrders(true);
            });

            <!-- para guardar recepción en laboratorio -->
            function guardarRecepcionLab() {
                bloquearUI(parametros.blockMess);
                var ordenesObj = {};
                ordenesObj['mensaje'] = '';
                ordenesObj['idRecepcion']=$("#idRecepcion").val();
                ordenesObj['calidadMx'] = $('#codCalidadMx option:selected').val();
                ordenesObj['causaRechazo'] = $('#causaRechazo').val();
                $.ajax(
                    {
                        url: parametros.sAddReceiptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(ordenesObj),
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
                                var msg = $("#msg_receipt_added").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                limpiarDatosRecepcion();
                                setTimeout(function () {window.location.href = parametros.sSearchReceiptUrl},2000);
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });

            }

            function limpiarDatosRecepcion(){
                //$("#txtNombreTransporta").val('');
                //$("#txtTemperatura").val('');
                //$("#codLaboratorioProce").val('').change();
            }

            <!-- al seleccionar SILAIS -->
            $('#codSilais').change(function(){
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
                }else{
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadSalud').html(html);
                }
                $('#codUnidadSalud').val('').change();
                unBlockUI();
            });

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
        }
    };

}();

