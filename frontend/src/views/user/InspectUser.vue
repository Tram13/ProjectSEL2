<template>
  <div class="content-container" v-if="user && !loading">
    <div class='banner banner-large'>
      <div class='banner-content'>
        <BackButton/>
        <div class="title-bar">
          <h1>{{`${user.lastName} ${user.firstName}`}}</h1>
          <div class="right">
            <div class="image-button" v-on:click="showEditUserPopup = true">
              <img src='@/assets/pencil.svg' alt="bewerk"/>
            </div>
            <Popup size="medium" v-if="showEditUserPopup" @closePopup="showEditUserPopup = false">
              <h1>Gebruiker aanpassen</h1>
              <UserForm :user="user" @user_form_close="showEditUserPopup = false"/>
            </Popup>
            <div class="image-button" v-on:click="showPasswordVerifier = true">
              <img src='@/assets/trash.svg' alt="verwijder"/>
            </div>
          </div>
        </div>
        <div class="key-value-grid banner-info">
          <span>email:</span>
          <span>{{ user.email }}</span>
          <span>rol:</span>
          <span>{{ userRoleTranslator[user.role] }}</span>
        </div>
        <TabSelector :tabs="userTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>

    <div class="content">

      <TabPanel :activePage="activeTab">
        <template v-slot:organisations>
          <div>
            <div class="title-bar">
              <h4>Lid van:</h4>
              <button v-if="authUser && authUser.role !== 'customer'"
                v-on:click="showOrganisationPopup = true"
                class="button-ternary-full right">
                Aansluiten bij een organisatie
              </button>
            </div>
            <Popup size="large" class="organisation-popup" v-if="showOrganisationPopup"
                    @closePopup="showOrganisationPopup = false">
              <h2>Aansluiten bij een organisatie</h2>
              <data-table
                generalSearchParameter='generalSearch'
                height='400px'
                filterPlaceholder="Zoek organisatie"
                :entries="organisation"
                :headers="OrganisationHeaders"
                :updateFunction="organisationUpdate(authUser.id)"
                :loading="loadingOrganisations"
                :filters="OrganisationFilters">
                <template v-slot:links="item">
                  <button v-on:click="addUserToOrganisation(item.entry)"
                          class="button-ternary-full">
                    Aansluiten
                  </button>
                </template>
              </data-table>
            </Popup>
            <data-table
              ref="organisationTable"
              filterPlaceholder='Zoek organisatie'
              :entries="userOrganisations"
              :headers="UserOrganisationHeaders"
              :updateFunction="userAcceptedOrganisationUpdate"
              :loading="organisationLoading">
              <template v-slot:organisationName="item">
                  {{ item.entry.organisation.organisationName }}
              </template>
              <template v-slot:status="item">
                <img v-if="item.entry.organisation.approved" src="@/assets/check.svg"
                  width="20" height="20" alt="goedgekeurd" />
                <img v-else src="@/assets/cross.svg"
                  width="20" height="20" alt="niet goedgekeurd" />
                {{ item.entry.organisation.approved ? "goedgekeurd" : "niet goedgekeurd" }}
              </template>
              <template v-slot:role="item">
                {{ item.entry.role }}
              </template>
              <template v-slot:links="item" >
                <div class="leave-org-image">
                  <div v-on:click="removeUserFromOrganisation(item.entry)"
                      class="tooltip leave-org-image">
                    <img src="@/assets/check-out.svg" alt="verlaat organisatie"/>
                    <span class="tooltiptext">organisatie verlaten</span>
                  </div>
                <router-link
                  :to="{
                    name:'InspectOrganisation',
                    params: { id: item.entry.organisation.id }
                  }">
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
                </router-link>
                </div>
              </template>
            </data-table>
          </div>
          <Popup v-if="showPasswordVerifier" @closePopup="showPasswordVerifier = false">
            <PasswordVerifier @correct-password="deleteUser"/>
          </Popup>
        </template>
        <template v-slot:invites>
          <div>
            <div class="title-bar">
              <h4>Uitgenodigd voor:</h4>
            </div>
            <data-table
              filterPlaceholder='Zoek uitnodiging'
              :entries="userOrganisations"
              :headers="UserOrganisationHeaders"
              :updateFunction="userInvitedOrganisationUpdate"
              :loading="organisationLoading">
              <template v-slot:organisationName="item">
                {{ item.entry.organisation.organisationName }}
              </template>
              <template v-slot:status="item">
                <img v-if="item.entry.organisation.approved" src="@/assets/check.svg"
                     width="20" height="20" alt="goedgekeurd"/>
                <img v-else src="@/assets/cross.svg"
                     width="20" height="20" alt="niet goedgekeurd"/>
                {{ item.entry.organisation.approved ? "goedgekeurd" : "niet goedgekeurd" }}
              </template>
              <template v-slot:role="item">
                {{ item.entry.role }}
              </template>
              <template v-slot:links="item">
                <router-link
                  :to="{
                    name:'InspectOrganisation',
                    params: { id: item.entry.organisation.id }
                  }">
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
                </router-link>
              </template>
            </data-table>
          </div>
          <Popup v-if="showPasswordVerifier" @closePopup="showPasswordVerifier = false">
            <PasswordVerifier @correct-password="deleteUser"/>
          </Popup>
        </template>
      </TabPanel>
    </div>

    <Footer/>
  </div>
  <div v-else>
    <pulse-loader color="#FFE615"></pulse-loader>
  </div>
</template>

<script lang=ts>
import {
  ref,
  defineComponent,
  computed,
} from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import DataTable from '@/components/elements/DataTable.vue';
import UserForm from '@/components/user/UserForm.vue';
import Footer from '@/components/Footer.vue';
import PasswordVerifier from '@/components/account/PasswordVerifier.vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import { UserOrganisationHeaders, OrganisationHeaders, OrganisationFilters } from '@/helpers/paginationFields';
import Popup from '@/components/elements/popup/popup.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import BackButton from '@/components/elements/BackButton.vue';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import { createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import { memberRoleTranslator, userRoleTranslator } from '@/helpers/fieldTranslators';

export default defineComponent({
  components: {
    UserForm,
    Footer,
    PasswordVerifier,
    PulseLoader,
    TabPanel,
    TabSelector,
    DataTable,
    Popup,
    BackButton,
  },
  setup() {
    const showEditUserPopup = ref(false);
    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    const showPasswordVerifier = ref(false);
    const showOrganisationPopup = ref(false);
    // the user that will be displayed
    const user = computed(() => store.state.userstore.user.value);
    const loading = computed(() => store.state.userstore.user.loading);
    const authUser = computed(() => store.state.authenticationstore.user);
    // DATA FOR TABLE WHERE USER HAS ACCEPTED THE INVITATION OF THE ORG
    const organisationLoading = computed(() => store.state.userstore.user.organisations.loading);
    const userOrganisations = computed(() => store.state.userstore.user.organisations.value
      .map((data) => {
        const res = { ...data };
        res.role = memberRoleTranslator[data.role];
        return res;
      }));
    const userAcceptedOrganisationUpdate = createUpdateFunctionExtraParams(
      'CLEAR_USER_ORGANISATIONS',
      'get_user_organisations_pagination',
      {
        user,
        accepted: true,
      },
    );
    const userInvitedOrganisationUpdate = createUpdateFunctionExtraParams(
      'CLEAR_USER_ORGANISATIONS',
      'get_user_organisations_pagination',
      {
        user,
        accepted: false,
      },
    );

    const loadUser = (userId) => {
      store.dispatch('get_user', userId);
    };
    loadUser(route ? route.params.id : undefined);

    if (authUser.value && authUser.value.role !== 'customer') {
      store.dispatch('fetch_organisations');
    }

    // variable for selectionlist
    const organisation = computed(() => store.state.organisationstore.organisations.value);
    // organisations the user can be added to => all organisations - userorganisations

    const loadingOrganisations = computed(
      () => store.state.organisationstore.organisations.loading,
    );

    const organisationUpdate = (id) => createUpdateFunctionExtraParams(
      'CLEAR_ORGANISATIONS',
      'fetch_organisations_pagination',
      { notMember: id },
    );

    function deleteUser() {
      // eslint-disable-next-line
      if (confirm('Bent u zeker?')) {
        store.dispatch('delete_user', user.value);
        router.push({ name: user.value.id === authUser.value.id ? 'Login' : 'User' });
      }
    }

    function addUserToOrganisation(org) {
      // add user to organisations
      store.dispatch('add_user_to_organisation', {
        user: user.value,
        organisation: org,
        role: 'member',
      }).then(() => {
        showOrganisationPopup.value = false;
      });
    }

    const organisationTable = ref(null);

    // _ for eslint (declared in upperscope)
    function removeUserFromOrganisation(_organisation) {
      // remove user from organisation
      // eslint-disable-next-line
      if (confirm('Bent u zeker dat u de organisatie wil verlaten?')) {
        store.dispatch('remove_member_from_organisation', {
          id: user.value.id,
          organisation: _organisation,
        }).then(() => organisationTable.value.refreshPage());
      }
    }

    const userTabs = [
      {
        name: 'organisations',
        text: 'Alle Organisaties',
      },
      {
        name: 'invites',
        text: 'Uitnodigingen',
      },
    ];
    const activeTab = ref('organisations');

    return {
      showPasswordVerifier,
      showEditUserPopup,
      user,
      authUser,
      loading,
      userOrganisations,
      organisation,
      loadingOrganisations,
      organisationUpdate,
      deleteUser,
      addUserToOrganisation,
      removeUserFromOrganisation,
      userTabs,
      activeTab,
      UserOrganisationHeaders,
      OrganisationHeaders,
      OrganisationFilters,
      showOrganisationPopup,
      userRoleTranslator,
      userAcceptedOrganisationUpdate,
      userInvitedOrganisationUpdate,
      organisationLoading,
      organisationTable,
      loadUser,
    };
  },
  beforeRouteUpdate(to, _, next) {
    this.loadUser(to ? to.params.id : undefined);
    next();
  },
});
</script>

<style scoped lang="sass">
.leave-org-image
  display: flex
  justify-content: center
  align-items: center
  cursor: pointer

  > img
    margin: 0 15px 5px
    width: 20px

.tooltip
  position: relative
  display: inline-block

  &:hover .tooltiptext
    visibility: visible

  .tooltiptext
    visibility: hidden
    width: 120px
    background-color: black
    color: #fff
    text-align: center
    border-radius: 6px
    padding: 5px 0
    /* Position the tooltip*/
    position: absolute
    z-index: 1
    font-size: 10px
    bottom: 100%
    left: 50%
    margin-left: -60px

/* Use half of the width (120/2 = 60), to center the tooltip
</style>
