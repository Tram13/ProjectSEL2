<template>
  <header>
    <div class="home_button">
      <router-link :to="{name: 'Home'}" class="icon_flanders_container">
        <img src="../assets/logo_vlaanderen.svg" alt="icon-flanders-logo" class="icon_flanders">
      </router-link>
    </div>
    <h3>Vlaanderen</h3>
    <span>Magda</span>
    <AccountIndicator @show-menu="menuVisible = true"/>
    <AccountMenu
      v-if="menuVisible"
      v-on:mouseenter="menuVisible = true"
      v-on:mouseleave="menuVisible = false"/>
  </header>
</template>

<script lang=ts>
import { defineComponent, ref } from 'vue';
import AccountIndicator from '@/components/account/AccountIndicator.vue';
import AccountMenu from '@/components/account/AccountMenu.vue';

export default defineComponent({
  name: 'Header',
  components: {
    AccountIndicator,
    AccountMenu,
  },
  emits: ['show-menu'],
  setup() {
    const menuVisible = ref(false);
    return {
      menuVisible,
    };
  },
});
</script>

<style scoped>

.icon_flanders{
  width: 30px;
  height:30px;
  min-width: 30px;
  min-height:30px;
  padding: 0 15px 0 25px;
  transition: 0.15s ease-in;
}

.home_button{
  float: left;
  width: 90px;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #FFE615;
  z-index: 9005;

}
.home_button::after{
  content:"";
  background-color: white;
  clip-path: polygon(100% 0, 0 0, 100% 100%);
  width: 15px;
  height: 100%;
}

.icon_flanders_container{
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}

header {
  position: relative;
  display: flex;
  /*justify-content: space-between;*/
  align-items: center;
  width: 100%;
  height: 43px;
  background-color: white;
  z-index: 9005;
  box-shadow: inset 0px -1px 0px 0 hsl(0deg 0% 85% / 44%);
}
/*header::after{
  position: absolute;
  bottom: 0;
  content: "";
  display: inline-block;
  z-index: 9002;

  width: 100vw;
  height: 1px;
  background-color: #CBD2DA;
}*/

header h3{
  margin: 0;
  margin-left: 50px;
}
</style>
