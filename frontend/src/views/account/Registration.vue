<template>
  <div class="content-container">
    <div class="content">
      <h1>Registratie</h1>
      <UserForm :registration="true"/>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import UserForm from '@/components/user/UserForm.vue';
import Footer from '@/components/Footer.vue';

export default defineComponent({
  name: 'Registration',
  components: {
    UserForm,
    Footer,
  },
});
</script>
