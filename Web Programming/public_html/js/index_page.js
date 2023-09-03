import {Y_MAX, Y_MIN, yIsValid, RADIUS_ALLOWED_ARR} from './input_validator.js'
var xCoords = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
var chosenIndex = 5;
var xCoordsButton = document.getElementById("xCoord-button");
function changeValue() {
    chosenIndex = (chosenIndex+1)%xCoords.length;
    xCoordsButton.innerHTML = xCoords[chosenIndex];
    var input = document.getElementById("xCoord-input")
    input.value = xCoords[chosenIndex];
            }
xCoordsButton.addEventListener('click', changeValue);


 var box = document.getElementById('y-textbox');
box.addEventListener('input', function(event) 
        {
        //Вариант без блокировки вводимого значения, но с использованием системы validityCheck HTML
        if(yIsValid(event.data))box.setCustomValidity("");
        else box.setCustomValidity(`Y coordinated must be a float number in range of: ${Y_MIN}..${Y_MAX}`)
        box.reportValidity();

        });



var selector = document.getElementById("radius-selector")
for(let i = 0; i< RADIUS_ALLOWED_ARR.length; i++) {
        selector.appendChild(new Option(RADIUS_ALLOWED_ARR[i], RADIUS_ALLOWED_ARR[i], i===0))
    }
