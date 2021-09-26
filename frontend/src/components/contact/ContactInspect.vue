<template>
  <Popup size="medium" v-if="!loading && !editing" @closePopup="closeComponent">
    <div class="title-bar">
      <h1>{{ contact.firstName }}  {{ contact.lastName }}</h1>
      <div class="right">
        <div class="image-button" v-on:click="toggleEditing">
          <img src='@/assets/pencil.svg' alt="bewerken"/>
        </div>
        <div class="image-button" v-on:click="deleteContact">
          <img src='@/assets/trash.svg' alt="verwijderen"/>
        </div>
      </div>
    </div>
    <h4>email: {{contact.email}}</h4>
    <h4>Telefoonnummer: {{ contact.phoneNumber }}</h4>
  </Popup>
  <Popup size="medium" v-if="!loading && editing" @closePopup="toggleEditing">
    <h1>Contact aanpassen</h1>
    <ContactForm
      :contact="contact"
      :organisationId="organisationId"
      @contact_form_close="toggleEditing"
      @contact_form_submit="submit"
    />
  </Popup>
</template>

<script lang=ts>
import Popup from '@/components/elements/popup/popup.vue';
import ContactForm from '@/components/contact/ContactForm.vue';
import { useStore } from 'vuex';
import { computed, ref } from 'vue';

export default {
  name: 'ContactInspect',
  components: {
    Popup,
    ContactForm,
  },
  props: {
    contactId: null,
    organisationId: null,
  },
  emits: ['close_contact_inspect'],
  setup(props, { emit }) {
    const store = useStore();
    const editing = ref(false);

    const contact = computed(() => store.state.contactstore.contact.value);
    const loading = computed(() => store.state.contactstore.contact.loading);

    store.dispatch('fetch_contact',
      { organisationId: props.organisationId, contactId: props.contactId });

    const closeComponent = () => {
      emit('close_contact_inspect');
    };

    const toggleEditing = () => {
      editing.value = !editing.value;
    };

    const deleteContact = () => {
      // eslint-disable-next-line
      if (confirm('Bent u zeker?')) {
        store.dispatch('delete_contact', contact.value)
          .then(() => { emit('close_contact_inspect'); });
      }
    };

    const submit = (newContact) => {
      editing.value = !editing.value;
      store.dispatch('update_contact', { ...contact.value, ...newContact });
    };

    return {
      closeComponent,
      contact,
      loading,
      editing,
      toggleEditing,
      deleteContact,
      submit,
    };
  },
};

</script>
