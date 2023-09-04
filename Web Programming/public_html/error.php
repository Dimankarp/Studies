<!DOCTYPE html>
<html>

  <head>
    <title>Ooops...</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="./css/index.css">
    <link rel="stylesheet" href="./css/fonts.css">
    <link rel="stylesheet" href="./css/error-page.css">
    <script src="./js/response_desc.js" type="text/javascript"></script>
  </head>

  <body style="margin: 100px;">
    <header class="header">

      <div class="title">
         <span>P3217</span>
         <a href="./index.php"><span  id="name-text">Mitya Khoroshikh </span></a>   <!-- Вообще-то я Хороших Дмитрий Максимович, но Mitya Khoroshikh - стилёво!  -->
         <span>Variant 2711</span>
        </div>
    </header>


    <div class="content">
        <div class="error-screen">
            <div>
            <h1>Error <?=http_response_code()?></h1>

            <h3 id="response-desc"> </h3>

            
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
   


    <script>setResponseDesc(<?=http_response_code()?>) </script>
  </body>


</html>