

CKEDITOR.plugins.add( 'addFile',
          {
            init: function( editor )
            {  
              editor.addCommand( 'addFile', new CKEDITOR.dialogCommand( 'addFileDialog' ));
        
              editor.ui.addButton( 'AddFile',
              {
                label: 'Wczytaj plik',
                command: 'addFile',
                icon: this.path + 'uploadfile.png'
              } );
        
        CKEDITOR.dialog.add( 'addFileDialog', function( editor )
    {
      return {
        title : 'Dodaj plik',
        minWidth : 450,
        minHeight : 400,
        contents :
        [
          {
            id : 'tab1',
            label : 'Załaduj plik',
            elements :
            [
              {
                type : 'html',
                html : '<h2>URL pliku:</h2>'
              },
              {
                type : 'text',
                id : 'url',
                label : '',
                validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
              },
              {
                  type : 'html',
                  html : '<h2>Opis linku:</h2>'
               },
	      {
		type: 'text',
	  	id : 'descript',
		label : '',
		validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
	      },
	      {
	       type : 'html',
	       html : '<iframe src="/filestorage" style="width:100%;min-height:250px;" frameborder="0" onload="getImageURLfromIFrame(this)" ></iframe>'
	      }
            ]
          },
          
        ],
	
        onOk : function()
        {
		var dialog = this;
		var url = dialog.getValueOf('tab1','url');
		var descript = dialog.getValueOf('tab1','descript');
		var anchorStr = '<a href="' + url + '">' + descript + '</a>'
	 	var anchor = CKEDITOR.dom.element.createFromHtml(anchorStr);
		editor.insertElement( anchor );
        
        },
       
      };
    } );
               
             
            }
          } );
 
