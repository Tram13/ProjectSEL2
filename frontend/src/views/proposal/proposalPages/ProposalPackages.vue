<template>
  <ProposalSidebar :activeItem="'pakketten'">
    <div class="content">
      <div>
      <h1>Pakketten</h1>
        <data-table
          generalSearchParameter='generalSearch'
          filterPlaceholder='Zoek pakket'
          :entries="packages"
          :headers="headers"
          :updateFunction="updateFunction"
          :filters="filters"
          :loading="loading">
          <template v-slot:proposed="item">
            <input type="checkbox"
            :disabled="!editing"
            :checked="inProposal(item.entry.id)" @change="updateProposedStatus($event, item.entry)">
          </template>
          <template v-slot:links="item">
            <router-link :to="{name:'InspectService', params: { id: item.entry.id }}">
              <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
            </router-link>
          </template>
        </data-table>
        <span v-if="proposal.status !== 'draft'">Feedback:</span>
        <SpeechBubble v-if="proposal.status !== 'draft'" v-model="feedback.packages"
          @input="updateFeedback($event)">
        </SpeechBubble>
      </div>
      <div class="proposal-navigation-footer">
        <router-link :to="`/aanvraag/${proposal.id}/context`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Juridische Context</button>
        </router-link>
        <router-link :to="`/aanvraag/${proposal.id}/diensten`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Diensten</button>
        </router-link>
      </div>

    </div>

  </ProposalSidebar>
</template>

<script lang=ts>
import { defineComponent, computed } from 'vue';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';
import { ServiceFilters, ServiceHeaders } from '@/helpers/paginationFields';
import { useStore } from 'vuex';
import { createUpdateFunction } from '@/helpers/paginationHelpers';
import DataTable from '@/components/elements/DataTable.vue';
import SpeechBubble from '@/components/elements/base/SpeechBubble.vue';

export default defineComponent({
  components: {
    ProposalSidebar,
    DataTable,
    SpeechBubble,
  },
  setup() {
    const filters = [
      {
        text: 'Naam',
        name: 'name',
        type: 'text',
      },
    ];
    const headers = [
      { name: 'Vraag aan', propName: 'proposed' },
      { name: 'Naam', propName: 'name' },
      { name: '', propName: 'links' },
    ];
    const store = useStore();
    const proposal = computed(() => store.state.proposalstore.proposal.value);
    const editing = computed(() => store.state.proposalstore.editing);

    const packages = computed(() => store.state.packagestore.packages.value.map(
      (el) => ({ ...el, proposed: false }),
    ));

    const loading = computed(() => store.state.packagestore.loading);

    function updateProposedStatus(event, updatedPackage) {
      if (event.target.checked) {
        store.commit('ADD_PROP_PACKAGE', updatedPackage);
      } else {
        store.commit('DEL_PROP_PACKAGE', updatedPackage);
      }
    }

    function inProposal(id) {
      return store.state.proposalstore.proposal.value.packages.some(
        ({ packageId }) => packageId === id,
      );
    }

    const updateFunction = createUpdateFunction('CLEAR_PACKAGES', 'fetch_packages_pagination');

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'packages',
        value: event.target.value,
      });
    }
    return {
      inProposal,
      editing,
      updateProposedStatus,
      proposal,
      headers,
      packages,
      loading,
      updateFunction,
      filters,
      ServiceFilters,
      ServiceHeaders,
      feedback,
      updateFeedback,
    };
  },
});
</script>

<style scoped lang="sass">
  .introduction-ul li
    list-style: disc
    margin-left: 2rem
    margin-top: 12px
</style>
