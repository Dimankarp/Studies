
<?php
  const X_ALLOWED_ARR = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
  const Y_MAX = 5;
  const Y_MIN = -3;
  const RADIUS_ALLOWED_ARR = [1, 2, 3, 4, 5];
  
  
    ini_set('display_errors', '1');
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        
        # Target Coords
        $xCoord = isset($_POST["xCoord"]) ? (int)strip_tags($_POST["xCoord"]) : null;
        $yCoord = isset($_POST["yCoord"]) ? (float)strip_tags($_POST["yCoord"]) : null;
        $radius = isset($_POST["radius"]) ? (int)strip_tags($_POST["radius"]) : null;
        if (is_null($xCoord) OR !in_array($xCoord, X_ALLOWED_ARR) OR
            is_null($yCoord) OR !($yCoord >= Y_MIN && $yCoord <= Y_MAX) OR
            is_null($radius) OR  !in_array($radius, RADIUS_ALLOWED_ARR)) {
            http_response_code(400);
            include('error.php');
            exit();
        }

        header("Content-Type: text/HTML; charset=utf-8");
        header("Content-Transfer-Encoding: 8bit");
        $success =true;
        if ($success) {
            #200 - Okay.
            http_response_code(200);
        } else {
            #500 - Internal Server Error
            http_response_code(500);
            include('error.php');
            exit();
        }

    } else {
        #The request is not a POST
        http_response_code(403);
        include('error.php');
        exit();
    }


    require_once("utils.php");

    session_start();

    $isHit = checkShot($xCoord, $yCoord, $radius);
    if(isset($_SESSION["shotsArray"]))array_unshift($_SESSION["shotsArray"], new Shot($isHit, $xCoord, $yCoord, $radius, date('m.d.y h:i:s A')));
    else $_SESSION["shotsArray"] = [new Shot($isHit, $xCoord, $yCoord, $radius, date('m.d.y h:i:s A'))];
?>

<!DOCTYPE html>
<html>

  <head>
    <title>Митин сайт</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="./css/index.css">
    <link rel="stylesheet" href="./css/fonts.css">
    <link rel="stylesheet" href="./css/shot-page.css">
    </head>

  <body>
    <header class="header" id="sticky_header">
    <script src="./js/sticky_header.js" type="text/javascript"></script>
      <div class="title">
         <span>P3217</span>
         <a href="./index.php"><span  id="name-text">Mitya Khoroshikh </span></a>   <!-- Вообще-то я Хороших Дмитрий Максимович, но Mitya Khoroshikh - стилёво!  -->
         <span>Variant 2711</span>
        </div>
    </header>

    <div class="content" style="min-height:30cm;">
    
        <div class="shot-screen">
          <div>
          <h1><?= $isHit ? "That's a hit!" : "Ooops...You've missed!" ?></h1>
          </div>

          <div>
            <?php
            if(isset($_SESSION["shotsArray"])) {
            echo <<< EOF
            <table>
              <tr>
                <th>Is Hit</th>
                <th>X</th>
                <th>Y</th>
                <th>Radius</th>
                <th>Time</th>
              </tr>
            EOF;
            foreach ($_SESSION["shotsArray"] as $shot) {
              echo <<<EOF
              <tr>
                <td>$shot->isHit</td>
                <td>$shot->x</td>
                <td>$shot->y</td>
                <td>$shot->radius</td>
                <td>$shot->timestamp</td>
              </tr>
            EOF;
            }
          echo '</table>';
          }
            ?>
          </div>

        </div>
    
    
    </div>

    <footer class="footer">
    <p>
      This is a very long text that should be sort
      of a copyright of this masterpiece, but honeslty - I'm quite tired at this point.
      Anyway, this site was made as  a laboratory task №1 of the Web Programming course of the ITMO University.
      The Author - me, Dmitriy Khoroshikh, ISU number 367597. Was a lot of fun! Out!
      <a href="https://github.com/Dimankarp">Github</a>
    </p>
    </footer>
   



  </body>


</html>