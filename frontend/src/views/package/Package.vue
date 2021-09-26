<template>
  <div class="content-container">
    <div class='banner'>
      <div class='banner-content'>
        <div class="title-bar">
          <h1>Pakket</h1>
          <button class="button-secondary-edge"
                  v-on:click="showCreatePackagePopup = true">Pakket aanmaken</button>
          <PackageForm v-if="showCreatePackagePopup"
                       :pack="null"
                       @package_form_close="showCreatePackagePopup = false"
                       @package_form_submit="packageSubmit"/>
        </div>
        <TabSelector :tabs="packageTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>
    <div class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:all>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek pakket'
            :filters="PackageFilters"
            :entries="packages"
            :headers="PackageHeaders"
            :updateFunction="updateFunction"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectPackage', params: { id: item.entry.id }}">
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
import { createUpdateFunction } from '@/helpers/paginationHelpers';
import { PackageHeaders, PackageFilters } from '@/helpers/paginationFields';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import PackageForm from '@/components/package/PackageForm.vue';

export default defineComponent({
  components: {
    DataTable,
    Footer,
    TabSelector,
    TabPanel,
    PackageForm,
  },
  setup() {
    const store = useStore();
    const packages = computed(() => store.state.packagestore.packages.value);

    const updateFunction = createUpdateFunction('CLEAR_PACKAGES', 'fetch_packages_pagination');
    const loading = computed(() => store.state.packagestore.packages.loading);

    const activeTab = ref('all');
    const packageTabs = [
      {
        name: 'all',
        text: 'Alle',
      },
    ];

    const showCreatePackagePopup = ref(false);

    const packageSubmit = (pack) => {
      const copy = pack;
      copy.services = pack.services
        .map((service) => Object({ ...service, serviceId: service.id }));
      store.dispatch('create_package', copy);
      showCreatePackagePopup.value = false;
    };

    return {
      PackageHeaders,
      PackageFilters,
      packages,
      loading,
      updateFunction,
      activeTab,
      packageTabs,
      showCreatePackagePopup,
      packageSubmit,
    };
  },
});
</script>
