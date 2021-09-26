<template>
  <ProposalSidebar :activeItem="'streefdata'">
    <div class="content">
      <h2>Streefdata</h2>
      <p>Duidt hieronder een realistische streefdatum aan,
        zodat we uw aansluiting beter kunnen plannen.
        U kan een datum selecteren vandaag + 40 kalenderdagen.
        Opgelet, de effectieve planning en voltooiing van deze aansluiting
        is afhankelijk van de complexiteit van deze aanvraag en van de
        verwerkingscapaciteit van MAGDA.
      </p>
      <InputField type="date"
                  label="Streefdatum Test & Integration"
                  v-model=proposal.tiDeadline
                  dataTestid="ti-deadline"
                  @input="updatetiDeadlineInStore($event)"
                  :disabled="!editing">
       </InputField>
       <InputField type="date"
                  label="Streefdatum Productie"
                  v-model=proposal.deadline
                  dataTestid="deadline"
                  @input="updateDeadlineInStore($event)"
                  :disabled="!editing">
       </InputField>
       <InputField type="date"
                  label="Is er een juridische deadline om de connectie te maken?"
                  v-model=proposal.legalDeadline
                  dataTestid="legal-deadline"
                  @input="updateLegalDeadlineInStore($event)"
                  :disabled="!editing">
       </InputField>
      <InputField type="textarea"
                    label="Toelichting deadline"
                    placeholder="Gelieve hier wat meer uitleg te geven bij de deadline."
                    v-model=proposal.explanationDeadline
                    maxlength="512"
                    dataTestid="deadline-explanation"
                    @input="updateExplanationDeadlineInStore($event)"
                    :disabled="!editing">
       </InputField>
      <span v-if="proposal.status !== 'draft'">Feedback:</span>
      <SpeechBubble v-if="proposal.status !== 'draft'" v-model="feedback.supplement"
        @input="updateFeedback($event)">
      </SpeechBubble>
      <div class="proposal-navigation-footer">
        <router-link :to="`/aanvraag/${proposal.id}/functioneel`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Functioneel/technisch opzet</button>
        </router-link>
      </div>
    </div>

  </ProposalSidebar>
</template>

<script lang=ts>
import { defineComponent, computed } from 'vue';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';
import SpeechBubble from '@/components/elements/base/SpeechBubble.vue';

export default defineComponent({
  components: {
    ProposalSidebar,
    InputField,
    SpeechBubble,
  },
  setup() {
    const store = useStore();
    const proposal = computed(() => store.state.proposalstore.proposal.value);
    const editing = computed(() => store.state.proposalstore.editing);

    // make sure store is up to date
    function updateTiDeadlineInStore(event) {
      // ik heb het gevoel dat dit errors gaat geven... want die !== is niet logisch
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, tiDeadline: event.target.value });
    }
    function updateDeadlineInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, deadline: event.target.value });
    }
    function updateLegalDeadlineInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, legalDeadline: event.target.value });
    }
    function updateExplanationDeadlineInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, explanationDeadline: event.target.value });
    }

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'supplement',
        value: event.target.value,
      });
    }
    return {
      proposal,
      updateTiDeadlineInStore,
      updateDeadlineInStore,
      updateLegalDeadlineInStore,
      updateExplanationDeadlineInStore,
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
  .grid-container
    display: inline-grid
    grid-template-columns: auto auto
    grid-gap: 10px
    padding: 10px
</style>
