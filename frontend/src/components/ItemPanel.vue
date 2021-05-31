<template>
  <div
      class="flex flex-col items-center justify-center cursor-pointer hover:border-2 hover:border-green-600"
      @click="showDetails"
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
      {{ currentBidAsString }}
    </div>
    <div class="text-sm">
      {{ startBidAsString }}
    </div>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent, PropType} from "vue";
import {Item} from "../domain/Item";
import {useRouter} from "vue-router";
import {Money} from "../domain/Money";

export default defineComponent({
  name: "ItemPanel",
  props: {
    item: {
      type: Object as PropType<Item>,
      required: true,
    }
  },
  setup(props) {
    const router = useRouter();
    const showDetails = () => {
      router.push("/item-details/" + props.item.id)
    }

    const startBidAsString = computed<string>(() =>
        computeBidString(props.item.getStartBid()));

    const currentBidAsString = computed<string>(() =>
        computeBidString(props.item.getCurrentBid()));

    const computeBidString = (bid: Money) => {
      if (bid === Money.Null) {
        return "N/A";
      }
      return bid.toString();
    }

    return {
      showDetails,
      startBidAsString,
      currentBidAsString,
    }
  },
});
</script>

<style scoped>

</style>