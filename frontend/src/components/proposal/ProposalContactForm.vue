<template xmlns="http://www.w3.org/1999/xhtml">
  <form v-if="contact !== undefined" @submit.prevent="submit" class="input-form">
    <p>Voornaam: {{contact.firstName}}</p>
    <p>Achternaam: {{contact.lastName}}</p>
    <p>E-mail: {{contact.email}}</p>
    <p>Telefoonnummer: {{contact.phoneNumber}}</p>
    <select v-model="role" data-Testid="contact-role-select">
      <option v-for="{ val, name } in roles.filter(x => !selectedRoles.includes(x.val))"
        v-bind:key="val" v-bind:value="val">
        {{ name }}
      </option>
    </select>
    <button :disabled="v$.$invalid" type="submit">Toevoegen</button>
  </form>
</template>

<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
} from 'vue';
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';

export default defineComponent({
  name: 'ProposalContact',
  props: {
    contact: Object,
    selectedRoles: Object,
  },
  emits: ['proposal_contact_form_submit'],
  setup(props, { emit }) {
    const state = reactive({
      role: '',
    });

    const roles = [
      { val: 'submitter', name: 'Indiener' },
      { val: 'business', name: 'Business' },
      { val: 'business_backup', name: 'Business (back-up)' },
      { val: 'technical', name: 'Technisch' },
      { val: 'technnical_backup', name: 'Techisch (back-up)' },
      { val: 'safety_consultant', name: 'Veiligheidsconsulent' },
      { val: 'service_provider', name: 'Dienstenleverancier' },
      { val: 'responsible_d2d_managent_customer', name: 'Verantwoordlijke Dagelijks Bestuur Afnemer' },
      { val: 'manager_geosecure', name: 'Beheer Geosecure (Documentendienst)' },
    ];

    const rules = {
      role: { required },
    };
    const v$ = useVuelidate(rules, state);

    function submit() {
      if (!v$.$invalid) {
        emit('proposal_contact_form_submit', state.role);
      }
    }

    return {
      submit,
      roles,
      ...toRefs(state),
      v$,
    };
  },
});
</script>
