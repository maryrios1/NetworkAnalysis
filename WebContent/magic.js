// magic.js
$(document).ready(function() {
    var form_config = {button: null};
    //$("#selectPopular").hide();
    $("#selectNumber").hide();
    
/*
 * <input type="submit" value="StartSearch" id="StartSearch">                                                            
<input type="submit" value="ExportData" id="ExportData">
<input type="submit" value="ShowGraph" id="ShowGraph">
 */
    $("#StartSearch").click(function(){
      form_config.button = 'StartSearch';  
    });

    $("#ExportData").click(function(){
      form_config.button = 'ExportData';  
    });
    
    $("#ShowGraph").click(function(){
      form_config.button = 'ShowGraph';  
    });


    $("#ClassifyTweets").click(function(){
      form_config.button = 'ClassifyTweets';  
    });
    
    // process the form
    $('form').on('submit',function(event) {
        //var value = $(this).attr('id');
        var value =form_config.button;
        // get the form data
        // there are many ways to get this data using jQuery (you can use the class or id also)
        var formData = {
            'keywords'              : $('input[name=keywords]').val(),
            'SearchName'            : $('input[name=SearchName]').val(),
            'NameTable'             : $('input[name=NameTable]').val(),
            'SearchType'             : $('#SearchType option:selected').val()
        };

        //if (value === 'StartSearch') { 
        switch (value) {
            // process the form
            case 'StartSearch':

                $.ajax({
                    type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
                    url: 'SearchTweetsServlet', // the url where we want to POST
                    data: formData, // our data object
                    dataType: 'json', // what type of data do we expect back from the server
                    encode: true
                })
                        // using the done promise callback
                        .done(function (data) {
                            // log data to the console so we can see
                            console.log(data);

                            // here we will handle errors and validation messages
                            if (data.status == 'ERROR') 
                            {                                
                            	$('input[name=keywords]').val("");
                                $('input[name=SearchName]').val("");
                                $('input[name=NameTable]').val("");
                                alert(data.message);
                            }
                            else 
                                alert(data.message); 
                        })
                        // using the fail promise callback
                        .fail(function (data) {
                            // show any errors
                            // best to remove for production
                        	$('input[name=keywords]').val("");
                            $('input[name=SearchName]').val("");
                            $('input[name=NameTable]').val("");                            
                            alert("ERROR "  + data.message);
                        })
                        .success( function(data){
                        	//alert('OK:' + data.message);
                        })
                        .beforeSend( function(data) {
					    	//alert("La búsqueda será iniciada.");
                        });
                break;
            case 'ExportData':

                // process the form
                $.ajax({
                    type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
                    url: 'ExportData', // the url where we want to POST
                    data: formData, // our data object
                    dataType: 'json', // what type of data do we expect back from the server
                    encode: true
                })
                        // using the done promise callback
                        .done(function (data) {
                            // log data to the console so we can see
                            console.log(data);

                            // here we will handle errors and validation messages
                            if (!data.success)
                            {
                                if (data.errors.keywords) {
                                    $('#raro').addClass('has-error'); // add the error class to show red input
                                    $('#raro').append('<div class="help-block">' + data.errors.name + '</div>'); // add the actual error message under our input
                                }
                            }
                            else {

                                // ALL GOOD! just show the success message!
                                $('form').append('<div>' + data.message + '</div>');

                                // usually after form submission, you'll want to redirect
                                // window.location = '/thank-you'; // redirect a user to another page
                                alert('success'); // for now we'll just alert the user

                            }
                        })
                        // using the fail promise callback
                        .fail(function (data) {

                            // show any errors
                            // best to remove for production
                            console.log(data);
                        });
                break;
            case 'ShowGraph':
                //ShowGraph
                //alert("Show Graph");
                $.ajax({
                    type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
                    url: 'GetJsonData', // the url where we want to POST
                    data: formData, // our data object
                    dataType: 'json', // what type of data do we expect back from the server
                    encode: true
                })
                        .done(function (data) {
                            // log data to the console so we can see
                            console.log(data);

                            // here we will handle errors and validation messages
                            if (!data.success)
                            {
                                if (data.errors.keywords) {
                                    $('#raro').addClass('has-error'); // add the error class to show red input
                                    $('#raro').append('<div class="help-block">' + data.errors.name + '</div>'); // add the actual error message under our input
                                }
                            }
                            else {

                                // ALL GOOD! just show the success message!
                                $('form').append('<div>' + data.message + '</div>');

                                // usually after form submission, you'll want to redirect
                                // window.location = '/thank-you'; // redirect a user to another page
                                alert('graph ' + data.graph); // for now we'll just alert the user

                            }
                        })
                        // using the fail promise callback
                        .fail(function (data) {

                            // show any errors
                            // best to remove for production
                            console.log(data);
                        });

                break;
            case 'ClassifyTweets':
                $.ajax({
                    type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
                    url: 'ClassifyTweets', // the url where we want to POST
                    data: formData, // our data object
                    dataType: 'json', // what type of data do we expect back from the server
                    encode: true
                })
                        .done(function (data) {
                            // log data to the console so we can see
                            console.log(data);

                            // here we will handle errors and validation messages
                            if (!data.success)
                            {
                                if (data.errors.keywords) {
                                    $('#raro').addClass('has-error'); // add the error class to show red input
                                    $('#raro').append('<div class="help-block">' + data.errors.name + '</div>'); // add the actual error message under our input
                                }
                            }
                            else {

                                // ALL GOOD! just show the success message!
                                $('form').append('<div>' + data.message + '</div>');

                                // usually after form submission, you'll want to redirect
                                // window.location = '/thank-you'; // redirect a user to another page
                                alert('clasificado ' ); // for now we'll just alert the user

                            }
                        })
                        // using the fail promise callback
                        .fail(function (data) {

                            // show any errors
                            // best to remove for production
                            console.log(data);
                        });
                break;
            default:
                break;
        }
        // stop the form from submitting the normal way and refreshing the page
        event.preventDefault();
    });
    
     $('#frmExportData').on('submit',function(event) {
        var formData = {
            'keywords'              : $('input[name=keywords]').val()
        };
     });
     
     $("#tablediv").hide();
     	$("#showTable").click(function(event){
     		$.get('GetUserSearchesServlet',function(responseJson) {
 				if(responseJson!=null){
 					
					$("#searchtable").find("tr:gt(0)").remove();
					var table1 = $("#searchtable");
					$.each(responseJson, function(key,value) { 
						var status=""; 
						if(value['keepsearching']==true) 
							status = "STOP"; 
						 else 
							 status = "RESTART";
						
						var selection = ""
						if(value['type']=='SEARCH')
							selection = "<option value='STREAM' id='STREAM'>Stream</option>" +
										"<option value='SEARCH' id='SEARCH' selected='selected'>Search</option> ";
						else
							selection = "<option value='STREAM' id='STREAM' selected='selected'>Stream</option>" +
										"<option value='SEARCH' id='SEARCH'>Search</option> ";
						
						//Search Name, Words,,Start Search, End Search,	Last Update,Type,Keep Searching,Export
						var rowNew = $("<tr><td></td><td></td><td></td><td></td><td></td>" + 
								"<td><div class='select-wrapper'>" +
									"<select name='SearchType_" + value['IDSearch'] + "' id='SearchType_" + value['IDSearch'] + "'> " +
									selection +
									"</select> " +
								"</div></td>" +
								"<td><input type='button' onclick='stopRestartSearch(this," + value['IDSearch'] + ")' value='" + status +"' id='btn_" + value['IDSearch'] + "' /></td>" +
								"<td><input type='button' onclick='exportToExcel(this," + value['IDSearch'] + ")' value='Export' id='btnExport_" + value['IDSearch'] + "' /></td></tr>");
						rowNew.children().eq(0).text(value['searchname']); 
						//rowNew.children().eq(1).text(value['iduser']);
						rowNew.children().eq(1).text(value['searchwords']); 						
						rowNew.children().eq(2).text(value['startsearch']); 
						rowNew.children().eq(3).text(value['endsearch']); 
						//rowNew.children().eq(4).text(value['lastupdate']); 
						rowNew.children().eq(4).text(value['tweetsNumber']);
						//rowNew.children().eq(5).find("option[value='"+ value['type'] +"']" ).attr('selected','selected');
						//rowNew.children().eq(5).find("#SearchType_" + value['IDSearch'] + " option[value='"+ value['type'] +"']").attr('selected', 'selected'); 
						//rowNew.children().eq(6).text(value['keepsearching']);						
						rowNew.appendTo(table1);						
					});
 				}
     		});
     		$("#tablediv").show();
     	});

});

$(document).ajaxSend(function(event, request, settings) {
  $('#loading-indicator').show();
});

$(document).ajaxComplete(function(event, request, settings) {
  $('#loading-indicator').hide();
});

//the helper function provided by neo4j documents
function idIndex(a,id) {
    for (var i=0;i<a.length;i++) {
        if (a[i].id == id) return i;}
    return null;
}
function stopRestartSearch2(btn,id){
	var btnId = btn.id;
	var formData;
	var url;
	if(btn.value == 'RESTART'){
		url = '/NetworkAnalysis/rest/Search/RestartRequest';
		formData = {
	            "typeAction"    : "RESTART",
	            "type"          : $('#SearchType_' + id + ' option:selected').val(),
	            "relation"      : "REPLIED",
	            "idSearch"		: id
	        };
		
		$.ajax({
            url: url,
            data: JSON.stringify(formData),
            dataType: "json",
            type: "POST",
            contentType: "application/json",
            beforeSend: function(data){
            	btn.value  = 'STOP';
            },
            success: function(data) {
                alert ("success" + data.message);
                btn.value  = 'STOP';
            },
            error: function(data) {
                alert("error" +  data.message);
                btn.value  = 'RESTART';
            }
        });
	}
	else
	{
		url = '/NetworkAnalysis/rest/Search/StopRequest';
		formData = {
	            "typeAction"    : "STOP",
	            "idSearch"		: id
        };
		 
		$.ajax({
            url: url,
            data: JSON.stringify(formData),
            dataType: "json",
            contentType: "application/json",
            type: "POST",
            beforeSend: function(data){
            	btn.value  = 'RESTART';
            },
            success: function(data) {
                alert ("success" + data.message);
                btn.value  = 'RESTART';
            },
            error: function(data) {
                alert("error" +  data.message);
                btn.value  = 'STOP';
            }
        });
	}
}

function stopRestartSearch(btn,id){
	var btnId = btn.id;
	//alert('The id of the button is ' + btnId + ',  id: ' + id + ', value: ' + btn.value)
	var formData;
	if(btn.value == 'RESTART'){
		formData = {
	            'typeAction'    : 'RESTART',
	            'type'          : $('#SearchType_' + id + ' option:selected').val(),
	            'relation'      : 'REPLIED',
	            'idSearch'		: id
	        };
	}
	else
	{
		formData = {
	            'typeAction'    : 'STOP',
	            'idSearch'		: id
        };
	}

	$.ajax({
        type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
        url: 'RestartStopSearchServlet', // the url where we want to POST
        data: formData, // our data object
        dataType: 'json', // what type of data do we expect back from the server
        encode: true,
        beforeSend: function(data){
        	if(btn.value=="RESTART"){
        		btn.value  = 'STOP';
        	}
        	else{
        		btn.value  = 'RESTART';
        	}
        	
        },
        complete: function(data){
        	/*if(btn.value=="STOP"){
        		btn.value  = 'RESTART'
        	}
        	else{
        		btn.value  = 'STOP'
        	}*/
        	//btn.value = data.object;
        }
    })
            // using the done promise callback
            .done(function (data) {
                // log data to the console so we can see
                console.log(data);
                // here we will handle errors and validation messages
                if (data.status=='ERROR')
                {
                    alert(data.message);
                }
                else {
                	alert(data.message);
                }
                btn.value = data.object;
            })
            // using the fail promise callback
            .fail(function (data) {

                // show any errors
                // best to remove for production
                console.log(data);
                alert('¡Ha ocurrido un error! ' + data.message);
                btn.value = data.object;
            });
}

function exportToExcel(btn,id){
	var btnId = btn.id;
	var favorite = $('#cFavorito').is(":checked");
	var retweeted = $('#cRetweeted').is(":checked");
	var limit = $('#txtNumber').val();

	location.href = "ExportToExcel?SearchId=" + id+"&NormalNodes=" + $('#cNormalNodes').is(":checked") + 
	"&HashtagNodes=" + $('#cHashtagNodes').is(":checked") + "&Tweets=" +$('#cTweets').is(":checked") + 
	"&Favorite=" + favorite + "&Retweeted=" + retweeted + "&Limit=" + limit;
	/*
	var formData = {
            'NormalNodes'   : $('#cNormalNodes').is(":checked"),
            'HashtagNodes'  : $('#cHashtagNodes').is(":checked"),
            'Tweets'      	: $('#cTweets').is(":checked"),
            'SearchId'		: id
        };
   	*/
}

function showSelectNumber(){
	var ischeckedF = $('#cFavorito').is(":checked");
	var ischeckedR = $('#cRetweeted').is(":checked");
	
	if(ischeckedF == true || ischeckedR == true)	
		$('#selectNumber').show();
	else
		$('#selectNumber').hide();
	
}