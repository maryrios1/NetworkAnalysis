// magic.js
$(document).ready(function() {
    var form_config = {button: null};
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
            'NameTable'             : $('input[name=NameTable]').val()
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
							 
						var rowNew = $("<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td><input type='button' value='" + status +"' id='btn_" + value['IDSearch'] + "' /></td</tr>");
						rowNew.children().eq(0).text(value['searchname']); 
						rowNew.children().eq(1).text(value['iduser']);
						rowNew.children().eq(6).text(value['searchwords']); 						
						rowNew.children().eq(2).text(value['startsearch']); 
						rowNew.children().eq(3).text(value['endsearch']); 
						rowNew.children().eq(4).text(value['lastupdate']); 
						rowNew.children().eq(5).text(value['type']); 
						rowNew.children().eq(6).text(value['keepsearching']);						
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
