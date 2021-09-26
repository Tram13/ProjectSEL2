<template>
  <div class="organisation-selector">
    <OrganisationSelectorItem
      v-for="(val, index) in organisations"
      :organisation=val
      :key="index"
      @click="selectOrganisation(val)"
    />
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import { useStore } from 'vuex';
import OrganisationSelectorItem from '@/components/organisation/OrganisationSelectorItem.vue';

export default defineComponent({
  name: 'OrganisationSelector',
  components: {
    OrganisationSelectorItem,
  },
  emits: ['select-organisation'],
  setup(props, { emit }) {
    const store = useStore();
    // the organisations of which the logged in user is a member
    const organisations = computed(() => store.state.authenticationstore.organisations);
    function selectOrganisation(organisation) {
      emit('select-organisation', organisation);
    }
    return {
      organisations,
      selectOrganisation,
    };
  },
});
</script>
