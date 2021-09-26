<template>
  <div class="content-container">
    <div class="content">
      <div class="login_window popup_window">
        <LoginForm/>
      </div>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import LoginForm from '@/components/account/LoginForm.vue';
import Footer from '@/components/Footer.vue';

export default defineComponent({
  name: 'Login',
  components: {
    LoginForm,
    Footer,
  },
});
</script>

<style scoped lang="sass">
  .login_window
    position: relative
    top: 20%
    width: 65%
    padding: 40px
    box-sizing: border-box
    margin: 0 auto
    border-radius: 12px
    border-top-width: 22px

  .login_window > .input-form
    width: 100%

  .content
    margin: auto
</style>
