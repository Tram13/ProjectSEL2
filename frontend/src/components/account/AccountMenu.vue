<template>
  <div class="account_menu" v-if="user">
    <div class="account_menu_content" v-if="user">
      <div class="account_menu_icon_container">
        <img src="../../assets/user.svg" alt="approved"/>
      </div>
      <span>{{user.lastName}} {{user.firstName}}</span>
      <span>{{user.email}}</span>
      <span>{{roleTranslateMap[user.role]}}</span>

    </div>
    <div class="organisation_picker">
      <div class="organisation_picker_title">
        <span>Wissel van organisatie</span>
      </div>
      <ul>
        <li v-for="(org, index) in organisations"
            v-bind:key="index"
            @click="selectOrganisation(org)">
          <div v-if="selectedOrganisation"
            :class="
              'organisation_list_item ' +
              (org.id === selectedOrganisation.organisation.id ? 'item_selected' : '')
            ">
            <span>{{org.organisation.organisationName}}</span>
          </div>
        </li>
      </ul>
    </div>
    <div class="horizontal_button_group">
        <router-link :to="{name: 'InspectUser', params: { id: user.id }}">
            <button class="button-ternary-full">Account Beheer</button>
        </router-link>
        <button v-on:click="logout()" class="button-ternary-full">Afmelden</button>
      </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import { useStore } from 'vuex';
import { userRoleTranslator } from '@/helpers/fieldTranslators';

export default defineComponent({
  setup() {
    const roleTranslateMap = userRoleTranslator;

    const store = useStore();
    const user = computed(() => store.state.authenticationstore.user);
    const organisations = computed(
      () => store.state.authenticationstore.organisations,
    );
    const selectedOrganisation = computed(
      () => store.state.authenticationstore.selectedOrganisation,
    );
    function selectOrganisation(organisation) {
      store.commit('SET_SELECTED_ORGANISATION', organisation.organisation.id);
      this.$router.push(
        { name: 'InspectOrganisation', params: { id: organisation.organisation.id } },
      );
    }
    function logout() {
      store.dispatch('logout')
        .then(() => this.$router.push({ name: 'Login' }));
    }
    return {
      store,
      user,
      organisations,
      selectedOrganisation,
      selectOrganisation,
      logout,
      roleTranslateMap,
    };
  },
});
</script>

<style scoped lang="sass">
.account_menu
  width: 300px
  top: 50px
  right: 20px
  border-top: 7px solid #FFE615
  border-radius: 5px
  box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1)
  overflow: hidden
  background-color: white

.account_menu_icon_container
  background: #FFE615
  border-radius: 50%
  width: 60px
  height: 60px
  display: flex
  justify-content: center
  align-items: center
  margin: auto auto 4px

  img
    width: 30px

.account_menu_content
  display: flex
  flex-direction: column
  align-items: center
  padding: 12px 16px
  z-index: 1

  span
    padding: 4px

.organisation_picker
  width: 100%
  max-height: 300px
  overflow-y: auto
  border-top: 1px solid #e9e9e9

.organisation_picker_title
  padding: 7px 20px
  font-weight: bold
  font-size: 13px
  color: grey

.organisation_list_item
  cursor: pointer
  padding: 7px 20px

.item_selected, .organisation_list_item:hover
  background: rgb(235, 235, 235)

.horizontal_button_group
  border-top: 1px solid #e9e9e9
  padding: 10px 20px
  display: flex
  justify-content: space-between
</style>
