<template>
  <div class="space-y-10">
    <div class="card">
      <div class="flex items-center space-x-2">
        <div class="w-1/3 rounded-md overflow-hidden">
          <img
              alt=""
              src="../../assets/images/item_default.jpeg"
          >
        </div>
        <div class="w-full">
          <div class="form-label">Name</div>
          <div
              class=""
              data-test-id="name"
          >
            {{ state.item.name }}
          </div>
          <div class="form-label">Description</div>
          <div
              class=""
              data-test-id="description"
          >
            {{ state.item.description }}
          </div>
          <div class="form-label">Current Bid</div>
          <div
              class=""
              data-test-id="currentBid"
          >
            {{ currentBid }}
          </div>
          <div class="form-label">Start Bid</div>
          <div
              class=""
              data-test-id="startBid"
          >
            {{ startBid }}
          </div>
        </div>
      </div>
    </div>
    <div
        class="card"
        data-test-id="bids"
    >
      <div class="uppercase text-xl font-bold pb-4">
        Bids
      </div>
      <table
          class="w-full text-center"
          data-test-id='bids'
      >
        <thead>
        <tr class="uppercase font-bold">
          <th>Bidder</th>
          <th>Time</th>
          <th>Amount</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="bid in state.item.bids"
            :key="bid"
            class="my-10 py-10"
            :data-test-id="bid.user.username"
        >
          <td data-test-id="bidder">
            {{ bid.user.username }}
          </td>
          <td data-test-id="time">
            {{ utcDateTimeToLocalString(bid.time) }}
          </td>
          <td data-test-id="amount">
            {{ bid.amount.toString() }}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import {computed, onBeforeMount, reactive} from "vue";
import {Item} from "../../domain/Item";
import {useStore} from "../../store";
import {useRoute} from "vue-router";
import {Money} from "../../domain/Money";
import {useDateTimeUtils} from "../../utils";

type State = {
  item: Item,
}

export default {
  name: "Details",
  setup() {
    const state = reactive<State>({
      item: Item.Null,
    });

    const store = useStore();
    const route = useRoute();

    onBeforeMount(async () => {
      store.setCurrentPage("Item Details")
      state.item = await store.fetchItemById(route.params.id as string);
    })

    const currentBid = computed<string>(
        () => {
          if (state.item.isNull()) {
            return Money.Null.toString();
          }
          return state.item.getCurrentBid().toString();
        }
    );

    const startBid = computed<string>(
        () => {
          if (state.item.isNull()) {
            return Money.Null.toString();
          }
          return state.item.getStartBid().toString();
        }
    );

    const {utcDateTimeToLocalString} = useDateTimeUtils();

    return {
      state,
      currentBid,
      startBid,
      utcDateTimeToLocalString,
    }
  }
}
</script>

<style scoped>

</style>