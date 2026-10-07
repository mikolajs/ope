
class Groups {
    constructor(){
        this._mkOTable();
    }

    _mkOTable(){
       const oTablePrivate = $();
       const oTable = $('#datatable').dataTable({
        			"sPaginationType": "two_button",
        			"bFilter": true,
        			"iDisplayLength": 50,
        			"bLengthChange": true,
        		    "oLanguage": {
        		        "sSearch": "Filtruj wiersze: ",
        		        "sZeroRecords": "Brak danych do wyświetlenia",
        		        "sInfoEmpty": "Brak danych do wyświetlenia",
        		        "sEmptyTable": "Brak danych do wyświetlenia",
        		        "sInfo": "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
        		        "oPaginate": {
        		        	"sPrevious": "Poprzednie",
        			        "sNext": "Następne",
        			        "sFirst": "Początek",
        			        "sLast": "Koniec",
        		        },
        		        "sInfoFiltered": " - odfiltrowano z _MAX_ wierszy",
        		        "sLengthMenu": 'Pokaż <select>'+
        		        '<option value="20">20</option>'+
        		        '<option value="50">50</option>'+
        		        '<option value="100">100</option>'+
        		        '<option value="-1">całość</option>'+
        		        '</select> wierszy'
        		    }
  	   });
    }

	change(elem){
		let button = $(elem);
		let block = false;
		if(button.hasClass('btn-success')) block = true;
		let id = button.parent().parent().get(0).id;
		id = id.substring(3);
		//console.log(id);
		$('#ajaxId').val(id);
		//console.log('id OK');
		$('#ajaxBlock').prop('checked', block);
		//console.log('block OK');
		$('#ajaxSubmit').trigger('click');
		//console.log('trriger OK');
	}

	setGroup(id, check) {
		id = '#id_' + id
		//console.log('Looking for '+id);
		let child = $(id).find('button').get(0);
		if(check) {
			child.classList.remove('btn-success')
			child.classList.add('btn-danger')
		} else {
			child.classList.add('btn-success')
			child.classList.remove('btn-danger')
		}

	}

}
