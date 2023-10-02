<%@tag description="General page template" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="title" required="true" %>
<%@attribute name="head_additives" fragment="true" %>
<%@attribute name="post_scripts" fragment="true" %>
<%@attribute name="post_footer" fragment="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/img/cloud_favicon.png">
    <jsp:invoke fragment="head_additives"/>
</head>

<body>
<t:generalheader/>
<jsp:doBody/>

<footer class="footer">
    <p>
        This is a very long text that should be sort
        of a copyright of this masterpiece, but honestly - I'm quite tired at this point.
        Anyway, this site was made as  a laboratory task №2 of the Web Programming course of the ITMO University.
        The Author - me, Dmitriy Khoroshikh, ISU number 367597. Was a lot of fun! Out!
        <jsp:invoke fragment="post_footer"/>
        <a href="https://github.com/Dimankarp">Github</a>
    </p>
</footer>
<jsp:invoke fragment="post_scripts"/>
</body>
</html>