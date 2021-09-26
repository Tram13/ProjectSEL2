<template>
  <div class="searchbar_container">

    <div class="input-field">
      <Filter v-for="filter in activeFilters"
              :key="filter.id"
              :text="filter.text"
              :type="filter.type"
              :placeholder="filter.placeholder"
              :selectOptions="filter.options"
              :value="filter.value"
              :v-model:value="filter.value"
              @destroyFilter="removeFilter(filter.id)"
              @filterChanged="filterChanged($event, filter.id)"/>
      <input v-model="query"
             type="text"
             id="fname"
             name="fname"
             :placeholder="placeholder"
             @input="parseSearch"/>
    </div>

    <div class="search-btn">
      <img src="@/assets/search.svg" alt="icon-search"/>
    </div>

    <label for="toggle" class="filter-btn">
      <span>Filter</span>
      <img src="@/assets/arrow_down.svg" alt="icon-search"/>
    </label>
    <input type="checkbox" class="filter-checkbox" id="toggle">

    <div class="filter_container">
      <div class="filter_inner_container">
        <div class="advanced-search">
          <span class="advanced-search-label">Gericht zoeken:</span>
          <div v-for="searchOption in filters"
               :key="searchOption.propName"
               class="advanced-search-option"
               @click="addFilter(searchOption.text, '')">
            {{searchOption.text}}: ..
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Filter from '@/components/elements/filters/FilterTest.vue';
import { ref } from 'vue';

export default {
  components: {
    Filter,
  },
  props: {
    placeholder: String,
    generalSearchParameter: String,
    filters: Array,
  },
  emits: ['filtersChanged'],
  setup(props, { emit }) {
    let filterId = 0;
    const query = ref('');
    const activeFilters = ref([]);

    const filterText = props.filters.map((f) => f.text);
    const filterRegex = new RegExp(`(^| )(${filterText.join('|')}) *:[^ ]*`, 'ig');

    const invalidate = () => {
      const filterOptions = {};
      activeFilters.value.forEach((filter) => {
        if (filter.value !== '') {
          filterOptions[filter.name] = filter.value;
        }
      });
      if (props.generalSearchParameter && query.value) {
        filterOptions[props.generalSearchParameter] = query.value;
      }
      emit('filtersChanged', filterOptions);
    };

    const filterChanged = (value, id) => {
      const filter = activeFilters.value.find((f) => f.id === id);
      filter.value = value;

      invalidate();
    };

    const addFilter = (text, value) => {
      const filter = { ...props.filters.find((f) => f.text.toLowerCase() === text.toLowerCase()) };
      filter.value = value;
      filter.id = filterId;
      filterId += 1;
      activeFilters.value.push(filter);
    };

    const parseFilter = (filterString) => {
      const [filter, FilterContent] = filterString.trim().split(/ *:/);
      addFilter(filter, FilterContent);
      return '';
    };

    const parseSearch = () => {
      query.value = query.value.replace(
        filterRegex,
        parseFilter,
      );
      invalidate();
    };

    const removeFilter = (id) => {
      activeFilters.value = activeFilters.value.filter((f) => f.id !== id);
      invalidate();
    };
    return {
      query,
      activeFilters,
      parseSearch,
      removeFilter,
      addFilter,
      invalidate,
      filterChanged,
    };
  },
};
</script>

<style scoped lang="sass">
.advanced-search
  margin-left: 7px
  margin-bottom: 10px
  margin-top: 5px

.advanced-search-label
  display: block
  margin-bottom: 5px
  font-size: 13px
  font-weight: bold
  color: rgb(136, 136, 136)

.advanced-search-option
  background-color: rgb(245 245 245)
  align-items: center
  justify-content: center
  padding: 3px 10px
  display: inline-block
  border-radius: 5px
  margin: 3px 5px
  font-size: 15px
  cursor: pointer

.searchbar_container
  display: grid
  grid-template: "searchbar searchbtn filter" "filter-options filter-options filter-options"
  grid-template-columns: 1fr auto
  overflow: hidden
  height: auto
  margin-bottom: 20px

.filter_container
  box-sizing: border-box
  grid-area: filter-options
  max-height: 0
  width: 100%
  transition: max-height 0.3s

.filter_inner_container
  padding: 15px 0

.filter-option
  display: flex
  flex-flow: wrap
  align-content: stretch
  align-items: stretch
  justify-content: space-between

.filter_last
  flex: 1
  min-width: 20%
  max-width: 100%

  /*min-height: 50px;
  display: flex
  flex-flow: wrap
  align-items: flex-end
  justify-content: flex-end
  order: 99
  margin: 7px 0

.filter_last_reset
  border: none
  cursor: pointer
  outline: none
  font-size: 17px
  padding: 7px 25px
  border-radius: 10px
  margin: 0 7px
  background: rgb(237, 238, 241)

.filter_last_apply
  border: none
  cursor: pointer
  outline: none
  background-color: rgb(191, 237, 255)
  padding: 7px 25px
  border-radius: 10px
  font-size: 17px
  margin: 0 7px
  min-width: 100px

.input-field
  grid-area: searchbar
  border-radius: 7px 0 0 7px
  width: 100%
  display: flex
  flex-flow: wrap
  align-items: center
  background-color: #f5f5f5
  padding: 5px

  input
    border: none
    outline: none
    height: 40px
    background-color: #f5f5f5
    font-size: 16px
    flex: 1
    padding-left: 15px

.search-btn
  grid-area: searchbtn
  cursor: pointer
  display: flex
  align-items: center
  justify-content: space-around
  background-color: #f5f5f5

  img
    width: 23px
    margin-left: 30px
    margin-right: 30px

  &::after
    content: ""
    width: 1px
    height: 50%
    background-color: rgb(88, 88, 88)
    display: inline-block
    transform: rotate(-20deg)

.filter-checkbox
  display: none

  &:checked ~ .filter_container
    max-height: 1000px

.filter-btn
  grid-area: filter
  border-radius: 0 7px 7px 0
  cursor: pointer
  height: 100%
  display: flex
  align-items: center
  justify-content: space-between
  background-color: #f5f5f5
  span
    margin-left: 30px
    margin-right: 12px
    font-size: 17px
    display: inline-block

  img
    width: 17px
    margin-right: 30px
</style>
