/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';
import runWithDelay from '@/helpers/RateLimiter';

const axios = require('axios');

// the amount of milliseconds to wait in between href fetches
const fetchDelay = 50;

function postFile(formData, rootGetters) {
  return axios.post(rootGetters.endpoint('files'), formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
}

export const state = {
  proposals: {
    value: [],
    loading: false,
    error: null,
    request: null,
  },
  proposal: { value: null, loading: false, error: null },
  proposalContacts: { loading: false },
  editing: false,
  proposalServices: { loading: false },
  servicesInPackages: [],
  fileInfo: null,
  proposalOrganisation: { value: null, loading: false, error: null },
};

export const mutations = {
  SET_FILE_INFO(state, info) {
    state.fileInfo = info;
  },
  TOGGLE_PROPOSAL_CONTACTS_LOADING(state) {
    state.proposalContacts.loading = !state.editProposal.proposalContacts.loading;
  },
  ADD_PROPOSAL_CONTACT(state, { contact, role }) {
    state.proposal.value.contacts.push({ ...contact, role, contactId: contact.id });
  },
  DEL_PROPOSAL_CONTACT(state, { contactId, role }) {
    state.proposal.value.contacts = state.proposal.value.contacts.filter(
      (_) => _.id !== contactId || _.role !== role,
    );
  },
  SET_LOADING(state, payload) {
    state[payload.obj].loading = payload.val;
  },
  SET_ERROR(state, payload) {
    state[payload.obj].error = payload.val;
  },
  SET_EDIT(state, val) {
    state.editing = val;
  },
  ADD_PROPOSAL(state, proposal) {
    const index = state.proposals.value.findIndex((_) => _.id === proposal.id);
    if (index === -1) {
      // if proposal does not exist add it
      state.proposals.value.push(proposal);
    } else {
      // else update the proposal
      state.proposals.value[index] = proposal;
    }
  },
  DELETE_PROPOSAL(state, proposal) {
    state.proposals.value = state.proposals.value.filter((prop) => prop.id !== proposal.id);
  },
  UPDATE_PROPOSAL(state, proposal) {
    state.proposals.value.forEach((prop, i) => {
      if (prop.id === proposal.id) {
        state.proposals.value[i] = proposal;
      }
    });
  },
  CLEAR_PROPOSALS(state) {
    state.proposals.value = [];
  },
  ADD_REQUEST_PROPOSAL(state, val) {
    if (state.proposals.request) {
      state.proposals.request.cancel('abort');
    }
    state.proposals.request = val;
  },
  SET_EDIT_PROPOSAL(state, proposal) {
    state.proposal.value = proposal;
    if (!proposal.feedback) {
      state.proposal.value = proposal;
      // set the feedbackfield
      state.proposal.value.feedback = {
        introduction: '',
        description: '',
        contact: '',
        context: '',
        packages: '',
        services: '',
        functional: '',
        supplement: '',
      };
    } else {
      state.proposal.value = proposal;
      // parse the feedback to JSON
      // if proposal.feedback is string parse to json
      if (typeof proposal.feedback === 'string') {
        state.proposal.value.feedback = JSON.parse(proposal.feedback);
      }
    }
  },
  UPDATE_FEEDBACK(state, { what, value }) {
    state.proposal.value.feedback[what] = value;
  },
  CLEAR_SERVICE_IDS(state) {
    state.servicesInPackages = [];
  },
  ADD_SERVICE_ID(state, id) {
    state.servicesInPackages.push(id);
  },
  ADD_PROP_CONTACT(state, val) {
    state.proposal.value.contacts.push(val);
  },
  DEL_PROP_CONTACT_BY_ID(state, id) {
    state.proposal.value.contacts = state.proposal.value.contacts.filter(
      (contact) => contact.contactId !== id,
    );
  },
  ADD_PROP_PACKAGE(state, proposalPackage) {
    state.proposal.value.packages.push({ packageId: proposalPackage.id });
  },
  DEL_PROP_PACKAGE(state, proposalPackage) {
    state.proposal.value.packages = state.proposal.value.packages.filter(
      ({ packageId }) => packageId !== proposalPackage.id,
    );
  },
  ADD_PROP_SERVICE(state, service) {
    state.proposal.value.services.push({
      source: service.selectedSource,
      deliveryMethod: service.selectedDeliveryMethod,
      serviceId: service.id,
    });
  },
  DEL_PROP_SERVICE(state, service) {
    state.proposal.value.services = state.proposal.value.services.filter(
      ({ serviceId }) => serviceId !== service.id,
    );
  },
  UPDATE_PROP_SERVICE(state, service) {
    state.proposal.value.services.forEach((propService, index) => {
      if (propService.serviceId === service.id) {
        state.proposal.value.services[index].source = service.selectedSource;
        state.proposal.value.services[index].deliveryMethod = service.selectedDeliveryMethod;
      }
    });
  },
  /*
  * merges contact details with the contacts in the proposal that have matching IDs
  */
  SET_EDIT_PROPOSAL_CONTACT(state, contact) {
    state.proposal.value.contacts = state.proposal.value.contacts.map(
      ({ contactId, role, ...cnt }) => {
        if (contactId === contact.id) {
          return { ...contact, contactId, role };
        }
        return { ...cnt, contactId, role };
      },
    );
  },
  /*
  * merges service details with the service in the proposal that matches the ID
  */
  SET_EDIT_PROPOSAL_SERVICE(state, service) {
    const index = state.proposal.value.services.findIndex(
      ({ serviceId }) => serviceId === service.id,
    );
    state.proposal.value.services[index] = {
      ...service,
      ...state.proposal.value.services[index],
      deliveryMethods: [state.proposal.value.services[index].deliveryMethod],
    };
  },
  /*
  * sets the organisation to which the proposal belongs
  */
  SET_PROPOSAL_ORGANISATION(state, organisation) {
    state.proposalOrganisation.value = organisation;
  },
};

export const actions = {
  fetch_proposal({ commit, rootGetters, dispatch }, proposalId) {
    if (!state.proposal.loading) {
      commit('SET_LOADING', { obj: 'proposal', val: true });
      axios.get(`${rootGetters.endpoint('proposals')}/${proposalId}`)
        .then(({ data }) => commit('SET_EDIT_PROPOSAL', data, encodedFields.proposal))
        .then(() => dispatch('fetch_proposal_organisation'))
        .catch(console.error)
        .finally(() => commit('SET_LOADING', { obj: 'proposal', val: false }));
    }
  },
  fetch_proposals_pagination({ commit, rootGetters }, paginationParams, filterParams) {
    commit('SET_LOADING', { obj: 'proposals', val: true });
    commit('CLEAR_PROPOSALS');
    const cancelToken = axios.CancelToken;
    const source = cancelToken.source();
    commit('ADD_REQUEST_PROPOSAL', source);
    return axios.get(rootGetters.endpoint('proposals'), {
      params: {
        ...paginationParams,
        ...filterParams,
      },
      cancelToken: source.token,
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.proposalList.forEach(
            (proposal) => commit('ADD_PROPOSAL', decodeObject(proposal, encodedFields.proposal)),
          );
        }
        return data.total;
      })
      .catch((e) => {
        if (e.message !== 'abort') {
          console.error(e);
        }
        return 0;
      })
      .finally(() => commit('SET_LOADING', { obj: 'proposals', val: false }));
  },
  create_proposal({ commit, rootGetters }, proposal) {
    encodeObject(proposal, encodedFields.proposal);
    commit('SET_LOADING', { obj: 'proposal', val: true });
    commit('SET_ERROR', { obj: 'proposal', val: undefined });
    return axios.post(rootGetters.endpoint('proposals'), proposal)
      .then(({ data }) => commit('SET_EDIT_PROPOSAL', decodeObject(data, encodedFields.proposal)))
      .catch((error) => commit('SET_ERROR', { obj: 'proposal', val: error }))
      .finally(() => commit('SET_LOADING', { obj: 'proposal', val: false }));
  },
  update_proposal({ commit }, proposal) {
    const proposalCopy = { ...proposal };
    proposalCopy.feedback = JSON.stringify(proposal.feedback);
    axios.patch(proposalCopy._links.self.href, proposalCopy)
      .catch((error) => {
        commit('SET_ERROR', { obj: 'proposal', val: error });
        console.error(error);
      });
  },
  delete_proposal({ commit }, proposal) {
    axios.delete(proposal._links.self.href)
      .then(() => commit('DELETE_PROPOSAL', proposal))
      .catch(console.error);
  },
  update_service_package_filter({ commit, rootGetters }) {
    commit('CLEAR_SERVICE_IDS');
    state.proposal.value.packages.forEach(({ packageId }) => {
      axios.get(`${rootGetters.endpoint('packages')}/${packageId}`)
        .then(({ data }) => {
          data.services.forEach(({ href }) => {
            const splittedHref = href.split('/');
            commit('ADD_SERVICE_ID', parseInt(splittedHref[splittedHref.length - 1], 10));
          });
        });
    });
  },
  /*
  * Fetch contact details from the backend
  */
  fetch_proposal_contact({ commit }, contact) {
    if (contact.href) {
      axios.get(contact.href)
        .then(({ data }) => commit(
          'SET_EDIT_PROPOSAL_CONTACT',
          decodeObject(data, encodedFields.contact),
        ))
        .catch(console.error);
    }
  },
  /*
  * Fetch contact details from the backend
  */
  fetch_proposal_contacts({ dispatch, commit }, contacts) {
    if (!state.proposalContacts.loading) {
      commit('SET_LOADING', { obj: 'proposalContacts', val: true });
      const ids = new Set();
      runWithDelay(
        async (contact) => {
          if (!ids.has(contact.contactId)) {
            ids.add(contact.contactId);
            dispatch('fetch_proposal_contact', contact);
          }
        },
        contacts,
        fetchDelay,
        () => commit('SET_LOADING', { obj: 'proposalContacts', val: false }),
      );
    }
  },
  /*
  * Fetch service details from the backend
  */
  fetch_proposal_service({ commit }, service) {
    if (service.href) {
      axios.get(service.href)
        .then(({ data }) => commit(
          'SET_EDIT_PROPOSAL_SERVICE',
          decodeObject(data, encodedFields.service),
        ))
        .catch(console.error);
    }
  },
  /*
  * Fetch service details from the backend
  */
  fetch_proposal_services({ dispatch, commit }, services) {
    commit('SET_LOADING', { obj: 'proposalServices', val: true });
    runWithDelay(
      async (service) => dispatch('fetch_proposal_service', service),
      services,
      fetchDelay,
      () => commit('SET_LOADING', { obj: 'proposalServices', val: false }),
    );
  },
  set_file({
    commit,
    rootGetters,
  }, id) {
    return axios.get(`${rootGetters.endpoint('files')}/${id}`)
      .then(({ data }) => {
        commit('SET_FILE_INFO', { fileLocation: data.fileLocation });
      })
      .catch(console.error);
  },
  /*
  * Fetch the organisation to which the proposal belongs
  */
  fetch_proposal_organisation({ commit }) {
    commit('SET_LOADING', { obj: 'proposalOrganisation', val: true });
    axios.get(state.proposal.value._links.organisation.href)
      .then(({ data }) => commit(
        'SET_PROPOSAL_ORGANISATION',
        decodeObject(data, encodedFields.organisation),
      ))
      .catch(console.error)
      .finally(commit('SET_LOADING', { obj: 'proposalOrganisation', val: false }));
  },
  add_architecture_visualization_to_proposal({
    commit,
    rootGetters,
  }, {
    formData,
  }) {
    postFile(formData, rootGetters)
      .then(({ data }) => {
        commit('SET_EDIT_PROPOSAL', { ...state.proposal.value, architectureVisualization: data.id });
      })
      .catch(console.error);
  },
  add_personal_data_authorization_to_proposal({
    commit,
    rootGetters,
  }, {
    formData,
  }) {
    postFile(formData, rootGetters)
      .then(({ data }) => {
        commit('SET_EDIT_PROPOSAL', { ...state.proposal.value, authorizationPersonalData: data.id });
      })
      .catch(console.error);
  },
  add_ssh_to_proposal({
    commit,
    rootGetters,
  }, {
    formData,
  }) {
    postFile(formData, rootGetters)
      .then(({ data }) => {
        commit('SET_EDIT_PROPOSAL', { ...state.proposal.value, sshKey: data.id });
      })
      .catch(console.error);
  },
  add_cooperation_agreement_to_proposal({
    commit,
    rootGetters,
  }, {
    formData,
  }) {
    postFile(formData, rootGetters)
      .then(({ data }) => {
        commit('SET_EDIT_PROPOSAL', { ...state.proposal.value, cooperationAgreement: data.id });
      })
      .catch(console.error);
  },
  add_processing_agreement_to_proposal({
    commit,
    rootGetters,
  }, {
    formData,
  }) {
    postFile(formData, rootGetters)
      .then(({ data }) => {
        commit('SET_EDIT_PROPOSAL', { ...state.proposal.value, processingAgreement: data.id });
      })
      .catch(console.error);
  },
};
