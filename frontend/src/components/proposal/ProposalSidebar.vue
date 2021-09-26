<template>
  <div v-if="proposal && organisation" class="proposal-wrapper">
      <div class="proposal-progress-bar">
        <div style="margin-left:10px; height: 30%;">
          <h2>
            {{proposal.name}}
          </h2>
          <span>laatst bewerkt op {{ formatDate(proposal.lastUpdated) }}</span>
          <p> {{ "status: " + proposalStatusTranslator[proposal.status] }}</p>
          <button class="button-primary-full"
           v-if="!editing && proposal.status!=='accepted' && proposal.status!=='in review'"
           @click="toggleEditing()">Bewerken
          </button>
        </div>
        <nav class="progress-nav">
          <ul style="height: 100%">
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/inleiding`"
                 class="nav-progress-item"
                 :active-class="introduction ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Inleiding</label>
                <div class="progress-icon-container">
                  <img v-if="introduction" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>

              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/beschrijving`"
                 class="nav-progress-item"
                 :active-class="description ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Beschrijving</label>
                <div class="progress-icon-container">
                  <img v-if="description" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/contact`"
                 class="nav-progress-item"
                 :active-class="contact ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Contact</label>
                <div class="progress-icon-container">
                  <img v-if="contact" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/context`"
                 class="nav-progress-item"
                 :active-class="context ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Juridische Context</label>
                <div class="progress-icon-container">
                  <img v-if="context" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/pakketten`"
                 class="nav-progress-item"
                 :active-class="packages ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Pakketten</label>
                <div class="progress-icon-container">
                  <img v-if="packages" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/diensten`"
                 class="nav-progress-item"
                 :active-class="services ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Diensten</label>
                <div class="progress-icon-container">
                  <img v-if="services" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/functioneel`"
                 class="nav-progress-item"
                 :active-class="functional ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                <label>Functioneel / technisch opzet</label>
                <div class="progress-icon-container">
                  <img v-if="functional" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li>
              <router-link :to="`/aanvraag/${proposal.id}/streefdata`"
                 class="nav-progress-item"
                 :active-class="supplement ? 'nav-progress-item-check'
                 : 'nav-progress-item-cross'">
                  <label>Streefdata</label>
                <div class="progress-icon-container">
                  <img v-if="supplement" src="@/assets/check.svg" alt="state-icon"/>
                  <img v-else src="@/assets/cross.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
            <li v-if="proposal.status==='accepted'">
              <router-link :to="`/aanvraag/${proposal.id}/certificaten`" class="nav-progress-item">
                <label>Maak certificaat</label>
                <div class="progress-icon-container">
                  <img src="@/assets/check.svg" alt="state-icon"/>
                </div>
              </router-link>
            </li>
          </ul>
        </nav>
        <div style="height: 10%;position: relative">

          <button class="container_buttons submit-btn" @click="submitProposal()"
                  v-if="proposal.status==='draft'" :disabled="!(introduction && description &&
                  contact && context && packages && services
                  && functional && supplement)"
          >Indienen</button>

          <div class="container_buttons"
               v-if="(proposal.status==='in_review'||proposal.status==='pending_feedback')
                  && (user.role === 'admin' || user.role === 'employee')">
            <button class="submit-btn" @click="voteProposal(1)"
            >Goedkeuren</button>

            <button class="submit-btn" @click="voteProposal(2)"
            >afkeuren</button>
          </div>

        </div>
      </div>
      <div class="proposal-content">
        <slot></slot>
        <Footer/>
      </div>
  </div>
  <div v-else>
    <pulse-loader color="#FFE615"></pulse-loader>
  </div>
</template>

<script lang=ts>
import { defineComponent, computed } from 'vue';
import Footer from '@/components/Footer.vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import { proposalStatusTranslator } from '@/helpers/fieldTranslators';
import formatDate from '@/helpers/datehelpers';

export default defineComponent({
  components: {
    Footer,
    PulseLoader,
  },
  props: {
    activeItem: String,
  },
  setup() {
    const store = useStore();
    const route = useRoute();

    const proposal = computed(() => store.state.proposalstore.proposal.value);
    const organisation = computed(() => store.state.proposalstore.proposalOrganisation.value);
    const editing = computed(() => store.state.proposalstore.editing);
    const user = computed(() => store.state.authenticationstore.user);
    function toggleEditing() {
      store.commit('SET_EDIT', true);
    }

    // ============ For V and X =============
    const introduction = computed(() => true);

    const description = computed(() => proposal.value.businessContext);

    const contact = computed(() => proposal.value.contacts.length >= 2);

    const context = computed(() => {
      const legalContext = proposal.value.legalContext !== '';
      const purposeRequestedData = proposal.value.purposeRequestedData !== '';
      const requiresPersonalData = proposal.value.requiresPersonalData !== 'null';
      const inspection = proposal.value.inspection !== null;
      if (proposal.value.requiresPersonalData === 'true') {
        return legalContext && purposeRequestedData && proposal.value.authorizationPersonalData
          && requiresPersonalData && inspection;
      }
      return legalContext && purposeRequestedData && requiresPersonalData && inspection;
    });

    const packages = computed(() => proposal.value.packages.length
       || proposal.value.services.length);

    const services = computed(() => proposal.value.packages.length
       || proposal.value.services.length);

    const functional = computed(() => proposal.value.architectureVisualization);

    const supplement = computed(() => proposal.value.tiDeadline && proposal.value.deadline
      && proposal.value.legalDeadline && proposal.value.explanationDeadline);

    // END ============ For V and X =============

    function submitProposal() {
      store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, status: 'in_review' });
      store.dispatch('update_proposal', proposal.value);
      this.$router.push({ name: 'Proposal' });
    }

    function voteProposal(vote) {
      if (user.value.role === 'admin' || user.value.role === 'employee') {
        const status = (vote === 1) ? 'accepted' : 'denied';
        store.commit('SET_EDIT_PROPOSAL', { ...proposal.value, status });
        store.dispatch('update_proposal', proposal.value);
        this.$router.push({ name: 'Proposal' });
      }
    }

    if (route && (!proposal.value || proposal.value.id !== parseInt(route.params.id, 10))) {
      store.dispatch('fetch_proposal', route.params.id);
    }

    const proposalSaver = setInterval(() => {
      if (proposal.value) store.dispatch('update_proposal', proposal.value);
    }, 20000);

    return {
      proposal,
      organisation,
      submitProposal,
      proposalStatusTranslator,
      formatDate,
      proposalSaver,
      toggleEditing,
      editing,
      introduction,
      description,
      contact,
      context,
      packages,
      services,
      functional,
      supplement,
      user,
      voteProposal,
    };
  },
  beforeUnmount() {
    const store = useStore();
    /*
    * checks whether proposal is valid (here valid means the backend won't throw error 400-499)
    */
    const validProposal = computed(() => {
      if (store.state.proposalstore.proposal.value) {
        return store.state.proposalstore.proposal.value.contacts.findIndex((_) => _.role === 'submitter') !== -1;
      }
      return null;
    });

    if (validProposal.value) {
      clearInterval(this.proposalSaver);
      store.dispatch('update_proposal', store.state.proposalstore.proposal.value);
    }
  },
});
</script>

<style scoped lang="sass">
.nav-progress-item-cross
  box-shadow: 3px 0 0 0 #FF0000
  background: linear-gradient(to right, rgba(255, 0, 0, 0), rgb(191 191 191 / 30%))
  color: rgb(36, 36, 36)

.nav-progress-item-check
  box-shadow: 3px 0 0 0 #4ba144
  background: linear-gradient(to right, rgba(255, 0, 0, 0), rgb(191 191 191 / 30%))
  color: rgb(36, 36, 36)

.introduction-ul li
  list-style: disc
  margin-left: 2rem
  margin-top: 12px

.container_buttons
  position: absolute
  top: 50%
  left: 50%
  transform: translate(-50%, -50%)

.submit-btn
  border: none
  background-color: rgba(211, 211, 211, 0.863)
  margin: 3%

.nav-progress-item
  width: 100%
  height: 100%
  display: flex
  justify-content: space-between
  align-items: center
  text-decoration: none
  color: rgb(121, 121, 121)
  position: absolute
  right: 0

  label
    margin-left: 30px
    display: inline-block
    font-weight: bold
    cursor: pointer

.progress-icon-container
  padding: 25px
  display: flex
  justify-content: center

  img
    width: 18px

.proposal-wrapper
  display: flex
  height: 100%

.proposal-progress-bar
  width: 300px
  height: 100%
  display: flex
  flex-direction: column
  justify-content: space-between
  align-items: center
  z-index: 50
  border-right: 1px solid rgb(233, 233, 233)

.proposal-content
  flex: 1
  height: 100%
  display: flex
  flex-direction: column
  overflow: auto

.progress-nav
  height: 60%
  width: 100%

  li
    position: relative
    height: 10%
</style>
