<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form">
    <InputField label="Organisatie naam:*"
                type="text"
                v-model="organisationName"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                dataTestid="org-name-input">
      <span v-if="v$.organisationName.required.$invalid">
        Naam ontbreekt
      </span>
    </InputField>
    <InputField label="KBO nummer:*"
                type="text"
                v-model="kboNumber"
                :alwaysShowError="showAllErrors"
                placeholder="0123.321.123"
                dataTestid="org-kbo-input">
      <span v-if="v$.kboNumber.required.$invalid">
        KBO nummer ontbreekt
      </span>
      <span v-else-if="v$.kboNumber.kbo.$invalid">
        ongeldig KBO nummer
      </span>
    </InputField>
    <InputField label="OVO code:"
                type="text"
                v-model="ovoCode"
                :alwaysShowError="showAllErrors"
                dataTestid="org-ovo-input"
                placeholder="OVO999999">
      <span v-if="v$.ovoCode.ovo.$invalid">
        ongeldige OVO code
      </span>
    </InputField>
    <InputField label="NIS nummer:"
                type="text"
                v-model="nisNumber"
                :alwaysShowError="showAllErrors"
                dataTestid="org-nis-input"
                placeholder="99999">
      <span v-if="v$.nisNumber.nis.$invalid">
        ongeldig NIS nummer
      </span>
    </InputField>
    <InputField label="Dienstenleverancier:*"
                type="text"
                v-model="serviceProvider"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                dataTestid="org-provider-input">
      <span v-if="v$.serviceProvider.required.$invalid" class="errorMsg">
        dienstenleverancier ontbreekt
      </span>
    </InputField>
    <button data-testid="submit-button"
            type="submit"
            class="button-primary-full"
            :class="{ 'button-primary-full-disabled': v$.$invalid }">
            Indienen
    </button>
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
  computed,
} from 'vue';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';
import { kboRegex, ovoRegex, nisRegex } from '@/helpers/regexhelper';

const kbo = (value) => kboRegex.test(value);
const ovo = (value) => !value || ovoRegex.test(value);
const nis = (value) => !value || nisRegex.test(value);

export default defineComponent({
  components: {
    InputField,
  },
  props: {
    organisation: Object,
  },
  emits: ['organisation_form_close'],
  setup(props, { emit }) {
    const state = reactive({
      organisationName: props.organisation ? props.organisation.organisationName : '',
      kboNumber: props.organisation ? props.organisation.kboNumber : '',
      ovoCode: props.organisation ? props.organisation.ovoCode : '',
      nisNumber: props.organisation ? props.organisation.nisNumber : '',
      serviceProvider: props.organisation ? props.organisation.serviceProvider : '',
    });

    const rules = {
      organisationName: { required },
      kboNumber: {
        required,
        kbo,
      },
      ovoCode: {
        ovo,
      },
      nisNumber: {
        nis,
      },
      serviceProvider: { required },
    };

    const showAllErrors = ref(false);

    const v$ = useVuelidate(rules, state);

    const store = useStore();

    const authUser = computed(() => store.state.authenticationstore.user);

    function submit(invalid) {
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      // if this.organisation => its an update, else create new organisation
      if (props.organisation) {
        // remove . in kbo
        state.kboNumber = state.kboNumber.replace('.', '');
        // merge the old organisation with new fields for id and _links
        store.dispatch('update_organisation', { ...props.organisation, ...state });
        // close the form
        emit('organisation_form_close');
      } else {
        // remove . in kbo 2 times
        state.kboNumber = state.kboNumber.replace('.', '').replace('.', '');
        store.dispatch('create_organisation', state)
          .then((id) => this.$router.push({ name: 'InspectOrganisation', params: { id } }))
          .then(() => store.dispatch('get_user_organisations', authUser))
          .catch(console.error);
        emit('organisation_form_close');
      }
    }

    return {
      showAllErrors,
      ...toRefs(state),
      v$,
      submit,
    };
  },
  methods: {
  },
});
</script>

<style scoped lang="sass">
.error
  border: 2px solid red
</style>
