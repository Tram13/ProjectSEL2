<template>
  <div class="content-container">
    <div class='banner'>
      <div class='banner-content'>
        <div class="title-bar">

          <h1>Aanvraag</h1>
          <router-link
              :to="{name: 'CreateProposal'}"
              class="right">
            <button class="button-secondary-edge">Aanvraag Aanmaken</button>
          </router-link>
        </div>
        <TabSelector :tabs="proposalTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>
    <div class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:all>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek aanvraag'
            :entries="proposals"
            :headers="ProposalHeaders"
            :updateFunction="updateFunction"
            :filters="ProposalFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <!-- draft, completed, in_review, pending_feedback, accepted, denied, cancelled -->
        <template v-slot:completed>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek aanvraag'
            :entries="proposals"
            :headers="ProposalHeaders"
            :updateFunction="getStatusUpdateFunction('completed')"
            :filters="ProposalFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:inReview>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek aanvraag'
            :entries="proposals"
            :headers="ProposalHeaders"
            :updateFunction="getStatusUpdateFunction('in_review')"
            :filters="ProposalFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:pendingFeedback>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek aanvraag'
            :entries="proposals"
            :headers="ProposalHeaders"
            :updateFunction="getStatusUpdateFunction('pending_feedback')"
            :filters="ProposalFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:accepted>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek aanvraag'
            :entries="proposals"
            :headers="ProposalHeaders"
            :updateFunction="getStatusUpdateFunction('accepted')"
            :filters="ProposalFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:denied>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek aanvraag'
            :entries="proposals"
            :headers="ProposalHeaders"
            :updateFunction="getStatusUpdateFunction('denied')"
            :filters="ProposalFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
      </TabPanel>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue';
import { useStore } from 'vuex';
import DataTable from '@/components/elements/DataTable.vue';
import Footer from '@/components/Footer.vue';
import { ProposalHeaders, ProposalFilters } from '@/helpers/paginationFields';
import { createUpdateFunction, createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import { proposalStatusTranslator } from '@/helpers/fieldTranslators';
import formatDate from '@/helpers/datehelpers';

export default defineComponent({
  components: {
    DataTable,
    Footer,
    TabSelector,
    TabPanel,
  },
  setup() {
    const store = useStore();
    const proposals = computed(() => store.state.proposalstore.proposals.value
      .map((data) => {
        const res = { ...data };
        res.status = proposalStatusTranslator[data.status];
        try {
          res.deadline = formatDate(res.deadline);
        } catch (err) {
          res.deadline = null;
        }
        return res;
      }));
    const loading = computed(() => store.state.proposalstore.proposals.loading);

    const updateFunction = createUpdateFunction('CLEAR_PROPOSALS', 'fetch_proposals_pagination');
    const getStatusUpdateFunction = (status) => createUpdateFunctionExtraParams(
      'CLEAR_PROPOSALS',
      'fetch_proposals_pagination',
      { status },
    );

    const activeTab = ref('all');
    // draft and canceled are skipped
    const proposalTabs = [
      {
        name: 'all',
        text: 'Alle',
      },
      {
        name: 'completed',
        text: 'Ingediend',
      },
      {
        name: 'inReview',
        text: 'In Review',
      },
      {
        name: 'accepted',
        text: 'Geaccepteerd',
      },
      {
        name: 'denied',
        text: 'Geweigerd',
      },
    ];

    return {
      ProposalFilters,
      ProposalHeaders,
      proposals,
      loading,
      updateFunction,
      activeTab,
      proposalTabs,
      getStatusUpdateFunction,
    };
  },
});
</script>
