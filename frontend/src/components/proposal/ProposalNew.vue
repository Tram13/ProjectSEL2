<template>
  <div v-if="!organisation || !organisation.organisation" class="errorMsg-group">
    <div class="errorMsg">
      selecteer een organisatie
    </div>
  </div>
  <div v-else-if="!organisation.organisation.approved" class="errorMsg-group">
    <div class="errorMsg">
      uw organisatie is nog niet goedgekeurd
    </div>
  </div>
  <form v-else @submit.prevent="submit()">
    <InputField label="Toepassingsnaam:*"
                type="text"
                v-model="name"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                data-Testid="proposal-name-input">
      <span v-if="v$.name.required.$invalid">
        Naam ontbreekt
      </span>
    </InputField>
    <br/>
    <InputField
                label="Telefoonnummer:*"
                type="text"
                v-model="phoneNumber"
                placeholder="+32471291344"
                :alwaysShowError="showAllErrors"
                data-Testid="contact-phone-input">
      <span v-if="v$.phoneNumber.required.$invalid">
        Telefoonnummer ontbreekt
      </span>
      <span v-else-if="v$.phoneNumber.phone.$invalid">
        Ongeldig telefoonnummer (enkel Belgische en Nederlandse nummers worden geaccepteerd)
      </span>
    </InputField>
    <br v-if="!userIsContact"/>
    <button :class="{ 'button-primary-full-disabled': v$.$invalid }"
            type="submit"
            class="button-primary-full"
            data-Testid="submit-button">
      Aan de slag!
    </button>
  </form>
</template>

<script lang=ts>
/* eslint no-underscore-dangle: 0 */
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import { phoneRegex } from '@/helpers/regexhelper';
import {
  reactive,
  toRefs,
  defineComponent,
  computed,
  ref,
  watch,
} from 'vue';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';

export default defineComponent({
  components: {
    InputField,
  },
  emits: ['proposal_form_close'],
  setup(props, { emit }) {
    const state = reactive({
      name: '',
      contacts: [],
      phoneNumber: '',
    });

    const store = useStore();

    const organisation = computed(
      () => store.state.authenticationstore.selectedOrganisation,
    );

    const organisationContacts = computed(() => store.state.contactstore.contacts.value);
    const userIsContact = ref(false);

    // user is a contact in the organisation => other flow
    if (!organisation.value) {
      // add watcher that will fetch contacts if organisation is known (needed if page is reloaded)
      watch(organisation, (newOrganisation) => {
        if (newOrganisation) {
          store.commit('CLEAR_CONTACTS');
          store.dispatch('fetch_contacts', newOrganisation.organisation.id)
            .then(() => {
              const userContact = store.state.contactstore.contacts.value.find(
                (contact) => contact.email === store.state.authenticationstore.user.email,
              );
              if (userContact) {
                // set the info
                userIsContact.value = true;
                state.phoneNumber = userContact.phoneNumber;
              }
              // else a contact will be made in submit
            });
        }
      });
    } else {
      // else just fetch contacts
      store.commit('CLEAR_CONTACTS');
      store.dispatch('fetch_contacts', organisation.value.organisation.id)
        .then(() => {
          const userContact = store.state.contactstore.contacts.value.find(
            (contact) => contact.email === store.state.authenticationstore.user.email,
          );
          if (userContact) {
            // set the info
            userIsContact.value = true;
            state.phoneNumber = userContact.phoneNumber;
          }
        });
      // else a contact will be created in submit
    }

    const phone = (value) => phoneRegex.test(value);

    const rules = {
      name: { required },
      phoneNumber: {
        required,
        phone,
      },
    };

    const showAllErrors = ref(false);

    const v$ = useVuelidate(rules, state);

    function submit() {
      if (v$._value.$invalid) {
        showAllErrors.value = true;
      } else {
        emit('proposal_form_close', state, organisation);
      }
    }

    return {
      ...toRefs(state),
      v$,
      submit,
      organisation,
      organisationContacts,
      showAllErrors,
      userIsContact,
    };
  },
});
</script>

<style scoped lang="sass">
form
  display: grid
  grid-template-columns: 150px 200px
  grid-gap: 10px

.error
  border: 2px solid red

input
  padding: 5px 10px
  font-size: 18px

select
  max-width: 300px
</style>
