<template>
  <div class="content-container" v-if="service && !loading">
    <div class='banner banner-large'>
      <div class='banner-content'>
        <BackButton/>
        <div class="title-bar">
          <h1>{{service.name}}</h1>
          <div class="right">
            <div class="image-button" v-on:click="toggleEditing">
              <img src='@/assets/pencil.svg' alt="bewerk" />
            </div>
            <div class="image-button" v-on:click="deleteService">
              <img src='@/assets/trash.svg' alt="verwijder"/>
            </div>
          </div>
        </div>
        <div class="banner-info horizontal">
          <div class="key-value-grid">
            <span>domein:</span>
            <span>{{service.domain}}</span>

          </div>
          <div class="key-value-grid">
            <span>bronnen:</span>
            <span>{{service.sources.join(', ')}}</span>
            <span>leveringsmethoden:</span>
            <span>{{service.deliveryMethods.join(', ')}}</span>
          </div>
        </div>
        <TabSelector :tabs="serviceTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>

    <Popup size="large" v-if="editing" @closePopup="toggleEditing">
      <h1>Dienst aanpassen</h1>
      <ServiceForm :service="service" @service_form_close="toggleEditing"/>
    </Popup>

    <div v-else class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:description>
          <h4>Beschrijving</h4>
          <span>{{service.description}}</span>
        </template>
      </TabPanel>
    </div>
    <Footer/>
  </div>
  <div v-else>
    <pulse-loader color="#FFE615"></pulse-loader>
  </div>
</template>

<script lang=ts>
import { ref, defineComponent, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import ServiceForm from '@/components/service/ServiceForm.vue';
import Footer from '@/components/Footer.vue';
import BackButton from '@/components/elements/BackButton.vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import Popup from '@/components/elements/popup/popup.vue';

export default defineComponent({
  components: {
    ServiceForm,
    Footer,
    PulseLoader,
    TabSelector,
    TabPanel,
    BackButton,
    Popup,
  },
  setup() {
    const editing = ref(false);

    const store = useStore();
    const router = useRouter();
    const route = useRoute();

    const service = computed(() => store.state.servicestore.service.value);

    store.dispatch('fetch_service', (route ? route.params.id : undefined));
    const loading = computed(() => store.state.servicestore.service.loading);

    function deleteService() {
      // eslint-disable-next-line
      if (confirm('Bent u zeker?')) {
        store.dispatch('delete_service', service.value);
        router.push({ name: 'Service' });
      }
    }

    function toggleEditing() {
      editing.value = !editing.value;
    }

    const serviceTabs = [
      {
        name: 'description',
        text: 'Beschrijving',
      },
    ];
    const activeTab = ref('description');

    return {
      editing,
      service,
      loading,
      deleteService,
      toggleEditing,
      serviceTabs,
      activeTab,
    };
  },
});
</script>
