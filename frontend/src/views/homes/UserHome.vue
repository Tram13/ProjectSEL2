<template>
  <div class="content-container">
    <div class="content">
      <h1>User Home</h1>
    </div>
    <Footer/>
  </div>

</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Footer from '@/components/Footer.vue';

export default defineComponent({
  components: {
    Footer,
  },
});
</script>
