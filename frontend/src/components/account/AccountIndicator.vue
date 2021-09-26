<template>
  <div :class="[loggedIn ? 'account_indicator_loggedIn' : 'account_indicator_loggedOut'
  , 'account_indicator']">
    <div v-if="loggedIn && user" class="account_info"
    v-on:click="showMenu">
      <img src="../../assets/user.svg" alt="icon-userprofile" class="profile_icon"/>
      <span class="indicator_span user_span">
        {{user.lastName}} {{user.firstName}}
      </span>

      <span class="indicator_span organisation_span" v-if="selectedOrganisation">
        &nbsp;/ {{ selectedOrganisation.organisation.organisationName }}
      </span>
    </div>
    <div v-else class="account_info">
      <router-link :to="{name: 'Login'}">
        <img src="../../assets/user.svg" alt="icon-userprofile" class="profile_icon"/>
        <span class="indicator_span">Aanmelden</span>
      </router-link>
    </div>
  </div>
</template>

<script lang=ts>
import { defineComponent, computed, ref } from 'vue';
import { useStore } from 'vuex';

export default defineComponent({
  name: 'AccountIndicator',
  emits: ['show-menu'],
  setup(props, { emit }) {
    const store = useStore();
    const loggedIn = computed(() => store.getters.loggedIn);
    const user = computed(() => store.state.authenticationstore.user);
    const selectedOrganisation = computed(
      () => store.state.authenticationstore.selectedOrganisation,
    );
    const menuVisible = ref(false);
    function showMenu() {
      emit('show-menu');
    }
    return {
      loggedIn,
      user,
      selectedOrganisation,
      menuVisible,
      showMenu,
    };
  },
});
</script>

<style scoped lang="sass">
.account_indicator
  background-color: #FFE615
  height: 100%
  margin-left: auto
  display: flex
  cursor: pointer
  &::before
    content: ""
    display: inline-block
    height: 100%
    width: 15px
    clip-path: polygon(0 100%, 0 0, 100% 100%)
    background-color: white
    box-shadow: inset 0px -1px 0px 0 hsl(0deg 0% 85% / 44%)
.profile_icon
  width: 17px
.indicator_span
  white-space: nowrap
  margin-left: 10px
  color: rgb(32, 32, 32)
.user_span
  color: rgb(54, 54, 54)
  overflow: hidden
  text-overflow: ellipsis
  max-width: 170px
  font-weight: bold
.organisation_span
  color: rgb(54, 54, 54)
  overflow: hidden
  text-overflow: ellipsis
  max-width: 170px
.account_info
  padding-right: 40px
  padding-left: 40px
  width: 100%
  height: 100%
  display: flex
  align-items: center
  box-sizing: border-box
  transition: 0.15s ease-in
  *
    color: black
    text-decoration: none
  &:hover
    padding-right: 52px
    padding-left: 52px
    transition: 0.15s ease-out
</style>
