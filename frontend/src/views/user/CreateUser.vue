<template>
  <div class="content-container">
    <div class="content">
      <h1>Gebruiker aanmaken</h1>
      <UserForm :user="null" @user_form_close="$router.push('/gebruiker')"/>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import UserForm from '@/components/user/UserForm.vue';
import Footer from '@/components/Footer.vue';

export default defineComponent({
  components: {
    UserForm,
    Footer,
  },
});
</script>
