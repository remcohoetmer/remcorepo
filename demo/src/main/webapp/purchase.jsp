<%@ page import="nl.cerios.demo.common.*" %>

<!DOCTYPE html>
<html>
<head>
<title>Webshop</title>
</head>
<body>
 <h2><%=request.getAttribute("errorMessage")%></h2>
 
<h3>Order Status</h3>
<%
PurchaseRequest purchaseRequest= (PurchaseRequest) request.getAttribute("purchaseRequest");
%>

  <table>
  <tr>
  <th>
  <td>Name</td>
  <td>Value</td>
  </tr>
  <tr><td>Purchase</td><td></td><%=purchaseRequest.getCustomerId()%></tr>
  <tr><td>Transaction</td><td></td><%=purchaseRequest.getTransactionId()%></tr>
  <tr><td>Order</td><td></td><%=purchaseRequest.getOrderId()%></tr>
  
  </table>
</body>
</html>
