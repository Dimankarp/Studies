<script setup>
import '@/assets/css/vue-inputs.scss'
import '@/assets/css/shot-page.css'
import {ref} from "vue";

defineProps({
  to: String,
})

const registerTitle = ref("Please, enter credentials!")
const submitButtonText = ref("Register")

const formInProgress = ref(false);

const username = ref("")
const password = ref("")

function register(event){
  formInProgress.value = true;

  let formData = new FormData();
  formData.append("username", username.value)
  formData.append("password", password.value)

  fetch("/api/auth/register",
      {
        method: "POST",
        headers:{
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body: formData
      }).then((response)=> {
    if (response.status === 200) {
      //Transfer to main page and get token
      this.$emit("registrationSuccess")
    } else if (response.status === 409) {
      registerTitle.value = "User with such username is already registered!"
    }
  }).catch((err)=>{
    registerTitle.value = "Something went wrong!"
  }).finally((res)=> {
    formInProgress.value = false
  })
}

</script>

<template>outputText
  <div class="form-container">
    <form class="coords-form" :disabled="formInProgress">
      <h1>{{registerTitle}}</h1>
      <div class="coords-input-pair">
        <label for="username-textbox" value="Username:"/>
        <input id="username-textbox" type="text" placeholder="Username"  name="username" v-model="username" required="true" maxlength="33"/>
      </div>
      <div class="coords-input-pair">
        <label for="password-textbox" value="Password:"/>
        <input id="password-textbox" type="text" placeholder="Password"  name="password" v-model="password" required="true" maxlength="33"/>
      </div>

      <button @click.prevent="register">{{submitButtonText}}</button>
    </form>
  </div>
</template>
