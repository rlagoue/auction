<template>
  <div class="card">
    <input class="form-input mb-4" placeholder="filter" type="text">
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
    <div class="my-6 flex items-center justify-center space-x-2">
      <button
          v-show="state.hasPrevious"
          class="nav-button"
          @click="moveBefore"
      >
        <img alt="" src="../../assets/images/navigate_before_black_24dp.svg">
      </button>
      <span>{{ state.currentPageIndex }}</span>
      <button
          v-show="state.hasNext"
          class="nav-button"
          @click="moveNext"
      >
        <img alt="" src="../../assets/images/navigate_next_black_24dp.svg">
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import {onBeforeMount, reactive, watchEffect} from "vue";
import {useStore} from "../../store";
import {Item} from "../../domain/Item";
import ItemPanel from "../../components/ItemPanel.vue";

type State = {
  items: Item[],
  currentPageIndex: number,
  hasNext: boolean,
  hasPrevious: boolean,
  pagesCount: number,
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
      currentPageIndex: 1,
      hasNext: false,
      hasPrevious: false,
      pagesCount: 0,
    });

    onBeforeMount(async () => {
      store.setCurrentPage("Items");
    });

    watchEffect(async () => {
      state.items = await store.fetchItems(state.currentPageIndex);
      state.pagesCount = Math.floor(Item.totalCount / 10);
      state.hasNext = state.currentPageIndex < state.pagesCount;
      state.hasPrevious = state.currentPageIndex > 1;
    });

    const moveBefore = () => state.currentPageIndex--;
    const moveNext = () => state.currentPageIndex++;

    return {
      state,
      moveBefore,
      moveNext,
    };
  }
}
</script>

<style scoped>
.nav-button {
  @apply border border-4 p-4 rounded border-green-600;
}
</style>