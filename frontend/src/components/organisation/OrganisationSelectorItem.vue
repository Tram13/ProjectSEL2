<template>
  <div class="organisation-selector-item">
    {{ organisation.organisationName }}
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'OrganisationSelectorItem',
  props: {
    organisation: Object,
  },
  emits: ['click'],
  setup() {
    return {};
  },
});
</script>
