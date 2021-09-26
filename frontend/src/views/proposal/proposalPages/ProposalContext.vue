<template>
  <ProposalSidebar :activeItem="'context'">
    <div class="content">
      <h2>Juridische Context</h2>
      <InputField type="text"
                    label="Heeft het project waarbinnen de aanvraag zich situeert een juridische
        basis (is het gestoeld op een wet, decreet, een opgelegde taak, …)?"
                    placeholder="Vul hier in of je aanvraag een juridische context heeft,
                     en zo ja, welke."
                    v-model=proposal.legalContext
                    maxlength="216"
                    dataTestid="legal-context"
                    @input="updateLegalContextInStore($event)"
                    :disabled="!editing">
       </InputField>
       <InputField type="textarea"
                    label=" Geef hieronder het verwerkingsdoeleinde waarvoor
                    je de gevraagde gegevens
        zult gebruiken. Indien het gaat om persoonsgegevens, herneem het
        verwerkingsdoeleinde (finaliteit) waarvan sprake in de
        machtiging/beraadslaging/protocol waarmee je juridische toegang
        verleend wordt tot de gevraagde gegevens."
                    placeholder="Geef hier meer uitleg bij de juridische context."
                    v-model=proposal.contextExplanation
                    maxlength="216"
                    dataTestid="legal-context-explanation"
                    @input="updateLegalDescriptionInStore($event)"
                    :disabled="!editing">
       </InputField>
      <div>
        <col>
        <p>Inspectiedienst?</p>
        <col>
        <div>
          <input type="radio" id="inspectionYes" name="inspection"
          value="true" v-model="inspection" :disabled="!editing">
          <label for="inspectionYes">Ja</label>
          <input type="radio" id="inspectionNo" name="inspection"
          value="false" v-model="inspection" :disabled="!editing">
          <label for="inspectionNo">Nee</label>
        </div>
      </div>
      <div>
      <div>
        <col>
        <p>Wens je met deze aanvraag persoonsgegevens op te halen?</p>
        <col>
        <input type="radio" id="personalInfoYes" value="true" :disabled="!editing"
                            name="personalInfoCheck" v-model="personalInfo">
        <label>Ja</label>
        <input type="radio" id="personalInfoNo" value="false" :disabled="!editing"
                            name="personalInfoCheck" v-model="personalInfo">
        <label>Nee</label>
      </div>
    <div v-show="personalInfo === 'true'" style="margin-left:50px">
      <div v-if="proposal.authorizationPersonalData">
        <button @click="download()">Download authorisatie</button>
      </div>
      <div v-else>
        <p> Voeg je machtiging voor het opvragen van persoonsgegevens toe.</p>
        <input type="file" id="myFile" name="filename"
        @change="handleDataAuthFileChoice($event)">
        <button @click="uploadDataAuthFile()" :disabled="!editing">Upload bestand</button>
      </div>
    </div>
      <span v-if="proposal.status !== 'draft'">Feedback:</span>
      <SpeechBubble v-if="proposal.status !== 'draft'" v-model="feedback.context"
        @input="updateFeedback($event)">
      </SpeechBubble>
      </div>
      <br>
      <div class="proposal-navigation-footer">
        <router-link :to="`/aanvraag/${proposal.id}/contact`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Contact</button>
        </router-link>
        <router-link :to="`/aanvraag/${proposal.id}/pakketten`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Pakketten</button>
        </router-link>
      </div>
    </div>

  </ProposalSidebar>
</template>

<script lang=ts>
import {
  defineComponent, computed, ref, watch,
} from 'vue';
import axios from 'axios';
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
    const personalInfo = ref(proposal.value ? proposal.value.requiresPersonalData : String(null));
    const inspection = ref(proposal.value ? proposal.value.inspection : String(null));

    const editing = computed(() => store.state.proposalstore.editing);

    // watch for new proposal so personalInfo and inspection can be updated
    watch(proposal, (newProposal) => {
      // String value so v-model contains string and not an actual bool
      personalInfo.value = String(newProposal.requiresPersonalData);
      inspection.value = String(newProposal.inspection);
    });

    // make sure store is up to date
    function updateLegalDescriptionInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, purposeRequestedData: event.target.value });
    }
    function updateLegalContextInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, legalContext: event.target.value });
    }
    function handleDataAuthFileChoice(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, authFileChoice: event.target.files[0] });
    }
    function uploadDataAuthFile() {
      const formData = new FormData();
      formData.append('file', proposal.value.authFileChoice);
      store.dispatch('add_personal_data_authorization_to_proposal', { formData });
    }
    async function download() {
      const fileInfo = proposal.value.authorizationPersonalData;
      let fileLocation;
      if (typeof fileInfo === 'number') {
        await store.dispatch('set_file', fileInfo)
          .then(() => {
            const file = computed(() => store.state.proposalstore.fileInfo);
            fileLocation = computed(() => file.value.fileLocation);
          });
      } else {
        fileLocation = computed(() => fileInfo.fileLocation);
      }
      const data = await axios.get(fileLocation.value, { responseType: 'blob' });
      const blob = new Blob([data.data]);
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      const fileExtension = fileLocation.value.substring(fileLocation.value.search(/\.[^.]+$/g));
      link.download = 'authorisatie_persoonlijke_data'.concat(fileExtension);
      link.click();
      URL.revokeObjectURL(link.href);
    }

    // watch for changes and update if needed
    watch(inspection, (newInspection) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, inspection: newInspection }));
    watch(personalInfo, (newInfo) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, requiresPersonalData: newInfo }));

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'context',
        value: event.target.value,
      });
    }

    return {
      proposal,
      personalInfo,
      inspection,
      updateLegalDescriptionInStore,
      updateLegalContextInStore,
      handleDataAuthFileChoice,
      uploadDataAuthFile,
      download,
      editing,
      feedback,
      updateFeedback,
    };
  },
});
</script>

<style scoped lang="sass">
  .introduction-ul li
    list-style: disc
    margin-left: 2rem
    margin-top: 12px
</style>
