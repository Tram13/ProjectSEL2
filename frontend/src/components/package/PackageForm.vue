<template>
  <Popup size="large" @closePopup="closeForm">
    <h1>{{ pack ? 'Pakket aanpassen' : 'Pakket aanmaken'}}</h1>
    <form @submit.prevent="submit(v$.$invalid)" class="input-form">
      <InputField label="Naam:*"
                  type="text"
                  v-model="name"
                  maxlength="256"
                  :alwaysShowError="showAllErrors"
                  dataTestid="package-name-input">
        <span v-if="v$.name.required.$invalid">
          Naam ontbreekt
        </span>
      </InputField>
      <h3>Geselecteerde diensten</h3>
      <data-table
        generalSearchParameter='generalSearch'
        filterPlaceholder='Zoek dienst'
        :entries="servicesInTable"
        :headers="headers"
        :updateFunction="updateFunction"
        :filters="filters"
        :loading="loading"
      >
        <template v-slot:proposed="item">
          <input type="checkbox"
                 :checked="inPackage(item.entry.id)"
                 @change="addDelPackageService($event, item.entry)">
        </template>
        <template v-slot:source="item">
          <select v-if="item.entry.sources.length > 1"
                  v-model="item.entry.selectedSource"
                  @change="updatePackageService(item.entry)">
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
                  @change="updatePackageService(item.entry)">
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
      <span v-if="showAllErrors && v$.services.required.$invalid" class="input-field-error">
          Een pakket moet minstens één dienst bevatten
        </span>
      <br/>
      <br/>
      <button :class="{ 'button-primary-full-disabled': v$.$invalid }"
              type="submit"
              class="button-primary-full floatRight"
              data-TestId="submit-button"
      >
                Indienen
      </button>
    </form>
  </Popup>
</template>

<script lang=ts>
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import {
  reactive,
  toRefs,
  defineComponent,
  ref, computed,
} from 'vue';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';
import Popup from '@/components/elements/popup/popup.vue';
import DataTable from '@/components/elements/DataTable.vue';
import { createUpdateFunction } from '@/helpers/paginationHelpers';

export default defineComponent({
  props: {
    pack: Object,
  },
  components: {
    InputField,
    Popup,
    DataTable,
  },
  emits: ['package_form_close', 'package_form_submit'],
  setup(props, { emit }) {
    const filters = [
      {
        text: 'Naam',
        name: 'name',
        type: 'text',
      },
    ];
    const headers = [
      { name: 'Voeg toe', propName: 'proposed' },
      { name: 'Naam', propName: 'name' },
      { name: 'Bron', propName: 'source' },
      { name: 'Aanlevermethode', propName: 'deliveryMethod' },
      { name: '', propName: 'links' },
    ];
    const state = reactive({
      name: props.pack ? props.pack.name : '',
      services: props.pack ? props.pack.services : [],
    });

    const rules = {
      name: { required },
      services: { required },
    };

    const showAllErrors = ref(false);
    const v$ = useVuelidate(rules, state);

    const store = useStore();

    function submit(invalid) {
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      emit('package_form_submit', state);
    }

    const servicesInTable = computed(() => {
      const servicesExtraInfo = store.state.servicestore.services.value.map(
        (el) => ({
          ...el,
          proposed: false,
          selectedSource: el.sources[0],
          selectedDeliveryMethod: el.deliveryMethods[0],
        }),
      );

      servicesExtraInfo.forEach((serv, index) => {
        if (state.services) {
          const packageService = state.services.find(
            ({ id }) => id === serv.id,
          );
          if (packageService) {
            servicesExtraInfo[index].selectedSource = packageService.source;
            servicesExtraInfo[index].selectedDeliveryMethod = packageService.deliveryMethod;
          }
        }
      });

      return servicesExtraInfo;
    });

    const loading = computed(() => store.state.servicestore.services.loading);

    const addDelPackageService = (event, updatedService) => {
      if (event.target.checked) {
        state.services = state.services.concat(
          state.services,
          [
            {
              source: updatedService.selectedSource,
              deliveryMethod: updatedService.selectedDeliveryMethod,
              id: updatedService.id,
            },
          ],
        );
      } else {
        state.services = state.services.filter(
          ({ id }) => id !== updatedService.id,
        );
      }
    };

    const updatePackageService = (updatedService) => {
      state.services.forEach((propService, index) => {
        if (propService.id === updatedService.id) {
          state.services[index].source = updatedService.selectedSource;
          state.services[index].deliveryMethod = updatedService.selectedDeliveryMethod;
        }
      });
    };

    const inPackage = (serviceId) => state.services.some(
      ({ id }) => id === serviceId,
    );

    const updateFunction = createUpdateFunction('CLEAR_SERVICES', 'fetch_services_pagination');

    const closeForm = () => {
      emit('package_form_close');
    };

    return {
      ...toRefs(state),
      v$,
      submit,
      showAllErrors,
      closeForm,
      loading,
      filters,
      headers,
      updateFunction,
      inPackage,
      updatePackageService,
      addDelPackageService,
      servicesInTable,
    };
  },
});
</script>

<style scoped lang="sass">
.error
  border: 2px solid red
.input-field-error
  overflow: hidden
  min-height: 25px
  color: rgb(255, 93, 93)
  font-size: 12px
</style>
