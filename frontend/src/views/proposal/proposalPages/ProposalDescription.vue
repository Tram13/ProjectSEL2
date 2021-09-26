<template>
  <ProposalSidebar :activeItem="'Beschrijving'">
    <div class="content">
      <h2>Beschrijving</h2>
      <h3>Algemeen</h3>
      <p>
        Geef een algemene beschrijving van het project waarbinnen
        de aanvraag zich situeert (Wat is de huidige situatie?
        Waarom sluit je aan op MAGDA? wat is de ToBe situatie?).
        Geef ook mee voor wie het project bedoeld is
        (specifieer voor welke ambtenaren, burgers, ondernemingen, …
        het project een oplossing/verbetering zal zijn).
      </p>
      <InputField type="textarea"
                  placeholder="Geef hier uw beschrijving in."
                  v-model=proposal.businessContext
                  maxlength="2048"
                  dataTestid="business-context"
                  @input="updateDescriptionInStore($event)"
                  :disabled="!editing">
      </InputField>
      <div class="proposal-navigation-footer">
        <router-link :to="`/aanvraag/${proposal.id}/inleiding`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Inleiding</button>
        </router-link>
        <router-link :to="`/aanvraag/${proposal.id}/contact`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Contact</button>
        </router-link>
      </div>

    </div>

  </ProposalSidebar>
</template>

<script lang=ts>
/* eslint no-restricted-globals: 0, no-nested-ternary: 0 */
import { defineComponent, computed } from 'vue';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';

export default defineComponent({
  components: {
    ProposalSidebar,
    InputField,
  },
  setup() {
    const store = useStore();

    const proposal = computed(() => store.state.proposalstore.proposal.value);
    const editing = computed(() => store.state.proposalstore.editing);

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);

    // make sure store is up to date
    function updateDescriptionInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, businessContext: event.target.value });
    }
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'description',
        value: event.target.value,
      });
    }

    return {
      proposal,
      updateDescriptionInStore,
      feedback,
      updateFeedback,
      editing,
    };
  },
});
</script>

<style scoped lang="sass">
  .introduction-ul li
    list-style: disc
    margin-left: 2rem
    margin-top: 12px
  .input
    width: 35%
    margin-left: 10px
</style>
