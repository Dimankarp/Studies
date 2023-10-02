import {X_ALLOWED_ARR, xIsValid, Y_MAX, Y_MIN, yIsValid} from './input_validator.js'
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
const canvas = document.getElementById("target-canvas")
const inputX = document.getElementById("xCoord-input")
const inputXButton = document.getElementById("xCoord-button")
const inputY = document.getElementById("yCoord-textbox")
const inputRadius = document.getElementById("radius-selector")
const childModeCheckbox = document.getElementById("child-mode-check")
const prefireModeCheckbox = document.getElementById("prefire-mode-check")

const submitButton = document.getElementById("coords-submit-btn")



const ctx = canvas.getContext("2d")

const gridWidth = X_ALLOWED_ARR.length + 1
const gridHeight = Y_MAX - Y_MIN + 2

let targetCoords = {x: inputX.value, y: inputY.value}
let isTargetLocked = false
const LOCKED_COLOR = "rgba(249, 166, 2, 0.5)"
const UNLOCKED_COLOR = "rgba(119, 102, 92, 0.5)"
let shots = []


canvas.addEventListener("mousedown", function (e) {
    targetLock(canvas, e);
});

canvas.addEventListener("mousemove", function (e) {
    if (targetMove(canvas, e)) redrawScene(shots, targetCoords)

});

inputXButton.addEventListener('click', function (e) {
    if (xIsValid(inputXButton.innerHTML)) {
        targetCoords.x = inputXButton.innerHTML
        isTargetLocked = false;
        redrawScene(shots, targetCoords)
    }
})

inputY.addEventListener('input', function (e) {

    if (yIsValid(inputY.value)) {
        targetCoords.y = inputY.value
        isTargetLocked = false;
        redrawScene(shots, targetCoords)
    }

})

childModeCheckbox.addEventListener('click', (e)=>{
    redrawScene(shots, targetCoords)
})

inputRadius.addEventListener('change', (e)=>{
    redrawScene(shots, targetCoords)
})

const coordsForm = document.getElementById("main-coords-form")
function targetLock(canvas, e) {
    if (isTargetLocked) {
        isTargetLocked = false
        targetMove(canvas, e)
        redrawScene(shots, targetCoords)
    } else {
        redrawScene(shots, targetCoords, undefined, LOCKED_COLOR)
        isTargetLocked = true
    }
    if(prefireModeCheckbox.checked){
        coordsForm.submit()
    }

}

function fromRealToGrid({x, y}) {
    const currRealCenter = {x: canvas.width / 2, y: canvas.height / 2}
    const transformed = {x: x - currRealCenter.x, y: currRealCenter.y - y}
    return {x: transformed.x * (gridWidth / canvas.width), y: transformed.y * (gridHeight / canvas.height)}
}

function fromGridToReal({x, y}) {
    const currRealCenter = {x: canvas.width / 2, y: canvas.height / 2}
    const unscaled = {x: x / (gridWidth / canvas.width), y: y / (gridHeight / canvas.height)}
    return {x: unscaled.x + currRealCenter.x, y: currRealCenter.y - unscaled.y}
}



function targetMove(canvas, event, targetColor) {
    if (!isTargetLocked) {
        let rect = canvas.getBoundingClientRect();
        let x = event.clientX - rect.left;
        let y = event.clientY - rect.top;
        y = Math.round(y * 100) / 100 //That's JS baby
        let {x: newX, y: newY} = fromRealToGrid({x: x, y: y})
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

function drawCircle({x, y}, radius = canvas.width / (gridWidth * 2), color = UNLOCKED_COLOR) {
    ctx.fillStyle = color
    ctx.strokeStyle = color
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2, true);
    ctx.fill()
    ctx.stroke()
}

function drawGraph(color = "rgba(119, 102, 92, 0.5)") {
    const currRealCenter = {x: canvas.width / 2, y: canvas.height / 2}
    ctx.fillStyle = color
    ctx.strokeStyle = color
    ctx.beginPath()
    ctx.moveTo(currRealCenter.x, 0)
    ctx.lineTo(currRealCenter.x, canvas.height)
    ctx.moveTo(0, currRealCenter.y)
    ctx.lineTo(canvas.width, currRealCenter.y)
    ctx.stroke()
}

function drawAllowedBorder(color = "rgba(119, 102, 92, 0.5)") {
    ctx.fillStyle = color
    ctx.strokeStyle = color
    let left_down = fromGridToReal({x: X_ALLOWED_ARR[0], y: Y_MIN})
    let left_up = fromGridToReal({x: X_ALLOWED_ARR[0], y: Y_MAX})
    let right_down = fromGridToReal({x: X_ALLOWED_ARR[X_ALLOWED_ARR.length - 1], y: Y_MIN})
    let right_up = fromGridToReal({x: X_ALLOWED_ARR[X_ALLOWED_ARR.length - 1], y: Y_MAX})
    ctx.beginPath()
    ctx.moveTo(left_up.x, left_up.y)
    ctx.lineTo(left_down.x, left_down.y)
    ctx.lineTo(right_down.x, right_down.y)
    ctx.lineTo(right_up.x, right_up.y)
    ctx.lineTo(left_up.x, left_up.y)
    ctx.closePath()
    ctx.stroke()
}

function drawZones(color = "rgba(119, 102, 92, 0.5)"){
    ctx.fillStyle = color
    ctx.strokeStyle = color
    const R = inputRadius.value
    ctx.beginPath()
    const startPos = fromGridToReal({x:-R, y:-R/2})
    ctx.moveTo(startPos.x, startPos.y)
    let pointsToArc = [
        [0, -R/2],
        [0, 0],
        [R, 0]]
    for (const item in pointsToArc){
        let realCoords = fromGridToReal({x:pointsToArc[item][0], y:pointsToArc[item][1]})
        ctx.lineTo(realCoords.x, realCoords.y)
    }
    let arcCoords = fromGridToReal({x:0, y:0})
    ctx.arc(arcCoords.x, arcCoords.y, R*(canvas.width / gridWidth), 0, 3*Math.PI/2, true)
    pointsToArc = [
        [0, R/2],
        [-R, 0]]
    for (const item in pointsToArc){
        let realCoords = fromGridToReal({x:pointsToArc[item][0], y:pointsToArc[item][1]})
        ctx.lineTo(realCoords.x, realCoords.y)
    }
    ctx.lineTo(startPos.x, startPos.y)
    ctx.closePath()
    ctx.fill()


}

function redrawScene(shotsArr = shots, coords, radius, targetColor) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawGraph();
    drawAllowedBorder();
    if(childModeCheckbox.checked){
        drawZones()
    }
    shotsArr.forEach((item) => {
        let hitColor = item.isHit ? "rgba(119, 255, 92, 0.5)" : "rgba(255, 102, 92, 0.5)"
        drawCircle(fromGridToReal({x: item.xCoord, y: item.yCoord}), canvas.width / (gridWidth * 2), hitColor)
    })

    drawCircle(fromGridToReal(coords), radius, targetColor)
}

function getShotsData() {

    try {
        let ajaxRequest = new XMLHttpRequest();
        ajaxRequest.onreadystatechange = () => {
            if (ajaxRequest.readyState === 4) {
                if (ajaxRequest.status === 200) {
                    shots = JSON.parse(ajaxRequest.responseText)
                    redrawScene(shots, targetCoords, isTargetLocked ? LOCKED_COLOR : UNLOCKED_COLOR)
                    console.log(shots)
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
                    if(shots.length !== 0){

                        try {
                            for(let shotIndex in shots){
                                tableHTML+=
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
                            tableHTML+="</table>"
                            recordTableDiv.innerHTML = tableHTML
                        }
                        catch (e) {
                        }
                    }
                }
            }

        };
        let jsonURL = "shot"
        ajaxRequest.open("GET", jsonURL, true)
        ajaxRequest.setRequestHeader("accept_json", "true")
        ajaxRequest.send(null);
        return true;
    } catch (e) {
        console.log(e)
        return false;
    }
}

getShotsData()
