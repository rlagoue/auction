<template>
  <div class="card space-y-5">

    <div v-if="state.errors.length > 0">
      <b>Please correct the following error(s):</b>
      <ul class="text-red-700">
        <li v-for="error in state.errors">
          {{ error }}
        </li>
      </ul>
    </div>

    <div>
      <div class="form-label">
        Max Bid Amount:
      </div>
      <div class="flex items-center space-x-2">
        <input
            v-model="state.maxBidAmount"
            class="form-input"
            data-test-id="settings-auto-bid-max-amount"
            type="number"
        >
        <span>$</span>
      </div>
    </div>
    <button
        class="nav-button"
        data-test-id="settings-submit"
        @click="trySubmit"
    >
      Submit
    </button>
  </div>
</template>

<script>
import {onBeforeMount, reactive} from "vue";
import {useStore} from "../store";

export default {
  name: "Settings",
  setup() {
    const state = reactive({
      maxBidAmount: 0,
      errors: [],
    });

    const store = useStore();

    onBeforeMount(async () => {
      store.setCurrentPage("Settings");
      const settings = await store.fetchSettings();
      state.maxBidAmount = settings.maxBidAmount.value;
    });

    const trySubmit = async () => {
      state.errors.length = 0;
      if (state.maxBidAmount < 0) {
        state.errors.push("Max bid amount cannot be negative");
      }
      if (state.errors.length === 0) {
        await store.saveSettings(
            {
              maxBidAmount: {
                value: state.maxBidAmount,
                currency: "USD",
              }
            }
        );
      }
    };

    return {
      state,
      trySubmit,
    }
  }
}
</script>

<style scoped>

</style>