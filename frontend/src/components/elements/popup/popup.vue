<template>
  <div class="popup-overlay">
    <div class="popup-background" @click="closePopup"></div>
    <div class="popup-content-container" :class="size" @click="{}">
      <div class="close-btn" @click="closePopup">
          <img src="@/assets/close.svg" alt="close-popup"/>
      </div>
      <slot></slot>
    </div>

  </div>
</template>

<script lang=ts>
export default ({
  // size must be small, medium, large
  props: {
    size: { type: String, default: undefined },
  },
  emits: ['closePopup'],
  setup(_, { emit }) {
    const closePopup = () => {
      emit('closePopup');
    };
    return {
      closePopup,
    };
  },
});
</script>

<style scoped lang="sass">
.popup-overlay
  top: 0
  left: 0
  width: 100vw
  height: 100vh
  position: fixed
  z-index: 11000
  background-color: rgba(5, 5, 5, 0.15)
  display: flex
  align-items: center
  justify-content: center

.popup-background
  position: fixed
  width: 100%
  height: 100%

.popup-content-container
  width: auto
  min-width: 35vw
  position: fixed
  background-color: white
  padding: 40px
  border-radius: 10px
  overflow: auto
  max-height: 80vh
  max-width: 70vw

.close-btn
  top: 0
  right: 0
  position: absolute
  display: flex
  align-items: center
  justify-content: center
  padding: 20px
  cursor: pointer

  img
    width: 20px

.small
  width: 20vw
  min-width: 200px

.medium
  width: 25vw
  min-width: 400px

.large
  width: 60vw
  min-width: 700px
</style>
