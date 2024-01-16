<script setup>
import '@/assets/css/vue-inputs.scss'
import '@/assets/css/login.scss'

import { ref } from "vue";
import { usernameIsValid, passwordIsValid, passwordsMatch } from "@/assets/js/input_validation.js"
import {registerToken} from "@/assets/js/auth.js"

const emit = defineEmits(['registrationSuccess'])

const registerTitle = ref("Please, enter credentials to register!")
const submitButtonText = ref("Register")

const formInProgress = ref(false);

const username = ref("")
const password = ref("")
const repeat_password = ref("")

const usernameErrorText = "Username must be an ASCII string shorter than 35 symbols."
const passwordErrorText = "Password must be longer than 10 and shorter than 35 symbols."
const passwordDontMatchErrorText = "Password don't match."

const usernameIsInvalid = ref(false);
const passwordIsInvalid = ref(false);
const matchingIsInvalid = ref(false);


function validateForm() {
  let result = true
  if (!usernameIsValid(username.value)) {
    usernameIsInvalid.value = true;
    result = result && false
  } else {
    usernameIsInvalid.value = false;
  }

  if (!passwordIsValid(password.value)) {
    passwordIsInvalid.value = true;
    result = result && false
  } else {
    passwordIsInvalid.value = false;
  }

  if (!passwordsMatch(password.value, repeat_password.value)) {
    matchingIsInvalid.value = true;
    result = result && false
  } else {
    matchingIsInvalid.value = false;
  }

  return result
}

function register(event) {
  if (!validateForm()) {
    return;
  }

  formInProgress.value = true;

  let urlEncoded = new URLSearchParams();
  urlEncoded.append("username", username.value);
  urlEncoded.append("password", password.value);

  fetch("/api/auth/register",
    {
      method: "POST",
      body: urlEncoded,
      credentials: "same-origin"
    }).then(async (response) => {
      if (response.ok) {

       let loginSuccess = await fetch("/api/auth/token",
          {
            method: "POST",
            body: urlEncoded,
            credentials: "same-origin"
          }).then(async (response) => {
            if (response.ok) {

             let token = await response.text()
              registerToken(token, username.value)
              return true;
            }
            else { return false; }
          })
        if (loginSuccess) {
          emit("registrationSuccess")
        } else {
          registerTitle.value = "Registered but couldn't login!"
        }
      }
      else if (response.status === 409) {
        registerTitle.value = "User with such username is already registered!"
      }
    }).catch((err) => {
      registerTitle.value = "Something went wrong!"
      submitButtonText.value = "Try again!"
    }).finally((res) => {
      formInProgress.value = false
    })
}



</script>

<template>
  <div class="form-container">
    <form class="register-form" :disabled="formInProgress" novalidate="true">
      <h1>{{ registerTitle }}</h1>
      <div class="input-pair">
        <label for="username-textbox">Username:</label>
        <input @input="validateForm()" :class="{ invalid: usernameIsInvalid }" type="text" placeholder="Username"
          name="username" v-model="username" required="true" maxlength="33" />
        <span class="input-pair_errormsg" v-if="usernameIsInvalid">{{ usernameErrorText }}</span>
      </div>
      <div class="input-pair">
        <label for="password-textbox">Password:</label>
        <input @input="validateForm()" :class="{ invalid: passwordIsInvalid }" type="password" name="password"
          v-model="password" required="true" maxlength="35" />
        <span class="input-pair_errormsg" v-if="passwordIsInvalid">{{ passwordErrorText }}</span>
      </div>
      <div class="input-pair">
        <label for="password-textbox">Repeat password:</label>
        <input @input="validateForm()" :class="{ invalid: matchingIsInvalid }" type="password" v-model="repeat_password"
          required="true" maxlength="35" />
        <span class="input-pair_errormsg" v-if="matchingIsInvalid">{{ passwordDontMatchErrorText }}</span>
      </div>
      <button @click.prevent="register">{{ submitButtonText }}</button>
    </form>
  </div>
</template>
