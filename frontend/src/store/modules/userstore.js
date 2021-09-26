/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

export const state = {
  // list of users
  users: {
    value: [],
    loading: false,
    error: undefined,
    request: undefined,
  },
  // single user if for inspect pages etc
  user: {
    value: null,
    organisations: {
      value: [],
      loading: false,
    },
    loading: false,
  },
};

export const mutations = {
  SET_USER(state, user) {
    // set user
    state.user.value = user;
    // clear organisations (can still be from another user)
    state.user.organisations.value = [];
  },
  ADD_USER_ORGANISATION(state, { organisation }) {
    const index = state.user.organisations.value.findIndex(
      (_) => _.organisation.id === organisation.organisation.id,
    );
    if (index === -1) {
      // if organisation does not exist add it
      state.user.organisations.value.push(organisation);
    } else {
      // else update the organisation
      state.user.organisations.value[index] = organisation;
    }
  },
  DELETE_USER_ORGANISATION(state, { organisation }) {
    state.user.organisations.value = state.user.organisations.value.filter(
      (_) => _.organisation.id !== organisation.id,
    );
  },
  ADD_USER(state, user) {
    const index = state.users.value.findIndex((_) => _.id === user.id);
    if (index === -1) {
      // if user does not exist add it
      state.users.value.push(user);
    } else {
      // else update the user
      state.users.value[index] = user;
    }
  },
  DELETE_USER(state, user) {
    state.users.value = state.users.value.filter((usr) => usr.id !== user.id);
  },
  UPDATE_USER(state, user) {
    state.users.value.forEach((usr, i) => {
      if (usr.id === user.id) state.users.value[i] = user;
    });
    if (state.user.value.id === user.id) state.user.value = user;
  },
  CLEAR_USERS(state) {
    state.users.value = [];
    state.users.error = undefined;
  },
  CLEAR_USER_ORGANISATIONS(state) {
    state.user.organisations.value = [];
  },
  TOGGLE_LOADING_USER(state) {
    state.user.loading = !state.user.loading;
  },
  SET_LOADING_USERS(state, val) {
    state.users.loading = val;
  },
  TOGGLE_LOADING_USER_ORGANISATIONS(state) {
    state.user.organisations.loading = !state.user.organisations.loading;
  },
  ADD_REQUEST_USER(state, val) {
    if (state.users.request) {
      state.users.request.cancel('abort');
    }
    state.users.request = val;
  },
};

export const actions = {
  get_user({
    commit,
    rootGetters,
  }, id) {
    commit('TOGGLE_LOADING_USER');
    return axios.get(`${rootGetters.endpoint('users')}/${id}`)
      .then(({ data }) => commit('SET_USER', decodeObject(data, encodedFields.user)))
      .catch(console.error)
      .finally(() => commit('TOGGLE_LOADING_USER'));
  },
  get_users_pagination({
    commit,
    rootGetters,
  }, paginationParams, filterParams) {
    commit('SET_LOADING_USERS', true);
    commit('CLEAR_USERS');
    const cancelToken = axios.CancelToken;
    const source = cancelToken.source();
    commit('ADD_REQUEST_USER', source);
    return axios.get(rootGetters.endpoint('users'), {
      params: {
        ...paginationParams,
        ...filterParams,
      },
      cancelToken: source.token,
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.userList.forEach((user) => commit('ADD_USER', decodeObject(user, encodedFields.user)));
        }

        return data.total;
      })
      .catch((e) => {
        if (e.message !== 'abort') {
          console.error(e);
        }
        return 0;
      })
      .finally(() => commit('SET_LOADING_USERS', false));
  },
  create_user({
    commit,
    rootGetters,
  }, user) {
    encodeObject(user, encodedFields.user);
    return axios.post(rootGetters.endpoint('users'), user)
      .then(({ data }) => commit('ADD_USER', decodeObject(data, encodedFields.user)));
  },
  update_user({ commit }, user) {
    encodeObject(user, encodedFields.user);
    return axios.patch(user._links.self.href, user)
      .then(({ data }) => commit('UPDATE_USER', decodeObject(data, encodedFields.user)));
  },
  delete_user({ commit }, user) {
    axios.delete(user._links.self.href)
      .then(() => commit('DELETE_USER', user))
      .catch(console.error);
  },
  get_user_organisations({ commit }, user) {
    commit('TOGGLE_LOADING_USER_ORGANISATIONS');
    return axios.get(user._links.organisations.href)
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.organisationList.forEach((organisation) => commit('ADD_USER_ORGANISATION',
            { user, organisation }));
        }
      })
      .finally(() => commit('TOGGLE_LOADING_USER_ORGANISATIONS'));
  },
  get_user_organisations_pagination({ commit }, params) {
    const { user } = params;
    delete params.user;
    commit('CLEAR_USER_ORGANISATIONS');
    commit('TOGGLE_LOADING_USER_ORGANISATIONS');
    return axios.get(user.value._links.organisations.href, {
      params: {
        ...params,
      },
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.organisationList.forEach((organisation) => commit('ADD_USER_ORGANISATION',
            { user: user.value, organisation }));
        }
        return data.total;
      })
      .finally(() => commit('TOGGLE_LOADING_USER_ORGANISATIONS'));
  },
  add_user_to_organisation({
    dispatch,
    rootGetters,
  }, {
    user,
    organisation,
    role,
  }) {
    axios.post(`${rootGetters.endpoint('organisations')}/${organisation.id}/members`, {
      userId: user.id,
      role,
    })
      .then(() => dispatch('get_user_organisations', user))
      .catch(console.error);
  },
  add_user_with_email_to_organisation({
    rootGetters,
  }, {
    email,
    organisationId,
    role,
  }) {
    return axios.post(`${rootGetters.endpoint('organisations')}/${organisationId}/members`, {
      email,
      role,
    });
  },
  remove_member_from_organisation({
    commit,
    rootGetters,
  }, {
    id,
    organisation,
  }) {
    return axios.delete(`${rootGetters.endpoint('organisations')}/${organisation.organisation.id}/members/${id}`)
      .then(() => commit('DELETE_USER_ORGANISATION', { user: state.user.value, organisation: organisation.organisation }))
      .catch(console.error);
  },
};
