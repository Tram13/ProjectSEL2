<template>
  <div class="content-container" v-if="pack">
    <div class='banner banner-large'>
      <div class='banner-content'>
        <BackButton/>
        <div class="title-bar">
          <h1>{{pack.name}}</h1>
          <div class="right">
            <div class="image-button" v-on:click="toggleEditing">
              <img src='@/assets/pencil.svg' alt="bewerken"/>
            </div>
            <div class="image-button" v-on:click="deletePackage">
              <img src='@/assets/trash.svg' alt="verwijderen" />
            </div>
          </div>
        </div>
        <div class="key-value-grid user-info banner-info">
          <span>Aanmaakdatum:</span>
          <span>{{ formatDate(pack.created) }}</span>
          <span>Laatst Gewijzigd:</span>
          <span>{{ formatDate(pack.lastUpdated) }}</span>
        </div>
        <TabSelector :tabs="packageTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>

    <div v-if="!editing" class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:services>
          <h2>Diensten:</h2>
          <data-table
            ref="serviceTable"
            v-if="pack"
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek dienst'
            :filters="ServiceFilters"
            :entries="packServices"
            :headers="headers"
            :updateFunction="updateFunction"
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
    <div v-else class="content">
      <PackageForm :pack="pack"
                   @package_form_close="toggleEditing"
                   @package_form_submit="submitChange"/>
    </div>
    <Footer/>
  </div>
  <div v-else>
    <h3>Pakket bestaat niet</h3>
  </div>
</template>

<script lang=ts>
import { ref, defineComponent, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import PackageForm from '@/components/package/PackageForm.vue';
import Footer from '@/components/Footer.vue';
import DataTable from '@/components/elements/DataTable.vue';
import BackButton from '@/components/elements/BackButton.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import formatDate from '@/helpers/datehelpers';
import { createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import { ServiceFilters } from '@/helpers/paginationFields';

export default defineComponent({
  components: {
    PackageForm,
    BackButton,
    Footer,
    DataTable,
    TabSelector,
    TabPanel,
  },
  setup() {
    const headers = [
      { name: 'Naam', propName: 'name' },
      { name: 'Beschrijving', propName: 'description' },
      { name: '', propName: 'links' },
    ];

    const editing = ref(false);

    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    store.dispatch('fetch_package', (route ? route.params.id : undefined));
    const pack = computed(() => store.state.packagestore.package.value);
    const loading = computed(() => store.state.packagestore.packageServices.loading);

    const packServices = computed(() => store.state.packagestore.package.value.services);

    function deletePackage() {
      // eslint-disable-next-line
      if (confirm('Bent u zeker?')) {
        store.dispatch('delete_package', pack.value);
        router.push({ name: 'Package' });
      }
    }

    function toggleEditing() {
      editing.value = !editing.value;
    }

    const updateFunction = createUpdateFunctionExtraParams(
      'CLEAR_PACKAGE_SERVICES',
      'fetch_package_services_pagination',
      { packageId: (route ? route.params.id : undefined) },
    );

    const activeTab = ref('services');
    const packageTabs = [
      {
        name: 'services',
        text: 'Diensten',
      },
    ];

    const serviceTable = ref(null);

    const submitChange = (state) => {
      state.services = state.services
        .map((service) => Object({ ...service, serviceId: service.id }));
      store.dispatch('update_package', { ...pack.value, ...state })
        .then(() => {
          serviceTable.value.refreshPage();
        });
      toggleEditing();
    };

    return {
      editing,
      pack,
      deletePackage,
      toggleEditing,
      packServices,
      updateFunction,
      headers,
      activeTab,
      packageTabs,
      loading,
      formatDate,
      submitChange,
      serviceTable,
      ServiceFilters,
    };
  },
});
</script>

<style lang="sass">
.info-container
  margin-top: 20px
</style>
