<?php
$name = $_POST["firstname"];
$surname = $_POST["lastname"];
echo "Твоя кликуха: <b>".$name . " " . $surname . "</b>";
?>

<?php
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        
        # Sender Data
        $xCoord = trim($_POST["xCoord"]);
        $yCoord = $_POST["yCoord"];
        $radius = $_POST["radius"];
        $name = str_replace(array("\r","\n"),array(" "," ") , strip_tags(trim($_POST["name"])));
        $email = filter_var(trim($_POST["email"]), FILTER_SANITIZE_EMAIL);
        $message = trim($_POST["message"]);
        
        if ( empty($name) OR !filter_var($email, FILTER_VALIDATE_EMAIL) OR empty($subject) OR empty($message)) {
            # Set a 400 (bad request) response code and exit.
            http_response_code(400);
            echo "Please complete the form and try again.";
            exit;
        }
        
        # Mail Content
        $content = "Name: $name\n";
        $content .= "Email: $email\n\n";
        $content .= "Message:\n$message\n";

        # email headers.
        $headers = "From: $name <$email>";
    $headers .= 'MIME-Version: 1.0' ."\r\n";
    $headers .= 'Content-Type: text/HTML; charset=utf-8' . "\r\n";
    $headers .= 'Content-Transfer-Encoding: 8bit'. "\n\r\n";
    $headers .= $text . "\r\n";


        # Send the email.
        $success = mail($mail_to, $subject, $content, $headers);
        if ($success) {
            # Set a 200 (okay) response code.
            http_response_code(200);
            echo "Thank You! Your message has been sent.";
        } else {
            # Set a 500 (internal server error) response code.
            http_response_code(500);
            echo "Oops! Something went wrong, we couldn't send your message.";
        }

    } else {
        # Not a POST request, set a 403 (forbidden) response code.
        http_response_code(403);
        echo "There was a problem with your submission, please try again.";
    }

?>



<!DOCTYPE html>
<html>

  <head>
    <title>Митин сайт</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="./css/index.css">
    <link rel="stylesheet" href="./css/fonts.css">
    </head>

  <body>
    <header class="header" id="sticky_header">
    <script src="./js/sticky_header.js" type="text/javascript"></script>
      <div class="title">
         <span>P3217</span>
         <span id="name-text">Mitya Khoroshikh </span>   <!-- Вообще-то я Хороших Дмитрий Максимович, но Mitya Khoroshikh - стилёво!  -->
         <span>Variant 2711</span>
        </div>
    </header>


    <div class="content" style="height: 30cm;">
        <div class="form-container">
        
        <form action="display.php" method="POST">
          <h1>Try not to miss!</h1>
          <p>X Coordinate: <input type="button" name="xCoord"/></p>
          <p>Y Coordinate: <input type="text" name="yCoord"/></p>
          <p>Radius: <select name="radius"></select></p>
          <div><input type="submit" value="Shoot!"/></div>
          
        </form>
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