/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function sendFile() {
    
    var data = new FormData();
    $.each($('#file')[0].files, function(i, file) {
        data.append('file', file);

        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById("img-container").src = fr.result;
        }
        fr.readAsDataURL(file);

    });

    $.ajax({
        url: 'upload',
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',
        success: function(data){
            var dataObj = JSON.parse(data);

            //console.log("Data Received!");
            //console.log(dataObj);

            desenhar(dataObj);
        }
    });
      
}

