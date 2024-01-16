
import {Y_MAX, Y_MIN, yIsValid, RADIUS_ALLOWED_ARR, X_ALLOWED_ARR, xIsValid} from './input_validator.js'
import {Drawer, LOCKED_COLOR, UNLOCKED_COLOR} from "./drawing.js";
const DATE_FORMAT_OPTIONS = {
    hour12: false,
    localeMatcher: "lookup",
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
};

document.addEventListener("DOMContentLoaded", function () {
    const canvas = document.getElementById("target-canvas")
    const inputX = document.getElementById("xCoord-input")
    const inputXButton = document.getElementById("xCoord-button")
    const inputY = document.getElementById("yCoord-textbox")
    const inputRadius = document.getElementById("radius-selector")
    const childModeCheckbox = document.getElementById("child-mode-check")
    const prefireModeCheckbox = document.getElementById("prefire-mode-check")
    const coordsForm = document.getElementById("main-coords-form")


    //DRAWING
    const ctx = canvas.getContext("2d")
    const drawer = new Drawer(ctx, canvas.width, canvas.height)
    let targetCoords = {x: inputX.value, y: inputY.value}
    let isTargetLocked = false
    let shots = []

    canvas.addEventListener("mousedown", function (e) {
        targetLock(canvas, e);
    });

    canvas.addEventListener("mousemove", function (e) {
        if (targetMove(canvas, e)) drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
    });

    inputXButton.addEventListener('click', function (e) {
        if (xIsValid(inputXButton.innerHTML)) {
            targetCoords.x = inputXButton.innerHTML
            isTargetLocked = false;
            drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
        }
    })

    inputY.addEventListener('input', function (e) {

        if (yIsValid(inputY.value)) {
            targetCoords.y = inputY.value
            isTargetLocked = false;
            drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
        }

    })

    childModeCheckbox.addEventListener('click', (e) => {
        drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
    })

    inputRadius.addEventListener('change', (e) => {
        drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
    })

    function targetMove(canvas, event, targetColor) {
        if (!isTargetLocked) {
            let rect = canvas.getBoundingClientRect();
            let x = event.clientX - rect.left;
            let y = event.clientY - rect.top;
            y = Math.round(y * 100) / 100 //That's JS baby
            let {x: newX, y: newY} = drawer.fromRealToGrid({x: x, y: y})
            newX = Math.round(newX)
            newY = Math.round(newY * 100) / 100
            if (!xIsValid(newX) || !yIsValid(newY)) {
                return false;
            }
            if (newX === targetCoords.x && newY === targetCoords.y) {
                return false;
            }

            inputX.value = newX;
            inputY.value = newY;
            inputXButton.innerHTML = newX;

            targetCoords.x = newX
            targetCoords.y = newY
            return true;
        }
    }
    function targetLock(canvas, e) {
        if (isTargetLocked) {
            isTargetLocked = false
            targetMove(canvas, e)
            drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
        } else {
            drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value, undefined, LOCKED_COLOR)
            isTargetLocked = true
        }
        if(prefireModeCheckbox.checked){
            coordsForm.submit()
        }

    }


    //LOCAL STORAGE OPERATIONS
    let chosenIndex = 4;
    let lastXCoords = window.localStorage.getItem("xCoord")
    if(lastXCoords !== null){
        if(xIsValid(lastXCoords)){
            inputXButton.innerHTML =lastXCoords
            inputX.value =lastXCoords
        }
    }
    function changeValue() {
        chosenIndex = (chosenIndex+1)%X_ALLOWED_ARR.length;
        inputXButton.innerHTML = X_ALLOWED_ARR[chosenIndex];
        inputX.value = X_ALLOWED_ARR[chosenIndex];
    }
    inputXButton.addEventListener('click', changeValue);

    let lastYCoords = window.localStorage.getItem("yCoord")
    if(lastYCoords !== null){
        if(yIsValid(lastYCoords)){
            inputY.value =lastYCoords

        }
    }
    inputY.addEventListener('input', function(event)
    {
        //Вариант без блокировки вводимого значения, но с использованием системы validityCheck HTML
        if(yIsValid(inputY.value)){
            inputY.setCustomValidity("")
        }
        else inputY.setCustomValidity(`Y coordinated must be a float number in range of: ${Y_MIN}..${Y_MAX}`)
        inputY.reportValidity();
    });


    for(let i = 0; i< RADIUS_ALLOWED_ARR.length; i++) {
        inputRadius.appendChild(new Option(RADIUS_ALLOWED_ARR[i], RADIUS_ALLOWED_ARR[i], i===0))
    }

    let lastRadius = window.localStorage.getItem("radius")
    if(lastRadius !== null){
        let ind = RADIUS_ALLOWED_ARR.indexOf(Number(lastRadius))
        if(ind !== -1){
            inputRadius.selectedIndex = ind
        }
    }

    let childModeChecked = window.localStorage.getItem("childModeChecked")
    if(childModeChecked !== null){
        if(childModeChecked==='true' !== childModeCheckbox.checked){
            childModeCheckbox.click()
        }
    }

    let prefireModeChecked = window.localStorage.getItem("prefireModeChecked")
    if(prefireModeChecked !== null){
        if(prefireModeChecked==='true'  !== prefireModeCheckbox.checked)prefireModeCheckbox.click()
    }


    document.getElementById("main-coords-form").addEventListener("formdata", (e)=>{
        const formData = e.formData
        window.localStorage.setItem("xCoord", formData.get("xCoord"))
        window.localStorage.setItem("yCoord", formData.get("yCoord"))
        window.localStorage.setItem("radius", formData.get("radius"))
        window.localStorage.setItem("childModeChecked", childModeCheckbox.checked)
        window.localStorage.setItem("prefireModeChecked", prefireModeCheckbox.checked)
    })

    //GETTING SHOTS
    function getShotsData() {

        try {
            let ajaxRequest = new XMLHttpRequest();
            ajaxRequest.onreadystatechange = () => {
                if (ajaxRequest.readyState === 4) {
                    if (ajaxRequest.status === 200) {
                        shots = JSON.parse(ajaxRequest.responseText)
                        drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked,inputRadius.value,  undefined, isTargetLocked ? LOCKED_COLOR : UNLOCKED_COLOR)
                        const recordTableDiv = document.getElementById("record-table")
                        let tableHTML =
                            `
                                <table>
                                <tr>
                                <th>Is Hit</th>
                                <th>X</th>
                                <th>Y</th>
                                <th>Radius</th>
                                <th>Time</th>
                                </tr>
                        `
                        if (shots.length !== 0) {

                            try {
                                for (let shotIndex in shots) {
                                    tableHTML +=
                                        `
                                    <tr>
                                    <td>${shots[shotIndex].isHit ? "Yes!" : "No"}</td>
                                    <td>${shots[shotIndex].xCoord}</td>
                                    <td>${shots[shotIndex].yCoord}</td>
                                    <td>${shots[shotIndex].radius}</td>
                                    <td>${new Date(shots[shotIndex].timeStamp).toLocaleString(['en-US', 'ru-RU'], DATE_FORMAT_OPTIONS)}</td>
                                    </tr>
                                    `
                                }
                                tableHTML += "</table>"
                                recordTableDiv.innerHTML = tableHTML
                            } catch (e) {
                            }
                        }
                    }
                }

            };
            let jsonURL = "get-shots"
            ajaxRequest.open("GET", jsonURL, true)
            ajaxRequest.send(null);
            return true;
        } catch (e) {
            return false;
        }
    }

    getShotsData()





})





