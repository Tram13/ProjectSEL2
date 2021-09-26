<template>
  <div class="content-container">
    <div class="content">
      <h1>Aansluiten op MAGDA.</h1>
      <h2>Start hier je aanvraag</h2>
      <p>
        Wil je gebruik maken van de MAGDA diensten? Dan ben je op de goede weg!
        Vul hieronder een werknaam in voor je aanvraag en wij gidsen je door
        het aansluitingsformulier. Geen zorgen, Je kan deze naam op elke moment
        wijzigen of je aanvraag annuleren.
      </p>
      <p>
        *Extra informatie over het aansluitingsproces vind
        je op Aansluiten op MAGDA.
      </p>
      <ProposalForm
        @proposal_form_close="createProposal"/>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">

import { defineComponent, computed } from 'vue';
import { useRouter } from 'vue-router';
import Footer from '@/components/Footer.vue';
import ProposalForm from '@/components/proposal/ProposalNew.vue';
import { useStore } from 'vuex';

export default defineComponent({
  components: {
    Footer,
    ProposalForm,
  },
  setup() {
    const router = useRouter();
    const store = useStore();
    const newProposal = computed(() => store.state.proposalstore.proposal);
    async function createProposal(state, organisation) {
      const userContact = store.state.contactstore.contacts.value.find(
        (contact) => contact.email === store.state.authenticationstore.user.email,
      );
      if (userContact) {
        state.contacts.push({ contactId: userContact.id, role: 'submitter' });
      } else {
        const contact = {
          email: store.state.authenticationstore.user.email,
          firstName: store.state.authenticationstore.user.firstName,
          lastName: store.state.authenticationstore.user.lastName,
          phoneNumber: state.phoneNumber,
        };
        await store.dispatch('create_contact', { contact, organisationId: organisation.value.organisation.id });
        state.contacts.push({ contactId: store.state.contactstore.contact.value.id, role: 'submitter' });
      }
      await store.dispatch(
        'create_proposal',
        { ...state, organisationId: organisation.value.organisation.id },
      );
      router.push({
        name: 'InspectProposalIntroduction',
        params: { id: newProposal.value.value.id },
      });
    }
    return {
      createProposal,
    };
  },
});
</script>

<style scoped lang="sass">
.new-proposal-container
  flex: 1
  display: flex
  flex-wrap: wrap
  flex-direction: row
  justify-content: center
  align-items: center
  padding: 70px
.new-proposal-content
  flex: 1
  min-width: 300px
  max-width: 500px
.magda-connect-drawing
  flex: 1.5
  min-width: 500px
  max-width: 600px
</style>
