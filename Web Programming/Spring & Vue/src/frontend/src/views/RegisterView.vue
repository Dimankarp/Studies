<script setup>
import RegisterForm from '@/components/RegisterForm.vue';
import {useRouter} from 'vue-router'

const router = useRouter()

function redirectToShotPage(){
    console.log("Redirecting to shopt page!")
    router.push("/shot")
}

</script>

<template>
  <RegisterForm @registration-success="redirectToShotPage"/>
  <router-link to="/login" class="login-link">
    or login! </router-link>
</template>
