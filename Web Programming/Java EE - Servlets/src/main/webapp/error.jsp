<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:generalpage>
    <jsp:attribute name="title">
        Ooops...
    </jsp:attribute>
    <jsp:attribute name="head_additives">
          <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/error-page.css">
        <script type = "text/javascript" src="${pageContext.request.contextPath}/resources/js/response_desc.js"></script>
    </jsp:attribute>

    <jsp:attribute name="post_scripts">
        <script>setResponseDesc(${pageContext.response.status}) </script>
    </jsp:attribute>

<jsp:body>
    <div class="content">
        <div class="error-screen">
            <div>
                <h1>Error ${pageContext.response.status}</h1>
                <h3 id="response-desc"> </h3>
            </div>

        </div>
    </div>
</jsp:body>
</t:generalpage>