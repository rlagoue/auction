<template>
  <div>
    <div class="">
      <TheTopBar/>
    </div>
    <div class="h-screen pl-4 pt-4 flex-col">
      <div
          class="h-16 text-xl font-bold uppercase"
          data-test-id="page-title"
      >
        {{ title }}
      </div>
      <router-view/>
    </div>
  </div>
</template>

<script>
import {computed, defineComponent} from "vue";
import TheTopBar from "../components/TheTopBar.vue";
import {useStore} from "../store";

export default defineComponent({
  name: "Desktop",
  components: {
    TheTopBar,
  },
  setup() {
    let store = useStore();
    const title = computed(() => store.title)
    return {
      title
    };
  }
});
</script>

<style scoped>

</style>