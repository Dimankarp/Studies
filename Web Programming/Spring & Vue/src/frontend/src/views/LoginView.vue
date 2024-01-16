<script setup>
import LoginForm from '@/components/LoginForm.vue';
import { useRouter } from 'vue-router'
const router = useRouter()

function redirectToShotPage() {
  console.log("Redirecting to shopt page!")
  router.push("/shot")
}

</script>

<template>
  <LoginForm @login-success="redirectToShotPage" />
  <router-link to="/register" class="register-link">
    or register new account! </router-link>
</template>
