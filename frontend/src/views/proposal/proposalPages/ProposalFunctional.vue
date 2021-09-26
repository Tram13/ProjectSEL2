<template>
  <ProposalSidebar :activeItem="'functioneel'">
    <div v-if="!proposal.loading" class="content">
      <h2>Functioneel/technisch opzet</h2>
      <div >
      <col>
      <p> Mijn applicatie staat buiten het netwerk van de
        Vlaamse overheid en ik heb FTP/batch diensten aangevraagd.</p>
      <col>
        <input type="radio" value="true" name="ftpBatch" v-model="ftp" id="ftpYes"
         :disabled="!editing">
        <label class="custom-control-label" for="ftpYes">Ja</label>
        <input type="radio" value="false" name="ftpBatch" v-model="ftp" id="ftpNo"
         :disabled="!editing">
        <label class="custom-control-label" for="ftpNo">Nee</label>
      </div>
      <div v-show="ftp === 'true'" style="margin-left:50px">
        <p> Om FTP-communicatie door de firewall toe te laten, moet MAGDA het IP-adres van uitgaande
          server van de afnemer kennen. Meer informatie hierover: zie Richtlijnen FTP-gebruik.
          Indien je deze niet bij de hand hebt, gelieve ons deze zo snel mogelijk te bezorgen,
          anders zullen uw batch/ftp diensten niet opgezet kunnen worden.
        </p>
        <InputField type="text"
                    label="Externe IP-adressen Test"
                    placeholder="Externe IP-adressen Test"
                    v-model=proposal.externIPTest
                    maxlength="512"
                    :alwaysShowError="showAllErrors"
                    dataTestid="extern-ip-test"
                    @input="updateExternIpTestInStore($event)"
                    :disabled="!editing">
       </InputField>
        <InputField type="text"
                    label="Externe IP-adressen Productie"
                    placeholder="Externe IP-adressen Productie"
                    v-model=proposal.externIPProd
                    maxlength="512"
                    :alwaysShowError="showAllErrors"
                    dataTestid="extern-ip-prod"
                    @input="updateExternIpProdInStore($event)"
                    :disabled="!editing">
       </InputField>
        <InputField type="text"
                    label="Externe IP-adressen Range Test"
                    placeholder="Externe IP-adressen Range Test"
                    v-model=proposal.externIpRangeTest
                    maxlength="512"
                    :alwaysShowError="showAllErrors"
                    dataTestid="extern-ip-range-test"
                    @input="updateExternIpRangeTestInStore($event)"
                    :disabled="!editing">
       </InputField>
       <InputField type="text"
                    label="Externe IP-adressen Range Productie"
                    placeholder="Externe IP-adressen Range Productie"
                    v-model=proposal.externIpRangeProd
                    maxlength="512"
                    :alwaysShowError="showAllErrors"
                    dataTestid="extern-ip-range-prod"
                    @input="updateExternIpRangeProdInStore($event)"
                    :disabled="!editing">
       </InputField>
        <div v-if="proposal.sshKey">
          <button @click="download('ssh_key', proposal.sshKey)">
            Download SSH key
          </button>
        </div>
        <div v-else>
          <p>Hier kan u uw SSH key opladen (ZIP of RAR)</p>
          <input type="file" id="sshFile" name="filename" @change="handleSshFileChoice($event)">
          <button @click="uploadSshFile()" :disabled="!editing">Upload bestand</button>
        </div>
      </div>
      <div >
        <col>
        <p> Betreft het een uitbreiding op een bestaande aansluiting?</p>
      <col>
        <input type="radio" value="true" name="exists" :disabled="!editing"
        v-model="extensionPreviousProposal" id="existsYes">
        <label class="custom-control-label" for="existsYes">Ja</label>
        <input type="radio" value="false" name="exists" :disabled="!editing"
        v-model="extensionPreviousProposal" id="existsNo">
        <label class="custom-control-label" for="existsNo">Nee</label>
      </div>
      <div v-show="extensionPreviousProposal === 'true'" style="margin-left:50px">
        <p>Een uitbreiding kan makkelijker gerealiseerd worden indien u hieronder al de
          nodige informatie meegeeft. Vul enkel in waar u zeker van bent.
        </p>
        <InputField type="text"
                    label="Oorspronkelijk TAN nummer"
                    placeholder="Oorspronkelijk TAN nummer"
                    v-model=proposal.originalTanNumber
                    maxlength="256"
                    dataTestid="original-tan"
                    @input="updateOriginalTanNumberInStore($event)"
                    :disabled="!editing">
       </InputField>
       <InputField type="text"
                   label="URI"
                   placeholder="URI"
                   v-model=proposal.originalUri
                   maxlength="256"
                   dataTestid="original-uri"
                   @input="updateOriginalUriInStore($event)"
                   :disabled="!editing">
       </InputField>
       <InputField type="text"
                   label="Legal Context"
                   placeholder="Legal Context"
                   v-model=proposal.originalLegalContext
                   maxlength="256"
                   dataTestid="original-legal-context"
                   @input="updateOriginalLegalContextInStore($event)"
                   :disabled="!editing">
       </InputField>
       <InputField type="text"
                   label="FTP-account"
                   placeholder="FTP-account"
                   v-model=proposal.ftpAccount
                   maxlength="256"
                   dataTestid="original-ftp-account"
                   @input="updateOriginalFtpAccountInStore($event)"
                   :disabled="!editing">
       </InputField>
      </div>
      <div >
      <col>
      <p>Wens je bepaalde elementen expliciet te hergebruiken uit bestaande aansluitingen?</p>
      <col>
        <input type="radio" value="true" name="reuseRadio" :disabled="!editing"
        v-model="reusePreviousProposal" id="reuseYes">
        <label class="custom-control-label" for="reuseYes">Ja</label>
        <input type="radio" value="false" name="reuseRadio" :disabled="!editing"
        v-model="reusePreviousProposal" id="reuseNo">
        <label class="custom-control-label" for="reuseNo">Nee</label>
      </div>
      <div v-show="reusePreviousProposal === 'true'" style="margin-left:50px">
        <InputField type="text"
                    label="URI"
                    placeholder="URI"
                    v-model=proposal.reuseUri
                    maxlength="256"
                    dataTestid="reuse-uri"
                    @input="updateReuseUriInStore($event)"
                    :disabled="!editing">
       </InputField>
       <p>Geef de Common Name (CN) op indien u via bijv. een service bus gebruik
          wenst te maken van een bestaand certificaat.</p>
       <InputField type="text"
                    label="CN (persoonsgevoelige diensten)"
                    placeholder="CN (persoonsgevoelige diensten)"
                    v-model=proposal.cnPersonalData
                    maxlength="256"
                    dataTestid="cn-personal"
                    @input="updateCnPersonalDataInStore($event)"
                    :disabled="!editing">
        </InputField>
        <InputField type="text"
                    label="CN (repertoriumdiensten)"
                    placeholder="CN (repertoriumdiensten)"
                    v-model=proposal.cnRepertorium
                    maxlength="256"
                    dataTestid="cn-repertorium"
                    @input="updateCnRepertoriumInStore($event)"
                    :disabled="!editing">
        </InputField>
        <InputField type="text"
                    label="CN (overige diensten)"
                    placeholder="CN (overige diensten)"
                    v-model=proposal.cnOther
                    maxlength="256"
                    dataTestid="cn-other"
                    @input="updateCnOtherInStore($event)"
                    :disabled="!editing">
        </InputField>
        <InputField type="text"
                   label="Legal Context"
                   placeholder="Legal Context"
                   v-model=proposal.reuseLegalContext
                   maxlength="256"
                   dataTestid="reuse-legal-context"
                   @input="updateReuseLegalContextInStore($event)"
                   :disabled="!editing">
       </InputField>
       <InputField type="text"
                   label="FTP-account"
                   placeholder="FTP-account"
                   v-model=proposal.reuseFtpAccount
                   maxlength="256"
                   dataTestid="reuse-ftp-account"
                   @input="updateReuseFtpAccountInStore($event)"
                   :disabled="!editing">
       </InputField>
      </div>
      <div >
      <col>
      <p>Indien u wenst gebruik te maken van de
        messages.magdadocumentendienst-01.00, duidt dit hier aan. </p>
      <col>
        <input type="radio" value="true" name="messages" v-model="magdaMessages" id="messagesYes"
         :disabled="!editing">
        <label class="custom-control-label" for="messagesYes">Ja</label>
        <input type="radio" value="false" name="messages" v-model="magdaMessages" id="messagesNo"
         :disabled="!editing">
        <label class="custom-control-label" for="messagesNo">Nee</label>
      </div>
      <div v-show="magdaMessages === 'true'" style="margin-left:50px">
      <div >
      <col>
      <p> Wenst u de Documentendienst te gebruiken via Webservice of Magda Online Pro?</p>
      <col>
        <input type="radio" value="WEB_SERVICE" name="option" v-model="onlineOption"
        id="webservices" :disabled="!editing">
        <label class="custom-control-label" for="webservices">Webservice</label>
        <input type="radio" value="MAGDA_ONLINE_PRO" :disabled="!editing"
        name="option" v-model="onlineOption" id="MAGDAonline">
        <label class="custom-control-label" for="MAGDAonline">MAGDA Online Pro</label>
      </div>
      <div v-show="onlineOption === 'MAGDA_ONLINE_PRO'" style="margin-left:50px">
        <div v-if="proposal.cooperationAgreement">
        <button
        @click="download('samenwerkingsovereenkomst', proposal.cooperationAgreement)">
          Download AIV
        </button>
        </div>
        <div v-else>
          <p> Gelieve de samenwerkingsovereenkomst AIV-Magda online op te laden. </p>
          <input type="file" id="samenwerking" @change="handleCooperationFileChoice($event)">
          <button @click="uploadCooperationFile()" :disabled="!editing">Upload bestand</button>
        </div>
        </div>
        <p> Duidt hieronder de verschillende opties aan die u wenst te gebruiken m.b.t. onze
          magdadocumentendienst, en vul vervolgens de nodige informatie verder aan. </p>
        <div class="grid-container">
          <div class="grid-item"><p>ebox burger (BOSA)</p></div>
          <div class="grid-item">
            <input type="checkbox" id="bosa" :disbled=!editing>
          </div>
          <div class="grid-item"><p>ebox onderneming (RSZ)</p></div>
          <div class="grid-item">
            <input type="checkbox" id="rsz" :disbled=!editing>
          </div>
          <div class="grid-item"><p>Brief (IPEX)</p></div>
          <div class="grid-item">
            <input type="checkbox" id="ipex" :disbled=!editing>
          </div>
        <div v-if="proposal.processingAgreement">
            <button @click="download('verwerkersovereenkomst', proposal.processingAgreement)">
            Download verwerkersovereenkomst
          </button>
        </div>
        <div v-else>
          <p> Gelieve de verwerkersovereenkomst AIV op te laden. </p>
          <input type="file" id="processing" @change="handleProcessingFileChoice($event)">
          <button @click="uploadProcessingFile()" :disabled="!editing">Upload bestand</button>
        </div>
      </div>
      </div>
      <h3> Architectuur </h3>
      <div>
          <p> Visualiseer hoe uw doeltoepassing connecteert met MAGDA WS en FTP.
            Maak duidelijk of er sprake is van externe hosting en/of hosting in de cloud,
            werken met een enterprise service bus of andere elementen.
            Het toevoegen van een overzichtelijke tekening en toelichting
            versnelt de interpretatie en controle van uw aanvraag. </p>
          <div v-if="proposal.architectureVisualization">
            <button
            @click="download('architectuur_visualisatie', proposal.architectureVisualization)">
            Download visualisatie
          </button>
          </div>
          <div v-else>
            <input type="file" id="visual" @change="handleVisualFileChoice($event)">
            <button @click="uploadVisualFile()" :disabled="!editing">Upload bestand</button>
          </div>
      </div>
      <InputField type="text"
                  label="Toelichting bij de architectuurtekening"
                  placeholder="Gelieve hier een toelichting te geven aan de gewenste architectuur."
                  v-model=proposal.architectureVisualizationExplanation
                  maxlength="256"
                  dataTestid="architecture-explanation"
                  @input="updateVisualizationExplanationInStore($event)"
                  :disabled="!editing">
       </InputField>
      <h2> Aanvullende informatie </h2>
      <p> Geef een benadering van het aantal
        opvragingen die u maximaal per dienst op jaarbasis zal doen,
        door hieronder de voor u geldende optie te selecteren.
      </p>
      <select v-model="selected" :disabled="!editing">
        <option value="<100K">Minder dan 100.000 aantal dossieropvragingen per jaar</option>
        <option value="100K-1M">Tussen 100.000 en 999.999 per jaar</option>
        <option value="1M-5M">Tussen 1 miljoen en 5 miljoen per jaar</option>
        <option value=">5M">Meer dan 5 miljoen per jaar</option>
      </select>
      <div >
      <col>
      <p> Zullen deze opvragingen regelmatig gespreid zijn over het ganse jaar?</p>
      <col>
        <input type="radio" value="true" name="spread" v-model="requestsAreSpread" id="spreadYes"
        :disabled="!editing">
        <label class="custom-control-label" for="spreadYes">Ja</label>
        <input type="radio" value="false" name="spread" v-model="requestsAreSpread" id="spreadNo"
        :disabled="!editing">
        <label class="custom-control-label" for="spreadNo">Nee</label>
      </div>
      <div v-show="requestsAreSpread === 'false'" style="margin-left:50px">
        <InputField type="text"
                    label="Geef aan hoeveel piekperiodes je verwacht op jaarbasis."
                    placeholder="0 - 300"
                    v-model=proposal.peaks
                    @input="updatePeaksInStore($event)"
                    maxlength="256"
                    dataTestid="peaks"
                    :disabled="!editing">
       </InputField>
      </div>
      <br>
      <span v-if="proposal.status !== 'draft'">Feedback:</span>
      <SpeechBubble v-if="proposal.status !== 'draft'" v-model="feedback.functional"
        @input="updateFeedback($event)">
      </SpeechBubble>
      </div>
    <div class="proposal-navigation-footer">
    <router-link
        :to="`/aanvraag/${proposal.id}/diensten`" class="nav_item">
        <button class="proposal-navigation-btn">Naar Diensten</button>
      </router-link>
      <router-link
        :to="`/aanvraag/${proposal.id}/streefdata`" class="nav_item">
        <button class="proposal-navigation-btn">Naar Streefdata</button>
      </router-link>
    </div>
  </ProposalSidebar>
</template>

<script lang=ts>
/* eslint no-restricted-globals: 0, no-nested-ternary: 0 */
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
    const ftp = ref(proposal.value ? proposal.value.extensionPreviousProposal : String(null));
    const extensionPreviousProposal = ref(proposal.value
      ? proposal.value.extensionPreviousProposal : String(null));
    const reusePreviousProposal = ref(proposal.value
      ? proposal.value.reusePreviousProposal : String(null));
    const magdaMessages = ref(proposal.value
      ? proposal.value.magdaMessages : String(null));
    const onlineOption = ref(proposal.value ? proposal.value.onlineOption : String(null));
    const requestsAreSpread = ref(proposal.value
      ? proposal.value.requestsAreSpread : String(null));

    // not ideal..
    const selected = ref(proposal.value ? (proposal.value.estimatedNumberOfRequests ? proposal.value.estimatedNumberOfRequests : '<100K') : '<100K');

    const editing = computed(() => store.state.proposalstore.editing);

    // watch for new proposal so personalInfo and inspection can be updated
    watch(proposal, (newProposal) => {
      // String value so v-model contains string and not an actual bool
      ftp.value = String(newProposal.ftp);
      extensionPreviousProposal.value = String(newProposal.extensionPreviousProposal);
      reusePreviousProposal.value = String(newProposal.reusePreviousProposal);
      magdaMessages.value = String(newProposal.magdaMessages);
      onlineOption.value = newProposal.onlineOption;
      requestsAreSpread.value = String(newProposal.requestsAreSpread);
      selected.value = newProposal.estimatedNumberOfRequests;
    });

    // make sure store is up to date
    function updateExternIpTestInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, externIpTest: event.target.value });
    }
    function updateExternIpProdInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, externIpProd: event.target.value });
    }
    function updateExternIpRangeTestInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, externIpRangeTest: event.target.value });
    }
    function updateExternIpRangeProdInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, externIpRangeProd: event.target.value });
    }
    function handleSshFileChoice(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, sshFileChoice: event.target.files[0] });
    }
    function uploadSshFile() {
      const formData = new FormData();
      formData.append('file', proposal.value.sshFileChoice);
      store.dispatch('add_ssh_to_proposal', { formData });
    }
    function updateOriginalTanNumberInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, originalTanNumber: event.target.value });
    }
    function updateOriginalUriInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, originalUri: event.target.value });
    }
    function updateOriginalLegalContextInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, originalLegalContext: event.target.value });
    }
    function updateOriginalFtpAccountInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, ftpAccount: event.target.value });
    }
    function updateReuseUriInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, reuseUri: event.target.value });
    }
    function updateCnPersonalDataInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, cnPersonalData: event.target.value });
    }
    function updateCnRepertoriumInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, cnRepertorium: event.target.value });
    }
    function updateCnOtherInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, cnOther: event.target.value });
    }
    function updateReuseLegalContextInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, reuseLegalContext: event.target.value });
    }
    function updateReuseFtpAccountInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, reuseFtpAccount: event.target.value });
    }
    function updateVisualizationExplanationInStore(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, architectureVisualizationExplanation: event.target.value });
    }
    function handleVisualFileChoice(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, visualFileChoice: event.target.files[0] });
    }
    function uploadVisualFile() {
      const formData = new FormData();
      formData.append('file', proposal.value.visualFileChoice);
      store.dispatch('add_architecture_visualization_to_proposal', { formData });
    }
    function handleCooperationFileChoice(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, cooperationFileChoice: event.target.files[0] });
    }
    function uploadCooperationFile() {
      const formData = new FormData();
      formData.append('file', proposal.value.cooperationFileChoice);
      store.dispatch('add_cooperation_agreement_to_proposal', { formData });
    }
    function handleProcessingFileChoice(event) {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, processingFileChoice: event.target.files[0] });
    }
    function uploadProcessingFile() {
      const formData = new FormData();
      formData.append('file', proposal.value.processingFileChoice);
      store.dispatch('add_processing_agreement_to_proposal', { formData });
    }
    function updatePeaksInStore(event) {
      if (!isNaN(event.target.value)) {
        store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, peaks: Number(event.target.value) });
      }
    }
    async function download(filename, fileInfo) {
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
      link.download = filename.concat(fileExtension);
      link.click();
      URL.revokeObjectURL(link.href);
    }

    // watch for changes and update if needed
    watch(ftp, (newFtp) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, ftp: newFtp }));
    watch(extensionPreviousProposal, (newExtensionPreviousProposal) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, extensionPreviousProposal: newExtensionPreviousProposal }));
    watch(reusePreviousProposal, (newReuse) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, reusePreviousProposal: newReuse }));
    watch(magdaMessages, (newMagdaMessages) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, magdaMessages: newMagdaMessages }));
    watch(onlineOption, (newOnlineOption) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, onlineOption: newOnlineOption }));
    watch(requestsAreSpread, (newRequestsAreSpread) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, requestsAreSpread: newRequestsAreSpread }));
    watch(selected, (newSelected) => store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, estimatedNumberOfRequests: newSelected }));

    const feedback = computed(() => store.state.proposalstore.proposal.value.feedback);
    function updateFeedback(event) {
      store.commit('UPDATE_FEEDBACK', {
        what: 'functional',
        value: event.target.value,
      });
    }
    return {
      proposal,
      ftp,
      extensionPreviousProposal,
      reusePreviousProposal,
      magdaMessages,
      onlineOption,
      requestsAreSpread,
      updateExternIpTestInStore,
      updateExternIpProdInStore,
      updateExternIpRangeTestInStore,
      updateExternIpRangeProdInStore,
      handleSshFileChoice,
      uploadSshFile,
      updateOriginalTanNumberInStore,
      updateOriginalUriInStore,
      updateOriginalLegalContextInStore,
      updateOriginalFtpAccountInStore,
      updateReuseUriInStore,
      updateCnPersonalDataInStore,
      updateCnRepertoriumInStore,
      updateCnOtherInStore,
      updateReuseLegalContextInStore,
      updateReuseFtpAccountInStore,
      handleVisualFileChoice,
      uploadVisualFile,
      updateVisualizationExplanationInStore,
      handleCooperationFileChoice,
      uploadCooperationFile,
      handleProcessingFileChoice,
      uploadProcessingFile,
      updatePeaksInStore,
      selected,
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
  .grid-container
    display: inline-grid
    grid-template-columns: auto auto
    grid-gap: 10px
    padding: 10px
  select
    display: flex
    border: 2px solid rgb(184, 184, 184)
    border-radius: 7px
    font-size: 16px
    padding: 10px 20px
    background: transparent
    flex: 1
    overflow: hidden
    width: 100%
    height: 40px
  select:disabled
    background: rgb(194, 194, 194)
    color: rgb(105, 105, 105)
</style>
