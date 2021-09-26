<template>
  <ul class="tab-selector-list">
    <li v-for="(tab, index) in tabs" :key="index">
      <button @click="selectTab(tab.name)"
              :class="`tab-selector ${selected === tab.name ? 'tab-selector-active' : ''}`">
        {{ tab.text }}
      </button>
    </li>
  </ul>
</template>

<script lang=ts>
export default ({
  props: {
    tabs: Array,
    selected: String,
  },
  emits: ['tabSelected'],
  setup(_, { emit }) {
    const selectTab = (name) => {
      emit('tabSelected', name);
    };
    return {
      selectTab,
    };
  },
});
</script>

<style scoped lang="sass">
.tab-selector-list::v-deep
  display: flex

.tab-selector::v-deep
  margin-right: 30px
  background-color: transparent
  border-bottom: 4px solid transparent
  color: var(--secondary-normal)
  font-weight: bold
  font-size: 16px
  padding: 8px 20px

.tab-selector-active::v-deep
  border-bottom: 4px solid var(--secondary-normal)

</style>
