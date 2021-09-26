<template>
  <div class="content-container">
    <div class="content">
      <h1>Organisatie aanmaken</h1>
      <OrganisationForm
        :organisation="null"
        @organisation_form_close="$router.push('/organisatie')"/>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import OrganisationForm from '@/components/organisation/OrganisationForm.vue';
import Footer from '@/components/Footer.vue';

export default defineComponent({
  components: {
    OrganisationForm,
    Footer,
  },
});
</script>
