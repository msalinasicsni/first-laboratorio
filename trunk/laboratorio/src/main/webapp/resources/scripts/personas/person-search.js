var SearchPerson = function () {
	
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

    return {
        //main function to initiate the module
        init: function (parametros) {
			var responsiveHelper_dt_basic = undefined;
			var breakpointDefinition = {
				tablet : 1024,
				phone : 480
			};
			var table1 = $('#persons_result').dataTable({
				"sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
					"t"+
					"<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
				"autoWidth" : true,
				"preDrawCallback" : function() {
					// Initialize the responsive datatables helper once.
					if (!responsiveHelper_dt_basic) {
						responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#persons_result'), breakpointDefinition);
					}
				},
				"rowCallback" : function(nRow) {
					responsiveHelper_dt_basic.createExpandIcon(nRow);
				},
				"drawCallback" : function(oSettings) {
					responsiveHelper_dt_basic.respond();
				}
			});

            $('#search-form').validate({
    			// Rules for form validation
    				rules : {
    					filtro : {
    						required : true,
    						minlength: 3
    					}
    				},
    				// Do not change code below
    				errorPlacement : function(error, element) {
    					error.insertAfter(element.parent());
    				},
    				submitHandler: function (form) {
                        table1.fnClearTable();
                        //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                        getPersons();
                    }
            });

            
            function getPersons() {
            	bloquearUI(parametros.blockMess); 
    			$.getJSON(parametros.sPersonUrl, {
    				strFilter : encodeURI($('#filtro').val()),
    				ajax : 'true'
    			}, function(data) {
    				var len = data.length;
                    for ( var i = 0; i < len; i++) {
                        var nombreMuniRes = "";

                        if (data[i].municipioResidencia!=null){
                            nombreMuniRes = data[i].municipioResidencia.nombre;
                        }
						var actionUrl = parametros.sActionUrl + '/'+data[i].personaId;
						table1.fnAddData(
    							[data[i].identificacion, data[i].primerNombre, data[i].segundoNombre, data[i].primerApellido, data[i].segundoApellido, data[i].fechaNacimiento,nombreMuniRes,'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);

                        /*var actionUrl = parametros.sActionUrl + '/'+data[i].personaId;
                        table1.fnAddData(
                            [data[i].identNumero, data[i].primerNombre, data[i].segundoNombre, data[i].primerApellido, data[i].segundoApellido, data[i].fechaNacimiento,data[i].muniResiNombre,'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);*/
    				}
                    setTimeout($.unblockUI, 500);
    			})
    			.fail(function() {
				    alert( "error" );
				    setTimeout($.unblockUI, 5);
                        alert(" status: " + status + " er:" + er);
				});
            };

            $("#create-person").click(function(){
                window.location.href = parametros.sCreatePersonUrl;
            });
        }
    };

}();