<!DOCTYPE html>
<?php
http_response_code(404);
include('error.php');
die();
?>
<html>
    <head>
        <title> Митькин Сайт</title>
        <meta charset="utf-8">
    </head>

<body>
    <h1> Сайт на PHP</h1>
    <?php
    echo "Hello World!";
    print_r([1,4,9,16]);
    ?>


</html>


