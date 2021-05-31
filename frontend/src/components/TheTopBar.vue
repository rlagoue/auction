<template>
  <div class="w-full flex items-center justify-between bg-white py-8 px-4 shadow-md">
    <div></div>
    <button
        v-if="!isAdmin"
        class="nav-button font-bold uppercase my-2"
        data-test-id="settings"
        @click="menuClicked"
    >
      {{ menuText }}
    </button>

    <div class="cursor-pointer">
      <img
          alt=""
          data-test-id="logout"
          src="../assets/images/logout_black_24dp.svg"
          @click="logout"
      >
    </div>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent} from 'vue'
import {useStore} from "../store";
import {useRouter} from "vue-router";

export default defineComponent({
  name: "TheTopBar",
  props: {},
  setup: () => {
    const router = useRouter();
    const store = useStore();
    const logout = async () => {
      await store.logout();
      await router.push("/");
    };

    const isAdmin = computed<boolean>(() => store.isAdmin);
    const menuClicked = () => {
      if (isOnSettingsPage.value) {
        router.push("/items-list")
      } else {
        router.push("/auto-bid-settings")
      }
    };
    const isOnSettingsPage = computed<boolean>(
        () => store.currentPage.title === "Settings"
    );
    const menuText = computed<string>(() =>
        isOnSettingsPage.value ? "Home" : "Settings"
    );

    return {
      logout,
      isAdmin,
      menuClicked,
      menuText,
    };
  }
})
</script>

<style scoped>
</style>
