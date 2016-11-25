<%-- 
    Document   : Login
    Created on : 24/11/2016, 05:36:05 PM
    Author     : Maestria
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
<title>Get the lead - Login</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />

<!--[if lte IE 8]><script src="assets/js/ie/html5shiv.js"></script><![endif]-->
<link rel="stylesheet" href="assets/css/main.css" />
<script type="text/javascript"
	src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.7/angular.min.js"></script>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>

<!--<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.1/jquery-ui.min.js"></script>-->
<!-- Scripts -->
<script src="assets/js/jquery.min.js"></script>
<script src="assets/js/jquery.scrolly.min.js"></script>
<script src="assets/js/skel.min.js"></script>
<script src="assets/js/util.js"></script>
<!--[if lte IE 8]><script src="assets/js/ie/respond.min.js"></script><![endif]-->
<script src="assets/js/main.js"></script>

<script src="magic.js" type="text/javascript"></script>
<!-- load our javascript file -->
<!--[if lte IE 8]><link rel="stylesheet" href="assets/css/ie8.css" /><![endif]-->
<!--[if lte IE 9]><link rel="stylesheet" href="assets/css/ie9.css" /><![endif]-->
<!-- code -->
<script type="text/javascript" src="js/twitter-login.js"></script>
</head>
    <body>
        <section id="five" class="main style1">
            <div class="container">
                <h1>Welcome!</h1>
                <form action="LoginServlet" method="post">
                    <div class="row uniform 50%">
                            <div class="6u 12u$(xsmall)">
                                    <input type="text" name="demo-name" id="demo-name" value="" placeholder="Usuario" />
                                    <br>
                                    <input type="password" name="demo-password" id="demo-password" value="" placeholder="Password" />
                            </div>
                            <div class="6u$ 12u$(xsmall)">
                                    
                            </div>
                           
                            
                            <div class="12u$">
                                    <ul class="actions">
                                        <li><input type="submit" value="Send Message" /></li>
                                            <li><input type="reset" value="Reset" /></li>
                                    </ul>
                            </div>
                    </div>
                </form>
            </div>
        </section>
    </body>
</html>
