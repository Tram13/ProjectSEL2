<template>
  <router-link data-testid="add-user-button" :to="{name: ''}"
  class="small-bottom-margin">
    <button :disabled=!editing @click="showContactsPopup = true">Contact Toevoegen</button>
  </router-link>
  <data-table :entries="displayedContacts" :headers="headers" :updateFunction="updateFunction">
    <template v-slot:del="item">
      <button v-if="displayedContacts.length > 1"
              :disabled=!editing
              @click="$emit('remove-proposal-contact', item.entry)" type="button">
        Verwijder
      </button>
    </template>
  </data-table>
  <Popup v-if="showPopup" @closePopup="showPopup = false">
    <ContactForm
      @contact_form_close="showPopup = false"
      @contact_form_submit="submitContact"/>
  </Popup>
  <Popup v-if="showRolePickerPopup" @closePopup="showRolePickerPopup = false">
    <ProposalContactForm
      :contact="rolePickerContact"
      @proposal_contact_form_submit="submitProposalContact"
      :selectedRoles="roles"/>
  </Popup>
  <Popup v-if="showContactsPopup" @closePopup="showContactsPopup = false">
    <router-link data-testid="add-contact-button" :to="{name: ''}"
      class="small-bottom-margin">
      <button @click="createContactInPopup()">Contact Aanmaken</button>
    </router-link>
    <data-table
      :entries="organisationContacts"
      :headers="organisationContactsHeaders"
      :updateFunction="updateOrganisationContacts"
    >
      <template v-slot:add="item">
        <button @click="addProposalContact(item.entry)">Voeg Toe</button>
      </template>
    </data-table>
  </Popup>
</template>

<script lang=ts>
import { defineComponent, ref, computed } from 'vue';
import { useStore } from 'vuex';
import DataTable from '@/components/elements/DataTable.vue';
import Popup from '@/components/elements/popup/popup.vue';
import ContactForm from '@/components/contact/ContactForm.vue';
import ProposalContactForm from '@/components/proposal/ProposalContactForm.vue';
import { createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import { contactRoleTranslator } from '@/helpers/fieldTranslators';

export default defineComponent({
  components: {
    ProposalContactForm,
    DataTable,
    Popup,
    ContactForm,
  },
  props: {
    contacts: Array,
    organisationContacts: Array,
    organisation: Object,
    contactsMapper: Function,
    editing: Boolean,
  },
  emits: ['add-proposal-contact', 'remove-proposal-contact'],
  setup(props, { emit }) {
    const organisationContactsHeaders = [
      { name: 'Voornaam', propName: 'firstName' },
      { name: 'Achternaam', propName: 'lastName' },
      { name: 'Email', propName: 'email' },
      { name: 'Telefoonnummer', propName: 'phoneNumber' },
      { name: '', propName: 'add' },
    ];
    const headers = [
      { name: 'Rol', propName: 'role' },
      { name: 'Voornaam', propName: 'firstName' },
      { name: 'Achternaam', propName: 'lastName' },
      { name: 'Email', propName: 'email' },
      { name: 'Telefoonnummer', propName: 'phoneNumber' },
      { name: '', propName: 'del' },
    ];
    const showPopup = ref(false);
    const showContactsPopup = ref(false);
    let outOfDate = true;
    const store = useStore();

    // indices for pagination
    const sliceIndices = ref({ start: 0, end: 0 });
    const displayedContacts = computed(() => {
      let output = props.contacts.slice(sliceIndices.value.start, sliceIndices.value.end);
      if (props.contactsMapper && outOfDate) {
        outOfDate = false;
        output = props.contactsMapper(output);
      }
      return output.map((contact) => ({ ...contact, role: contactRoleTranslator[contact.role] }));
    });

    const updateOrganisationContacts = createUpdateFunctionExtraParams(
      'CLEAR_CONTACTS',
      'fetch_contacts_pagination',
      { organisationId: props.organisation.id },
    );

    const roles = computed(() => props.contacts.map((_) => _.role));

    function createContactInPopup() {
      showPopup.value = true;
      showContactsPopup.value = false;
    }

    async function updateFunction(skip, limit) {
      outOfDate = true;
      /*
      * End may be out of bounds, it doesn't throw an error.
      * Clipping end to avoid going out of bounds makes updating the slice
      * trickier, so just don't...
      * (vmedaert)
      */
      const end = skip + limit;
      sliceIndices.value = { start: skip, end };
      return props.contacts.length;
    }

    const submitContact = (contact) => {
      store.dispatch(
        'create_contact',
        {
          contact,
          organisationId: props.organisation.id,
        },
      )
        .then(() => {
          showPopup.value = false;
          showContactsPopup.value = true;
        }); // close the form
    };

    const showRolePickerPopup = ref(false);
    const rolePickerContact = ref(null);

    function addProposalContact(contact) {
      rolePickerContact.value = contact;
      showContactsPopup.value = false;
      showRolePickerPopup.value = true;
    }

    function submitProposalContact(role) {
      showRolePickerPopup.value = false;
      emit('add-proposal-contact', { contact: rolePickerContact.value, role });
    }

    return {
      roles,
      organisationContactsHeaders,
      headers,
      updateFunction,
      showPopup,
      showContactsPopup,
      submitContact,
      updateOrganisationContacts,
      rolePickerContact,
      showRolePickerPopup,
      addProposalContact,
      submitProposalContact,
      createContactInPopup,
      displayedContacts,
    };
  },
});
</script>

<style scoped lang="sass">
 #liquidelement
   width: 96%
   padding: 0 2%
</style>
