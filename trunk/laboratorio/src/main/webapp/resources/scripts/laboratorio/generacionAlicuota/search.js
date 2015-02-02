/**
 * Created by souyen-ics on 12-18-14.
 */

var ReceiptLabOrders = function () {
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
                "autoWidth" : true,
                "columns": [
                    null,null,null,null,null,null,null,null,
                    {
                        "className":      'details-control',
                        "orderable":      false,
                        "data":           null,
                        "defaultContent": ''
                    }, null
                ],
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


            $('#searchOrders-form').validate({
                // Rules for form validation
                rules: {
                    fecInicioRecepcionLab:{required:function(){return $('#fecInicioRecepcionLab').val().length>0;}},
                    fecFinRecepcionLab:{required:function(){return $('#fecFinRecepcionLab').val().length>0;}}
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
                var filtros = {};
                if (showAll){
                    filtros['nombreApellido'] = '';
                    filtros['fecInicioRecepcionLab'] = '';
                    filtros['fecFinRecepcionLab'] = '';
                    filtros['codSilais'] = '';
                    filtros['codUnidadSalud'] = '';
                    filtros['codTipoMx'] = '';

                }else {
                    filtros['nombreApellido'] = $('#txtfiltroNombre').val();
                    filtros['fecInicioRecepcionLab'] = $('#fecInicioRecepcionLab').val();
                    filtros['fecFinRecepcionLab'] = $('#fecFinRecepcionLab').val();
                    filtros['codSilais'] = $('#codSilais option:selected').val();
                    filtros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    filtros['codTipoMx'] = $('#codTipoMx option:selected').val();
                    filtros['fecFinRecepcionLab'] = $('#fec').val();

                }
                blockUI();
                $.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(filtros),
                    ajax : 'true'
                }, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var actionUrl = parametros.sActionUrl + dataToLoad[i].codigoUnicoMx;

                            table1.fnAddData(
                                [dataToLoad[i].tipoMuestra,dataToLoad[i].fechaTomaMx,dataToLoad[i].fechaInicioSintomas, dataToLoad[i].fechaRecepcionLab, dataToLoad[i].separadaMx,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, " <input type='hidden' value='"+dataToLoad[i].diagnosticos+"'/>", '<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);

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

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format (d,indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();
                var json =JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:50px;">'+
                    '<tr><td style="font-weight: bold">'+$('#text_dx').val()+'</td><td style="font-weight: bold">'+$('#text_dx_date').val()+'</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable =childTable +
                        '<tr></tr><td>'+json[i].nombre+'</td>'+
                        '<td>'+json[i].fechaSolicitud+'</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#orders_result tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if ( row.child.isShown() ) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child( format(row.data(),8)).show();
                    tr.addClass('shown');
                }
            } );

            //FIN


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
