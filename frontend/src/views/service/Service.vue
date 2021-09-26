<template>
  <div class="content-container">
    <div class='banner'>
      <div class='banner-content'>
        <div class="title-bar">
          <h1>Dienst</h1>
          <button class="button-secondary-edge"
                  v-on:click="showCreateServicePopup = true">Dienst aanmaken</button>
          <Popup size="large" v-if="showCreateServicePopup"
                 @closePopup="showCreateServicePopup = false">
            <h1>Dienst aanmaken</h1>
            <ServiceForm :service="null" @service_form_close="closeCreatePopup"/>
          </Popup>
        </div>
        <TabSelector :tabs="serviceTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>
    <div class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:all>
          <data-table
            ref="serviceTable"
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek dienst'
            :entries="services"
            :headers="ServiceHeaders"
            :updateFunction="updateFunction"
            :filters="ServiceFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectService', params: { id: item.entry.id }}">
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
import { ServiceFilters, ServiceHeaders } from '@/helpers/paginationFields';
import { createUpdateFunction } from '@/helpers/paginationHelpers';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import Popup from '@/components/elements/popup/popup.vue';
import ServiceForm from '@/components/service/ServiceForm.vue';

export default defineComponent({
  components: {
    DataTable,
    Footer,
    TabSelector,
    TabPanel,
    Popup,
    ServiceForm,
  },
  setup() {
    const store = useStore();
    const services = computed(() => store.state.servicestore.services.value);
    const loading = computed(() => store.state.servicestore.services.loading);

    const updateFunction = createUpdateFunction('CLEAR_SERVICES', 'fetch_services_pagination');

    const activeTab = ref('all');
    const serviceTabs = [
      {
        name: 'all',
        text: 'Alle',
      },
    ];

    const showCreateServicePopup = ref(false);
    const serviceTable = ref(null);

    const closeCreatePopup = () => {
      showCreateServicePopup.value = false;
      serviceTable.value.refreshPage();
    };

    return {
      ServiceFilters,
      ServiceHeaders,
      services,
      loading,
      updateFunction,
      activeTab,
      serviceTabs,
      showCreateServicePopup,
      closeCreatePopup,
      serviceTable,
    };
  },
});
</script>
