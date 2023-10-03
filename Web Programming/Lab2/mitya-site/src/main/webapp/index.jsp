<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mitya's Site</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/record-table.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/img/cloud_favicon.png">
</head>

<body>
<t:generalheader/>
<div class="content">
    <div class="form-container">

        <form class="coords-form" id="main-coords-form" action="shot" method="GET">
            <h1 id="test">Try not to miss!</h1>
            <div class="coords-input-pair">
                <label for="xCoord-button">X Coordinate:</label>
                <button type="button" id="xCoord-button">0</button>
                <input id="xCoord-input" type="hidden" name="xCoord" value="0"/>
            </div>

            <div class="coords-input-pair">
                <label for="yCoord-textbox">Y Coordinate:</label>
                <input type="text" maxlength="14" name="yCoord" id="yCoord-textbox" value="0" required/>
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

    <div class="canvas-container">
        <canvas width="400px" height="400px" id="target-canvas"></canvas>
        <div class="target-settings">
                <input type="checkbox" id="child-mode-check">
                <input type="checkbox" id="prefire-mode-check">

            </div>



    </div>

    <div id="record-table" class="record-table-container">

    </div>

</div>

<footer class="footer">
    <p>
        This is a very long text that should be sort
        of a copyright of this masterpiece, but honestly - I'm quite tired at this point.
        Anyway, this site was made as  a laboratory task №2 of the Web Programming course of the ITMO University.
        The Author - me, Dmitriy Khoroshikh, ISU number 367597. Was a lot of fun! Out!
        <a href="https://github.com/Dimankarp">Github</a>
    </p>
</footer>
<script type="module" src="${pageContext.request.contextPath}/resources/js/index_page.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/drawing.js"></script>
</body>
</html>

