<!DOCTYPE html>
<html>
  <head>
    <title>Mitya's site</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="./css/index.css">
    <link rel="stylesheet" href="./css/fonts.css">
    <script   type="module" src="./js/index_page.js"></script>
    </head>

  <body>
    <header class="header" id="sticky_header">
      <div class="title">
         <span>P3217</span>
         <a href="./index.php"><span  id="name-text">Mitya Khoroshikh </span></a>   <!-- Вообще-то я Хороших Дмитрий Максимович, но Mitya Khoroshikh - стилёво!  -->
         <span>Variant 2711</span>
        </div>
    </header>


    <div class="content">
        <div class="form-container">
        
        <form class="coords-form" action="shot.php" method="POST">
          <h1>Try not to miss!</h1>
          <div class="coords-input-pair">
          <label for="xCoord-button">X Coordinate:</label>
          <button form="target-coords-form" id="xCoord-button" >0</button> <input id="xCoord-input" type="hidden" name="xCoord" value="0"/>
          </div>

          <div class="coords-input-pair">
          <label for="y-textbox">Y Coordinate:</label>
          <input type="text" name="yCoord" id="y-textbox" value="0" required/> 
          </div>

          <div class="coords-input-pair">
          <label for="radius-selector">Radius:</label>
           <select name="radius" id="radius-selector" required>
          </select>
          <input id="time-zone-input" type="hidden" name="time-zone" value="">
          <script>document.getElementById("time-zone-input").value = Intl.DateTimeFormat().resolvedOptions().timeZone </script>
        </div> 
          <button type="submit" id="coords-submit-btn">Shoot!</button>
          
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
   


  <script type="module" src="./js/index_page.js"></script>
  </body>


</html>