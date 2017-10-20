<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PipaFace</title>
<style>
    #subBut {
        font-size: 30px;
    }
    
    #svg-container {
        border: 1px solid #000;
    }

    .face-feature {
        stroke: #000;
        stroke-width: 1px;
        fill:#fff;
    }

    .color-box {
        width: 50px;
        height: 50px;
        margin: 5px 5px 0px 5px;
    }

    .colors-container {
        padding-bottom: 5px;
        background-color: #fff;
        border: 1px solid #000;
        border-radius: 2px;
        position: absolute;
    }

</style>
</head>
<body>
    <center>
        <h1>PipaFace</h1>
        <br>
        <form method="post" action="upload"
            enctype="multipart/form-data">
            <input id="file" type="file" name="file" size="60" /><br /><br><br>
            <!-- <br /> <input id="subBut" type="submit" value="Upload" /> -->
        </form>
        <button onclick="sendFile()">Teste</button>
        <img id="recImg"/>
        <br>
        <img id="img-container"/>
        <svg id="svg-container" width="1000" height="500"></svg>

    </center>
    <script src="//code.jquery.com/jquery-1.12.0.min.js"></script>
    <script src="//d3js.org/d3.v3.min.js" charset="utf-8"></script>
    <script src="opencv.js"></script>
    <script src="main.js"></script>
    <script src="desenhista.js"></script>
</body>
</html>