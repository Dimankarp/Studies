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
var shots = []
const redrawEvent = new Event("redraw");
function ajaxExecuted(data) {
    if (data.status === 'success') {
        document.getElementById('shot-outcome-div').focus();
        shots = JSON.parse((document.getElementById('shot-json').innerHTML))
        const canvas = document.getElementById("target-canvas")
        canvas.dispatchEvent(redrawEvent);
    }
}


document.addEventListener("DOMContentLoaded", function () {
    const canvas = document.getElementById("target-canvas")
    const inputX = document.getElementById("main-coords-form:xCoord-textbox")
    const inputY = document.getElementById("main-coords-form:yCoord-textbox")
    const inputRadius = document.getElementById("main-coords-form:radius-textbox")
    const childModeCheckbox = document.getElementById("child-mode-check")
    const prefireModeCheckbox = document.getElementById("prefire-mode-check")
    const coordsForm = document.getElementById("main-coords-form")
    const submitButton = document.getElementById("main-coords-form:coords-submit-btn")


    //DRAWING
    const ctx = canvas.getContext("2d")
    const drawer = new Drawer(ctx, canvas.width, canvas.height)
    let targetCoords = {x: inputX.value, y: inputY.value}
    let isTargetLocked = false

    function redraw(){
        drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value, undefined, isTargetLocked ? drawer.LOCKED_COLOR : drawer.UNLOCKED_COLOR)
    }

    canvas.addEventListener(
        "redraw",
        (e) => {
            redraw()
            },
        false,
    );

    canvas.addEventListener("mousedown", function (e) {
        targetLock(canvas, e);
    });

    canvas.addEventListener("mousemove", function (e) {
        if (targetMove(canvas, e)) drawer.redrawScene(shots, targetCoords, childModeCheckbox.checked, inputRadius.value)
    });

    inputX.addEventListener('input', function (e) {

        if (xIsValid(inputX.value)) {
            targetCoords.x = inputX.value
            isTargetLocked = false;
            redraw()
        }

    })


    inputY.addEventListener('input', function (e) {

        if (yIsValid(inputY.value)) {
            targetCoords.y = inputY.value
            isTargetLocked = false;
            redraw()
        }

    })

    childModeCheckbox.addEventListener('click', (e) => {
        window.localStorage.setItem("childModeChecked", childModeCheckbox.checked)
        redraw()
    })

    prefireModeCheckbox.addEventListener('click', (e) => {
        window.localStorage.setItem("prefireModeChecked", prefireModeCheckbox.checked)
        isTargetLocked = false;
        redraw()
    })


    inputRadius.addEventListener('input', (e) => {
        redraw()
    })

    function targetMove(canvas, event, targetColor) {
        if (!isTargetLocked) {
            let rect = canvas.getBoundingClientRect();
            let x = event.clientX - rect.left;
            let y = event.clientY - rect.top;
            y = Math.round(y * 100) / 100 //That's JS baby
            let {x: newX, y: newY} = drawer.fromRealToGrid({x: x, y: y})
            newX = Math.round(newX * 100) / 100
            newY = Math.round(newY * 100) / 100
            if (!xIsValid(newX) || !yIsValid(newY)) {
                return false;
            }
            if (newX === targetCoords.x && newY === targetCoords.y) {
                return false;
            }

            inputX.value = newX;
            PF('x-slide').setValue(newX)
            inputY.value = newY;

            targetCoords.x = newX
            targetCoords.y = newY
            return true;
        }
    }

    function targetLock(canvas, e) {
        if (prefireModeCheckbox.checked) {
            submitButton.click() //Kinda crude, but because of ajax on submit button - it's necessary
            isTargetLocked = false
        } else if (isTargetLocked) {
            isTargetLocked = false
            targetMove(canvas, e)
            redraw()
        } else {
            isTargetLocked = true
            redraw()
        }

    }


    //LOCAL STORAGE OPERATIONS

    let childModeChecked = window.localStorage.getItem("childModeChecked")
    if (childModeChecked !== null) {
        if (childModeChecked === 'true' !== childModeCheckbox.checked) {
            childModeCheckbox.click()
        }
    }

    let prefireModeChecked = window.localStorage.getItem("prefireModeChecked")
    if (prefireModeChecked !== null) {
        if (prefireModeChecked === 'true' !== prefireModeCheckbox.checked) prefireModeCheckbox.click()
    }

    ajaxExecuted({status: "success"})
    redraw()


})





