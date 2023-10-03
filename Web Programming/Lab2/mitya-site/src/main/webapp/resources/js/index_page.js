
import {Y_MAX, Y_MIN, yIsValid, RADIUS_ALLOWED_ARR, X_ALLOWED_ARR, xIsValid} from './input_validator.js'
import {setCookie, getCookie, getCookies} from './cookie_handler.js'
var chosenIndex = 4;



var xCoordsButton = document.getElementById("xCoord-button");
const xInput = document.getElementById("xCoord-input")

let lastXCoords = getCookie("xCoord")
if(lastXCoords !== undefined){
    if(xIsValid(lastXCoords.val)){
        xCoordsButton.innerHTML =lastXCoords.val
        xInput.value =lastXCoords.val
    }
}
function changeValue() {
    chosenIndex = (chosenIndex+1)%X_ALLOWED_ARR.length;
    xCoordsButton.innerHTML = X_ALLOWED_ARR[chosenIndex];
    xInput.value = X_ALLOWED_ARR[chosenIndex];
            }
xCoordsButton.addEventListener('click', changeValue);



const box = document.getElementById('yCoord-textbox');
let lastYCoords = getCookie("yCoord")
if(lastYCoords !== undefined){
    if(yIsValid(lastYCoords.val)){
        box.value =lastYCoords.val

    }
}
box.addEventListener('input', function(event) 
        {
        //Вариант без блокировки вводимого значения, но с использованием системы validityCheck HTML
        if(yIsValid(box.value)){
            box.setCustomValidity("")
        }
        else box.setCustomValidity(`Y coordinated must be a float number in range of: ${Y_MIN}..${Y_MAX}`)
        box.reportValidity();
        });


const selector = document.getElementById("radius-selector")
for(let i = 0; i< RADIUS_ALLOWED_ARR.length; i++) {
    selector.appendChild(new Option(RADIUS_ALLOWED_ARR[i], RADIUS_ALLOWED_ARR[i], i===0))
}

let lastRadius = getCookie("radius")
if(lastRadius !== undefined){
    let ind = RADIUS_ALLOWED_ARR.indexOf(lastRadius.val)
    if(ind !== -1){
        selector.selectedIndex = ind
    }
}


const childModeCheckbox = document.getElementById("child-mode-check")
let childModeChecked = getCookie("childModeChecked")
if(childModeChecked !== undefined){
    if(childModeChecked.val==='true' !== childModeCheckbox.checked){
        childModeCheckbox.click()
    }
}

const prefireModeCheckbox = document.getElementById("prefire-mode-check")
let prefireModeChecked = getCookie("prefireModeChecked")
if(prefireModeChecked !== undefined){
    if(prefireModeChecked.val==='true'  !== prefireModeCheckbox.checked)prefireModeCheckbox.click()
}


document.getElementById("main-coords-form").addEventListener("formdata", (e)=>{
    const formData = e.formData
    setCookie("xCoord", formData.get("xCoord"))
    setCookie("yCoord", formData.get("yCoord"))
    setCookie("radius", formData.get("radius"))
    setCookie("childModeChecked", childModeCheckbox.checked)
    setCookie("prefireModeChecked", prefireModeCheckbox.checked)
})

