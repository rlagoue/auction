<template>
  <div class="w-full flex items-center justify-end bg-white py-8 px-4 shadow-md">
    <img
        alt=""
        data-test-id="logout"
        src="../assets/images/logout_black_24dp.svg"
        @click="logout"
    >
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import {useStore} from "../store";
import {useRouter} from "vue-router";

export default defineComponent({
  name: "TheTopBar",
  props: {},
  setup: () => {
    const router = useRouter();
    const logout = async () => {
      await useStore().logout();
      await router.push("/");
    };
    return {
      logout,
    };
  }
})
</script>

<style scoped>
</style>
