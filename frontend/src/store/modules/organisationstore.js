/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

export const state = {
  organisations: {
    value: [],
    loading: false,
    error: null,
    request: null,
  },
  organisation: {
    value: null,
    members: {
      value: [],
      loading: false,
    },
    loading: false,
    error: null,
  },
  member: {
    value: null,
    loading: false,
  },
};

export const mutations = {
  SET_ORGANISATION_LOADING(state, val) {
    state.organisation.loading = val;
  },
  SET_ORGANISATION_ERROR(state, val) {
    state.organisation.error = val;
  },
  SET_ORGANISATION_VALUE(state, organisation) {
    state.organisation.value = organisation;
  },
  ADD_ORGANISATION(state, organisation) {
    const index = state.organisations.value.findIndex((_) => _.id === organisation.id);
    if (index === -1) {
      // if organisation does not exist add it
      state.organisations.value.push(organisation);
    } else {
      // else update the organisation
      state.organisations.value[index] = organisation;
    }
  },
  ADD_ORGANISATION_MEMBER(state, member) {
    const index = state.organisation.members.value.findIndex((_) => _.id === member.id);
    if (index === -1) {
      // if organisation does not exist add it
      state.organisation.members.value.push(member);
    } else {
      // else update the organisation
      state.organisation.members.value[index] = member;
    }
  },
  DELETE_ORGANISATION(state, organisation) {
    state.organisations.value = state.organisations.value.filter(
      (org) => org.id !== organisation.id,
    );
  },
  UPDATE_ORGANISATION(state, organisation) {
    state.organisations.value.forEach((org, i) => {
      if (org.id === organisation.id) state.organisations.value[i] = organisation;
    });
    // also check if current organisation needs to be updated
    if (state.organisation.value) {
      if (state.organisation.value.id === organisation.id) state.organisation.value = organisation;
    }
  },
  SET_ORGANISATIONS_LOADING(state, val) {
    state.organisations.loading = val;
  },
  SET_MEMBER_LOADING(state, val) {
    state.organisation.members.loading = val;
  },
  CLEAR_ORGANISATIONS(state) {
    state.organisations.value = [];
    state.organisations.error = undefined;
  },
  INCREMENT_ORGANISATIONS_REQ_ID(state) {
    state.organisations.requestId += 1;
  },
  ADD_REQUEST_ORGANISATION(state, val) {
    if (state.organisations.request) {
      state.organisations.request.cancel('abort');
    }
    state.organisations.request = val;
  },
  CLEAR_MEMBERS(state) {
    state.organisation.members.value = [];
  },
  SET_ORGANISATION_MEMBER_LOADING(state, val) {
    state.member.loading = val;
  },
  SET_ORGANISATION_MEMBER_VALUE(state, member) {
    state.member.value = member;
  },
  UPDATE_ORGANISATION_MEMBER(state, member) {
    state.member.value = member;
  },
  DELETE_ORGANISATION_MEMBER(state) {
    state.member.value = null;
  },
};

export const actions = {
  fetch_organisation({ commit, rootGetters }, id) {
    commit('SET_ORGANISATION_LOADING', true);
    return axios.get(`${rootGetters.endpoint('organisations')}/${id}`)
      .then(({ data }) => {
        commit('SET_ORGANISATION_VALUE', decodeObject(data, encodedFields.organisation));
        return data;
      })
      .catch(console.error)
      .finally(() => commit('SET_ORGANISATION_LOADING', false));
  },
  fetch_organisations({ commit, rootGetters }) {
    axios.get(rootGetters.endpoint('organisations'))
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.organisationList.forEach(
            (organisation) => commit('ADD_ORGANISATION', decodeObject(organisation, encodedFields.organisation)),
          );
        }
      })
      .catch(console.error);
  },
  fetch_organisations_pagination({ commit, rootGetters }, paginationParams, filterParams) {
    commit('SET_ORGANISATIONS_LOADING', true);
    commit('CLEAR_ORGANISATIONS');
    commit('INCREMENT_ORGANISATIONS_REQ_ID');
    const cancelToken = axios.CancelToken;
    const source = cancelToken.source();
    commit('ADD_REQUEST_ORGANISATION', source);
    return axios.get(rootGetters.endpoint('organisations'), {
      params: {
        ...paginationParams,
        ...filterParams,
      },
      cancelToken: source.token,
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.organisationList.forEach(
            (organisation) => commit('ADD_ORGANISATION', decodeObject(organisation, encodedFields.organisation)),
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
      .finally(() => commit('SET_ORGANISATIONS_LOADING', false));
  },
  create_organisation({ commit, rootGetters }, organisation) {
    encodeObject(organisation, encodedFields.organisation);
    return axios.post(rootGetters.endpoint('organisations'), organisation)
      .then(({ data }) => {
        commit('SET_ORGANISATION_VALUE', decodeObject(data, encodedFields.organisation));
        return data.id;
      })
      .catch(console.error);
  },
  async update_organisation({ commit }, organisation) {
    encodeObject(organisation, encodedFields.organisation);
    return axios.patch(organisation._links.self.href, organisation)
      .then(() => commit('UPDATE_ORGANISATION', decodeObject(organisation, encodedFields.organisation)))
      .catch(console.error);
  },
  delete_organisation({ commit }, organisation) {
    axios.delete(organisation._links.self.href, organisation)
      .then(() => commit('DELETE_ORGANISATION', organisation))
      .catch(console.error);
  },
  fetch_organisations_members_pagination({ commit, rootGetters }, paginationParams) {
    commit('SET_MEMBER_LOADING', true);
    commit('CLEAR_MEMBERS');

    return axios.get(
      `${rootGetters.endpoint('organisations')}/${paginationParams.organisationId}/members`,
      {
        params: {
          ...paginationParams,
        },
      },
    )
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.memberList.forEach(
            (member) => {
              decodeObject(member.user, encodedFields.user);
              commit('ADD_ORGANISATION_MEMBER', member);
            },
          );
        }
        return data.total;
      })
      .catch(console.error)
      .finally(() => commit('SET_MEMBER_LOADING', false));
  },
  fetch_organisation_member({ commit, rootGetters }, { orgId, userId }) {
    commit('SET_ORGANISATION_MEMBER_LOADING', true);
    return axios.get(`${rootGetters.endpoint('organisations')}/${orgId}/members/${userId}`)
      .then(({ data }) => {
        commit('SET_ORGANISATION_MEMBER_VALUE', data);
        return data;
      })
      .catch(console.error)
      .finally(() => commit('SET_ORGANISATION_MEMBER_LOADING', false));
  },
  fetch_organisation_member_as_admin({ commit }, user) {
    commit('SET_ORGANISATION_MEMBER_LOADING', true);
    commit('SET_ORGANISATION_MEMBER_VALUE', {
      role: 'manager',
      accepted: true,
      user,
    });
    commit('SET_ORGANISATION_MEMBER_LOADING', false);
  },
  update_organisation_member({ commit }, member) {
    console.log(member);
    return axios.patch(member._links.self.href, member)
      .then(({ data }) => commit('UPDATE_ORGANISATION_MEMBER', data))
      .catch(console.error);
  },
  delete_organisation_member({ commit }, member) {
    axios.delete(member._links.self.href)
      .then(() => commit('DELETE_ORGANISATION_MEMBER'))
      .catch(console.error);
  },
};
