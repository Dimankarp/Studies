<script setup>
import '@/assets/css/vue-inputs.scss'
import '@/assets/css/login.scss'

import { ref, onMounted, watch, onUpdated, computed  } from "vue";
import { xIsValid, yIsValid, radiusIsValid, X_VALID_VALS, RADIUS_VALID_VALS } from "@/assets/js/input_validation.js"
import { launchWithAuthCycle } from "@/assets/js/auth.js"
import { getToken, getUsername, logout, tryRefreshingToken } from '../assets/js/auth';
import {Drawer} from '@/assets/js/drawing'
const emit = defineEmits(['shotPostSuccess', 'update:coords'])
const props = defineProps({
    coords: Object,
    shots: Array
})


const x = computed(()=>props.coords.x)
const y = computed(()=>props.coords.y)
const radius = computed(()=>props.coords.radius)


const shots = computed(()=>props.shots)

const postInProgress = ref(false)

function validateCoords(newX, newY) {
    let result = false
    
    let newCoordsObj = { x: x, y: y, radius: radius.value }
    if (xIsValid(Number(newX))) {
        newCoordsObj.x = Number(newX)
        result = result || true
    } 
    if (yIsValid(Number(newY))) {
        newCoordsObj.y = Number(newY)
        result = result || true
    } 
    if(result){ 
  emit("update:coords", newCoordsObj)
  }
    return result
}

/*
Returns true if token refreshment required.
*/
async function tryPostShot() {
    if (!validateCoords(x.value, y.value)) {
        return false;
    }
    console.log("Trying to post")
    postInProgress.value = true;
    try {
        let urlEncoded = new URLSearchParams();
        urlEncoded.append("x", x.value);
        urlEncoded.append("y", y.value);
        urlEncoded.append("radius", radius.value);
        let response = await fetch(`/api/shots/`,
            {
                method: "POST",
                body: urlEncoded,
                headers: {
                    Authorization: "Bearer " + getToken()
                },
                credentials: "same-origin"
            })

        console.log("Posted")
        if (response.ok) {
            let shotObj = await response.json();
            if (shotObj) {
                emit("shotPostSuccess", shotObj)
            } else {
                console.log("Couldn't parse shot obj!")
            }
            return false;

        } else if (response.status === 401) {
            return true;
        } else {
            console.log("Posting shot through canvas failed!")
            return false
        }
    }
    catch (err) {
        console.log(err);
        return false;
    } finally {
        postInProgress.value = false
    }
}

async function postShot() {
    launchWithAuthCycle(tryPostShot);
}

const canvasRef = ref(null)
const isChildMode = ref(window.localStorage.getItem("childMode") ?? false);
const isPrefireMode = ref(window.localStorage.getItem("prefireMode") ?? false);

watch(isChildMode, (newVal)=>{
    window.localStorage.setItem("childMode", newVal)
})

watch(isPrefireMode, (newVal)=>{
    window.localStorage.setItem("prefireMode", newVal)
})



const isCanvasOpen = computed(()=>(!postInProgress.value) && radius.value > 0)




//DRAWING
const drawer = ref(null)
const targetCoords = ref({ x: x.value, y: y.value })
const isTargetLocked = ref(false)

onMounted(()=>{
    const ctx = canvasRef.value.getContext("2d")
    drawer.value = new Drawer(ctx, canvasRef.value.width, canvasRef.value.height)
    redraw()
})

watch(props, ()=>{
    targetCoords.value.x = x.value
    targetCoords.value.y = y.value
    redraw()
})

watch([isTargetLocked, isChildMode, isPrefireMode], ()=>{
    redraw()
})

function redraw() {
    if(isCanvasOpen.value){
    drawer.value.redrawScene(shots.value, targetCoords.value, isChildMode.value, radius.value, undefined, isTargetLocked.value ? drawer.value.LOCKED_COLOR : drawer.value.UNLOCKED_COLOR)
    }
}


function targetMove(canvas, event, targetColor) {
    if (!isTargetLocked.value) {
        let rect = canvas.getBoundingClientRect();
        let mouseX = event.clientX - rect.left;
        let mouseY = event.clientY - rect.top;
        mouseY = Math.round(mouseY * 100) / 100 //That's JS baby
        let { x: newX, y: newY } = drawer.value.fromRealToGrid({ x: mouseX, y: mouseY })
        newX = Math.round( Math.round(newX/0.5*100)/100 )*0.5
        newY = Math.round(newY * 100) / 100
        if (!xIsValid(newX) || !yIsValid(newY)) {
            return false;
        }
        if (newX === targetCoords.value.x && newY === targetCoords.value.y) {
            return false;
        }

        targetCoords.value.x = newX
        targetCoords.value.y = newY
        validateCoords(newX, newY);


        return true;
    }
}

function targetLock(canvas, e) {
    if (isPrefireMode.value) {
        console.log("Posting Shot")
        postShot()
        isTargetLocked.value = false
    } else if (isTargetLocked.value) {
        isTargetLocked.value = false
        targetMove(canvas, e)
        redraw()
    } else {
        isTargetLocked.value = true
        redraw()
    }

}


// //LOCAL STORAGE OPERATIONS

// let childModeChecked = window.localStorage.getItem("childModeChecked")
// if (childModeChecked !== null) {
//     if (childModeChecked === 'true' !== childModeCheckbox.checked) {
//         childModeCheckbox.click()
//     }
// }

// let prefireModeChecked = window.localStorage.getItem("prefireModeChecked")
// if (prefireModeChecked !== null) {
//     if (prefireModeChecked === 'true' !== prefireModeCheckbox.checked) prefireModeCheckbox.click()
// }


function onCanvasMouseDown(e) {
    if(isCanvasOpen.value){
        targetLock(canvasRef.value, e);
    }
    
}

function onCanvasMouseMove(e) {
    if(isCanvasOpen.value){
    if (targetMove(canvasRef.value, e)) {
        drawer.value.redrawScene(shots.value, targetCoords.value, isChildMode.value, radius)
    }
}
}
</script>

<template>
    <canvas :disabled="!isCanvasOpen"  :width="400" :height="400" id="target-canvas" ref="canvasRef" 
    @mousedown="onCanvasMouseDown"
    @mousemove="onCanvasMouseMove"
    ></canvas>
    <div class="target-settings">
        <input type="checkbox" id="child-mode-check" v-model="isChildMode">
        <input type="checkbox" id="prefire-mode-check" v-model="isPrefireMode">
    </div>
</template>
