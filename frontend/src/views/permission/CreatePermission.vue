<template>
  <div class="content-wrapper">
    <div class="content">
      <h1>Machtiging aanmaken</h1>
      <PermissionForm :permission="null"
      @permission_form_close="$router.push('/machtiging')"/>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import PermissionForm from '@/components/permission/PermissionForm.vue';
import Footer from '@/components/Footer.vue';

export default defineComponent({
  components: {
    PermissionForm,
    Footer,
  },
});
</script>
