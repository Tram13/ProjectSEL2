<script>
import { h, ref } from 'vue';
import DataTableRow from '@/components/elements/DataTableRow.vue';
import PaginationBar from '@/components/elements/PaginationBar.vue';
import SearchFilterBar from '@/components/elements/SearchFilterBar.vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import DataTableRowLoading from '@/components/elements/DataTableRowLoading.vue';
import SortArrow from '@/assets/arrow_down.svg';

export default {
  components: {
    DataTableRow,
    PaginationBar,
    SearchFilterBar,
    PulseLoader,
    DataTableRowLoading,
  },
  props: {
    headers: Object, // list of all headers
    entries: Object, // list of all entries
    updateFunction: Function, // function that will update entries and return the total entries
    filters: { type: Object, required: false },
    filterPlaceholder: String,
    generalSearchParameter: String,
    loading: Boolean,
    height: String,
  },
  setup(props) {
    const currentPage = ref(1);
    const totalEntries = ref(0);
    let amount;
    // get preference of viewamount
    const currentViewAmountString = localStorage.getItem('paginationViewAmount');
    if (currentViewAmountString) {
      amount = JSON.parse(currentViewAmountString);
    } else {
      amount = 5;
    }
    const currentViewAmount = ref(amount);

    const sortBy = ref(props.headers[0].propName);
    const sortReverse = ref(false);
    const filterOptions = ref({});

    const refreshPage = () => props.updateFunction(
      currentViewAmount.value * (currentPage.value - 1),
      currentViewAmount.value,
      sortBy.value,
      sortReverse.value,
      filterOptions.value,
    );

    // when there is a pageupdate
    const updatePage = (newPage) => {
      // set the new page
      currentPage.value = newPage;
      // update the entries
      refreshPage().then((total) => { totalEntries.value = total; });
    };

    // when the amount per page changes
    const updateAmount = (newAmount) => {
      // the current page will need to change
      if (newAmount > currentViewAmount.value) {
        currentPage.value = Math.floor(
          (currentPage.value * currentViewAmount.value) / newAmount, 10,
        ) + 1;
      } else {
        currentPage.value = Math.floor(
          ((currentPage.value - 1) * currentViewAmount.value) / newAmount, 10,
        ) + 1;
      }
      // set the new amount
      currentViewAmount.value = newAmount;
      // safe in localstorage
      localStorage.setItem('paginationViewAmount', JSON.stringify(newAmount));
      // updat the entries
      refreshPage().then((total) => { totalEntries.value = total; });
    };

    // when the sorting changes
    const changeSorting = (key) => {
      // is key == current key => reverse sort order
      if (key === sortBy.value) {
        sortReverse.value = !sortReverse.value;
      } else {
        sortBy.value = key;
        sortReverse.value = false;
      }
      // update the pagination back to page 1 (also for update)
      updatePage(1);
    };

    const changeFiltering = (filterOps) => {
      filterOptions.value = filterOps;
      updatePage(1);
    };

    // call update for first entries and get the the total amount of entries
    if (props.updateFunction) {
      refreshPage().then((total) => { totalEntries.value = total; });
    }

    return {
      refreshPage,
      currentPage,
      currentViewAmount,
      updatePage,
      totalEntries,
      sortBy,
      sortReverse,
      changeSorting,
      updateAmount,
      filterOptions,
      changeFiltering,
    };
  },
  render() {
    const genRow = (entry, index) => h(DataTableRow, {
      entry,
      headers: this.headers,
      index,
    },
    this.$slots);
    const genFilter = (filters) => h(
      SearchFilterBar,
      {
        filters,
        placeholder: this.filterPlaceholder,
        generalSearchParameter: this.generalSearchParameter,
        onFiltersChanged: (filterOps) => this.changeFiltering(filterOps),
      },
    );

    const genRows = (entries) => {
      if (!this.loading) {
        return (entries.map((entry, item) => genRow(entry, item)));
      }
      const rows = [];
      for (let i = 0; i < this.currentViewAmount; i += 1) {
        rows.push(h(DataTableRowLoading, { headers: this.headers }));
      }
      return (rows);
    };
    const genBody = (entries) => h('tbody', {}, genRows(entries));
    const genSort = () => h(
      'div',
      { class: 'sort-arrow-container' },
      h('img', {
        class: this.sortReverse ? 'arrow-up' : '',
        src: SortArrow,
        alt: 'arrow-right',
      }),
    );
    const genHeaderElem = (header) => {
      const children = [header.name];
      if (header.propName === this.sortBy) {
        children.push(genSort());
      }
      return (h(
        'th',
        {
          onClick: () => this.changeSorting(header.propName),
        },
        children,
      ));
    };
    const genHeader = (headers) => h(
      'tr',
      {
        class: [
          'text-align-left',
        ],
      },
      headers.map((header) => genHeaderElem(header)),
    );
    const genHead = (headers) => h('thead', {}, [genHeader(headers)]);
    const genTable = () => h(
      'div',
      { },
      h('table', { class: ['responsive-table'] }, [genHead(this.headers), genBody(this.entries)]),
    );

    // <pagination-bar @page_update="updatePage" @amount_update="updateAmount"/>
    const genPaginationBar = () => h(
      PaginationBar,
      {
        currentPage: this.currentPage,
        currentAmount: this.currentViewAmount,
        totalEntries: this.totalEntries,
        onPage_update: (val) => this.updatePage(val),
        onAmount_update: (newAmount) => this.updateAmount(newAmount),
      },
    );

    const genTableWrapperDiv = () => {
      const children = [genTable()];
      if (this.updateFunction) {
        children.push(genPaginationBar());
      }
      return (h(
        'div',
        { class: 'scroll-container', style: `height: ${this.height}` },
        children,
      ));
    };

    const genDataTableWrapper = () => {
      const children = [genTableWrapperDiv()];
      if (this.filters !== undefined) {
        children.unshift(genFilter(this.filters));
      }
      return (h(
        'div',
        { class: 'table-wrapper' },
        children,
      ));
    };

    return genDataTableWrapper();
  },
};
</script>

<style lang="sass">
.table-wrapper
  width: 100%

.scroll-container
  overflow-y: auto

.sort-arrow-container
  height: 100%
  width: 20px
  display: inline-block
  margin-left: 10px
  cursor: pointer

  img
    height: 12px
    transition: transform 0.1s ease-in-out

.arrow-up
  -webkit-transform: rotate(180deg)
  -moz-transform: rotate(180deg)
  -o-transform: rotate(180deg)
  -ms-transform: rotate(180deg)
  transform: rotate(180deg)

.text-align-left
  text-align: left

table
  border-collapse: collapse
  border: none
  width: 100%

td
  animation: fade-in 0.5s
  padding: 13px 25px

th
  background-color: #f5f5f5
  padding: 20px 25px
  position: sticky
  top: 0

tbody:before
  content: ""
  display: block
  height: 20px

thead
  color: grey
  margin-bottom: 20px
  font-size: 14px
  border-collapse: separate
  border-spacing: 60px

  th
    &:first-child
      border-radius: 5px 0 0 5px

    &:last-child
      border-radius: 0 5px 5px 0

tbody
  td
    &:first-child
      border-radius: 5px 0 0 5px

    &:last-child
      border-radius: 0 5px 5px 0
      text-align: right

  tr:hover
    background-color: #f5f5f5

@keyframes fade-in
  from
    opacity: 0
  to
    opacity: 1
</style>
