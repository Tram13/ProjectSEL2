<template>
  <div class="content-container">
    <div class='banner'>
      <div class='banner-content'>
        <div class="title-bar">
          <h1>Organisatie</h1>
          <button class="button-secondary-edge"
                  v-on:click="showCreateOrganisationPopup = true">Organisatie Aanmaken</button>
          <Popup size="medium" v-if="showCreateOrganisationPopup"
                 @closePopup="showCreateOrganisationPopup = false">
            <h1>Organisatie aanmaken</h1>
            <OrganisationForm
              :organisation="null"
              @organisation_form_close="showCreateOrganisationPopup = false"/>
          </Popup>
        </div>
        <TabSelector :tabs="organisationTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>
    <div class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:all>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek organisatie'
            :filters="OrganisationFilters"
            :entries="organisation"
            :headers="OrganisationHeaders"
            :updateFunction="updateFunction"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectOrganisation', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:accepted>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek organisatie'
            :filters="OrganisationFilters"
            :entries="organisation"
            :headers="OrganisationHeaders"
            :updateFunction="approvedUpdateFunction"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectOrganisation', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:pendingApproval>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek organisatie'
            :filters="OrganisationFilters"
            :entries="organisation"
            :headers="OrganisationHeaders"
            :updateFunction="pendingUpdateFunction"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectOrganisation', params: { id: item.entry.id }}">
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
import { createUpdateFunction, createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import { OrganisationFilters, OrganisationHeaders } from '@/helpers/paginationFields';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import Popup from '@/components/elements/popup/popup.vue';
import OrganisationForm from '@/components/organisation/OrganisationForm.vue';

export default defineComponent({
  components: {
    DataTable,
    Footer,
    TabSelector,
    TabPanel,
    Popup,
    OrganisationForm,
  },
  setup() {
    const store = useStore();
    const organisation = computed(() => store.state.organisationstore.organisations.value);
    const loading = computed(() => store.state.organisationstore.organisations.loading);

    const updateFunction = createUpdateFunction('CLEAR_ORGANISATIONS', 'fetch_organisations_pagination');

    const pendingUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_ORGANISATIONS',
      'fetch_organisations_pagination',
      { approved: false },
    );
    const approvedUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_ORGANISATIONS',
      'fetch_organisations_pagination',
      { approved: true },
    );

    const activeTab = ref('all');

    const organisationTabs = [
      {
        name: 'all',
        text: 'Alle',
      },
      {
        name: 'accepted',
        text: 'Goedgekeurd',
      },
      {
        name: 'pendingApproval',
        text: 'In afwachting',
      },
    ];

    const showCreateOrganisationPopup = ref(false);

    return {
      OrganisationFilters,
      OrganisationHeaders,
      organisation,
      loading,
      updateFunction,
      activeTab,
      organisationTabs,
      pendingUpdateFunction,
      approvedUpdateFunction,
      showCreateOrganisationPopup,
    };
  },
});
</script>
