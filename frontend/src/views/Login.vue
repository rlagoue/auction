<template>
  <div class="flex justify-center items-center h-screen">
    <div class="flex flex-col items-center justify-center bg-white card">
      <div class="grid grid-cols-1 space-y-3">
        <div class="text-center uppercase font-bold text-2xl">
          Login
        </div>
        <div v-if="state.errors.length > 0">
          <b>Please correct the following error(s):</b>
          <ul class="text-red-700">
            <li v-for="error in state.errors">
              {{ error }}
            </li>
          </ul>
        </div>
        <input
            v-model="state.username"
            class="form-input"
            data-test-id="username"
            placeholder="username"
            required
            type="text"
            @keypress.enter="trySubmit"
        >
        <input
            v-model="state.password"
            class="form-input"
            data-test-id="password"
            placeholder="password"
            required
            type="password"
            @keypress.enter="trySubmit"
        >
        <button
            class="p-2 border-2 rounder-md bg-green-200 font-bold uppercase"
            data-test-id="submit"
            type="button"
            @click="trySubmit"
        >
          Submit
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent, reactive} from "vue";
import {useStore} from "../store";
import {useRouter} from "vue-router";

export default defineComponent({
  name: "Login",
  setup() {
    const state = reactive({
      username: "",
      password: "",
      errors: [],
    });
    const router = useRouter();

    const trySubmit = async () => {
      state.errors.length = 0;
      if (!state.username) {
        state.errors.push("Username is required");
      }
      if (!state.password) {
        state.errors.push("Password is required");
      }
      if (state.errors.length === 0) {
        await useStore().signIn(state.username, state.password);
        await router.push("/items-list");
      }
    };

    return {
      state,
      trySubmit
    };
  }
});
</script>

<style scoped>

</style>