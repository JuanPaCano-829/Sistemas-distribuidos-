function cambiar(identificador){
            // Define una función llamada cambiar
            // Recibe como parámetro el id del elemento que quieres modificar

            let elemento = document.getElementById(identificador);
            // Busca en el documento el elemento que tenga ese id
            // y lo guarda en la variable "elemento"

            elemento.innerHTML = "Texto alterado";
            // Cambia el contenido HTML interno del elemento encontrado
            // por el texto "Texto alterado"
        }

function changeImage() {
        element = document.getElementById("myimage")
        if (element.src.match("down")) {
            element.src = "IMAGES/up.jpg";
        } else {
            element.src = "IMAGES/down.jpg";
        }
    }

function myFunction() {
    var y = document.getElementById("mess");
    y.innerHTML="";
    try {
        var x  =document.getElementById("demo").value;
        if (x === "")      
        if (isNaN(x))     
        if (x > 10)         
        if (x < 5)           
        throw "empty";
        throw "not a number";
        throw "too high";
        throw "too low";
    } catch(err) {
        y.innerHTML="Error: " + err + ".";
    }
}
function increaseText(id){
    document.getElementById(id).innerHTML=
        document.getElementById(id).innerHTML.charAt(0)+
        document.getElementById(id).innerHTML;
}
function upperCase(id){
    var text=document.getElementById(id);
    text.value=text.value.toUpperCase();
}
function eventMouseOver(obj) {
    obj.innerHTML="Thank You";
}
function eventMouseOut(obj){
    obj.innerHTML="Mouse Over Me";
}