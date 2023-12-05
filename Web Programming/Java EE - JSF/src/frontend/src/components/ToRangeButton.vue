<script setup>
import '@/assets/css/vue-inputs.scss'

defineProps({
  to: String,
})

</script>

<template>
  <router-link :to="to" custom v-slot="{ navigate }">
    <input type="button" class="big-button centered" @click="navigate" value="Get to the Range"/>
  </router-link>
</template>
