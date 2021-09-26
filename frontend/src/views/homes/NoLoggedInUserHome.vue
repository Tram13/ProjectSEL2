<template>
  <div class="content-container">
    <div class="content">
      <h1>Gelieve eerst te registreren/</h1>
    </div>
    <Footer/>
  </div>

</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Footer from '@/components/Footer.vue';
import router from '@/router';

router.push('/aanmelden');
export default defineComponent({
  components: {
    Footer,
  },
});
</script>
