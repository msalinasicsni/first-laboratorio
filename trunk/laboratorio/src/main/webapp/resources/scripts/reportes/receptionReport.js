/**
 * Created by souyen-ics on 04-24-15.
 */


var ReceptionReport = function () {
    return {

        //main function to initiate the module
        init: function (parametros) {

            var codigos = "";


            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };


            var table1 = $('#received-samples').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-5 col-xs-12 hidden'i><'col-xs-12 col-sm-6'p>>",


                "autoWidth" : true,
                "columns": [
                    null,null,null,null,null,null, null,
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#received-samples'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                }

            });


            <!-- filtro Mx -->
            $('#received-samples-form').validate({
                // Rules for form validation
                rules: {
                    fecInicioRecepcion:{required:function(){return $('#fecInicioRecepcion').val().length>0;}},
                    fecFinRecepcion:{required:function(){return $('#fecFinRecepcion').val().length>0;}}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getMxs(false)
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

            function getMxs(showAll) {
                var mxFiltros = {};
                if (showAll){

                    mxFiltros['fechaInicioRecepcion'] = '';
                    mxFiltros['fechaFinRecepcion'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';

                }else {

                    mxFiltros['fechaInicioRecepcion'] = $('#fecInicioRecepcion').val();
                    mxFiltros['fechaFinRecepcion'] = $('#fecFinRecepcion').val();
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['codTipoSolicitud'] = $('#tipo option:selected').val();
                    mxFiltros['nombreSolicitud'] =  encodeURI($('#nombreSoli').val()) ;

                }
                blockUI();
                $.getJSON(parametros.searchUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax : 'true'
                }, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;

                    if (len > 0) {
                        codigos = "";
                        for (var i = 0; i < len; i++) {

                            //   var actionUrl = parametros.sActionUrl+idLoad;
                            //'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>'
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx ,dataToLoad[i].tipoMuestra, dataToLoad[i].fechaRecepcion, dataToLoad[i].calidad, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, " <input type='hidden' value='"+dataToLoad[i].solicitudes+"'/>"]);

                            if (i+1< len) {
                                codigos += dataToLoad[i].codigoUnicoMx + ",";

                            } else {
                                codigos += dataToLoad[i].codigoUnicoMx;

                            }
                        }
                        codigos = reemplazar(codigos,"-","*");
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
                getMxs(true);
            });

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format (d,indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();

                var json =JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">'+
                    '<tr><td style="font-weight: bold">'+$('#text_dx').val()+'</td><td style="font-weight: bold">'+$('#text_dx_date').val()+'</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable =childTable +
                        '<tr><td>'+json[i].nombre+'</td>'+
                        '<td>'+json[i].fechaSolicitud+'</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#received-samples tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if ( row.child.isShown() ) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child( format(row.data(),7)).show();
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


            function reemplazar (texto, buscar, nuevo){
                var temp = '';
                var long = texto.length;
                for (j=0; j<long; j++) {
                    if (texto[j] == buscar)
                    {
                        temp += nuevo;
                    } else
                        temp += texto[j];
                }
                return temp;
            }


            $("#export").click(function() {
               $.ajax(
                        {
                            url: parametros.exportUrl,
                            type: 'GET',
                            dataType: 'text',
                            data: {codes: codigos, fromDate: $('#fecInicioRecepcion').val()  , toDate: $('#fecFinRecepcion').val()},
                            contentType: 'application/json',
                            mimeType: 'application/json',
                            success: function (data) {
                                if(data.length != 0){
                                    var blob = b64toBlob(data, 'application/pdf');
                                    var blobUrl = URL.createObjectURL(blob);

                                    window.open(blobUrl, '', 'width=600,height=400,left=50,top=50,toolbar=yes');
                                }else{
                                    $.smallBox({
                                        title : $("#msg_select").val(),
                                        content : "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
                                        color : "#C46A69",
                                        iconSmall : "fa fa-times fa-2x fadeInRight animated",
                                        timeout : 4000
                                    });
                                }

                                unBlockUI();
                            },
                            error: function (data, status, er) {
                                unBlockUI();
                                alert("error: " + data + " status: " + status + " er:" + er);
                            }
                        });


            });


            function b64toBlob(b64Data, contentType, sliceSize) {
                contentType = contentType || '';
                sliceSize = sliceSize || 512;

                var byteCharacters = atob(b64Data);
                var byteArrays = [];

                for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
                    var slice = byteCharacters.slice(offset, offset + sliceSize);

                    var byteNumbers = new Array(slice.length);
                    for (var i = 0; i < slice.length; i++) {
                        byteNumbers[i] = slice.charCodeAt(i);
                    }

                    var byteArray = new Uint8Array(byteNumbers);

                    byteArrays.push(byteArray);
                }

                var blob = new Blob(byteArrays, {type: contentType});
                return blob;
            }



        }
    };

}();

