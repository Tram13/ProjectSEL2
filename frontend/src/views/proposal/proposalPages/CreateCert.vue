<template>
  <ProposalSidebar :activeItem="'Maak certificaat'">
    <div class="content">
      <h1>Certificaat aanmaken</h1>
      <CertForm :cert="null" @cert_form_close="$router.push('/certificaten')"/>
    </div>
  </ProposalSidebar>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import CertForm from '@/components/cert/CertForm.vue';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';

export default defineComponent({
  components: {
    CertForm,
    ProposalSidebar,
  },
});
</script>
