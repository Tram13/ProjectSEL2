<template>
  <div class="content_wrapper">
    <div :class="[user ? 'sidebar_container_loggedIn' : '', 'sidebar_container']">
      <nav>
        <ul>
          <li v-if="selectedOrganisation">
            <router-link
              :to="{name: 'InspectOrganisation',
                         params: { id: selectedOrganisation.organisation.id }}"
              class="nav-link-container">
              <div class="nav-link-content-wrapper">
                <div class="nav-link-content">
                  <div class="nav-icon-container">
                    <img src="@/assets/users.svg" alt="icon-organisationprofile"/>
                  </div>
                  <label class="nav-label">Organisatie</label>
                </div>
              </div>
            </router-link>
          </li>
          <li v-else>
            <router-link :to="{name: 'CreateOrganisation' }"
              class="nav-link-container">
              <div class="nav-link-content-wrapper">
                <div class="nav-link-content">
                  <div class="nav-icon-container">
                    <img src="@/assets/users.svg" alt="icon-organisationprofile"/>
                  </div>
                  <label class="nav-label">Organisatie</label>
                </div>
              </div>
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'CreateProposal' }" class="nav-link-container">
              <div class="nav-link-content-wrapper">
                <div class="nav-link-content">
                  <div class="nav-icon-container">
                    <img src="@/assets/file.svg" alt="icon-organisationprofile"/>
                  </div>
                  <label class="nav-label">Aanvraag</label>
                </div>
              </div>
            </router-link>
          </li>
        </ul>
      </nav>
    </div>

    <div class="content_container_wrapper">
        <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import { useStore } from 'vuex';

export default defineComponent({
  setup() {
    const store = useStore();
    const user = computed(() => store.state.authenticationstore.user);
    const selectedOrganisation = computed(
      () => store.state.authenticationstore.selectedOrganisation,
    );
    return {
      user,
      selectedOrganisation,
    };
  },
});
</script>

<style scoped lang="sass">
.sidebar_container nav, ul, li
  width: 100%

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
  margin-left: 90px
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

.sidebar_container
  display: flex
  position: fixed
  justify-content: center
  overflow: hidden
  background-color: #FFE615
  width: 90px
  height: 100%
  transition: 0.25s ease
  z-index: 9001

.sidebar_container_loggedIn:hover
  width: 230px
  -webkit-box-shadow: 49px 0 50px 0 rgba(0,0,0,0.1)
  -moz-box-shadow: 49px 0 50px 0 rgba(0,0,0,0.1)
  box-shadow: 49px 0 50px 0 rgba(0,0,0,0.1)

.sidebar_container:hover .nav-link-container
  height: 65px

nav
  flex: 1
  display: flex
  align-items: center
</style>
