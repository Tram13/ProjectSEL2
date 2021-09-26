<template>
  <div class="content_wrapper">
    <div class="content_container_wrapper">
        <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  setup() {
    return {};
  },
});
</script>

<style scoped lang="sass">
a.router-link-active .nav-link-content-wrapper
  background-color: #d9c41a

.nav-link-container
  width: 100%
  height: 60px
  display: flex
  align-items: center
  transition: 0.15s ease

.nav-link-content-wrapper
  position: relative
  flex: 1
  height: 50px
  margin: 0 20px
  border-radius: 7px
  overflow: hidden

.nav-link-content
  height: 100%
  position: absolute
  display: flex
  align-items: center

.nav-icon-container
  width: 50px
  height: 50px
  display: flex
  place-content: center

  img
    width: 25px
    transition: 0.25s ease

.nav-label
  cursor: pointer
  color: #3f3f3f
  white-space: pre
  transition: 0.25s ease
  margin-left: 7px
  font-size: 17

.nav-link-container:hover
  .nav-label
    font-size: 19px

  img
    width: 30px

.content_wrapper
  display: flex
  width: 100%
  height: calc(100vh - 43px)

.content_container_wrapper
  display: flex
  flex-direction: column
  flex: 1
  overflow-y: auto
  justify-content: space-between
  background-color: #ffffff

  &::-webkit-scrollbar
    width: 0.7rem

  &::-webkit-scrollbar-track
    background-color: rgb(223, 223, 223)

  &::-webkit-scrollbar-thumb
    background-color: rgb(134, 134, 134)

.content_container
  flex: 1
  padding: 30px 6% 6%
</style>
