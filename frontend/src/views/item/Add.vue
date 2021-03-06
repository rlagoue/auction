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
        @keypress.enter="trySubmit"
    >
    <textarea
        v-model.trim="state.description"
        class="form-input"
        data-test-id="item-add-description"
        placeholder="Description"
        required
        @keypress.enter="trySubmit"
    />
    <div class="flex items-center justify-center-center space-x-2">
      <button
          class="mt-4 p-2 border-2 rounder-md bg-green-200 font-bold uppercase"
          data-test-id="item-add-submit"
          type="button"
          @click="trySubmit"
      >
        Submit
      </button>
      <button
          class="mt-4 p-2 border-2 rounder-md bg-white font-bold uppercase text-red-700"
          data-test-id="item-add-cancel"
          type="button"
          @click="cancel"
      >
        Cancel
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, onBeforeMount, reactive} from "vue";
import {useStore} from "../../store";
import {useRouter} from "vue-router";

type State = {
  name: string,
  description: string,
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
      if (state.errors.length === 0) {
        const newItemId = await store.addItem(
            state.name,
            state.description,
        );
        await router.push("/item-details/" + newItemId);
      }
    };

    const cancel = async () => router.push("/items-list");

    return {
      state,
      trySubmit,
      cancel,
    }
  }
})
</script>

<style scoped>

</style>