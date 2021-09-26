<template>
<ProposalSidebar :activeItem="'diensten'">
  <div class="content">
    <h1>Diensten</h1>
    <data-table
      generalSearchParameter='generalSearch'
      filterPlaceholder='Zoek dienst'
      :entries="services"
      :headers="headers"
      :updateFunction="updateFunction"
      :filters="filters"
      :loading="loading">
      <template v-slot:proposed="item">
        <input type="checkbox"
        :disabled=!editing
        :checked="inProposal(item.entry.id)" @change="addDelProposalService($event, item.entry)">
      </template>
      <template v-slot:source="item">
        <select v-if="item.entry.sources.length > 1"
                v-model="item.entry.selectedSource"
                @change="updateProposalService(item.entry)"
                :disabled=!editing>
          <option v-for="source, index in item.entry.sources"
                  :key="index">
            {{ source }}
          </option>
        </select>
        <span v-else>{{ item.entry.sources[0] }}</span>
      </template>
      <template v-slot:deliveryMethod="item">
        <select v-if="item.entry.deliveryMethods.length > 1"
                v-model="item.entry.selectedDeliveryMethod"
                @change="updateProposalService(item.entry)"
                :disabled=!editing>
          <option v-for="deliveryMethod, index in item.entry.deliveryMethods"
                  :key="index">
            {{ deliveryMethod }}
          </option>
        </select>
        <span v-else>{{ item.entry.sources[0] }}</span>
      </template>
      <template v-slot:links="item">
        <router-link :to="{name:'InspectService', params: { id: item.entry.id }}">
          <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
        </router-link>
      </template>
    </data-table>
    <br/>
    <div class="proposal-navigation-footer">
      <router-link :to="`/aanvraag/${proposal.id}/pakketten`" class="nav_item">
        <button class="proposal-navigation-btn">Naar Pakketten</button>
      </router-link>
      <router-link :to="`/aanvraag/${proposal.id}/functioneel`" class="nav_item">
        <button class="proposal-navigation-btn">Naar Functioneel/Technisch opzet</button>
      </router-link>
    </div>
  </div>
</ProposalSidebar>
</template>

<script lang="ts">
import { computed, defineComponent, watch } from 'vue';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';
import { useStore } from 'vuex';
import DataTable from '@/components/elements/DataTable.vue';
import { createUpdateFunction } from '@/helpers/paginationHelpers';
import { ServiceFilters, ServiceHeaders } from '@/helpers/paginationFields';

export default defineComponent({
  components: {
    ProposalSidebar,
    DataTable,
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
      { name: 'Bron', propName: 'source' },
      { name: 'Aanlevermethode', propName: 'deliveryMethod' },
      { name: '', propName: 'links' },
    ];
    const store = useStore();
    const editing = computed(() => store.state.proposalstore.editing);
    const proposal = computed(() => store.state.proposalstore.proposal.value);

    if (proposal.value) {
      store.dispatch('update_service_package_filter');
    } else {
      watch(proposal, () => store.dispatch('update_service_package_filter'));
    }

    const services = computed(() => {
      const servicesExtraInfo = store.state.servicestore.services.value.map(
        (el) => ({
          ...el,
          proposed: false,
          selectedSource: el.sources[0],
          selectedDeliveryMethod: el.deliveryMethods[0],
        }),
      );

      servicesExtraInfo.forEach((service, index) => {
        const proposalService = store.state.proposalstore.proposal.value.services.find(
          ({ serviceId }) => serviceId === service.id,
        );
        if (proposalService) {
          servicesExtraInfo[index].selectedSource = proposalService.source;
          servicesExtraInfo[index].selectedDeliveryMethod = proposalService.deliveryMethod;
        }
      });

      return servicesExtraInfo.filter(
        (service) => !store.state.proposalstore.servicesInPackages.find(
          (serviceId) => service.id === serviceId,
        ),
      );
    });

    const loading = computed(() => store.state.servicestore.services.loading);

    function addDelProposalService(event, updatedService) {
      if (event.target.checked) {
        store.commit('ADD_PROP_SERVICE', updatedService);
      } else {
        store.commit('DEL_PROP_SERVICE', updatedService);
      }
    }

    function updateProposalService(updatedService) {
      store.commit('UPDATE_PROP_SERVICE', updatedService);
    }

    function inProposal(id) {
      return store.state.proposalstore.proposal.value.services.some(
        ({ serviceId }) => serviceId === id,
      );
    }

    const selectedServices = computed(() => services.value.filter((el) => inProposal(el.id)));

    const updateFunction = createUpdateFunction('CLEAR_SERVICES', 'fetch_services_pagination');

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'services',
        value: event.target.value,
      });
    }
    return {
      inProposal,
      addDelProposalService,
      updateProposalService,
      proposal,
      headers,
      services,
      loading,
      updateFunction,
      filters,
      editing,
      ServiceFilters,
      ServiceHeaders,
      selectedServices,
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
