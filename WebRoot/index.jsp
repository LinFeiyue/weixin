<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
    <script type="text/javascript" src="resources/js/jquery-3.2.1.min.js"></script>

      <script type="text/javascript">
          $(function(){
              $("button").click(function(){
                  var url = "TicketServlet";
                  $.get(url,function(ticket){
                      var src = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
                      $("img").attr("src",src);
                  });

              });
          });
      </script>
  </head>
  
  <body>
    <form action="http://tplogin.cn/" method="post">
        <input id="lgPwd" name="password" type="password" maxlength="32">
        <input type="submit" class="subBtn" id="loginSub" value="确    定">
    </form>
    <br>
    <button >生成二维码</button>
    <img src="" alt="">
    This is my JSP page. <br>
  </body>
</html>
