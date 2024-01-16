<script setup>
import {RouterLink, RouterView, useRoute} from 'vue-router'
import ToRangeButton from "@/components/ToRangeButton.vue";
import {computed, onBeforeMount} from "vue";
import RegisterForm from './components/RegisterForm.vue';
import { onStartupAuthorizationCycle } from './assets/js/auth';

const route = useRoute()
const isLogin = computed(() => route.name==="login")

onBeforeMount(()=>{
  if(!isLogin)
  onStartupAuthorizationCycle()
})

</script>

<template>
  <router-view>
  </router-view>
</template>
