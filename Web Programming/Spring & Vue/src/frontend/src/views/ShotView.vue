<script setup>
import LoginForm from '@/components/LoginForm.vue';
import { onBeforeMount, ref, watch, onMounted } from "vue";
import { isLoggedIn, logout, launchWithAuthCycle, getUsername, getToken } from '@/assets/js/auth';
import ShotForm from '../components/ShotForm.vue';
import CanvasTarget from '@/components/CanvasTarget.vue'
import {getDateFromTimestamp} from "@/assets/js/utils"

onBeforeMount(() => {
  //Also redirects to the login page.
  if (!isLoggedIn()) logout();
})

const coords = ref(JSON.parse(window.localStorage.getItem("lastCoordsInput")) ?? { x: 0, y: 0, radius: 1 })
const hitHeader = ref(null)
const lastShot = ref(null)
const shots = ref([]);

onMounted(()=>{
  fetchShots();
})

watch(lastShot, (newVal) => {
  console.log(hitHeader.value)
  if (hitHeader.value != null) {
    hitHeader.value.focus()
  }
})

watch(coords, (newVal) => {
  window.localStorage.setItem("lastCoordsInput", JSON.stringify(newVal))
})


/*
Returns true if token refreshment required.
*/
async function tryFetchingShots() {
  try {
    let response = await fetch(`/api/shots/`,
      {
        method: "GET",
        headers: {
          Authorization: "Bearer " + getToken()
        },
        credentials: "same-origin"
      })

    if (response.ok) {
      let shotsArr = await response.json();
      if (shotsArr) {
          console.log(shotsArr);
          //Sorting from earliest to oldst
          shotsArr.sort((a, b)=>(b.timeStamp-a.timeStamp))
          shots.value = shotsArr;
      }
      return false;

    } else if (response.status === 401) {
      return true;
    } else {

      return false
    }
  }
  catch (err) {

    console.log(err);
    return false;
  } finally {

  }
}

async function fetchShots() {
  launchWithAuthCycle(tryFetchingShots);
}

async function onPostSuccess(newLastShot){
  lastShot.value = newLastShot;
  fetchShots();
}



</script>

<template>
  <h1 ref="hitHeader" v-if="lastShot != null">{{ lastShot.hit ? "That's a hit!" : "You've missed!" }}</h1>
  <ShotForm v-model:coords="coords" @shot-post-success="onPostSuccess"></ShotForm>

  <div class="canvas-container">
      <CanvasTarget v-model:coords="coords" @shot-post-success="onPostSuccess" :shots="shots"></CanvasTarget>      
    </div>

  <div id="record-table" class="record-table-container">
  <table>
    <thead>
    <tr>
      <th>Is Hit</th>
      <th>X</th>
      <th>Y</th>
      <th>Radius</th>
      <th>Time</th>
    </tr>
  </thead>
  <tbody>
    <tr v-for="shot in shots">
        <td>{{ shot.hit ? "Yes!" : "No." }}</td>
        <td>{{ shot.x }}</td>
        <td>{{ shot.y}}</td>
        <td>{{ shot.radius}}</td>
        <td>{{ getDateFromTimestamp(shot.timeStamp) }}</td>
    </tr>
  </tbody>
  </table>
</div>



</template>
