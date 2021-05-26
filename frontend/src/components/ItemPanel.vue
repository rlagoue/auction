<template>
  <div
      class="flex flex-col items-center justify-center"
      :data-test-id="item.id"
  >
    <div class="rounded-md overflow-hidden">
      <img
          src="../assets/images/item_default.jpeg"
          alt=""
      >
    </div>
    <div data-test-tag="name">
      {{item.name}}
    </div>
    <div class="text-xl font-bold">
      $ 100
    </div>
    <div class="text-sm">
      $ 20
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import {Item} from "../domain/Item";

export default defineComponent({
  name: "ItemPanel",
  props: {
    item: {
      type: Object as PropType<Item>,
      required: true,
    }
  },
  setup() {
    return {
    }
  },
});
</script>

<style scoped>

</style>