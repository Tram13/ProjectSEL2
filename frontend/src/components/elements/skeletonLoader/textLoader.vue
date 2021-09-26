<template>
  <div class="test">
    <span v-for="index in lines" class="text-skeleton" v-bind:key="index"></span>
  </div>
</template>

<script lang=ts>
export default ({
  props: {
    lines: { type: Number, required: false, default: 1 },
  },
});
</script>

<style scoped lang="sass">
.text-skeleton
  width: 100%
  height: 24px
  border-radius: 3px
  background-image: linear-gradient(90deg, #ddd 0px, #e8e8e8 40px, #ddd 80px)
  background-size: 600px
  animation: shine-lines-table 1.6s infinite linear
  display: inline-block
  min-width: 15px
</style>
<style lang="sass">
@keyframes shine-lines-table
  0%
    opacity: 0.4
  40%
    opacity: 0.8
  100%
    opacity: 0.4

.test
  display: flex
</style>
