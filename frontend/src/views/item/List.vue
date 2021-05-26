<template>
  <div class="card">
    <input type="text" class="form-input mb-4" placeholder="filter">
    <div
        class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 space-y-3"
        data-test-id="items-list"
    >
      <ItemPanel
          v-for="item in state.items"
          :key="item.id"
          :item="item"
      />
    </div>
  </div>
</template>

<script lang="ts">
import {onBeforeMount, onMounted, reactive} from "vue";
import {useStore} from "../../store";
import {Item} from "../../domain/Item";
import ItemPanel from "../../components/ItemPanel.vue";

interface State {
  items: Item[],
}

export default {
  name: "ItemsList",
  components: {
    ItemPanel,
  },
  setup() {
    const store = useStore();

    const state = reactive<State>({
      items: [],
    });

    onBeforeMount(async () => {
      store.setCurrentPage("Items");
      state.items = await store.fetchItems(1);
    });
    return {
      state,
    };
  }
}
</script>

<style scoped>

</style>