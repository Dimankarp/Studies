<script setup>
import {RouterLink, RouterView, useRoute} from 'vue-router'
import IndexView from "@/views/IndexView.vue";
import ToRangeButton from "@/components/ToRangeButton.vue";
import {computed} from "vue";
import RegisterForm from './components/RegisterForm.vue';

const route = useRoute()
const isShot = computed(() => route.name==="shot")

</script>

<template>
  <form method="post" action="http://localhost:8080/api/auth/register">
    <input type="text" name="username">
  <input type="text" name="password">
  <input type="submit">

  </form>
  <RegisterForm/>
  <template v-if="!isShot">
    <ToRangeButton to="/shot"/>
  </template>

  <router-view>
  </router-view>

</template>
