var SearchFinalResult = function () {
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
            tablet: 1024,
            phone: 480
        };
        var table1 = $('#orders_result').dataTable({
            "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                "t" +
                "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
            "autoWidth": true, //"T<'clear'>"+
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
            }
        });

        $("#all-results").click(function() {
            getMxResultadosFinal(true);
        });

        <!-- formulario de búsqueda de resultados finales -->
        $('#searchResults-form').validate({
            // Rules for form validation
            rules: {
                fecFinRecep:{required:function(){return $('#fecInicioRecep').val().length>0;}},
                fecInicioRecep:{required:function(){return $('#fecFinRecep').val().length>0;}}
            },
            // Do not change code below
            errorPlacement : function(error, element) {
                error.insertAfter(element.parent());
            },
            submitHandler: function (form) {
                table1.fnClearTable();
                //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                getMxResultadosFinal(false);
            }
        });

        function getMxResultadosFinal(showAll){
            var filtros = {};
            if (showAll){
                filtros['nombreApellido'] = '';
                filtros['fechaInicioRecep'] = '';
                filtros['fechaFinRecepcion'] = '';
                filtros['codSilais'] = '';
                filtros['codUnidadSalud'] = '';
                filtros['codTipoMx'] = '';
                filtros['esLab'] =  $('#txtEsLaboratorio').val();
                filtros['codTipoSolicitud'] = '';
                filtros['nombreSolicitud'] = '';
                filtros['conResultado']= 'Si';
            }else {
                filtros['nombreApellido'] = $('#txtfiltroNombre').val();
                filtros['fechaInicioRecep'] = $('#fecInicioRecep').val();
                filtros['fechaFinRecepcion'] = $('#fecFinRecep').val();
                filtros['codSilais'] = $('#codSilais').find('option:selected').val();
                filtros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                filtros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                filtros['esLab'] =  $('#txtEsLaboratorio').val();
                filtros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                filtros['codTipoSolicitud'] = $('#tipo').find('option:selected').val();
                filtros['nombreSolicitud'] = $('#nombreSoli').val();
                filtros['conResultado']= 'Si';

            }
            bloquearUI(parametros.blockMess);
            $.getJSON(parametros.sSearchUrl, {
                strFilter: JSON.stringify(filtros),
                ajax : 'true'
            }, function(dataToLoad) {
                table1.fnClearTable();
                var len = Object.keys(dataToLoad).length;
                if (len > 0) {
                    for (var i = 0; i < len; i++) {
                        var actionUrl = parametros.sActionUrl+dataToLoad[i].idSolicitud;
                        table1.fnAddData(
                            [dataToLoad[i].codigoUnicoMx, dataToLoad[i].tipoMuestra,dataToLoad[i].fechaTomaMx,dataToLoad[i].fechaInicioSintomas,
                                dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, dataToLoad[i].solicitud, '<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);
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
                .fail(function() {
                    desbloquearUI();
                    alert( "error" );
                });
        }

        <!-- al seleccionar SILAIS -->
        $('#codSilais').change(function(){
            bloquearUI(parametros.blockMess);
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
            desbloquearUI();
        });
    }
};

}();