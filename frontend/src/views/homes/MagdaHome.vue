<template>
  <div class="content-container">
    <div class="content">
      <h1>Magda Home</h1>
      <h3>Goed te keuren organisaties</h3>
      <hr/>
      <data-table
        :entries="organisation"
        :headers="OrganisationHeaders"
        :updateFunction="organisationUpdate"
        :loading="loadingOrganisations">
        <template v-slot:links="item">
          <div class="button_container">
            <router-link :to="{name:'InspectOrganisation', params: { id: item.entry.id }}">
              <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
            </router-link>
          </div>
        </template>
      </data-table>

      <br/>
      <h3>Lopende aanvragen</h3>
      <hr/>
      <data-table
        :entries="proposals"
        :headers="ProposalHeaders"
        :updateFunction="proposalUpdate"
        :loading="loadingProposals">
        <template v-slot:links="item">
          <router-link :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
            <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
          </router-link>
        </template>
      </data-table>
    </div>
    <Footer/>
  </div>

</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import { useStore } from 'vuex';
import DataTable from '@/components/elements/DataTable.vue';
import Footer from '@/components/Footer.vue';
import { createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import { OrganisationHeaders, ProposalHeaders } from '@/helpers/paginationFields';
import { proposalStatusTranslator } from '@/helpers/fieldTranslators';
import formatDate from '@/helpers/datehelpers';

export default defineComponent({
  components: {
    Footer,
    DataTable,
  },
  setup() {
    const store = useStore();

    // ORGANISATIONS
    const organisation = computed(
      () => store.state.organisationstore.organisations.value,
    );
    const loadingOrganisations = computed(
      () => store.state.organisationstore.organisations.loading,
    );

    const organisationUpdate = createUpdateFunctionExtraParams(
      'CLEAR_ORGANISATIONS',
      'fetch_organisations_pagination',
      { approved: false },
    );

    // PROPOSALS
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
    const loadingProposals = computed(() => store.state.proposalstore.proposals.loading);

    const proposalUpdate = createUpdateFunctionExtraParams(
      'CLEAR_PROPOSALS',
      'fetch_proposals_pagination',
      { status: 'completed, in_review, pending_feedback' },
    );

    return {
      OrganisationHeaders,
      organisation,
      loadingOrganisations,
      organisationUpdate,
      ProposalHeaders,
      proposals,
      loadingProposals,
      proposalUpdate,
    };
  },
});
</script>
<style scoped lang="sass">
.button_container
  display: flex
  flex-direction: column
  justify-content: space-evenly
  align-items: center
</style>
