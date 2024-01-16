<script setup>
import '@/assets/css/vue-inputs.scss'
import '@/assets/css/login.scss'

import { ref, computed, onUpdated, watch } from "vue";
import { xIsValid, yIsValid, radiusIsValid, X_VALID_VALS, RADIUS_VALID_VALS } from "@/assets/js/input_validation.js"
import { launchWithAuthCycle } from "@/assets/js/auth.js"
import { getToken, getUsername, logout, tryRefreshingToken } from '../assets/js/auth';

const emit = defineEmits(['shotPostSuccess', 'update:coords'])
const props = defineProps({
  coords: Object,
})
const shotTitle = ref("Please, enter coordinates!")
const submitButtonText = ref("Shoot!")

const formInProgress = ref(false);



const xIndex = ref(Math.floor(X_VALID_VALS.length / 2))
const radiusIndex = ref(Math.floor(RADIUS_VALID_VALS.length / 2))

const x = ref(props.coords.x)
const y = ref(props.coords.y)
const radius = ref(props.coords.radius)

watch(props, (newProps) => {
  x.value = newProps.coords.x;
  if (Number(y.value) !== Number(newProps.coords.y)) {
    y.value = newProps.coords.y;
  }
  radius.value = newProps.coords.radius;
})

const X_MIN = -2
const X_MAX = 2
const X_STEP = 0.5

const Y_MAX = 5;
const Y_MIN = -3;

const RADIUS_MIN = -2
const RADIUS_MAX = 2
const RADIUS_STEP = 0.5

const xErrorText = "X must be a number from range -2 to 2 with step 0.5."
const yErrorText = "Y must be a number from range -3 to 5."
const radiusErrorText = "Radius must be a number from range 0 to 2 with step 0.5."

const xIsInvalid = ref(false);
const yIsInvalid = ref(false);
const radiusIsInvalid = ref(false);


function validateForm() {

    let result = true
  let newCoordsObj = { x: x.value, y: Number(y.value), radius: radius.value }
  if (!xIsValid(x.value)) {
    xIsInvalid.value = true;
    newCoordsObj.x = props.coords.x
    result = result && false
  } else {
    xIsInvalid.value = false;
  }

  if (!yIsValid(Number(y.value))) {
    yIsInvalid.value = true;
    newCoordsObj.y = props.coords.y
    result = result && false
  } else {
    yIsInvalid.value = false;
  }

  if (!radiusIsValid(radius.value)) {
    radiusIsInvalid.value = true;
    newCoordsObj.radius = props.coords.radius
    result = result && false
  } else {
    radiusIsInvalid.value = false;
  }
  if (Number(x.value) === Number(props.coords.x) &&
    Number(y.value) === Number(props.coords.y) &&
    Number(radius.value) === Number(props.coords.radius)){
      return true;
    }
  if (result) {

    emit("update:coords", newCoordsObj)
  }
  return result
}

/*
Returns true if token refreshment required.
*/
async function tryPostShot() {
  if (!validateForm()) {
    return false;
  }
  formInProgress.value = true;
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

    if (response.ok) {
      let shotObj = await response.json();
      if (shotObj) {
        emit("shotPostSuccess", shotObj)
      } else {
        shotTitle.value = "Couldn't parse returned shot!"
      }
      return false;

    } else if (response.status === 401) {
      return true;
    } else {
      shotTitle.value = "Posting shot failed!"
      return false
    }
  }
  catch (err) {
    shotTitle.value = "Something went wrong!"
    console.log(err);
    return false;
  } finally {
    formInProgress.value = false
  }
}

async function postShot() {
  launchWithAuthCycle(tryPostShot);
}

function xChange() {
  xIndex.value = (xIndex.value + 1) % X_VALID_VALS.length
  x.value = X_VALID_VALS[xIndex.value]
  validateForm()
}

function radiusChange() {
  radiusIndex.value = (radiusIndex.value + 1) % RADIUS_VALID_VALS.length
  radius.value = RADIUS_VALID_VALS[radiusIndex.value]
  validateForm()
}

watch(y, () => {
  validateForm()
})

</script>

<template>
  <div class="form-container">
    <form class="shot-form" :disabled="formInProgress" novalidate="true">
      <h1>{{ shotTitle }}</h1>
      <div class="input-pair">
        <label for="x-textbox">X:</label>
        <button @click.prevent="xChange" :class="{ invalid: xIsInvalid }">{{ x }}</button>
        <span class="input-pair_errormsg" v-if="xIsInvalid">{{ xErrorText }}</span>
      </div>

      <div class="input-pair">
        <label for="y-textbox">Y:</label>
        <input :class="{ invalid: yIsInvalid }" type="text" name="y" v-model="y" required="true" maxlength="10" />
        <span class="input-pair_errormsg" v-if="yIsInvalid">{{ yErrorText }}</span>
      </div>

      <div class="input-pair">
        <label for="radius-textbox">Radius:</label>
        <button @click.prevent="radiusChange" :class="{ invalid: radiusIsInvalid }">{{ radius }}</button>
        <span class="input-pair_errormsg" v-if="radiusIsInvalid">{{ radiusErrorText }}</span>
      </div>
      <button @click.prevent="postShot">{{ submitButtonText }}</button>
    </form>
    <button @click.prevent="logout">Logout</button>
  </div>
</template>
