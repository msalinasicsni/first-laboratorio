/**
 * Created by FIRSTICT on 4/22/2015.
 */
var SearchWorkSheet = function () {
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
            var table1 = $('#mx-results').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "T"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "columns": [
                    null,null,null,
                    {
                        "className":      'details-control',
                        "orderable":      false,
                        "data":           null,
                        "defaultContent": ''
                    }
                ],
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#mx-results'), breakpointDefinition);
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


            <!-- formulario de búsqueda de mx -->
            $('#searchMx-form').validate({
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
                    getWorkSheets(false)
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

            function getWorkSheets(showAll) {
                var mxFiltros = {};
                if (showAll){
                    mxFiltros['hoja'] = '';
                    mxFiltros['fechaInicioHoja'] = '';
                    mxFiltros['fechaFinHoja'] = '';
                }else {
                    mxFiltros['hoja'] = $('#txtHoja').val();
                    mxFiltros['fechaInicioHoja'] = $('#fecInicioHoja').val();
                    mxFiltros['fechaFinHoja'] = $('#fecFinHoja').val();
                }
                blockUI();
                $.getJSON(parametros.searchUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax : 'true'
                }, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {

                            //   var actionUrl = parametros.sActionUrl+idLoad;
                            //'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>'
                            table1.fnAddData(
                                [dataToLoad[i].numero,dataToLoad[i].fecha, dataToLoad[i].cantidad, " <input type='hidden' value='"+dataToLoad[i].muestras+"'/>"]);

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
                    unBlockUI();
                })
                    .fail(function() {
                        unBlockUI();
                        alert( "error" );
                    });
            }


            $("#all-orders").click(function() {
                getWorkSheets(true);
            });

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format (d,indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();

                var json =JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">'+
                    '<tr><td style="font-weight: bold">'+$('#text_cod_mx').val()+
                    '</td><td style="font-weight: bold">'+$('#text_fechaMx').val()+
                    '</td><td style="font-weight: bold">'+$('#text_silais').val()+
                    '</td><td style="font-weight: bold">'+$('#text_unidadS').val()+
                    '</td><td style="font-weight: bold">'+$('#text_tipoMx').val()+
                    '</td><td style="font-weight: bold">'+$('#text_persona').val()+
                    '</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable =childTable +
                        '<tr></tr><td>'+json[i].codigoUnicoMx+'</td>'+
                        '<td>'+json[i].fechaTomaMx+'</td>'+
                        '<td>'+json[i].codSilais+'</td>' +
                        '<td>'+json[i].codUnidadSalud+'</td>' +
                        '<td>'+json[i].tipoMuestra+'</td>' +
                        '<td>'+json[i].persona+'</td>' +
                        '</tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#mx-results tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if ( row.child.isShown() ) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child( format(row.data(),3)).show();
                    tr.addClass('shown');
                }
            } );

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

           $("#print").click(function() {
                var oTT = TableTools.fnGetInstance('mx-results');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;

                if (len > 0) {

                    blockUI(parametros.blockMess);
                    var hojas = "";
                    for (var i = 0; i < len; i++) {
                        var texto = aSelectedTrs[i].firstChild.innerHTML;
                        var hoja = texto.substring(texto.lastIndexOf(">")+1, texto.length);
                        if (i+1< len) {
                            hojas += hoja + ",";
                        } else {
                            hojas += hoja;
                        }
                    }

                    $.ajax(
                        {
                            url: parametros.printUrl,
                            type: 'GET',
                            dataType: 'text',
                            data: {hojas: hojas},
                            contentType: 'application/json',
                            mimeType: 'application/json',
                            success: function (data) {
                                var blob = blobData(data, 'application/pdf');
                                showBlob(blob);
                                unBlockUI();
                            },
                            error: function (data, status, er) {
                                unBlockUI();
                                alert("error: " + data + " status: " + status + " er:" + er);
                            }
                        });



                }else{
                    $.smallBox({
                        title : $("#msg_select_workSheet").val(),
                        content : "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
                        color : "#C46A69",
                        iconSmall : "fa fa-times fa-2x fadeInRight animated",
                        timeout : 4000
                    });
                }


            });

        }
    };

}();

