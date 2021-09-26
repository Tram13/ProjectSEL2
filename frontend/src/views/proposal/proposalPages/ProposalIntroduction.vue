<template>
  <ProposalSidebar :activeItem="'inleiding'">
    <div v-if="!proposal.loading" class="content">
      <h2>Inleiding</h2>
      <p>
        Met dit formulier vraag je de aansluiting aan van jouw toepassing op het MAGDA-platform.
      </p>
      <p>
        Vul dit nauwkeurig in, de gevraagde informatie is nodig om de aansluiting te kunnen
        opstarten.
      </p>
      <p>
        Voordat je dit formulier invult is het belangrijk dat je geïnformeerd bent
        over het volledige aansluitingsproces. Dit gebeurt aan de hand van een intakegesprek.
        Heb je nog geen intakegesprek gehad? Mail naar info.magda@kb.vlaanderen.be.
        Informatie over het aansluitingsproces vind je op Aansluiten op MAGDA
      </p>
      <ul class="introduction-ul">
        <li>
          Contactgegevens van personen binnen jouw organisatie.
          (We vragen contactgegevens van personen met een businessprofiel,
          een technisch profiel en van de informatieveiligheidsconsulent/DPO)
        </li>
        <li>
          Bij het bevragen van persoonsgegevens dient u in bezit te zijn van een geldige
          machtiging/beraadslaging/protocol vooraleer te kunnen aansluiten op onze diensten.
          Voor het indienen van deze aanvraag hebt u de machtiging/beraadslaging/protocol
          nodig en de overeenkomstige referentiecode waarmee u toegang krijgt tot bepaalde
          persoonsgegevens; Ben je hiermee niet vertrouwd, kijk dan op 'Juridische toegang
          voor persoonsgegevens'
        </li>
        <li>
          Een lijst van MAGDA-webdiensten die je wilt gebruiken om deze gegevens op te halen;
          Tip: bekijk hiervoor de MAGDA-catalogus en de technische documentatie op de MAGDA
          gebruikersomgeving
        </li>
      </ul>
      <p>
        Is het niet 100% helder, neem dan contact op met onze relatiebeheerder via
        info.magda@kb.vlaanderen.be.
      </p>
      <div class="proposal-navigation-footer" v-if="!proposal.loading">
        <router-link to="/aanvraag" class="nav_item">
          <button class="proposal-navigation-btn">Terug</button>
        </router-link>
        <router-link
          :to="`/aanvraag/${proposal.id}/beschrijving`" class="nav_item">
          <button class="proposal-navigation-btn">Naar Beschrijving</button>
        </router-link>
      </div>

    </div>

  </ProposalSidebar>
</template>

<script lang=ts>
import { defineComponent, computed } from 'vue';
import ProposalSidebar from '@/components/proposal/ProposalSidebar.vue';
import { useStore } from 'vuex';

export default defineComponent({
  components: {
    ProposalSidebar,
  },
  setup() {
    const store = useStore();

    const proposal = computed(() => store.state.proposalstore.proposal.value);

    return {
      proposal,
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
