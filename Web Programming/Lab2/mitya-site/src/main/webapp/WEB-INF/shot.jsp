<%@page import="java.text.SimpleDateFormat, java.util.TimeZone, java.util.Date, java.util.LinkedList, mitya.site.Shot" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
    long start_time = System.currentTimeMillis();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    String timeZone = request.getParameter("time-zone");
    if (timeZone != null) dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
%>

<!DOCTYPE html>
<html>

<head>
    <title>Mitya's Site</title>
    <meta charset="utf-8">
    <!-- <meta name="viewport" content="width=device-width, initial-scale=1"> -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shot-page.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/record-table.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/img/cloud_favicon.png">
</head>

<body>
<t:generalheader/>
<div class="content">
    <div class="shot-screen">
        <div>
            <% if (request.getAttribute("isHit") != null && request.getAttribute("isHit") instanceof Boolean) {
                Boolean hit = (Boolean) request.getAttribute("isHit");
                if (hit) {
                    out.println("<h1>That's a hit!</h1>");
                } else {
                    out.println("<h1>Ooops...You've missed!</h1>");
                }
            }
            %>
        </div>

        <div class="record-table-container">
            <%
                if (session.getAttribute("SHOTS") != null) {
                    try {
                        LinkedList<Shot> shotsList = (LinkedList<Shot>) session.getAttribute("SHOTS");
                        out.println("<table>\n" +
                                "<tr>\n" +
                                "<th>Is Hit</th>\n" +
                                "<th>X</th>\n" +
                                "<th>Y</th>\n" +
                                "<th>Radius</th>\n" +
                                "<th>Time</th>\n" +
                                "</tr>");//Yeah-yeah, awful - I know! Say thank you for sourcle level below 15
                        for (Shot item : shotsList) {
                            out.println(String.format("<tr>\n" +
                                            "<td>%s</td>\n" +
                                            "<td>%s</td>\n" +
                                            "<td>%s</td>\n" +
                                            "<td>%s</td>\n" +
                                            "<td>%s</td>\n</tr> ",
                                    item.isHit() ? "Yes!" : "No", item.getX(), item.getY(), item.getRadius(), dateFormat.format(item.getTimeStamp())));
                        }
                        out.println("</table>");
                    } catch (ClassCastException | IllegalStateException e) {

                    }

                }

            %>
        </div>

    </div>


</div>

<footer class="footer">
    <p>
        This is a very long text that should be sort
        of a copyright of this masterpiece, but honeslty - I'm quite tired at this point.
        Anyway, this site was made as a laboratory task №2 of the Web Programming course of the ITMO University.
        The Author - me, Dmitriy Khoroshikh, ISU number 367597. Was a lot of fun! Out!

        This page rendered in <%=System.currentTimeMillis() - start_time%> ms at <%=dateFormat.format(new Date()) %>.
        <a href="https://github.com/Dimankarp">Github</a>
    </p>
</footer>


</body>


</html>