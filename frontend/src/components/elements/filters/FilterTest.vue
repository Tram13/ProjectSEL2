<template>
  <div class="filter-container">
    <span class="filter-title">
      {{text}}:
    </span>
    <div class="filter-content">
      <select v-if="type === 'select'"
              id="fname"
              name="fname"
              @input="invalidate">
        <option value="" disabled selected>{{placeholder}}</option>
        <option v-for="option in selectOptions"
                :key="option.value"
                :value="option.value">
          {{option.text}}
        </option>
      </select>
      <div  v-else :class="['text', 'number'].includes(type) ? 'input-container' : ''">
        <label v-if="['text', 'number'].includes(type)">{{inputValue}}</label>
        <input v-model="inputValue"
               :type="type"
               id="fname"
               name="fname"
               :placeholder="placeholder ?? '...'"
               @input="invalidate"/>
      </div>
    </div>
    <div class="filter-cancel" @click="remove()">
      <img src="@/assets/close.svg" alt="icon-cancel"/>
    </div>

  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  props: {
    text: String,
    placeholder: String,
    type: String,
    selectOptions: Array,
    value: String,
  },
  emits: ['destroyFilter', 'filterChanged'],
  setup(props, { emit }) {
    const inputValue = ref(props.value);

    const invalidate = (e) => {
      emit('filterChanged', e.target.value);
    };
    const remove = () => { emit('destroyFilter'); };
    return {
      inputValue,
      remove,
      invalidate,
    };
  },
};
</script>

<style lang="sass">
.input-container
  position: relative

  label, input
    margin: 0
    padding: 2px 2px 2px 7px
    font-size: 16px
    font-family: sans-serif
    box-sizing: border-box

  label
    min-width: 40px
    max-width: 150px
    height: 0
    display: inline-block
    visibility: hidden
    white-space: pre

  input
    width: 100%
    height: 100%
    position: absolute
    top: 0
    left: 0
    border: none
    background: none
    outline: none

.filter-container
  display: flex
  justify-content: space-between
  align-items: center
  height: 32px
  border-radius: 7px
  background-color: #ffffff
  margin: 4px

.filter-cancel
  cursor: pointer
  padding: 10px

  img
    width: 10px

.filter-title
  margin-left: 7px
  font-weight: bold
  font-size: 14px
  grid-area: title
  color: rgb(133, 133, 133)

.filter-content
  flex: 1
  display: flex
  align-items: center
  justify-content: center

  select
    min-width: 50px
    height: 26px
    border: none
    background: none
    outline: none
</style>
