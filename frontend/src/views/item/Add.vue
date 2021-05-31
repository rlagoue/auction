<template>
  <div class="card">
    <div v-if="state.errors.length > 0">
      <b>Please correct the following error(s):</b>
      <ul class="text-red-700">
        <li v-for="error in state.errors">
          {{ error }}
        </li>
      </ul>
    </div>
    <input
        v-model.trim="state.name"
        class="form-input"
        data-test-id="item-add-name"
        placeholder="Name"
        required
        type="text"
    >
    <textarea
        v-model.trim="state.description"
        class="form-input"
        data-test-id="item-add-description"
        placeholder="Description"
        required
    />
    <input
      v-model.number="state.startBid"
      class="form-input"
      data-test-id="item-add-bid"
      placeholder="Start Bid in USD"
      required
      type="number"
  >
    <button
        class="mt-4 p-2 border-2 rounder-md bg-green-200 font-bold uppercase"
        data-test-id="item-add-submit"
        type="button"
        @click="trySubmit"
    >
      Submit
    </button>
  </div>
</template>

<script lang="ts">
import {defineComponent, onBeforeMount, reactive} from "vue";
import {useStore} from "../../store";
import {useRouter} from "vue-router";
import {Item} from "../../domain/Item";
import {Bid} from "../../domain/Bid";
import {Currency} from "../../domain/Currency";
import {Money} from "../../domain/Money";

type State = {
  name: string,
  description: string,
  startBid: number,
  errors: string[],
}

export default defineComponent({
  name: "Add",
  setup() {
    const store = useStore();
    const router = useRouter();

    const state = reactive<State>({
      name: "",
      description: "",
      startBid: 0,
      errors: [],
    });

    onBeforeMount(() => {
      store.setCurrentPage("Item Add");
    });

    const trySubmit = async () => {
      state.errors.length = 0;
      if (!state.name) {
        state.errors.push("Name is required");
      }
      if (!state.description) {
        state.errors.push("Description is required");
      }
      if ((state.startBid + "").startsWith("-")) {
        state.errors.push("Start bid must be positive");
      }
      if (state.errors.length === 0) {
        const newItemId = await store.addItem(
              state.name,
              state.description,
              state.startBid
        );
        await router.push("/item-details/" + newItemId);
      }
    };

    return {
      state,
      trySubmit
    }
  }
})
</script>

<style scoped>

</style>