<template>
    <div class="quickcontainer">
      <Header/>
      <Sidebar v-if="user && (user.role === 'admin' || user.role === 'employee')">
        <router-view/>
      </Sidebar>
      <UserSidebar v-if="user && (user.role === 'customer')">
        <router-view/>
      </UserSidebar>
      <NoUserLoggedInSidebar v-if="!user">
        <router-view/>
      </NoUserLoggedInSidebar>
    </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue';
import { useStore } from 'vuex';
import Header from '@/components/Header.vue';
import Sidebar from '@/components/sidebars/Sidebar.vue';
import UserSidebar from '@/components/sidebars/UserSidebar.vue';
import NoUserLoggedInSidebar from '@/components/sidebars/NoUserLoggedInSidebar.vue';

export default defineComponent({
  components: {
    NoUserLoggedInSidebar,
    Sidebar,
    UserSidebar,
    Header,
  },
  setup() {
    const store = useStore();
    const user = computed(() => store.state.authenticationstore.user);
    const menuVisible = ref(false);
    return {
      user,
      menuVisible,
    };
  },
});
</script>

<style lang="sass">
  \:root
    --primary-dark: #F0D70F
    --primary-normal: #FFE615
    --primary-disabled: #fff7ac
    --primary-disabled-text: #999999
    --primary-text: #6B6B6B
    --secondary-dark: #4E7E71
    --secondary-normal: #3f48cc
    --secondary-light: #8a7ad4
    --secondary-disabled: #aea6d6
    --secondary-disabled-text: #999999
    --secondary-text: white
    --ternary-light: #EDEDED
    --ternary-normal: #D5D5D5
    --ternary-dark: #989898
    --ternary-text: #3d3d3d
    --ternary-disabled-text: #6B6B6B
  .floatRight
    float: right
  .image-button
    display: flex
    justify-content: center
    align-items: center
    padding: 10px
    cursor: pointer
  .image-button > img
    width: 20px
  .small-image-button
    display: flex
    justify-content: center
    align-items: center
    cursor: pointer
  .small-image-button > img
    margin: 0 15px
    width: 20px
  .breadcrumb
    background-color: transparent
    color: var(--secondary-light)
    font-weight: bold
  h1
    font-size: 2.2rem
    font-weight: 600
    color: #2c3e50
  .key-value-grid
    display: grid
    grid-template-columns: auto auto
    grid-gap: 15px 20px
    width: max-content
    color: var(--ternary-dark)
    font-size: 15px
  .key-value-grid
    span
      &:nth-child(2n + 1)
        font-weight: bold
        font-size: 13px
  .page-header
    display: flex
    justify-content: space-between
    align-items: center
  input:-webkit-autofill,
  input:-webkit-autofill:hover,  input:-webkit-autofill:focus,  input:-webkit-autofill:active
    -webkit-transition: "color 9999s ease-out, background-color 9999s ease-out"
    -webkit-transition-delay: 9999s
  .content-container
    height: 100%
    display: flex
    flex-direction: column
  .content
    display: flex
    flex-direction: column
    width: 80%
    max-width: 65rem
    margin: 50px auto
    flex: 1
    padding: 15px

  .horizontal
    display: flex
    gap: 0 70px
  .banner
    flex: 0 0 180px
    display: flex
    align-items: flex-end
    justify-content: center
    width: 100%
    background-color: #f4f6f86e
  .banner-info
    margin-left: 40px
    margin-bottom: 30px
  .banner-large
    flex: 0 0 280px
  .banner-content
    width: 80%
    max-width: 65rem
  body, html
    padding: 0
    margin: 0
    width: 100%
    height: 100%
    color: #2c3e50
  li
    list-style: none
  ul, li /* reset the margin and padding */
    margin: 0
    padding: 0
  #app
    font-family: Avenir, Helvetica, Arial, sans-serif
    -webkit-font-smoothing: antialiased
    -moz-osx-font-smoothing: grayscale
    height: 100%
  .quickcontainer
    width: 100vw
    height: 100vh
  .input-form
    width: 100%
  .form-element
    position: relative
    width: 70%
    margin-bottom: 20px

  .form-element > input, .form-element > select, .form-element > textarea
    position: absolute
    right: 0
    width: 70%
    box-sizing: border-box

  .form-element > textarea
    resize: none
    height: 120px

  .form-element-toggle
    width: 100%

  .form-element-toggle > .form-element
    width: 70%
    display: inline-block

  .form-element-toggle > .toggle-edit-button
    margin-left: 7px

  .contains-textarea
    margin-bottom: 120px

  .searchbar
    position: relative
    width: 100%

  .searchbar > input
    position: absolute
    float: right
    margin-left: 5px
    width: 100%
    box-sizing: border-box

  .small-bottom-margin
    margin-bottom: 8px

  .errorMsg-group
    margin-top: 5px
    padding: 12px
    border-radius: 5px
    background-color: #F7A7A3

  .errorMsg-group:empty
    display: none

  .errorMsg-group > .errorMsg
    color: #ff3e3e

  .arrow
    width: 20px

  .popup_window
    background-color: #F0F0F0
    border: 1px solid #FFE615
    border-top-width: 12px
    border-radius: 7px
    box-shadow: 0 8px 16px 0 rgba(0,0,0,0.2)
    box-sizing: border-box
    size: auto
    z-index: 10000

  .popup_window .form-element
    width: 90%

  .popup_window_content
    width: 100%
    padding: 12px 16px
    z-index: 1

  .account_menu
    position: fixed
    right: 32px
    margin-top: 12px
    transition: 1s ease-in
    z-index: 10000

  button
    outline: none
    border: none
    cursor: pointer

  button:disabled
    cursor: not-allowed

  .button-primary-full
    padding: 10px 20px
    font-size: 16px
    background-color: var(--primary-normal)
    border-radius: 7px

  .button-primary-full-disabled
    cursor: not-allowed
    background-color: var(--primary-disabled)
    color: #666666

  .button-primary-full:disabled
    background-color: var(--primary-disabled)
    color: #666666

  .button-secondary-edge
    padding: 8px 20px
    border: 2px solid var(--secondary-normal)
    color: var(--secondary-normal)
    font-weight: bold
    background-color: transparent
    border-radius: 7px

  .button-secondary-edge:disabled
    border: 2px solid #E5E5E5
    color: #666666

  .button-secondary-full
    padding: 8px 20px
    background-color: var(--secondary-normal)
    border-radius: 7px

  .button-secondary-full:disabled
    cursor: not-allowed
.button-primary-transparent
  padding: 10px 20px
  font-size: 16px
  background-color: transparent
  border-radius: 7px

.button-primary-full-disabled
  cursor: not-allowed
  background-color: var(--primary-disabled)
  color: #666666

.button-primary-full:disabled
  background-color: var(--primary-disabled)
  color: #666666

.button-secondary-edge
  padding: 8px 20px
  border: 2px solid var(--secondary-normal)
  color: var(--secondary-normal)
  font-weight: bold
  background-color: transparent
  border-radius: 7px

  &:disabled
    border: 2px solid #E5E5E5
    color: #666666

.button-secondary-full
  padding: 8px 20px
  background-color: var(--secondary-normal)
  border-radius: 7px

  &:disabled
    background-color: #c0e0d7
    color: #868686

.button-ternary-full
  padding: 8px 20px
  background-color: var(--ternary-light)
  border-radius: 7px
  color: var(--ternary-text)

  &:disabled
    color: var(--ternary-disabled-text)

.title-bar
  display: flex
  justify-content: space-between
  align-items: center

.right
  flex-shrink: 0

button + button, a
  margin-left: 5px

a + button
  margin-left: 5px

.proposal-navigation-footer
  margin-top: auto
  border-top: 1px solid rgb(185, 185, 185)
  width: 100%
  display: flex
  justify-content: space-between
  padding: 10px 0

.proposal-navigation-btn
  padding: 10px 30px
  font-size: 15px
  outline: none
  cursor: pointer

@media all and (max-width: 767px)
  .responsive-table
    thead
      display: none

    tr
      display: block
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1)
      margin: 2vh 1vw
      padding: 3vw

    .col
      display: flex
      padding: 10px 0

      &:before
        color: #6C7A89
        padding-right: 10px
        content: attr(data-label)
        text-align: left
</style>
