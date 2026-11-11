<%@ tag description="Campo oculto con el token CSRF" pageEncoding="UTF-8" %>
<%--
  Va dentro de todo <form method="post"> que NO sea <form:form> (ese lo agrega solo):
    <form method="post" action="..."> <n:csrf/> ... </form>
  Mientras el proyecto no tenga Spring Security no escribe nada; cuando se agregue, ya funciona.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty _csrf}">
    <input type="hidden" name="<c:out value='${_csrf.parameterName}'/>" value="<c:out value='${_csrf.token}'/>">
</c:if>
