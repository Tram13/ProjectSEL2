<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form">
    <InputField label="Naam:*"
                type="text"
                v-model="name"
                placeholder="naam"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                data-Testid="service-name-input">
      <span v-if="v$.name.required.$invalid">
        Naam ontbreekt
      </span>
    </InputField>
    <InputField label="Domein:*"
                type="text"
                v-model="domain"
                placeholder="onderwijs"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                data-Testid="service-domain-input">
      <span v-if="v$.domain.required.$invalid">
        Domein ontbreekt
      </span>
    </InputField>
    <InputField label="Beschrijving:*"
                type="textarea"
                v-model="description"
                placeholder="Dit document beschrijft het gebruik en de werking van..."
                maxlength="4096"
                :alwaysShowError="showAllErrors"
                data-Testid="service-description-input">
      <span v-if="v$.description.required.$invalid">
        Beschrijving ontbreekt
      </span>
    </InputField>
    <SelectionList
      label="Bronnen:*"
      :alwaysShowError="showAllErrors"
      :values="['BOSA', 'KSZ/RSZ', 'IPEX', 'RR']"
      :checked="sources" :buttonText="'bron toevoegen'" @update="updateSources">
      <span v-if=v$.sources.required.$invalid>
        Minstens één bron moet geselecteerd worden
      </span>
    </SelectionList>

    <SelectionList
      label="Aanlevermethode:*"
      :alwaysShowError="showAllErrors"
      :values="['WEBSERVICE', 'MO', 'FTP', 'PUB']"
      :checked="deliveryMethods" :buttonText="'Aanlevermethode Toevoegen'"
      @update="updateDeliveryMethods">
      <span v-if=v$.deliveryMethods.required.$invalid>
        Minstens één aanlevermethode moet geselecteerd worden
      </span>
    </SelectionList>
    <InputField label="Permissie nodig:"
                type="checkbox"
                v-model="needsPermissions"
                data-Testid="service-permission-input">
    </InputField>
    <button type="submit"
            data-Testid="submit-button"
            class="button-primary-full floatRight"
            :class="{ 'button-primary-full-disabled': v$.$invalid }">Indienen</button>
  </form>
</template>

<script lang=ts>
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import {
  reactive,
  toRefs,
  defineComponent,
  ref,
} from 'vue';
import { useStore } from 'vuex';
import SelectionList from '@/components/elements/base/SelectionList.vue';
import InputField from '@/components/elements/base/InputField.vue';

export default defineComponent({
  props: {
    service: Object,
  },
  components: {
    SelectionList,
    InputField,
  },
  emits: ['service_form_close'],
  setup(props, { emit }) {
    const state = reactive({
      name: props.service ? props.service.name : '',
      domain: props.service ? props.service.domain : '',
      description: props.service ? props.service.description : '',
      sources: props.service ? props.service.sources : [],
      deliveryMethods: props.service ? props.service.deliveryMethods : [],
      needsPermissions: props.service ? props.service.needsPermissions : false,
      deprecated: false,
    });

    const rules = {
      name: { required },
      domain: { required },
      description: { required },
      sources: { required },
      deliveryMethods: { required },
    };

    const showAllErrors = ref(false);

    const v$ = useVuelidate(rules, state);

    const store = useStore();

    function submit(invalid) {
      if (!invalid) {
        // if this.service => its an update, else create new service
        if (props.service) {
          // merge old service for id and _links with updated service
          store.dispatch('update_service', { ...props.service, ...state });
        } else {
          store.dispatch('create_service', state);
        }
        emit('service_form_close');
      } else {
        showAllErrors.value = true;
      }
    }

    return {
      ...toRefs(state),
      v$,
      submit,
      showAllErrors,
    };
  },
  methods: {
    updateSources(sourceList) {
      // call validator
      this.v$.sources.$touch();
      // update sources value
      this.sources = sourceList;
    },
    updateDeliveryMethods(methodList) {
      // call validator
      this.v$.deliveryMethods.$touch();
      // update deliveryMethods value
      this.deliveryMethods = methodList;
    },
  },
});
</script>

<style scoped lang="sass">
.error
  border: 2px solid red
</style>
