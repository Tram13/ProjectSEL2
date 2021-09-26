<template>
  <ProposalSidebar :activeItem="'contactgegevens'">
    <div class="content">
      <h1>Contact</h1>
      <p>
        Vul hieronder de contactpersonen voor dit aansluitingsdossier aan.
        Deze personen zullen op de hoogte gehouden worden van het aansluitingstraject.
        U dient telkens minstens 1 contactpersoon toe te voegen voor de rollen:
        Indiener, Business, Technisch, Verantwoordelijke Dagelijks Bestuur Afnemer en
        Informatieveiligheidsconsulent/DPO.
      </p>
      <ProposalContactSelector
        v-if="selectedOrganisation"
        :contacts="proposal.contacts"
        :organisation="selectedOrganisation.organisation"
        :organisationContacts="organisationContacts"
        :contactsMapper="fetchContacts"
        @add-proposal-contact="addProposalContact"
        @remove-proposal-contact="removeProposalContact"
        :editing="editing"
      />
      <div v-if="!containsSubmitter" class="errorMsg-group">
        <div class="errorMsg">
          indiener ontbreekt
        </div>
      </div>
      <span v-if="proposal.status !== 'draft'">Feedback:</span>
      <SpeechBubble v-if="proposal.status !== 'draft'" v-model="feedback.contact"
        @input="updateFeedback($event)">
      </SpeechBubble>
      <div class="proposal-navigation-footer">
        <router-link :to="`/aanvraag/${proposal.id}/beschrijving`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Beschrijving</button>
        </router-link>
        <router-link :to="`/aanvraag/${proposal.id}/context`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Juridische Context</button>
        </router-link>
      </div>
    </div>
  </ProposalSidebar>
</template>

<script lang=ts>

import { defineComponent, computed } from 'vue';
import { useStore } from 'vuex';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';
import ProposalContactSelector from '@/components/proposal/ProposalContactSelector.vue';
import { reverseContactRoleTranslator } from '@/helpers/fieldTranslators';
import SpeechBubble from '@/components/elements/base/SpeechBubble.vue';

export default defineComponent({
  components: {
    ProposalSidebar,
    ProposalContactSelector,
    SpeechBubble,
  },
  setup() {
    const store = useStore();

    const proposal = computed(() => store.state.proposalstore.proposal.value);
    const organisationContacts = computed(() => store.state.contactstore.contacts.value);
    const selectedOrganisation = computed(
      () => store.state.authenticationstore.selectedOrganisation,
    );
    const containsSubmitter = computed(
      () => store.state.proposalstore.proposal.value.contacts.findIndex(
        (_) => _.role === 'submitter',
      ) !== -1,
    );
    const editing = computed(() => store.state.proposalstore.editing);

    function addProposalContact(data) {
      store.commit('ADD_PROPOSAL_CONTACT', data);
    }

    function removeProposalContact(contact) {
      store.commit('DEL_PROPOSAL_CONTACT', {
        ...contact,
        role: reverseContactRoleTranslator[contact.role],
      });
    }

    function fetchContacts(contacts) {
      store.dispatch('fetch_proposal_contacts', contacts);
      return contacts;
    }

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'contact',
        value: event.target.value,
      });
    }

    return {
      proposal,
      organisationContacts,
      addProposalContact,
      removeProposalContact,
      selectedOrganisation,
      fetchContacts,
      containsSubmitter,
      editing,
      feedback,
      updateFeedback,
    };
  },
});
</script>

<style scoped lang="sass">
 #liquidelement
   width: 96%
   padding: 0 2%
</style>
