<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${cid}</title>
    <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css"/>
    <script src="js/jquery-1.11.3.min.js" type="text/javascript"></script>
    <script src="js/bootstrap.min.js" type="text/javascript"></script>
    <!-- 引入自定义css文件 style.css -->
    <link rel="stylesheet" href="css/style.css" type="text/css"/>

    <style>
        body {
            margin-top: 20px;
            margin: 0 auto;
            width: 100%;
        }

        .carousel-inner .item img {
            width: 100%;
            height: 300px;
        }
    </style>

    <script type="text/javascript">

        $(function () {
            $("#pageDiv li[value='${pageBean.currentPage}']").prop("class", "active");
            $("#pageDiv li[value='${pageBean.currentPage}'] a").prop("href", "javascript:void(0);");

            if (${pageBean.currentPage } == 1
        )
            {
                $("#previous").prop("class", "disabled");
                $("#previous a").prop("href", "javascript:void(0)");
            }

            if (${pageBean.currentPage } == ${pageBean.totalPage }) {
                $("#next").prop("class", "disabled");
                $("#next a").prop("href", "javascript:void(0)");
            }
        });

    </script>

</head>

<body>

<!-- 引入header.jsp -->
<jsp:include page="/header.jsp"></jsp:include>

<div class="row" style="width: 1210px; margin: 0 auto;">
    <div class="col-md-12">
        <ol class="breadcrumb">
            <li>
                <a href="#">首页</a>
            </li>
        </ol>
    </div>

    <c:forEach items="${pageBean.list}" var="product">
        <div class="col-md-2" style="height: 250px;">
            <a href="${pageContext.request.contextPath}/product?method=productInfo&cid=${cid}&pid=${product.pid}&currentPage=${pageBean.currentPage}">
                <img src="${pageContext.request.contextPath}/${product.pimage}" width="170" height="170"
                     style="display: inline-block;">
            </a>
            <p>
                <a href="${pageContext.request.contextPath}/product?method=productInfo&cid=${cid}&pid=${product.pid}&currentPage=${pageBean.currentPage}"
                   style='color: green'>${product.pname}</a>
            </p>
            <p>
                <font color="#FF0000">商城价：&yen;${product.shop_price}</font>
            </p>
        </div>
    </c:forEach>

</div>

<!--分页 -->
<div style="width: 380px; margin: 0 auto; margin-top: 50px;" id="pageDiv">
    <ul class="pagination" style="text-align: center; margin-top: 10px;">
        <li id="previous">
            <a href="${pageContext.request.contextPath}/product?method=productListByCid&cid=${cid}&currentPage=${pageBean.currentPage-1}"
               aria-label="Previous">
                <span aria-hidden="true">
                    &laquo;
                </span>
            </a>
        </li>

        <c:forEach begin="1" end="${pageBean.totalPage}" var="page">
            <li value="${page}">
                <a href="${pageContext.request.contextPath}/product?method=productListByCid&cid=${cid}&currentPage=${page}">${page }</a>
            </li>
        </c:forEach>

        <li id="next">
            <a href="${pageContext.request.contextPath}/product?method=productListByCid&cid=${cid}&currentPage=${pageBean.currentPage+1}"
               aria-label="Next">
				<span aria-hidden="true">
                    &raquo;
                </span>
            </a>
        </li>
    </ul>
</div>
<!-- 分页结束 -->

<!--商品浏览记录-->
<div style="width: 1210px; margin: 0 auto; padding: 0 9px; border: 1px solid #ddd; border-top: 2px solid #999; height: 246px;">

    <h4 style="width: 50%; float: left; font: 14px/30px 微软雅黑">浏览记录</h4>
    <div style="width: 50%; float: right; text-align: right;">
        <a href="">more</a>
    </div>
    <div style="clear: both;"></div>

    <div style="overflow: hidden;">

        <c:forEach begin="0" end="6" items="${historyProductList}" var="product">

            <ul style="list-style: none;">
                <li style="width: 150px; height: 216; float: left; margin: 0 8px 0 0; padding: 0 18px 15px; text-align: center;">
                    <a href=${pageContext.request.contextPath}/product?method=productInfo?cid=${cid}&pid=${product.pid}&currentPage=${pageBean.currentPage}">
                        <img src="${pageContext.request.contextPath}/${product.pimage}" width="130px" height="130px"/>
                    </a>
                </li>
            </ul>

        </c:forEach>

    </div>
</div>

<!-- 引入footer.jsp -->
<jsp:include page="/footer.jsp"></jsp:include>

</body>

</html>