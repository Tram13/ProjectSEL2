<template>
  <div class="content-container" v-if="user && !organisation.loading && !member.loading">
    <div class='banner banner-large'>
      <div class='banner-content'>
        <BackButton/>
        <Popup size="medium" v-if="!member.value.accepted"
               @closePopup="$router.go(-1)">
          <BackButton/>
          <h1>U bent uitgenodigd voor {{organisation.value.organisationName}}</h1>
          <h3>Wilt u hier op in gaan?</h3>
          <button class="button-primary-full floatRight"
                  v-on:click="acceptInvitation">Accepteer</button>
          <button class="button-primary-transparent floatRight"
                  v-on:click="denyInvitation">Weiger</button>
        </Popup>
        <div class="title-bar" v-if="!organisation.value.approved">
          <h3 :style="{ color: 'red' }">
            Deze organisatie is nog niet aanvaard
          </h3>
          <div class="right" v-if="userRole !== 'customer'">
            <button class="button-ternary-full"
                    v-on:click="approveOrganisation">Aanvaarden</button>
            <button class="button-ternary-full"
                    v-on:click="deleteOrganisation">Afkeuren</button>
          </div>
        </div>
        <div class="title-bar">
          <h1>{{organisation.value.organisationName}}</h1>
          <div class="right">
            <button class="button-secondary-edge" v-if="userRole === 'customer'"
                    v-on:click="showCreateOrganisationPopup = true">Organisatie Aanmaken</button>
            <Popup size="medium" v-if="showCreateOrganisationPopup"
                   @closePopup="showCreateOrganisationPopup = false">
              <h1>Organisatie aanmaken</h1>
              <OrganisationForm
                :organisation="null"
                @organisation_form_close="showCreateOrganisationPopup = false"/>
            </Popup>
            <div class="image-button" v-on:click="toggleEditing">
              <img src='@/assets/pencil.svg' alt="bewerken"/>
            </div>
            <div class="image-button" v-on:click="deleteOrganisation">
              <img src='@/assets/trash.svg' alt="verwijder"/>
            </div>
          </div>
        </div>
        <div class="horizontal banner-info">
          <div class="key-value-grid">
            <span>kbo:</span>
            <span>{{organisation.value.kboNumber}}</span>
            <span>ovo:</span>
            <span>{{organisation.value.ovoCode}}</span>
          </div>
          <div class="key-value-grid">
            <span>nis:</span>
            <span>{{organisation.value.nisNumber}}</span>
            <span>dienstenleverancier:</span>
            <span>{{organisation.value.serviceProvider}}</span>
          </div>
        </div>
        <TabSelector :tabs="organisationTabs" @tabSelected="(name) => activeTab = name"
                       :selected="activeTab"/>
      </div>
    </div>

    <Popup v-if="editing" size="medium"
           @closePopup="toggleEditing">
      <h1>Organisatie aanpassen</h1>
      <OrganisationForm :organisation="organisation.value"
                        @organisation_form_close="toggleEditing"/>
    </Popup>

    <div class="content">
      <div v-if="!organisation.loading">
        <TabPanel :activePage="activeTab">
          <template v-slot:members>
            <div class="title-bar">
              <h4>Organisatieleden</h4>
              <button class="button-ternary-full right"
                      @click="showNewMemberPopup = true"
                      v-if="userRole === 'admin' || member.value.role === 'manager'">
                Lid uitnodigen
              </button>
              <Popup size="medium" v-if="showNewMemberPopup"
                     @closePopup="showNewMemberPopup = false">
                <h1>Lid uitnodigen</h1>
                <MemberForm
                  :user="null"
                  :organisationId="organisationId"
                  @member_form_close="showNewMemberPopup = false"/>
              </Popup>
            </div>

            <data-table
              filterPlaceholder='Zoek lid'
              ref="memberTable"
              generalSearchParameter='generalSearch'
              :entries="members"
              :headers="MemberHeaders"
              :filters="MemberFilters"
              :updateFunction="membersUpdateFunction"
              :loading="loadingMembers">
              <template v-slot:firstName="item">
                {{ item.entry.user.firstName }}
              </template>
              <template v-slot:lastName="item">
                {{ item.entry.user.lastName }}
              </template>
              <template v-slot:email="item">
                {{ item.entry.user.email }}
              </template>
              <template v-slot:links="item">
                <router-link :to="{name:'InspectUser',
                    params: { id: item.entry.user.id }}">
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
                </router-link>
              </template>
            </data-table>
          </template>

          <template v-slot:proposals>
            <div class="title-bar">
              <h4>Aanvragen</h4>
              <router-link class="right" :to="{name: 'CreateProposal' }">
                <button class="button-ternary-full">Nieuwe aanvraag</button>
              </router-link>
            </div>
            <data-table
              filterPlaceholder='Zoek aanvraag'
              generalSearchParameter='generalSearch'
              :entries="proposals"
              :filters="ProposalFilters"
              :headers="ProposalHeaders"
              :updateFunction="proposalUpdateFunction"
              :loading="loadingProposals">
              <template v-slot:links="item">
                <router-link
                  :to="{name:'InspectProposalIntroduction', params: { id: item.entry.id }}">
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
                </router-link>
              </template>
            </data-table>
          </template>

          <template v-slot:contacts>
            <div class="title-bar">
              <h4>Contactpersonen</h4>
              <button class="button-ternary-full"
                      v-on:click="showCreateContactPopup = true">Contact toevoegen</button>
              <Popup size="medium" v-if="showCreateContactPopup"
                     @closePopup="showCreateContactPopup = false">
                <h1>Contact aanmaken</h1>
                <ContactForm
                  :contact="null"
                  :organisationId="organisationId"
                  @contact_form_submit="contactFormClosed"
                />
              </Popup>
            </div>
            <data-table ref="contactDataTable"
                        filterPlaceholder='Zoek contact'
                        generalSearchParameter='generalSearch'
                        :entries="contacts"
                        :headers="ContactHeaders"
                        :filters="ContactFilters"
                        :updateFunction="contactUpdateFunction"
                        :loading="loadingContacts">
              <template v-slot:links="item">
                <a>
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"
                       @click="showInspectContact(item.entry.id)"/>
                </a>
              </template>
            </data-table>
            <ContactInspect v-if="showInspectContactPopup"
                            :organisationId="organisationId"
                            :contactId="contactId"
                            @close_contact_inspect="showInspectContactPopup = false">
            </ContactInspect>
          </template>

          <template v-slot:certificates>
            <div class="title-bar">
              <h4>Certificaten</h4>
                <button @click="showCertPopup = true" class="button-ternary-full">
                  Certificaat Aanmaken</button>
            </div>
            <Popup size="medium" v-if="showCertPopup"
                   @closePopup="showCertPopup = false">
              <p>Om een certificaat aan te maken, kiest u een proposal die goedgekeurd is.
                In het tablad "maak certificaat" kunt u een certificaat aanmaken</p>
            </Popup>

            <data-table
              filterPlaceholder='Zoek certificaat'
              generalSearchParameter='generalSearch'
              :entries="certs"
              :filters="CertFilters"
              :headers="CertHeaders"
              :updateFunction="certUpdateFunction"
              :loading="certLoading">
              <template v-slot:name="item">
                {{ item.entry.proposal.name }}
              </template>
              <template v-slot:file="item">
                <a @click.prevent="downloadCert(item.entry.file.fileLocation,
                  item.entry.proposal.name + item.entry.created)" >
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
                </a>
              </template>
              <template v-slot:created="item">
                {{ item.entry.created }}
              </template>
              <template v-slot:lastUpdated="item">
                {{ item.entry.lastUpdated }}
              </template>
            </data-table>
          </template>

          <template v-slot:permissions>
            <div class="title-bar">
              <h4>Machtigingen</h4>
              <button class="button-ternary-full" @click="showCreatePermissionPopup = true">
                Machtiging toevoegen
              </button>
              <Popup size="medium" v-if="showCreatePermissionPopup"
                     @closePopup="showCreatePermissionPopup = false">
                <h1>Machtiging aanmaken</h1>
                <PermissionForm :permission="null"
                                :organisationId="organisationId"
                                @permission_form_close="showCreatePermissionPopup = false"/>
              </Popup>
            </div>

            <data-table
              filterPlaceholder='Zoek machtiging'
              generalSearchParameter='generalSearch'
              :filters="PermissionFilters"
              :entries="permissions"
              :headers="PermissionHeaders"
              :updateFunction="permUpdateFunction"
              :loading="permLoading">
              <template v-slot:links="item">
                <router-link :to="{name:'InspectPermission', params: { id: item.entry.id }}">
                  <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
                </router-link>
              </template>
            </data-table>
          </template>
        </TabPanel>
      </div>
      <div v-else>
        <pulse-loader color="#FFE615"></pulse-loader>
      </div>
    </div>
    <Footer/>
  </div>
</template>

<script lang=ts>
import {
  ref, watch, defineComponent, computed,
} from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import OrganisationForm from '@/components/organisation/OrganisationForm.vue';
import Footer from '@/components/Footer.vue';
import DataTable from '@/components/elements/DataTable.vue';
import { createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import {
  CertHeaders, CertFilters, ProposalHeaders, ProposalFilters, ContactHeaders, ContactFilters,
  MemberHeaders, MemberFilters, UserFilters, PermissionFilters, PermissionHeaders,
} from '@/helpers/paginationFields';
import { memberRoleTranslator, proposalStatusTranslator } from '@/helpers/fieldTranslators';
import formatDate from '@/helpers/datehelpers';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import axios from 'axios';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import BackButton from '@/components/elements/BackButton.vue';
import Popup from '@/components/elements/popup/popup.vue';
import MemberForm from '@/components/member/MemberForm.vue';
import ContactForm from '@/components/contact/ContactForm.vue';
import ContactInspect from '@/components/contact/ContactInspect.vue';
import PermissionForm from '@/components/permission/PermissionForm.vue';

export default defineComponent({
  components: {
    ContactInspect,
    OrganisationForm,
    Footer,
    DataTable,
    PulseLoader,
    TabPanel,
    TabSelector,
    BackButton,
    Popup,
    MemberForm,
    ContactForm,
    PermissionForm,
  },
  setup() {
    const editing = ref(false);

    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    const organisationId = route ? route.params.id : undefined;
    const userRole = computed(() => store.getters.role);
    const user = computed(() => store.state.authenticationstore.user);
    const downloadCert = (url, label) => {
      if (url) {
        axios.get(url, { responseType: 'blob' }).then((response) => {
          const blob = new Blob([response.data], { type: 'application/x-x509-user-cert' });
          const link = document.createElement('a');
          link.href = URL.createObjectURL(blob);
          link.download = `${label}.crt`;
          link.click();
          URL.revokeObjectURL(link.href);
        }).catch(console.error);
      }
    };

    // watcher for route parameterchanges when changing organisation
    watch(() => (route ? route.params.id : undefined), (newId) => {
      // only if the page is InspectOrganisation
      if (route.name === 'InspectOrganisation') {
        store.dispatch('fetch_organisation', newId)
          .then((org) => store.dispatch('fetch_organisations_members_pagination', { organisation: org }));
      }
    });

    store.dispatch('fetch_organisation', route ? route.params.id : undefined);

    const organisation = computed(() => store.state.organisationstore.organisation);

    if (user.value) {
      if (user.value.role !== 'admin') {
        store.dispatch('fetch_organisation_member', { orgId: organisationId, userId: user.value.id });
      } else {
        store.dispatch('fetch_organisation_member_as_admin', user.value);
      }
    }

    const member = computed(() => store.state.organisationstore.member);

    function approveOrganisation() {
      organisation.value.value.approved = true;
      store.dispatch('update_organisation', organisation.value.value);
    }

    function deleteOrganisation() {
      // value.value: first value is for vue wrapper, snd is for the store
      // eslint-disable-next-line
      if (confirm('Bent u zeker?')) {
        store.dispatch('delete_organisation', organisation.value.value);
        if (store.state.authenticationstore.user.role === 'customer') {
          router.push({ name: 'Home' });
        } else {
          router.push({ name: 'Organisation' });
        }
      }
    }

    function toggleEditing() {
      editing.value = !editing.value;
    }

    // ==================== INVITATIONS ===============================

    const memberTable = ref(null);

    function acceptInvitation() {
      const copy = member.value.value;
      copy.accepted = true;
      store.dispatch('update_organisation_member', copy)
        .then(() => memberTable.value.refreshPage());
    }

    function denyInvitation() {
      store.dispatch('delete_organisation_member', member.value.value);
      router.go(-1);
    }

    // ==================== MEMBERS ===============================

    const members = computed(
      () => store.state.organisationstore.organisation.members.value
        .map((data) => {
          const res = { ...data };
          res.role = memberRoleTranslator[data.role];
          return res;
        }),
    );
    const loadingMembers = computed(
      () => store.state.organisationstore.organisation.members.loading,
    );

    const membersUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_MEMBERS',
      'fetch_organisations_members_pagination',
      {
        organisationId,
        accepted: true,
      },
    );

    // ==================== PROPOSALS ===============================

    const proposalUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_PROPOSALS',
      'fetch_proposals_pagination',
      { organisationId },
    );
    const loadingProposals = computed(() => store.state.proposalstore.proposals.loading);
    const proposals = computed(() => store.state.proposalstore.proposals.value
      .map((data) => {
        const res = { ...data };
        res.status = proposalStatusTranslator[data.status];
        try {
          res.deadline = formatDate(res.deadline);
        } catch (err) {
          res.deadline = null;
        }
        return res;
      }));

    // ==================== CONTACTS ================================

    const contacts = computed(() => store.state.contactstore.contacts.value);
    const loadingContacts = computed(() => store.state.contactstore.contacts.loading);

    const contactUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_CONTACTS',
      'fetch_contacts_pagination',
      { organisationId },
    );

    // ==================== CERTS ================================

    const certs = computed(() => store.state.certstore.certs.value
      .map((data) => {
        const res = { ...data };
        res.created = formatDate(res.created);
        res.lastUpdated = formatDate(res.lastUpdated);
        return res;
      }));
    const certLoading = computed(() => store.state.certstore.certs.loading);
    const certUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_CERTS',
      'get_certs_pagination',
      { organisationId },
    );

    // ==================== PERMISSIONS ================================

    const permissions = computed(() => store.state.permissionstore.permissions.value);
    const permLoading = computed(() => store.state.permissionstore.permissions.loading);
    const permUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_PERMISSIONS',
      'fetch_permissions_pagination',
      { organisationId },
    );

    const activeTab = ref('members');
    const organisationTabs = [
      {
        name: 'members',
        text: 'Leden',
      },
      {
        name: 'proposals',
        text: 'Aanvragen',
      },
      {
        name: 'contacts',
        text: 'Contacten',
      },
      {
        name: 'certificates',
        text: 'Certificaten',
      },
      {
        name: 'permissions',
        text: 'Machtigingen',
      },
    ];

    const showNewMemberPopup = ref(false);
    const showCreateOrganisationPopup = ref(false);
    const showCreateContactPopup = ref(false);
    const showInspectContactPopup = ref(false);
    const showCertPopup = ref(false);
    const contactDataTable = ref(null);

    const contactFormClosed = (contact) => {
      store.dispatch(
        'create_contact',
        {
          contact,
          organisationId,
        },
      ).then(() => { contactDataTable.value.refreshPage(); });
      showCreateContactPopup.value = false;
    };

    const contactId = ref(null);

    const showInspectContact = (id) => {
      contactId.value = id;
      showInspectContactPopup.value = true;
    };

    const showCreatePermissionPopup = ref(false);

    return {
      certs,
      certLoading,
      certUpdateFunction,
      CertHeaders,
      organisationId,
      editing,
      organisation,
      approveOrganisation,
      deleteOrganisation,
      toggleEditing,
      members,
      loadingMembers,
      membersUpdateFunction,
      MemberHeaders,
      proposals,
      proposalUpdateFunction,
      loadingProposals,
      ProposalHeaders,
      contacts,
      ContactHeaders,
      loadingContacts,
      contactUpdateFunction,
      userRole,
      downloadCert,
      UserFilters,
      activeTab,
      organisationTabs,
      permissions,
      permLoading,
      permUpdateFunction,
      PermissionHeaders,
      PermissionFilters,
      showNewMemberPopup,
      member,
      acceptInvitation,
      denyInvitation,
      memberTable,
      showCreateOrganisationPopup,
      showCertPopup,
      CertFilters,
      ProposalFilters,
      ContactFilters,
      MemberFilters,
      showCreateContactPopup,
      contactFormClosed,
      contactDataTable,
      user,
      showInspectContactPopup,
      contactId,
      showInspectContact,
      showCreatePermissionPopup,
    };
  },
});
</script>
