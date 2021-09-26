<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form">
    <InputField label="Voornaam:*"
                type="text"
                v-model="firstName"
                placeholder="Jan"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                dataTestid="contact-firstName-input">
      <span v-if="v$.firstName.required.$invalid">
        Voornaam ontbreekt
      </span>
    </InputField>
    <InputField label="Achternaam:*"
                type="text"
                v-model="lastName"
                placeholder="Janssens"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                dataTestid="contact-lastName-input">
      <span v-if="v$.lastName.required.$invalid">
        Achternaam ontbreekt
      </span>
    </InputField>
    <InputField label="Email:*"
                type="text"
                v-model="email"
                placeholder="jan.janssens@gmail.com"
                :alwaysShowError="showAllErrors"
                dataTestid="contact-email-input">
      <span v-if="v$.email.required.$invalid">
        Email ontbreekt
      </span>
      <div v-else-if="v$.email.email.$invalid">
        Ongeldige email
      </div>
    </InputField>
    <InputField label="Telefoonnummer:*"
                type="text"
                v-model="phoneNumber"
                placeholder="+32-999-9999-99"
                :alwaysShowError="showAllErrors"
                dataTestid="contact-phone-input">
      <span v-if="v$.phoneNumber.required.$invalid">
        Telefoonnummer ontbreekt
      </span>
      <span v-else-if="v$.phoneNumber.phone.$invalid">
        Ongeldig telefoonnummer (enkel Belgische en Nederlandse nummers worden geaccepteerd)
      </span>
    </InputField>
    <button data-testid="submit-button"
            :class="{ 'button-primary-full-disabled': v$.$invalid } "
            class="button-primary-full floatRight"
            type="submit">Indienen</button>
    <div v-if="serverError" class="errorMsg-group">
      <div class="errorMsg">{{serverError}}</div>
    </div>
  </form>
</template>

<script lang=ts>
import useVuelidate from '@vuelidate/core';
import { required, email } from '@vuelidate/validators';
import { phoneRegex } from '@/helpers/regexhelper';
import {
  reactive,
  toRefs,
  defineComponent,
  ref,
} from 'vue';
import InputField from '@/components/elements/base/InputField.vue';

const phone = (value) => phoneRegex.test(value);

export default defineComponent({
  props: {
    contact: Object,
    organisationId: null,
  },
  components: {
    InputField,
  },
  emits: ['contact_form_submit'],
  setup(props, { emit }) {
    const state = reactive({
      firstName: props.contact ? props.contact.firstName : '',
      lastName: props.contact ? props.contact.lastName : '',
      email: props.contact ? props.contact.email : '',
      phoneNumber: props.contact ? props.contact.phoneNumber : '',
      serverError: null,
    });

    const showAllErrors = ref(false);

    const rules = {
      firstName: { required },
      lastName: { required },
      email: { required, email },
      phoneNumber: {
        required,
        phone,
      },
    };
    const v$ = useVuelidate(rules, state);

    function submit(invalid) {
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      emit('contact_form_submit', state);
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
.form-element
  width: auto
</style>
