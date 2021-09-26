<template>
  <div class="paginationbox">
    <div>
      <select id="amount-select" v-model="amount">
        <option value="1">1</option>
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="25">25</option>
        <option value="50">50</option>
        <option value="100">100</option>
      </select>
      <label for="amount-select" class="amount-select-label">/ pagina</label>
    </div>
    <div>
      <button :disabled="disablePrev"
              v-on:click="updatePage(currentPage - 1)"
              class="page-prev-next" type="button">
                Vorige
      </button>
      <button v-if="test.renderFirst"
              v-on:click="updatePage(1)"
              class="page"
              type="button">
                1
      </button>
      <span v-if="test.renderFirst && test.start > 2">&nbsp;...&nbsp;</span>
      <button v-for="index in (test.end - (test.start -1) )"
              type="button"
              v-bind:key="index"
              v-on:click="updatePage(index + (test.start - 1))"
              :class="`page ${currentPage === index + (test.start - 1) ? 'active-page' : ''}`">
        {{index + (test.start - 1)}}
      </button>
      <span v-if="test.renderLast && test.end < lastPage() - 1">&nbsp;...&nbsp;</span>
      <button v-if="test.renderLast"
              type="button"
              v-on:click="updatePage(lastPage())"
              class="page">
                {{lastPage()}}
      </button>
      <button :disabled="disableNext"
              v-on:click="updatePage(currentPage + 1)"
              class="page-prev-next"
              type="button">
                Volgende
      </button>
    </div>
  </div>
</template>

<script lang=ts>
import {
  ref,
  computed,
  watch,
  defineComponent,
} from 'vue';

export default defineComponent({
  emits: ['page_update', 'amount_update'],
  props: {
    currentPage: Number,
    currentAmount: Number,
    totalEntries: Number,
  },
  setup(props, { emit }) {
    const amount = ref(props.currentAmount);
    watch(amount, (newAmount) => emit('amount_update', parseInt(newAmount, 10)));

    const lastPage = () => {
      if (props.totalEntries === 0) { return 1; }
      return Math.ceil(props.totalEntries / props.currentAmount);
    };

    const test = computed(() => {
      let renderFirst;
      let renderLast;
      let start;
      let end;
      if (props.currentPage <= 3) {
        // begin with 1
        start = 1;
        end = Math.min(5, lastPage());
        renderLast = end < lastPage();
      } else if (lastPage() - props.currentPage <= 3) {
        start = Math.max(lastPage() - 4, 1);
        end = lastPage();
        renderFirst = start > 1;
      } else {
        renderFirst = true;
        renderLast = true;
        start = props.currentPage - 1;
        end = props.currentPage + 2;
      }

      return {
        renderFirst,
        renderLast,
        start,
        end,
      };
    });

    const updatePage = (page) => {
      if (page > 0 && page <= lastPage()) {
        emit('page_update', page);
      }
    };

    const disablePrev = computed(() => props.currentPage <= 1);
    const disableNext = computed(() => props.currentPage >= lastPage());

    return {
      test,

      amount,
      updatePage,
      disablePrev,
      disableNext,
      // show,
      lastPage,
    };
  },
});
</script>

<style scoped lang="sass">
#amount-select
  border: 1px solid rgb(187, 187, 187)
  color: rgb(121, 121, 121)
  border-radius: 7px
  outline: none

.amount-select-label
  font-size: 15px
  color: var(--secondary-normal)
  font-weight: bold
  margin-left: 5px

.page-prev-next
  font-size: 12px
  font-weight: bold
  background-color: transparent
  color: var(--secondary-normal)

  &:disabled
    color: var(--secondary-disabled)

.page
  padding: 5px 9px
  border: none
  background-color: transparent
  outline: none
  border-radius: 5px

.active-page
  border: 2px solid var(--secondary-normal)
  color: var(--secondary-normal)
  font-weight: bold

.paginationbox
  display: flex
  justify-content: space-between
  margin-top: 30px
</style>
