var SendOrdersReceipt = function () {
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
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
			var table1 = $('#orders_result').dataTable({
				"sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "T"+
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
				},
                "oTableTools": {
                    "sSwfPath": parametros.sTableToolsPath,
                    "sRowSelect": "multi",
                    "aButtons": [ {"sExtends":"select_all", "sButtonText": text_selected_all}, {"sExtends":"select_none", "sButtonText": text_selected_none}]
                }
			});

            $('#searchOrders-form').validate({
    			// Rules for form validation
                rules: {
                    fechaFinRecepcion:{required:function(){return $('#fechaInicioRecep').val().length>0;}},
                    fechaInicioRecep:{required:function(){return $('#fechaFinRecepcion').val().length>0;}}
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

            $('#sendOrders-form').validate({
                // Rules for form validation
                rules: {
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                        error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    enviarRecepcionesSeleccionadas();
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
                    encuestaFiltros['fechaInicioRecep'] = '';
                    encuestaFiltros['fechaFinRecepcion'] = '';
                    encuestaFiltros['codSilais'] = '';
                    encuestaFiltros['codUnidadSalud'] = '';
                    encuestaFiltros['codTipoMx'] = '';
                }else {
                    encuestaFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    encuestaFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    encuestaFiltros['fechaFinRecepcion'] = $('#fechaFinRecepcion').val();
                    encuestaFiltros['codSilais'] = $('#codSilais option:selected').val();
                    encuestaFiltros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    encuestaFiltros['codTipoMx'] = $('#codTipoMx option:selected').val();
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
                            table1.fnAddData(
                                [dataToLoad[i].areaProcesa,dataToLoad[i].tipoMuestra,dataToLoad[i].tipoExamen,dataToLoad[i].fechaRecepcion,dataToLoad[i].fechaHoraOrden, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].separadaMx, dataToLoad[i].cantidadTubos,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona]);
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

            function enviarRecepcionesSeleccionadas() {
                var oTT = TableTools.fnGetInstance('orders_result');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_sending_confirm_t").val(),
                        content: $("#msg_sending_confirm_c").val(),
                        buttons: '['+opcSi+']['+opcNo+']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            bloquearUI(parametros.blockMess);
                          alert('exito');
                          /*
                            var ordenesObj = {};
                            ordenesObj['idRecepcion'] = '';
                            ordenesObj['mensaje'] = '';
                            ordenesObj['idOrdenExamen']=$("#idOrdenExamen").val();
                            ordenesObj['verificaCantTb'] = $('input[name="rdCantTubos"]:checked', '#receiptOrders-form').val();
                            ordenesObj['verificaTipoMx'] = $('input[name="rdTipoMx"]:checked', '#receiptOrders-form').val()
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
                        */
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_sending_cancel").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
                                color: "#C46A69",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                }else{
                    $.smallBox({
                        title : $("#msg_sending_select_order").val(),
                        content : "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
                        color : "#C46A69",
                        iconSmall : "fa fa-times fa-2x fadeInRight animated",
                        timeout : 4000
                    });
                }

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
        }
    };

}();

