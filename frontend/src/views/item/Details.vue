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
            {{ currentBidAsString }}
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
      <div v-if="!state.bidFormDisplayed">
        <button
            class="nav-button font-bold uppercase my-2 mr-2"
            data-test-id="make-bid"
            @click="startMakingBid"
        >
          Make Bid
        </button>
        <button
            v-if="state.item.isAutoBidActive"
            class="nav-button font-bold uppercase my-2"
            data-test-id="deactivate-auto-bid"
            @click="deactivateAutoBid"
        >
          Deactivate Auto Bid
        </button>
        <button
            v-else
            class="nav-button font-bold uppercase my-2"
            data-test-id="activate-auto-bid"
            @click="activateAutoBid"
        >
          Activate Auto Bid
        </button>
      </div>
      <div
          v-if="state.bidFormDisplayed"
          class="flex items-center justify-center space-x-4"
      >
        <input
            v-model="newBid"
            class="form-input"
            data-test-id="make-bid-input"
            type="number"
        >
        <button
            class="nav-button font-bold uppercase my-2"
            data-test-id="make-bid-submit"
            @click="makeBid"
        >
          Submit
        </button>
      </div>
      <div class="flex center-items justify-center">
        <span
            v-if="state.biddingFeedbackMessage"
            class="text-2xl text-red-700 font-bold"
            data-test-id="make-bid-error-message"
        >
          {{ state.biddingFeedbackMessage }}
        </span>
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
            :key="bid.id"
            :data-test-id="bid.user.username"
            class="my-10 py-10"
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
import {computed, onBeforeMount, reactive, ref} from "vue";
import {Item} from "../../domain/Item";
import {useStore} from "../../store";
import {useRoute} from "vue-router";
import {Money} from "../../domain/Money";
import {useDateTimeUtils} from "../../utils";
import {Bid} from "../../domain/Bid";
import {Currency} from "../../domain/Currency";

type State = {
  item: Item,
  bidFormDisplayed: boolean,
  biddingFeedbackMessage: string,
}

export default {
  name: "Details",
  setup() {
    const store = useStore();
    const route = useRoute();
    const itemId = route.params.id as string;

    const state = reactive<State>({
      item: Item.Null,
      bidFormDisplayed: false,
      biddingFeedbackMessage: "",
    });

    onBeforeMount(async () => {
      store.setCurrentPage("Item Details")
      await fetchData();
    });

    const fetchData = async () =>
        state.item = await store.fetchItemById(itemId);

    const currentBid = computed<Money>(
        () => {
          if (state.item.isNull()) {
            return Money.Null;
          }
          return state.item.getCurrentBid();
        }
    );

    const currentBidAsString = computed<string>(
        () => currentBid.value.toString()
    );

    const startBid = computed<string>(
        () => {
          if (state.item.isNull()) {
            return Money.Null.toString();
          }
          return state.item.getStartBid().toString();
        }
    );

    const newBid = ref(0);
    const startMakingBid = () => {
      state.bidFormDisplayed = true;
      newBid.value = Math.floor(currentBid.value.value) + 1;
    };

    const {utcDateTimeToLocalString, localDateToUtc} = useDateTimeUtils();

    const clearBiddingFeedbackMessage = () => state.biddingFeedbackMessage = "";
    const makeBid = async () => {
      clearBiddingFeedbackMessage();
      const result = await store.makeBid(itemId, newBid.value);
      if (result === "success") {
        state.item.bids.push(
            new Bid(
                "",
                store.currentUser,
                localDateToUtc(new Date()),
                new Money(newBid.value, Currency.USD)
            )
        );
      } else if (result === "outbidded") {
        displayOutbiddedErroMessage();
        hideBidForm();
        await fetchData();
      } else if (result === "original-state-changed") {
        displayOriginalStateChangedMessage();
        await fetchData();
      } else if (result === "CannotBidManuallyOnItemWithAutoBidActivated") {
        state.biddingFeedbackMessage = "Please Deactivate the Auto Bid for this item, Then you will be able to bid manually again";
        await fetchData();
      } else if (result === "CannotActivateAutoBidWhenBeingLeadingBidder") {
        state.biddingFeedbackMessage = "As long you are the highest bidder on this item, you are not able to activate autobid on it.";
        await fetchData();
      }

    };
    const displayOutbiddedErroMessage = () =>
        state.biddingFeedbackMessage = "!!!You have been outbidded!!!";
    const displayOriginalStateChangedMessage = () =>
        state.biddingFeedbackMessage = "!!! The state of the item has changed, please check the new state before your next att!!!";
    const hideBidForm = () => state.bidFormDisplayed = false;
    const activateAutoBid = async () => {
      await store.activateAutoBid(itemId);
      state.item.isAutoBidActive = true;
    };
    const deactivateAutoBid = async () => {
      await store.deactivateAutoBid(itemId);
      state.item.isAutoBidActive = false;
    };

    return {
      state,
      currentBidAsString,
      startBid,
      utcDateTimeToLocalString,
      startMakingBid,
      newBid,
      makeBid,
      activateAutoBid,
      deactivateAutoBid,
    }
  }
}
</script>

<style scoped>

</style>